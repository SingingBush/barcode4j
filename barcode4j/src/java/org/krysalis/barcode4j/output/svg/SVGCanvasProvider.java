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
package org.krysalis.barcode4j.output.svg;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

/**
 * Implementation that outputs to a W3C DOM.
 * 
 * @author Jeremias Maerki
 * @version $Id: SVGCanvasProvider.java,v 1.3 2004-09-04 20:26:16 jmaerki Exp $
 */
public class SVGCanvasProvider extends AbstractSVGGeneratingCanvasProvider {

    private DOMImplementation domImpl;
    private Document doc;
    private Element detailGroup;

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(String namespacePrefix) 
                throws BarcodeCanvasSetupException {
        this(null, namespacePrefix);
    }

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     * @param domImpl DOMImplementation to use (JAXP default is used when
     *     this is null)
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(DOMImplementation domImpl, String namespacePrefix) 
                throws BarcodeCanvasSetupException {
        super(namespacePrefix);
        this.domImpl = domImpl;
        init();
    }

    /**
     * Creates a new SVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(boolean useNamespace) 
                throws BarcodeCanvasSetupException {
        this(null, useNamespace);
    }

    /**
     * Creates a new SVGCanvasProvider.
     * @param domImpl DOMImplementation to use (JAXP default is used when
     *     this is null)
     * @param useNamespace Controls whether namespaces should be used
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(DOMImplementation domImpl, boolean useNamespace) 
                throws BarcodeCanvasSetupException {
        super(useNamespace);
        this.domImpl = domImpl;
        init();
    }

    /**
     * Creates a new SVGCanvasProvider with default settings (with namespaces, 
     * but without namespace prefix).
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider() throws BarcodeCanvasSetupException {
        super();
        init();
    }

    private void init() {
        doc = createDocument();
        Element svg = doc.getDocumentElement();

        detailGroup = createElement("g");
        svg.appendChild(detailGroup);
        detailGroup.setAttribute("style", "fill:black; stroke:none");
    }


    private Element createElement(String localName) {
        Element el;
        if (isNamespaceEnabled()) {
            el = doc.createElementNS(SVG_NAMESPACE, getQualifiedName(localName));
        } else {
            el = doc.createElement(localName);
        }
        return el;
    }


    private Document createDocument() {
        try {
            if (this.domImpl == null) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                dbf.setValidating(false);
                DocumentBuilder db = dbf.newDocumentBuilder();
                this.domImpl = db.getDOMImplementation();
            }

            if (isNamespaceEnabled()) {
                Document doc = this.domImpl.createDocument(
                        SVG_NAMESPACE, getQualifiedName("svg"), null);
                /*
                if (getNamespacePrefix() == null) {
                    doc.getDocumentElement().setAttribute(
                            "xmlns", SVG_NAMESPACE);
                } else {
                    doc.getDocumentElement().setAttribute(
                            "xmlns:" + getNamespacePrefix(), SVG_NAMESPACE);
                }*/
                return doc;
            } else {
                return this.domImpl.createDocument(null, "svg", null);
            }
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce.getMessage());
        }
    }

    /**
     * Returns the DOM document containing the SVG barcode.
     * @return the DOM document
     */
    public org.w3c.dom.Document getDOM() {
        return this.doc;
    }

    /**
     * Returns the DOM fragment containing the SVG barcode.
     * @return the DOM fragment
     */
    public org.w3c.dom.DocumentFragment getDOMFragment() {
        DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.importNode(doc.getFirstChild(), true));
        return frag;
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        Element svg = (Element)doc.getDocumentElement();
        svg.setAttribute("width", addUnit(dim.getWidthPlusQuiet()));
        svg.setAttribute("height", addUnit(dim.getHeightPlusQuiet()));
        svg.setAttribute("viewBox", "0 0 " 
                + getDecimalFormat().format(dim.getWidthPlusQuiet()) + " " 
                + getDecimalFormat().format(dim.getHeightPlusQuiet()));
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceFillRect(double x, double y, double w, double h) {
        Element el = createElement("rect");
        el.setAttribute("x", getDecimalFormat().format(x));
        el.setAttribute("y", getDecimalFormat().format(y));
        el.setAttribute("width", getDecimalFormat().format(w));
        el.setAttribute("height", getDecimalFormat().format(h));
        detailGroup.appendChild(el);
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
    public void deviceCenteredText(String text, double x1, double x2, double y1,
                            String fontName, double fontSize, boolean justify) {
        Element el = createElement("text");
        el.setAttribute("style", "font-family:" + fontName + "; font-size:" 
                    + getDecimalFormat().format(fontSize) + "; text-anchor:middle");
        el.setAttribute("x", getDecimalFormat().format(x1 + (x2 - x1) / 2));
        el.setAttribute("y", getDecimalFormat().format(y1));
        if (justify) {
            el.setAttribute("textLength", getDecimalFormat().format(x2 - x1));
        }
        el.appendChild(doc.createTextNode(text));
        detailGroup.appendChild(el);

    }

}