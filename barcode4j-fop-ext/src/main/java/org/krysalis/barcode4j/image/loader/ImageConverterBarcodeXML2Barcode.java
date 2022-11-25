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

/* $Id: ImageConverterBarcodeXML2Barcode.java,v 1.1 2010-11-18 09:34:22 jmaerki Exp $ */

package org.krysalis.barcode4j.image.loader;

import java.io.IOException;
import java.util.Map;

import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.VariableUtil;
import org.w3c.dom.Document;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.XMLNamespaceEnabledImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageXMLDOM;

/**
 * This ImageConverter converts barcodes XML to a barcode representation needed by the
 * converters that generate the actual barcodes.
 */
public class ImageConverterBarcodeXML2Barcode extends AbstractImageConverter {

    /** An barcode in form of a W3C DOM instance */
    private static final XMLNamespaceEnabledImageFlavor BARCODE_XML_FLAVOR
        = new XMLNamespaceEnabledImageFlavor(
            ImageFlavor.XML_DOM, BarcodeConstants.NAMESPACE);


    /** {@inheritDoc} */
    public Image convert(Image src, Map hints) throws ImageException, IOException {
        checkSourceFlavor(src);
        ImageXMLDOM barcodeXML = (ImageXMLDOM)src;

        Document doc = barcodeXML.getDocument();

        try {
            Configuration cfg = ConfigurationUtil.buildConfiguration(doc);
            String msg = ConfigurationUtil.getMessage(cfg);

            //int orientation = cfg.getAttributeAsInteger("orientation", 0);
            //orientation = BarcodeDimension.normalizeOrientation(orientation);

            BarcodeGenerator bargen = BarcodeUtil.getInstance().
                    createBarcodeGenerator(cfg);
            PageInfo pageInfo = PageInfo.fromProcessingHints(hints);
            String expandedMsg = VariableUtil.getExpandedMessage(pageInfo, msg);
            BarcodeDimension bardim = bargen.calcDimensions(expandedMsg);

            ImageBarcode imageBarcode = new ImageBarcode(src.getInfo(), cfg, bardim);
            return imageBarcode;
        } catch (ConfigurationException ce) {
            throw new ImageException("Error building configuration object for barcode", ce);
        } catch (BarcodeException be) {
            throw new ImageException("Error determining dimensions for barcode", be);
        }
    }

    /** {@inheritDoc} */
    public ImageFlavor getSourceFlavor() {
        return BARCODE_XML_FLAVOR;
    }

    /** {@inheritDoc} */
    public ImageFlavor getTargetFlavor() {
        return ImageBarcode.BARCODE_IMAGE_FLAVOR;
    }

}
