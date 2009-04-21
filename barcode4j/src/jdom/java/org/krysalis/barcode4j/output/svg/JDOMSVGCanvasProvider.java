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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.TextAlignment;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

/**
 * SVG generating implementation that outputs to a JDOM.
 *
 * @author Jeremias Maerki
 * @version $Id: JDOMSVGCanvasProvider.java,v 1.6 2009-04-21 15:33:46 jmaerki Exp $
 */
public class JDOMSVGCanvasProvider extends AbstractSVGGeneratingCanvasProvider {

    private Namespace ns;
    private Document doc;
    private Element detailGroup;

    /**
     * Creates a new JDOMSVGCanvasProvider with namespaces enabled.
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider(String namespacePrefix, int orientation)
                throws BarcodeCanvasSetupException {
        super(namespacePrefix, orientation);
    }

    /**
     * Creates a new JDOMSVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider(boolean useNamespace, int orientation)
                throws BarcodeCanvasSetupException {
        super(useNamespace, orientation);
        init();
    }

    /**
     * Creates a new JDOMSVGCanvasProvider with default settings (with namespaces,
     * but without namespace prefix).
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public JDOMSVGCanvasProvider(int orientation) throws BarcodeCanvasSetupException {
        super(orientation);
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
        detailGroup.setAttribute("fill", "black");
        detailGroup.setAttribute("stroke", "none");
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

    /** {@inheritDoc} */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        Element svg = doc.getRootElement();
        svg.setAttribute("width", getDecimalFormat().format(dim.getWidthPlusQuiet()));
        svg.setAttribute("height", getDecimalFormat().format(dim.getHeightPlusQuiet()));
        svg.setAttribute("viewBox", "0 0 "
                + getDecimalFormat().format(dim.getWidthPlusQuiet()) + " "
                + getDecimalFormat().format(dim.getHeightPlusQuiet()));
    }

    /** {@inheritDoc} */
    public void deviceFillRect(double x, double y, double w, double h) {
        Element el = new Element("rect", ns);
        el.setAttribute("x", getDecimalFormat().format(x));
        el.setAttribute("y", getDecimalFormat().format(y));
        el.setAttribute("width", getDecimalFormat().format(w));
        el.setAttribute("height", getDecimalFormat().format(h));
        detailGroup.addContent(el);
    }

    /** {@inheritDoc} */
    public void deviceText(String text, double x1, double x2, double y1,
                            String fontName, double fontSize, TextAlignment textAlign) {
        Element el = new Element("text", ns);
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
        el.addContent(text);
        detailGroup.addContent(el);
    }

}