/*
 * Copyright 2003-2004,2007 Jeremias Maerki.
 * Copyright 2006 Robert Deeken (compatibility with Saxon 8.7.1 and later)
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

import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;

import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.event.SequenceReceiver;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.trans.DynamicError;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ValidationException;

/**
 * This represents the main barcode element.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeStyleElement.java,v 1.4 2007-01-15 11:12:33 jmaerki Exp $
 */
public class BarcodeStyleElement extends StyleElement {

    private Expression message;
    private Expression orientation;

    /**
     * @see net.sf.saxon.style.StyleElement#isInstruction()
     */
    @Override
    public boolean isInstruction() {
        return true;
    }

    /**
     * Determine whether this type of element is allowed to contain a template-body
     *
     * @return true: yes, it may contain a template-body (this is done only so that
     * it can contain xsl:fallback)
     */
    @Override
    public boolean mayContainSequenceConstructor() {
        return true;
    }

    /**
     * @see net.sf.saxon.style.StyleElement#prepareAttributes()
     */
    @Override
    public void prepareAttributes() throws XPathException {
        // Get mandatory message attribute
        final String msgAtt = super.getAttributeList().getValue("", "message");
        if (msgAtt == null) {
            reportAbsence("message");
        }
        this.message = makeAttributeValueTemplate(msgAtt);

        final String orientationAtt = super.getAttributeList().getValue("", "orientation");
        this.orientation = orientationAtt != null ? makeAttributeValueTemplate(orientationAtt) : null;
    }

    /**
     * @see net.sf.saxon.style.StyleElement#validate()
     */
    @Override
    public void validate() throws XPathException {
        super.validate();
        checkWithinTemplate();
        message = typeCheck("message", message);
        if (orientation != null) {
            orientation = typeCheck("orientation", orientation);
        }
    }

    /**
     * @see net.sf.saxon.style.StyleElement#compile(net.sf.saxon.instruct.Executable)
     */
    @Override
    public Expression compile(Executable exec) throws XPathException {
        final NodeOverNodeInfo node = NodeOverNodeInfo.wrap(this);
        final Configuration cfg = ConfigurationUtil.buildConfiguration(node);
        return new BarcodeExpression(message, orientation, cfg);
    }

    /**
     * @see net.sf.saxon.style.StyleElement#isPermittedChild(net.sf.saxon.style.StyleElement)
     */
    @Override
    protected boolean isPermittedChild(StyleElement styleElement) {
        // I am allowing anything right now
        return true;
    }

    private static class BarcodeExpression extends SimpleExpression {

        private final Expression message;
        private final Expression orientation;
        private final Configuration cfg;

        public BarcodeExpression(final Expression message, final Expression orientation, final Configuration cfg) {
            this.message = message;
            this.orientation = orientation;
            this.cfg = cfg;
        }

        /**
         * @see net.sf.saxon.expr.ComputedExpression#getImplementationMethod()
         */
        @Override
        public int getImplementationMethod() {
            return Expression.PROCESS_METHOD;
        }

        @Override
        public void process(XPathContext context) throws XPathException {
            final String effMessage = message.evaluateAsString(context);
            int effOrientation = 0;

            if (orientation != null) {
                final String degrees = orientation.evaluateAsString(context);
                try {
                    effOrientation = BarcodeDimension.normalizeOrientation(Integer.parseInt(degrees));
                } catch (final NumberFormatException e) {
                    throw new ValidationException(e);
                } catch (final IllegalArgumentException e) {
                    throw new ValidationException(e);
                }
            }

            try {
                final SequenceReceiver out = context.getReceiver();

                //Acquire BarcodeGenerator
                final BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);

                //Setup Canvas
                final SVGCanvasProvider svg = cfg.getAttributeAsBoolean("useNamespace", true) ?
                        new SVGCanvasProvider(cfg.getAttribute("prefix", "svg"), effOrientation) :
                        new SVGCanvasProvider(false, effOrientation);

                //Generate barcode
                gen.generateBarcode(svg, effMessage);

                final DocumentWrapper wrapper = new DocumentWrapper(
                        svg.getDOM(),
                        SVGCanvasProvider.SVG_NAMESPACE,
                        context.getConfiguration()
                );

                out.append(wrapper, this.getLocationId(), 1);
            } catch (final ConfigurationException e) {
                throw new DynamicError("(Barcode4J) " + e.getMessage());
            } catch (final BarcodeException e) {
                throw new DynamicError("(Barcode4J) " + e.getMessage());
            }
        }
    }
}
