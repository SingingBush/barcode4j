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

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Default Logic Handler implementation for painting on a Canvas.
 * 
 * @author Jeremias Maerki
 * @version $Id: DefaultCanvasLogicHandler.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class DefaultCanvasLogicHandler implements ClassicBarcodeLogicHandler {
    
    private GenericBarcodeImpl bcImpl;
    private Canvas canvas;
    private double x = 0.0;
    private String msg;
    private String lastgroup;
    
    /**
     * Main constructor.
     * @param bcImpl the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultCanvasLogicHandler(GenericBarcodeImpl bcImpl, Canvas canvas) {
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

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarcode(String msg) {
        this.msg = msg;
        //Calculate extents
        BarcodeDimension dim = bcImpl.calcDimensions(msg);
        
        canvas.establishDimensions(dim);
        x = getStartX();
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarGroup(BarGroup type, String submsg) {
        this.lastgroup = submsg;
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void addBar(boolean black, int width) {
        final double w = bcImpl.getBarWidth(width);
        if (black) {
            if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                canvas.drawRectWH(x, 0, w, bcImpl.getHeight());
            } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                canvas.drawRectWH(x, bcImpl.getHumanReadableHeight(), w, bcImpl.getBarHeight());
            } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                canvas.drawRectWH(x, 0, w, bcImpl.getBarHeight());
            }
        }
        x += w;
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarGroup() {
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarcode() {
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawCenteredText(canvas, msg, getStartX(), x, bcImpl.getHumanReadableHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawCenteredText(canvas, msg, getStartX(), x, bcImpl.getHeight());
        }
    }

}

