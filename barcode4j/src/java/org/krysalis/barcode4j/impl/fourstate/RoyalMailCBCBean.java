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
package org.krysalis.barcode4j.impl.fourstate;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * Implements the Royal Mail Customer Barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: RoyalMailCBCBean.java,v 1.2 2006-12-22 15:55:45 jmaerki Exp $
 */
public class RoyalMailCBCBean extends HeightVariableBarcodeBean {

    /** The default module width for RoyalMail. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.53; //mm

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    private double intercharGapWidth;
    private double trackHeight = 1.25f; //mm
    private double ascenderHeight = 1.8f; //mm
    
    /** Create a new instance. */
    public RoyalMailCBCBean() {
        super();
        this.msgPos = HumanReadablePlacement.HRP_NONE; //Different default than normal
        this.quietZone = 2.0; //mm
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        this.intercharGapWidth = this.moduleWidth;
        updateHeight();
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#setMsgPosition(
     *          org.krysalis.barcode4j.HumanReadablePlacement)
     */
    public void setMsgPosition(HumanReadablePlacement placement) {
        //nop, no human-readable with this symbology!!!
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

    /** @return the height of the vertical quiet zone (in mm) */
    public double getVerticalQuietZone() {
        return getQuietZone(); //Same as horizontal
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
     * Returns the height of the track.
     * @return the height of the track
     */
    public double getTrackHeight() {
        return this.trackHeight;
    }
    
    /**
     * Sets the height of the track.
     * @param height the height of the track
     */
    public void setTrackHeight(double height) {
        this.trackHeight = height;
        updateHeight();
    }
    
    /**
     * Returns the height of the ascender/descender.
     * @return the height of the ascender/descender
     */
    public double getAscenderHeight() {
        return this.ascenderHeight;
    }
    
    /**
     * Sets the height of the ascender/descender.
     * @param height the height of the ascender/descender
     */
    public void setAscenderHeight(double height) {
        this.ascenderHeight = height;
        updateHeight();
    }
    
    /**
     * Updates the height variable of the barcode.
     */
    protected void updateHeight() {
        setBarHeight(getTrackHeight() + (2 * getAscenderHeight()));
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#getBarWidth(int)
     */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == -1) {
            return this.intercharGapWidth;
        } else {
            throw new IllegalArgumentException("Only width 1 allowed");
        }
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeBean#getBarHeight(int)
     */
    public double getBarHeight(int height) {
        switch (height) {
        case 0: return trackHeight;
        case 1: return trackHeight + ascenderHeight;
        case 2: return trackHeight + ascenderHeight;
        case 3: return trackHeight + (2 * ascenderHeight);
        default: throw new IllegalArgumentException("Only height 0-3 allowed");
        }
    }
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#generateBarcode(CanvasProvider, String)
     */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        FourStateLogicHandler handler = 
                new FourStateLogicHandler(this, new Canvas(canvas));

        RoyalMailCBCLogicImpl impl = new RoyalMailCBCLogicImpl(
                getChecksumMode());
        impl.generateBarcodeLogic(handler, msg);
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        String modMsg = RoyalMailCBCLogicImpl.removeStartStop(msg);
        int additional = (getChecksumMode() == ChecksumMode.CP_ADD 
                || getChecksumMode() == ChecksumMode.CP_AUTO) ? 1 : 0;
        final int len = modMsg.length() + additional;
        final double width = (((len * 4) + 2) * moduleWidth) 
                + (((len * 4) + 1) * intercharGapWidth);
        final double qz = (hasQuietZone() ? quietZone : 0);
        return new BarcodeDimension(width, getBarHeight(), 
                width + (2 * qz), getBarHeight() + (2 * qz), 
                quietZone, quietZone);
    }

}