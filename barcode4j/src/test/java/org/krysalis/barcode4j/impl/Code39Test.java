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

import org.krysalis.barcode4j.ChecksumMode;

import junit.framework.TestCase;

/**
 * Test class for the Code39 implementation.
 * 
 * @author Jeremias Maerki
 * @version $Id: Code39Test.java,v 1.2 2004-09-04 20:25:55 jmaerki Exp $
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