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

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import javax.xml.bind.util.JAXBSource;
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
 * Integration test for using the Fop extension that will output a pdf to the temp dir and
 * display the path to the file so that it can be opened and inspected by hand
 *
 * @author Samael Bate (singingbush)
 * created on 09/11/2023
 */
public class FopTest {

    @Test
    @DisplayName("Use xmlns:barcode within an XSLT to transform StreamSource data to XSL-FO")
    void testTransformStreamSourceToFop() throws Exception {
        final Source srcData = new StreamSource(loadTestResourceFile("barcodes.xml"));

        // capture resulting fop for assertions
        final StreamResult resultingXslFo = new StreamResult(new StringWriter());

        TransformerFactory.newInstance()
            .newTransformer(
                new StreamSource(loadTestResourceFile("barcode-data-XSL-2.xsl"))
            )
            .transform(srcData, resultingXslFo); // apply our data to the XSLT to get XSL-FO

        final String xslFo = resultingXslFo.getWriter().toString();

        assertTrue(xslFo.contains("xmlns:barcode=\"http://barcode4j.krysalis.org/ns\""));

        assertTrue(xslFo.contains("<barcode:barcode message=\"012345678905\">"));
        assertTrue(xslFo.contains("<barcode:upc-A>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"0012345678905\">"));
        assertTrue(xslFo.contains("<barcode:ean-13>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"1234567890\">"));
        assertTrue(xslFo.contains("<barcode:code128>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"00353912341234567895\">"));
        assertTrue(xslFo.contains("<barcode:ean-128>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"Here is some text encoded in a 2D barcode\">"));
        assertTrue(xslFo.contains("<barcode:pdf417>"));

        assertTrue(xslFo.contains("<barcode:datamatrix>"));

        // now ensure the generated XSL-FO can be used by Apache FOP without error
        generatePdfFileFromXslFo(new ByteArrayInputStream(xslFo.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("Use xmlns:barcode within an XSLT to transform JAXBSource data to XSL-FO")
    void testTransformJaxbSourceToFop() throws JAXBException, TransformerException, FOPException, IOException {
        final Source srcData = createDataFromPojos();

        // capture resulting fop for assertions
        final StreamResult resultingXslFo = new StreamResult(new StringWriter());

        // for debugging the JAXB source data:
        // final Marshaller jaxbMarshaller = JAXBContext.newInstance(Data.class).createMarshaller();
        // jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // jaxbMarshaller.marshal(data, new PrintWriter(System.out));

        TransformerFactory.newInstance()
            .newTransformer(
                new StreamSource(loadTestResourceFile("barcode-data-XSL-2.xsl"))
            )
            .transform(srcData, resultingXslFo); // apply our data to the XSLT to get XSL-FO

        final String xslFo = resultingXslFo.getWriter().toString();
        assertTrue(xslFo.contains("xmlns:barcode=\"http://barcode4j.krysalis.org/ns\""));

        assertTrue(xslFo.contains("<barcode:barcode message=\"012345678905\">"));
        assertTrue(xslFo.contains("<barcode:upc-A>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"0012345678905\">"));
        assertTrue(xslFo.contains("<barcode:ean-13>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"1234567890\">"));
        assertTrue(xslFo.contains("<barcode:code128>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"00353912341234567895\">"));
        assertTrue(xslFo.contains("<barcode:ean-128>"));

        assertTrue(xslFo.contains("<barcode:barcode message=\"Here is some text encoded in a 2D barcode\">"));
        assertTrue(xslFo.contains("<barcode:pdf417>"));

        // now ensure the generated XSL-FO can be used by Apache FOP without error
        generatePdfFileFromXslFo(new ByteArrayInputStream(xslFo.getBytes(StandardCharsets.UTF_8)));
    }

    private Source createDataFromPojos() throws JAXBException {
        final Barcode upcA = new Barcode();
        upcA.setMessage("012345678905");
        upcA.setType("upc-A");

        final Barcode ean13 = new Barcode();
        ean13.setMessage("0012345678905");
        ean13.setType("ean-13");

        final Barcode code128 = new Barcode();
        code128.setMessage("1234567890");
        code128.setType("code128");

        final Barcode ean128 = new Barcode();
        ean128.setMessage("00353912341234567895");
        ean128.setType("ean-128");

        final Barcode pdf417 = new Barcode();
        pdf417.setMessage("Here is some text encoded in a 2D barcode");
        pdf417.setType("pdf417");

        final Data data = new Data();
        final List<Barcode> barcodes = new java.util.ArrayList<>();
        barcodes.add(upcA);
        barcodes.add(ean13);
        barcodes.add(code128);
        barcodes.add(ean128);
        barcodes.add(pdf417);

        data.setBarcodes(barcodes);

        return new JAXBSource( JAXBContext.newInstance(Data.class, Barcode.class) , data);
    }

    private void generatePdfFileFromXslFo(final InputStream xslFo) throws FOPException, IOException, TransformerException {
        final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        final Path filePath = Files.createTempFile("barcode4j-fop-test-", ".pdf");

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

    @XmlRootElement(name = "barcode")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Barcode {
        @XmlElement(name = "type", required = true)
        private String type;
        @XmlElement(name = "message", required = true)
        private String message;

        public Barcode() {}

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Data {

        @XmlElementWrapper
        @XmlElement(name = "barcode")
        private List<Barcode> barcodes;

        public Data() {}

        public List<Barcode> getBarcodes() {
            return barcodes;
        }

        public void setBarcodes(List<Barcode> barcodes) {
            this.barcodes = barcodes;
        }
    }
}
