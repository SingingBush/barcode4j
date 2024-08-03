package org.krysalis.barcode4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samael Bate (singingbush)
 * created on 03/08/2024
 */
class BarcodeDimensionTest {

    final BarcodeDimension dimensionNoQuietZone = new BarcodeDimension(100, 50);
    final BarcodeDimension dimensionWithQuietZone = new BarcodeDimension(100, 50, 20, 20, 0, 0);

    @Test
    void testWidthAndHeightGetters() {
        // basic width / height
        assertEquals(100, dimensionNoQuietZone.getWidth());
        assertEquals(50, dimensionNoQuietZone.getHeight());
        assertEquals(100, dimensionWithQuietZone.getWidth());
        assertEquals(50, dimensionWithQuietZone.getHeight());

        // with the quiet zone
        assertEquals(100, dimensionNoQuietZone.getWidthPlusQuiet());
        assertEquals(50, dimensionNoQuietZone.getHeightPlusQuiet());
        assertEquals(20.0, dimensionWithQuietZone.getWidthPlusQuiet()); // only quiet
        assertEquals(20.0, dimensionWithQuietZone.getHeightPlusQuiet()); // only quiet

        assertEquals(50, dimensionNoQuietZone.getWidth(90));
        assertEquals(100, dimensionNoQuietZone.getHeight(90));
        assertEquals(20.0, dimensionWithQuietZone.getWidthPlusQuiet(90)); // only quiet
        assertEquals(20.0, dimensionWithQuietZone.getHeightPlusQuiet(90)); // only quiet
    }

    @Test
    void testNormalizeOrientation() {
        assertEquals(0, BarcodeDimension.normalizeOrientation(0));
        assertEquals(90, BarcodeDimension.normalizeOrientation(90));
        assertEquals(180, BarcodeDimension.normalizeOrientation(180));
        assertEquals(270, BarcodeDimension.normalizeOrientation(270));

        assertEquals(270, BarcodeDimension.normalizeOrientation(-90));
        assertEquals(180, BarcodeDimension.normalizeOrientation(-180));
        assertEquals(90, BarcodeDimension.normalizeOrientation(-270));
    }

    @Test
    void testNormalizeOrientationWithInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> BarcodeDimension.normalizeOrientation(45));
    }
}
