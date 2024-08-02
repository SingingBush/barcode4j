package org.krysalis.barcode4j.fop;

import org.apache.fop.fo.ElementMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BarcodeElementMappingTest {

    private BarcodeElementMapping elementMapping;

    @BeforeEach
    void setUp() {
        elementMapping = new BarcodeElementMapping();
    }

    @Test
    void ensureTableIsInitialised() {
        final HashMap<String, ElementMapping.Maker> foObjs = elementMapping.getTable();

        assertNotNull(foObjs);
        assertEquals(BarcodeElementMapping.BarcodeRootMaker.class, foObjs.get("barcode").getClass());
        assertEquals(BarcodeElementMapping.BarcodeMaker.class, foObjs.get(ElementMapping.DEFAULT).getClass());
    }

    @Test
    void getDOMImplementation() {
        // the BarcodeElementMapping just calls through to the super class getDefaultDOMImplementation()
        assertNotNull(elementMapping.getDOMImplementation());
    }
}
