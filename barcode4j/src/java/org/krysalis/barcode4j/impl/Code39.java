/*
 * Copyright 2002-2004 Jeremias Maerki.
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
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;

/**
 * This class is an implementation of the Code39 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: Code39.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class Code39 extends GenericBarcodeImpl 
            implements Configurable {

    /** The default wide factor for Code 39 */
    protected static final double DEFAULT_WIDE_FACTOR = 2.5;

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private double  intercharGapWidth = moduleWidth;
    private double  wideFactor = DEFAULT_WIDE_FACTOR; //Width of binary one

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.19mm"), "mm");
        this.moduleWidth = mw.getValueAsMillimeter();

        super.configure(cfg);
    
        //Checksum mode    
        this.checksumMode = ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName()));
    
        //Wide factor    
        this.wideFactor = cfg.getChild("wide-factor").getValueAsFloat((float)DEFAULT_WIDE_FACTOR);
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue("1mw"), "mw");
        if (igw.getUnit().equalsIgnoreCase("mw")) {
            this.intercharGapWidth = igw.getValue() * getModuleWidth();
        } else {
            this.intercharGapWidth = igw.getValueAsMillimeter();
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
     * @see org.krysalis.barcode4j.impl.GenericBarcodeImpl#getBarWidth(int)
     */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == 2) {
            return moduleWidth * wideFactor;
        } else if (width == -1) {
            return this.intercharGapWidth;
        } else throw new IllegalArgumentException("Only widths 1 and 2 allowed");
    }
    
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#generateBarcode(CanvasProvider, String)
     */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        ClassicBarcodeLogicHandler handler = 
                new DefaultCanvasLogicHandler(this, new Canvas(canvas));

        Code39LogicImpl impl = new Code39LogicImpl(getChecksumMode());
        impl.generateBarcodeLogic(handler, msg);
    }


    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        final double width = ((msg.length() + 2) * (3 * wideFactor + 6) * moduleWidth) 
                + ((msg.length() + 1) * intercharGapWidth);
        final double qz = (hasQuietZone() ? quietZone : 0);
        return new BarcodeDimension(width, getHeight(), 
                width + (2 * qz), getHeight(), 
                quietZone, 0.0);
    }

}