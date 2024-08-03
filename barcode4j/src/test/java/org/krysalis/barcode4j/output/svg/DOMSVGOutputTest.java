/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.output.svg;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SVG output to JDOM.
 *
 * @author Jeremias Maerki
 * @version $Id: DOMSVGOutputTest.java,v 1.4 2006-11-07 16:44:25 jmaerki Exp $
 */
// note that is class should get renamed to SVGCanvasProviderTest
public class DOMSVGOutputTest {

    @ParameterizedTest
    @ValueSource(strings = {"ean-13", "intl2of5", "upc-A"})
    void testDOMSVG(final String symbology) throws Exception {
        // Build up Avalon style configuration
        final DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration(symbology));

        final BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
        final SVGCanvasProvider svg = new SVGCanvasProvider(false, 0);

        //Create Barcode and render it to SVG
        gen.generateBarcode(svg, "012345678905");

        final Document dom = svg.getDOM();
        final String domString = transformToXmlString(dom);

        // the output svg will have the barcode as a text block (separated text blocks differing by symbology)
        assertTrue(domString.contains("12345"));
        assertTrue(domString.contains("7890"));

        final DocumentFragment frag = svg.getDOMFragment();
        assertNotNull(frag);
    }

    private String transformToXmlString(final Node node) throws TransformerException {
        final Transformer trans = TransformerFactory.newInstance().newTransformer();
        final StringWriter sw = new StringWriter();
        trans.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }
}
