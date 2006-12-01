/*
 * Copyright 2006 Jeremias Maerki.
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

/* $Id: DataMatrixHighLevelEncoder.java,v 1.3 2006-12-01 15:22:43 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * DataMatrix ECC 200 data encoder following the algorithm described in ISO/IEC 16022:200(E) in
 * annex S.
 * 
 * @version $Id: DataMatrixHighLevelEncoder.java,v 1.3 2006-12-01 15:22:43 jmaerki Exp $
 */
public class DataMatrixHighLevelEncoder implements DataMatrixConstants {

    private static final boolean DEBUG = true;
    
    private static final int ASCII_ENCODATION = 0;
    private static final int C40_ENCODATION = 1;
    private static final int TEXT_ENCODATION = 2;
    private static final int X12_ENCODATION = 3;
    private static final int EDIFACT_ENCODATION = 4;
    private static final int BASE256_ENCODATION = 5;

    private static final String[] ENCODATION_NAMES
        = new String[] {"ASCII", "C40", "Text", "ANSI X12", "EDIFACT", "Base 256"};
    
    private static final String DEFAULT_ASCII_ENCODING = "ISO-8859-1";
    
    /**
     * Converts the message to a byte array using the default encoding (cp437) as defined by the
     * specification
     * @param msg the message
     * @return the byte array of the message
     */
    public static byte[] getBytesForMessage(String msg) {
        final String charset = "cp437"; //See 4.4.3 and annex B of ISO/IEC 15438:2001(E)
        try {
            return msg.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(
                    "Incompatible JVM! The '" + charset + "' charset is not available!");
        }
    }
    
    /**
     * Performs message encoding of a DataMatrix message using the algorithm described in annex P
     * of ISO/IEC 16022:2000(E).
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 255)
     */
    public static String encodeHighLevel(String msg) {
        //the codewords 0..255 are encoded as Unicode characters
        Encoder[] encoders = new Encoder[] {new ASCIIEncoder(), 
                new C40Encoder(), new TextEncoder()}; 
        
        int encodingMode = ASCII_ENCODATION; //Default mode
        EncoderContext context = new EncoderContext(msg);
        while (context.hasMoreCharacters()) {
            encoders[encodingMode].encode(context);
            if (context.newEncoding >= 0) {
                encodingMode = context.newEncoding;
                context.resetEncoderSignal();
            }
        }
        if (encodingMode != ASCII_ENCODATION) {
            context.writeCodeword('\u00fe'); //Unlatch (254)
        }
        
        return context.codewords.toString();
    }

    private static class EncoderContext {

        private String msg;
        private StringBuffer codewords;
        private int pos = 0;
        private int newEncoding = -1;
        
        public EncoderContext(String msg) {
            this.msg = msg;
            this.codewords = new StringBuffer(msg.length());
        }
        
        public char getCurrentChar() {
            return msg.charAt(pos);
        }
        
        public void writeCodewords(String codewords) {
            this.codewords.append(codewords);
        }
        
        public void writeCodeword(char codeword) {
            this.codewords.append(codeword);
        }
        
        public void signalEncoderChange(int encoding) {
            this.newEncoding = encoding;
        }
        
        public void resetEncoderSignal() {
            this.newEncoding = -1;
        }

        public boolean hasMoreCharacters() {
            return pos < msg.length();
        }
    }
    
    private interface Encoder {
        int getEncodingMode();
        void encode(EncoderContext context);
    }
    
    private static class ASCIIEncoder implements Encoder {
        
        public int getEncodingMode() {
            return ASCII_ENCODATION;
        }
        
        public void encode(EncoderContext context) {
            //step B
            int n = determineConsecutiveDigitCount(context.msg, context.pos);
            if (n >= 2) {
                context.writeCodeword(encodeASCIIDigits(context.msg.charAt(context.pos), 
                        context.msg.charAt(context.pos + 1)));
                context.pos += 2;
            } else {
                char c = context.getCurrentChar();
                int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                if (newMode != getEncodingMode()) {
                    switch (newMode) {
                    case BASE256_ENCODATION:
                        context.writeCodeword(LATCH_TO_BASE256);
                        //lengthBytePosition = codewords.length();
                        context.writeCodeword('\000');
                        context.signalEncoderChange(BASE256_ENCODATION);
                        return;
                    case C40_ENCODATION:
                        context.writeCodeword(LATCH_TO_C40);
                        context.signalEncoderChange(C40_ENCODATION);
                        return;
                    case X12_ENCODATION:
                        context.writeCodeword(LATCH_TO_ANSIX12);
                        context.signalEncoderChange(X12_ENCODATION);
                        break;
                    case TEXT_ENCODATION:
                        context.writeCodeword(LATCH_TO_TEXT);
                        context.signalEncoderChange(TEXT_ENCODATION);
                        break;
                    case EDIFACT_ENCODATION:
                        context.writeCodeword(LATCH_TO_EDIFACT);
                        context.signalEncoderChange(EDIFACT_ENCODATION);
                        break;
                    default:
                        throw new IllegalStateException("Illegal mode: " + newMode);
                    }
                } else if (isExtendedASCII(c)) {
                    context.writeCodeword(UPPER_SHIFT);
                    context.writeCodeword((char)(c - 128));
                    context.pos++;
                } else {
                    if (DEBUG) {
                        if (!isASCII7(c)) {
                            throw new IllegalArgumentException("Not an ASCII-7 character");
                        }
                    }
                    context.writeCodeword((char)(c + 1));
                    context.pos++;
                }
                
            }
        }
        
    }
    
    private static class C40Encoder implements Encoder {
        
        public int getEncodingMode() {
            return C40_ENCODATION;
        }
        
        public void encode(EncoderContext context) {
            //step C
            StringBuffer buffer = new StringBuffer();
            while (context.hasMoreCharacters()) {
                char c = context.getCurrentChar();
                encodeChar(c, buffer);
                int count = buffer.length(); 
                if (count >= 3) {
                    context.writeCodewords(encodeToCodewords(buffer, 0));
                    buffer.delete(0, 3);
                    
                    int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                    if (newMode != getEncodingMode()) {
                        context.signalEncoderChange(newMode);
                    }
                }
                context.pos++;
            }
            int count = buffer.length();
            if (count == 2) {
                buffer.append('\0'); //Shift 1
                context.writeCodewords(encodeToCodewords(buffer, 0));
            } else if (count == 1) {
                context.writeCodeword(C40_UNLATCH);
                //TODO Skip unlatch if only one character until the symbol is full
                //and it has to be enlarged 
                context.pos--;
                context.signalEncoderChange(ASCII_ENCODATION);
            }
            
        }
        
        protected void encodeChar(char c, StringBuffer sb) {
            if (c == ' ') {
                sb.append('\3');
            } else if (c >= '0' && c <= '9') {
                sb.append((char)(c - 48 + 4));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append((char)(c - 65 + 14));
            } else if (c >= '\0' && c <= '\u001f') {
                sb.append('\0'); //Shift 1 Set
                sb.append(c);
            } else if (c >= '!' && c <= '/') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 33));
            } else if (c >= ':' && c <= '@') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 58 + 15));
            } else if (c >= '[' && c <= '_') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 91 + 22));
            } else if (c >= '\'' && c <= '\u007f') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 96));
            } else if (c >= '\u0080') {
                sb.append("\1\u001e"); //Shift 2, Upper Shift
                encodeChar((char)(c - 128), sb);
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
        }
        
        protected String encodeToCodewords(StringBuffer sb, int startPos) {
            char c1 = sb.charAt(startPos);
            char c2 = sb.charAt(startPos + 1);
            char c3 = sb.charAt(startPos + 2);
            int v = (1600 * c1) + (40 * c2) + c3 + 1;
            char cw1 = (char)(v / 256);
            char cw2 = (char)(v % 256);
            return "" + cw1 + cw2; 
        }

    }
    
    private static class TextEncoder extends C40Encoder {
        
        public int getEncodingMode() {
            return TEXT_ENCODATION;
        }
        
        protected void encodeChar(char c, StringBuffer sb) {
            if (c == ' ') {
                sb.append('\3');
            } else if (c >= '0' && c <= '9') {
                sb.append((char)(c - 48 + 4));
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char)(c - 97 + 14));
            } else if (c >= '\0' && c <= '\u001f') {
                sb.append('\0'); //Shift 1 Set
                sb.append(c);
            } else if (c >= '!' && c <= '/') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 33));
            } else if (c >= ':' && c <= '@') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 58 + 15));
            } else if (c >= '[' && c <= '_') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 91 + 22));
            } else if (c == '\'') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 96));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 65 + 1));
            } else if (c >= '{' && c <= '\u007f') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 123 + 26));
            } else if (c >= '\u0080') {
                sb.append("\1\u001e"); //Shift 2, Upper Shift
                encodeChar((char)(c - 128), sb);
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
        }
        
    }
    
    private static char encodeASCIIDigits(char digit1, char digit2) {
        if (isDigit(digit1) && isDigit(digit2)) {
            int num = (digit1 - 48) * 10 + (digit2 - 48);
            return (char)(num + 130);
        } else {
            throw new IllegalArgumentException("not digits: " + digit1 + digit2);
        }
    }

    private static int lookAheadTest(String msg, int startpos, int currentMode) {
        float[] charCounts;
        //step J
        if (currentMode == ASCII_ENCODATION) {
            charCounts = new float[] {0, 1, 1, 1, 1, 1.25f};
        } else {
            charCounts = new float[] {1, 2, 2, 2, 2, 2.25f};
            charCounts[currentMode] = 0;
        }
        
        int charsProcessed = 0;
        while (true) {
            //step K
            if ((startpos + charsProcessed) == msg.length() - 1) {
                int min = Integer.MAX_VALUE;
                byte[] mins = new byte[6];
                int[] intCharCounts = new int[6];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                int minCount = getMinimumCount(mins);
                
                if (intCharCounts[ASCII_ENCODATION] == min) {
                    return ASCII_ENCODATION;
                } else if (minCount == 1 && mins[BASE256_ENCODATION] > 0) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                } else {
                    return C40_ENCODATION;
                }
            }
            
            char c = msg.charAt(startpos + charsProcessed);
            charsProcessed++;

            //step L
            if (isDigit(c)) {
                charCounts[ASCII_ENCODATION] += 0.5;
            } else if (isExtendedASCII(c)) {
                charCounts[ASCII_ENCODATION] = (int)Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION] += 2;
            } else {
                charCounts[ASCII_ENCODATION] = (int)Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION] += 1;
            }
            
            //step M
            if (isNativeC40(c)) {
                charCounts[C40_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[C40_ENCODATION] += 8f / 3f;
            } else {
                charCounts[C40_ENCODATION] += 4f / 3f;
            }
            
            //step N
            if (isNativeText(c)) {
                charCounts[TEXT_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[TEXT_ENCODATION] += 8f / 3f;
            } else {
                charCounts[TEXT_ENCODATION] += 4f / 3f;
            }
            
            //step O
            if (isNativeX12(c)) {
                charCounts[X12_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[X12_ENCODATION] += 13f / 3f;
            } else {
                charCounts[X12_ENCODATION] += 10f / 3f;
            }
            
            //step P
            if (isNativeEDIFACT(c)) {
                charCounts[EDIFACT_ENCODATION] += 3f / 4f;
            } else if (isExtendedASCII(c)) {
                charCounts[EDIFACT_ENCODATION] += 17f / 4f;
            } else {
                charCounts[EDIFACT_ENCODATION] += 13f / 4f;
            }
            
            // step Q
            if (isSpecialB256(c)) {
                charCounts[BASE256_ENCODATION] += 4;
            } else {
                charCounts[BASE256_ENCODATION] += 1;
            }
            
            //step R
            if (charsProcessed >= 4) {
                /*
                for (int a = 0; a < charCounts.length; a++) {
                    System.out.println(a + " " + ENCODATION_NAMES[a] + " " + charCounts[a]);
                }*/
                
                int min = Integer.MAX_VALUE;
                int[] intCharCounts = new int[6];
                byte[] mins = new byte[6];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                int minCount = getMinimumCount(mins);
                
                if (intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[C40_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[TEXT_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[X12_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[EDIFACT_ENCODATION]) {
                    return ASCII_ENCODATION;
                } else if (intCharCounts[BASE256_ENCODATION] + 1 <= intCharCounts[ASCII_ENCODATION]
                        || (mins[C40_ENCODATION] 
                                 + mins[TEXT_ENCODATION]
                                 + mins[X12_ENCODATION]
                                 + mins[EDIFACT_ENCODATION]) == 0) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                } else if (intCharCounts[C40_ENCODATION] + 1 < intCharCounts[ASCII_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[EDIFACT_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[TEXT_ENCODATION]) {
                    if (intCharCounts[C40_ENCODATION] < intCharCounts[X12_ENCODATION]) {
                        return C40_ENCODATION;
                    } else if (intCharCounts[C40_ENCODATION] == intCharCounts[X12_ENCODATION]) {
                        int p = startpos + charsProcessed + 1;
                        while (p < msg.length()) {
                            char tc = msg.charAt(p);
                            if (isX12TermSep(tc)) {
                                return X12_ENCODATION;
                            } else if (!isNativeX12(tc)) {
                                break;
                            }
                            p++;
                        }
                        return C40_ENCODATION;
                    }
                }
            }
        }
    }

    private static int findMinimums(float[] charCounts, int[] intCharCounts, 
            int min, byte[] mins) {
        Arrays.fill(mins, (byte)0);
        for (int i = 0; i < 6; i++) {
            intCharCounts[i] = (int)Math.ceil(charCounts[i]);
            int current = intCharCounts[i];
            if (min > current) {
                min = current;
                Arrays.fill(mins, (byte)0);
            }
            if (min == current) {
                mins[i]++;
                
            }
        }
        return min;
    }
    
    private static int getMinimumCount(byte[] mins) {
        int minCount = 0;
        for (int i = 0; i < 6; i++) {
            minCount += mins[i];
        }
        return minCount;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    private static boolean isText(char ch) {
        return (ch == 9 //TAB
                || ch == 10 //LF
                || ch == 13 //CR
                || (ch >= 32 && ch <= 126)); 
    }

    private static final String EXTENDED_ASCII; 
    static {
        byte[] buf = new byte[128];
        for (int i = 0; i < 128; i++) {
            buf[i] = (byte)(i + 128);
        }
        try {
            EXTENDED_ASCII = new String(buf, DEFAULT_ASCII_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private static final boolean isExtendedASCII(char ch) {
        return (EXTENDED_ASCII.indexOf(ch) >= 0); 
    }
    
    private static final boolean isASCII7(char ch) {
        return (ch >= 0 && ch <=127);
    }
    
    private static final boolean isNativeC40(char ch) {
        //return isASCII7(ch);
        return (ch == 32)
                || (ch >= 48 && ch <= 57) //0..9
                || (ch >= 65 && ch <= 90); //A..Z
    }
    
    private static final boolean isNativeText(char ch) {
        //return isASCII7(ch);
        return (ch == 32)
        || (ch >= 48 && ch <= 57) //0..9
        || (ch >= 97 && ch <= 122); //a..z
    }
    
    private static final boolean isNativeX12(char ch) {
        return isX12TermSep(ch)
                || (ch == 32) //SPACE
                || (ch >= 48 && ch <= 57)
                || (ch >= 65 && ch <= 90); 
    }
    
    private static boolean isX12TermSep(char ch) {
        return (ch == 13) //CR
                || (ch == 42) //"*"
                || (ch == 62); //">"
    }

    private static final boolean isNativeEDIFACT(char ch) {
        return (ch >= 32 && ch <= 94);
    }
    
    private static final boolean isSpecialB256(char ch) {
        return false; //TODO NOT IMPLEMENTED YET!!!
    }
    
    
    /**
     * Determines the number of consecutive characters that are encodable using numeric compaction.
     * @param msg the message
     * @param startpos the start position within the message
     * @return the requested character count
     */
    public static int determineConsecutiveDigitCount(String msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx); 
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

}
