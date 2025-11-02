package org.krysalis.barcode4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.impl.qr.QRConstants;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
        final String xml = "<barcode message=\"3216455597\" xmlns=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <qr/>\n" +
            "</barcode>";

        validate(xml);
    }

    @Test
    void testParameterisedBarcode() throws IOException {
        final String xml = "<barcode message=\"3216455597\" xmlns=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <pdf417>\n" +
            "        <module-width>0.705554mm</module-width>\n" +
            "        <row-height>3mw</row-height>\n" +
            "        <columns>2</columns>\n" +
            "        <min-columns>2</min-columns>\n" +
            "        <max-columns>2</max-columns>\n" +
            "        <min-rows>3</min-rows>\n" +
            "        <max-rows>90</max-rows>\n" +
            "        <ec-level>0</ec-level>\n" +
            "        <quiet-zone enabled=\"false\">123cm</quiet-zone>\n" +
            "    </pdf417>\n" +
            "</barcode>";

        validate(xml);
    }

    @Test
    void testBasicNamespacedXml() throws IOException {
        final String xml = "<barcode:barcode message=\"3216455597\" xmlns:barcode=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <barcode:aztec/>\n" +
            "</barcode:barcode>";

        validate(xml);
    }

    // Supported values for encodings are the strings defined in org.krysalis.barcode4j.tools.ECIUtil
    // We don't support underscores as used in the names on StandardCharsets enum.
    @ParameterizedTest
    @ValueSource(strings = {
        "Cp437",
        "ISO-8859-1", // default for both QR and Aztec. (Do not use "ISO_8859_1" as per StandardCharsets enum name)
        "ISO-8859-2",
        "ISO-8859-3",
        "ISO-8859-4",
        "ISO-8859-5",
        "ISO-8859-6",
        "ISO-8859-7",
        "ISO-8859-8",
        "ISO-8859-9",
        "ISO-8859-10",
        "ISO-8859-11",
        "ISO-8859-13",
        "ISO-8859-14",
        "ISO-8859-15",
        "ISO-8859-16",
        "SJIS",
        "Cp1250",
        "Cp1251",
        "Cp1252",
        "Cp1256",
        "UnicodeBigUnmarked",
        "UTF-8",
        "US-ASCII",
        "Big5",
        "GB18030"
    })
    void testNamespacedParameterised_Aztec_Barcode(final String encoding) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:aztec>\n" +
            "        <bc:module-width>1.8mm</bc:module-width>\n" +
            String.format("        <bc:encoding>%s</bc:encoding>\n", encoding) +
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

    @ParameterizedTest
    @ValueSource(strings = {
        "3in",
        "3pt",
        "3cm",
        "3mm",
        "3mw"
    })
    void testNamespacedParameterised_PDF417_Barcode(final String rowHeight) throws IOException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "  <bc:pdf417>\n" +
            "    <bc:module-width>0.705554mm</bc:module-width>\n" +
            "    <bc:encoding>US-ASCII</bc:encoding>\n" +
            String.format("    <bc:row-height>%s</bc:row-height>\n", rowHeight) +
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
    @ValueSource(strings = {
        "auto",
        "ignore",
        "add",
        "check"
    })
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
    @ValueSource(strings = {
        "none",
        "top",
        "bottom"
    })
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
        final String xml = "<barcode:barcode message=\"3216455597\" xmlns:barcode=\"http://barcode4j.krysalis.org/ns\">\n" +
            String.format("    <barcode:%s/>\n", symbology) +
            "</barcode:barcode>";

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
        } catch (final SAXException e) {
            if (SAXParseException.class.isAssignableFrom(e.getClass())) {
                final SAXParseException spe = (SAXParseException) e;
                System.out.printf("XML validation failed at line %s, column %s:\n%s%n", spe.getLineNumber(), spe.getColumnNumber(), xml);
            }
            fail(e.getMessage(), e);
        }
    }

}
