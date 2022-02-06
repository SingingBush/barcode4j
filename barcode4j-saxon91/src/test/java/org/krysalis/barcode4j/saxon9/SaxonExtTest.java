/*
 * Copyright 2003-2004 Jeremias Maerki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.saxon9;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.*;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.Transform;
import net.sf.saxon.TransformerFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for the Saxon 9.1 extension.
 *
 * @author Samael Bate (singingbush)
 */
public class SaxonExtTest {

    private static Configuration configuration;

    @BeforeAll
    static void beforeAll() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @DisplayName("Do XML transform using Saxon's implementation of TransformerFactory (StreamSource)")
    @ValueSource(strings = { "xml/saxon9-xml-transform.xsl", "xml/saxon9-html-transform.xsl" })
    void testSaxon9Ext_UsingSaxonTransformerFactoryWithStreamSource(final String xsltTemplate) throws Exception {
        final TransformerFactory factory = new TransformerFactoryImpl(configuration); // use Saxon implementation of TransformerFactory!
        final Transformer trans = factory.newTransformer(
                new StreamSource(loadTestResourceFile(xsltTemplate))
        );

        final Source xmlDataSource = new StreamSource(loadTestResourceFile("xml/xslt-test.xml")); // barcodes

        final StreamResult result = new StreamResult(new StringWriter());

        trans.transform(xmlDataSource, result);

        final String output = result.getWriter().toString();

        assertTrue(output.contains("<svg xmlns=\"http://www.w3.org/2000/svg\""));
        assertTrue(output.contains("<g "));
        assertTrue(output.contains("<rect "));
        assertTrue(output.contains("<text "));
        assertTrue(output.contains("1234567890")); // the text value of the barcode in the XML data

        final Path tempFile = Files.createTempFile("barcode4j-test-", xsltTemplate.contains("html") ? ".html" : ".xml");
        Files.write(tempFile, output.getBytes(StandardCharsets.UTF_8));
        System.out.println("Created temp file: " + tempFile.toAbsolutePath());
    }

    @ParameterizedTest
    @DisplayName("Do XML transform using Saxon's implementation of TransformerFactory (JAXBSource)")
    @ValueSource(strings = { "xml/saxon9-xml-transform.xsl", "xml/saxon9-html-transform.xsl" })
    void testSaxon9Ext_UsingSaxonTransformerFactoryWithJAXBSource(final String xsltTemplate) throws Exception {
        final TransformerFactory factory = new TransformerFactoryImpl(configuration); // use Saxon implementation of TransformerFactory!
        final Transformer trans = factory.newTransformer(
            new StreamSource(loadTestResourceFile(xsltTemplate))
        );

        final Source xmlDataSource = new JAXBSource(
            JAXBContext.newInstance(Barcodes.class, Barcode.class),
            new Barcodes(Collections.singletonList(new Barcode("code128", "1234567890")))
        );

        final StreamResult result = new StreamResult(new StringWriter());

        trans.transform(xmlDataSource, result);

        final String output = result.getWriter().toString();

        assertTrue(output.contains("<svg xmlns=\"http://www.w3.org/2000/svg\""));
        assertTrue(output.contains("<g "));
        assertTrue(output.contains("<rect "));
        assertTrue(output.contains("<text "));
        assertTrue(output.contains("1234567890")); // the text value of the barcode in the data source

        final Path tempFile = Files.createTempFile("barcode4j-test-", xsltTemplate.contains("html") ? ".html" : ".xml");
        Files.write(tempFile, output.getBytes(StandardCharsets.UTF_8));
        System.out.println("Created temp file: " + tempFile.toAbsolutePath());
    }

    @Test
    @Disabled // only here for an example of using Saxon CLI
    void testSaxon8Ext_UsingSaxonCli() throws Exception {
        final String outputFile = Files.createTempFile("barcode4j-saxon-", ".xml").toString();

        final String[] args = { "-o", outputFile, loadTestResourceFile("xml/xslt-test.xml").getAbsolutePath(), loadTestResourceFile("xml/saxon8-test.xsl").getAbsolutePath() };

        Transform.main(args);

        System.out.println("Created temp file: " + outputFile);
    }

    private File loadTestResourceFile(final String resource) {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource(resource).toURI()).toFile();
        } catch (final URISyntaxException e) {
            fail("Could no load resource : " + resource);
        }
        return null;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class Barcodes {
        @XmlElement(name="barcode")
        private List<Barcode> barcodes;

        public Barcodes() {}

        public Barcodes(List<Barcode> barcodes) {
            this.barcodes = barcodes;
        }
    }

    @XmlRootElement(name = "barcode")
    @XmlType(propOrder = { "type", "message" })
    private static class Barcode {
        private String type;
        private String message;

        public Barcode() {}

        public Barcode(String type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        @XmlElement(name = "type")
        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        @XmlElement(name = "message")
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
