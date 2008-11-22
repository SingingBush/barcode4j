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

import junit.framework.TestCase;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.codabar.Codabar;
import org.krysalis.barcode4j.impl.codabar.CodabarLogicImpl;

/**
 * Test class for the Codabar implementation.
 *
 * @author Jeremias Maerki
 * @version $Id: CodabarTest.java,v 1.5 2008-11-22 09:57:29 jmaerki Exp $
 */
public class CodabarTest extends TestCase {

    public CodabarTest(String name) {
        super(name);
    }

    public void testIllegalArguments() throws Exception {
        try {
            Codabar impl = new Codabar();
            impl.generateBarcode(null, null);
            fail("Expected an NPE");
        } catch (NullPointerException npe) {
            assertNotNull("Error message is empty", npe.getMessage());
        }

        try {
            CodabarLogicImpl logic = new CodabarLogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

    }

    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        CodabarLogicImpl logic;
        String expected;

        logic = new CodabarLogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "d123e");
        expected = "<BC>"
            + "<SBG:msg-char:d>B1W1B1W2B2W2B1</SBG>"
            + "W1"
            + "<SBG:msg-char:1>B1W1B1W1B2W2B1</SBG>"
            + "W1"
            + "<SBG:msg-char:2>B1W1B1W2B1W1B2</SBG>"
            + "W1"
            + "<SBG:msg-char:3>B2W2B1W1B1W1B1</SBG>"
            + "W1"
            + "<SBG:msg-char:e>B1W1B1W2B2W2B1</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //Again with upper-case characters
        sb.setLength(0);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "D123E");
        assertEquals(expected, sb.toString());

        /**@todo Implement start/stop character checking */
    }

    public void testStartStopHandling() throws Exception {
        StringBuffer sb = new StringBuffer();
        CodabarLogicImpl logic;
        String expected;

        //With start/stop in the HR part
        logic = new CodabarLogicImpl(ChecksumMode.CP_AUTO, true);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "*12*");
        expected = "<BC:*12*>"
            + "<SBG:msg-char:*>B1W1B1W2B1W2B2</SBG>"
            + "W1"
            + "<SBG:msg-char:1>B1W1B1W1B2W2B1</SBG>"
            + "W1"
            + "<SBG:msg-char:2>B1W1B1W2B1W1B2</SBG>"
            + "W1"
            + "<SBG:msg-char:*>B1W1B1W2B1W2B2</SBG>"
            + "</BC>";
        assertEquals(expected, sb.toString());

        //Without start/stop in the HR part
        sb.setLength(0);
        logic = new CodabarLogicImpl(ChecksumMode.CP_AUTO, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "*12*");
        expected = "<BC:12>"
            + "<SBG:msg-char:*>B1W1B1W2B1W2B2</SBG>"
            + "W1"
            + "<SBG:msg-char:1>B1W1B1W1B2W2B1</SBG>"
            + "W1"
            + "<SBG:msg-char:2>B1W1B1W2B1W1B2</SBG>"
            + "W1"
            + "<SBG:msg-char:*>B1W1B1W2B1W2B2</SBG>"
            + "</BC>";
        assertEquals(expected, sb.toString());
    }

}