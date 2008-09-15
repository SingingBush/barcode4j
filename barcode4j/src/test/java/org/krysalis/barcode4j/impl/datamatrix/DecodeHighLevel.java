/*
 * Copyright 2007 Jeremias Maerki
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

/* $Id: DecodeHighLevel.java,v 1.2 2008-09-15 07:10:31 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Decodes DataMatrix codewords and logs their meaning.
 */
public class DecodeHighLevel {

    public static void decode(String codewords) {
        int idx = 0;
        int len = codewords.length();
        //if (len > 10) len = 10;
        while (idx < len) {
            char ch = codewords.charAt(idx);
            switch (ch) {
            case DataMatrixConstants.LATCH_TO_C40:
                System.out.println("<latch to C40>");
                continue;
            case DataMatrixConstants.LATCH_TO_BASE256:
                System.out.println("<latch to Base 256>");
                idx = decodeBase256(codewords, idx + 1);
                continue;
            case DataMatrixConstants.LATCH_TO_TEXT:
                System.out.println("<latch to Text>");
                idx = decodeText(codewords, idx + 1);
                continue;
            case DataMatrixConstants.PAD:
                System.out.println("<pad>...");
                return;
            default:
                if (ch >= 1 && ch <= 128) {
                    System.out.println("char: " + (char)(ch - 1) + " - " + (int)(ch - 1)
                            + " (" + (int)ch + ")");
                } else if (ch >= 130 && ch <= 229) {
                    int num = ch - 130;
                    System.out.println("number: " + num + " (" + (int)ch + ")");
                } else {
                    System.out.println("unknown: " + ch + " - " + (int)ch);
                }
            }

            idx++;
        }
    }

    private static int decodeBase256(String codewords, int start) {
        int len = codewords.length();
        int idx = start;
        int sectionLen;
        char d1 = codewords.charAt(idx);
        d1 = unrandomize255State(d1, ++idx);
        if (d1 == 0) {
            System.out.println("<Base 256 for the remainder of the symbol>");
            sectionLen = len - start - 1;
        } else if (d1 >= 1 && d1 <= 249) {
            sectionLen = d1;
        } else {
            char d2 = codewords.charAt(idx);
            d2 = unrandomize255State(d2, idx + 1);
            sectionLen = 250 * (d1 - 249) + d2;
            idx++;
        }
        for (int i = 0; i < sectionLen; i++) {
            char cw = codewords.charAt(idx);
            char urcw = unrandomize255State(cw, ++idx);
            System.out.println("byte: " + urcw + " - " + (int)urcw
                    + " (0x" + Integer.toHexString(urcw) + ")"
                    + ", org: " + cw + " - " + (int)cw
                    + " (0x" + Integer.toHexString(cw) + ")");
        }
        return idx;
    }

    private static int decodeText(String codewords, int start) {
        int len = codewords.length();
        int idx = start;

        StringBuffer sb = new StringBuffer();
        int mode = 0;

        while (idx < len) {
            int ch = codewords.charAt(idx);
            if (ch == DataMatrixConstants.C40_UNLATCH) {
                System.out.println("<unlatch> (" + ch + ")");
                idx++;
                break;
            }
            int ch1 = ch;
            int pair = ch * 256;
            pair += codewords.charAt(++idx);
            System.out.print("pair: " + pair + " (" + ch1 + "/" + ch + ")");

            pair--;
            int c1 = pair / 1600;
            pair -= (c1 * 1600);
            int c2 = pair / 40;
            pair -= (c2 * 40);
            int c3 = pair;
            System.out.println(" --> " + c1 + " " + c2 + " " + c3);
            sb.append((char)c1).append((char)c2).append((char)c3);


            idx++;
        }

        return idx;
    }

    private static char unrandomize255State(char ch, int codewordPosition) {
        int pseudoRandom = ((149 * codewordPosition) % 255) + 1;
        int tempVariable = ch - pseudoRandom;
        if (tempVariable >= 0) {
            return (char)tempVariable;
        } else {
            return (char)(tempVariable + 256);
        }
    }

}
