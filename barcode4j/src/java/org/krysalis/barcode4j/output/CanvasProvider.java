/*
 * Copyright 2002-2004,2006 Jeremias Maerki.
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
package org.krysalis.barcode4j.output;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;

/**
 * This interface is implemented to provide an output format that is using
 * a coordinate system.
 * 
 * @author Jeremias Maerki
 * @version $Id: CanvasProvider.java,v 1.4 2008-05-13 13:00:45 jmaerki Exp $
 */
public interface CanvasProvider {

    /**
     * Sets the dimensions of the barcode.
     * @param dim the dimensions of the barcode
     */
    void establishDimensions(BarcodeDimension dim);

    /**
     * Returns the barcode dimensions once established.
     * @return the barcode dimensions
     */
    BarcodeDimension getDimensions();

    /**
     * Returns the orientation of the barcode.
     * @return the orientation (0, 90, 180 or 270)
     */
    int getOrientation();
    
    /**
     * Paints a filled rectangle.
     * @param x x coordinate of the upper left corner
     * @param y y coordinate of the upper left corner
     * @param w the width
     * @param h the height
     */
    void deviceFillRect(double x, double y, double w, double h);
    
    /**
     * Draws justified text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     */
    void deviceJustifiedText(String text, double x1, double x2, double y1, 
            String fontName, double fontSize);
            
    /**
     * Draws centered text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     */
    void deviceCenteredText(String text, double x1, double x2, double y1, 
            String fontName, double fontSize);

    /**
     * Draws text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     * @param textAlign the text alignment
     */
    void deviceText(String text, double x1, double x2, double y1, 
            String fontName, double fontSize, TextAlignment textAlign);

}