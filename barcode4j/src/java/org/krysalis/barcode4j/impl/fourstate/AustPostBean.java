/*
 * Copyright 2006-2009 Antun Oreskovic.
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
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * Implements the Australia Post Barcode.
 * 
 * @author Antun Oreskovic
 * @version $Id: AustPostBean.java 2008/12/30 $
 */
public class AustPostBean extends AbstractFourStateBean {

    /** The default module width for AustPost. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.4; //mm

    /** Create a new instance. */
    public AustPostBean() {
        super();
        this.msgPos = HumanReadablePlacement.HRP_NONE; //Different default than normal
        setModuleWidth(DEFAULT_MODULE_WIDTH);
        setTrackHeight(1.25f); //mm
        setAscenderHeight(1.8f); //mm
        setQuietZone(2.0); //mm
        setIntercharGapWidth(getModuleWidth());
        updateHeight();
    }
    
    /** {@inheritDoc} */
    public void setMsgPosition(HumanReadablePlacement placement) {
        //nop, no human-readable with this symbology!!!
    }

    /** {@inheritDoc} */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) 
                || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        FourStateAustPostLogicHandler handler = 
                new FourStateAustPostLogicHandler(this, new Canvas(canvas));

        AustPostLogicImpl impl = new AustPostLogicImpl(
                getChecksumMode());
        impl.generateBarcodeLogic(handler, msg);
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        int len = getBarLength(msg.substring(0,2));
        final double width = (((len * 4) + 2) * moduleWidth) 
                + (((len * 4) + 1) * getIntercharGapWidth());
        final double qzh = (hasQuietZone() ? getQuietZone() : 0);        
        final double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);
        return new BarcodeDimension(width, getBarHeight(), 
                width + (2 * qzh), getBarHeight() + (2 * qzv), 
                qzh, qzv);
    }
    
    private int getBarLength(String fcc) {
        int ifcc = Integer.parseInt(fcc);
        switch (ifcc) {
        case 11:
        case 87:
        case 45:
        case 92:
            return 13;
        case 59:
            return 15;
        case 62:
        case 44:
            return 18;
        default:
            return 13;
        }
    }

    /** {@inheritDoc} 
     *  Need to override from Fourstate.
     *  height 0 and 3 are reversed with Auspost 
     */
    public double getBarHeight(int height) {
        switch (height) {
        case 0: return getTrackHeight() + (2 * getAscenderHeight());
        case 1: return getTrackHeight() + getAscenderHeight();
        case 2: return getTrackHeight() + getAscenderHeight();
        case 3: return getTrackHeight();
        default: throw new IllegalArgumentException("Only height 0-3 allowed");
        }
    }
}