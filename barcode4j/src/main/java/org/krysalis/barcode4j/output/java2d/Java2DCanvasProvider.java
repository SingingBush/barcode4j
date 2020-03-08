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
package org.krysalis.barcode4j.output.java2d;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * CanvasProvider implementation that renders to Java2D (AWT).
 * 
 * @author Jeremias Maerki
 * @version $Id: Java2DCanvasProvider.java,v 1.7 2008-05-13 13:00:46 jmaerki Exp $
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
    public Java2DCanvasProvider(Graphics2D g2d, int orientation) {
        super(orientation);
        setGraphics2D(g2d);
    }
    
    /**
     * Sets the Graphics2D instance to paint on
     * @param g2d the Graphics2D instance
     */
    public void setGraphics2D(Graphics2D g2d) {
        this.g2d = g2d;
    }

    /**
     * Returns the Graphics2D in use.
     * @return the Graphics2D instance to paint on
     */
    public Graphics2D getGraphics2D() {
        return this.g2d;
    }
    
    /** {@inheritDoc} */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        int orientation = BarcodeDimension.normalizeOrientation(getOrientation());
        double w = dim.getWidthPlusQuiet(orientation);
        double h = dim.getHeightPlusQuiet(orientation);
        this.g2d = (Graphics2D)this.g2d.create();
        switch (orientation) {
        case 90:
            g2d.rotate(-Math.PI / 2);
            g2d.translate(-h, 0);
            break;
        case 180:
            g2d.rotate(-Math.PI);
            g2d.translate(-w, -h);
            break;
        case 270:
            g2d.rotate(-Math.PI * 1.5);
            g2d.translate(0, -w);
            break;
        default:
            //nop
        }
    }

    /** {@inheritDoc} */
    public void deviceFillRect(double x, double y, double w, double h) {
        g2d.fill(new Rectangle2D.Double(x, y, w, h));
    }

    /** {@inheritDoc} */
    public void deviceDrawRect(double x, double y, double w, double h) {
        g2d.draw(new Rectangle2D.Double(x, y, w, h));
    }

    /** {@inheritDoc} */
    public void deviceText(
            String text,
            double x1,
            double x2,
            double y1,
            String fontName,
            double fontSize,
            TextAlignment textAlign) {
        if (DEBUG) {
            System.out.println("deviceText " + x1 + " " + x2 + " " 
                    + (x2 - x1) + " " + y1 + " " + text);
            System.out.println("fontSize: " 
                    + fontSize + "mm (" + UnitConv.mm2pt(fontSize) + "pt)");
        }
        Font font = new Font(fontName, Font.PLAIN, 
            (int)Math.round(fontSize));
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
        if (textAlign == TextAlignment.TA_JUSTIFY) {
            if (text.length() > 1) {
                indent = 0.0f;
            } else {
                indent = distributableSpace / 2; //Center if only one character
            }
        } else if (textAlign == TextAlignment.TA_CENTER) {
            indent = distributableSpace / 2;
        } else if (textAlign == TextAlignment.TA_RIGHT) {
            indent = distributableSpace;
        } else {
            indent = 0.0f;
        }
        Font oldFont = g2d.getFont();
        g2d.setFont(font);
        if (textAlign == TextAlignment.TA_JUSTIFY) {
            //move the individual glyphs
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
            g2d.draw(new Rectangle2D.Double(x1, y1 - fontSize, 
                x2 - x1, fontSize));
            g2d.draw(new Rectangle2D.Double(x1 + indent, 
                y1 - fontSize, 
                textwidth, fontSize));
        }
    }

}
