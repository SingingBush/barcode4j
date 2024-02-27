package org.krysalis.barcode4j.image.loader;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.ImageGraphics2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeDimension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.krysalis.barcode4j.image.loader.ImageBarcode.BARCODE_IMAGE_FLAVOR;

class ImageConverterBarcode2G2DTest {

    private ImageConverterBarcode2G2D imageConverter;

    @BeforeEach
    void setUp() {
        imageConverter = new ImageConverterBarcode2G2D();
    }

    @Test
    void convert() throws ImageException, IOException {
        final ImageInfo info = new ImageInfo("", ImageLoaderFactoryBarcode.MIME_TYPE);

        final DefaultConfiguration barcodeXML = createBarcodeConfig("intl2of5", "0123456789012");

        final BarcodeDimension dimensions = new BarcodeDimension(100, 100);

        final Image image = new ImageBarcode(info, barcodeXML, dimensions);

        final Image converted = imageConverter.convert(image, null);

        assertNotNull(converted);
        assertTrue(ImageGraphics2D.class.isAssignableFrom(converted.getClass()));
    }

    @Test
    void getSourceFlavor() {
        assertEquals(BARCODE_IMAGE_FLAVOR, this.imageConverter.getSourceFlavor());
    }

    @Test
    void getTargetFlavor() {
        assertEquals(ImageFlavor.GRAPHICS2D, this.imageConverter.getTargetFlavor());
    }

    private DefaultConfiguration createBarcodeConfig(final String barcodeType, final String message) {
        final DefaultConfiguration barcodeXML = new DefaultConfiguration("cfg");
        barcodeXML.addChild(new DefaultConfiguration(barcodeType));
        barcodeXML.setAttribute("message", message);
        barcodeXML.setAttribute("orientation", "90");
        return barcodeXML;
    }
}
