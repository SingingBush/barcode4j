/*
 * $Id: Java2DCanvasProvider.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.output.java2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * CanvasProvider implementation that renders to Java2D (AWT).
 * 
 * @author Jeremias Maerki
 */
public class Java2DCanvasProvider extends AbstractCanvasProvider {

    private static final boolean DEBUG = false; 

    private Graphics2D g2d;

    /**
     * Creates a new Java2DCanvasProvider.
     * <p>
     * This class internally operates with millimeters (mm) as units. This
     * means you have to apply the necessary transformation before rendering
     * a barcode to obtain the expected size. See the source code for 
     * BitmapBuilder.java for an example.
     * <p>
     * To improve the quality of text output it is recommended that fractional
     * font metrics be enabled on the Graphics2D object passed in:
     * <br>
     * <code>
     * g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
     * RenderingHints.VALUE_FRACTIONALMETRICS_ON);
     * </code>
     * @param g2d Graphics2D object to paint on
     */
    public Java2DCanvasProvider(Graphics2D g2d) {
        this.g2d = g2d;
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceFillRect(double x, double y, double w, double h) {
        g2d.fill(new Rectangle2D.Double(x, y, w, h));
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceDrawRect(double x, double y, double w, double h) {
        g2d.draw(new Rectangle2D.Double(x, y, w, h));
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceJustifiedText(String text, double x1, double x2, double y1,
                            String fontName, double fontSize) {
        deviceCenteredText(text, x1, x2, y1, fontName, fontSize, true);
    }
                            
    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceCenteredText(String text, double x1, double x2, double y1,
                            String fontName, double fontSize) {
        deviceCenteredText(text, x1, x2, y1, fontName, fontSize, false);
    }
                            
    /**
     * Draws centered text.
     * @param text the text to draw
     * @param x1 the left boundary
     * @param x2 the right boundary
     * @param y1 the y coordinate
     * @param fontName the name of the font
     * @param fontSize the size of the font
     * @param justify true if the text should be justified instead of centered
     */
    public void deviceCenteredText(
            String text,
            double x1,
            double x2,
            double y1,
            String fontName,
            double fontSize,
            boolean justify) {
        if (DEBUG) {
            System.out.println("deviceText " + x1 + " " + x2 + " " 
                    + (x2 - x1) + " " + y1 + " " + text);
            System.out.println("fontSize: " 
                    + fontSize + "pt (" + UnitConv.pt2mm(fontSize) + "mm)");
        }
        Font font = new Font(fontName, Font.PLAIN, 
            (int)Math.round(UnitConv.pt2mm(fontSize)));
        FontRenderContext frc = g2d.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, text);
        
        final float textwidth = (float)gv.getLogicalBounds().getWidth();
        final float distributableSpace = (float)((x2 - x1) - textwidth);
        final float intercharSpace;
        if (gv.getNumGlyphs() > 1) {
            intercharSpace = distributableSpace / (gv.getNumGlyphs() - 1);
        } else {
            intercharSpace = 0.0f;
        }
        if (DEBUG) {
            System.out.println(gv.getLogicalBounds()
                    + " " + gv.getVisualBounds());
            System.out.println("textwidth=" + textwidth);
            System.out.println("distributableSpace=" + distributableSpace);
            System.out.println("intercharSpace=" + intercharSpace);
        }
        final float indent;
        if (justify && text.length() > 1) {
            indent = 0.0f;
        } else {
            indent = distributableSpace / 2;
        }
        Font oldFont = g2d.getFont();
        g2d.setFont(font);
        if (justify) {
            //move the individual glyphs
            float rx = 0.0f;
            for (int i = 0; i < gv.getNumGlyphs(); i++) {
                Point2D point = gv.getGlyphPosition(i);
                point.setLocation(point.getX() + i * intercharSpace, point.getY());
                gv.setGlyphPosition(i, point);
                if (DEBUG) {
                    System.out.println(i + " " + point 
                            + " " + gv.getGlyphLogicalBounds(i).getBounds2D());
                    System.out.println(i + " " + text.substring(i, i + 1) 
                        + " " + gv.getGlyphMetrics(i).getBounds2D());
                }
            }
        }
        g2d.drawGlyphVector(gv, (float)x1 + indent, (float)y1);
        g2d.setFont(oldFont);
        if (DEBUG) {
            g2d.setStroke(new BasicStroke(0.01f));
            g2d.draw(new Rectangle2D.Double(x1, y1 - UnitConv.pt2mm(fontSize), 
                x2 - x1, UnitConv.pt2mm(fontSize)));
            g2d.draw(new Rectangle2D.Double(x1 + indent, 
                y1 - UnitConv.pt2mm(fontSize), 
                textwidth, UnitConv.pt2mm(fontSize)));
        }
    }

}
