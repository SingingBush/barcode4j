/*
 * Copyright 2002-2005,2007 Jeremias Maerki
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
import org.krysalis.barcode4j.tools.MessagePatternUtil;

/**
 * Default Logic Handler implementation for painting on a Canvas.
 *
 * @version $Id: DefaultCanvasLogicHandler.java,v 1.7 2007-01-19 12:26:55 jmaerki Exp $
 */
public class DefaultCanvasLogicHandler implements ClassicBarcodeLogicHandler {

    private AbstractBarcodeBean bcBean;
    private Canvas canvas;
    private double x = 0.0;
    private String formattedMsg;
    private String lastgroup;

    /**
     * Main constructor.
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultCanvasLogicHandler(AbstractBarcodeBean bcBean, Canvas canvas) {
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

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarcode(String msg, String formattedMsg) {
        this.formattedMsg = MessagePatternUtil.applyCustomMessagePattern(
                formattedMsg, bcBean.getPattern());
        
        //Calculate extents
        BarcodeDimension dim = bcBean.calcDimensions(msg);

        canvas.establishDimensions(dim);
        x = getStartX();
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarGroup(BarGroup type, String submsg) {
        this.lastgroup = submsg;
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void addBar(boolean black, int width) {
        final double w = bcBean.getBarWidth(width);
        if (black) {
            if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                canvas.drawRectWH(x, 0, w, bcBean.getHeight());
            } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                canvas.drawRectWH(x, bcBean.getHumanReadableHeight(), w, bcBean.getBarHeight());
            } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                canvas.drawRectWH(x, 0, w, bcBean.getBarHeight());
            }
        }
        x += w;
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarGroup() {
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarcode() {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            double ty = bcBean.getHumanReadableHeight();
            if (bcBean.hasFontDescender()) {
                ty -= bcBean.getHumanReadableHeight() / 13 * 3;
            }
            DrawingUtil.drawCenteredText(canvas, bcBean, formattedMsg,
                    getStartX(), x, ty);
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            double ty = bcBean.getHeight();
            if (bcBean.hasFontDescender()) {
                ty -= bcBean.getHumanReadableHeight() / 13 * 3;
            }
            DrawingUtil.drawCenteredText(canvas, bcBean, formattedMsg,
                    getStartX(), x, ty);
        }
    }

}

