/*
 * $Id: BitmapCanvasProvider.java,v 1.1 2003-12-13 20:23:41 jmaerki Exp $
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
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.output.AbstractCanvasProvider;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 * CanvasProvider implementation for generating bitmaps. This class wraps
 * Java2DCanvasProvider to do the actual rendering.
 * 
 * @author Jeremias Maerki
 */
public class BitmapCanvasProvider extends AbstractCanvasProvider {

    private OutputStream out;
    private String mime;
    private int resolution;
    private int imageType;
    private boolean antiAlias;
    private BufferedImage image;
    private Java2DCanvasProvider delegate;

    /**
     * Creates a new BitmapCanvasProvider. 
     * @param out OutputStream to write to
     * @param mime MIME type of the desired output format (ex. "image/png")
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @param antiAlias true if anti-aliasing should be enabled
     */
    public BitmapCanvasProvider(OutputStream out, String mime, 
                    int resolution, int imageType, boolean antiAlias) {
        super();
        this.out = out;
        this.mime = mime;
        this.resolution = resolution;
        this.imageType = imageType;
        this.antiAlias = antiAlias;
    }

    /**
     * Creates a new BitmapCanvasProvider. 
     * @param resolution the desired image resolution (dots per inch)
     * @param imageType the desired image type (Values: BufferedImage.TYPE_*)
     * @param antiAlias true if anti-aliasing should be enabled
     */
    public BitmapCanvasProvider(int resolution, int imageType, boolean antiAlias) {
        this(null, null, resolution, imageType, antiAlias);
    }

    /**
     * Call this method to finish any pending operations after the 
     * BarcodeGenerator has finished its work.
     * @throws IOException in case of an I/O problem
     */
    public void finish() throws IOException {
        this.image.flush();
        if (this.out != null) {
            final BitmapEncoder encoder = BitmapEncoderRegistry.getInstance(mime);
            encoder.encode(this.image, out, mime, resolution);
        }
    }
    
    /**
     * Returns the buffered image that is used to paint the barcode on.
     * @return the image.
     */
    public BufferedImage getBufferedImage() {
        return this.image;
    }

    /** {@inheritDoc} */
    public void establishDimensions(BarcodeDimension dim) {
        super.establishDimensions(dim);
        this.image = BitmapBuilder.prepareImage(dim, this.resolution, this.imageType);
        this.delegate = new Java2DCanvasProvider(
            BitmapBuilder.prepareGraphics2D(this.image, dim, this.antiAlias));
        this.delegate.establishDimensions(dim);
    }

    /** {@inheritDoc} */
    public void deviceFillRect(double x, double y, double w, double h) {
        this.delegate.deviceFillRect(x, y, w, h);
    }

    /** {@inheritDoc} */
    public void deviceJustifiedText(String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        this.delegate.deviceJustifiedText(text, x1, x2, y1, fontName, fontSize);
    }

    /** {@inheritDoc} */
    public void deviceCenteredText(String text,
            double x1, double x2, double y1,
            String fontName, double fontSize) {
        this.delegate.deviceCenteredText(text, x1, x2, y1, fontName, fontSize);
    }

}
