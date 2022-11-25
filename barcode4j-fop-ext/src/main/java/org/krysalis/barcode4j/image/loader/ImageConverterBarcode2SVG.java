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

/* $Id: ImageConverterBarcode2SVG.java,v 1.1 2010-11-18 09:34:22 jmaerki Exp $ */

package org.krysalis.barcode4j.image.loader;

import java.io.IOException;
import java.util.Map;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.svg.AbstractSVGGeneratingCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.VariableUtil;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.XMLNamespaceEnabledImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageXMLDOM;

/**
 * This ImageConverter converts barcodes to SVG.
 */
public class ImageConverterBarcode2SVG extends AbstractImageConverter {

    /** An SVG image in form of a W3C DOM instance */
    private static final XMLNamespaceEnabledImageFlavor SVG_DOM
        = new XMLNamespaceEnabledImageFlavor(
            ImageFlavor.XML_DOM, AbstractSVGGeneratingCanvasProvider.SVG_NAMESPACE);


    /** {@inheritDoc} */
    public Image convert(Image src, Map hints) throws ImageException, IOException {
        checkSourceFlavor(src);
        ImageBarcode barcodeImage = (ImageBarcode)src;

        Configuration cfg = barcodeImage.getBarcodeXML();
        int orientation = BarcodeDimension.normalizeOrientation(
                cfg.getAttributeAsInteger("orientation", 0));

        try {
            String msg = barcodeImage.getMessage();
            PageInfo pageInfo = PageInfo.fromProcessingHints(hints);
            String expandedMsg = VariableUtil.getExpandedMessage(pageInfo, msg);

            final BarcodeGenerator bargen = BarcodeUtil.getInstance().
                        createBarcodeGenerator(cfg);

            //TODO Optionally use Batik's SVG DOM?
            SVGCanvasProvider canvas = new SVGCanvasProvider(true, orientation);
            bargen.generateBarcode(canvas, expandedMsg);

            ImageXMLDOM svgImage = new ImageXMLDOM(src.getInfo(), canvas.getDOM(), SVG_DOM);
            return svgImage;
        } catch (ConfigurationException ce) {
            throw new ImageException("Error in Barcode XML", ce);
        } catch (BarcodeException be) {
            throw new ImageException("Error while converting barcode to SVG", be);
        }
    }

    /** {@inheritDoc} */
    public ImageFlavor getSourceFlavor() {
        return ImageBarcode.BARCODE_IMAGE_FLAVOR;
    }

    /** {@inheritDoc} */
    public ImageFlavor getTargetFlavor() {
        return SVG_DOM;
    }

}
