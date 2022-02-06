/*
 * Copyright 2008,2010 Jeremias Maerki.
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

/* $Id: ImageLoaderFactoryBarcode.java,v 1.1 2010-11-18 09:34:22 jmaerki Exp $ */

package org.krysalis.barcode4j.image.loader;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoaderFactory;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

/**
 * Factory class for the ImageLoader for barcodes.
 */
public class ImageLoaderFactoryBarcode extends AbstractImageLoaderFactory {

    /** MIME type for Barcode4J's barcode XML */
    public static final String MIME_TYPE = "application/x-barcode4j+xml";

    /** {@inheritDoc} */
    public String[] getSupportedMIMETypes() {
        return new String[] {MIME_TYPE};
    }

    /** {@inheritDoc} */
    public ImageFlavor[] getSupportedFlavors(String mime) {
        return new ImageFlavor[] {ImageBarcode.BARCODE_IMAGE_FLAVOR};
    }

    /** {@inheritDoc} */
    public ImageLoader newImageLoader(ImageFlavor targetFlavor) {
        return new ImageLoaderBarcode(targetFlavor);
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        try {
            Class.forName("org.krysalis.barcode4j.BarcodeGenerator");
            return true;
        } catch (Exception e) {
            //ignore
        }
        return false;
    }

}
