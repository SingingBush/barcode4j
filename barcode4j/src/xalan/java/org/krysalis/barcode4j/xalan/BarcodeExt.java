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
package org.krysalis.barcode4j.xalan;

import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.xalan.extensions.XSLProcessorContext;
import org.apache.xalan.templates.ElemExtensionCall;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * This class is an Extension for Apache XML Xalan to generate SVG barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeExt.java,v 1.3 2004-10-02 14:59:40 jmaerki Exp $
 */
public class BarcodeExt {

    /**
     * Generates a barcode in SVG format as a DOM fragment.
     * @param nl node list with the XML configuration
     * @param message the message to encode
     * @return the DOM fragment
     * @throws SAXException if generation fails
     */
    public DocumentFragment generate(NodeList nl, String message) 
                    throws SAXException {
        try {
            
            //Build configuration
            final Configuration cfg = ConfigurationUtil.buildConfiguration(nl.item(0));
            
            //Acquire BarcodeGenerator
            final BarcodeGenerator gen = 
                    BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
    
            //Setup Canvas
            final SVGCanvasProvider svg;
            if (cfg.getAttributeAsBoolean("useNamespace", true)) {
                svg = new SVGCanvasProvider(cfg.getAttribute("prefix", "svg"));
            } else {
                svg = new SVGCanvasProvider(false);
            }
            //Generate barcode
            gen.generateBarcode(svg, message);
    
            return svg.getDOMFragment();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    /**
     * Extension element: Generates a barcode in SVG format as a DOM fragment.
     * @param context the processor context
     * @param elem the extension element
     * @return a DOM fragment containing the SVG barcode
     * @throws javax.xml.transform.TransformerException if an Exception occurs
     * during barcode generation
     */
    public DocumentFragment barcode(XSLProcessorContext context, ElemExtensionCall elem)
            throws javax.xml.transform.TransformerException {
        try {
            String message = elem.getAttribute("message", 
                    context.getContextNode(), context.getTransformer());
            
            //Build configuration
            final Configuration cfg = new ElemWrappingConfiguration(elem);
            
            //Acquire BarcodeGenerator
            final BarcodeGenerator gen = 
                    BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
    
            //Setup Canvas
            final SVGCanvasProvider svg;
            if (cfg.getAttributeAsBoolean("useNamespace", true)) {
                svg = new SVGCanvasProvider(cfg.getAttribute("prefix", "svg"));
            } else {
                svg = new SVGCanvasProvider(false);
            }
            //Generate barcode
            gen.generateBarcode(svg, message);
    
            return svg.getDOMFragment();
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransformerException(e);
        }
                  
    }
  

}
