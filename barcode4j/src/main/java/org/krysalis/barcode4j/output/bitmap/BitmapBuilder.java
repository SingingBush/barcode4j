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
 * @version $Id: BitmapBuilder.java,v 1.4 2008-04-30 16:37:05 jmaerki Exp $
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
        return prepareImage(dim, 0, resolution, imageType);
    }
    
    /**
     * Prepares a BufferedImage to paint to.
     * @param dim the barcode dimensions
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @return the requested BufferedImage
     */
    public static BufferedImage prepareImage(BarcodeDimension dim,
                        int orientation,
                        int resolution, int imageType) {
        int bmw = UnitConv.mm2px(dim.getWidthPlusQuiet(orientation), resolution);
        int bmh = UnitConv.mm2px(dim.getHeightPlusQuiet(orientation), resolution);
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
     * @param orientation the barcode orientation (0, 90, 180, 270)
     * @param antiAlias true enables anti-aliasing
     * @return the Graphics2D object to paint on
     */
    public static Graphics2D prepareGraphics2D(BufferedImage image, 
                BarcodeDimension dim, int orientation,
                boolean antiAlias) {
        Graphics2D g2d = image.createGraphics();
        if (antiAlias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setBackground(Color.white);
        g2d.setColor(Color.black);
        g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
        g2d.scale(image.getWidth() / dim.getWidthPlusQuiet(orientation), 
                image.getHeight() / dim.getHeightPlusQuiet(orientation));
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
        int orientation = 0;
        Graphics2D g2d = prepareGraphics2D(bi, dim, orientation, true);
        Java2DCanvasProvider provider = new Java2DCanvasProvider(g2d, orientation);
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
