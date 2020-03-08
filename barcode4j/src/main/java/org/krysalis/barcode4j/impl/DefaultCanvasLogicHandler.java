/*
 * Copyright 2002-2005,2007-2008 Jeremias Maerki.
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
 * Default Logic Handler implementation for painting on a Canvas.
 *
 * @version $Id: DefaultCanvasLogicHandler.java,v 1.10 2009-03-11 10:42:15 jmaerki Exp $
 */
public class DefaultCanvasLogicHandler implements ClassicBarcodeLogicHandler {

    /** the barcode bean */
    protected AbstractBarcodeBean bcBean;
    /** the canvas to paint on */
    protected Canvas canvas;
    /** the barcode dimensions */
    protected BarcodeDimension dimensions;
    private double x = 0.0;
    private double y;
    private String formattedMsg;

    /**
     * Main constructor.
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public DefaultCanvasLogicHandler(AbstractBarcodeBean bcBean, Canvas canvas) {
        this.bcBean = bcBean;
        this.canvas = canvas;
    }

    /**
     * Returns the start X position of the bars.
     * @return the start X position of the bars.
     */
    protected double getStartX() {
        if (bcBean.hasQuietZone()) {
            return bcBean.getQuietZone();
        } else {
            return 0.0;
        }
    }

    /**
     * Returns the start Y position of the bars.
     * @return the start Y position of the bars.
     */
    protected double getStartY() {
        double y = 0.0;
        if (bcBean.hasQuietZone()) {
            y += bcBean.getVerticalQuietZone();
        }
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            y += bcBean.getHumanReadableHeight();
        }
        return y;
    }

    /** {@inheritDoc} */
    public void startBarcode(String msg, String formattedMsg) {
        this.formattedMsg = MessagePatternUtil.applyCustomMessagePattern(
                formattedMsg, bcBean.getPattern());

        //Calculate extents
        this.dimensions = bcBean.calcDimensions(msg);

        canvas.establishDimensions(dimensions);
        x = getStartX();
        y = getStartY();
    }

    /** {@inheritDoc} */
    public void startBarGroup(BarGroup type, String submsg) {
        //nop
    }

    /** {@inheritDoc} */
    public void addBar(boolean black, int width) {
        final double w = bcBean.getBarWidth(width);
        if (black) {
            canvas.drawRectWH(x, y, w, bcBean.getBarHeight());
        }
        x += w;
    }

    /** {@inheritDoc} */
    public void endBarGroup() {
    }

    /** {@inheritDoc} */
    public void endBarcode() {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else {
            double ty = getTextBaselinePosition();
            DrawingUtil.drawText(canvas, bcBean, formattedMsg,
                    getStartX(), x, ty, TextAlignment.TA_CENTER);
        }
    }

    /**
     * Returns the vertical text baseline position.
     * @return the vertical text baseline position
     */
    protected double getTextBaselinePosition() {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            double ty = bcBean.getHumanReadableHeight();
            if (bcBean.hasFontDescender()) {
                ty -= bcBean.getHumanReadableHeight() / 13 * 3;
            }
            return ty;
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            double ty = bcBean.getHeight();
            if (bcBean.hasFontDescender()) {
                ty -= bcBean.getHumanReadableHeight() / 13 * 3;
            }
            return ty;
        } else {
            throw new IllegalStateException("not applicable");
        }
    }

}

