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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * Drawing utilities.
 * 
 * @author Jeremias Maerki
 */
public class DrawingUtil {

    /**
     * Draws a centered character on a canvas.
     * @param canvas the canvas to paint on
     * @param bean the barcode bean to get the font settings from
     * @param ch the character
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    public static void drawCenteredChar(Canvas canvas, AbstractBarcodeBean bean, 
                                    char ch, 
                                    double x1, double x2, double y1) {
        canvas.drawCenteredChar(ch, x1, x2, 
                y1 - UnitConv.pt2mm(bean.getFontSize()) * 0.2, 
                bean.getFontName(), bean.getFontSize());
    }

    /**
     * Draws justified text on a canvas.
     * @param canvas the canvas to paint on
     * @param bean the barcode bean to get the font settings from
     * @param text the text to paint
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    public static void drawJustifiedText(Canvas canvas, AbstractBarcodeBean bean,
                                    String text, 
                                    double x1, double x2, double y1) {
        canvas.drawJustifiedText(text, x1, x2, 
                y1 - UnitConv.pt2mm(bean.getFontSize()) * 0.2, 
                bean.getFontName(), bean.getFontSize());
    }

    /**
     * Draws centered text on a canvas.
     * @param canvas the canvas to paint on
     * @param bean the barcode bean to get the font settings from
     * @param text the text to paint
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    public static void drawCenteredText(Canvas canvas, AbstractBarcodeBean bean, 
                                    String text, 
                                    double x1, double x2, double y1) {
        canvas.drawCenteredText(text, x1, x2, 
                y1 - UnitConv.pt2mm(bean.getFontSize()) * 0.2, 
                bean.getFontName(), bean.getFontSize());
    }

    
}
