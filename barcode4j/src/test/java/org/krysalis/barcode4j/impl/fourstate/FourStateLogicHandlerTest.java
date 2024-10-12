package org.krysalis.barcode4j.impl.fourstate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;

import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Samael Bate (singingbush)
 * created on 12/10/2024
 */
class FourStateLogicHandlerTest {

    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3 })
    void addBar(final int height) {
        final HeightVariableBarcodeBean bean = mock(HeightVariableBarcodeBean.class);
        when(bean.getBarWidth(1)).thenReturn(100.0);
        when(bean.getBarHeight(height)).thenReturn(100.0);
        when(bean.getBarHeight()).thenReturn((double)height);
        when(bean.getBarHeight(anyInt())).thenReturn(4.0);

        final Canvas canvas = mock(Canvas.class);
        final FourStateLogicHandler logicHandler = new FourStateLogicHandler(bean, canvas);

        logicHandler.addBar(true, height);

        verify(canvas, times(1)).drawRectWH(
            anyDouble(),
            anyDouble(),
            eq(100.0),
            eq(4.0)
        );
        verify(bean, times(1)).getBarWidth(eq(-1));
    }
}
