/*
 * $Id: DefaultHeightVariableLogicHandler.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
