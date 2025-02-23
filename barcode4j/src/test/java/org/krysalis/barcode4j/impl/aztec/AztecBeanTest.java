package org.krysalis.barcode4j.impl.aztec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AztecBeanTest {

    @Test
    void testErrorCorrectionParameter() {
        final AztecBean bean = new AztecBean();
        assertEquals(33, bean.getErrorCorrectionLevel(), "Should default to 33");
        bean.setErrorCorrectionLevel(50);
        assertEquals(50, bean.getErrorCorrectionLevel(), "Can override to values between 23 and 99");
    }

    @Test
    void testLayersParameter() {
        final AztecBean bean = new AztecBean();
        assertEquals(0, bean.getLayers(), "Should default to 0");
        bean.setLayers(16);
        assertEquals(16, bean.getLayers(), "Can override to values from 1 to 32");
    }
}
