/*
 * $Id: BitmapOutputTest.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.output.bitmap;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

import junit.framework.TestCase;

/**
 * Test class for basic bitmap output functionality.
 * 
 * @author Jeremias Maerki
 */
public class BitmapOutputTest extends TestCase {

    public BitmapOutputTest(String name) {
        super(name);
    }

    private BarcodeGenerator getGenerator() throws BarcodeException {
        DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("intl2of5"));
        Logger log = new NullLogger();

        BarcodeUtil util = BarcodeUtil.getInstance();
        BarcodeGenerator gen = util.createBarcodeGenerator(cfg, log);
        return gen;
    }

    public void testBitmap() throws Exception {
        BarcodeGenerator gen = getGenerator();
        BarcodeDimension dim = gen.calcDimensions("123");
        
        BufferedImage image = 
            BitmapBuilder.prepareImage(dim, 200, BufferedImage.TYPE_BYTE_GRAY);
        assertEquals("Width in pixels should be 107", 107, image.getWidth());
        assertEquals("Height in pixels should be 118", 118, image.getHeight());
    }

    public void testBitmapFile() throws Exception {
        BarcodeGenerator gen = getGenerator();
        
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        BitmapCanvasProvider provider = new BitmapCanvasProvider(baout, 
                "image/jpeg", 200, BufferedImage.TYPE_BYTE_GRAY, true); 

        //Create Barcode and render it to a bitmap
        gen.generateBarcode(provider, "123");
        provider.finish();
        
        assertTrue(baout.size() > 0);
    }
    
    public void testBitmapBuffered() throws Exception {
        BarcodeGenerator gen = getGenerator();
        
        BitmapCanvasProvider provider = 
            new BitmapCanvasProvider(200, BufferedImage.TYPE_BYTE_GRAY, true); 

        //Create Barcode and render it to a bitmap
        gen.generateBarcode(provider, "123");
        provider.finish();
        BufferedImage image = provider.getBufferedImage();

        assertNotNull(image);        
        assertEquals("Width in pixels should be 107", 107, image.getWidth());
        assertEquals("Height in pixels should be 118", 118, image.getHeight());
    }
    
}