/*
 * $Id: Interleaved2Of5Test.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * Test class for the Interleaved 2 of 5 implementation.
 * 
 * @author Jeremias Maerki
 */
public class Interleaved2Of5Test extends TestCase {

    public Interleaved2Of5Test(String name) {
        super(name);
    }

    public void testChecksum() throws Exception {
        //Check with default specification (ITF-14, EAN-14, SSC-14, DUN14 and USPS)
        assertEquals('5', Interleaved2Of5LogicImpl.calcChecksum("123456789"));

        assertTrue(Interleaved2Of5LogicImpl.validateChecksum("1234567895"));
        assertFalse(Interleaved2Of5LogicImpl.validateChecksum("1234567896"));

        //Check with German Post Identcode and Leitcode specification
        assertEquals('6', Interleaved2Of5LogicImpl.calcChecksum("12345678901", 4, 9));
    }
    
    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        Interleaved2Of5LogicImpl logic;
        String expected;
        
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "12345670");
        expected = "<BC><SBG:start-char:null>B1W1B1W1</SBG>"
                                  + "<SBG:msg-char:12>B2W1B1W2B1W1B1W1B2W2</SBG>"
                                  + "<SBG:msg-char:34>B2W1B2W1B1W2B1W1B1W2</SBG>"
                                  + "<SBG:msg-char:56>B2W1B1W2B2W2B1W1B1W1</SBG>"
                                  + "<SBG:msg-char:70>B1W1B1W1B1W2B2W2B2W1</SBG>"
                                  + "<SBG:stop-char:null>B2W1B1</SBG>"
                                  + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());


        sb.setLength(0);
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_ADD);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "12345670");
        expected = "<BC><SBG:start-char:null>B1W1B1W1</SBG>"
                                  + "<SBG:msg-char:01>B1W2B1W1B2W1B2W1B1W2</SBG>"
                                  + "<SBG:msg-char:23>B1W2B2W2B1W1B1W1B2W1</SBG>"
                                  + "<SBG:msg-char:45>B1W2B1W1B2W2B1W1B2W1</SBG>"
                                  + "<SBG:msg-char:67>B1W1B2W1B2W1B1W2B1W2</SBG>"
                                  + "<SBG:msg-char:00>B1W1B1W1B2W2B2W2B1W1</SBG>"
                                  + "<SBG:stop-char:null>B2W1B1</SBG>"
                                  + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());


        sb.setLength(0);
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123456700");
        //Variable expected stays the same for this test!!!!!
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        
        sb.setLength(0);
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_CHECK);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123456706");
            fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }
    
}