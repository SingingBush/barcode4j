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
import org.krysalis.barcode4j.impl.NullClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.upcean.UPCE;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;
import org.krysalis.barcode4j.impl.upcean.UPCELogicImpl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UPC-E implementation.
 *
 * @author Jeremias Maerki
 * @version $Id: UPCETest.java,v 1.1 2004-09-12 17:57:54 jmaerki Exp $
 */
public class UPCETest {

    @Test
    void testIllegalArguments() throws Exception {
        final UPCE impl = new UPCE();
        try {
            impl.generateBarcode(null, null);
            fail("Expected an Null Pointer or Illegal Arg exception");
        } catch (IllegalArgumentException | NullPointerException e) {
            assertNotNull(e.getMessage(), "Error message is empty");
        }

        //Test invalid characters in message
        try {
            final UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2");
            fail("Expected an exception complaining about illegal characters");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //Test less than 12 characters
        try {
            final UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }

        //Test more than 12 characters
        try {
            final UPCELogicImpl logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
            logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123456789012344567890");
            fail("Expected an exception complaining about invalid message length");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    private static final String[][] COMPACTION_TESTS = {
        {"01278907", "012000007897"},
        {"01278916", "012100007896"},
        {"01278925", "012200007895"},
        {"01238935", "012300000895"},
        {"01248934", "012400000894"},
        {"01258933", "012500000893"},
        {"01268932", "012600000892"},
        {"01278931", "012700000891"},
        {"01288930", "012800000890"},
        {"01298939", "012900000899"},
        {"01291944", "012910000094"},
        {"01291155", "012911000055"},
        {"01291162", "012911000062"},
        {"01291179", "012911000079"},
        {"01291186", "012911000086"},
        {"00102546", "001020000056"},
        {"01234133", "012300000413"}};

    @Test
    void testMessageCompaction() throws Exception {
        for (int i = 0; i < COMPACTION_TESTS.length; i++) {
            assertEquals(
                COMPACTION_TESTS[i][0],
                UPCELogicImpl.compactMessage(COMPACTION_TESTS[i][1]),
                COMPACTION_TESTS[i][1] + " must be compacted to " + COMPACTION_TESTS[i][0]
            );

            String nocheck = COMPACTION_TESTS[i][1].substring(0, 11);
            assertEquals(
                COMPACTION_TESTS[i][0],
                UPCELogicImpl.compactMessage(nocheck),
                nocheck + " must be compacted to " + COMPACTION_TESTS[i][0]
            );
        }

        final String noUPCE = "01234567890";
        assertNull(UPCELogicImpl.compactMessage(noUPCE));
        assertNull(UPCELogicImpl.compactMessage(noUPCE + UPCEANLogicImpl.calcChecksum(noUPCE)));

        try {
            UPCELogicImpl.compactMessage("ajsgf");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
        try {
            UPCELogicImpl.compactMessage("0000000000000000000000000");
            fail("Invalid messages must fail");
        } catch (IllegalArgumentException iae) {
            //must fail
        }
    }

    @Test
    void testMessageExpansion() throws Exception {
        for (int i = 0; i < COMPACTION_TESTS.length; i++) {
            assertEquals(
                COMPACTION_TESTS[i][1],
                UPCELogicImpl.expandMessage(COMPACTION_TESTS[i][0]),
                COMPACTION_TESTS[i][0] + " must be expanded to " + COMPACTION_TESTS[i][1]
            );

            String nocheck = COMPACTION_TESTS[i][0].substring(0, 7);
            assertEquals(
                    COMPACTION_TESTS[i][1],
                UPCELogicImpl.expandMessage(nocheck),
                    nocheck + " must be expanded to " + COMPACTION_TESTS[i][1]
            );
        }
    }

    @Test
    void testLogic() throws Exception {
        StringBuffer sb = new StringBuffer();
        UPCELogicImpl logic;
        String expected;

        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "0425261");
        expected = "<BC>"
            + "<SBG:upc-ean-guard:null>B1W1B1</SBG>"
            + "<SBG:upc-ean-lead:0>"
            + "</SBG>"
            + "<SBG:upc-ean-group:425261>"
                + "<SBG:msg-char:4>W2B3W1B1</SBG>"
                + "<SBG:msg-char:2>W2B1W2B2</SBG>"
                + "<SBG:msg-char:5>W1B3W2B1</SBG>"
                + "<SBG:msg-char:2>W2B2W1B2</SBG>"
                + "<SBG:msg-char:6>W1B1W1B4</SBG>"
                + "<SBG:msg-char:1>W2B2W2B1</SBG>"
            + "</SBG>"
            + "<SBG:upc-ean-check:4>"
            + "</SBG>"
            + "<SBG:upc-ean-guard:null>W1B1W1B1W1B1</SBG>"
            + "</BC>";
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with check mode
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_CHECK);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04252614");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with UPC-A message
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "042100005264");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

        //The same but with UPC-A message without checksum
        sb.setLength(0);
        logic = new UPCELogicImpl(ChecksumMode.CP_AUTO);
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "04210000526");
        //System.out.println(expected);
        //System.out.println(sb.toString());
        assertEquals(expected, sb.toString());

    }

}
