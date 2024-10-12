package org.krysalis.barcode4j.impl.pdf417;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Samael Bate (singingbush)
 * created on 12/10/2024
 */
class PDF417Test {

    @Test
    void testBeanType() {
        final PDF417 impl = new PDF417();

        assertEquals(PDF417Bean.class, impl.getPDF417Bean().getClass());
    }

    @Test
    void testIllegalArguments() {
        final PDF417 impl = new PDF417();

        // due to @NotNull on args, can be one of two exceptions depending on how tests are run
        assertThrows(RuntimeException.class,
            () -> impl.generateBarcode(null, null),
            "Expected a NullPointerException or IllegalArgumentException");
    }

    @Test
    void testLogic() {
        final PDF417Bean bean = new PDF417Bean();
        final TwoDimBarcodeLogicHandler handler = new MockTwoDimBarcodeLogicHandler();

        final PDF417LogicImpl logic = new PDF417LogicImpl();
        logic.generateBarcodeLogic(handler, "123", bean);

        final String expected = "<BC>" +
            "<row><SBG:start-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:stop-char:null></SBG></row><row><SBG:start-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:stop-char:null></SBG></row><row><SBG:start-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:msg-char:null></SBG><SBG:stop-char:null></SBG></row>" +
            "</BC>";

        assertEquals(expected, handler.toString());
    }

    private static final class MockTwoDimBarcodeLogicHandler implements TwoDimBarcodeLogicHandler {
        private final StringBuffer sb;

        public MockTwoDimBarcodeLogicHandler() {
            this.sb = new StringBuffer();
        }

        @Override
        public void startRow() {
            sb.append("<row>");
        }

        @Override
        public void endRow() {
            sb.append("</row>");
        }

        /** {@inheritDoc} */
        public void startBarGroup(BarGroup type, String submsg) {
            sb.append("<SBG:");
            sb.append(type.getName());
            sb.append(":");
            sb.append(submsg);
            sb.append(">");
        }

        /** {@inheritDoc} */
        public void addBar(boolean black, int weight) {
            // NO-OP
        }

        /** {@inheritDoc} */
        public void endBarGroup() {
            sb.append("</SBG>");
        }

        /** {@inheritDoc} */
        public void startBarcode(String msg, String formattedMsg) {
            sb.append("<BC>");
        }

        /** {@inheritDoc} */
        public void endBarcode() {
            sb.append("</BC>");
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

}
