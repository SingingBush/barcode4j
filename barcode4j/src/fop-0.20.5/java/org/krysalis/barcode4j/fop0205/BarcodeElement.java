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
package org.krysalis.barcode4j.fop0205;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.MessageUtil;
import org.krysalis.barcode4j.tools.UnitConv;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;

import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.DirectPropertyListBuilder;
import org.apache.fop.fo.FObj;
import org.apache.fop.fo.PropertyList;
import org.apache.fop.fo.Status;
import org.apache.fop.layout.Area;
import org.apache.fop.layout.FontState;
import org.apache.fop.layout.inline.ForeignObjectArea;
import org.apache.fop.messaging.MessageHandler;

/**
 * Class representing bc:barcode pseudo flow object.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeElement.java,v 1.8 2008-12-10 15:52:37 jmaerki Exp $
 */
public class BarcodeElement extends BarcodeObj {

    private static final String XMLNS_NAMESPACE_URI =
            "http://www.w3.org/2000/xmlns/";


    /**
     * inner class for making SVG objects.
     */
    public static class Maker extends FObj.Maker {

        /**
         * make an SVG object.
         *
         * @param parent the parent formatting object
         * @param propertyList the explicit properties of this object
         *
         * @return the SVG object
         */
        public FObj make(FObj parent, PropertyList propertyList,
                         String systemId, int line, int column)
                        throws FOPException {
            return new BarcodeElement(parent, propertyList,
                                  systemId, line, column);
        }
    }

    /**
     * returns the maker for this object.
     *
     * @return the maker for SVG objects
     */
    public static FObj.Maker maker() {
        return new BarcodeElement.Maker();
    }

    FontState fs;

    /**
     * constructs an SVG object (called by Maker).
     *
     * @param parent the parent formatting object
     * @param propertyList the explicit properties of this object
     */
    public BarcodeElement(FObj parent, PropertyList propertyList,
                      String systemId, int line, int column) {
        super(parent, propertyList, "barcode", systemId, line, column);
        init();
    }

    protected void init() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            this.doc = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException pce) {
            throw new CascadingRuntimeException("Error creating DOM", pce);
        }
        this.doc.appendChild(this.doc.createElementNS(BarcodeConstants.NAMESPACE, "barcode"));

        this.element = doc.getDocumentElement();

        buildTopLevel(this.doc, this.element);
    }

    public void buildTopLevel(Document doc, Element svgRoot) {
        // build up the info for the top level element
        if(this.properties instanceof DirectPropertyListBuilder.AttrPropertyList) {
            Attributes attr = ((DirectPropertyListBuilder.AttrPropertyList)this.properties).getAttributes();
            for (int count = 0; count < attr.getLength(); count++) {
                String rf = attr.getValue(count);
                String qname = attr.getQName(count);
                if (qname.indexOf(":") == -1) {
                    element.setAttribute(qname, rf);
                } else {
                    String pref =
                       qname.substring(0, qname.indexOf(":"));
                    ns.put("xlink", "http://www.w3.org/1999/xlink");
                    if (pref.equals("xmlns")) {
                        ns.put(qname.substring(qname.indexOf(":")
                                                      + 1), rf);
                        element.setAttributeNS(XMLNS_NAMESPACE_URI,
                                               qname, rf);
                    } else {
                        element.setAttributeNS((String)ns.get(pref),
                                               qname, rf);
                    }
                }
            }
        } else {
        }
    }

    /**
     * Factory method for creating BarcodeAreas.
     * @param fontState the font state
     * @param width the width of the area
     * @param height the height of the area
     * @return the newly created BarcodeArea
     */
    protected BarcodeArea createArea(FontState fontState, float width, float height) {
        return new BarcodeArea(fontState, width, height);
    }

    /**
     * layout this formatting object.
     *
     * @param area the area to layout the object into
     *
     * @return the status of the layout
     */
    public int layout(final Area area) throws FOPException {
        if (!(area instanceof ForeignObjectArea)) {
            // this is an error
            throw new FOPException("Barcode not in fo:instream-foreign-object");
        }

        if (this.marker == START) {
            this.fs = area.getFontState();

            this.marker = 0;
        }

        //MessageHandler.logln("Creating barcode area");

        /* create a barcode area */
        /* if width and height are zero, get the bounds of the content. */
        final ForeignObjectArea foa = (ForeignObjectArea)area;

        Element e = this.doc.getDocumentElement();

        //if(!e.hasAttributeNS(XMLSupport.XMLNS_NAMESPACE_URI, "xmlns")) {
            e.setAttributeNS(XMLNS_NAMESPACE_URI, "xmlns", BarcodeConstants.NAMESPACE);
        //}

        Configuration cfg = ConfigurationUtil.buildConfiguration(this.doc);
        try {
            String msg = ConfigurationUtil.getMessage(cfg);
            msg = MessageUtil.unescapeUnicode(msg);

            int orientation = cfg.getAttributeAsInteger("orientation", 0);

            //MessageHandler.logln("Barcode message: " + msg);
            final String renderMode = cfg.getAttribute("render-mode", "native");
            //MessageHandler.logln("Render mode: " + renderMode);

            BarcodeGenerator bargen = BarcodeUtil.getInstance().
                    createBarcodeGenerator(cfg);
            String expandedMsg = VariableUtil.getExpandedMessage(foa.getPage(), msg);
            BarcodeDimension bardim = bargen.calcDimensions(expandedMsg);
            final float w = (float)UnitConv.mm2pt(bardim.getWidthPlusQuiet(orientation)) * 1000;
            final float h = (float)UnitConv.mm2pt(bardim.getHeightPlusQuiet(orientation)) * 1000;


            BarcodeArea barcodeArea = createArea(fs, w, h);
            barcodeArea.setParent(foa);
            barcodeArea.setPage(foa.getPage());
            barcodeArea.setBarcode(bargen, expandedMsg, renderMode, orientation);
            barcodeArea.start();
            barcodeArea.end();

            /* add the SVG area to the containing area */
            foa.setObject(barcodeArea);
            foa.setIntrinsicWidth(barcodeArea.getWidth());
            foa.setIntrinsicHeight(barcodeArea.getHeight());

            /* return status */
            return Status.OK;
        } catch (ConfigurationException ce) {
            MessageHandler.errorln("Error in barcode XML: " + ce.getMessage());
            throw new FOPException("Error in barcode XML", ce);
        } catch (BarcodeException be) {
            MessageHandler.errorln("Error generating barcode: " + be.getMessage());
            throw new FOPException("Error generating barcode", be);
        }
    }

}
