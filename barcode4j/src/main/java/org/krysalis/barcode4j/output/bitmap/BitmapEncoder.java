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

/**
 * This interface is used to encode bitmaps into their target formats.
 * 
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoder.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public interface BitmapEncoder {

    /**
     * Returns an array of MIME types supported.
     * @return the array of MIME types
     */
    String[] getSupportedMIMETypes();
    
    /**
     * Encodes a BufferedImage to a target format and writes it to the 
     * OutputStream.
     * @param image the image to encode
     * @param out the OutputStream to write the image to
     * @param mime the MIME type in which to encode the image
     * @param resolution the resolution in dpi of the image
     * @throws IOException in case of an I/O problem
     */
    void encode(BufferedImage image, OutputStream out, 
            String mime, int resolution) throws IOException;

}
