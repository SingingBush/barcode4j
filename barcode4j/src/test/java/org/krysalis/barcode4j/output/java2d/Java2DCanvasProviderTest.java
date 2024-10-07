package org.krysalis.barcode4j.output.java2d;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Java2DCanvasProviderTest {

    private static final int ORIENTATION = 0; // one of: 0, 90, 180, 270

    @Test
    @DisplayName("establishDimensions() 0 Degrees (Default Orientation)")
    void establishDimensionsDefaultOrientation() {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        when(mockGraphics2D.create()).thenReturn(mockGraphics2D);

        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, ORIENTATION);
        canvasProvider.establishDimensions(new BarcodeDimension(100, 100));

        verify(mockGraphics2D, never()).rotate(anyDouble());
        verify(mockGraphics2D, never()).translate(anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("establishDimensions() 90 Degrees")
    void establishDimensionsDefault90Degrees() {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        when(mockGraphics2D.create()).thenReturn(mockGraphics2D);

        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, 90);
        canvasProvider.establishDimensions(new BarcodeDimension(100, 100));

        verify(mockGraphics2D, times(1)).rotate(eq(-1.5707963267948966));
        verify(mockGraphics2D, times(1)).translate(eq(-100.0), eq(0.0));
    }

    @Test
    @DisplayName("establishDimensions() 180 Degrees")
    void establishDimensionsDefault180Degrees() {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        when(mockGraphics2D.create()).thenReturn(mockGraphics2D);

        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, 180);
        canvasProvider.establishDimensions(new BarcodeDimension(100, 100));

        verify(mockGraphics2D, times(1)).rotate(eq(-3.141592653589793));
        verify(mockGraphics2D, times(1)).translate(eq(-100.0), eq(-100.0));
    }

    @Test
    @DisplayName("establishDimensions() 270 Degrees")
    void establishDimensionsDefault270Degrees() {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        when(mockGraphics2D.create()).thenReturn(mockGraphics2D);

        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, 270);
        canvasProvider.establishDimensions(new BarcodeDimension(100, 100));

        verify(mockGraphics2D, times(1)).rotate(eq(-4.71238898038469));
        verify(mockGraphics2D, times(1)).translate(eq(0.0), eq(-100.0));
    }

    @ParameterizedTest
    @DisplayName("Fill Rect")
    @MethodSource("reactangleArgs")
    void deviceFillRect(double x, double y, double width, double height) {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, ORIENTATION);

        canvasProvider.deviceFillRect(x, y, width, height);

        verify(mockGraphics2D, times(1)).fill(eq(new Rectangle2D.Double(x, y, width, height)));
    }

    @ParameterizedTest
    @DisplayName("Draw Rect")
    @MethodSource("reactangleArgs")
    void deviceDrawRect(double x, double y, double width, double height) {
        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, ORIENTATION);

        canvasProvider.deviceDrawRect(x, y, width, height);

        verify(mockGraphics2D, times(1)).draw(eq(new Rectangle2D.Double(x, y, width, height)));
    }

    @Test
    @DisplayName("deviceText() should set font on g2d and draw glyph")
    void deviceText() {
        final FontRenderContext fontRenderContext = new FontRenderContext(
            null,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON
        );

        final Graphics2D mockGraphics2D = mock(Graphics2D.class);
        when(mockGraphics2D.getFontRenderContext()).thenReturn(fontRenderContext);

        final Java2DCanvasProvider canvasProvider = new Java2DCanvasProvider(mockGraphics2D, ORIENTATION);
        canvasProvider.deviceText("Blah", 0, 0, 0, "", 1.2, TextAlignment.TA_JUSTIFY);

        verify(mockGraphics2D, times(1)).setFont(any(Font.class));
        verify(mockGraphics2D, times(1)).drawGlyphVector(any(GlyphVector.class), anyFloat(), anyFloat());
        verify(mockGraphics2D, times(1)).setFont(any(Font.class));
    }

    private static Stream<Arguments> reactangleArgs() {
        return Stream.of(
            Arguments.of(0, 0, 0, 0),
            Arguments.of(5, 5, 100, 100),
            Arguments.of(10, 10, 250, 400)
        );
    }
}
