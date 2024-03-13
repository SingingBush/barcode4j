package org.krysalis.barcode4j.output.bitmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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

        assertTrue(mimeTypes.containsAll(Arrays.stream(new String[] {
            "image/vnd.wap.wbmp",
            "image/png",
            "image/x-png",
            "image/jpeg",
            "image/tiff",
            "image/bmp",
            "image/gif"
        }).collect(Collectors.toSet())));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image/vnd.wap.wbmp",
        "image/png",
        "image/x-png",
        "image/jpeg",
        "image/tiff",
        "image/bmp",
        "image/gif"
    })
    @DisplayName("supports with type %s")
    void supportMimeType(final String mimeType) {
        assertTrue(BitmapEncoderRegistry.supports(mimeType));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "image/vnd.wap.wbmp",
        "image/png",
        "image/x-png",
        "image/jpeg",
        "image/tiff",
        "image/bmp",
        "image/gif"
    })
    @DisplayName("supports with ImageIOBitmapEncoder and type %s")
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
