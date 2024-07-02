package org.krysalis.barcode4j.image.loader;

import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.BarcodeDimension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.krysalis.barcode4j.image.loader.ImageBarcode.BARCODE_IMAGE_FLAVOR;
import static org.krysalis.barcode4j.image.loader.ImageBarcode.MESSAGE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ImageBarcodeTest {

    private ImageBarcode imageBarcode;

    @Mock private ImageInfo info;
    @Mock private Configuration barcodeXML;
    @Mock private BarcodeDimension bardim;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.imageBarcode = new ImageBarcode(info, barcodeXML, bardim);
    }

    @Test
    @DisplayName("Flavour should be Barcode4J")
    void getFlavor() {
        assertEquals(BARCODE_IMAGE_FLAVOR, this.imageBarcode.getFlavor());
    }

    @Test
    @DisplayName("Should be cacheable")
    void isCacheable() {
        assertTrue(this.imageBarcode.isCacheable());
    }

    @Test
    void getBarcodeXML() {
        assertEquals(this.barcodeXML, imageBarcode.getBarcodeXML());
    }

    @Test
    void getBarcodeDimension() {
        assertEquals(this.bardim, imageBarcode.getBarcodeDimension());
    }

    @Test
    @DisplayName("Should attempt to get message from map first")
    void getMessageViaCustomObjects() throws ConfigurationException {
        Map<Object,Object> props = new HashMap<>();
        props.put(MESSAGE, "some test message");

        when(info.getCustomObjects()).thenReturn(props);

        assertEquals("some test message", imageBarcode.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should attempt to get message from barcode XML attributes if not found in map")
    @ValueSource(strings = { "message", "msg" })
    void getMessageViaBarcodeXML(final String key) throws ConfigurationException {
        when(info.getCustomObjects()).thenReturn(Collections.emptyMap());

        if (key.equals("msg")) {
            when(barcodeXML.getAttribute(eq("message"))).thenThrow(ConfigurationException.class);
        }
        when(barcodeXML.getAttribute(eq(key))).thenReturn("test message");

        assertEquals("test message", imageBarcode.getMessage());
    }
}
