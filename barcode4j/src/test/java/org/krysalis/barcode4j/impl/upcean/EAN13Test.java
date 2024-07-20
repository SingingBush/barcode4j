package org.krysalis.barcode4j.impl.upcean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.output.CanvasProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class EAN13Test {

    /* 13 numeric chars including a checksum */
    private static final String VALID_EAN13_MESSAGE = "7812487141205";

    @Test
    @DisplayName("Should fail fast if args are incorrect")
    void testGenerateBarcodeWithBadArgs() {
        final EAN13 impl = new EAN13();
        assertInstanceOf(EAN13Bean.class, impl.getBean());

        final CanvasProvider mockCanvas = mock(CanvasProvider.class);

        assertThrows(NullPointerException.class, () -> impl.generateBarcode(mockCanvas, null));
        assertThrows(NullPointerException.class, () -> impl.generateBarcode(mockCanvas, ""));
    }

    @Test
    @DisplayName("Should establish the canvas dimensions")
    void testGenerateBarcodeEstablishesDimensions() {
        final EAN13 impl = new EAN13();
        assertInstanceOf(EAN13Bean.class, impl.getBean());

        final CanvasProvider mockCanvas = mock(CanvasProvider.class);

        impl.generateBarcode(mockCanvas, VALID_EAN13_MESSAGE);

        verify(mockCanvas).establishDimensions(any(BarcodeDimension.class));
    }

    @Test
    @DisplayName("The EAN13Logic should use the handler to generate a barcode")
    void testEAN13LogicGenerateBarcode() {
        final EAN13LogicImpl logic = new EAN13LogicImpl(ChecksumMode.CP_AUTO);

        final ClassicBarcodeLogicHandler mockHandler = mock(ClassicBarcodeLogicHandler.class);
        logic.generateBarcodeLogic(mockHandler, VALID_EAN13_MESSAGE);

        // barcode has start and end
        verify(mockHandler, times(1)).startBarcode(eq(VALID_EAN13_MESSAGE), eq(VALID_EAN13_MESSAGE));
        verify(mockHandler, times(1)).endBarcode();

        // bar groups:
        verify(mockHandler, times(1)).startBarGroup(eq(BarGroup.UPC_EAN_GROUP), eq("7:812487"));
        verify(mockHandler, times(1)).startBarGroup(eq(BarGroup.UPC_EAN_GROUP), eq(VALID_EAN13_MESSAGE.substring(7, 13)));
        verify(mockHandler, times(1)).startBarGroup(eq(BarGroup.UPC_EAN_CHECK), eq(String.valueOf(VALID_EAN13_MESSAGE.charAt(12))));
        // each char of the message
        verify(mockHandler, times(VALID_EAN13_MESSAGE.length()-1)).startBarGroup(eq(BarGroup.MSG_CHARACTER), anyString());
        // x2 side guards & 1 center guard
        verify(mockHandler, times(3)).startBarGroup(eq(BarGroup.UPC_EAN_GUARD), eq(null));

        // all the bar groups are ended
        verify(mockHandler, times(5+VALID_EAN13_MESSAGE.length())).endBarGroup(); // multiple times
    }
}
