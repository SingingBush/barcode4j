/*
 * Copyright 2003-2012 Jeremias Maerki.
 * Copyright 2020-2024 Samael Bate (singingbush)
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

import com.saxonica.xqj.SaxonXQPreparedExpression;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.event.Outputter;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.event.SequenceReceiver;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.Literal;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.SimpleNodeConstructor;
import net.sf.saxon.expr.parser.ContextItemStaticInfo;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.expr.parser.RebindingMap;
import net.sf.saxon.om.*;
import net.sf.saxon.option.dom4j.DOM4JNodeWrapper;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trace.ExpressionPresenter;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ArrayIterator;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.ValidationException;

import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.w3c.dom.Node;

/**
 * This represents the main barcode element.
 *
 * @author Jeremias Maerki &amp; Samael Bate (singingbush)
 */
public class BarcodeStyleElement extends ExtensionInstruction {

    private static final int STATIC_CONTEXT = -1;

    private Expression message;
    private Expression orientation;

//    public BarcodeStyleElement() {
//        super();
//        this.defaultXPathNamespace = "barcode4j";
//        //this.defaultCollationName = null;
//    }

//    /**
//     * @see StyleElement#isInstruction()
//     */
//    @Override
//    public boolean isInstruction() {
//        return true; // todo: evaluate extending ExtensionInstruction which already does this
//    }

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
     * @see StyleElement#prepareAttributes()
     */
    @Override
    public void prepareAttributes() {
        // Get mandatory message attribute
        // final String msgAtt = super.getAttributeValue("message");
        final String msgAtt = super.getAttributeValue("", "message");
        if (msgAtt == null) {
            reportAbsence("message");
        }

        //final AttributeInfo attributeInfo = new AttributeInfo(NodeName);

        this.message = super.makeAttributeValueTemplate(msgAtt, null);

        // final String orientationAtt = super.getAttributeValue("orientation");
        final String orientationAtt = super.getAttributeValue("", "orientation");
        this.orientation = orientationAtt != null ? super.makeAttributeValueTemplate(orientationAtt, null) : null;
    }

    /**
     * @see StyleElement#validate(ComponentDeclaration)
     */
    @Override
    public void validate(ComponentDeclaration decl) throws XPathException {
        super.validate(decl);
        //super.checkWithinTemplate();
        message = typeCheck("message", message);
        if (orientation != null) {
            orientation = typeCheck("orientation", orientation);
        }
    }

    @Override
    public void postValidate() throws XPathException {
        if(!this.hasChildNodes()) {
            this.compileError("barcode should have child element");
        }
        // todo: enumerate child nodes and make sure that a BarcodeNonRootStyleElement exists
    }

    /**
     * @see StyleElement#compile(Compilation, ComponentDeclaration)
     */
    @Override
    public Expression compile(Compilation exec, ComponentDeclaration decl) throws XPathException {
        if (this.isTopLevel()) {
            return null;
        }
        final NodeOverNodeInfo node = NodeOverNodeInfo.wrap(this);
        final Configuration cfg = ConfigurationUtil.buildConfiguration(node);

        return new BarcodeExpression(message, orientation, cfg);
    }

    /**
     * @see StyleElement#isPermittedChild(StyleElement)
     */
    @Override
    protected boolean isPermittedChild(StyleElement styleElement) {
        // I am allowing anything right now
        // todo: Consider doing something like:
        //  return styleElement instanceof BarcodeNonRootStyleElement;
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
         * @see Expression#getImplementationMethod()
         */
        @Override
        public int getImplementationMethod() {
            return Expression.PROCESS_METHOD;
        }

//        @Override
//        public ItemType getItemType(TypeHierarchy typeHierarchy) {
//          return Type.ITEM_TYPE; // that's what SimpleExpression would do
//        }

        /*
         * Diagnostic print of expression structure. The abstract expression tree is written to the supplied output destination.
         * @param destination the expression presenter used to display the structure
         */
//        @Override
//        public void explain(ExpressionPresenter destination) {
//
//        }

        /*
         * Determine the static cardinality of the expression. This implementation returns "zero or more", which can be overridden in a subclass.
         * @return the computed cardinality, as one of the values StaticProperty.ALLOWS_ZERO_OR_ONE, StaticProperty.EXACTLY_ONE, StaticProperty.ALLOWS_ONE_OR_MORE, StaticProperty.ALLOWS_ZERO_OR_MORE
         */
//        @Override
//        protected int computeCardinality() {
//            return 0;
//        }

        /*
         * Copy an expression. This makes a deep copy.
         * @return A deep copy of the expression
         */
//        @Override
//        public Expression copy(RebindingMap rebindings) {
//            throw new UnsupportedOperationException("copy");
//        }

//        @Override
//        public CharSequence evaluateAsString(XPathContext context) throws XPathException {
//            return super.evaluateAsString(context);
//        }

        /**
         * This is the replacement of what used to be "void process(XPathContext context) throws XPathException" in Saxon 8
         *
         * Generally it is advisable, if calling iterate() to process a supplied sequence, to call it only once; if the value is required more than once, it should first be converted to a GroundedValue by calling the utility method SequenceTool.toGroundedValue().
         *
         * If the expected value is a single item, the item should be obtained by calling Sequence.head(): it cannot be assumed that the item will be passed as an instance of Item or AtomicValue.
         *
         * It is the caller's responsibility to perform any type conversions required to convert arguments to the type expected by the callee. An exception is where this Callable is explicitly an argument-converting wrapper around the original Callable.
         *
         * @param context   the dynamic evaluation context
         * @param arguments the values of the arguments, supplied as Sequences.
         *                  <p>Generally it is advisable, if calling iterate() to process a supplied sequence, to
         *                  call it only once; if the value is required more than once, it should first be converted
         *                  to a {@link GroundedValue} by calling the utility method
         *                  SequenceTool.toGroundedValue().</p>
         *                  <p>If the expected value is a single item, the item should be obtained by calling
         *                  Sequence.head(): it cannot be assumed that the item will be passed as an instance of
         *                  {@link Item} or {@link net.sf.saxon.value.AtomicValue}.</p>
         *                  <p>It is the caller's responsibility to perform any type conversions required
         *                  to convert arguments to the type expected by the callee. An exception is where
         *                  this Callable is explicitly an argument-converting wrapper around the original
         *                  Callable.</p>
         * @return the result of the evaluation, in the form of a Sequence. It is the responsibility
         *         of the callee to ensure that the type of result conforms to the expected result type.
         * @throws XPathException if a dynamic error occurs during the evaluation of the expression
         * @see net.sf.saxon.expr.Callable#call(XPathContext, Sequence[])
         *
         * This method signature will change in a later release of Saxon 9
         */
        @Override
        public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
            final String effMessage = (message.evaluateAsString(context)).toString();
            int effOrientation = 0;

            if (orientation != null) {
                final String degrees = orientation.evaluateAsString(context).toString();
                try {
                    effOrientation = BarcodeDimension.normalizeOrientation(Integer.parseInt(degrees));
                } catch (final NumberFormatException e) {
                    throw new ValidationException(e);
                } catch (final IllegalArgumentException e) {
                    throw new ValidationException(e);
                }
            }

            try {
                final Receiver out = context.getController().getPrincipalResult();
                //final SequenceReceiver out = context.getController().getPrincipalResult();

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

                final Item item = new DocumentNodeWrapper(svg.getDOM(), wrapper);
                //final Item item = new DOMNodeWrapper(svg.getDOM(), wrapper, null, -1);

                out.append(item, this.getLocation(), 1);

                return null; // todo: fix this!!

                // todo: fix this code
                // in later Saxon use something like this:
                // final GroundedValue val1 = SequenceTool.toGroundedValue(new ArrayIterator<Item>(arguments[0].head()));
                // final Item val2 = arguments[0].head();

                //return arguments[0]; // todo: this is probably not right
                //return evaluateItem(context);
                //return item.makeRepeatable();
                //return wrapper.getRootNode();

            } catch (final ConfigurationException e) {
                throw new XPathException("(Barcode4J) " + e.getMessage());
            } catch (final BarcodeException e) {
                throw new XPathException("(Barcode4J) " + e.getMessage());
            }
        }

        static class DocumentNodeWrapper extends DOMNodeWrapper {
            protected DocumentNodeWrapper(Node node, DocumentWrapper docWrapper) {
                super(node, docWrapper, null, -1);
            }
        }

        /*
        * Diagnostic print of expression structure. The abstract expression tree is written to the supplied output destination.
        */
//        @Override
//        public void export(ExpressionPresenter destination) throws XPathException {
//            super.export(destination);
//        }

        /*
         * Process the instruction, without returning any tail calls
         * @param context The dynamic context, giving access to the current node, the current variables, etc.
         * @throws XPathException
         */
        //@Override
//        public void process(XPathContext context) throws XPathException {
//            String effMessage = (message.evaluateAsString(context)).toString();
//            int effOrientation = 0;
//            if (orientation != null) {
//                String s = (orientation.evaluateAsString(context)).toString();
//                try {
//                    effOrientation = Integer.parseInt(s);
//                    effOrientation = BarcodeDimension
//                            .normalizeOrientation(effOrientation);
//                } catch (NumberFormatException nfe) {
//                    throw new ValidationException(nfe);
//                } catch (IllegalArgumentException iae) {
//                    throw new ValidationException(iae);
//                }
//            }
//
//            try {
//                SequenceReceiver out = context.getReceiver();
//
//                // Acquire BarcodeGenerator
//                final BarcodeGenerator gen = BarcodeUtil.getInstance()
//                        .createBarcodeGenerator(cfg);
//
//                // Setup Canvas
//                final SVGCanvasProvider svg;
//                if (cfg.getAttributeAsBoolean("useNamespace", true)) {
//                    svg = new SVGCanvasProvider(cfg.getAttribute("prefix", "svg"),
//                            effOrientation);
//                } else {
//                    svg = new SVGCanvasProvider(false, effOrientation);
//                }
//                // Generate barcode
//                gen.generateBarcode(svg, effMessage);
//
//                DocumentWrapper wrapper = new DocumentWrapper(svg.getDOM(),
//                        SVGCanvasProvider.SVG_NAMESPACE, context.getConfiguration());
//                out.append(wrapper, this.getLocationId(), 1);
//
//            } catch (ConfigurationException ce) {
//                throw new XPathException("(Barcode4J) " + ce.getMessage());
//            } catch (BarcodeException be) {
//                throw new XPathException("(Barcode4J) " + be.getMessage());
//            }
//        }

    }
}
