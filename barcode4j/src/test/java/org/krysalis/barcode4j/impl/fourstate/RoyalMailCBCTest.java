/*
 * Copyright 2006-2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.fourstate;

import junit.framework.TestCase;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;

/**
 * Testcase for the Royal Mail CBC barcode.
 */
public class RoyalMailCBCTest extends TestCase {

    public void testChecksum() throws Exception {
        String msg = "SN34RD1A";
        RoyalMailCBCLogicImpl logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_AUTO);
        char check = logic.calcChecksum(msg);
        assertEquals('K', check);
    }
    
    public void testChecksumHandling() throws Exception {
        String msg = "SN34RD1A";
        RoyalMailCBCLogicImpl logic;
        String res;
        
        logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_AUTO);
        res = logic.handleChecksum(msg);
        assertEquals("SN34RD1AK", res);
        
        logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_ADD);
        res = logic.handleChecksum(msg);
        assertEquals("SN34RD1AK", res);
        
        logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_CHECK);
        res = logic.handleChecksum(msg + "K");
        assertEquals("SN34RD1AK", res);

        logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_CHECK);
        try {
            res = logic.handleChecksum(msg + "L");
            fail("IllegalArgumentException expected on invalid checksum");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        
        logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_IGNORE);
        res = logic.handleChecksum(msg);
        assertEquals("SN34RD1A", res);
    }
    
    public void testLogic() throws Exception {
        RoyalMailCBCLogicImpl logic = new RoyalMailCBCLogicImpl(ChecksumMode.CP_AUTO);
        StringBuffer sb = new StringBuffer();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "B31HQ1A");
        String expected = "<BC>"
            + "<SBG:msg-char:(>B1</SBG>"
            + "<SBG:msg-char:B>B2B3B0B1</SBG>"
            + "<SBG:msg-char:3>B2B0B1B3</SBG>"
            + "<SBG:msg-char:1>B0B2B1B3</SBG>"
            + "<SBG:msg-char:H>B2B3B1B0</SBG>"
            + "<SBG:msg-char:Q>B1B2B3B0</SBG>"
            + "<SBG:msg-char:1>B0B2B1B3</SBG>"
            + "<SBG:msg-char:A>B2B1B2B1</SBG>"
            + "<SBG:msg-char:F>B2B1B1B2</SBG>"
            + "<SBG:msg-char:)>B3</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
    }
    
}