/*
 * $Id: BitmapBuilder.java,v 1.1 2003-12-13 20:23:41 jmaerki Exp $
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * Helper class for bitmap generation.
 * 
 * @author Jeremias Maerki
 */
public class BitmapBuilder {

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected BitmapBuilder() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Prepares a BufferedImage to paint to.
     * @param dim the barcode dimensions
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @return the requested BufferedImage
     */
    public static BufferedImage prepareImage(BarcodeDimension dim, 
                        int resolution, int imageType) {
        int bmw = UnitConv.mm2px(dim.getWidthPlusQuiet(), resolution);
        int bmh = UnitConv.mm2px(dim.getHeightPlusQuiet(), resolution);
        BufferedImage bi = new BufferedImage(
                bmw,
                bmh,
                imageType);
        return bi;
    }

    /**
     * Prepares a Graphics2D object for painting on a given BufferedImage. The
     * coordinate system is adjusted to the demands of the Java2DCanvasProvider.
     * @param image the BufferedImage instance
     * @param dim the barcode dimensions
     * @param antiAlias true enables anti-aliasing
     * @return the Graphics2D object to paint on
     */
    public static Graphics2D prepareGraphics2D(BufferedImage image, 
                BarcodeDimension dim, boolean antiAlias) {
        Graphics2D g2d = image.createGraphics();
        if (antiAlias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setBackground(Color.white);
        g2d.setColor(Color.black);
        g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
        g2d.scale(image.getWidth() / dim.getWidthPlusQuiet(), 
                image.getHeight() / dim.getHeightPlusQuiet());
        return g2d;
    }

    /**
     * Generates a barcode as bitmap image.
     * @param bargen the BarcodeGenerator to use
     * @param msg the message to encode
     * @param resolution the desired image resolution (dots per inch)
     * @return the requested BufferedImage
     */
    public static BufferedImage getImage(BarcodeGenerator bargen, String msg, int resolution) {
        BarcodeDimension dim = bargen.calcDimensions(msg);
        BufferedImage bi = prepareImage(dim, resolution, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = prepareGraphics2D(bi, dim, true);
        Java2DCanvasProvider provider = new Java2DCanvasProvider(g2d);
        bargen.generateBarcode(provider, msg);
        bi.flush();
        return bi;
    }
    
    /**
     * Convenience method for save a bitmap to a file/OutputStream. It uses
     * BitmapEncoderRegistry to look up a suitable BitmapEncoder.
     * @param image image to save
     * @param out OutputStream to write to
     * @param mime MIME type of the desired output format (ex. "image/png")
     * @param resolution the image resolution (dots per inch)
     * @throws IOException In case of an I/O problem
     * @see org.krysalis.barcode4j.output.bitmap.BitmapEncoderRegistry
     */
    public static void saveImage(BufferedImage image, 
                OutputStream out, String mime, int resolution) throws IOException {
        BitmapEncoder encoder = BitmapEncoderRegistry.getInstance(mime);
        /* DEBUG
        String[] mimes = encoder.getSupportedMIMETypes();
        for (int i = 0; i < mimes.length; i++) {
            System.out.println(mimes[i]); 
        }*/
        encoder.encode(image, out, mime, resolution);
    }

    /**
     * Generates a barcode as bitmap image file.
     * @param bargen the BarcodeGenerator to use
     * @param msg the message to encode
     * @param out the OutputStream to write to
     * @param mime MIME type of the desired output format (ex. "image/png")
     * @param resolution the desired image resolution (dots per inch)
     * @throws IOException In case of an I/O problem
     */
    public static void outputBarcodeImage(BarcodeGenerator bargen,
                                            String msg,
                                            OutputStream out,
                                            String mime,
                                            int resolution)
                throws IOException {
        BufferedImage image = getImage(bargen, msg, resolution);
        saveImage(image, out, mime, resolution);
    }

}
