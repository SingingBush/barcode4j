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
package org.krysalis.barcode4j.saxon;

import com.saxonica.config.EnterpriseConfiguration;
import com.saxonica.config.EnterpriseTransformerFactory;
import net.sf.saxon.Configuration;
import net.sf.saxon.jaxp.SaxonTransformerFactory;
import net.sf.saxon.lib.FeatureKeys;
import net.sf.saxon.trans.LicenseException;
import net.sf.saxon.trans.XPathException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for the Saxon PE/EE extension.
 * <p>
 * Saxon-PE: com.saxonica.config.ProfessionalTransformerFactory
 * Saxon-EE: com.saxonica.config.EnterpriseTransformerFactory
 * </p>
 * See {@linkplain 'https://www.saxonica.com/documentation12/index.html#!extensibility/extension-functions'}
 *
 * @author Jeremias Maerki &amp; Samael Bate
 */
@EnabledIf(
    value = "validSaxonLicenseFound",
    disabledReason = "A valid saxon license needs to be on the classpath for this test to run"
)
public class SaxonExtTest {

    private static EnterpriseConfiguration configuration;

    @BeforeAll
    static void beforeAll() {
        configuration = new EnterpriseConfiguration();
    }

    static boolean validSaxonLicenseFound() {
        // final Processor processor = new Processor(true);
        // processor.registerExtensionFunction(new ExtensionFunction());

        // can use either ProfessionalConfiguration or EnterpriseConfiguration
        final Configuration configuration = EnterpriseConfiguration.newConfiguration();

        try {
            System.out.println("Using Saxon " + configuration.getEditionCode() + " configuration");
            //return configuration.isLicensedFeature(Feature.ALLOW_SYNTAX_EXTENSIONS.code);
            return configuration.isLicensedFeature(Configuration.LicenseFeature.ENTERPRISE_XSLT);
        } catch (final LicenseException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @DisplayName("Do XML transform using Saxon's implementation of TransformerFactory")
    @ParameterizedTest
    @ValueSource(strings = { "xml/saxon-xml-transform.xsl", "xml/saxon-html-transform.xsl" })
    void testSaxonExt(final String xsltTemplate) throws Exception {
        givenBarcodeExtensionRegistered();

        final EnterpriseConfiguration config = new EnterpriseConfiguration();

        // PE or EE only:
        config.setExtensionElementNamespace("http://barcode4j.krysalis.org/ns", BarcodeExtensionElementFactory.class.getName());

        //config.setAllNodesUntyped(true); // because we have no license when testing (cannot be Schema Aware)
        //config.setExtensionElementNamespace();
        final SaxonTransformerFactory factory = new EnterpriseTransformerFactory(config); // required for EE!!

        factory.setAttribute(FeatureKeys.ALLOW_SYNTAX_EXTENSIONS, true);

        // FeatureKeys.ALLOW_SYNTAX_EXTENSIONS

        // todo: Don't use 'net.sf.saxon.jaxp.TransformerImpl', it should be using a PE or EE
        // equivalent. new com.saxonica.config.ProfessionalTransformerFactory()
        final Transformer trans = factory.newTransformer(
                new StreamSource(loadTestResourceFile(xsltTemplate))
        );
        final Source src = new StreamSource(loadTestResourceFile("xml/barcodes.xml"));

        final StringWriter writer = new StringWriter();
        Result res = new StreamResult(writer);

        trans.transform(src, res);
        String output = writer.getBuffer().toString();
        assertTrue(output.indexOf("svg") >= 0);
        //System.out.println(writer.getBuffer());
    }

    private void givenBarcodeExtensionRegistered() throws XPathException {
        //configuration.registerExtensionInstruction(BarcodeStyleElement.class);

        // PE or EE only:
        configuration.setExtensionElementNamespace("java:/org.krysalis.barcode4j.saxon.BarcodeExtensionElementFactory", BarcodeExtensionElementFactory.class.getName());
        configuration.setExtensionElementNamespace("http://barcode4j.krysalis.org/org.krysalis.barcode4j.saxon.BarcodeExtensionElementFactory", BarcodeExtensionElementFactory.class.getName());

        //configuration.setExtensionElementNamespace("http://barcode4j.krysalis.org/ns", BarcodeExtensionElementFactory.class.getName());
        //configuration.setExtensionElementNamespace("classpath://org.krysalis.barcode4j.saxon9.BarcodeExtensionElementFactory", BarcodeExtensionElementFactory.class.getName());
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
