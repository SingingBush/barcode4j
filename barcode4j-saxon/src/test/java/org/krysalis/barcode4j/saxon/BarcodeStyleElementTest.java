package org.krysalis.barcode4j.saxon;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.style.XSLDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Samael Bate (singingbush)
 * created on 27/11/2023
 */
public class BarcodeStyleElementTest {

    private static final String BARCODE_ELEMENT_NAME = "barcode";

    private BarcodeStyleElement element;

    @BeforeEach
    void setUp() {
        final StyleElement parent = new XSLDocument();

        this.element = new BarcodeStyleElement();
        assertNull(element.getParent());

        parent.insertChildren(new NodeInfo[]{ this.element }, true, false);
        assertNotNull(element.getParent());
    }

    @Test
    @DisplayName("should be an instruction element")
    void testIsInstruction() {
        assertTrue(element.isInstruction());
    }

    @Test
    void testMayContainSequenceConstructor() {
        assertTrue(element.mayContainSequenceConstructor());
    }

    @Test
    @DisplayName("Should permit nor root barcode child elements")
    void isPermittedChild() {
        assertTrue(element.isPermittedChild(new BarcodeNonRootStyleElement()));

        // it should probably allow child elements for doing logic
        assertTrue(element.isPermittedChild(new net.sf.saxon.style.XSLIf()));
        assertTrue(element.isPermittedChild(new net.sf.saxon.style.XSLLocalVariable()));
        assertTrue(element.isPermittedChild(new net.sf.saxon.style.XSLLocalParam()));

        // this is also the reason mayContainSequenceConstructor() returns true
        assertTrue(element.isPermittedChild(new net.sf.saxon.style.XSLFallback()));

        // todo: Consider disallowing other element types
        //assertFalse(element.isPermittedChild(null));
        //assertFalse(element.isPermittedChild(new net.sf.saxon.style.XSLElement()));
        //assertFalse(element.isPermittedChild(new net.sf.saxon.style.XSLDocument()));
    }
}
