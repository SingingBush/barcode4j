package org.krysalis.barcode4j.image.loader;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageLoaderBarcodeTest {

    @Test
    @DisplayName("Should load original ImageBarcode when instantiated with BARCODE_IMAGE_FLAVOR target")
    void testSupportedTargetFlavor_Barcode4jImageFlavor() throws ImageException, IOException {
        final ImageBarcode originalImage = mock(ImageBarcode.class);

        final ImageInfo info = mock(ImageInfo.class);
        when(info.getOriginalImage()).thenReturn(originalImage);

        final ImageLoader loader = new ImageLoaderBarcode(ImageBarcode.BARCODE_IMAGE_FLAVOR);

        final Image result = loader.loadImage(info, null, null);

        assertNotNull(result);
        assertEquals(originalImage, result);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with target flavor: BUFFERED_IMAGE")
    void testUnsupportedTargetFlavor_BufferedImage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ImageLoaderBarcode(ImageFlavor.BUFFERED_IMAGE);
        });
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException with target flavor: RENDERED_IMAGE")
    void testUnsupportedTargetFlavor_RenderedImage() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ImageLoaderBarcode(ImageFlavor.RENDERED_IMAGE);
        });
    }
}
