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

/* $Id: PDF417HighLevelEncoder.java,v 1.2 2006-11-07 16:03:17 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * PDF417 high-level encoder following the algorithm described in ISO/IEC 15438:2001(E) in
 * annex P.
 * 
 * @version $Id: PDF417HighLevelEncoder.java,v 1.2 2006-11-07 16:03:17 jmaerki Exp $
 */
public class PDF417HighLevelEncoder implements PDF417Constants {

    private static final int TEXT_COMPACTION = 0;
    private static final int BYTE_COMPACTION = 1;
    private static final int NUMERIC_COMPACTION = 2;
    
    private static final int SUBMODE_ALPHA = 0;
    private static final int SUBMODE_LOWER = 1;
    private static final int SUBMODE_MIXED = 2;
    private static final int SUBMODE_PUNCTUATION = 3;
    
    private static final byte[] MIXED = new byte[128];
    private static final byte[] PUNCTUATION = new byte[128];
    
    static {
        //Construct inverse lookups
        Arrays.fill(MIXED, (byte)-1);
        for (byte i = 0; i < TEXT_MIXED_RAW.length; i++) {
            byte b = TEXT_MIXED_RAW[i];
            if (b > 0) {
                MIXED[b] = i;
            }
        }
        Arrays.fill(PUNCTUATION, (byte)-1);
        for (byte i = 0; i < TEXT_PUNCTUATION_RAW.length; i++) {
            byte b = TEXT_PUNCTUATION_RAW[i];
            if (b > 0) {
                PUNCTUATION[b] = i;
            }
        }
    }
    
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
     * Performs high-level encoding of a PDF417 message using the algorithm described in annex P
     * of ISO/IEC 15438:2001(E).
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 928)
     */
    public static String encodeHighLevel(String msg) {
        byte[] bytes = null; //Fill later and only if needed 
        
        //the codewords 0..928 are encoded as Unicode characters
        StringBuffer sb = new StringBuffer(msg.length());
        
        int len = msg.length();
        int p = 0;
        int encodingMode = TEXT_COMPACTION; //Default mode, see 4.4.2.1
        while (p < len) {
            int n = determineConsecutiveDigitCount(msg, p);
            if (n >= 13) {
                sb.append((char)LATCH_TO_NUMERIC);
                encodingMode = NUMERIC_COMPACTION;
                encodeNumeric(msg, p, n, sb);
                p += n;
            } else {
                int t = determineConsecutiveTextCount(msg, p);
                if (t >= 5 || n == len) {
                    if (encodingMode != TEXT_COMPACTION) {
                        sb.append((char)LATCH_TO_TEXT);
                    }
                    encodingMode = TEXT_COMPACTION;
                    encodeText(msg, p, t, sb);
                    p += t;
                } else {
                    if (bytes == null) {
                        bytes = getBytesForMessage(msg);
                    }
                    int b = determineConsecutiveBinaryCount(msg, bytes, p);
                    if (b == 1 && encodingMode == TEXT_COMPACTION) {
                        encodeBinary(msg, bytes, p, b, sb);
                    } else {
                        //Mode latch performed by encodeBinary()
                        encodingMode = BYTE_COMPACTION;
                        encodeBinary(msg, bytes, p, b, sb);
                    }
                    p += b;
                }
            }
        }
        
        return sb.toString();
    }

    /**
     * Encode parts of the message using Text Compaction as described in ISO/IEC 15438:2001(E), 
     * chapter 4.4.2.
     * @param msg the message
     * @param startpos the start position within the message
     * @param count the number of characters to encode
     * @param sb receives the encoded codewords
     */
    public static void encodeText(String msg, int startpos, int count, StringBuffer sb) {
        StringBuffer tmp = new StringBuffer(count);
        int submode = SUBMODE_ALPHA;
        int idx = 0;
        while (true) {
            char ch = msg.charAt(startpos + idx);
            switch (submode) {
            case SUBMODE_ALPHA:
                if (isAlphaUpper(ch)) {
                    if (ch != ' ') {
                        tmp.append((char)(ch - 65));
                    } else {
                        tmp.append((char)26); //space
                    }
                } else {
                    if (isAlphaLower(ch)) {
                        submode = SUBMODE_LOWER;
                        tmp.append((char)27); //ll
                        continue;
                    } else if (isMixed(ch)) {
                        submode = SUBMODE_MIXED;
                        tmp.append((char)28); //ml
                        continue;
                    } else {
                        tmp.append((char)29); //ps
                        tmp.append((char)PUNCTUATION[ch]);
                        break;
                    }
                }
                break;
            case SUBMODE_LOWER:
                if (isAlphaLower(ch)) {
                    if (ch != ' ') {
                        tmp.append((char)(ch - 97));
                    } else {
                        tmp.append((char)26); //space
                    }
                } else {
                    if (isAlphaUpper(ch)) {
                        tmp.append((char)27); //as
                        if (ch != ' ') {
                            tmp.append((char)(ch - 65));
                        } else {
                            tmp.append((char)26); //space
                        }
                        break;
                    } else if (isMixed(ch)) {
                        submode = SUBMODE_MIXED;
                        tmp.append((char)28); //ml
                        continue;
                    } else {
                        tmp.append((char)29); //ps
                        tmp.append((char)PUNCTUATION[ch]);
                        break;
                    }
                }
                break;
            case SUBMODE_MIXED:
                if (isMixed(ch)) {
                    tmp.append((char)MIXED[ch]);
                } else {
                    if (isAlphaUpper(ch)) {
                        submode = SUBMODE_ALPHA;
                        tmp.append((char)28); //al
                        continue;
                    } else if (isAlphaLower(ch)) {
                        submode = SUBMODE_LOWER;
                        tmp.append((char)27); //ll
                        continue;
                    } else {
                        char next = msg.charAt(startpos + idx + 1);
                        if (isPunctuation(next)) {
                            submode = SUBMODE_PUNCTUATION;
                            tmp.append((char)25); //pl
                            continue;
                        } else {
                            tmp.append((char)29); //ps
                            tmp.append((char)PUNCTUATION[ch]);
                            break;
                        }
                    }
                }
                break;
            default: //SUBMODE_PUNCTUATION
                if (isPunctuation(ch)) {
                    tmp.append(PUNCTUATION[ch]);
                } else {
                    submode = SUBMODE_ALPHA;
                    tmp.append((char)28); //al
                    continue;
                }
            }
            idx++;
            if (idx >= count) {
                break;
            }
        }
        char h = 0;
        int len = tmp.length();
        for (int i = 0; i < len; i++) {
            boolean odd = (i % 2) != 0;
            if (odd) {
                sb.append((char)((h * 30) + tmp.charAt(i)));
            } else {
                h = tmp.charAt(i);
            }
        }
        if ((len % 2) != 0) {
            sb.append((char)((h * 30) + 29)); //ps
        }
    }

    /**
     * Encode parts of the message using Byte Compaction as described in ISO/IEC 15438:2001(E), 
     * chapter 4.4.3. The Unicode characters will be converted to to binary using the cp437 
     * codepage.
     * @param msg the message
     * @param bytes the message converted to a byte array
     * @param startpos the start position within the message
     * @param count the number of bytes to encode
     * @param sb receives the encoded codewords
     */
    public static void encodeBinary(String msg, byte[] bytes, int startpos, int count, StringBuffer sb) {
        if (count == 1) {
            sb.append((char)SHIFT_TO_BYTE);
        } else {
            boolean sixpack = ((count % 6) == 0); 
            if (sixpack) {
                sb.append((char)LATCH_TO_BYTE);
            } else {
                sb.append((char)LATCH_TO_BYTE_PADDED);
            }
        }
        
        char[] chars = new char[5];
        int idx = startpos;
        while ((startpos + count - idx) >= 6) {
            long t = 0;
            for (int i = 0; i < 6; i++) {
                t <<= 8;
                t += bytes[idx + i] & 0xff;
            }
            for (int i = 0; i < 5; i++) {
                chars[i] = (char)(t % 900);
                t /= 900;
            }
            for (int i = chars.length - 1; i >= 0; i--) {
                sb.append(chars[i]);
            }
            idx += 6;
        }
        //Encode rest (remaining n<5 bytes if any)
        for (int i = idx; i < startpos + count; i++) {
            int ch = bytes[i] & 0xff;
            sb.append((char)ch);
        }
    }
    
    public static void encodeNumeric(String msg, int startpos, int count, StringBuffer sb) {
        int idx = startpos;
        StringBuffer tmp = new StringBuffer(count / 3 + 1);
        final BigInteger num900 = BigInteger.valueOf(900);
        final BigInteger num0 = BigInteger.valueOf(0);
        while (idx < startpos + count) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            String part = "1" + msg.substring(idx, idx + len);
            BigInteger bigint = new BigInteger(part);
            do {
                BigInteger c = bigint.mod(num900);
                tmp.append((char)(c.intValue()));
                bigint = bigint.divide(num900);
            } while (!bigint.equals(num0));
            
            //Reverse temporary string
            for (int i = tmp.length() - 1; i >= 0; i--) {
                sb.append(tmp.charAt(i));
            }
            idx += len;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
    
    private static boolean isAlphaUpper(char ch) {
        return (ch == ' ' || (ch >= 'A' && ch <= 'Z'));
    }
    
    private static boolean isAlphaLower(char ch) {
        return (ch == ' ' || (ch >= 'a' && ch <= 'z'));
    }
    
    private static boolean isMixed(char ch) {
        return (MIXED[ch] != -1);
    }
    
    private static boolean isPunctuation(char ch) {
        return (PUNCTUATION[ch] != -1);
    }
    
    private static boolean isText(char ch) {
        return (ch == 9 //TAB
                || ch == 10 //LF
                || ch == 13 //CR
                || (ch >= 32 && ch <= 126)); 
    }
    /*
    private boolean isByte(int pos) {
        char ch = msg.charAt(pos);
        //Sun returns a ASCII 31 (?) for a character that cannot be mapped. Let's hope all
        //other VMs do the same
        return (byteMap[pos] != 31 || ch == '?');
    }
    
    private boolean isEncodableCharacter(int pos) {
        char ch = msg.charAt(pos);
        return isText(ch) || isByte(pos);
    }*/
    
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

    /**
     * Determines the number of consecutive characters that are encodable using text compaction.
     * @param msg the message
     * @param startpos the start position within the message
     * @return the requested character count
     */
    public static int determineConsecutiveTextCount(String msg, int startpos) {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx); 
            int numericCount = 0;
            while (numericCount < 13 && isDigit(ch) && idx < len) {
                numericCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (numericCount >= 13) {
                return idx - startpos - numericCount;
            }
            if (numericCount > 0) {
                //Heuristic: All text-encodable chars or digits are binary encodable
                continue;  
            }
            ch  = msg.charAt(idx);
            
            //Check if character is encodable
            if (!isText(ch)) {
                break;
            }
            idx++;
        }
        return idx - startpos;
    }

    /**
     * Determines the number of consecutive characters that are encodable using binary compaction.
     * @param msg the message
     * @param bytes the message converted to a byte array
     * @param startpos the start position within the message
     * @return the requested character count
     */
    public static int determineConsecutiveBinaryCount(String msg, byte[] bytes, int startpos) {
        int len = msg.length();
        int idx = startpos;
        while (idx < len) {
            char ch = msg.charAt(idx); 
            int numericCount = 0;
            int textCount = 0;
            while (numericCount < 13 && isDigit(ch) && idx < len) {
                numericCount++;
                textCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (numericCount >= 13) {
                return idx - startpos - numericCount;
            }
            while (textCount < 5 && isText(ch) && idx < len) {
                textCount++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
            if (textCount >= 5) {
                return idx - startpos - textCount;
            }
            if (textCount > 0) {
                //Heuristic: All text-encodable chars or digits are binary encodable
                continue;  
            }
            ch  = msg.charAt(idx);
            
            //Check if character is encodable
            //Sun returns a ASCII 63 (?) for a character that cannot be mapped. Let's hope all
            //other VMs do the same
            if (bytes[idx] == 63 && ch != '?') {
                throw new IllegalArgumentException("Non-encodable character detected: " 
                        + ch + " (Unicode: " + (int)ch + ")");
            }
            idx++;
        }
        return idx - startpos;
    }

    
}
