/*
 * Copyright 2003-2004 Jeremias Maerki.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.fop.layout.FontState;
import org.apache.fop.messaging.MessageHandler;
import org.apache.fop.pdf.PDFFormXObject;
import org.apache.fop.render.Renderer;
import org.apache.fop.render.pdf.PDFRenderer;
import org.apache.fop.render.ps.PSRenderer;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * Class representing an Barcode area in which the Barocde graphics sit
 * 
 * @version $Id: BarcodeArea.java,v 1.3 2004-09-04 20:25:58 jmaerki Exp $
 */
public class BarcodeArea extends org.krysalis.barcode4j.fop0205.BarcodeArea {
    
    /**
     * Construct an Barcode area
     *
     * @param fontState the font state
     * @param width the width of the area
     * @param height the height of the area
     */
    public BarcodeArea(FontState fontState, float width, float height) {
        super(fontState, width, height);
    }

    /**
     * Render the Barcode.
     *
     * @param renderer the Renderer to use
     */
    public void render(Renderer renderer) {
        if ("svg".equalsIgnoreCase(getRenderMode())) {
            //MessageHandler.logln("Rendering Barcode using SVG/Batik");
            super.render(renderer);
        } else if (renderer instanceof PSRenderer) {
            PSRenderer psr = (PSRenderer)renderer;
            //MessageHandler.logln("Rendering Barcode to PS using EPS");
            renderPostScriptBarcodeEPS(psr);
        } else if (renderer instanceof PDFRenderer) {
            PDFRenderer pdfr = (PDFRenderer)renderer;
            //MessageHandler.logln("Rendering Barcode to PDF natively");
            renderPDFBarcodeNative(pdfr);
        } else {
            super.render(renderer);
        }
    }
    
    protected void renderPostScriptBarcodeEPS(PSRenderer psr) {
        try {
            ByteArrayOutputStream baout = new ByteArrayOutputStream(1024);
            EPSCanvasProvider epsout = new EPSCanvasProvider(baout);
            getBarcodeGenerator().generateBarcode(epsout, getMessage());
            epsout.finish();
            BarcodeDimension dim = epsout.getDimensions();
            int bw = (int)(UnitConv.mm2pt(dim.getWidthPlusQuiet()));
            int bh = (int)(UnitConv.mm2pt(dim.getHeightPlusQuiet()));

            psr.renderEPS(baout.toByteArray(), "Barcode",
                //4 times millipoints
                psr.getCurrentX(), psr.getCurrentY(),
                getWidth(), getHeight(),
                //4 times points
                0, 0, 
                bw, bh);
        } catch (IOException ioe) {
            MessageHandler.errorln(
                "Couldn't render barcode due to IOException: " 
                    + ioe.getMessage());
        }
    }

    protected void renderPDFBarcodeNative(PDFRenderer pdfr) {
        try {
            PDFCanvasProvider pdfout = new PDFCanvasProvider();
            getBarcodeGenerator().generateBarcode(pdfout, getMessage());
            PDFFormXObject xobj = pdfout.createXObject(pdfr.getPDFDocument());
            pdfr.renderXObject(xobj.getName(), getWidth(), getHeight(), getFontState());
        } catch (IOException ioe) {
            MessageHandler.errorln(
                "Couldn't render barcode due to IOException: " 
                    + ioe.getMessage());
        }
    }

}
