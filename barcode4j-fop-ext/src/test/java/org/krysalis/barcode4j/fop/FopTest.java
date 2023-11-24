/*
 * Copyright 2023 Samael Bate (singingbush).
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
package org.krysalis.barcode4j.fop;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Samael Bate (singingbush)
 * created on 09/11/2023
 */
public class FopTest {

    @Test
    @DisplayName("Use xmlns:barcode within an XSLT to produce XSL-FO")
    void testUseOfBarcode4jNamespaceForXslTransform() throws Exception {
        final TransformerFactory factory = TransformerFactory.newInstance();

        final Transformer trans = factory.newTransformer(
            new StreamSource(loadTestResourceFile("barcode-data-XSL-2.xsl"))
        );

        final Source srcData = new StreamSource(loadTestResourceFile("barcodes.xml"));

        final StreamResult resultingXslFo = new StreamResult(new StringWriter());

        trans.transform(srcData, resultingXslFo); // apply our data to the XSLT to get XSL-FO

        final String xslFo = resultingXslFo.getWriter().toString();
        //System.out.println(xslFo);

        assertTrue(xslFo.contains("xmlns:barcode=\"http://barcode4j.krysalis.org/ns\""));

        assertTrue(xslFo.contains("<barcode:barcode message=\"012345678905\">"));
        assertTrue(xslFo.contains("<barcode:upc-A>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"0012345678905\">"));
        assertTrue(xslFo.contains("<barcode:ean-13>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"1234567890\">"));
        assertTrue(xslFo.contains("<barcode:code128>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"Here is some text encoded in a 2D barcode\">"));
        assertTrue(xslFo.contains("<barcode:pdf417>"));

        assertTrue(xslFo.contains("<barcode:datamatrix>"));

        // now ensure the generated XSL-FO can be used by Apache FOP without error
        generatePdfFileFromXslFo(new ByteArrayInputStream(xslFo.getBytes(StandardCharsets.UTF_8)));
    }

    private void generatePdfFileFromXslFo(final InputStream xslFo) throws FOPException, IOException, TransformerException {
        final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        final Path filePath = Files.createTempFile("barcode4j-test-", ".pdf");

        try(OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
            final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            final Transformer transformer = TransformerFactory.newInstance().newTransformer();

            final Source src = new StreamSource(xslFo);
            final Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(src, res);

            System.out.println("To view rendered pdf open " + filePath);
        }
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
