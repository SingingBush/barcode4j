/*
 * $Id: Canvas.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.output;

import org.krysalis.barcode4j.BarcodeDimension;

/**
 * This class is used by barcode rendering classes that paint a barcode using 
 * a coordinate system. The class delegates the call to a CanvasProvider and
 * provides some convenience methods.
 * 
 * @author Jeremias Maerki
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
        getCanvasImp().deviceCenteredText(new Character(ch).toString(), 
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
     */
    public void drawJustifiedText(String text, double x1, double x2, double y1, 
                String fontName, double fontSize) {
        getCanvasImp().deviceJustifiedText(text, 
                x1, x2, y1, 
                fontName, fontSize);
    }

    /**
     * Draws centered text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     */
    public void drawCenteredText(String text, double x1, double x2, double y1, 
                String fontName, double fontSize) {
        getCanvasImp().deviceCenteredText(text, 
                x1, x2, y1, 
                fontName, fontSize);
    }

}