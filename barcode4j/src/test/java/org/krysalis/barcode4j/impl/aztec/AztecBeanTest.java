package org.krysalis.barcode4j.impl.aztec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.output.CanvasProvider;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void testEncodingParameter() {
        final AztecBean bean = new AztecBean();
        assertEquals(StandardCharsets.ISO_8859_1.name(), bean.getEncoding(), "Should default to ISO_8859_1");
        bean.setEncoding(StandardCharsets.US_ASCII.name());
        assertEquals(StandardCharsets.US_ASCII.name(), bean.getEncoding());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "" })
    void testGenerateBarcodeWithNoMessage(final String message) {
        final CanvasProvider provider = mock(CanvasProvider.class);
        final AztecBean bean = new AztecBean();

        assertThrows(IllegalArgumentException.class, () -> bean.generateBarcode(provider, message));
    }

    @Test
    void testGenerateBarcodeWithMessage() {
        final CanvasProvider provider = mock(CanvasProvider.class);
        final AztecBean bean = new AztecBean();

        bean.generateBarcode(provider, "Hello Aztec Barcodes!");

        // we can verify that DefaultTwoDimCanvasLogicHandler::addBar was invoked by checking the provider's deviceFillRect is called
        verify(provider, times(1)).establishDimensions(any());
        verify(provider, times(179)).deviceFillRect(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}
