/*
 * $Id: JDOMSVGCanvasProvider.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
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
package org.krysalis.barcode4j.output.svg;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

/**
 * SVG generating implementation that outputs to a JDOM.
 * 
 * @author Jeremias Maerki
 */
public class JDOMSVGCanvasProvider extends AbstractSVGGeneratingCanvasProvider {

    private Namespace ns;
    private Document doc;
    private Element detailGroup;

    /**
     * Creates a new JDOMSVGCanvasProvider with namespaces enabled.
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider(String namespacePrefix)
                throws BarcodeCanvasSetupException {
        super(namespacePrefix);
    }

    /**
     * Creates a new JDOMSVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider(boolean useNamespace)
                throws BarcodeCanvasSetupException {
        super(useNamespace);
        init();
    }

    /**
     * Creates a new JDOMSVGCanvasProvider with default settings (with namespaces, 
     * but without namespace prefix).
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider() throws BarcodeCanvasSetupException {
        super();
        init();
    }

    private void init() {
        Element svg;
        if (isNamespaceEnabled()) {
            if (getNamespacePrefix() != null) {
                ns = Namespace.getNamespace(getNamespacePrefix(), SVG_NAMESPACE);
            } else {
                ns = Namespace.getNamespace(SVG_NAMESPACE);
            }
        } else {
            ns = null;
        }
        svg = new Element("svg", ns);
        doc = new Document(svg);

        detailGroup = new Element("g", ns);
        svg.addContent(detailGroup);
        detailGroup.setAttribute("style", "fill:black; stroke:none");
    }

    /**
     * Returns the JDOM document.
     * @return the JDOM document
     */
    public Document getDocument() {
        return this.doc;
    }

    /**
     * Converts the internal JDOM to a DOM and returns it.
     * @return the DOM document.
     */
    public org.w3c.dom.Document getDOM() {
        org.jdom.output.DOMOutputter output = new org.jdom.output.DOMOutputter();
        try {
            return output.output(this.doc);
        } catch (JDOMException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the DOM fragment containing the SVG barcode.
     * @return the DOM fragment
     */
    public org.w3c.dom.DocumentFragment getDOMFragment() {
        org.w3c.dom.Document doc = getDOM();
        org.w3c.dom.DocumentFragment frag = doc.createDocumentFragment();
        frag.appendChild(doc.getDocumentElement());
        return frag;
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        Element svg = doc.getRootElement();
        svg.setAttribute("width", addUnit(dim.getWidthPlusQuiet()));
        svg.setAttribute("height", addUnit(dim.getHeightPlusQuiet()));
    }

    /** @see org.krysalis.barcode4j.output.CanvasProvider */
    public void deviceFillRect(double x, double y, double w, double h) {
        Element el = new Element("rect", ns);
        el.setAttribute("x", addUnit(x));
        el.setAttribute("y", addUnit(y));
        el.setAttribute("width", addUnit(w));
        el.setAttribute("height", addUnit(h));
        detailGroup.addContent(el);
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
        Element el = new Element("text", ns);
        el.setAttribute("style", "font-family:" + fontName + "; font-size:" 
                    + fontSize + "pt; text-anchor:middle");
        el.setAttribute("x", addUnit(x1 + (x2 - x1) / 2));
        el.setAttribute("y", addUnit(y1));
        if (justify) {
            el.setAttribute("textLength", addUnit(x2 - x1));
        }
        el.addContent(text);
        detailGroup.addContent(el);

    }

}