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

import junit.framework.TestCase;

/**
 * Test class for the Code128 message encoder implementations.
 * 
 * @author Jeremias Maerki
 * @version $Id: Code128EncoderTest.java,v 1.4 2004-09-04 20:25:55 jmaerki Exp $
 */
public class Code128EncoderTest extends TestCase {

    public static final String[] CHAR_NAMES =
        {"NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL", "BS", "HT",
         "LF", "VT", "FF", "CR", "SO", "SI", "DLE", "DC1", "DC2", "DC3",
         "DC4", "NAK", "SYN", "ETB", "CAN", "EM", "SUB", "ESC", "FS", "GS",
         "RS", "US"}; //ASCII 0-31

    public Code128EncoderTest(String name) {
        super(name);
    }

    private String visualizeChar(char c) {
        if (c < 32) {
            return "<" + CHAR_NAMES[c] + ">";
        } else if (c == 127) {
            return "<DEL>";
        } else {
            return new Character(c).toString();
        }
    }

    private void visualizeCodesetA(StringBuffer sb, int idx) {
        if (idx < 64) {
            sb.append(visualizeChar((char)(idx + 32)));
        } else if ((idx >= 64) && (idx <= 95)) {
            sb.append(visualizeChar((char)(idx - 64)));
        } else if (idx == 96) {
            sb.append("<FNC3>");
        } else if (idx == 97) {
            sb.append("<FNC2>");
        } else if (idx == 101) {
            sb.append("<FNC4>");
        } else if (idx == 102) {
            sb.append("<FNC1>");
        }
    }

    private void visualizeCodesetB(StringBuffer sb, int idx) {
        if (idx <= 95) {
            sb.append(visualizeChar((char)(idx + 32)));
        } else if (idx == 96) {
            sb.append("<FNC3>");
        } else if (idx == 97) {
            sb.append("<FNC2>");
        } else if (idx == 100) {
            sb.append("<FNC4>");
        } else if (idx == 102) {
            sb.append("<FNC1>");
        }
    }

    private void visualizeCodesetC(StringBuffer sb, int idx) {
        if (idx < 100) {
            sb.append("[");
            sb.append(Integer.toString(idx));
            sb.append("]");
        } else if (idx == 102) {
            sb.append("<FNC1>");
        }
    }

    private String visualize(int[] encodedMsg) {
        StringBuffer sb = new StringBuffer();
        char codeset;
        if (encodedMsg[0] == 103) {
            codeset = 'A';
        } else if (encodedMsg[0] == 104) {
            codeset = 'B';
        } else if (encodedMsg[0] == 105) {
            codeset = 'C';
        } else {
            throw new RuntimeException("Invalid start character");
        }
        sb.append("->" + codeset);
        int pos = 1;
        while (pos < encodedMsg.length) {
            int idx = encodedMsg[pos];
            if (codeset == 'A') {
                if (idx == 98) {
                    sb.append("<SHIFT-B>");
                    pos++;
                    visualizeCodesetB(sb, encodedMsg[pos]);
                } else if (idx == 99) {
                    codeset = 'C';
                    sb.append("->" + codeset);
                } else if (idx == 100) {
                    codeset = 'B';
                    sb.append("->" + codeset);
                } else {
                    visualizeCodesetA(sb, idx);
                }
            } else if (codeset == 'B') {
                if (idx == 98) {
                    sb.append("<SHIFT-A>");
                    pos++;
                    visualizeCodesetA(sb, encodedMsg[pos]);
                } else if (idx == 99) {
                    codeset = 'C';
                    sb.append("->" + codeset);
                } else if (idx == 101) {
                    codeset = 'A';
                    sb.append("->" + codeset);
                } else {
                    visualizeCodesetB(sb, idx);
                }
            } else if (codeset == 'C') {
                if (idx == 100) {
                    codeset = 'B';
                    sb.append("->" + codeset);
                } else if (idx == 101) {
                    codeset = 'A';
                    sb.append("->" + codeset);
                } else {
                    visualizeCodesetC(sb, idx);
                }
            }
            pos++;
        }
        return sb.toString();
    }

    private String encodeToDebug(String msg, Code128Encoder encoder) {
        String s = visualize(encoder.encode(msg));
        //System.out.println(s); 
        return s;
    }
    
    private void testEncoderSpecialChars(Code128Encoder encoder) {
        assertEquals("->BCode<SHIFT-A><HT>128", encodeToDebug("Code\t128", encoder));
        assertEquals("->BCode->A<HT><HT>128", encodeToDebug("Code\t\t128", encoder));
        assertEquals("->C<FNC1>[12][34][56]", encodeToDebug("\u00f1123456", encoder));
        assertEquals("->B<FNC2>->C[12][34][56]", encodeToDebug("\u00f2123456", encoder));
        assertEquals("->Bbefore<FNC3>after", encodeToDebug("before\u00f3after", encoder));
        assertEquals("->Bbefore<FNC4>after<DEL>", 
                encodeToDebug("before\u00f4after\u007f", encoder));
    }

    private void testEncoder(Code128Encoder encoder) throws Exception {
        assertEquals("->B1", encodeToDebug("1", encoder));
        assertEquals("->C[12]", encodeToDebug("12", encoder));
        assertEquals("->B123", encodeToDebug("123", encoder));
        assertEquals("->C[12][34]", encodeToDebug("1234", encoder));
        assertEquals("->C[12][34]->B5", encodeToDebug("12345", encoder));
        assertEquals("->C[12][34][56]", encodeToDebug("123456", encoder));

        assertEquals("->B1Code", encodeToDebug("1Code", encoder));
        assertEquals("->B12Code", encodeToDebug("12Code", encoder));
        assertEquals("->B123Code", encodeToDebug("123Code", encoder));
        assertEquals("->C[12][34]->BCode", encodeToDebug("1234Code", encoder));
        assertEquals("->C[12][34]->B5Code", encodeToDebug("12345Code", encoder));
        assertEquals("->C[12][34][56]->BCode", encodeToDebug("123456Code", encoder));

        assertEquals("->BCode128", encodeToDebug("Code128", encoder));
        assertEquals("->BCode128", encodeToDebug("Code128", encoder));
        assertEquals("->BCode->C[56][78]->Ba", encodeToDebug("Code5678a", encoder));
        String res = encodeToDebug("Code56789a", encoder);
        assertTrue(res.equals("->BCode->C[56][78]->B9a") 
            || res.equals("->BCode5->C[67][89]->Ba")
            || res.equals("->BCode5->C[67][89]<SHIFT-B>a"));
        assertEquals("->BCode->C[56][78][90]->Bab", encodeToDebug("Code567890ab", encoder));
        assertEquals("->BCode5->C[67][89]", encodeToDebug("Code56789", encoder));
    }

    /*
    public void testNaiveEncoder() throws Exception {
        Code128Encoder encoder = new NaiveCode128Encoder();
        testEncoder(encoder);
    }*/

    public void testDefaultEncoder() throws Exception {
        Code128Encoder encoder = new DefaultCode128Encoder();
        testEncoder(encoder);
        testEncoderSpecialChars(encoder);
        
        try {
            encodeToDebug("before\u00f5after", encoder);
            fail("Expected IllegalArgumentException because of illegal char 0xF5");
        } catch (IllegalArgumentException iae) {
            //expected
        }
    }
    
    public void testBug942246() throws Exception {
        Code128Encoder encoder = new DefaultCode128Encoder();
        assertEquals(
            "->C[37][10]->B0<FNC1>->C[31][1][0][2][0]",
            encodeToDebug("37100\u00f13101000200", encoder));
        assertEquals(
            "->C<FNC1>[2][4][56][78][90][12][34][18][37][10]->B0<FNC1>->C[31][1][0][2][0]",
            encodeToDebug("\u00f1020456789012341837100\u00f13101000200", encoder));
        assertEquals(
            "->C<FNC1>[2][4][56][78][90][12][34][18][37][10]<FNC1>[31][1][0][2][0]",
            encodeToDebug("\u00f102045678901234183710\u00f13101000200", encoder));
    }
}