/*
 * $Id: PDFCanvasProvider.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2003 Nicola Ken Barozzi.  All rights reserved.
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
package org.krysalis.barcode4j.foppost0205;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.fop.pdf.PDFDocument;
import org.apache.fop.pdf.PDFFormXObject;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * CanvasProvider for FOP's PDF library.
 * 
 * @author Jeremias Maerki
 */
public class PDFCanvasProvider extends AbstractCanvasProvider {

    private StringWriter writer;
    private DecimalFormat df;

    public PDFCanvasProvider() {
        this.writer = new StringWriter();
    }

    public PDFFormXObject createXObject(PDFDocument pdf) throws IOException {
        PDFFormXObject xobj = pdf.makeFormXObject("Barcode");
        try {
            xobj.setData(writer.getBuffer().toString().getBytes("US-ASCII"));
            long w = Math.round(Math.ceil(UnitConv.mm2pt(getDimensions().getWidthPlusQuiet())));
            long h = Math.round(Math.ceil(UnitConv.mm2pt(getDimensions().getHeightPlusQuiet())));
            long[] bbox = {0, 0, w, h};
            xobj.setBBox(bbox);
            
            //Reorient content
            AffineTransform at = new AffineTransform();
            at.scale(1, -1);
            at.translate(0, -h);
            xobj.setMatrix(at);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Incompatible VM! Doesn't understand US-ASCII");
        }
        return xobj;
    }

    /**
     * Returns the DecimalFormat instance to use internally to format numbers.
     * @return a DecimalFormat instance
     */
    protected DecimalFormat getDecimalFormat() {
        if (this.df == null) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            this.df = new DecimalFormat("0.####", dfs);
        }
        return this.df;
    }
    
    private String format(double coord) {
        return getDecimalFormat().format(coord);
    }
    
    private String formatmm(double coord) {
        return getDecimalFormat().format(UnitConv.mm2pt(coord));
    }
    
    /* (non-Javadoc)
     * @see org.krysalis.barcode4j.output.CanvasProvider#deviceFillRect(double, double, double, double)
     */
    public void deviceFillRect(double x, double y, double w, double h) {
        writer.write(formatmm(x) + " " + formatmm(y) + " " 
                + formatmm(w) + " " + formatmm(h) + " re f\n");
    }

    /* (non-Javadoc)
     * @see org.krysalis.barcode4j.output.CanvasProvider#deviceJustifiedText(java.lang.String, double, double, double, java.lang.String, double)
     */
    public void deviceJustifiedText(
            String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.krysalis.barcode4j.output.CanvasProvider#deviceCenteredText(java.lang.String, double, double, double, java.lang.String, double)
     */
    public void deviceCenteredText(
            String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        // TODO Auto-generated method stub

    }

}
