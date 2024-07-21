package org.krysalis.barcode4j.impl.datamatrix;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.*;
import org.krysalis.barcode4j.output.CanvasProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Samael Bate (singingbush)
 * created on 21/07/2024
 */
class DataMatrixTest {

    private static final String VALID_NUMERIC_MESSAGE = "123456"; // fit's into 8x8 symbol
    private static final String VALID_ASCII_MESSAGE = "This is a larger message that requires 24x24"; // fit's into 24x24 symbol
    private static final String VALID_TEXT_MESSAGE_FR = "Il s'agit d'un message plus vaste qui nécessite 4 x (14x14)"; // ISO-8859-1

    private static final String UNSUPPORTED_TEXT_MESSAGE_CN = "这是一条较大的消息，需要"; // Cannot use UTF-8

    @Test
    @DisplayName("Should fail fast if args are incorrect")
    void testGenerateBarcodeWithBadArgs() {
        final DataMatrix impl = new DataMatrix();
        assertInstanceOf(DataMatrixBean.class, impl.getBean());

        final CanvasProvider mockCanvas = mock(CanvasProvider.class);

        assertThrows(NullPointerException.class, () -> impl.generateBarcode(mockCanvas, null));
        assertThrows(NullPointerException.class, () -> impl.generateBarcode(mockCanvas, ""));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        VALID_NUMERIC_MESSAGE,
        VALID_ASCII_MESSAGE,
        VALID_TEXT_MESSAGE_FR
    })
    @DisplayName("Should establish the canvas dimensions")
    void testGenerateBarcodeEstablishesDimensions(final String message) {
        final DataMatrix impl = new DataMatrix();
        assertInstanceOf(DataMatrixBean.class, impl.getBean());

        final CanvasProvider mockCanvas = mock(CanvasProvider.class);

        impl.generateBarcode(mockCanvas, message);

        verify(mockCanvas).establishDimensions(any(BarcodeDimension.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        VALID_NUMERIC_MESSAGE,
        VALID_ASCII_MESSAGE,
        VALID_TEXT_MESSAGE_FR
    })
    @EmptySource
    @DisplayName("The DataMatrixLogic should use the handler to generate a barcode")
    void testDataMatrixLogicGenerateBarcode(final String message) {
        final DataMatrixLogicImpl logic = new DataMatrixLogicImpl();

        final TwoDimBarcodeLogicHandler mockHandler = mock(TwoDimBarcodeLogicHandler.class);
        logic.generateBarcodeLogic(mockHandler, message, SymbolShapeHint.FORCE_NONE, null, null);

        // barcode has start and end
        verify(mockHandler, times(1)).startBarcode(eq(message), eq(message));
        verify(mockHandler, times(1)).endBarcode();

        // rows
        verify(mockHandler, atLeast(10)).startRow();
        verify(mockHandler, atLeast(10)).endRow();

        // draws a black square
        verify(mockHandler, atLeast(10)).addBar(eq(true), eq(1));

        // bar groups are not used
        verify(mockHandler, never()).startBarGroup(any(BarGroup.class), anyString());
        verify(mockHandler, never()).endBarGroup();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        UNSUPPORTED_TEXT_MESSAGE_CN
    })
    void testDataMatrixLogicWithInvalidMessage(final String message) {
        final DataMatrixLogicImpl logic = new DataMatrixLogicImpl();

        final TwoDimBarcodeLogicHandler mockHandler = mock(TwoDimBarcodeLogicHandler.class);

        assertThrows(IllegalArgumentException.class, () -> logic.generateBarcodeLogic(mockHandler, message, SymbolShapeHint.FORCE_NONE, null, null));
    }
}
