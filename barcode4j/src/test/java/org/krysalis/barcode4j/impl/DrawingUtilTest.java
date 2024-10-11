package org.krysalis.barcode4j.impl;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.Canvas;

import static org.mockito.Mockito.*;

class DrawingUtilTest {

    @Test
    void drawCenteredChar() {
        final Canvas mockCanvas = mock(Canvas.class);
        final AbstractBarcodeBean mockBean = mock(AbstractBarcodeBean.class);
        when(mockBean.getFontName()).thenReturn("TestingFontName");
        when(mockBean.getFontSize()).thenReturn(1.2);

        DrawingUtil.drawCenteredChar(
            mockCanvas,
            mockBean,
            'A',
            1.0,
            1.0,
            1.0
        );

        verify(mockCanvas, times(1)).drawCenteredChar(
            eq('A'),
            eq(1.0),
            eq(1.0),
            anyDouble(),
            eq("TestingFontName"),
            eq(1.2)
        );
    }

    @Test
    void drawJustifiedText() {
        final Canvas mockCanvas = mock(Canvas.class);
        final AbstractBarcodeBean mockBean = mock(AbstractBarcodeBean.class);
        when(mockBean.getFontName()).thenReturn("TestingFontName");
        when(mockBean.getFontSize()).thenReturn(1.2);

        DrawingUtil.drawJustifiedText(
            mockCanvas,
            mockBean,
            "Hello World",
            1.0,
            1.0,
            1.0
        );

        verify(mockCanvas, times(1)).drawText(
            eq("Hello World"),
            eq(1.0),
            eq(1.0),
            anyDouble(),
            eq("TestingFontName"),
            eq(1.2),
            eq(TextAlignment.TA_JUSTIFY)
        );
    }

    @Test
    void drawCenteredText() {
        final Canvas mockCanvas = mock(Canvas.class);
        final AbstractBarcodeBean mockBean = mock(AbstractBarcodeBean.class);
        when(mockBean.getFontName()).thenReturn("TestingFontName");
        when(mockBean.getFontSize()).thenReturn(1.2);

        DrawingUtil.drawCenteredText(
            mockCanvas,
            mockBean,
            "Hello World",
            1.0,
            1.0,
            1.0
        );

        verify(mockCanvas, times(1)).drawText(
            eq("Hello World"),
            eq(1.0),
            eq(1.0),
            anyDouble(),
            eq("TestingFontName"),
            eq(1.2),
            eq(TextAlignment.TA_CENTER)
        );
    }

    @Test
    void drawText() {
        final Canvas mockCanvas = mock(Canvas.class);
        final AbstractBarcodeBean mockBean = mock(AbstractBarcodeBean.class);
        when(mockBean.getFontName()).thenReturn("TestingFontName");
        when(mockBean.getFontSize()).thenReturn(1.2);

        DrawingUtil.drawText(
            mockCanvas,
            mockBean,
            "Hello World",
            1.0,
            1.0,
            1.0,
            TextAlignment.TA_RIGHT
        );

        verify(mockCanvas, times(1)).drawText(
            eq("Hello World"),
            eq(1.0),
            eq(1.0),
            anyDouble(),
            eq("TestingFontName"),
            eq(1.2),
            eq(TextAlignment.TA_RIGHT)
        );
    }
}
