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
package org.krysalis.barcode4j.output.eps;

import java.io.ByteArrayOutputStream;

import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for basic EPS output functionality.
 *
 * @author Jeremias Maerki
 * @version $Id: EPSOutputTest.java,v 1.4 2006-11-07 16:44:25 jmaerki Exp $
 */
public class EPSOutputTest {

    @Test
    void testEPS() throws Exception {
        final DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("intl2of5"));

        final BarcodeUtil util = BarcodeUtil.getInstance();
        final BarcodeGenerator gen = util.createBarcodeGenerator(cfg);

        try(final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            final EPSCanvasProvider provider = new EPSCanvasProvider(outputStream, 0);

            //Create Barcode and render it to EPS
            gen.generateBarcode(provider, "123");
            provider.finish();

            assertTrue(outputStream.size() > 0);
        } catch (final Exception e) {
            fail("Unexpected exception", e);
        }
    }

}
