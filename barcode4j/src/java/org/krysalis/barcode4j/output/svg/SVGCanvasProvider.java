/*
 * $Id: SVGCanvasProvider.java,v 1.2 2004-08-30 21:21:54 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2004 Nicola Ken Barozzi.  All rights reserved.
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