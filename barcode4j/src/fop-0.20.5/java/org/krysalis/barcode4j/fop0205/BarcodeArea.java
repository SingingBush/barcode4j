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
package org.krysalis.barcode4j.fop0205;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.fop.layout.Area;
import org.apache.fop.layout.FontState;
import org.apache.fop.messaging.MessageHandler;
import org.apache.fop.render.Renderer;
import org.apache.fop.svg.SVGArea;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * Class representing an Barcode area in which the barcode graphics sits.
 * 
 * @version $Id: BarcodeArea.java,v 1.8 2007-02-14 10:19:07 jmaerki Exp $
 */
public class BarcodeArea extends Area {
    
    private BarcodeGenerator bargen;
    private String msg;
    private String renderMode;
    private int orientation;

    /**
     * Construct an Barcode area
     *
     * @param fontState the font state
     * @param width the width of the area
     * @param height the height of the area
     */
    public BarcodeArea(FontState fontState, float width, float height) {
        super(fontState, (int)width, (int)height);
        currentHeight = (int)height;
        contentRectangleWidth = (int)width;
    }

    public void setBarcode(BarcodeGenerator bargen, 
            String msg, String renderMode) {
        setBarcode(bargen, msg, renderMode, 0);
    }
    
    public void setBarcode(BarcodeGenerator bargen, 
                String msg, String renderMode, int orientation) {
        this.bargen = bargen;
        this.msg = msg;
        this.renderMode = renderMode;
        this.orientation = orientation;
    }

    public BarcodeGenerator getBarcodeGenerator() {
        return this.bargen;
    }

    public String getMessage() {
        return this.msg;
    }
    
    public String getRenderMode() {
        return this.renderMode;
    }

    public int getOrientation() {
        return this.orientation;
    }
        
    public int getWidth() {
        return contentRectangleWidth;
    }

    public double mpt2mm(double mpt) {
        return UnitConv.pt2mm(mpt / 1000);
    }

    /**
     * Render the Barcode.
     *
     * @param renderer the Renderer to use
     */
    public void render(Renderer renderer) {
        renderBarcodeUsingSVG(renderer);
    }
    
    protected SVGArea createSVGArea() throws BarcodeCanvasSetupException {
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        //TODO Implement orientation feature
        SVGCanvasProvider svgout = new SVGCanvasProvider(domImpl, true, getOrientation());
        getBarcodeGenerator().generateBarcode(svgout, getMessage());
        Document dom = svgout.getDOM();
        SVGArea svgarea = new SVGArea(getFontState(), getWidth(), getHeight());
        svgarea.setSVGDocument(dom);
        return svgarea;
    }
    
    protected void renderBarcodeUsingSVG(Renderer renderer) {
        try {
            renderer.renderSVGArea(createSVGArea());
        } catch (BarcodeCanvasSetupException bcse) {
            MessageHandler.errorln(
                "Couldn't render barcode due to BarcodeCanvasSetupException: " 
                    + bcse.getMessage());
        }
    }
    
}
