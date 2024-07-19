package org.krysalis.barcode4j.image.loader;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.ImageXMLDOM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.krysalis.barcode4j.image.loader.ImageConverterBarcodeXML2Barcode.BARCODE_XML_FLAVOR;

class ImageConverterBarcodeXML2BarcodeTest {

    private ImageConverterBarcodeXML2Barcode imageConverter;

    @BeforeEach
    void setUp() {
        this.imageConverter = new ImageConverterBarcodeXML2Barcode();
    }

    @Test
    @DisplayName("Should convert ImageXMLDOM to ImageBarcode")
    void testConvertImageXMLDOM() throws ImageException, IOException, ParserConfigurationException, ConfigurationException {
        final ImageInfo info = new ImageInfo("", ImageLoaderFactoryBarcode.MIME_TYPE);

        final Document document = constructBarcodeDocument("0123456789012");

        final Image image = new ImageXMLDOM(info, document, BARCODE_XML_FLAVOR);

        final Image converted = imageConverter.convert(image, null);

        assertNotNull(converted);
        assertEquals(ImageLoaderFactoryBarcode.MIME_TYPE, converted.getInfo().getMimeType());
        assertEquals(ImageBarcode.BARCODE_IMAGE_FLAVOR, converted.getFlavor());
        assertTrue(ImageBarcode.class.isAssignableFrom(converted.getClass()));

        final ImageBarcode barcode = (ImageBarcode) converted;
        assertEquals("", barcode.getBarcodeXML().getNamespace());
        assertEquals("barcode", barcode.getBarcodeXML().getName());
        assertEquals("0123456789012", barcode.getMessage());
        assertEquals("90", barcode.getBarcodeXML().getAttribute("orientation"));
    }

    @Test
    void getSourceFlavor() {
        assertEquals(BARCODE_XML_FLAVOR, this.imageConverter.getSourceFlavor());
    }

    @Test
    void getTargetFlavor() {
        assertEquals(ImageBarcode.BARCODE_IMAGE_FLAVOR, this.imageConverter.getTargetFlavor());
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
