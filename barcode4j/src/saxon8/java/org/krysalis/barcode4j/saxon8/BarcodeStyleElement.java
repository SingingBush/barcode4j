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
package org.krysalis.barcode4j.saxon8;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;

import net.sf.saxon.Controller;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.event.SequenceReceiver;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.Instruction;
import net.sf.saxon.instruct.TailCall;
import net.sf.saxon.style.StyleElement;

/**
 * This represents the main barcode element.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeStyleElement.java,v 1.2 2004-09-04 20:25:55 jmaerki Exp $
 */
public class BarcodeStyleElement extends StyleElement {

    private Expression message;

    /**
     * @see net.sf.saxon.style.StyleElement#isInstruction()
     */
    public boolean isInstruction() {
        return true;
    }

    /**
     * Determine whether this type of element is allowed to contain a template-body
     * @return true: yes, it may contain a template-body (this is done only so that
     * it can contain xsl:fallback)
     */
    public boolean mayContainSequenceConstructor() {
        return true;
    }

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

    public void validate() throws TransformerConfigurationException {
        checkWithinTemplate();
        message = typeCheck("message", message);
    }


    public Instruction compile(Executable exec) throws TransformerConfigurationException {
        final Configuration cfg = ConfigurationUtil.buildConfiguration(this);
        BarcodeInstruction inst = new BarcodeInstruction( message, cfg );
        return inst;
    }

    private static class BarcodeInstruction extends Instruction {

        private Logger log = new org.apache.avalon.framework.logger.ConsoleLogger();
        private Expression message;
        private Configuration cfg;
        
        public BarcodeInstruction(Expression message, Configuration cfg) {
            this.message = message;
            this.cfg = cfg;
        }
        
        /**
         * @see net.sf.saxon.instruct.Instruction#getInstructionName()
         */
        public String getInstructionName() {
            return "barcode";
        }

        /**
         * @see net.sf.saxon.instruct.Instruction#processLeavingTail(net.sf.saxon.expr.XPathContext)
         */
        public TailCall processLeavingTail(XPathContext context) throws TransformerException {
            Controller controller = context.getController();

            String effMessage = message.evaluateAsString(context);

            try {
                SequenceReceiver out = controller.getReceiver();

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
                gen.generateBarcode(svg, effMessage);
                
                DocumentWrapper wrapper = new DocumentWrapper(svg.getDOM(), 
                            SVGCanvasProvider.SVG_NAMESPACE);
                out.append(wrapper);

            } catch (BarcodeException be) {
                throw new TransformerException("(Barcode4J) " + be.getMessage());
            }
            return null;
        }
    
    }
    
}
