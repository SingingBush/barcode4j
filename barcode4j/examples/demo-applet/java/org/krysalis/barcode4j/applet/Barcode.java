/*
 * Copyright 2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.applet;

import java.awt.Graphics2D;

/**
 * Sample Swing component for painting barcodes in Swing applications.
 * 
 * @author Jeremias Maerki
 */
public class Barcode extends AbstractBarcode {

    protected void transformAsNecessary(Graphics2D g2d) {
        if (getBarcodeDimension() != null) {
            double horzScale = getWidth() / getBarcodeDimension().getWidthPlusQuiet();
            double vertScale = getHeight() / getBarcodeDimension().getHeightPlusQuiet();
            double scale;
            double dx = 0;
            double dy = 0;
            if (horzScale < vertScale) {
                scale = horzScale;
                dy = ((getHeight() / scale) - getBarcodeDimension().getHeightPlusQuiet()) / 2;
            } else {
                scale = vertScale;
                dx = ((getWidth() / scale) - getBarcodeDimension().getWidthPlusQuiet()) / 2;
            }
            g2d.scale(scale, scale); //scale for mm to screen pixels
            g2d.translate(dx, dy); //center
        }
    }
    
}
