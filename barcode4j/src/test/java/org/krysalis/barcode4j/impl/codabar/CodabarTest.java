package org.krysalis.barcode4j.impl.codabar;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samael Bate (singingbush)
 * created on 12/10/2024
 */
class CodabarTest {

    @Test
    void testBeanType() {
        final Codabar impl = new Codabar();

        assertEquals(CodabarBean.class, impl.getCodabarBean().getClass());
    }

    @Test
    void testIllegalArguments() {
        final Codabar impl = new Codabar();

        // due to @NotNull on args, can be one of two exceptions depending on how tests are run
        assertThrows(RuntimeException.class,
                () -> impl.generateBarcode(null, null),
                "Expected a NullPointerException or IllegalArgumentException");
    }

    @Test
    void testLogic() {
        final StringBuffer sb = new StringBuffer();
        final CodabarLogicImpl logic = new CodabarLogicImpl();
        logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), "123");

        final String expected = "<BC>" +
                "<SBG:msg-char:1>B1W1B1W1B2W2B1</SBG>W1<SBG:msg-char:2>B1W1B1W2B1W1B2</SBG>W1<SBG:msg-char:3>B2W2B1W1B1W1B1</SBG>" +
                "</BC>";

        assertEquals(expected, sb.toString());
    }

}
