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

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * Base class for most barcode implementations.
 * 
 * @author Jeremias Maerki
 * @version $Id: GenericBarcodeImpl.java,v 1.3 2004-09-04 20:25:54 jmaerki Exp $
 */
public abstract class GenericBarcodeImpl 
            implements BarcodeGenerator, Configurable {

    /** Net height of bars in mm */
    protected double height          = 15.0; //mm
    /** Width of narrow module in mm */
    protected double moduleWidth;
    /** Position of human-readable text */
    protected HumanReadablePlacement msgPos = HumanReadablePlacement.HRP_BOTTOM;
    /** Font size in mm */
    protected double fontSize        = UnitConv.pt2mm(8); //pt
    /** Font name */
    protected String fontName        = "Helvetica"; //"OCR-B,Helvetica,Arial";
    /** True if quiet zone should be rendered */
    protected boolean doQuietZone    = true;
    /** Width of the quiet zone left and right of the barcode in mm */
    protected double quietZone;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Human-readable placement
        this.msgPos = HumanReadablePlacement.byName(
            cfg.getChild("human-readable").getValue(HumanReadablePlacement.HRP_BOTTOM.getName()));

        Length fs = new Length(cfg.getChild("human-readable-size").getValue("8pt"));
        this.fontSize = fs.getValueAsMillimeter();

        /* this does not seem to work */
        this.fontName = cfg.getChild("human-readable-font").getValue("Helvetica");
        
        //Height (must be evaluated after the font size because of setHeight())
        Length h = new Length(cfg.getChild("height").getValue("15mm"), "mm");
        this.setHeight(h.getValueAsMillimeter());
        
        //Quiet zone
        this.doQuietZone = cfg.getChild("quiet-zone").getAttributeAsBoolean("enabled", true);
        Length qz = new Length(cfg.getChild("quiet-zone").getValue("10mw"), "mw");
        if (qz.getUnit().equalsIgnoreCase("mw")) {
            this.quietZone = qz.getValue() * getModuleWidth();
        } else {
            this.quietZone = qz.getValueAsMillimeter();
        }
    }
    
    /**
     * Returns the height of the human-readable part.
     * @return the height of the human-readable part (in mm)
     */
    public double getHumanReadableHeight() {
        double textHeight = this.fontSize;
        return 1.0 * textHeight;
    }

    /**
     * Returns the height of the bars.
     * @return the height of the bars (in mm)
     */
    public double getBarHeight() {
        return this.height;
    }

    /**
     * Returns the full height of the barcode.
     * @return the full height (in mm)
     */
    public double getHeight() {
        return getBarHeight() + getHumanReadableHeight();
    }

    /**
     * Sets the height of the bars.
     * @param height the height of the bars (in mm)
     */
    public void setBarHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the full height of the barcode.
     * @param height the full height (in mm)
     */
    public void setHeight(double height) {
        this.height = height - getHumanReadableHeight();
    }

    /**
     * Returns the width of the narrow module.
     * @return the width of the narrow module (in mm)
     */
    public double getModuleWidth() {
        return this.moduleWidth;
    }

    /**
     * Sets the width of the narrow module.
     * @param width the width of the narrow module (in mm)
     */
    public void setModuleWidth(double width) {
        this.moduleWidth = width;
    }
    
    /**
     * Returns the effective width of a bar with a given logical width.
     * @param width the logical width (1=narrow, 2=wide)
     * @return the effective width of a bar (in mm)
     */
    public abstract double getBarWidth(int width);

    /**
     * Indicates whether a quiet zone is included.
     * @return true if a quiet zone is included
     */
    public boolean hasQuietZone() {
        return this.doQuietZone;
    }

    /**
     * Controls whether a quiet zone should be included or not.
     * @param value true if a quiet zone should be included
     */
    public void doQuietZone(boolean value) {
        this.doQuietZone = value;
    }

    /**
     * Returns the width of the quiet zone.
     * @return the width of the quiet zone (in mm)
     */
    public double getQuietZone() {
        return this.quietZone;
    }

    /**
     * Returns the placement of the human-readable part.
     * @return the placement of the human-readable part
     */
    public HumanReadablePlacement getMsgPosition() {
        return this.msgPos;
    }

    /**
     * Sets the placement of the human-readable part.
     * @param placement the placement of the human-readable part
     */
    public void setMsgPosition(HumanReadablePlacement placement) {
        this.msgPos = placement;
    }

    /**
     * Draws a centered character on a canvas.
     * @param canvas the canvas to paint on
     * @param ch the character
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    protected void drawCenteredChar(Canvas canvas, char ch, 
                                    double x1, double x2, double y1) {
        canvas.drawCenteredChar(ch, x1, x2, y1 - UnitConv.pt2mm(fontSize) * 0.2, 
                fontName, fontSize);
    }

    /**
     * Draws justified text on a canvas.
     * @param canvas the canvas to paint on
     * @param text the text to paint
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    protected void drawJustifiedText(Canvas canvas, String text, 
                                    double x1, double x2, double y1) {
        canvas.drawJustifiedText(text, x1, x2, y1 - UnitConv.pt2mm(fontSize) * 0.2, 
                fontName, fontSize);
    }

    /**
     * Draws centered text on a canvas.
     * @param canvas the canvas to paint on
     * @param text the text to paint
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate of the font's baseline
     */
    protected void drawCenteredText(Canvas canvas, String text, 
                                    double x1, double x2, double y1) {
        canvas.drawCenteredText(text, x1, x2, y1 - UnitConv.pt2mm(fontSize) * 0.2, 
                fontName, fontSize);
    }

    /** @see org.krysalis.barcode4j.BarcodeGenerator */
    public abstract void generateBarcode(CanvasProvider canvas, String msg);
    
    /** @see org.krysalis.barcode4j.BarcodeGenerator */
    public BarcodeDimension calcDimensions(String msg) {
        throw new UnsupportedOperationException("NYI");
    }
}
