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
package org.krysalis.barcode4j.saxon;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.xml.sax.SAXException;

import com.icl.saxon.ContentEmitter;
import com.icl.saxon.Context;
import com.icl.saxon.DOMDriver;
import com.icl.saxon.expr.Expression;
import com.icl.saxon.om.NamePool;
import com.icl.saxon.style.StyleElement;

/**
 * This represents the main barcode element.
 * 
 * @author Jeremias Maerki
 */
public class BarcodeStyleElement extends StyleElement {

    private Logger log = new org.apache.avalon.framework.logger.ConsoleLogger();

    private Expression message;


    /**
     * @see com.icl.saxon.style.StyleElement#prepareAttributes()
     */
    public void prepareAttributes() throws TransformerConfigurationException {
        // Get mandatory message attribute
        
        String msgAtt = attributeList.getValue("message");
        if (msgAtt == null) {
            reportAbsence("message");
        }
        message = makeAttributeValueTemplate(msgAtt);
    }


    /**
     * @see com.icl.saxon.style.StyleElement#process(com.icl.saxon.Context)
     */
    public void process(Context context) throws TransformerException {
        final Configuration cfg = ConfigurationUtil.buildConfiguration(this);

        try {
            //Acquire BarcodeGenerator
            final BarcodeGenerator gen = 
                    BarcodeUtil.getInstance().createBarcodeGenerator(cfg, log);
    
            //Setup Canvas
            final SVGCanvasProvider svg;
            if (cfg.getAttributeAsBoolean("useNamespace", true)) {
                svg = new SVGCanvasProvider(cfg.getAttribute("prefix", "svg"));
            } else {
                svg = new SVGCanvasProvider(false);
            }
            //Generate barcode
            gen.generateBarcode(svg, message.evaluateAsString(context));
            
            ContentEmitter ce = new ContentEmitter();
            ce.setEmitter(context.getOutputter().getEmitter());
            ce.setNamePool(NamePool.getDefaultNamePool());
            try {
                DOMDriver domdriver = new DOMDriver();
                domdriver.setStartNode(svg.getDOMFragment());
                domdriver.setContentHandler(ce);
                domdriver.parse();
            } catch (SAXException saxe) {
                throw new TransformerException(saxe);
            }
        } catch (BarcodeException be) {
            throw new TransformerException(be);
        }
    }
   

    /**
     * @see com.icl.saxon.style.StyleElement#isInstruction()
     */
    public boolean isInstruction() {
        return true;
    }

}
