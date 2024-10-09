package org.krysalis.barcode4j.output.bitmap;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.impl.upcean.EAN13;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BitmapBuilderTest {

    private static final int DESIRED_DPI = 300; // desired image resolution (dots per inch)

    /* 13 numeric chars including a checksum */
    private static final String VALID_EAN13_MESSAGE = "7812487141205";

    @Test
    void testPrepareImage() {
        final BarcodeDimension dim = new BarcodeDimension(200.0, 100.0); // in millimeters
        final int imageType = BufferedImage.TYPE_INT_RGB;

        final BufferedImage result = BitmapBuilder.prepareImage(dim, DESIRED_DPI, imageType);

        assertNotNull(result);
        // the image will have pixel dimensions calculated based on the mm dimensions of barcode
        // with space based on orientation as well as taking DPI into account
        assertEquals(2362, result.getWidth());
        assertEquals(1181, result.getHeight());
    }

    @Test
    void getImage() {
        final BarcodeGenerator bargen = new EAN13();

        final BufferedImage result = BitmapBuilder.getImage(bargen, VALID_EAN13_MESSAGE, DESIRED_DPI);

        assertNotNull(result);
        assertEquals(ComponentColorModel.class, result.getColorModel().getClass());
        assertEquals(448, result.getWidth());
        assertEquals(210, result.getHeight());
    }

    @Test // just ensure no exception
    void outputBarcodeImage() throws IOException {
        BitmapBuilder.outputBarcodeImage(
            new EAN13(),
            VALID_EAN13_MESSAGE,
            NullOutputStream.INSTANCE,
            "image/png",
            DESIRED_DPI
        );
    }
}
