/*
 * $Id: POSTNET.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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