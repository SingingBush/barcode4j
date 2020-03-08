/*
 * Copyright 2002-2004,2006,2008 Jeremias Maerki.
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
 * This class is used by barcode rendering classes that paint a barcode using 
 * a coordinate system. The class delegates the call to a CanvasProvider and
 * provides some convenience methods.
 * 
 * @author Jeremias Maerki
 * @version $Id: Canvas.java,v 1.4 2008-05-13 13:00:45 jmaerki Exp $
 */
public class Canvas {

    private CanvasProvider canvasImp;

    /**
     * Main constructor
     * @param canvasImp the canvas provider to use
     */
    public Canvas(CanvasProvider canvasImp) {
        this.canvasImp = canvasImp;
    }

    /**
     * Returns the canvas provider in use.
     * @return the canvas provider
     */
    public CanvasProvider getCanvasImp() {
        return canvasImp;
    }

    /**
     * Sets the dimensions of the barcode.
     * @param dim the barcode dimensions
     */
    public void establishDimensions(BarcodeDimension dim) {
        getCanvasImp().establishDimensions(dim);
    }
    
    /**
     * @return the orientation of the barcode (0, 90, 180, 270, -90, -180, -270)
     */
    public int getOrientation() {
        return getCanvasImp().getOrientation();
    }

    /**
     * Draws a rectangle.
     * @param x1 x coordinate of the upper left corner
     * @param y1 y coordinate of the upper left corner
     * @param x2 x coordinate of the lower right corner
     * @param y2 y coordinate of the lower right corner
     */
    public void drawRect(double x1, double y1, double x2, double y2) {
        drawRectWH(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Draws a rectangle
     * @param x x coordinate of the upper left corner
     * @param y y coordinate of the upper left corner
     * @param w the width
     * @param h the height
     */
    public void drawRectWH(double x, double y, double w, double h) {
        getCanvasImp().deviceFillRect(x, y, w, h);
    }

    /**
     * Draws a centered character.
     * @param ch the character
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     */
    public void drawCenteredChar(char ch, double x1, double x2, double y1, 
                String fontName, double fontSize) {
        drawCenteredText(new Character(ch).toString(), 
                x1, x2, y1, 
                fontName, fontSize);
    }

    /**
     * Draws justified text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     * @deprecated Use {@link #drawText(String, double, double, double, String, double, TextAlignment)} instead.
     */
    public void drawJustifiedText(String text, double x1, double x2, double y1, 
                String fontName, double fontSize) {
        drawText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_JUSTIFY);
    }

    /**
     * Draws centered text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     * @deprecated Use {@link #drawText(String, double, double, double, String, double, TextAlignment)} instead.
     */
    public void drawCenteredText(String text, double x1, double x2, double y1, 
                String fontName, double fontSize) {
        drawText(text, x1, x2, y1, fontName, fontSize, TextAlignment.TA_CENTER);
    }

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
    public void drawText(String text, double x1, double x2, double y1, 
                String fontName, double fontSize, TextAlignment textAlign) {
        getCanvasImp().deviceText(text, 
                x1, x2, y1, 
                fontName, fontSize, textAlign);
    }

}