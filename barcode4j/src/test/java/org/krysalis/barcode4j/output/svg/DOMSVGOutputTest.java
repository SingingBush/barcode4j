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

import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.w3c.dom.DocumentFragment;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for SVG output to JDOM.
 *
 * @author Jeremias Maerki
 * @version $Id: DOMSVGOutputTest.java,v 1.4 2006-11-07 16:44:25 jmaerki Exp $
 */
public class DOMSVGOutputTest {

    @Test
    void testDOMSVG() throws Exception {
        final DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("intl2of5"));

        final BarcodeGenerator gen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
        final SVGCanvasProvider svg = new SVGCanvasProvider(false, 0);

        //Create Barcode and render it to SVG
        gen.generateBarcode(svg, "123");

        final DocumentFragment frag = svg.getDOMFragment();
        assertNotNull(frag);
    }

}
