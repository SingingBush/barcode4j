package org.krysalis.barcode4j.image.loader;

import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeConstants;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.krysalis.barcode4j.image.loader.ImageLoaderFactoryBarcode.MIME_TYPE;

class PreloaderBarcodeTest {

    private PreloaderBarcode preloader;

    @BeforeEach
    void setUp() {
        this.preloader = new PreloaderBarcode();
    }

    @Test
    @DisplayName("Should preload image info from DOMSource")
    void preloadImageFromDOMSource() throws IOException, ParserConfigurationException {
        final DOMSource source = new DOMSource(constructBarcodeDocument("0123456789"));

        final ImageContext ctx = new DefaultImageContext();

        final ImageInfo imageInfo = preloader.preloadImage(BarcodeConstants.NAMESPACE, source, ctx);

        assertNotNull(imageInfo);
        assertEquals(MIME_TYPE, imageInfo.getMimeType());
        assertEquals("0123456789", imageInfo.getCustomObjects().get("Message"));
    }

    private Document constructBarcodeDocument(final String message) throws ParserConfigurationException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        final Document doc = docBuilder.newDocument();

        //final Element rootElement = doc.createElementNS(SVG_NAMESPACE, "svg");
        final Element rootElement = doc.createElementNS(BarcodeConstants.NAMESPACE, "barcode");
        rootElement.setAttribute("message", message);
        rootElement.setAttribute("orientation", "90");

        // <bc:intl2of5></bc:intl2of5>
        final Element barcodeType = doc.createElementNS(BarcodeConstants.NAMESPACE, "intl2of5");
        rootElement.appendChild(barcodeType);

        // <bc:module-width>2mm</bc:module-width>
//        final Element moduleWidth = doc.createElementNS(BarcodeConstants.NAMESPACE, "module-width");
//        moduleWidth.appendChild(doc.createTextNode("2mm"));
//        barcodeType.appendChild(moduleWidth);

        doc.appendChild(rootElement);

        return doc;
    }
}
