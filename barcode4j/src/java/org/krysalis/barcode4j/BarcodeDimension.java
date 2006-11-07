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
package org.krysalis.barcode4j;

import java.awt.geom.Rectangle2D;

/**
 * This class provides information on the dimensions of a barcode. It makes a
 * distinction between the dimensions with and without quiet zone.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeDimension.java,v 1.3 2006-11-07 16:43:37 jmaerki Exp $
 */
public class BarcodeDimension {
    
    private double width;
    private double height;
    
    private double widthPlusQuiet;
    private double heightPlusQuiet;
    private double xOffset;
    private double yOffset;
    
    /**
     * Creates a new BarcodeDimension object. No quiet-zone is respected.
     * @param w width of the barcode in millimeters (mm).
     * @param h height of the barcode in millimeters (mm).
     */
    public BarcodeDimension(double w, double h) {
        this.width = w;
        this.height = h;
        this.widthPlusQuiet = this.width;
        this.heightPlusQuiet = this.height;
        this.xOffset = 0.0;
        this.yOffset = 0.0;    
    }
    
    /**
     * Creates a new BarcodeDimension object.
     * @param w width of the raw barcode (without quiet-zone) in millimeters (mm).
     * @param h height of the raw barcode (without quiet-zone) in millimeters (mm).
     * @param wpq width of the barcode (quiet-zone included) in millimeters (mm).
     * @param hpq height of the barcode (quiet-zone included) in millimeters (mm).
     * @param xoffset x-offset if the upper-left corner of the barcode within 
     * the extended barcode area.
     * @param yoffset y-offset if the upper-left corner of the barcode within 
     * the extended barcode area.
     */
    public BarcodeDimension(double w, double h, 
                double wpq, double hpq, 
                double xoffset, double yoffset) {
        this.width = w;
        this.height = h;
        this.widthPlusQuiet = wpq;
        this.heightPlusQuiet = hpq;
        this.xOffset = xoffset;
        this.yOffset = yoffset;    
    }
    
    
    /**
     * Returns the height of the barcode (ignores quiet-zone).
     * @return height in millimeters (mm)
     */
    public double getHeight() {
        return height;
    }

    public double getHeight(int orientation) {
        orientation = normalizeOrientation(orientation);
        if (orientation % 180 != 0) {
            return getWidth();
        } else {
            return getHeight();
        }
    }
    
    /**
     * Returns the height of the barcode (quiet-zone included).
     * @return height in millimeters (mm)
     */
    public double getHeightPlusQuiet() {
        return heightPlusQuiet;
    }

    public double getHeightPlusQuiet(int orientation) {
        orientation = normalizeOrientation(orientation);
        if (orientation % 180 != 0) {
            return getWidthPlusQuiet();
        } else {
            return getHeightPlusQuiet();
        }
    }
    
    /**
     * Returns the width of the barcode (ignores quiet-zone).
     * @return width in millimeters (mm)
     */
    public double getWidth() {
        return width;
    }

    public static int normalizeOrientation(int orientation) {
        switch (orientation) {
        case 0:
            return 0;
        case 90:
        case -270:
            return 90;
        case 180:
        case -180:
            return 180;
        case 270:
        case -90:
            return 270;
        default:
            throw new IllegalArgumentException(
                    "Orientation must be 0, 90, 180, 270, -90, -180 or -270");
        }
    }
    
    public double getWidth(int orientation) {
        orientation = normalizeOrientation(orientation);
        if (orientation % 180 != 0) {
            return getHeight();
        } else {
            return getWidth();
        }
    }
    
    /**
     * Returns the width of the barcode (quiet-zone included).
     * @return width in millimeters (mm)
     */
    public double getWidthPlusQuiet() {
        return widthPlusQuiet;
    }

    public double getWidthPlusQuiet(int orientation) {
        orientation = normalizeOrientation(orientation);
        if (orientation % 180 != 0) {
            return getHeightPlusQuiet();
        } else {
            return getWidthPlusQuiet();
        }
    }
    
    /**
     * Returns the x-offset of the upper-left corner of the barcode within the 
     * extended barcode area.
     * @return double x-offset in millimeters (mm)
     */
    public double getXOffset() {
        return xOffset;
    }

    /**
     * Returns the y-offset of the upper-left corner of the barcode within the 
     * extended barcode area.
     * @return double y-offset in millimeters (mm)
     */
    public double getYOffset() {
        return yOffset;
    }

    /** @return a bounding rectangle (including quiet zone if applicable) */
    public Rectangle2D getBoundingRect() {
        Rectangle2D.Double r = new Rectangle2D.Double(
                0, 0, getWidthPlusQuiet(), getHeightPlusQuiet());
        return r;
    }
    
    /** @return a content rectangle (excluding quiet zone) */
    public Rectangle2D getContentRect() {
        Rectangle2D.Double r = new Rectangle2D.Double(
                getXOffset(), getYOffset(), getWidth(), getHeight());
        return r;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append("[width=");
        sb.append(getWidth());
        sb.append("(");
        sb.append(getWidthPlusQuiet());
        sb.append("),height=");
        sb.append(getHeight());
        sb.append("(");
        sb.append(getHeightPlusQuiet());
        sb.append(")]");
        return sb.toString();
    }
}
