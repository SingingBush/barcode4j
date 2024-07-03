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

    /*
     * Generally a BarcodeStyleElement will have BarcodeNonRootStyleElement as children
     * <code>
     *     <barcode4j:barcode message="{message}" orientation="90">
     *         <barcode4j:code128>
     *             <barcode4j:height>15mm</barcode4j:height>
     *             <barcode4j:module-width>0.21mm</barcode4j:module-width>
     *             <barcode4j:codesets>ABC</barcode4j:codesets>
     *             <barcode4j:quiet-zone enabled="true">10mw</barcode4j:quiet-zone>
     *         </barcode4j:code128>
     *     </barcode4j:barcode>
     * </code>
     */
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
