/*
 * Copyright 2008 Jeremias Maerki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id: USPSIntelligentMailLogicImpl.java,v 1.3 2010-10-12 08:20:04 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.fourstate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.MissingResourceException;
import java.util.StringTokenizer;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.tools.IOUtil;

/**
 * This class is a logic implementation for the USPS Intelligent Mail Barcode (aka 4-State Customer
 * Barcode, USPS-B-3200 revision E, 2007-10-30).
 */
public class USPSIntelligentMailLogicImpl extends AbstractFourStateLogicImpl {

    static final char[] TABLE5OF13 = new char[1287];
    static final char[] TABLE2OF13 = new char[78];
    static final BarToCharacterMapping[] TABLE_BAR_TO_CHARACTER = new BarToCharacterMapping[65];

    static {
        initializeNof13Table(TABLE5OF13, 5, TABLE5OF13.length);
        initializeNof13Table(TABLE2OF13, 2, TABLE2OF13.length);
        initializeBarToCharacterTable();
    }

    /**
     * Main constructor.
     */
    public USPSIntelligentMailLogicImpl() {
        super(ChecksumMode.CP_AUTO); //Special check mechanism!
    }

    private static int reverseUnsignedShort(int input) {
        int reverse = 0;
        for (int i = 0; i < 16; i++) {
            reverse <<= 1;
            reverse |= input & 1;
            input >>= 1;
        }
        return reverse;
    }

    private static void initializeNof13Table(char[] tableNof13, int n, int tableLength) {
        //Count up to 2^13 - 1 and find all those value that have N bits on
        int lutLowerIndex = 0;
        int lutUpperIndex = tableLength - 1;

        for (int count = 0; count < 8192; count++) {
            int bitCount = 0;
            for (int bitIndex = 0; bitIndex < 13; bitIndex++) {
                if ((count & (1 << bitIndex)) != 0) {
                    bitCount++;
                }
            }

            //if we don't have the right number of bits on, go on to the next value
            if (bitCount != n) {
                continue;
            }

            //if the reverse is less than count, we have already visited this pair before
            int reverse = reverseUnsignedShort(count) >> 3;
            if (reverse < count) {
                continue;
            }

            //if count is symmetric, place it at the first free slot from the end of the
            //list. Otherwise, place it at the first free slot from the beginning of the
            //list AND place reverse at the next free slot from the beginning of the list.

            if (count == reverse) {
                tableNof13[lutUpperIndex] = (char)count;
                lutUpperIndex--;
            } else {
                tableNof13[lutLowerIndex] = (char)count;
                lutLowerIndex++;
                tableNof13[lutLowerIndex] = (char)reverse;
                lutLowerIndex++;
            }
        }

        //make sure the lower and upper parts of the table meet properly
        if (lutLowerIndex != lutUpperIndex + 1) {
            throw new IllegalStateException("lookup table indices didn't meet properly!");
        }
    }

    private static final String BAR_TO_CHARACTER_TABLE_FILENAME
                = "usps-4bc-bar-to-character-table.csv";

    private static void initializeBarToCharacterTable() {
        InputStream in = USPSIntelligentMailLogicImpl.class.getResourceAsStream(
                BAR_TO_CHARACTER_TABLE_FILENAME);
        if (in == null) {
            throw new MissingResourceException(
                    "Resource " + BAR_TO_CHARACTER_TABLE_FILENAME + " not found!", null, null);
        }
        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(in));
        try {
            int idx = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                TABLE_BAR_TO_CHARACTER[idx] = new BarToCharacterMapping(idx, line);
                idx++;
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Could not initialize constant due to I/O error: "
                    + ioe.getMessage());
        } finally {
            IOUtil.closeQuietly(reader);
            IOUtil.closeQuietly(in);
        }

    }

    private static final class BarToCharacterMapping {

        private int descChar;
        private int descBitMap;
        private int ascChar;
        private int ascBitMap;

        private BarToCharacterMapping(int index, String line) {
            StringTokenizer tokenizer = new StringTokenizer(line, ";");
            if (Integer.parseInt(tokenizer.nextToken()) != (index + 1)) {
                throw new IllegalStateException("Encountered the wrong line number");
            }
            this.descChar = tokenizer.nextToken().charAt(0) - 'A';
            this.descBitMap = 1 << Integer.parseInt(tokenizer.nextToken());
            this.ascChar = tokenizer.nextToken().charAt(0) - 'A';
            this.ascBitMap = 1 << Integer.parseInt(tokenizer.nextToken());
        }
    }

    /** {@inheritDoc} */
    public char calcChecksum(String msg) {
        return 0; //Not used
    }

    static BigInteger convertToBinary(String msg) {
        if (msg.length() < 20) {
            throw new IllegalArgumentException(
                    "Message is too short. It must have at least 20 digits");
        }
        if (msg.length() > 31) {
            throw new IllegalArgumentException(
                    "Message must not be longer than 31 digits");
        }
        String routingCode = msg.substring(20);
        int routingLength = routingCode.length();
        BigInteger routingBig;
        switch (routingLength) {
        case 0:
            routingBig = BigInteger.ZERO;
            break;
        case 5:
            routingBig = new BigInteger(routingCode);
            routingBig = routingBig.add(BigInteger.ONE);
            break;
        case 9:
            routingBig = new BigInteger(routingCode);
            routingBig = routingBig.add(BigInteger.valueOf(100000L + 1L));
            break;
        case 11:
            routingBig = new BigInteger(routingCode);
            routingBig = routingBig.add(BigInteger.valueOf(1000000000L + 100000L + 1L));
            break;
        default:
            throw new IllegalArgumentException(
                    "Invalid length for the routing code. Expected 0, 5, 9 or 11 but got "
                        + routingLength);
        }
        BigInteger binary = routingBig; //Set the rightmost 37 bits

        final BigInteger five = BigInteger.valueOf(5);
        final BigInteger ten = BigInteger.valueOf(10);
        String trackingCode = msg.substring(0, 20);

        //First tracking code digit
        binary = binary.multiply(ten);
        binary = binary.add(new BigInteger(trackingCode.substring(0, 1)));

        //Second tracking code digit
        binary = binary.multiply(five);
        binary = binary.add(new BigInteger(trackingCode.substring(1, 2)));

        //Remaining tracking code digits
        for (int i = 2; i < 20; i++) {
            binary = binary.multiply(ten);
            binary = binary.add(new BigInteger(trackingCode.substring(i, i + 1)));
        }

        return binary;
    }

    static byte[] to13ByteArray(BigInteger binary) {
        byte[] result = new byte[13];
        byte[] bin = binary.toByteArray();
        System.arraycopy(bin, 0, result, 13 - bin.length, bin.length);
        return result;
    }

    private static final int GENERATOR_POLYNOMIAL = 0x0F35;
    private static final int ELEVEN_BITS = 0x07FF;

    static int calcFCS(byte[] binary) {
        int frameCheckSequence = ELEVEN_BITS;
        int data;
        int startBit;

        for (int byteIndex = 0; byteIndex < 13; byteIndex++) {
            if (byteIndex == 0) {
                //For the most significant byte skipping the 2 most significant bits
                startBit = 2;
                data = binary[0] << 5;
            } else {
                //For the rest of the bytes
                startBit = 0;
                data = binary[byteIndex] << 3;
            }
            for (int bit = startBit; bit < 8; bit++) {
                if (((frameCheckSequence ^ data) & 0x400) != 0) {
                    frameCheckSequence = (frameCheckSequence << 1) ^ GENERATOR_POLYNOMIAL;
                } else {
                    frameCheckSequence = (frameCheckSequence << 1);
                }
                frameCheckSequence &= ELEVEN_BITS;
                data <<= 1;
            }
        }
        return frameCheckSequence;
    }

    static int[] convertToCodewords(BigInteger binary) {
        int[] codewords = new int[10];
        BigInteger[] quotRem;

        quotRem = binary.divideAndRemainder(BigInteger.valueOf(636));
        codewords[9] = quotRem[1].intValue();
        binary = quotRem[0];

        final BigInteger const1365 = BigInteger.valueOf(1365);
        for (int i = 8; i >= 1; i--) {
            quotRem = binary.divideAndRemainder(const1365);
            codewords[i] = quotRem[1].intValue();
            binary = quotRem[0];
        }

        codewords[0] = binary.intValue();

        return codewords;
    }

    static int[] modifyCodewords(int[] codewords, int fcs) {
        int[] modified = new int[10];
        //Codeword J is doubled (orientation information)
        modified[9] = codewords[9] * 2;

        System.arraycopy(codewords, 1, modified, 1, 8);

        //Codeword A: process FCS
        if ((fcs & 0x0400) != 0) {
            modified[0] = codewords[0] + 659;
        } else {
            modified[0] = codewords[0];
        }
        return modified;
    }

    static char[] convertToCharacters(int[] codewords, int fcs) {
        int c = codewords.length;
        char[] chars = new char[c];
        for (int i = 0; i < c; i++) {
            int codeword = codewords[i];
            if (codeword < TABLE5OF13.length) {
                chars[i] = TABLE5OF13[codeword];
            } else {
                chars[i] = TABLE2OF13[codeword - TABLE5OF13.length];
            }
            if ((fcs & (1 << i)) != 0) {
                chars[i] = (char)(~chars[i] & 0x1FFF); //bitwise negation
            }
        }
        return chars;
    }

    static String convertToBars(char[] chars) {
        StringBuffer bars = new StringBuffer(65);
        bars.setLength(65);
        for (int i = 0; i < bars.length(); i++) {
            BarToCharacterMapping mapping = TABLE_BAR_TO_CHARACTER[i];
            int resultBits = 0;
            char c;

            c = chars[mapping.ascChar];
            if ((c & mapping.ascBitMap) != 0) {
                resultBits |= 1;
            }

            c = chars[mapping.descChar];
            if ((c & mapping.descBitMap) != 0) {
                resultBits |= 2;
            }

            bars.setCharAt(i, (char)(resultBits + '0'));
        }
        return bars.toString();
    }

    /** {@inheritDoc} */
    protected String[] encodeHighLevel(String msg) {
        BigInteger binary = convertToBinary(msg);
        int fcs = calcFCS(to13ByteArray(binary));
        int[] codewords = convertToCodewords(binary);
        int[] modified = modifyCodewords(codewords, fcs);
        char[] chars = convertToCharacters(modified, fcs);
        String bars = convertToBars(chars);
        return new String[] {bars};
    }

    /** {@inheritDoc} */
    protected String normalizeMessage(String msg) {
        StringBuffer sb = new StringBuffer(msg.length());
        for (int i = 0, c = msg.length(); i < c; i++) {
            char ch = msg.charAt(i);
            if (Character.isDigit(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /** {@inheritDoc} */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        //slightly specialized version of this method for USPS 4BC:
        //-> there's no direct correlation between the original msg chars and the effective chars
        String normalizedMsg = normalizeMessage(msg);
        String[] encodedMsg = encodeHighLevel(normalizedMsg);
        //encodedMsg.length will always be 1

        logic.startBarcode(msg, normalizedMsg);

        // encode message
        String codeword = encodedMsg[0];
        for (int i = 0, count = codeword.length(); i < count; i++) {
            int height = Integer.parseInt(codeword.substring(i, i + 1));
            logic.addBar(true, height);
        }

        logic.endBarcode();
    }
}
