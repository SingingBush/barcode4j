/*
 * Copyright 2005 Dietmar Bürkle.
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
package org.krysalis.barcode4j.applet;

import org.krysalis.barcode4j.impl.code128.EAN128Bean;

/**
 * Utility to parse escape characters. 
 * 
 * @author buerkle
 */
public class EscapeHandler {

    private static char decodeChar(String msg, int start, int end, int base) {
        if (end > msg.length()) return '?';
        char c;
        int val = 0;
        for (int i = start; i < end; i++) {
            val *= base;
            c = msg.charAt(i);
            switch (c) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7':
                    val += c - '0';
                    break;
                case '8': case '9':
                    if (base == 8) return '?';
                    val += c - '0';
                    break;
                case 'a': case 'b': case 'c':case 'd': case 'e': case 'f':
                    if (base == 8 || base == 10) return '?';
                    val += 10 + c - 'a';
                    break;
                case 'A': case 'B': case 'C':case 'D': case 'E': case 'F':
                    if (base == 8 || base == 10) return '?';
                    val += 10 + c - 'A';
                    break;
                default:
                    return '?';
            }
        }
        return (char)val;
    }

    public static String parseEscapes(String msg) {
        if (msg == null || msg.indexOf('\\') < 0) 
            return msg;
        int len = msg.length();
        StringBuffer ret = new StringBuffer(len);
        char c;

        for (int i = 0; i < len; ) {
            c = msg.charAt(i++);
            if (c != '\\' || i >= len) { //if no Escape, or if Escape is the last char! 
                ret.append(c);
            } else {
                c = msg.charAt(i++);
                switch (c) {
                    case '\\': ret.append('\\'); break;
                    case '0': ret.append('\0'); break;
                    case 'b': ret.append('\b'); break;
                    case 'c': 
                        if (i >= len || (c = msg.charAt(i++)) != 'd') { 
                            ret.append("\\c");  
                        } else { 
                            ret.append(EAN128Bean.DEFAULT_CHECK_DIGIT_MARKER);
                        }
                        break;
                    case 'f': ret.append('\f'); break;
                    case 'g': 
                        if (i >= len || (c = msg.charAt(i++)) != 's') { 
                            ret.append("\\g");  
                        } else { 
                            ret.append(EAN128Bean.DEFAULT_GROUP_SEPARATOR);
                        }
                        break;
                    case 'n': ret.append('\n'); break;
                    case 'r': ret.append('\r'); break;
                    case 't': ret.append('\t'); break;
                    case 'o': ret.append(decodeChar(msg, i, i += 3, 8)); break;
                    case 'd': ret.append(decodeChar(msg, i, i += 3, 10)); break;
                    case 'x': ret.append(decodeChar(msg, i, i += 2, 16)); break;
                    case 'u': ret.append(decodeChar(msg, i, i += 4, 16)); break;
                    default: ret.append('\\').append(c); break;
                }
            } 
        }
        return ret.toString();
    }


}
