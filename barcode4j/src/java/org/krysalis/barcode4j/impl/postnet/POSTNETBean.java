/*
 * Copyright 2003,2004 Jeremias Maerki.
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
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.impl.DefaultHeightVariableLogicHandler;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * Implements the United States Postal Service POSTNET barcode.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNETBean.java,v 1.1 2004-09-12 17:57:51 jmaerki Exp $
 */
public class POSTNETBean extends HeightVariableBarcodeBean {

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    private double intercharGapWidth = moduleWidth;
    private BaselineAlignment baselinePosition = BaselineAlignment.ALIGN_BOTTOM;
    private double shortBarHeight = 1.25f;
    
    /** Create a new instance. */
    public POSTNETBean() {
        super();
        this.msgPos = HumanReadablePlacement.HRP_NONE; //Different default than normal
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
    
    /**
     * @see org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator#getBarWidth(int)
     */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == -1) {
            return this.intercharGapWidth;
        } else throw new IllegalArgumentException("Only width 1 allowed");
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeBean#getBarHeight(int)
     */
    public double getBarHeight(int height) {
        if (height == 2) {
            return getBarHeight();
        } else if (height == 1) {
            return shortBarHeight;
        } else if (height == -1) {
            return getBarHeight();  // doesn't matter since it's blank
        } else throw new IllegalArgumentException("Only height 0 or 1 allowed");
    }
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#generateBarcode(CanvasProvider, String)
     */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        DefaultHeightVariableLogicHandler handler = 
                new DefaultHeightVariableLogicHandler(this, new Canvas(canvas));

        POSTNETLogicImpl impl = new POSTNETLogicImpl(getChecksumMode());
        impl.generateBarcodeLogic(handler, msg);
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        String modMsg = POSTNETLogicImpl.removeIgnoredCharacters(msg);
        final double width = (((modMsg.length() * 5) + 2) * moduleWidth) 
                + (((modMsg.length() * 5) + 1) * intercharGapWidth);
        final double qz = (hasQuietZone() ? quietZone : 0);
        double height = getHeight();
        if (getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            height -= getHumanReadableHeight();
        }
        return new BarcodeDimension(width, height, 
                width + (2 * qz), height, 
                quietZone, 0.0);
    }

    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeBean#getBaselinePosition()
     */
    public BaselineAlignment getBaselinePosition() {
        return baselinePosition;
    }

    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeBean#setBaselinePosition(int)
     */
    public void setBaselinePosition(BaselineAlignment baselinePosition) {
        this.baselinePosition = baselinePosition;
    }

}