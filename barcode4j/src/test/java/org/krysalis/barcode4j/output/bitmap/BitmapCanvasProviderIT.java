package org.krysalis.barcode4j.output.bitmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.int2of5.ITF14Bean;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.postnet.POSTNETBean;
import org.krysalis.barcode4j.impl.qr.QRCodeBean;
import org.krysalis.barcode4j.impl.upcean.*;
import org.krysalis.barcode4j.tools.MimeTypes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/*
* Integration test for the BitmapCanvasProvider that will output an image to the temp dir and
* display the path to the file so that it can be opened and inspected by hand
*/
public class BitmapCanvasProviderIT {

    private static final int DESIRED_DPI = 300; // desired image resolution (dots per inch)
    private static final int ORIENTATION = 0; // one of: 0, 90, 180, 270, -90, -180 or -270

    @ParameterizedTest
    @DisplayName("Should render barcode in jpeg format")
    @MethodSource("barcodeBeans")
    public void testJpegFormat(final AbstractBarcodeBean barcodeBean, final String message) throws IOException {
        final Path tempFile = Files.createTempFile(String.format("barcode-%s-test-%s", barcodeBean.getClass().getSimpleName(), Instant.now().getEpochSecond()), ".jpg");

        try (final OutputStream out = Files.newOutputStream(tempFile)) {
            final BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, MimeTypes.MIME_JPEG, DESIRED_DPI, BufferedImage.TYPE_BYTE_BINARY, false, ORIENTATION);

            //Generate the barcode
            barcodeBean.generateBarcode(canvas, message);

            //Signal end of generation
            canvas.finish();

            System.out.printf("Test file written to %s%n", tempFile);
        } catch (final IOException e) {
            fail();
        }

        assertTrue(Files.size(tempFile) > 0, "Some bytes should have been written to disk");
    }

    @ParameterizedTest
    @DisplayName("Should render barcode in png format")
    @MethodSource("barcodeBeans")
    public void testPngFormat(final AbstractBarcodeBean barcodeBean, final String message) throws IOException {
        final Path tempFile = Files.createTempFile(String.format("barcode-%s-test-%s", barcodeBean.getClass().getSimpleName(), Instant.now().getEpochSecond()), ".png");

        try (final OutputStream out = Files.newOutputStream(tempFile)) {
            final BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, MimeTypes.MIME_PNG, DESIRED_DPI, BufferedImage.TYPE_BYTE_BINARY, false, ORIENTATION);

            //Generate the barcode
            barcodeBean.generateBarcode(canvas, message);

            //Signal end of generation
            canvas.finish();

            System.out.printf("Test file written to %s%n", tempFile);
        } catch (final IOException e) {
            fail();
        }

        assertTrue(Files.size(tempFile) > 0, "Some bytes should have been written to disk");
    }

    private static Stream<Arguments> barcodeBeans() {
        return Stream.of(
            Arguments.of(new Code39Bean(), "0123456789"),
            Arguments.of(new Code128Bean(), "0123456789"),
            Arguments.of(new DataMatrixBean(), "0123456789"),
            Arguments.of(new DataMatrixBean(), "Here's a data matrix with text data"),
            Arguments.of(new Interleaved2Of5Bean(), "0123456789"),
            Arguments.of(new ITF14Bean(), "09501101530003"),
            Arguments.of(new PDF417Bean(), "09501101530003"),
            Arguments.of(new POSTNETBean(), "5555512372"),
            Arguments.of(new QRCodeBean(), "Any old text can be in a QR Code :)"),
            Arguments.of(new EAN8Bean(), "12345670"),
            Arguments.of(new EAN13Bean(), "123456789123"),
            Arguments.of(new UPCABean(), "123456789128"),
            Arguments.of(new UPCEBean(), "12345670")
        );
    }

}
