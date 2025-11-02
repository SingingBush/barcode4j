package org.krysalis.barcode4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.impl.qr.QRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Samael Bate (singingbush)
 * created on 13/10/2025
 */
public class BarcodeSchemaTest {

    private static final Logger _log = LoggerFactory.getLogger(BarcodeSchemaTest.class);

    private static final Validator _validator;

    static {
        try {
            _validator = initValidator();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBasicXml() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:qr/>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testPDF417Barcode() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:pdf417>\n" +
            "        <bc:module-width>0.705554mm</bc:module-width>\n" +
            "        <bc:row-height>3mm</bc:row-height>\n" +
            "        <bc:columns>2</bc:columns>\n" +
            "        <bc:min-columns>2</bc:min-columns>\n" +
            "        <bc:max-columns>2</bc:max-columns>\n" +
            "        <bc:min-rows>3</bc:min-rows>\n" +
            "        <bc:max-rows>90</bc:max-rows>\n" +
            "        <bc:ec-level>0</bc:ec-level>\n" +
            "        <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "    </bc:pdf417>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testBasicNamespacedXml() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:aztec/>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testNamespacedParameterised_Aztec_Barcode() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:aztec>\n" +
            "        <bc:module-width>1.8mm</bc:module-width>\n" +
            "        <bc:encoding>ISO-8859-1</bc:encoding>\n" +
            "        <bc:ec-level>23</bc:ec-level>\n" +
            "        <bc:layers>0</bc:layers>\n" +
            "        <bc:quiet-zone enabled=\"false\"/>\n" +
            "    </bc:aztec>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @ParameterizedTest
    @ValueSource(chars = {
        QRConstants.ERROR_CORRECTION_LEVEL_L,
        QRConstants.ERROR_CORRECTION_LEVEL_M,
        QRConstants.ERROR_CORRECTION_LEVEL_Q,
        QRConstants.ERROR_CORRECTION_LEVEL_H,
    })
    void testNamespacedParameterised_QRCode_Barcode(final char ecLevel) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:qr>\n" +
            "    <bc:module-width>1.8mm</bc:module-width>\n" +
            "    <bc:encoding>UTF-8</bc:encoding>\n" +
            String.format("    <bc:ec-level>%s</bc:ec-level>\n", ecLevel) +
            "    <bc:min-symbol-size>30</bc:min-symbol-size>\n" +
            "    <bc:quiet-zone>12mm</bc:quiet-zone>\n" +
            "    <bc:max-symbol-size>60x60</bc:max-symbol-size>\n" +
            "  </bc:qr>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "force-none",
        "force-square",
        "force-rectangle"
    })
    void testNamespacedParameterised_DataMatrix_Barcode(final String shape) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:datamatrix>\n" +
            "    <bc:module-width>1.8mm</bc:module-width>\n" +
            String.format("    <bc:shape>%s</bc:shape>\n", shape) +
            "    <bc:min-symbol-size>30</bc:min-symbol-size>\n" +
            "    <bc:max-symbol-size>60x60</bc:max-symbol-size>\n" +
            "    <bc:quiet-zone>12</bc:quiet-zone>\n" +
            "  </bc:datamatrix>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testNamespacedParameterised_PDF417_Barcode() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:pdf417>\n" +
            "    <bc:module-width>0.705554mm</bc:module-width>\n" +
            "    <bc:encoding>US-ASCII</bc:encoding>\n" +
            "    <bc:row-height>3mm</bc:row-height>\n" +
            "    <bc:columns>2</bc:columns>\n" +
            "    <bc:min-columns>2</bc:min-columns>\n" +
            "    <bc:max-columns>2</bc:max-columns>\n" +
            "    <bc:min-rows>3</bc:min-rows>\n" +
            "    <bc:max-rows>90</bc:max-rows>\n" +
            "    <bc:ec-level>0</bc:ec-level>\n" +
            "    <bc:enable-eci>true</bc:enable-eci>\n" +
            "    <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "  </bc:pdf417>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @ParameterizedTest
    @ValueSource(strings = {"auto","ignore","add","check"})
    void testNamespacedParameterised_UPC_A_Barcode(final String checksumValue) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:upc-a>\n" +
            "    <bc:module-width>0.705554mm</bc:module-width>\n" +
            String.format("    <bc:checksum>%s</bc:checksum>\n", checksumValue) +
            "    <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "  </bc:upc-a>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @ParameterizedTest
    @ValueSource(strings = {"none","top","bottom"})
    void testNamespacedParameterised_EAN_13_Barcode(final String hrPlacementName) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:ean-13>\n" +
            "    <bc:height>50mm</bc:height>\n" +
            "    <bc:quiet-zone>20mw</bc:quiet-zone>\n" +
            "    <bc:human-readable>\n" +
            String.format("    <bc:placement>%s</bc:placement>\n", hrPlacementName) +
            "    </bc:human-readable>\n" +
            "  </bc:ean-13>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testNamespacedParameterised_Code128_Barcode() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:code128>\n" +
            "    <bc:module-width>0.705554mm</bc:module-width>\n" +
            "    <bc:human-readable>\n" +
            "      <bc:font-name>Courier</bc:font-name>\n" +
            "      <bc:font-size>8pt</bc:font-size>\n" +
            "      <bc:pattern>__.__.______.________</bc:pattern>\n" +
            "    </bc:human-readable>" +
            "    <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "  </bc:code128>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @Test
    void testNamespacedParameterised_EAN_128_Barcode() throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:ean-128>\n" +
            "    <bc:height>18mm</bc:height>\n" +
            "    <bc:human-readable>\n" +
            "      <bc:font-size>20pt</bc:font-size>\n" +
            "      <bc:pattern>(__) _ _______ _________ _</bc:pattern>\n" +
            "      <bc:omit-brackets>true</bc:omit-brackets>\n" +
            "    </bc:human-readable>\n" +
            "    <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "  </bc:ean-128>\n" +
            "</bc:barcode>";

        validate(xml);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "codabar",
        "code39",
        "code128",
        "ean-128",
        "intl2of5",
        "itf-14",
        "ean-13",
        "ean-8",
        "upc-a",
        "upc-e",
        "postnet",
        "royal-mail-cbc",
        "usps4cb",
        "pdf417",
        "datamatrix",
        "qr",
        "aztec"
    })
    void testSymbols(final String symbology) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            String.format("    <bc:%s/>\n", symbology) +
            "</bc:barcode>";

        validate(xml);
    }

    private static Validator initValidator() throws SAXException {
        final URL xsdFileUrl = BarcodeSchemaTest.class.getClassLoader().getResource("barcode.xsd");
        if (xsdFileUrl == null) {
            // in Intellij this could mean that the resources directory isn't marked as a sources root
            fail("Unable to get barcode.xsd from resource folder");
        }
        final File xsdFile = new File(xsdFileUrl.getFile());
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Source schemaFile = new StreamSource(xsdFile);
        final Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    private static void validate(String xml) throws IOException {
        try {
            _validator.validate(new StreamSource(new StringReader(xml)));
        }
        catch (SAXException e) {
            _log.info("Validation failed for XML:\n{}", xml);
            fail("XML failed validation", e);
        }
    }

}
