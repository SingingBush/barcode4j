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
package org.krysalis.barcode4j.impl.code128;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.NullClassicBarcodeLogicHandler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Code128 implementation.
 *
 * @author Jeremias Maerki
 * @version $Id: Code128Test.java,v 1.2 2009-02-20 13:07:20 jmaerki Exp $
 */
public class Code128Test {

    @Test
    void testIllegalArguments() {
        final Code128 impl = new Code128();

        // due to @NotNull on args, can be one of two exceptions depending on how tests are run
        assertThrows(RuntimeException.class,
            () -> impl.generateBarcode(null, null),
            "Expected a NullPointerException or IllegalArgumentException");
    }

    @Test
    void testLogic() {
        final StringBuffer sb = new StringBuffer();
        final Code128LogicImpl logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123");

        final String expected = "<BC>"
            + "<SBG:msg-char:StartB>B2W1B1W2B1W4</SBG>"
            + "<SBG:msg-char:idx17>B1W2B3W2B2W1</SBG>"
            + "<SBG:msg-char:idx18>B2W2B3W2B1W1</SBG>"
            + "<SBG:msg-char:idx19>B2W2B1W1B3W2</SBG>"
            + "<SBG:msg-char:idx8>B1W3B2W2B1W2</SBG>"
            + "<SBG:stop-char:null>B2W3B3W1B1W1B2</SBG>"
            + "</BC>";

        assertEquals(expected, sb.toString());
    }

    @Test
    void testLogicThrowsOnInvalidChars() {
        final Code128LogicImpl logic = new Code128LogicImpl();

        assertThrows(IllegalArgumentException.class,
            () -> logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "123èöö2"),
            "Expected an IllegalArgumentException due to illegal characters");
    }

    @Test
    void testNonPrintableAscii() throws Exception {
        StringBuffer sb = new StringBuffer();
        Code128LogicImpl logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb, false, true), "AA\rBB\tCC");

        final String expected = "<BC:AA BB CC>"
            + "<SBG:msg-char:StartA></SBG>"
            + "<SBG:msg-char:idx33></SBG>"
            + "<SBG:msg-char:idx33></SBG>"
            + "<SBG:msg-char:idx77></SBG>"
            + "<SBG:msg-char:idx34></SBG>"
            + "<SBG:msg-char:idx34></SBG>"
            + "<SBG:msg-char:idx73></SBG>"
            + "<SBG:msg-char:idx35></SBG>"
            + "<SBG:msg-char:idx35></SBG>"
            + "<SBG:msg-char:idx54></SBG>"
            + "<SBG:stop-char:null></SBG>"
            + "</BC>";
        assertEquals(expected, sb.toString());
    }

    @Test
    void testBug942246() throws Exception {
        Code128LogicImpl logic = new Code128LogicImpl();
        logic.generateBarcodeLogic(new NullClassicBarcodeLogicHandler(), "\u00f1020456789012341837100\u00f13101000200");
        //expect no failure
    }

}
