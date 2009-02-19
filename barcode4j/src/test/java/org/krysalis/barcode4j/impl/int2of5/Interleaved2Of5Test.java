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
package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5LogicImpl;

import junit.framework.TestCase;

/**
 * Test class for the Interleaved 2 of 5 implementation.
 * 
 * @author Jeremias Maerki
 * @version $Id: Interleaved2Of5Test.java,v 1.1 2009-02-19 10:14:54 jmaerki Exp $
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
        
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_AUTO, false);
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
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_ADD, false);
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
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_CHECK, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123456700");
        //Variable expected stays the same for this test!!!!!
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        
        sb.setLength(0);
        logic = new Interleaved2Of5LogicImpl(ChecksumMode.CP_CHECK, false);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123456706");
            fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }
    
}