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

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Logic Handler to be used by subclasses of HeightVariableBarcodeImpl 
 * for painting on a Canvas.
 * 
 * @author Chris Dolphy
 * @version $Id: DefaultHeightVariableLogicHandler.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class DefaultHeightVariableLogicHandler 
            implements ClassicBarcodeLogicHandler {


    private HeightVariableBarcodeImpl bcImpl;
    private Canvas canvas;
    private double x = 0.0;
    //private int baselinePos;
    private String msg;

    /**
     * Constructor 
     * @param bcImpl the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultHeightVariableLogicHandler(HeightVariableBarcodeImpl bcImpl, Canvas canvas) {
        this.bcImpl = bcImpl;
        this.canvas = canvas;
    }

    private double getStartX() {
        if (bcImpl.hasQuietZone()) {
            return bcImpl.getQuietZone();
        } else {
            return 0.0;
        }
    }            

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#startBarcode(String)
     */
    public void startBarcode(String msg) {
        this.msg = msg;
        //Calculate extents
        BarcodeDimension dim = bcImpl.calcDimensions(msg);       
        canvas.establishDimensions(dim);        
        x = getStartX();
    }

    /**
     * @see org.krysalis.barcode4j.HeightVariableLogicHandler#addBar(boolean, int)
     */
    public void addBar(boolean black, int height) {
        final double w = black ? bcImpl.getBarWidth(1) : bcImpl.getBarWidth(-1);
        final double h = bcImpl.getBarHeight(height);
        final BaselineAlignment baselinePosition = bcImpl.getBaselinePosition();
        
        if (black) {
            if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                if (baselinePosition == BaselineAlignment.ALIGN_TOP) {
                    canvas.drawRectWH(x, bcImpl.getHumanReadableHeight(), w, h);
                } else if (baselinePosition == BaselineAlignment.ALIGN_BOTTOM) {
                    canvas.drawRectWH(x, bcImpl.getHeight() - h, w, h);
                }
            } else {
                if (baselinePosition == BaselineAlignment.ALIGN_TOP) {
                    canvas.drawRectWH(x, 0, w, h);
                } else if (baselinePosition == BaselineAlignment.ALIGN_BOTTOM) {
                    canvas.drawRectWH(x, bcImpl.getBarHeight() - h, w, h);
                } 
            }
        }
        x += w;
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#endBarcode()
     */
    public void endBarcode() {
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawCenteredText(canvas, msg, getStartX(), x, bcImpl.getHumanReadableHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawCenteredText(canvas, msg, getStartX(), x, bcImpl.getHeight());
        }
    }

    /**
     * @see org.krysalis.barcode4j.HeightVariableLogicHandler#startBarGroup(BarGroup, String)
     */
    public void startBarGroup(BarGroup barGroup, String string) {
    }

    /**
     * @see org.krysalis.barcode4j.HeightVariableLogicHandler#endBarGroup()
     */
    public void endBarGroup() {
    }

}
