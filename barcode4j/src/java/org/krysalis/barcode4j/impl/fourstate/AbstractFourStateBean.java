/*
 * Copyright 2006-2008 Jeremias Maerki.
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

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;

/**
 * Abstract base class for four state barcode beans.
 * 
 * @author Jeremias Maerki
 * @version $Id: AbstractFourStateBean.java,v 1.1 2008-05-13 13:00:43 jmaerki Exp $
 */
public abstract class AbstractFourStateBean extends HeightVariableBarcodeBean {

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    private double intercharGapWidth;
    private double trackHeight;
    private double ascenderHeight;
    
    /** Create a new instance. */
    public AbstractFourStateBean() {
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
        switch (height) {
        case 0: return trackHeight;
        case 1: return trackHeight + ascenderHeight;
        case 2: return trackHeight + ascenderHeight;
        case 3: return trackHeight + (2 * ascenderHeight);
        default: throw new IllegalArgumentException("Only height 0-3 allowed");
        }
    }
    
}