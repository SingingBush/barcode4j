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
package org.krysalis.barcode4j.impl.upcean;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for general UPC/EAN functionality.
 *
 * @author Jeremias Maerki
 * @version $Id: UPCEANTest.java,v 1.1 2004-09-12 17:57:54 jmaerki Exp $
 */
public class UPCEANTest {

    @Test
    void testRemoveSupplemental() throws Exception {
        assertEquals("1234", UPCEANLogicImpl.removeSupplemental("1234"));
        assertEquals("1234", UPCEANLogicImpl.removeSupplemental("1234+20"));
    }

    @Test
    void testRetrieveSupplemental() throws Exception {
        assertNull(UPCEANLogicImpl.retrieveSupplemental("1234"));
        assertEquals("20", UPCEANLogicImpl.retrieveSupplemental("1234+20"));
    }

    @Test
    void testGetSupplementalLength() throws Exception {
        assertEquals(0, UPCEANLogicImpl.getSupplementalLength("1234"));
        assertEquals(2, UPCEANLogicImpl.getSupplementalLength("1234+12"));
        assertEquals(5, UPCEANLogicImpl.getSupplementalLength("1234+12345"));

        assertThrows(IllegalArgumentException.class, () -> UPCEANLogicImpl.getSupplementalLength("1234+123"));
    }

    @Test
    void testSupplemental2() throws Exception {
        StringBuffer sb = new StringBuffer();

        final EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
        logic.drawSupplemental(new MockClassicBarcodeLogicHandler(sb), "34");

        final String expected = "<SBG:upc-ean-supp:34>"
            + "<SBG:upc-ean-guard:null>B1W1B2</SBG>"
            + "<SBG:msg-char:3>W1B1W4B1</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1</SBG>"
            + "<SBG:msg-char:4>W1B1W3B2</SBG>"
            + "</SBG>";
        //System.out.println("expected: " + expected);
        //System.out.println("actual:   " + sb.toString());
        assertEquals(expected, sb.toString());
    }

    @Test
    void testSupplemental5() {
        final StringBuffer sb = new StringBuffer();

        final EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);
        logic.drawSupplemental(new MockClassicBarcodeLogicHandler(sb), "51234");

        final String expected = "<SBG:upc-ean-supp:51234>"
            + "<SBG:upc-ean-guard:null>B1W1B2</SBG>"
            + "<SBG:msg-char:5>W1B2W3B1</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1</SBG>"
            + "<SBG:msg-char:1>W2B2W2B1</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1</SBG>"
            + "<SBG:msg-char:2>W2B2W1B2</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1</SBG>"
            + "<SBG:msg-char:3>W1B4W1B1</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1</SBG>"
            + "<SBG:msg-char:4>W2B3W1B1</SBG>"
            + "</SBG>";
        //System.out.println("expected: " + expected);
        //System.out.println("actual:   " + sb.toString());
        assertEquals(expected, sb.toString());
    }

}
