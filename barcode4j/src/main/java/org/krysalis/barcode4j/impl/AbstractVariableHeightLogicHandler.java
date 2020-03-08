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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.tools.MessagePatternUtil;

/**
 * Logic Handler to be used by subclasses of HeightVariableBarcodeBean 
 * for painting on a Canvas.
 * 
 * @author Chris Dolphy
 * @version $Id: AbstractVariableHeightLogicHandler.java,v 1.3 2008-05-13 13:00:45 jmaerki Exp $
 */
public abstract class AbstractVariableHeightLogicHandler 
            implements ClassicBarcodeLogicHandler {

    /** the barcode bean */
    protected HeightVariableBarcodeBean bcBean;
    /** the canvas to paint on */
    protected Canvas canvas;
    /** the cursor in x-direction */
    protected double x = 0.0;
    /** the cursor in y-direction */
    protected double y = 0.0;
    private String formattedMsg;
    private TextAlignment textAlignment = TextAlignment.TA_CENTER;

    /**
     * Constructor 
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public AbstractVariableHeightLogicHandler(HeightVariableBarcodeBean bcBean, Canvas canvas) {
        this.bcBean = bcBean;
        this.canvas = canvas;
    }

    /**
     * Sets the alignment of the human-readable part.
     * @param align the new alignment
     */
    public void setTextAlignment(TextAlignment align) {
        if (align == null) {
            throw new NullPointerException("align must not be null");
        }
        this.textAlignment = align;
    }
    
    private double getStartX() {
        if (bcBean.hasQuietZone()) {
            return bcBean.getQuietZone();
        } else {
            return 0.0;
        }
    }            

    /** {@inheritDoc} */
    public void startBarcode(String msg, String formattedMsg) {
        this.formattedMsg = MessagePatternUtil.applyCustomMessagePattern(
                formattedMsg, bcBean.getPattern());
        //Calculate extents
        BarcodeDimension dim = bcBean.calcDimensions(msg);       
        canvas.establishDimensions(dim);        
        x = getStartX();
    }

    /**
     * Determines the Y coordinate for the baseline of the human-readable part.
     * @return the adjusted Y coordinate
     */
    protected double getTextY() {
        double texty = 0.0;
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            texty += bcBean.getHumanReadableHeight();
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            texty += bcBean.getHeight();
            if (bcBean.hasQuietZone()) {
                texty += bcBean.getVerticalQuietZone();
            }
        }
        return texty;
    }
    
    /** {@inheritDoc} */
    public void endBarcode() {
        if (bcBean.getMsgPosition() != HumanReadablePlacement.HRP_NONE) {
            double texty = getTextY();
            DrawingUtil.drawText(canvas, bcBean, formattedMsg, 
                    getStartX(), x, texty, this.textAlignment);
        }
    }

    /** {@inheritDoc} */
    public void startBarGroup(BarGroup barGroup, String string) {
    }

    /** {@inheritDoc} */
    public void endBarGroup() {
    }

}
