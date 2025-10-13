package org.krysalis.barcode4j;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    void testBasicXml() throws IOException, SAXException {
        final String xml = "<barcode message=\"3216455597\" xmlns=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <qr/>\n" +
            "</barcode>";

        _validator.validate(new StreamSource(new StringReader(xml)));
    }

    @Test
    void testParameterisedBarcode() throws IOException, SAXException {
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

        _validator.validate(new StreamSource(new StringReader(xml)));
    }

    @Test
    void testBasicNamespacedXml() throws IOException, SAXException {
        final String xml = "<barcode:barcode message=\"3216455597\" xmlns:barcode=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <barcode:aztec/>\n" +
            "</barcode:barcode>";

        _validator.validate(new StreamSource(new StringReader(xml)));
    }

    @Test
    void testNamespacedParameterisedBarcode() throws IOException, SAXException {
        final String xml = "<bc:barcode message=\"3216455597\" xmlns:bc=\"http://barcode4j.krysalis.org/ns\">\n" +
            "    <bc:pdf417>\n" +
            "        <bc:module-width>0.705554mm</bc:module-width>\n" +
            "        <bc:row-height>3mw</bc:row-height>\n" +
            "        <bc:columns>2</bc:columns>\n" +
            "        <bc:min-columns>2</bc:min-columns>\n" +
            "        <bc:max-columns>2</bc:max-columns>\n" +
            "        <bc:min-rows>3</bc:min-rows>\n" +
            "        <bc:max-rows>90</bc:max-rows>\n" +
            "        <bc:ec-level>0</bc:ec-level>\n" +
            "        <bc:quiet-zone enabled=\"false\">123cm</bc:quiet-zone>\n" +
            "    </bc:pdf417>\n" +
            "</bc:barcode>";

        _validator.validate(new StreamSource(new StringReader(xml)));
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
    void testSymbols(final String symbology) throws IOException, SAXException {
        final String xml = "<barcode:barcode message=\"3216455597\" xmlns:barcode=\"http://barcode4j.krysalis.org/ns\">\n" +
            String.format("    <barcode:%s/>\n", symbology) +
            "</barcode:barcode>";

        _validator.validate(new StreamSource(new StringReader(xml)));
    }

    private static Validator initValidator() throws SAXException {
        final File xsdFile = new File(BarcodeSchemaTest.class.getClassLoader().getResource("barcode.xsd").getFile());
        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Source schemaFile = new StreamSource(xsdFile);
        final Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

}
