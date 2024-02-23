package org.krysalis.barcode4j.examples.fop;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FopExample {

    private final TransformerFactory transformerFactory;

    public static void main(String[] args) {
        final FopExample example = new FopExample();

        try {
            // barcodes
            final StreamResult barcodesXslFo = example.xsltFo(
                new StreamSource(loadResourceFile("barcodes.xml")),
                new StreamSource(loadResourceFile("barcodes2xsl-fo.xsl"))
            );
            //System.out.println(barcodesXslFo.getWriter().toString());
            example.generatePdfFileFromXslFo(new StreamSource(
                new StringReader(barcodesXslFo.getWriter().toString()),
                "barcodes xsl-fo reader"
            ));

            // invoice
            final StreamResult invoiceXslFo = example.xsltFo(
                new StreamSource(loadResourceFile("invoice.xml")),
                new StreamSource(loadResourceFile("invoice2xsl-fo.xsl"))
            );
            //System.out.println(invoiceXslFo.getWriter().toString());
            example.generatePdfFileFromXslFo(new StreamSource(
                new StringReader(invoiceXslFo.getWriter().toString()),
                "invoice xsl-fo reader"
            ));

            // This one is already a FOP file so just render a pdf
            example.generatePdfFileFromXslFo(new StreamSource(
                loadResourceFile("fop-extension-demo.fo")
            ));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // note that the example xsl files used require the xalan implementation of TransformerFactory.
    // There are differences between what Xalan, javax.xml, and Saxon support in terms of XSLT
    public FopExample() {
        this.transformerFactory =
            org.apache.xalan.processor.TransformerFactoryImpl.newInstance();
            // net.sf.saxon.TransformerFactoryImpl.newInstance();
            // javax.xml.transform.TransformerFactory.newInstance();
    }

    private static File loadResourceFile(final String resource) throws URISyntaxException {
        return Paths.get(FopExample.class.getClassLoader().getResource(resource).toURI()).toFile();
    }

    private void generatePdfFileFromXslFo(final StreamSource fopSource) throws FOPException, IOException, TransformerException {
        final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

        final Path filePath = Files.createTempFile("barcode4j-test-", ".pdf");

        try(OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
            final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            final Transformer transformer = this.transformerFactory.newTransformer();

            final Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(fopSource, res);

            System.out.println("To view rendered pdf open " + filePath);
        }
    }

    private StreamResult xsltFo(final StreamSource srcData, final StreamSource srcXsl) throws TransformerException {
        final Transformer trans = this.transformerFactory.newTransformer(srcXsl);

        final StreamResult resultingXslFo = new StreamResult(new StringWriter());

        trans.transform(srcData, resultingXslFo); // apply our data to the XSLT to get XSL-FO

        return resultingXslFo;
    }
}
