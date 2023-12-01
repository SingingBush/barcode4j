package org.krysalis.barcode4j.fop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.flow.InstreamForeignObject;
import org.apache.fop.fo.pagination.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeConstants;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.AttributesImpl;


/**
 * @author Samael Bate (singingbush)
 * created on 23/11/2023
 */
class BarcodeElementTest {

    private static final String BARCODE_ELEMENT_NAME = "barcode";

    private BarcodeElement barcodeElement;

    @BeforeEach
    void setUp() {
        final FONode ifo = new InstreamForeignObject(new Root(null));
        this.barcodeElement = new BarcodeElement(ifo);

        assertEquals(BarcodeConstants.NAMESPACE, this.barcodeElement.getNamespaceURI());
        assertEquals("bc", this.barcodeElement.getNormalNamespacePrefix());
    }

    @Test
    void processNode() throws FOPException {
        final AttributesImpl attlist = new Attributes2Impl();
        //final PropertyList propertyList = new StaticPropertyList(null, null);

        assertNull(this.barcodeElement.getDOMDocument());

        this.barcodeElement.processNode(BARCODE_ELEMENT_NAME, null, attlist, null);

        assertNotNull(this.barcodeElement.getDOMDocument());
        assertEquals(1, this.barcodeElement.getDOMDocument().getChildNodes().getLength());
        assertEquals("bc:barcode", this.barcodeElement.getName());
        assertEquals("bc", this.barcodeElement.getNormalNamespacePrefix());
        assertEquals(BarcodeConstants.NAMESPACE, this.barcodeElement.getNamespaceURI());
    }

//    @Test
//    void getDimension() {
//        this.element.getDimension(null);
//    }
}
