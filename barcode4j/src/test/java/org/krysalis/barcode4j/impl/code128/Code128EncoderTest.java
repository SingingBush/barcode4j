/*
 * Copyright 2002-2004,2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code128;

import junit.framework.TestCase;

/**
 * Test class for the Code128 message encoder implementations.
 *
 * @version $Id: Code128EncoderTest.java,v 1.3 2009-02-18 16:09:03 jmaerki Exp $
 */
public class Code128EncoderTest extends TestCase {

    private static final String[] CHAR_NAMES =
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
            sb.append(visualizeChar((char) (idx + 32)));
        } else if ((idx >= 64) && (idx <= 95)) {
            sb.append(visualizeChar((char) (idx - 64)));
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
            sb.append(visualizeChar((char) (idx + 32)));
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
        String eff = encodeToDebug("12345", encoder);
        assertTrue("->C[12][34]->B5".equals(eff) || "->B1->C[23][45]".equals(eff));
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
        assertEquals("->BCode<FNC1>5->C[67][89]", encodeToDebug("Code\u00f156789", encoder));
        assertEquals("->BCode->C[56]<FNC1>[78]->B9", encodeToDebug("Code56\u00f1789", encoder));
    }

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
        String eff = encodeToDebug("37100\u00f13101000200", encoder);
        assertTrue(
            "->C[37][10]->B0<FNC1>->C[31][1][0][2][0]".equals(eff)
            || "->C[37][10]->B0->C<FNC1>[31][1][0][2][0]".equals(eff));
        eff = encodeToDebug("\u00f1020456789012341837100\u00f13101000200", encoder);
        assertTrue(
            "->C<FNC1>[2][4][56][78][90][12][34][18][37][10]->B0<FNC1>->C[31][1][0][2][0]".equals(eff)
            || "->C<FNC1>[2][4][56][78][90][12][34][18][37][10]->B0->C<FNC1>[31][1][0][2][0]".equals(eff));
        assertEquals(
                "->C<FNC1>[2][4][56][78][90][12][34][18][37][10]<FNC1>[31][1][0][2][0]",
                encodeToDebug("\u00f102045678901234183710\u00f13101000200", encoder));
    }

    public void testCodesets() throws Exception {
        Code128Encoder encoder;

        /*
         * Testing codeset A
         */
        encoder = new DefaultCode128Encoder(Code128Constants.CODESET_A);
        assertEquals("->AA*B*C*D", encodeToDebug("A*B*C*D", encoder));
        assertEquals("->A1234567890", encodeToDebug("1234567890", encoder));
        assertEquals("->AARTHUR<HT>DENT", encodeToDebug("ARTHUR\tDENT", encoder));
        try {
            encodeToDebug("ABCDEf", encoder);
            fail("Expected to fail on characters from Codeset B");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        try {
            encodeToDebug("f1234567890", encoder);
            fail("Expected to fail on characters from Codeset B");
        } catch (IllegalArgumentException iae) {
            //expected
        }

        /*
         * Testing codeset B
         */
        encoder = new DefaultCode128Encoder(Code128Constants.CODESET_B);
        assertEquals("->BA*B*C*D", encodeToDebug("A*B*C*D", encoder));
        assertEquals("->Ba*b*c*d", encodeToDebug("a*b*c*d", encoder));
        assertEquals("->B1234567890", encodeToDebug("1234567890", encoder));
        try {
            encodeToDebug("arthur\tDENT", encoder);
            fail("Expected to fail on characters from Codeset A");
        } catch (IllegalArgumentException iae) {
            //expected
        }

        /*
         * Testing codeset A + B
         */
        encoder = new DefaultCode128Encoder(
                Code128Constants.CODESET_A | Code128Constants.CODESET_B);
        assertEquals("->Barthur<SHIFT-A><HT>DENT", encodeToDebug("arthur\tDENT", encoder));
        assertEquals("->B1234567890", encodeToDebug("1234567890", encoder));

        /*
         * Testing codeset C
         */
        encoder = new DefaultCode128Encoder(Code128Constants.CODESET_C);
        try {
            encodeToDebug("7483927d584f301g83755", encoder);
            fail("Expected IllegalArgumentException for the Codeset B characters");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        assertEquals("->C[74][83][92][75][84][30][18][37][55]",
                encodeToDebug("748392758430183755", encoder));
        try {
            encodeToDebug("74839275843018375", encoder);
            fail("Expected IllegalArgumentException for the odd number of digits");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        encoder = new DefaultCode128Encoder(
                Code128Constants.CODESET_A | Code128Constants.CODESET_C);
        assertEquals("->A7->C[48][39][27][58][43][1][83][75]",
                encodeToDebug("74839275843018375", encoder));
        encoder = new DefaultCode128Encoder(
                Code128Constants.CODESET_B | Code128Constants.CODESET_C);
        assertEquals("->B7->C[48][39][27][58][43][1][83][75]",
                encodeToDebug("74839275843018375", encoder));
    }
}