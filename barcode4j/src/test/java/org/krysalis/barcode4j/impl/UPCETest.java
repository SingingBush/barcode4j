/*
 * $Id: UPCETest.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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

import org.krysalis.barcode4j.ChecksumMode;

import junit.framework.TestCase;

/**
 * Test class for the UPC-E implementation.
 * 
 * @author Jeremias Maerki
 */
public class UPCETest extends TestCase {

    public UPCETest(String name) {
        super(name);
    }

    public void testIllegalArguments() throws Exception {
        try {
            UPCE impl = new UPCE();
            impl.generateBarcode(null, null);
            fail("Expected an NPE");
        } catch (NullPointerException npe) {
            assertNotNull("Error message is empty", npe.getMessage());
        }

        //Test invalid characters in message
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        //Test less than 12 characters
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        //Test more than 12 characters
        try {
            UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), 
                    "123456789012344567890");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    private static final String[][] COMPACTION_TESTS = {
        {"01278907", "012000007897"},
        {"01278916", "012100007896"},
        {"01278925", "012200007895"},
        {"01238935", "012300000895"},
        {"01248934", "012400000894"},
        {"01258933", "012500000893"},
        {"01268932", "012600000892"},
        {"01278931", "012700000891"},
        {"01288930", "012800000890"},
        {"01298939", "012900000899"},
        {"01291944", "012910000094"},
        {"01291155", "012911000055"},
        {"01291162", "012911000062"},
        {"01291179", "012911000079"},
        {"01291186", "012911000086"},
        {"00102546", "001020000056"},
        {"01234133", "012300000413"}};

    public void testMessageCompaction() throws Exception {
        for (int i = 0; i < COMPACTION_TESTS.length; i++) {
            assertEquals(
                COMPACTION_TESTS[i][1] + " must be compacted to " 
                    + COMPACTION_TESTS[i][0],
                COMPACTION_TESTS[i][0], 
                UPCELogicImpl.compactMessage(COMPACTION_TESTS[i][1]));
            String nocheck = COMPACTION_TESTS[i][1].substring(0, 11);
            assertEquals(
                nocheck + " must be compacted to " 
                    + COMPACTION_TESTS[i][0],
                COMPACTION_TESTS[i][0], 
                UPCELogicImpl.compactMessage(nocheck));
        }
        final String noUPCE = "01234567890";
        assertNull(UPCELogicImpl.compactMessage(noUPCE));
        assertNull(UPCELogicImpl.compactMessage(noUPCE + UPCEANLogicImpl.calcChecksum(noUPCE)));
        try {
            UPCELogicImpl.compactMessage("ajsgf");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        try {
            UPCELogicImpl.compactMessage("0000000000000000000000000");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }
    
    public void testMessageExpansion() throws Exception {
        for (int i = 0; i < COMPACTION_TESTS.length; i++) {
            assertEquals(
                COMPACTION_TESTS[i][0] + " must be expanded to " 
                    + COMPACTION_TESTS[i][1],
                COMPACTION_TESTS[i][1], 
                UPCELogicImpl.expandMessage(COMPACTION_TESTS[i][0]));
            String nocheck = COMPACTION_TESTS[i][0].substring(0, 7);
            assertEquals(
                nocheck + " must be expanded to " 
                    + COMPACTION_TESTS[i][1],
                COMPACTION_TESTS[i][1], 
                UPCELogicImpl.expandMessage(nocheck));
        }
    }

    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        UPCELogicImpl logic;
        String expected;
        
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "0425261");
        expected = "<BC>"
            + "<SBG:upc-ean-guard:null>B1W1B1</SBG>"
            + "<SBG:upc-ean-lead:0>"
            + "</SBG>"
            + "<SBG:upc-ean-group:425261>"
                + "<SBG:msg-char:4>W2B3W1B1</SBG>"
                + "<SBG:msg-char:2>W2B1W2B2</SBG>"
                + "<SBG:msg-char:5>W1B3W2B1</SBG>"
                + "<SBG:msg-char:2>W2B2W1B2</SBG>"
                + "<SBG:msg-char:6>W1B1W1B4</SBG>"
                + "<SBG:msg-char:1>W2B2W2B1</SBG>"
            + "</SBG>"
            + "<SBG:upc-ean-check:4>"
            + "</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1W1B1W1B1</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with check mode
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04252614");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
        //The same but with UPC-A message
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "042100005264");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
        //The same but with UPC-A message without checksum
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04210000526");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
    }

}