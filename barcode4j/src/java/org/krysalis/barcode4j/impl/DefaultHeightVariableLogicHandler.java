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
 * Logic Handler to be used by subclasses of HeightVariableBarcodeBean 
 * for painting on a Canvas.
 * 
 * @author Chris Dolphy
 * @version $Id: DefaultHeightVariableLogicHandler.java,v 1.5 2004-10-24 11:45:37 jmaerki Exp $
 */
public class DefaultHeightVariableLogicHandler 
            implements ClassicBarcodeLogicHandler {


    private HeightVariableBarcodeBean bcBean;
    private Canvas canvas;
    private double x = 0.0;
    //private int baselinePos;
    private String formattedMsg;

    /**
     * Constructor 
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultHeightVariableLogicHandler(HeightVariableBarcodeBean bcBean, Canvas canvas) {
        this.bcBean = bcBean;
        this.canvas = canvas;
    }

    private double getStartX() {
        if (bcBean.hasQuietZone()) {
            return bcBean.getQuietZone();
        } else {
            return 0.0;
        }
    }            

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#startBarcode(String, String)
     */
    public void startBarcode(String msg, String formattedMsg) {
        this.formattedMsg = formattedMsg;
        //Calculate extents
        BarcodeDimension dim = bcBean.calcDimensions(msg);       
        canvas.establishDimensions(dim);        
        x = getStartX();
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#addBar(boolean, int)
     */
    public void addBar(boolean black, int height) {
        final double w = black ? bcBean.getBarWidth(1) : bcBean.getBarWidth(-1);
        final double h = bcBean.getBarHeight(height);
        final BaselineAlignment baselinePosition = bcBean.getBaselinePosition();
        
        if (black) {
            if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                if (baselinePosition == BaselineAlignment.ALIGN_TOP) {
                    canvas.drawRectWH(x, bcBean.getHumanReadableHeight(), w, h);
                } else if (baselinePosition == BaselineAlignment.ALIGN_BOTTOM) {
                    canvas.drawRectWH(x, bcBean.getHeight() - h, w, h);
                }
            } else {
                if (baselinePosition == BaselineAlignment.ALIGN_TOP) {
                    canvas.drawRectWH(x, 0, w, h);
                } else if (baselinePosition == BaselineAlignment.ALIGN_BOTTOM) {
                    canvas.drawRectWH(x, bcBean.getBarHeight() - h, w, h);
                } 
            }
        }
        x += w;
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#endBarcode()
     */
    public void endBarcode() {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawCenteredText(canvas, bcBean, formattedMsg, 
                    getStartX(), x, bcBean.getHumanReadableHeight());
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawCenteredText(canvas, bcBean, formattedMsg, 
                    getStartX(), x, bcBean.getHeight());
        }
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    public void startBarGroup(BarGroup barGroup, String string) {
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#endBarGroup()
     */
    public void endBarGroup() {
    }

}
