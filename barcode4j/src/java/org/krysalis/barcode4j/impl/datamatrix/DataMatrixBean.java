/*
 * Copyright 2006 Jeremias Maerki.
 *
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
package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;
import java.io.IOException;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * This class is an implementation of DataMatrix (ISO 16022:2000(E)).
 *
 * @version $Id: DataMatrixBean.java,v 1.8 2011-05-04 08:29:03 jmaerki Exp $
 */
public class DataMatrixBean extends AbstractBarcodeBean {

    /** The default module width (dot size) for DataMatrix. */
    protected static final double DEFAULT_MODULE_WIDTH = UnitConv.in2mm(1.0 / 72); //1px at 72dpi

    /**
     * The requested shape. May be <code>FORCE_NONE</code>,
     * <code>FORCE_SQUARE</code> or <code>FORCE_RECTANGLE</code>.
     */
    private SymbolShapeHint shape;

    /** Optional: the minimum size of the symbol. */
    private Dimension minSize;
    /** Optional: the maximum size of the symbol. */
    private Dimension maxSize;

    /** Create a new instance. */
    public DataMatrixBean() {
        this.height = 0.0; //not used by DataMatrix
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        setQuietZone(1 * moduleWidth);
        this.shape = SymbolShapeHint.FORCE_NONE;
    }

    /**
     * Sets the requested shape for the generated barcodes.
     * @param shape requested shape. May be <code>SymbolShapeHint.FORCE_NONE</code>,
     * <code>SymbolShapeHint.FORCE_SQUARE</code> or <code>SymbolShapeHint.FORCE_RECTANGLE</code>.
     */
    public void setShape(SymbolShapeHint shape) {
        this.shape = shape;
    }

    /**
     * Gets the requested shape for the generated barcodes.
     * @return the requested shape (one of SymbolShapeHint.*).
     */
    public SymbolShapeHint getShape() {
        return shape;
    }

    /**
     * Sets the minimum symbol size that is to be produced.
     * @param minSize the minimum size (in pixels), or null for no constraint
     */
    public void setMinSize(Dimension minSize) {
        this.minSize = (minSize != null ? new Dimension(minSize) : null);
    }

    /**
     * Returns the minimum symbol size that is to be produced. If the method returns null,
     * there's no constraint on the symbol size.
     * @return the minimum symbol size (in pixels), or null if there's no size constraint
     */
    public Dimension getMinSize() {
        if (this.minSize != null) {
            return new Dimension(this.minSize);
        } else {
            return null;
        }
    }

    /**
     * Sets the maximum symbol size that is to be produced.
     * @param maxSize the maximum size (in pixels), or null for no constraint
     */
    public void setMaxSize(Dimension maxSize) {
        this.maxSize = (maxSize != null ? new Dimension(maxSize) : null);
    }

    /**
     * Returns the maximum symbol size that is to be produced. If the method returns null,
     * there's no constraint on the symbol size.
     * @return the maximum symbol size (in pixels), or null if there's no size constraint
     */
    public Dimension getMaxSize() {
        if (this.maxSize != null) {
            return new Dimension(this.maxSize);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null)
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        TwoDimBarcodeLogicHandler handler =
                new DefaultTwoDimCanvasLogicHandler(this, new Canvas(canvas));

        DataMatrixLogicImpl impl = new DataMatrixLogicImpl();
        impl.generateBarcodeLogic(handler, msg, getShape(), getMinSize(), getMaxSize());
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        String encoded;
        try {
            encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg,
                    shape, getMinSize(), getMaxSize());
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot fetch data: " + e.getLocalizedMessage());
        }
        DataMatrixSymbolInfo symbolInfo = DataMatrixSymbolInfo.lookup(encoded.length(), shape);

        double width = symbolInfo.getSymbolWidth() * getModuleWidth();
        double height = symbolInfo.getSymbolHeight() * getBarHeight();
        double qzh = (hasQuietZone() ? getQuietZone() : 0);
        double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);
        return new BarcodeDimension(width, height,
                width + (2 * qzh), height + (2 * qzv),
                qzh, qzv);
    }

    /** {@inheritDoc} */
    public double getVerticalQuietZone() {
        return getQuietZone();
    }

    /** {@inheritDoc} */
    public double getBarWidth(int width) {
        return moduleWidth;
    }

    /** {@inheritDoc} */
    public double getBarHeight() {
        return moduleWidth;
    }

}