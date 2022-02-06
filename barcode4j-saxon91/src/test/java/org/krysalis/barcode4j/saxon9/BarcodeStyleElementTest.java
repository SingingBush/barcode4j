package org.krysalis.barcode4j.saxon9;

import junit.framework.TestCase;
import net.sf.saxon.trans.XPathException;

/**
 * @author Samael Bate (singingbush)
 * created on 06/02/2022
 */
public class BarcodeStyleElementTest extends TestCase {

    private BarcodeStyleElement element;

    public void setUp() throws Exception {
        super.setUp();

        this.element = new BarcodeStyleElement();
    }

    public void testIsInstruction() {
        assertTrue(this.element.isInstruction());
    }

    public void testMayContainSequenceConstructor() {
        assertTrue(this.element.mayContainSequenceConstructor());
    }

    // todo: get a valid test built up
//    public void testPrepareAttributes() throws XPathException {
//        this.element.prepareAttributes();
//    }

    public void testValidate() throws XPathException {
        this.element.validate();
    }

    // todo: get a valid test built up
//    public void testCompile() throws XPathException {
//        // prepareAttributes() should be called prior to compile()
//        final Expression expression = this.element.compile(null);
//    }

    public void testIsPermittedChild() {
        assertTrue(this.element.isPermittedChild(null)); // todo: check if this is ok
    }
}