/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the Interleaved 2 of 5 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: Interleaved2Of5LogicImpl.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class Interleaved2Of5LogicImpl {

    private static final byte[][] CHARSET = {{1, 1, 2, 2, 1}, 
                                             {2, 1, 1, 1, 2}, 
                                             {1, 2, 1, 1, 2}, 
                                             {2, 2, 1, 1, 1}, 
                                             {1, 1, 2, 1, 2}, 
                                             {2, 1, 2, 1, 1}, 
                                             {1, 2, 2, 1, 1}, 
                                             {1, 1, 1, 2, 2}, 
                                             {2, 1, 1, 2, 1}, 
                                             {1, 2, 1, 2, 1}};

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    
    /**
     * Main constructor.
     * @param mode the checksum mode
     */
    public Interleaved2Of5LogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the current checksum mode
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * @param msg message to calculate the check digit for
     * @param oddMultiplier multiplier to be used for odd positions (usually 3 or 4)
     * @param evenMultiplier multiplier to be used for even positions (usually 1 or 9)
     * @return char the check digit
     */
    public static char calcChecksum(String msg, int oddMultiplier, int evenMultiplier) {
        int oddsum = 0;
        int evensum = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (i % 2 == 0) {
                evensum += Character.digit(msg.charAt(i), 10);
            } else {
                oddsum += Character.digit(msg.charAt(i), 10);
            }
        }
        int check = 10 - ((evensum * oddMultiplier + oddsum * evenMultiplier) % 10);
        if (check >= 10) check = 0;
        return Character.forDigit(check, 10);
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * This method uses the default specification 
     * (ITF-14, EAN-14, SSC-14, DUN14 and USPS).
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        return calcChecksum(msg, 3, 1);
    }

    /**
     * Verifies the checksum for a message.
     * @param msg message (check digit included)
     * @return boolean True, if the checksum is correct
     */
    public static boolean validateChecksum(String msg) {
        char actual = msg.charAt(msg.length() - 1);
        char expected = calcChecksum(msg.substring(0, msg.length() - 1));
        return (actual == expected);
    }

    private int widthAt(char ch, int index) {
        if (Character.isDigit(ch)) {
            int digit = Character.digit(ch, 10);
            int width = CHARSET[digit][index];
            return width;
        } else {
            throw new IllegalArgumentException("Invalid character '" + ch 
                    + " (" + Character.getNumericValue(ch) 
                    + ")'. Expected a digit.");
        }
    }

    private void encodeGroup(ClassicBarcodeLogicHandler logic, String group) {
        if (group.length() != 2) {
            throw new IllegalArgumentException("Parameter group must have two characters");
        }
        
        logic.startBarGroup(BarGroup.MSG_CHARACTER, group);
        for (int index = 0; index < 5; index++) {
            logic.addBar(true, widthAt(group.charAt(0), index));
            logic.addBar(false, widthAt(group.charAt(1), index));
        }
        logic.endBarGroup();
    }
    
    private String handleChecksum(String msg) {
        if (getChecksumMode() == ChecksumMode.CP_ADD) {
            return msg + calcChecksum(msg);
        } else if (getChecksumMode() == ChecksumMode.CP_CHECK) {
            if (!validateChecksum(msg)) {
                throw new IllegalArgumentException("Message '" 
                    + msg
                    + "' has a bad checksum. Expected: " 
                    + calcChecksum(msg.substring(0, msg.length() - 1)));
            }
            return msg;
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return msg;
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return msg; //equals ignore
        } else {
            throw new UnsupportedOperationException(
                "Unknown checksum mode: " + getChecksumMode());
        }
    }
    
    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        //Checksum handling as requested
        String s = handleChecksum(msg);
        
        //Length must be even
        if ((s.length() % 2) != 0) {
            s = "0" + s;
        }

        logic.startBarcode(msg);

        //Start character
        logic.startBarGroup(BarGroup.START_CHARACTER, null);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.endBarGroup();

        //Process string
        int i = 0;
        do {
            encodeGroup(logic, s.substring(i, i + 2));
            i += 2;
        } while (i < s.length());

        //End character
        logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
        logic.addBar(true, 2);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();

        logic.endBarcode();
    }

}