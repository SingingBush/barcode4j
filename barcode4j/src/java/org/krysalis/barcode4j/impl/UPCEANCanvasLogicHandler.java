/*
 * $Id: UPCEANCanvasLogicHandler.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
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

import java.util.Stack;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Logic Handler implementation for painting on a Canvas. This is a special
 * implementation for UPC and EAN barcodes.
 * 
 * @author Jeremias Maerki
 */
public class UPCEANCanvasLogicHandler implements ClassicBarcodeLogicHandler {
    
    private UPCEAN bcImpl;
    private Canvas canvas;
    private double x = 0.0;
    private BarcodeDimension dim;
    private String msg;
    private String lastgroup;
    private double groupx;
    private boolean inMsgGroup;
    private boolean inSupplemental;
    private Stack groupStack = new Stack();
    
    /**
     * Main constructor.
     * @param bcImpl the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public UPCEANCanvasLogicHandler(GenericBarcodeImpl bcImpl, Canvas canvas) {
        if (!(bcImpl instanceof UPCEAN)) {
            throw new IllegalArgumentException("This LogicHandler can only be "
                + "used with UPC and EAN barcode implementations");
        }
        this.bcImpl = (UPCEAN)bcImpl;
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
        this.dim = bcImpl.calcDimensions(msg);
        
        canvas.establishDimensions(dim);
        x = getStartX();
        inMsgGroup = false;
        inSupplemental = false;
        
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarGroup(BarGroup type, String submsg) {
        if (type == BarGroup.UPC_EAN_GUARD) {
            //nop
        } else if (type == BarGroup.UPC_EAN_GROUP) {
            inMsgGroup = true;
            groupx = x;
            lastgroup = submsg;
        } else if (type == BarGroup.UPC_EAN_LEAD) {
            lastgroup = submsg;
        } else if (type == BarGroup.UPC_EAN_CHECK) {
            if (!inMsgGroup) {
                lastgroup = submsg;
            }
        } else if (type == BarGroup.UPC_EAN_SUPP) {
            inSupplemental = true;
            x += bcImpl.getQuietZone();
            groupx = x;
        }
        groupStack.push(type);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void addBar(boolean black, int width) {
        final double w = bcImpl.getBarWidth(width);
        if (black) {
            final double h;
            final double y;
            if (!inSupplemental) {
                if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    canvas.drawRectWH(x, 0, w, bcImpl.getHeight());
                } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    if (inMsgGroup) {
                        h = bcImpl.getBarHeight();
                        y = bcImpl.getHumanReadableHeight();
                    } else {
                        h = bcImpl.getBarHeight() + (bcImpl.getHumanReadableHeight() / 2);
                        y = bcImpl.getHumanReadableHeight() / 2;
                    }
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    if (inMsgGroup) {
                        h = bcImpl.getBarHeight();
                    } else {
                        h = bcImpl.getBarHeight() + (bcImpl.getHumanReadableHeight() / 2);
                    }
                    canvas.drawRectWH(x, 0.0, w, h);
                }
            } else {
                //Special painting in supplemental
                if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    h = bcImpl.getBarHeight();
                    y = bcImpl.getHumanReadableHeight();
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    h = bcImpl.getBarHeight() 
                        + (bcImpl.getHumanReadableHeight() / 2)
                        - bcImpl.getHumanReadableHeight();
                    y = bcImpl.getHumanReadableHeight() / 2;
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    h = bcImpl.getBarHeight() 
                        + (bcImpl.getHumanReadableHeight() / 2)
                        - bcImpl.getHumanReadableHeight();
                    y = bcImpl.getHumanReadableHeight();
                    canvas.drawRectWH(x, y, w, h);
                }
            }
        }
        x += w;
    }
    
    private boolean isEAN() {
        return (bcImpl instanceof EAN13) || (bcImpl instanceof EAN8);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarGroup() {
        BarGroup group = (BarGroup)groupStack.pop();

        if (group == BarGroup.UPC_EAN_GROUP) {
            inMsgGroup = false;
            if (lastgroup == null) {
                //Guards don't set the lastgroup variable
                return;
            }
            int colonPos = lastgroup.indexOf(":");
            String grouptext = lastgroup;
            if (colonPos >= 0) {
                String lead = new Character(grouptext.charAt(0)).toString();
                drawLeadChar(lead);
                grouptext = grouptext.substring(colonPos + 1);
            }
    
            //character group text
            drawGroupText(grouptext);
        } else if (group == BarGroup.UPC_EAN_LEAD) {
            if (!isEAN()) {
                drawLeadChar(lastgroup);
            }
        } else if (group == BarGroup.UPC_EAN_CHECK) {
            if (!isEAN()) {
                drawTrailingChar(lastgroup);
            }
        } else if (group == BarGroup.UPC_EAN_SUPP) {
            drawSupplementalText(UPCEANLogicImpl.retrieveSupplemental(this.msg));
            inSupplemental = false;
        }
    }
    
    private void drawLeadChar(String lead) {
        final double leadw = 7 * bcImpl.getBarWidth(1);
        final double leadx = getStartX() 
                    - 3 * bcImpl.getBarWidth(1)
                    - leadw;
                    
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawCenteredText(canvas, lead, leadx, leadx + leadw, 
                    bcImpl.getHumanReadableHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawCenteredText(canvas, lead, leadx, leadx + leadw, 
                    bcImpl.getHeight());
        }
    }

    private void drawTrailingChar(String trailer) {
        final double trailerw = 7 * bcImpl.getBarWidth(1);
        final double trailerx = getStartX()
                    + this.dim.getWidth()
                    - bcImpl.supplementalWidth(this.msg)
                    + 3 * bcImpl.getBarWidth(1);
                    
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawCenteredText(canvas, 
                    trailer, trailerx, trailerx + trailerw, 
                    bcImpl.getHumanReadableHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawCenteredText(canvas, 
                    trailer, trailerx, trailerx + trailerw, 
                    bcImpl.getHeight());
        }
    }

    private void drawGroupText(String text) {
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawJustifiedText(canvas, text, 
                    groupx + bcImpl.getBarWidth(1), 
                    x - bcImpl.getBarWidth(1), 
                    bcImpl.getHumanReadableHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawJustifiedText(canvas, text, 
                    groupx + bcImpl.getBarWidth(1), 
                    x - bcImpl.getBarWidth(1), 
                    bcImpl.getHeight());
        }
    }
    
    private void drawSupplementalText(String supp) {
        if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            bcImpl.drawCenteredText(canvas, supp, 
                    groupx, 
                    x, 
                    bcImpl.getHeight());
        } else if (bcImpl.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            bcImpl.drawCenteredText(canvas, supp, 
                    groupx, 
                    x, 
                    bcImpl.getHumanReadableHeight());
        }
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarcode() {
    }

}

