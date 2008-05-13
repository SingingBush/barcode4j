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
package org.krysalis.barcode4j.impl.upcean;

import java.util.Stack;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DrawingUtil;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Logic Handler implementation for painting on a Canvas. This is a special
 * implementation for UPC and EAN barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCEANCanvasLogicHandler.java,v 1.3 2008-05-13 13:00:44 jmaerki Exp $
 */
public class UPCEANCanvasLogicHandler implements ClassicBarcodeLogicHandler {
    
    private UPCEANBean bcBean;
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
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public UPCEANCanvasLogicHandler(AbstractBarcodeBean bcBean, Canvas canvas) {
        if (!(bcBean instanceof UPCEANBean)) {
            throw new IllegalArgumentException("This LogicHandler can only be "
                + "used with UPC and EAN barcode implementations");
        }
        this.bcBean = (UPCEANBean)bcBean;
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
        this.msg = msg;
        //Calculate extents
        this.dim = bcBean.calcDimensions(msg);
        
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
            x += bcBean.getQuietZone();
            groupx = x;
        }
        groupStack.push(type);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void addBar(boolean black, int width) {
        final double w = bcBean.getBarWidth(width);
        if (black) {
            final double h;
            final double y;
            if (!inSupplemental) {
                if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    canvas.drawRectWH(x, 0, w, bcBean.getHeight());
                } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    if (inMsgGroup) {
                        h = bcBean.getBarHeight();
                        y = bcBean.getHumanReadableHeight();
                    } else {
                        h = bcBean.getBarHeight() + (bcBean.getHumanReadableHeight() / 2);
                        y = bcBean.getHumanReadableHeight() / 2;
                    }
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    if (inMsgGroup) {
                        h = bcBean.getBarHeight();
                    } else {
                        h = bcBean.getBarHeight() + (bcBean.getHumanReadableHeight() / 2);
                    }
                    canvas.drawRectWH(x, 0.0, w, h);
                }
            } else {
                //Special painting in supplemental
                if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
                    h = bcBean.getBarHeight();
                    y = bcBean.getHumanReadableHeight();
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
                    h = bcBean.getBarHeight() 
                        + (bcBean.getHumanReadableHeight() / 2)
                        - bcBean.getHumanReadableHeight();
                    y = bcBean.getHumanReadableHeight() / 2;
                    canvas.drawRectWH(x, y, w, h);
                } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
                    h = bcBean.getBarHeight() 
                        + (bcBean.getHumanReadableHeight() / 2)
                        - bcBean.getHumanReadableHeight();
                    y = bcBean.getHumanReadableHeight();
                    canvas.drawRectWH(x, y, w, h);
                }
            }
        }
        x += w;
    }
    
    private boolean isEAN() {
        return (bcBean instanceof EAN13Bean) || (bcBean instanceof EAN8Bean);
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
        final double leadw = 7 * bcBean.getBarWidth(1);
        final double leadx = getStartX() 
                    - 3 * bcBean.getBarWidth(1)
                    - leadw;
                    
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(canvas, bcBean, 
                    lead, leadx, leadx + leadw, 
                    bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(canvas, bcBean, 
                    lead, leadx, leadx + leadw, 
                    bcBean.getHeight(), TextAlignment.TA_CENTER);
        }
    }

    private void drawTrailingChar(String trailer) {
        final double trailerw = 7 * bcBean.getBarWidth(1);
        final double trailerx = getStartX()
                    + this.dim.getWidth()
                    - bcBean.supplementalWidth(this.msg)
                    + 3 * bcBean.getBarWidth(1);
                    
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(canvas, bcBean, 
                    trailer, trailerx, trailerx + trailerw, 
                    bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(canvas, bcBean, 
                    trailer, trailerx, trailerx + trailerw, 
                    bcBean.getHeight(), TextAlignment.TA_CENTER);
        }
    }

    private void drawGroupText(String text) {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_NONE) {
            //nop
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(canvas, bcBean, text, 
                    groupx + bcBean.getBarWidth(1), 
                    x - bcBean.getBarWidth(1), 
                    bcBean.getHumanReadableHeight(), TextAlignment.TA_JUSTIFY);
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(canvas, bcBean, text, 
                    groupx + bcBean.getBarWidth(1), 
                    x - bcBean.getBarWidth(1), 
                    bcBean.getHeight(), TextAlignment.TA_JUSTIFY);
        }
    }
    
    private void drawSupplementalText(String supp) {
        if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_TOP) {
            DrawingUtil.drawText(canvas, bcBean, supp, 
                    groupx, 
                    x, 
                    bcBean.getHeight(), TextAlignment.TA_CENTER);
        } else if (bcBean.getMsgPosition() == HumanReadablePlacement.HRP_BOTTOM) {
            DrawingUtil.drawText(canvas, bcBean, supp, 
                    groupx, 
                    x, 
                    bcBean.getHumanReadableHeight(), TextAlignment.TA_CENTER);
        }
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarcode() {
    }

}

