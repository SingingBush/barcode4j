/*
 * Copyright 2003,2004,2006 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.postnet;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * Implements the United States Postal Service POSTNET barcode.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNETBean.java,v 1.9 2008-05-13 13:00:44 jmaerki Exp $
 */
public class POSTNETBean extends HeightVariableBarcodeBean {

    /** The default module width for POSTNET. */
    static final double DEFAULT_MODULE_WIDTH = 0.020f; //inch
    static final double DEFAULT_TALL_BAR_HEIGHT = 0.125f; //inch
    static final double DEFAULT_SHORT_BAR_HEIGHT = 0.050f; //inch
    
    static final double DEFAULT_HORZ_QUIET_ZONE_INCH = 1.0 / 8; // 1/8 inch
    static final double DEFAULT_VERT_QUIET_ZONE_INCH = 1.0 / 25; // 1/25 inch

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    private double intercharGapWidth;
    private BaselineAlignment baselinePosition = BaselineAlignment.ALIGN_BOTTOM;
    private double shortBarHeight = UnitConv.in2mm(DEFAULT_SHORT_BAR_HEIGHT);
    private boolean displayChecksum = false;
    private Double quietZoneVertical;
    
    /** Create a new instance. */
    public POSTNETBean() {
        super();
        this.msgPos = HumanReadablePlacement.HRP_NONE; //Different default than normal
        this.moduleWidth = UnitConv.in2mm(DEFAULT_MODULE_WIDTH);
        this.intercharGapWidth = this.moduleWidth;
        setQuietZone(UnitConv.in2mm(DEFAULT_HORZ_QUIET_ZONE_INCH));
        setVerticalQuietZone(UnitConv.in2mm(DEFAULT_VERT_QUIET_ZONE_INCH));
        setBarHeight(UnitConv.in2mm(DEFAULT_TALL_BAR_HEIGHT));
    }
    
    /**
     * Sets the height of the vertical quiet zone. If this value is not explicitely set the
     * vertical quiet zone has the same width as the horizontal quiet zone.
     * @param height the height of the vertical quiet zone (in mm)
     */
    public void setVerticalQuietZone(double height) {
        this.quietZoneVertical = new Double(height);
    }
    
    /** {@inheritDoc} */
    public double getVerticalQuietZone() {
        if (this.quietZoneVertical != null) {
            return this.quietZoneVertical.doubleValue();
        } else {
            return getQuietZone();
        }
    }

    /**
     * Sets the checksum mode
     * @param mode the checksum mode
     */
    public void setChecksumMode(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the current checksum mode.
     * @return ChecksumMode the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Returns the width between encoded characters.
     * @return the interchar gap width
     */
    public double getIntercharGapWidth() {
        return this.intercharGapWidth;
    }
    
    /**
     * Sets the width between encoded characters.
     * @param width the interchar gap width
     */
    public void setIntercharGapWidth(double width) {
        this.intercharGapWidth = width;
    }
    
    /**
     * Returns the height of a short bar.
     * @return the height of a short bar
     */
    public double getShortBarHeight() {
        return this.shortBarHeight;
    }
    
    /**
     * Sets the height of a short bar.
     * @param height the height of a short bar
     */
    public void setShortBarHeight(double height) {
        this.shortBarHeight = height;
    }
    
    /** {@inheritDoc} */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == -1) {
            return this.intercharGapWidth;
        } else {
            throw new IllegalArgumentException("Only width 1 allowed");
        }
    }
    
    /** {@inheritDoc} */
    public double getBarHeight(int height) {
        if (height == 2) {
            return getBarHeight();
        } else if (height == 1) {
            return shortBarHeight;
        } else if (height == -1) {
            return getBarHeight();  // doesn't matter since it's blank
        } else {
            throw new IllegalArgumentException("Only height 0 or 1 allowed");
        }
    }
    
    /**
     * Indicates whether the checksum will be displayed as
     * part of the human-readable message.
     * @return true if checksum will be included in the human-readable message
     */
    public boolean isDisplayChecksum() {
        return this.displayChecksum;
    }
    
    /**
     * Enables or disables the use of the checksum in the
     * human-readable message.
     * @param value true to include the checksum in the human-readable message, 
     *   false to ignore
     */
    public void setDisplayChecksum(boolean value) {
        this.displayChecksum = value;
    }
    
    /** {@inheritDoc} */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        POSTNETLogicHandler handler = 
                new POSTNETLogicHandler(this, new Canvas(canvas));

        POSTNETLogicImpl impl = new POSTNETLogicImpl(
                getChecksumMode(), isDisplayChecksum());
        impl.generateBarcodeLogic(handler, msg);
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        String modMsg = POSTNETLogicImpl.removeIgnoredCharacters(msg);
        final double width = (((modMsg.length() * 5) + 2) * moduleWidth) 
                + (((modMsg.length() * 5) + 1) * intercharGapWidth);
        final double qz = (hasQuietZone() ? quietZone : 0);
        double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);        
        double height = getHeight();
        if (getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            height -= getHumanReadableHeight();
        }
        return new BarcodeDimension(width, height, 
                width + (2 * qz), height + (2 * qzv), 
                quietZone, qzv);
    }

    /**
     * Returns the baseline position. Indicates whether the bars are top-align or bottom-aligned.
     * @return the baseline position
     */
    public BaselineAlignment getBaselinePosition() {
        return baselinePosition;
    }

    /**
     * Sets the baseline position. Indicates whether the bars are top-align or bottom-aligned.
     * @param baselinePosition the baseline position
     */
    public void setBaselinePosition(BaselineAlignment baselinePosition) {
        this.baselinePosition = baselinePosition;
    }

}