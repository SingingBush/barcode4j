/*
 * $Id: Code128EncoderTest.java,v 1.2 2004-04-27 18:28:18 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.impl;

import junit.framework.TestCase;

/**
 * Test class for the Code128 message encoder implementations.
 * 
 * @author Jeremias Maerki
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
        return visualize(encoder.encode(msg));
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
        encodeToDebug("\u00f1020456789012341837100\u00f13101000200", encoder);
    }
}