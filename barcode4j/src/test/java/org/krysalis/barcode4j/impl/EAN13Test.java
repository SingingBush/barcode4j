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
 * Test class for the EAN-13 implementation.
 * 
 * @author Jeremias Maerki
 * @version $Id: EAN13Test.java,v 1.2 2004-09-04 20:25:55 jmaerki Exp $
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
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123���2");
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