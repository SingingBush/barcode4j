package org.krysalis.barcode4j.output.bitmap;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @Test
    @DisplayName("BitmapEncoderRegistry should allow registering another BitmapEncoder instance and retrieving by mime type")
    @Order(Integer.MAX_VALUE-1) // make sure this runs after the tests above
    void testRegisterBitmapEncoder() {
        final BitmapEncoder mockEncoder = mock(BitmapEncoder.class);
        when(mockEncoder.getSupportedMIMETypes()).thenReturn(new String[] { "image/mock" });

        assertTrue(BitmapEncoderRegistry.register(mockEncoder, 50));

        assertEquals(mockEncoder, BitmapEncoderRegistry.getInstance("image/mock"));
    }

    @Test
    @DisplayName("BitmapEncoderRegistry should allow registering another BitmapEncoder by classname and retrieving by mime type")
    @Order(Integer.MAX_VALUE) // make sure this runs after the tests above
    void testRegisterBitmapEncoderByClassname() {
        assertThrows(UnsupportedOperationException.class, () -> BitmapEncoderRegistry.getInstance("fake/image"));

        BitmapEncoderRegistry.register(TestBitmapEncoder.class.getName(), 10);

        assertEquals(TestBitmapEncoder.class, BitmapEncoderRegistry.getInstance("fake/image").getClass());
    }

    // Just used for testing. This approach can be used to create and registering custom encoders
    public static final class TestBitmapEncoder implements BitmapEncoder {
        @Override
        public String[] getSupportedMIMETypes() {
            return new String[] { "fake/image" };
        }

        @Override
        public void encode(BufferedImage image, OutputStream out, String mime, int resolution) throws IOException {}
    }
}
