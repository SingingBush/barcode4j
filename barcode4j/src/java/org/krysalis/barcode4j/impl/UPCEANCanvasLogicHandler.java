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
 * @version $Id: UPCEANCanvasLogicHandler.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
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

