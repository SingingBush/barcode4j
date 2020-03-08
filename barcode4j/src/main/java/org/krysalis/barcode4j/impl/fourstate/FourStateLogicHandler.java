/*
 * Copyright 2006,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.fourstate;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.AbstractVariableHeightLogicHandler;
import org.krysalis.barcode4j.impl.HeightVariableBarcodeBean;
import org.krysalis.barcode4j.output.Canvas;

/**
 * Logic Handler to be used by "four-state" barcodes 
 * for painting on a Canvas.
 * 
 * @author Jeremias Maerki
 * @version $Id: FourStateLogicHandler.java,v 1.2 2008-05-13 13:00:43 jmaerki Exp $
 */
public class FourStateLogicHandler 
            extends AbstractVariableHeightLogicHandler {

    /**
     * Constructor 
     * @param bcBean the barcode implementation class
     * @param canvas the canvas to paint to
     */
    public FourStateLogicHandler(HeightVariableBarcodeBean bcBean, Canvas canvas) {
        super(bcBean, canvas);
    }

    private double getStartY() {
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
    public void addBar(boolean black, int height) {
        final double w = bcBean.getBarWidth(1);
        final double h = bcBean.getBarHeight(height);
        
        final double middle = bcBean.getBarHeight() / 2;
        double y1;
        switch (height) {
        case 0:
        case 2:
            y1 = middle - (bcBean.getBarHeight(0) / 2);
            break;
        case 1:
        case 3:
            y1 = middle - (bcBean.getBarHeight(3) / 2);
            break;
        default:
            throw new RuntimeException("Bug!");
        }
        
        canvas.drawRectWH(x, getStartY() + y1, w, h);
        x += w + bcBean.getBarWidth(-1);
    }

}
