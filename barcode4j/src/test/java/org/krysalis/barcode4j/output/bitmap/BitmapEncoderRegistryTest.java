package org.krysalis.barcode4j.output.bitmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BitmapEncoderRegistryTest {

    @Test
    @DisplayName("BitmapEncoderRegistry does not allow constructor being used")
    void cannotBeInstantiated() {
        assertThrows(UnsupportedOperationException.class, BitmapEncoderRegistry::new);
    }

    @Test
    @DisplayName("BitmapEncoderRegistry should support various Mime Types by default (ImageIOBitmapEncoder should be auto-registered)")
    void getSupportedMIMETypes() {
        final Set<String> mimeTypes = BitmapEncoderRegistry.getSupportedMIMETypes();

        final Set<String> expected = Arrays.stream(ImageIO.getWriterMIMETypes()).collect(Collectors.toSet());

        assertEquals(expected, mimeTypes);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image/vnd.wap.wbmp",
        "image/png",
        "image/x-png",
        "image/jpeg",
        // "image/tiff", // tiff is not supported by JDK 8 ImageIO.getWriterMIMETypes()
        "image/bmp",
        "image/gif"
    })
    @DisplayName("BitmapEncoderRegistry should support all ImageIO mime types (all encoders)")
    void supportMimeType(final String mimeType) {
        assertTrue(BitmapEncoderRegistry.supports(mimeType));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image/vnd.wap.wbmp",
        "image/png",
        "image/x-png",
        "image/jpeg",
        // "image/tiff", // tiff is not supported by JDK 8 ImageIO.getWriterMIMETypes()
        "image/bmp",
        "image/gif"
    })
    @DisplayName("BitmapEncoderRegistry should support all ImageIO mime types (ImageIOBitmapEncoder)")
    void supportMimeTypeByBitmapEncoder(final String mimeType) {
        assertTrue(BitmapEncoderRegistry.supports(new ImageIOBitmapEncoder(), mimeType));
    }

    @Test
    @DisplayName("BitmapEncoderRegistry should not allow registering another ImageIOBitmapEncoder at priority 0 (ImageIOBitmapEncoder should be auto-registered)")
    void testRegisterImageIOBitmapEncoderPriority0() {
        assertFalse(BitmapEncoderRegistry.register(new ImageIOBitmapEncoder(), 0));

    }

    @Test
    @DisplayName("BitmapEncoderRegistry should allow registering another ImageIOBitmapEncoder at priority 1 (ImageIOBitmapEncoder should be auto-registered)")
    void testRegisterImageIOBitmapEncoderPriority1() {
        // todo: Consider changing this. It doesn't make sense to register duplicate bitmap encoders
        //  but existing barcode4j supports this as the TreeSet is for Entry class
        assertTrue(BitmapEncoderRegistry.register(new ImageIOBitmapEncoder(), 1));
    }
}
