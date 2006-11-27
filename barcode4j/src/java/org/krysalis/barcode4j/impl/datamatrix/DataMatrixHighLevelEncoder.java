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

/* $Id: DataMatrixHighLevelEncoder.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * DataMatrix ECC 200 data encoder following the algorithm described in ISO/IEC 16022:200(E) in
 * annex S.
 * 
 * @version $Id: DataMatrixHighLevelEncoder.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $
 */
public class DataMatrixHighLevelEncoder implements DataMatrixConstants {

    private static final boolean DEBUG = true;
    
    private static final int ASCII_ENCODATION = 0;
    private static final int C40_ENCODATION = 1;
    private static final int TEXT_ENCODATION = 2;
    private static final int X12_ENCODATION = 3;
    private static final int EDIFACT_ENCODATION = 4;
    private static final int BASE256_ENCODATION = 5;

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
        StringBuffer sb = new StringBuffer(msg.length());
        int lengthBytePosition = -1;
        
        int len = msg.length();
        int p = 0;
        int encodingMode = ASCII_ENCODATION; //Default mode
        while (p < len) {
            char c;
            switch (encodingMode) {
            case ASCII_ENCODATION:
                //step B
                int n = determineConsecutiveDigitCount(msg, p);
                if (n >= 2) {
                    sb.append(encodeASCIIDigits(msg.charAt(p), msg.charAt(p + 1)));
                    p += 2;
                } else {
                    c = msg.charAt(p);
                    int newMode = lookAheadTest(msg, p, encodingMode);
                    if (newMode != encodingMode) {
                        switch (newMode) {
                        case BASE256_ENCODATION:
                            sb.append(LATCH_TO_BASE256);
                            lengthBytePosition = sb.length();
                            sb.append('\000');
                            break;
                        case C40_ENCODATION:
                            sb.append(LATCH_TO_C40);
                            break;
                        case X12_ENCODATION:
                            sb.append(LATCH_TO_ANSIX12);
                            break;
                        case TEXT_ENCODATION:
                            sb.append(LATCH_TO_TEXT);
                            break;
                        case EDIFACT_ENCODATION:
                            sb.append(LATCH_TO_EDIFACT);
                            break;
                        default:
                            throw new IllegalStateException("Illegal mode: " + newMode);
                        }
                    } else if (isExtendedASCII(c)) {
                        sb.append(UPPER_SHIFT);
                        sb.append(c);
                        p++;
                    } else {
                        if (DEBUG) {
                            if (!isASCII7(c)) {
                                throw new IllegalArgumentException("Not an ASCII-7 character");
                            }
                        }
                        sb.append((char)(c + 1));
                        p++;
                    }
                    
                }
                break;
            case C40_ENCODATION:
                //step C
                c = msg.charAt(p);
                if (true) throw new IllegalStateException("NYI");
                break;
            case TEXT_ENCODATION:
                //step D
                c = msg.charAt(p);
                if (true) throw new IllegalStateException("NYI");
                break;
            case X12_ENCODATION:
                //step E
                c = msg.charAt(p);
                if (true) throw new IllegalStateException("NYI");
                break;
            case EDIFACT_ENCODATION:
                //step F
                c = msg.charAt(p);
                if (true) throw new IllegalStateException("NYI");
                break;
            case BASE256_ENCODATION:
                //step G
                int newMode = lookAheadTest(msg, p, encodingMode);
                if (newMode != encodingMode) {
                    encodingMode = newMode;
                } else {
                    c = msg.charAt(p);
                    if (true) throw new IllegalStateException("NYI");
                }
                break;
            }
        }
        
        return sb.toString();
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
        double[] charCounts;
        //step J
        if (currentMode == ASCII_ENCODATION) {
            charCounts = new double[] {0, 1, 1, 1, 1, 1.25f};
        } else {
            charCounts = new double[] {1, 2, 2, 2, 2, 2.25f};
            charCounts[currentMode] = 0;
        }
        
        int charsProcessed = 0;
        while (true) {
            //step K
            if ((startpos + charsProcessed) == msg.length() - 1) {
                int min = Integer.MAX_VALUE;
                byte[] mins = new byte[6];
                min = findMinimums(charCounts, min, mins);
                int minCount = getMinimumCount(mins);
                
                if (charCounts[ASCII_ENCODATION] == min) {
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
                charCounts[C40_ENCODATION] += 2.0 / 3.0;
            } else if (isExtendedASCII(c)) {
                charCounts[C40_ENCODATION] += 8.0 / 3.0;
            } else {
                charCounts[C40_ENCODATION] += 4.0 / 3.0;
            }
            
            //step N
            if (isNativeText(c)) {
                charCounts[TEXT_ENCODATION] += 2.0 / 3.0;
            } else if (isExtendedASCII(c)) {
                charCounts[TEXT_ENCODATION] += 8.0 / 3.0;
            } else {
                charCounts[TEXT_ENCODATION] += 4.0 / 3.0;
            }
            
            //step O
            if (isNativeX12(c)) {
                charCounts[X12_ENCODATION] += 2.0 / 3.0;
            } else if (isExtendedASCII(c)) {
                charCounts[X12_ENCODATION] += 13.0 / 3.0;
            } else {
                charCounts[X12_ENCODATION] += 10.0 / 3.0;
            }
            
            //step P
            if (isNativeEDIFACT(c)) {
                charCounts[EDIFACT_ENCODATION] += 3.0 / 4.0;
            } else if (isExtendedASCII(c)) {
                charCounts[EDIFACT_ENCODATION] += 17.0 / 4.0;
            } else {
                charCounts[EDIFACT_ENCODATION] += 13.0 / 4.0;
            }
            
            // step Q
            if (isSpecialB256(c)) {
                charCounts[BASE256_ENCODATION] += 4;
            } else {
                charCounts[BASE256_ENCODATION] += 1;
            }
            
            //step R
            if (charsProcessed >= 4) {
                int min = Integer.MAX_VALUE;
                byte[] mins = new byte[6];
                min = findMinimums(charCounts, min, mins);
                int minCount = getMinimumCount(mins);
                
                if (charCounts[ASCII_ENCODATION] + 1 == min + 1) {
                    return ASCII_ENCODATION;
                } else if (mins[BASE256_ENCODATION] + 1 <= charCounts[ASCII_ENCODATION]
                        || charCounts[BASE256_ENCODATION] + 1 == min + 1) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[EDIFACT_ENCODATION] + 1 > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[TEXT_ENCODATION] + 1 > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[X12_ENCODATION] + 1 > 0) {
                    return X12_ENCODATION;
                } else if (mins[C40_ENCODATION] + 1 < mins[ASCII_ENCODATION]
                        || mins[C40_ENCODATION] + 1 < mins[BASE256_ENCODATION]
                        || mins[C40_ENCODATION] + 1 < mins[EDIFACT_ENCODATION]
                        || mins[C40_ENCODATION] + 1 < mins[TEXT_ENCODATION]) {
                    if (mins[C40_ENCODATION] < mins[X12_ENCODATION]) {
                        return C40_ENCODATION;
                    } else if (mins[C40_ENCODATION] == mins[X12_ENCODATION]) {
                        int p = startpos + charsProcessed + 1;
                        while (p < msg.length()) {
                            char tc = msg.charAt(p);
                            if (isX12TermSep(tc)) {
                                return X12_ENCODATION;
                            } else if (!isNativeX12(tc)) {
                                break;
                            }
                        }
                        return C40_ENCODATION;
                    }
                }
            }
        }
    }

    private static int findMinimums(double[] charCounts, int min, byte[] mins) {
        Arrays.fill(mins, (byte)0);
        for (int i = 0; i < 6; i++) {
            charCounts[i] = Math.ceil(charCounts[i]);
            int current = (int)charCounts[i];
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

    
    private static final boolean isExtendedASCII(char ch) {
        byte[] buf = new byte[128];
        for (int i = 0; i < 128; i++) {
            buf[i] = (byte)(i + 128);
        }
        try {
            String ext = new String(buf, DEFAULT_ASCII_ENCODING);
            return (ext.indexOf(ch) >= 0); 
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private static final boolean isASCII7(char ch) {
        return (ch >= 0 && ch <=127);
    }
    
    private static final boolean isNativeC40(char ch) {
        return isASCII7(ch);
    }
    
    private static final boolean isNativeText(char ch) {
        return isASCII7(ch);
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
