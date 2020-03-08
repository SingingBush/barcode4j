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

/* $Id: PDF417HighLevelEncoder.java,v 1.10 2012-05-17 13:57:37 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;

import org.krysalis.barcode4j.tools.ECIUtil;
import org.krysalis.barcode4j.tools.URLUtil;

/**
 * PDF417 high-level encoder following the algorithm described in ISO/IEC 15438:2001(E) in
 * annex P.
 *
 * @version $Id: PDF417HighLevelEncoder.java,v 1.10 2012-05-17 13:57:37 jmaerki Exp $
 */
public class PDF417HighLevelEncoder implements PDF417Constants {

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
     * @param charset the charset (encoding) to use for the conversion
     * @return the byte array of the message
     */
    static byte[] getBytesForMessage(String msg, String charset) {
        try {
            return msg.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(
                    "Incompatible JVM! The '" + charset + "' charset is not available!");
        }
    }

    /**
     * Performs high-level encoding of a PDF417 message (binary data).
     * @param data the binary data stream
     * @return the encoded message (the char values range from 0 to 928)
     */
    public static String encodeHighLevel(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length);
        encodeBinary(null, data, 0, data.length, TEXT_COMPACTION, sb);
        return sb.toString();
    }

    /**
     * Performs high-level encoding of a PDF417 message using the algorithm described in annex P
     * of ISO/IEC 15438:2001(E).
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 928)
     */
    public static String encodeHighLevel(String msg) {
        return encodeHighLevel(msg, DEFAULT_ENCODING, false);
    }

    /**
     * Performs high-level encoding of a PDF417 message using the algorithm described in annex P
     * of ISO/IEC 15438:2001(E).
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 928)
     */
    public static String encodeHighLevel(String msg, String encoding, boolean enableECI) {
        //the codewords 0..928 are encoded as Unicode characters
        StringBuffer sb = new StringBuffer(msg.length());

        if (enableECI) {
            int eci = ECIUtil.getECIForEncoding(encoding);
            if (eci < 0) {
                throw new IllegalArgumentException("No ECI supported for encoding: " + encoding);
            }
            encodingECI(eci, sb);
        }

        String url = URLUtil.getURL(msg);
        if (url != null) {
            byte[] data;
            try {
                data = URLUtil.getData(url, encoding);
            } catch (IOException ioe) {
                throw new IllegalArgumentException(
                        "Could not get binary content from URL: " + url, ioe);
            }
            encodeBinary(null, data, 0, data.length, TEXT_COMPACTION, sb);
            return sb.toString();
        }

        byte[] bytes = null; //Fill later and only if needed


        int len = msg.length();
        int p = 0;
        int encodingMode = TEXT_COMPACTION; //Default mode, see 4.4.2.1
        int textSubMode = SUBMODE_ALPHA;
        while (p < len) {
            int n = determineConsecutiveDigitCount(msg, p);
            if (n >= 13) {
                sb.append((char)LATCH_TO_NUMERIC);
                encodingMode = NUMERIC_COMPACTION;
                textSubMode = SUBMODE_ALPHA; //Reset after latch
                encodeNumeric(msg, p, n, sb);
                p += n;
            } else {
                int t = determineConsecutiveTextCount(msg, p);
                if (t >= 5 || n == len) {
                    if (encodingMode != TEXT_COMPACTION) {
                        sb.append((char)LATCH_TO_TEXT);
                        encodingMode = TEXT_COMPACTION;
                        textSubMode = SUBMODE_ALPHA; //start with submode alpha after latch
                    }
                    textSubMode = encodeText(msg, p, t, sb, textSubMode);
                    p += t;
                } else {
                    if (bytes == null) {
                        bytes = getBytesForMessage(msg, encoding);
                    }
                    int b = determineConsecutiveBinaryCount(msg, bytes, p);
                    if (b == 0) {
                        b = 1;
                    }
                    if (b == 1 && encodingMode == TEXT_COMPACTION) {
                        //Switch for one byte (instead of latch)
                        encodeBinary(msg, bytes, p, b, encodingMode, sb);
                    } else {
                        //Mode latch performed by encodeBinary()
                        encodeBinary(msg, bytes, p, b, encodingMode, sb);
                        encodingMode = BYTE_COMPACTION;
                        textSubMode = SUBMODE_ALPHA; //Reset after latch
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
     * @param initialSubmode should normally be SUBMODE_ALPHA
     * @return the text submode in which this method ends
     */
    public static int encodeText(String msg, int startpos, int count, StringBuffer sb,
            int initialSubmode) {
        StringBuffer tmp = new StringBuffer(count);
        int submode = initialSubmode;
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
                        tmp.append((char)(ch - 65));
                        //space cannot happen here, it is also in "Lower"
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
                        if (startpos + idx + 1 < count) {
                            char next = msg.charAt(startpos + idx + 1);
                            if (isPunctuation(next)) {
                                submode = SUBMODE_PUNCTUATION;
                                tmp.append((char)25); //pl
                                continue;
                            }
                        }
                        tmp.append((char)29); //ps
                        tmp.append((char)PUNCTUATION[ch]);
                    }
                }
                break;
            default: //SUBMODE_PUNCTUATION
                if (isPunctuation(ch)) {
                    tmp.append((char)PUNCTUATION[ch]);
                } else {
                    submode = SUBMODE_ALPHA;
                    tmp.append((char)29); //al
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
                h = (char)((h * 30) + tmp.charAt(i));
                sb.append(h);
            } else {
                h = tmp.charAt(i);
            }
        }
        if ((len % 2) != 0) {
            sb.append((char)((h * 30) + 29)); //ps
        }
        return submode;
    }

    /**
     * Encode parts of the message using Byte Compaction as described in ISO/IEC 15438:2001(E),
     * chapter 4.4.3. The Unicode characters will be converted to binary using the cp437
     * codepage.
     * @param msg the message
     * @param bytes the message converted to a byte array
     * @param startpos the start position within the message
     * @param count the number of bytes to encode
     * @param startmode the mode from which this method starts
     * @param sb receives the encoded codewords
     */
    public static void encodeBinary(String msg, byte[] bytes, int startpos, int count,
            int startmode, StringBuffer sb) {
        if (count == 1 && startmode == TEXT_COMPACTION) {
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
        int idx = 0;
        StringBuffer tmp = new StringBuffer(count / 3 + 1);
        final BigInteger num900 = BigInteger.valueOf(900);
        final BigInteger num0 = BigInteger.valueOf(0);
        while (idx < count - 1) {
            tmp.setLength(0);
            int len = Math.min(44, count - idx);
            String part = "1" + msg.substring(startpos + idx, startpos + idx + len);
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

            while (numericCount < 13 && isDigit(ch)) {
                numericCount++;
                //textCount++;
                int i = idx + numericCount;
                if (i < len) {
                    ch = msg.charAt(i);
                } else {
                    break;
                }
            }
            if (numericCount >= 13) {
                return idx - startpos;
            }
            while (textCount < 5 && isText(ch)) {
                textCount++;
                int i = idx + textCount;
                if (i < len) {
                    ch = msg.charAt(i);
                } else {
                    break;
                }
            }
            if (textCount >= 5) {
                return idx - startpos;
            }
            ch = msg.charAt(idx);

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

    private static void encodingECI(int eci, StringBuffer sb) {
        if (eci >= 0 && eci < 900) {
            sb.append((char)ECI_CHARSET);
            sb.append((char)eci);
        } else if (eci < 810900) {
            sb.append((char)ECI_GENERAL_PURPOSE);
            sb.append((char)(eci / 900 - 1));
            sb.append((char)(eci % 900));
        } else if (eci < 811800) {
            sb.append((char)ECI_USER_DEFINED);
            sb.append((char)(810900 - eci));
        } else {
            throw new IllegalArgumentException(
                    "ECI number not in valid range from 0..811799, but was " + eci);
        }
    }

}
