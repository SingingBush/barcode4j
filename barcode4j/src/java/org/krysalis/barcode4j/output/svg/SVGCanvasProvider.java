/*
 * Copyright 2002-2004,2006,2008 Jeremias Maerki.
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

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * Implementation that outputs to a W3C DOM.
 *
 * @author Jeremias Maerki
 * @version $Id: SVGCanvasProvider.java,v 1.6 2009-04-21 15:33:46 jmaerki Exp $
 */
public class SVGCanvasProvider extends AbstractSVGGeneratingCanvasProvider {

    private DOMImplementation domImpl;
    private Document doc;
    private Element detailGroup;

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(String namespacePrefix, int orientation)
                throws BarcodeCanvasSetupException {
        this(null, namespacePrefix, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider with namespaces enabled.
     * @param domImpl DOMImplementation to use (JAXP default is used when
     *     this is null)
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(DOMImplementation domImpl, String namespacePrefix,
                    int orientation)
                throws BarcodeCanvasSetupException {
        super(namespacePrefix, orientation);
        this.domImpl = domImpl;
        init();
    }

    /**
     * Creates a new SVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(boolean useNamespace, int orientation)
                throws BarcodeCanvasSetupException {
        this(null, useNamespace, orientation);
    }

    /**
     * Creates a new SVGCanvasProvider.
     * @param domImpl DOMImplementation to use (JAXP default is used when
     *     this is null)
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(DOMImplementation domImpl, boolean useNamespace, int orientation)
                throws BarcodeCanvasSetupException {
        super(useNamespace, orientation);
        this.domImpl = domImpl;
        init();
    }

    /**
     * Creates a new SVGCanvasProvider with default settings (with namespaces,
     * but without namespace prefix).
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public SVGCanvasProvider(int orientation) throws BarcodeCanvasSetupException {
        super(orientation);
        init();
    }

    private void init() {
        doc = createDocument();
        Element svg = doc.getDocumentElement();

        detailGroup = createElement("g");
        svg.appendChild(detailGroup);
        detailGroup.setAttribute("fill", "black");
        detailGroup.setAttribute("stroke", "none");
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

    /** {@inheritDoc} */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        int orientation = BarcodeDimension.normalizeOrientation(getOrientation());
        Element svg = (Element)doc.getDocumentElement();
        svg.setAttribute("width", addUnit(dim.getWidthPlusQuiet(orientation)));
        svg.setAttribute("height", addUnit(dim.getHeightPlusQuiet(orientation)));
        String w = getDecimalFormat().format(dim.getWidthPlusQuiet(orientation));
        String h = getDecimalFormat().format(dim.getHeightPlusQuiet(orientation));
        svg.setAttribute("viewBox", "0 0 " + w + " " + h);
        String transform;
        switch (orientation) {
        case 90:
            transform = "rotate(-90) translate(-" + h + ")";
            break;
        case 180:
            transform = "rotate(-180) translate(-" + w + " -" + h + ")";
            break;
        case 270:
            transform = "rotate(-270) translate(0 -" + w + ")";
            break;
        default:
            transform = null;
        }
        if (transform != null) {
            detailGroup.setAttribute("transform", transform);
        }
    }

    /** {@inheritDoc} */
    public void deviceFillRect(double x, double y, double w, double h) {
        Element el = createElement("rect");
        el.setAttribute("x", getDecimalFormat().format(x));
        el.setAttribute("y", getDecimalFormat().format(y));
        el.setAttribute("width", getDecimalFormat().format(w));
        el.setAttribute("height", getDecimalFormat().format(h));
        detailGroup.appendChild(el);
    }

    /** {@inheritDoc} */
    public void deviceText(String text, double x1, double x2, double y1,
                            String fontName, double fontSize, TextAlignment textAlign) {
        Element el = createElement("text");
        String anchor;
        double tx;
        if (textAlign == TextAlignment.TA_LEFT) {
            anchor = "start";
            tx = x1;
        } else if (textAlign == TextAlignment.TA_RIGHT) {
            anchor = "end";
            tx = x2;
        } else {
            anchor = "middle";
            tx = x1 + (x2 - x1) / 2;
        }
        el.setAttribute("font-family", fontName);
        el.setAttribute("font-size", getDecimalFormat().format(fontSize));
        el.setAttribute("text-anchor", anchor);
        el.setAttribute("x", getDecimalFormat().format(tx));
        el.setAttribute("y", getDecimalFormat().format(y1));
        if (textAlign == TextAlignment.TA_JUSTIFY) {
            el.setAttribute("textLength", getDecimalFormat().format(x2 - x1));
        }
        el.appendChild(doc.createTextNode(text));
        detailGroup.appendChild(el);

    }

}