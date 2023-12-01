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
package org.krysalis.barcode4j.saxon8;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.Transform;
import net.sf.saxon.TransformerFactoryImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for the Saxon 8.x extension.
 *
 * @author Jeremias Maerki
 * @version $Id: SaxonExtTest.java,v 1.2 2004-09-04 20:26:15 jmaerki Exp $
 */
public class SaxonExtTest {

    @ParameterizedTest
    @DisplayName("Do XML transform using Saxon's implementation of TransformerFactory")
    @ValueSource(strings = { "xml/saxon8-test.xsl", "xml/saxon8-html-transform.xsl" })
    void testSaxon8Ext_UsingJAXP(final String xsltTemplate) throws Exception {
        final TransformerFactory factory = new TransformerFactoryImpl(); // Saxon's implementation of TransformerFactory
        final Transformer trans = factory.newTransformer(
                new StreamSource(loadTestResourceFile(xsltTemplate))
        );

        final Source xmlDataSource = new StreamSource(loadTestResourceFile("xml/xslt-test.xml"));

        final StringWriter writer = new StringWriter();
        final Result result = new StreamResult(writer);

        trans.transform(xmlDataSource, result);

        final String output = writer.getBuffer().toString();
        assertTrue(output.contains("<svg:svg xmlns:svg=\"http://www.w3.org/2000/svg\""));
        assertTrue(output.contains("<svg:g "));
        assertTrue(output.contains("<svg:rect "));
        assertTrue(output.contains("<svg:text "));
        assertTrue(output.contains("1234567890")); // the text value of the barcode in the XML data
        //System.out.println(output);

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

    @Test
    @DisplayName("Do XML transform using Saxon's Transform class (used by CLI)")
    @Disabled // only here for an example of using Saxon CLI
    void testSaxon8Ext_UsingSaxon() throws TransformerException, IOException {
        final Configuration configuration = new Configuration(); // Configuration.makeSchemaAwareConfiguration(); // Need paid version for com.saxonica.validate.SchemaAwareConfiguration
        configuration.setAllNodesUntyped(true); // because we have no license when testing (cannot be Schema Aware)

        final TransformerFactory factory = new TransformerFactoryImpl(configuration); // use Saxon implementation of TransformerFactory!
        final Templates xslt = factory.newTemplates(new StreamSource(loadTestResourceFile("xml/saxon8-test.xsl")));
        // factory.getAssociatedStylesheet()

        final Source xmlInput = new StreamSource(loadTestResourceFile("xml/xslt-test.xml"));
        final File outputFile = Files.createTempFile("barcode4j-saxon-", ".xml").toFile(); // or null for System.out
        final ArrayList<String> parameterList = new ArrayList<>();
        final String initialMode = null; // either expanded Clark notation ("{uri}local"), or "local", or null

        final Transform transform = new Transform();
        transform.processFile(xmlInput, xslt, outputFile, parameterList, initialMode);

        System.out.println("Created temp file: " + outputFile.getAbsolutePath());
    }

    private File loadTestResourceFile(final String resource) {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource(resource).toURI()).toFile();
        } catch (final URISyntaxException e) {
            fail("Could no load resource : " + resource);
        }
        return null;
    }

}
