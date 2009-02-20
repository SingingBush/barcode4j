/*
 * Copyright 2002-2004,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code39;

import junit.framework.TestCase;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.NullClassicBarcodeLogicHandler;

/**
 * Test class for the Code39 implementation.
 *
 * @author Jeremias Maerki
 * @version $Id: Code39Test.java,v 1.3 2009-02-20 09:33:43 jmaerki Exp $
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

    public void testIllegalChars() throws Exception {
        Code39LogicImpl logic;

        try {
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //In standard charset, the * is legal in the message if it is used as start/stop chars
        try {
            StringBuffer sb = new StringBuffer();
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "*1*");
            String expected = "<BC:1>"
                + "<SBG:start-char:*>"
                + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
                + "</SBG>"
                + "W-1"
                + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
                + "W-1"
                + "<SBG:stop-char:*>"
                + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
                + "</SBG>"
                + "</BC>";
            //System.out.println(expected);
            //System.out.println(sb.toString());
            assertEquals(expected, sb.toString());
        } catch (IllegalArgumentException iae) {
            fail("Must not complain about '*' if used properly in stadard mode!");
        }

        //In standard charset, the * is illegal inside the message
        try {
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "*1");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        try {
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "1*");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //In standard charset, the * is illegal inside the message
        try {
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "*1*2*");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //...but with extended charset enabled, this is valid
        try {
            StringBuffer sb = new StringBuffer();
            logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, true);
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "*1*");
            String expected = "<BC:*1*>"
                + "<SBG:start-char:*>"
                + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
                + "</SBG>"
                + "W-1"
                + "<SBG:msg-char:/>B1W2B1W2B1W1B1W2B1</SBG>"
                + "W-1"
                + "<SBG:msg-char:J>B1W1B1W1B2W2B2W1B1</SBG>"
                + "W-1"
                + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
                + "W-1"
                + "<SBG:msg-char:/>B1W2B1W2B1W1B1W2B1</SBG>"
                + "W-1"
                + "<SBG:msg-char:J>B1W1B1W1B2W2B2W1B1</SBG>"
                + "W-1"
                + "<SBG:stop-char:*>"
                + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
                + "</SBG>"
                + "</BC>";
            //System.out.println(expected);
            //System.out.println(sb.toString());
            assertEquals(expected, sb.toString());
        } catch (IllegalArgumentException iae) {
            fail("Must not complain about '*' with extended charset enabled!");
        }
    }

    public void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        Code39LogicImpl logic;
        String expected;

        logic = new Code39LogicImpl(ChecksumMode.CP_AUTO, false, false, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "123ABC");
        expected = "<BC:123ABC>"
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
        logic = new Code39LogicImpl(ChecksumMode.CP_ADD, false, false, false);
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
        logic = new Code39LogicImpl(ChecksumMode.CP_CHECK, false, false, false);
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
        logic = new Code39LogicImpl(ChecksumMode.CP_CHECK, false, false, false);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123F");
            fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    public void testDisplayStartStop() throws Exception {
        StringBuffer sb = new StringBuffer();
        Code39LogicImpl logic;
        String expected;

        logic = new Code39LogicImpl(ChecksumMode.CP_IGNORE, true, false, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "123");
        expected = "<BC:*123*>"
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
            + "<SBG:stop-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
    }

    public void testDisplayChecksum() throws Exception {
        StringBuffer sb = new StringBuffer();
        Code39LogicImpl logic;
        String expected;

        logic = new Code39LogicImpl(ChecksumMode.CP_ADD, false, true, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, true), "123");
        expected = "<BC:1236>"
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
    }

    public void testExtendedCharSet() throws Exception {
        Code39LogicImpl logic = new Code39LogicImpl(ChecksumMode.CP_IGNORE, false, false, true);
        StringBuffer sb = new StringBuffer();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "a1$A");
        String expected = "<BC>"
            + "<SBG:start-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "W-1"
            + "<SBG:msg-char:+>B1W2B1W1B1W2B1W2B1</SBG>"
            + "W-1"
            + "<SBG:msg-char:A>B2W1B1W1B1W2B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:1>B2W1B1W2B1W1B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:/>B1W2B1W2B1W1B1W2B1</SBG>"
            + "W-1"
            + "<SBG:msg-char:D>B1W1B1W1B2W2B1W1B2</SBG>"
            + "W-1"
            + "<SBG:msg-char:A>B2W1B1W1B1W2B1W1B2</SBG>"
            + "W-1"
            + "<SBG:stop-char:*>"
            + "<SBG:msg-char:*>B1W2B1W1B2W1B2W1B1</SBG>"
            + "</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());
    }

    public void testDimension() throws Exception {
        Code39Bean bean = new Code39Bean();
        bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        bean.setModuleWidth(0.29);
        bean.setIntercharGapWidth(bean.getModuleWidth());
        bean.setHeight(14);
        BarcodeDimension dim;

        dim = bean.calcDimensions("*test*");
        assertEquals(24.94, dim.getWidth(), 0.02);
        assertEquals(28.74, dim.getWidthPlusQuiet(), 0.02);
        assertEquals(14.0, dim.getHeight(), 0.02);
        assertEquals(14.0, dim.getHeightPlusQuiet(), 0.02);
        assertEquals(1.9, dim.getXOffset(), 0.02);
        assertEquals(0, dim.getYOffset(), 0.02);

        dim = bean.calcDimensions("test");
        //Without the extended character set, start/stop is filtered,
        //therefore the width is the same
        assertEquals(24.94, dim.getWidth(), 0.02);
        assertEquals(28.74, dim.getWidthPlusQuiet(), 0.02);
        assertEquals(14.0, dim.getHeight(), 0.02);
        assertEquals(14.0, dim.getHeightPlusQuiet(), 0.02);
        assertEquals(1.9, dim.getXOffset(), 0.02);
        assertEquals(0, dim.getYOffset(), 0.02);

        bean.setExtendedCharSetEnabled(true);

        dim = bean.calcDimensions("*test*");
        assertEquals(58.58, dim.getWidth(), 0.02);
        assertEquals(62.38, dim.getWidthPlusQuiet(), 0.02);
        assertEquals(14.0, dim.getHeight(), 0.02);
        assertEquals(14.0, dim.getHeightPlusQuiet(), 0.02);
        assertEquals(1.9, dim.getXOffset(), 0.02);
        assertEquals(0, dim.getYOffset(), 0.02);

        dim = bean.calcDimensions("test");
        //With the extended character set, "*" is an escaped character
        //so without them, the barcode is narrower
        assertEquals(41.76, dim.getWidth(), 0.02);
        assertEquals(45.56, dim.getWidthPlusQuiet(), 0.02);
        assertEquals(14.0, dim.getHeight(), 0.02);
        assertEquals(14.0, dim.getHeightPlusQuiet(), 0.02);
        assertEquals(1.9, dim.getXOffset(), 0.02);
        assertEquals(0, dim.getYOffset(), 0.02);
    }

}