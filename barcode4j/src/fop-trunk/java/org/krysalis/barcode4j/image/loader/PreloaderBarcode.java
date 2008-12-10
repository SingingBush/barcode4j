/*
 * Copyright 2008 Jeremias Maerki.
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

/* $Id: PreloaderBarcode.java,v 1.1 2008-12-10 15:52:37 jmaerki Exp $ */

package org.krysalis.barcode4j.image.loader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.fop.VariableUtil;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.MessageUtil;
import org.w3c.dom.Document;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.apache.xmlgraphics.image.loader.impl.AbstractImagePreloader;
import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.apache.xmlgraphics.util.UnitConv;
import org.apache.xmlgraphics.util.io.SubInputStream;

/**
 * Image preloader for barcodes (barcode XML).
 */
public class PreloaderBarcode extends AbstractImagePreloader {

    /** {@inheritDoc} */
    public ImageInfo preloadImage(String uri, Source src, ImageContext context)
            throws IOException {
        ImageInfo info = null;
        if (!isSupportedSource(src)) {
            return null;
        }
        info = getImage(uri, src, context);
        if (info != null) {
            ImageUtil.closeQuietly(src); //Image is fully read
        }
        return info;
    }

    private ImageInfo getImage(String uri, Source src,
            ImageContext context) throws IOException {
        InputStream in = null;
        try {
            Document doc;
            if (src instanceof DOMSource) {
                DOMSource domSrc = (DOMSource)src;
                doc = (Document)domSrc.getNode();
            } else {
                in = ImageUtil.needInputStream(src);
                int length = in.available();
                in.mark(length + 1);
                doc = getDocument(new SubInputStream(in, Long.MAX_VALUE, false));
            }
            if (!BarcodeConstants.NAMESPACE.equals(
                    doc.getDocumentElement().getNamespaceURI())) {
                in.reset();
                return null;
            }

            ImageInfo info;
            try {
                info = createImageInfo(uri, context, doc);
            } catch (ConfigurationException e) {
                resetInputStream(in);
                throw new IOException("Error in Barcode XML: " + e.getLocalizedMessage());
            } catch (BarcodeException e) {
                resetInputStream(in);
                throw new IOException("Error processing Barcode XML: " + e.getLocalizedMessage());
            }

            return info;
        } catch (TransformerException te) {
            resetInputStream(in);
            return null;
        }
    }

    private void resetInputStream(InputStream in) {
        try {
            if (in != null) {
                in.reset();
            }
        } catch (IOException ioe) {
            //Ignored. We're more interested in the original exception.
        }
    }

    private ImageInfo createImageInfo(String uri, ImageContext context, Document doc)
                throws ConfigurationException, BarcodeException {
        Configuration cfg = ConfigurationUtil.buildConfiguration(doc);
        String msg = ConfigurationUtil.getMessage(cfg);
        msg = MessageUtil.unescapeUnicode(msg);

        int orientation = cfg.getAttributeAsInteger("orientation", 0);
        orientation = BarcodeDimension.normalizeOrientation(orientation);

        BarcodeGenerator bargen = BarcodeUtil.getInstance().
                createBarcodeGenerator(cfg);
        //Expand with null information and hope the size will match the actual barcode
        String expandedMsg = VariableUtil.getExpandedMessage(null, msg);
        BarcodeDimension bardim = bargen.calcDimensions(expandedMsg);
        int widthMpt = (int)Math.ceil(UnitConv.mm2mpt(bardim.getWidthPlusQuiet(orientation)));
        int heightMpt = (int)Math.ceil(UnitConv.mm2mpt(bardim.getHeightPlusQuiet(orientation)));

        ImageInfo info = new ImageInfo(uri, ImageLoaderFactoryBarcode.MIME_TYPE);
        ImageSize size = new ImageSize();
        size.setSizeInMillipoints(widthMpt, heightMpt);
        //Set the resolution to that of the FOUserAgent
        size.setResolution(context.getSourceResolution());
        size.calcPixelsFromSize();
        info.setSize(size);

        //The whole image had to be loaded to determine the image size, so keep that information
        ImageBarcode barcodeImage = new ImageBarcode(info, cfg, bardim);
        info.getCustomObjects().put(ImageInfo.ORIGINAL_IMAGE, barcodeImage);
        //Add the non-expanded message!
        info.getCustomObjects().put(ImageBarcode.MESSAGE, msg);
        return info;
    }

    private boolean isSupportedSource(Source src) {
        if (src instanceof DOMSource) {
            DOMSource domSrc = (DOMSource)src;
            return (domSrc.getNode() instanceof Document);
        } else {
            return ImageUtil.hasInputStream(src);
        }
    }

    private Document getDocument(InputStream in) throws TransformerException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        //Custom error listener to minimize output to console
        ErrorListener errorListener = new ErrorListener() {

            public void error(TransformerException exception) throws TransformerException {
                throw exception;
            }

            public void fatalError(TransformerException exception) throws TransformerException {
                throw exception;
            }

            public void warning(TransformerException exception) throws TransformerException {
                //ignore
            }

        };
        tFactory.setErrorListener(errorListener);
        Transformer transformer = tFactory.newTransformer();
        transformer.setErrorListener(errorListener);
        Source source = new StreamSource(in);
        DOMResult res = new DOMResult();
        transformer.transform(source, res);

        Document doc = (Document)res.getNode();
        return doc;
    }

}
