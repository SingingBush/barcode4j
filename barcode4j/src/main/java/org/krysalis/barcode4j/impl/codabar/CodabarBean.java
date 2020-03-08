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
package org.krysalis.barcode4j.impl.codabar;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * This class is an implementation of the Codabar barcode.
 *
 * @author Jeremias Maerki
 * @version $Id: CodabarBean.java,v 1.7 2008-11-22 09:57:10 jmaerki Exp $
 */
public class CodabarBean extends AbstractBarcodeBean {

    /** The default module width for Codabar. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.21f; //mm

    /** The default wide factor for Codabar. */
    protected static final double DEFAULT_WIDE_FACTOR = 3.0;

    /** The default display start/stop value for Codabar. */
    protected static final boolean DEFAULT_DISPLAY_START_STOP = false;

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private boolean displayStartStop = DEFAULT_DISPLAY_START_STOP;
    private double wideFactor = DEFAULT_WIDE_FACTOR; //Width of binary one

    /** Create a new instance. */
    public CodabarBean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        setQuietZone(10 * this.moduleWidth);
        setVerticalQuietZone(0); //1D barcodes don't have vertical quiet zones
    }

    /**
     * Returns the current checksum mode.
     * @return ChecksumMode the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Sets the checksum mode
     * @param mode the checksum mode
     */
    public void setChecksumMode(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the factor by which wide bars are broader than narrow bars.
     * @return the wide factor
     */
    public double getWideFactor() {
        return this.wideFactor;
    }

    /**
     * Sets the factor by which wide bars are broader than narrow bars.
     * @param value the wide factory (should be > 1.0)
     */
    public void setWideFactor(double value) {
        if (value <= 1.0) {
            throw new IllegalArgumentException("wide factor must be > 1.0");
        }
        this.wideFactor = value;
    }

    /** {@inheritDoc} */
    public double getBarWidth(int width) {
        if (width == 1) {
            return moduleWidth;
        } else if (width == 2) {
            return moduleWidth * wideFactor;
        } else {
            throw new IllegalArgumentException("Only widths 1 and 2 allowed");
        }
    }

    /**
     * Indicates whether the start and stop character will be displayed as
     * part of the human-readable message.
     * @return true if leading and trailing "*" will be displayed
     */
    public boolean isDisplayStartStop() {
        return this.displayStartStop;
    }

    /**
     * Enables or disables the use of the start and stop characters in the
     * human-readable message.
     * @param value true to enable the start/stop character, false to disable
     */
    public void setDisplayStartStop(boolean value) {
        this.displayStartStop = value;
    }

    /** {@inheritDoc} */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null)
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        ClassicBarcodeLogicHandler handler =
                new DefaultCanvasLogicHandler(this, new Canvas(canvas));

        CodabarLogicImpl impl = new CodabarLogicImpl(getChecksumMode(), isDisplayStartStop());
        impl.generateBarcodeLogic(handler, msg);
    }

    private double calcCharWidth(char c) {
        final int idx = CodabarLogicImpl.getCharIndex(c);
        if (idx >= 0) {
            int narrow = 0;
            int wide = 0;
            for (int i = 0; i < 7; i++) {
                final byte width = CodabarLogicImpl.CHARSET[idx][i];
                if (width == 0) {
                    narrow++;
                } else {
                    wide++;
                }
            }
            return (narrow * moduleWidth) + (wide * moduleWidth * wideFactor);
        } else {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        double width = 0.0;
        for (int i = 0; i < msg.length(); i++) {
            if (i > 0) {
                width += moduleWidth; //Intercharacter gap
            }
            width += calcCharWidth(msg.charAt(i));
        }
        final double qz = (hasQuietZone() ? quietZone : 0);
        return new BarcodeDimension(width, getHeight(),
                width + (2 * qz), getHeight(),
                quietZone, 0.0);
    }

}