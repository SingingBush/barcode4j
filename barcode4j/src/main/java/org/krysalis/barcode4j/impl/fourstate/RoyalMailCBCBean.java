/*
 * Copyright 2006,2008 Jeremias Maerki.
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
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * Implements the Royal Mail Customer Barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: RoyalMailCBCBean.java,v 1.3 2008-05-13 13:00:43 jmaerki Exp $
 */
public class RoyalMailCBCBean extends AbstractFourStateBean {

    /** The default module width for RoyalMail. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.53; //mm

    /** Create a new instance. */
    public RoyalMailCBCBean() {
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

        FourStateLogicHandler handler = 
                new FourStateLogicHandler(this, new Canvas(canvas));

        RoyalMailCBCLogicImpl impl = new RoyalMailCBCLogicImpl(
                getChecksumMode());
        impl.generateBarcodeLogic(handler, msg);
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        String modMsg = RoyalMailCBCLogicImpl.removeStartStop(msg);
        int additional = (getChecksumMode() == ChecksumMode.CP_ADD 
                || getChecksumMode() == ChecksumMode.CP_AUTO) ? 1 : 0;
        final int len = modMsg.length() + additional;
        final double width = (((len * 4) + 2) * moduleWidth) 
                + (((len * 4) + 1) * getIntercharGapWidth());
        final double qzh = (hasQuietZone() ? getQuietZone() : 0);        
        final double qzv = (hasQuietZone() ? getVerticalQuietZone() : 0);        
        return new BarcodeDimension(width, getBarHeight(), 
                width + (2 * qzh), getBarHeight() + (2 * qzv), 
                qzh, qzv);
    }

}