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
import java.io.IOException;
import java.io.OutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * BitmapEncoder implementation using Sun's JPEG encoder from the JDK.  
 * 
 * @author Jeremias Maerki
 * @version $Id: SunJPEGBitmapEncoder.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class SunJPEGBitmapEncoder implements BitmapEncoder {

    private static final String MIME_JPEG = "image/jpeg"; 

    /**
     * Constructs the BitmapEncoder. The constructor checks if Sun's JPEG
     * encoder is available so it doesn't get registered in case it's not 
     * there.
     * @throws ClassNotFoundException if Sun's JPEG encoder is unavailable 
     */
    public SunJPEGBitmapEncoder() throws ClassNotFoundException {
        Class.forName("com.sun.image.codec.jpeg.JPEGCodec");
    }

    /** {@inheritDoc} */
    public String[] getSupportedMIMETypes() {
        return new String[] {MIME_JPEG};
    }

    /** {@inheritDoc} */
    public void encode(BufferedImage image, OutputStream out, String mime, int resolution)
                throws IOException {
        if (!MIME_JPEG.equals(mime)) {
            throw new IllegalArgumentException("Unsupported MIME type: " + mime);
        }
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
        //param.setQuality(0.8f, true);
        param.setXDensity(resolution);
        param.setYDensity(resolution);
        param.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
        encoder.encode(image, param);
    }

}
