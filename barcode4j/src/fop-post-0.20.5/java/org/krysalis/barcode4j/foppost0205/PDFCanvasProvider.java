/*
 * Copyright 2003,2004 Jeremias Maerki.
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
 * @version $Id: PDFCanvasProvider.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
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
