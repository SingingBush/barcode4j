/*
 * $Id: EAN13Test.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * Test class for the EAN-13 implementation.
 * 
 * @author Jeremias Maerki
 */
public class EAN13Test extends TestCase {

    public EAN13Test(String name) {
        super(name);
    }

    public void testIllegalArguments() throws Exception {
        try {
            EAN13 impl = new EAN13();
            impl.generateBarcode(null, null);
            fail("Expected an NPE");
        } catch (NullPointerException npe) {
            assertNotNull("Error message is empty", npe.getMessage());
        }

        //Test invalid characters in message
        try {
            EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        //Test less than 13 characters
        try {
            EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        //Test more than 13 characters
        try {
            EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123456789012344567890");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        EAN13LogicImpl logic;
        String expected;
        
        logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "200123456789");
        expected = "<BC>"
            + "<SBG:upc-ean-guard:null>B1W1B1</SBG>"
            + "<SBG:upc-ean-group:2:001234>"
                + "<SBG:msg-char:0>W3B2W1B1</SBG>"
                + "<SBG:msg-char:0>W3B2W1B1</SBG>"
                + "<SBG:msg-char:1>W1B2W2B2</SBG>"
                + "<SBG:msg-char:2>W2B2W1B2</SBG>"
                + "<SBG:msg-char:3>W1B4W1B1</SBG>"
                + "<SBG:msg-char:4>W2B3W1B1</SBG>"
            + "</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1W1B1W1</SBG>"
            + "<SBG:upc-ean-group:567893>"
                + "<SBG:msg-char:5>B1W2B3W1</SBG>"
                + "<SBG:msg-char:6>B1W1B1W4</SBG>"
                + "<SBG:msg-char:7>B1W3B1W2</SBG>"
                + "<SBG:msg-char:8>B1W2B1W3</SBG>"
                + "<SBG:msg-char:9>B3W1B1W2</SBG>"
                + "<SBG:upc-ean-check:3>"
                    + "<SBG:msg-char:3>B1W4B1W1</SBG>"
                + "</SBG>"
            + "</SBG>"
            + "<SBG:upc-ean-guard:null>B1W1B1</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with check mode
        sb.setLength(0);
        logic = new EAN13LogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "2001234567893");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
    }

}