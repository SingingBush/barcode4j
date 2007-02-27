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
package org.krysalis.barcode4j.impl.pdf417;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultTwoDimCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * This class is an implementation of the PDF417 barcode.
 * 
 * @version $Id: PDF417Bean.java,v 1.2 2007-02-27 20:55:29 jmaerki Exp $
 */
public class PDF417Bean extends AbstractBarcodeBean {

    /** The default module width for PDF417. */
    protected static final double DEFAULT_MODULE_WIDTH = UnitConv.in2mm(1.0 / 72); //1px at 72dpi

    /** The default wide factor for PDF417. */
    protected static final int DEFAULT_X_TO_Y_FACTOR = 3;

    /** The default column count for PDF417. */
    protected static final int DEFAULT_COLUMN_COUNT = 2;

    protected static final int DEFAULT_ERROR_CORRECTION_LEVEL = 0;

    private int errorCorrectionLevel = DEFAULT_ERROR_CORRECTION_LEVEL;
    private int cols = DEFAULT_COLUMN_COUNT;
    private Double quietZoneVertical;
    
    /** Create a new instance. */
    public PDF417Bean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        this.height = DEFAULT_X_TO_Y_FACTOR * moduleWidth;
        this.quietZone = 2 * moduleWidth;
    }
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#generateBarcode(CanvasProvider, String)
     */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        TwoDimBarcodeLogicHandler handler = 
                new DefaultTwoDimCanvasLogicHandler(this, new Canvas(canvas));

        PDF417LogicImpl impl = new PDF417LogicImpl();
        impl.generateBarcodeLogic(handler, msg, 
                getColumns(), getErrorCorrectionLevel());
    }
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        int k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(getErrorCorrectionLevel()); 
        String highLevel = PDF417HighLevelEncoder.encodeHighLevel(msg);
        int m = highLevel.length();
        int r = PDF417LogicImpl.getNumberOfRows(m, k, getColumns());
        
        double width = (17 * getColumns() + 69) * getModuleWidth();
        double height = (getBarHeight() * r); 
        double qzh = (hasQuietZone() ? getQuietZone() : 0);        
        double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);        
        return new BarcodeDimension(width, height, 
                width + (2 * qzh), height + (2 * qzv), 
                qzh, qzv);
    }

    /**
     * Sets the error correction level for the barcode.
     * @param level the error correction level (a value between 0 and 8)
     */
    public void setErrorCorrectionLevel(int level) {
        if (level < 0 || level > 8) {
            throw new IllegalArgumentException("Error correction level must be between 0 and 8!");
        }
        this.errorCorrectionLevel = level;
    }
    
    /** @return the error correction level (0-8) */
    public int getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }
    
    /**
     * Sets the number of data columns for the barcode. The number of rows will automatically
     * be determined based on the amount of data.
     * @param cols the number of columns
     */
    public void setColumns(int cols) {
        this.cols = cols;
    }
    
    /** @return the number of data columns to produce */
    public int getColumns() {
        return this.cols;
    }
    
    /**
     * Sets the height of the vertical quiet zone. If this value is not explicitely set the
     * vertical quiet zone has the same width as the horizontal quiet zone.
     * @param height the height of the vertical quiet zone (in mm)
     */
    public void setVerticalQuietZone(double height) {
        this.quietZoneVertical = new Double(height);
    }
    
    /** @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#getVerticalQuietZone() */
    public double getVerticalQuietZone() {
        if (this.quietZoneVertical != null) {
            return this.quietZoneVertical.doubleValue();
        } else {
            return getQuietZone();
        }
    }

    /** @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#getBarWidth(int) */
    public double getBarWidth(int width) {
        return width * moduleWidth;
    }
    
    /** @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#getBarHeight() */
    public double getBarHeight() {
        return this.height;
    }
    
}