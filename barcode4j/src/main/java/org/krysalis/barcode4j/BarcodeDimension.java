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

    private final double width;
    private final double height;

    private final double widthPlusQuiet;
    private final double heightPlusQuiet;
    private final double xOffset;
    private final double yOffset;

    /**
     * Creates a new BarcodeDimension object. No quiet-zone is respected.
     * @param width width of the barcode in millimeters (mm).
     * @param height height of the barcode in millimeters (mm).
     */
    public BarcodeDimension(double width, double height) {
        this.width = width;
        this.height = height;
        this.widthPlusQuiet = this.width;
        this.heightPlusQuiet = this.height;
        this.xOffset = 0.0;
        this.yOffset = 0.0;
    }

    /**
     * Creates a new BarcodeDimension object.
     * @param width width of the raw barcode (without quiet-zone) in millimeters (mm).
     * @param height height of the raw barcode (without quiet-zone) in millimeters (mm).
     * @param wpq width of the barcode (quiet-zone included) in millimeters (mm).
     * @param hpq height of the barcode (quiet-zone included) in millimeters (mm).
     * @param xOffset x-offset if the upper-left corner of the barcode within
     * the extended barcode area.
     * @param yOffset y-offset if the upper-left corner of the barcode within
     * the extended barcode area.
     */
    public BarcodeDimension(double width, double height,
                double wpq, double hpq,
                double xOffset, double yOffset) {
        this.width = width;
        this.height = height;
        this.widthPlusQuiet = wpq;
        this.heightPlusQuiet = hpq;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }


    /**
     * Returns the height of the barcode (ignores quiet-zone).
     * @return height in millimeters (mm)
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param orientation The orientation of the barcode (eg: 0, 90, 180 or 270)
     * @return height in millimeters (mm) that will take the orientation into account
     */
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

    /**
     * @param orientation The orientation of the barcode (eg: 0, 90, 180 or 270)
     * @return height in millimeters (mm) including the Quiet Zone
     */
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

    /**
     * @param orientation The orientation of the barcode (eg: 0, 90, 180 or 270)
     * @return Normalised value for orientation. For example if you pass in -90 method will return positive value equivalent of 270
     */
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
            throw new IllegalArgumentException("Orientation must be 0, 90, 180, 270, -90, -180 or -270");
        }
    }

    /**
     * @param orientation The orientation of the barcode (eg: 0, 90, 180 or 270)
     * @return width in millimeters (mm) that will take the orientation into account
     */
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

    /**
     * @param orientation The orientation of the barcode (eg: 0, 90, 180 or 270)
     * @return width in millimeters (mm) including the Quiet Zone
     */
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
        return new Rectangle2D.Double(
            0,
            0,
            getWidthPlusQuiet(),
            getHeightPlusQuiet()
        );
    }

    /** @return a content rectangle (excluding quiet zone) */
    public Rectangle2D getContentRect() {
        return new Rectangle2D.Double(
            getXOffset(),
            getYOffset(),
            getWidth(),
            getHeight()
        );
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
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
