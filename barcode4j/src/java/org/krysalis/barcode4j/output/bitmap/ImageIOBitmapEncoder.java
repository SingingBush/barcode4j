/*
 * Copyright 2002-2004,2010 Jeremias Maerki.
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
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.krysalis.barcode4j.tools.DebugUtil;
import org.krysalis.barcode4j.tools.MimeTypes;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * BitmapEncoder implementation using ImageIO.
 *
 * @author Jeremias Maerki
 */
public class ImageIOBitmapEncoder implements BitmapEncoder {

    /**
     * Constructs the BitmapEncoder. The constructor checks if the ImageIO
     * API is available so it doesn't get registered in case it's not
     * there.
     * @throws ClassNotFoundException if the ImageIO API is unavailable
     */
    public ImageIOBitmapEncoder() throws ClassNotFoundException {
        Class.forName("javax.imageio.ImageIO");
    }

    /** {@inheritDoc} */
    public String[] getSupportedMIMETypes() {
        return ImageIO.getWriterMIMETypes();
    }

    /** {@inheritDoc} */
    public void encode(BufferedImage image, OutputStream out,
                String mime, int resolution) throws IOException {

        //Simply get first offered writer
        Iterator i = ImageIO.getImageWritersByMIMEType(mime);
        ImageWriter writer = (ImageWriter)i.next();

        //Prepare output
        ImageOutputStream imout = ImageIO.createImageOutputStream(out);
        writer.setOutput(imout);

        //Prepare metadata
        IIOMetadata iiometa = setupMetadata(image, writer, mime, resolution);

        //Write image
        IIOImage iioimage = new IIOImage(image, null, iiometa);
        writer.write(iioimage);
        writer.dispose();
        imout.close();
    }

    private IIOMetadata setupMetadata(BufferedImage image, ImageWriter writer,
                String mime, int resolution) throws IOException {
        IIOMetadata iiometa;
        try {
            iiometa = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image),
                writer.getDefaultWriteParam());
        } catch (Exception e) {
            return null; //ImageIO has problems with metadata
        }
        if (iiometa == null) {
            return null; //Some JAI-codecs don't support metadata
        }

        /*
        String[] metanames = iiometa.getMetadataFormatNames();
        for (int j = 0; j < metanames.length; j++) System.out.println(metanames[j]);
        */
        final String stdmeta = "javax_imageio_1.0";
        final String jpegmeta = "javax_imageio_jpeg_image_1.0";

        if (!iiometa.isReadOnly()) {
            if (MimeTypes.MIME_JPEG.equals(mime)
                    && jpegmeta.equals(iiometa.getNativeMetadataFormatName())) {

                /* JPEG gets special treatment because I believe there's a bug in
                 * the JPEG codec in ImageIO converting the pixel size incorrectly
                 * when using standard metadata format. JM, 2003-10-28
                 */

                checkWritable(iiometa);

                IIOMetadataNode rootnode = (IIOMetadataNode)iiometa.getAsTree(jpegmeta);
                IIOMetadataNode variety = (IIOMetadataNode)rootnode.
                        getElementsByTagName("JPEGvariety").item(0);

                IIOMetadataNode jfif = (IIOMetadataNode)variety.
                        getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("resUnits", "1"); //dots per inch
                jfif.setAttribute("Xdensity", Integer.toString(resolution));
                jfif.setAttribute("Ydensity", Integer.toString(resolution));

                //dumpMetadata(iiometa);
                //DebugUtil.dumpNode(rootnode);

                iiometa.setFromTree(jpegmeta, rootnode);

                //dumpMetadata(iiometa);

            } else if (iiometa.isStandardMetadataFormatSupported()) {
                checkWritable(iiometa);

                IIOMetadataNode rootnode = new IIOMetadataNode(stdmeta);

                IIOMetadataNode imagedim = new IIOMetadataNode("Dimension");
                IIOMetadataNode child = new IIOMetadataNode("HorizontalPixelSize");
                double effResolution = 1 / (UnitConv.in2mm(1) / resolution);
                child.setAttribute("value", Double.toString(effResolution));
                imagedim.appendChild(child);
                child = new IIOMetadataNode("VerticalPixelSize");
                child.setAttribute("value", Double.toString(effResolution));
                imagedim.appendChild(child);

                IIOMetadataNode textNode = new IIOMetadataNode("Text");
                child = new IIOMetadataNode("TextEntry");
                child.setAttribute("keyword", "Software");
                child.setAttribute("value", "Barcode4J");
                child.setAttribute("encoding", "Unicode");
                child.setAttribute("language", "en");
                child.setAttribute("compression", "none");
                textNode.appendChild(child);

                rootnode.appendChild(imagedim);
                rootnode.appendChild(textNode);

                //dumpMetadata(iiometa);
                //DebugUtil.dumpNode(rootnode);

                try {
                    iiometa.mergeTree(stdmeta, rootnode);
                } catch (Exception e1) {
                    try {
                        iiometa.setFromTree(stdmeta, rootnode);
                    } catch (Exception e2) {
                        //ignore metadata
                    }
                }

                //dumpMetadata(iiometa);
            }
        }

        return iiometa;
    }

    private void checkWritable(IIOMetadata iiometa) throws IOException {
        if (iiometa.isReadOnly()) {
            //System.out.println("Metadata is read-only");
            throw new IOException("Metadata is read-only. Cannot modify");
        }
    }

    private void dumpMetadata(IIOMetadata iiometa) {
        String[] metanames = iiometa.getMetadataFormatNames();
        for (int j = 0; j < metanames.length; j++) {
            System.out.println("--->" + metanames[j]);
            DebugUtil.dumpNode(iiometa.getAsTree(metanames[j]));
        }
    }

}
