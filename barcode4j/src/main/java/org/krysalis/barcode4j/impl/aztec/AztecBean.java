/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.impl.aztec;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

import com.google.zxing.aztec.encoder.AztecCode;
import org.krysalis.barcode4j.tools.ECIUtil;

import static com.google.zxing.aztec.encoder.Encoder.DEFAULT_EC_PERCENT;

/**
 * This class is an implementation of the Aztec barcode. It's a 2D barcode, similar to QR Codes but without
 * the need for a quiet-zone.
 * <p>
 * Aztec can support all 256 ASCII characters (0-255) of ISO-8859-1 (Latin-1) and can range in size from 15x15
 * to 151x151 depending on the amount of data contained. Like QR codes, Aztec supports error correction using
 * the Reed Solomon algorithm.
 * </p>
 *
 * @since 2.3.2
 * @author Samael Bate (singingbush)
 */
public class AztecBean extends AbstractBarcodeBean {

    static final double DEFAULT_MODULE_WIDTH = 1.8;

    private int errorCorrectionLevel = DEFAULT_EC_PERCENT;
    private int layers = Encoder.DEFAULT_AZTEC_LAYERS;
    private String encoding = StandardCharsets.ISO_8859_1.name();

    public AztecBean() {
        setHeight(0.0); // not used by 2D barcodes
        setModuleWidth(DEFAULT_MODULE_WIDTH);
        doQuietZone(false); // Aztec barcodes do not require a quiet-zone
    }

    public int getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }

    public void setErrorCorrectionLevel(int errorCorrectionLevel) {
        if (errorCorrectionLevel < 23 || errorCorrectionLevel > 99) {
            throw new IllegalArgumentException(String.format("Error Correction must be a percentage value of at least 23. The default is %s", DEFAULT_EC_PERCENT));
        }
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    public int getLayers() {
        return layers;
    }

    /**
     * Aztec Code can contain from 1 to 32 data layers. Most users can ignore this parameter and let the encoder handle it
     * @param layers manually set the number of data layers for the barcode
     */
    public void setLayers(final int layers) {
        this.layers = layers;
    }

    /**
     * Sets the message encoding. The value must conform to one of Java's encodings and
     * have a mapping in the ECI registry.
     * @param encoding the message encoding
     */
    public void setEncoding(final String encoding) {
        if (ECIUtil.getECIForEncoding(encoding) < 0) {
            throw new IllegalArgumentException("Not a valid encoding: " + encoding);
        }
        this.encoding = encoding;
    }

    /**
     * Returns the message encoding.
     * @return the message encoding (default is "ISO-8859-1")
     */
    public String getEncoding() {
        return this.encoding != null ? this.encoding : StandardCharsets.ISO_8859_1.name();
    }

    /** {@inheritDoc} */
    @Override
    public void generateBarcode(@NotNull CanvasProvider canvas, @Nullable String msg) {
        if ((msg == null) || (msg.isEmpty())) {
            throw new IllegalArgumentException("Parameter msg must not be empty");
        }

        final BitMatrix matrix = generateAztecBarcodeMatrix(msg);

        applyMatrixToCanvas(new Canvas(canvas), msg, matrix);
    }

    /** {@inheritDoc} */
    @Override
    public BarcodeDimension calcDimensions(@NotNull String msg) {
        final BitMatrix matrix = generateAztecBarcodeMatrix(msg);

        int effWidth = matrix.getWidth();
        int effHeight = matrix.getHeight();

        double width = effWidth * getModuleWidth();
        double height = effHeight * getBarHeight();
        double qzh = (hasQuietZone() ? getQuietZone() : 0);
        double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);
        return new BarcodeDimension(width, height,
            width + (2 * qzh), height + (2 * qzv),
            qzh, qzv);
    }

    /** {@inheritDoc} */
    @Override
    public double getVerticalQuietZone() {
        return getQuietZone();
    }

    /** {@inheritDoc} */
    @Override
    public double getBarWidth(int width) {
        return this.getModuleWidth();
    }

    /** {@inheritDoc} */
    @Override
    public double getBarHeight() {
        return this.getModuleWidth();
    }

    private BitMatrix generateAztecBarcodeMatrix(@NotNull final String msg) {
        final AztecCode aztec = Encoder.encode(msg, this.errorCorrectionLevel, this.layers, Charset.forName(this.encoding));
        return aztec.getMatrix();
    }

    private void applyMatrixToCanvas(
        @NotNull final Canvas canvas,
        @NotNull final String msg,
        @NotNull final BitMatrix matrix
    ) {
        final DefaultTwoDimCanvasLogicHandler handler = new DefaultTwoDimCanvasLogicHandler(this, canvas);

        final int width = matrix.getWidth();
        final int height = matrix.getHeight();

        handler.startBarcode(msg, msg);
        for (int y = 0; y < height; y++) {
            handler.startRow();
            for (int x = 0; x < width; x++) {
                handler.addBar(matrix.get(x, y), 1);
            }
            handler.endRow();
        }
        handler.endBarcode();
    }
}
