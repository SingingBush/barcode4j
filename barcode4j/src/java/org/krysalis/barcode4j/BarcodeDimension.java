/*
 * $Id: BarcodeDimension.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j;

/**
 * This class provides information on the dimensions of a barcode. It makes a
 * distinction between the dimensions with and without quiet zone.
 * 
 * @author Jeremias Maerki
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

    /**
     * Returns the height of the barcode (quiet-zone included).
     * @return height in millimeters (mm)
     */
    public double getHeightPlusQuiet() {
        return heightPlusQuiet;
    }

    /**
     * Returns the width of the barcode (ignores quiet-zone).
     * @return width in millimeters (mm)
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the width of the barcode (quiet-zone included).
     * @return width in millimeters (mm)
     */
    public double getWidthPlusQuiet() {
        return widthPlusQuiet;
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
