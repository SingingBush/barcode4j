/*
 * $Id: Code39Test.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * Test class for the Code39 implementation.
 * 
 * @author Jeremias Maerki
 */
public class Code39Test extends TestCase {

    public Code39Test(String name) {
        super(name);
    }

    public void testChecksum() throws Exception {
        assertEquals('L', Code39LogicImpl.calcChecksum("12345ABCDEZ/"));
        assertEquals('L', Code39LogicImpl.calcChecksum("12345abcdez/"));
        assertEquals('M', Code39LogicImpl.calcChecksum("494140"));
        assertEquals('P', Code39LogicImpl.calcChecksum("415339"));
    }
    
    public void testIllegalArguments() throws Exception {
        try {
            Code39 impl = new Code39();
            impl.generateBarcode(null, null);
            fail("Expected an NPE");
        } catch (NullPointerException npe) {
            assertNotNull("Error message is empty", npe.getMessage());
        }
    }
    
    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        Code39LogicImpl logic;
        String expected;
        
        try {
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        
        logic = new Code39LogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123ABC");
        expected = "<BC>"
            + "<SBG:start-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "W-1"
            + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:2>B1W1B2W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:3>B2W1B2W2B1W1B1W1B1</SBG>"
            + "W-1"
            + "<SBG:msg-char:A>B2W1B1W1B1W2B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:B>B1W1B2W1B1W2B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:C>B2W1B2W1B1W2B1W1B1</SBG>"
            + "W-1"
            + "<SBG:stop-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new Code39LogicImpl(ChecksumMode.CP_ADD);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123");
        expected = "<BC>"
            + "<SBG:start-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "W-1"
            + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:2>B1W1B2W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:3>B2W1B2W2B1W1B1W1B1</SBG>"
            + "W-1"
            + "<SBG:msg-char:6>B1W1B2W2B2W1B1W1B1</SBG>"
            + "W-1"
            + "<SBG:stop-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new Code39LogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "1236");
        expected = "<BC>"
            + "<SBG:start-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "W-1"
            + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:2>B1W1B2W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:3>B2W1B2W2B1W1B1W1B1</SBG>"
            + "W-1"
            + "<SBG:msg-char:6>B1W1B2W2B2W1B1W1B1</SBG>"
            + "W-1"
            + "<SBG:stop-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
        
        
        sb.setLength(0);
        logic = new Code39LogicImpl(ChecksumMode.CP_CHECK);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123F");
            fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

}