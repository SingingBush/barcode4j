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
package org.krysalis.barcode4j.output.bitmap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Test class for basic bitmap output functionality.
 *
 * @author Jeremias Maerki
 * @version $Id: BitmapOutputTest.java,v 1.5 2009-02-18 20:28:13 jmaerki Exp $
 */
public class BitmapOutputTest extends TestCase {

    public BitmapOutputTest(String name) {
        super(name);
    }

    private BarcodeGenerator getGenerator() throws ConfigurationException, BarcodeException {
        DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("intl2of5"));

        BarcodeUtil util = BarcodeUtil.getInstance();
        BarcodeGenerator gen = util.createBarcodeGenerator(cfg);
        return gen;
    }

    public void testBitmap() throws Exception {
        BarcodeGenerator gen = getGenerator();
        BarcodeDimension dim = gen.calcDimensions("123");

        BufferedImage image =
            BitmapBuilder.prepareImage(dim, 200, BufferedImage.TYPE_INT_RGB);
        assertEquals("Width in pixels should be 107", 107, image.getWidth());
        assertEquals("Height in pixels should be 140", 140, image.getHeight());
    }

    public void testBitmapFile() throws Exception {
        BarcodeGenerator gen = getGenerator();

        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        BitmapCanvasProvider provider = new BitmapCanvasProvider(baout,
                "image/jpeg", 200, BufferedImage.TYPE_BYTE_GRAY, true, 0);

        //Create Barcode and render it to a bitmap
        gen.generateBarcode(provider, "123");
        provider.finish();

        assertTrue(baout.size() > 0);
    }

    public void testBitmapBuffered() throws Exception {
        BarcodeGenerator gen = getGenerator();

        BitmapCanvasProvider provider =
            new BitmapCanvasProvider(200, BufferedImage.TYPE_BYTE_GRAY, true, 0);

        //Create Barcode and render it to a bitmap
        gen.generateBarcode(provider, "123");
        provider.finish();
        BufferedImage image = provider.getBufferedImage();

        assertNotNull(image);
        assertEquals("Width in pixels should be 107", 107, image.getWidth());
        assertEquals("Height in pixels should be 140", 140, image.getHeight());
    }

}