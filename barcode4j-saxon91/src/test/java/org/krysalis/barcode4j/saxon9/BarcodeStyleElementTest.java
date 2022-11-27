package org.krysalis.barcode4j.saxon9;

import net.sf.saxon.trans.XPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samael Bate (singingbush)
 * created on 06/02/2022
 */
public class BarcodeStyleElementTest {

    private BarcodeStyleElement element;

    @BeforeEach
    public void setUp() throws Exception {
        this.element = new BarcodeStyleElement();
    }

    @Test
    void testIsInstruction() {
        assertTrue(this.element.isInstruction());
    }

    @Test
    void testMayContainSequenceConstructor() {
        assertTrue(this.element.mayContainSequenceConstructor());
    }

    // todo: get a valid test built up
//    public void testPrepareAttributes() throws XPathException {
//        this.element.prepareAttributes();
//    }

    @Test
    void testValidate() throws XPathException {
        this.element.validate();
    }

    // todo: get a valid test built up
//    public void testCompile() throws XPathException {
//        // prepareAttributes() should be called prior to compile()
//        final Expression expression = this.element.compile(null);
//    }

    @Test
    void testIsPermittedChild() {
        assertTrue(this.element.isPermittedChild(null)); // todo: check if this is ok
    }
}
