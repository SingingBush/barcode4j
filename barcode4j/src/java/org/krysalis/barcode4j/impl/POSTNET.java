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
package org.krysalis.barcode4j.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;

/**
 * Implements the United States Postal Service POSTNET barcode.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNET.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class POSTNET extends HeightVariableBarcodeImpl 
            implements Configurable {

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    private double intercharGapWidth = moduleWidth;
    private BaselineAlignment baselinePosition = BaselineAlignment.ALIGN_BOTTOM;
    private double shortBarHeight = 1.25f;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.020in"), "mm");
        this.moduleWidth = mw.getValueAsMillimeter();

        super.configure(cfg);
    
        //Checksum mode    
        this.checksumMode = ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName()));
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue("0.026in"), "mm");
        this.intercharGapWidth = igw.getValueAsMillimeter();

        Length h = new Length(cfg.getChild("tall-bar-height").getValue("0.125in"), "mm");
        this.setBarHeight(h.getValueAsMillimeter());
        
        Length hbh = new Length(cfg.getChild("short-bar-height").getValue("0.050in"), "mm");
        this.shortBarHeight = hbh.getValueAsMillimeter();

        //Human-readable placement
        this.msgPos = HumanReadablePlacement.byName(
            cfg.getChild("human-readable").getValue(HumanReadablePlacement.HRP_NONE.getName()));

        this.baselinePosition = BaselineAlignment.byName(
            cfg.getChild("baseline-alignment").getValue(BaselineAlignment.ALIGN_BOTTOM.getName()));
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
     * @see org.krysalis.barcode4j.impl.GenericBarcodeImpl#getBarWidth(int)
     */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == -1) {
            return this.intercharGapWidth;
        } else throw new IllegalArgumentException("Only width 1 allowed");
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeImpl#getBarHeight(int)
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
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeImpl#getBaselinePosition()
     */
    public BaselineAlignment getBaselinePosition() {
        return baselinePosition;
    }

    /**
     * @see org.krysalis.barcode4j.impl.HeightVariableBarcodeImpl#setBaselinePosition(int)
     */
    public void setBaselinePosition(BaselineAlignment baselinePosition) {
        this.baselinePosition = baselinePosition;
    }

}