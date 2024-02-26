package org.krysalis.barcode4j.image.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImageLoaderFactoryBarcodeTest {

    private ImageLoaderFactoryBarcode imageLoaderFactoryBarcode;

    @BeforeEach
    void setUp() {
        this.imageLoaderFactoryBarcode = new ImageLoaderFactoryBarcode();
    }

    @Test
    void testIsAvailable() {
        // makes sure that the classloader has the barcode4j BarcodeGenerator available
        assertTrue(imageLoaderFactoryBarcode.isAvailable());
    }
}
