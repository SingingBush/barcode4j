/*
 * Copyright 2003,2004 Jeremias Maerki.
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

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.postnet.POSTNET;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.postnet.POSTNETLogicImpl;

import org.krysalis.barcode4j.configuration.DefaultConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the POSTNET implementation.
 *
 * @author Chris Dolphy
 * @version $Id: POSTNETTest.java,v 1.5 2008-05-13 13:00:43 jmaerki Exp $
 */
public class POSTNETTest {

    @Test
    void testChecksum() throws Exception {
        assertEquals('1', POSTNETLogicImpl.calcChecksum("75368"));
        assertEquals('7', POSTNETLogicImpl.calcChecksum("110119000"));
        assertEquals('7', POSTNETLogicImpl.calcChecksum("11011-9000"));
        assertEquals('0', POSTNETLogicImpl.calcChecksum("400017265951"));
    }

    @Test
    void testIllegalArguments() throws Exception {
        final POSTNET impl = new POSTNET();
        try {
            impl.generateBarcode(null, null);
            fail("Expected an Null Pointer or Illegal Arg exception");
        } catch (IllegalArgumentException | NullPointerException e) {
            assertNotNull(e.getMessage(), "Error message is empty");
        }
    }

    @Test
    void testIgnoreChars() throws Exception {
        assertEquals("75368", POSTNETLogicImpl.removeIgnoredCharacters("75368"));
        assertEquals("110119000", POSTNETLogicImpl.removeIgnoredCharacters("11011-9000"));
    }

    @Test
    void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        POSTNETLogicImpl logic;
        String expected;

        try {
            logic = new POSTNETLogicImpl(ChecksumMode.CP_AUTO, false);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        logic = new POSTNETLogicImpl(ChecksumMode.CP_AUTO, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "75368");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());


        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_ADD, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "75368");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:1>B1W-1B1W-1B1W-1B2W-1B2W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());


        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_CHECK, false);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "753681");
        expected = "<BC>"
            + "B2W-1"
            + "<SBG:msg-char:7>B2W-1B1W-1B1W-1B1W-1B2W-1</SBG>"
            + "<SBG:msg-char:5>B1W-1B2W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:3>B1W-1B1W-1B2W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:6>B1W-1B2W-1B2W-1B1W-1B1W-1</SBG>"
            + "<SBG:msg-char:8>B2W-1B1W-1B1W-1B2W-1B1W-1</SBG>"
            + "<SBG:msg-char:1>B1W-1B1W-1B1W-1B2W-1B2W-1</SBG>"
            + "B2"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());


        sb.setLength(0);
        logic = new POSTNETLogicImpl(ChecksumMode.CP_CHECK, false);
        try {
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "753685");
            fail("Expected logic implementation to fail because wrong checksum is supplied");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    @Test
    void testDefaultsInXML() throws Exception {
        POSTNETBean refBean = new POSTNETBean();

        POSTNET gen = new POSTNET();
        DefaultConfiguration cfg = new DefaultConfiguration("postnet");
        gen.configure(cfg);
        POSTNETBean xmlBean = gen.getPOSTNETBean();
        assertEquals(refBean.getBarHeight(), xmlBean.getBarHeight(), 0.01);
        assertEquals(refBean.getBaselinePosition(), xmlBean.getBaselinePosition());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.isDisplayChecksum(), xmlBean.isDisplayChecksum());
        assertEquals(refBean.getFontSize(), xmlBean.getFontSize(), 0.01);
        assertEquals(refBean.getHeight(), xmlBean.getHeight(), 0.01);
        assertEquals(refBean.getHumanReadableHeight(), xmlBean.getHumanReadableHeight(), 0.01);
        assertEquals(refBean.getIntercharGapWidth(), xmlBean.getIntercharGapWidth(), 0.01);
        assertEquals(refBean.getModuleWidth(), xmlBean.getModuleWidth(), 0.01);
        assertEquals(refBean.getQuietZone(), xmlBean.getQuietZone(), 0.01);
        assertEquals(refBean.getShortBarHeight(), xmlBean.getShortBarHeight(), 0.01);
        assertEquals(refBean.getVerticalQuietZone(), xmlBean.getVerticalQuietZone(), 0.01);
        assertEquals(refBean.hasQuietZone(), xmlBean.hasQuietZone());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.getMsgPosition(), xmlBean.getMsgPosition());
        assertEquals(refBean.getPattern(), xmlBean.getPattern());
    }

}
