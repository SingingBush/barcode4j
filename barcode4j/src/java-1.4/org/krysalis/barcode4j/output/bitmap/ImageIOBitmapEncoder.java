/*
 * $Id: ImageIOBitmapEncoder.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
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
        IIOMetadata iiometa = writer.getDefaultImageMetadata(
                new ImageTypeSpecifier(image), 
                writer.getDefaultWriteParam());
        if (iiometa == null) return null; //Some JAI-codecs don't support metadata
    
        /*
        String[] metanames = iiometa.getMetadataFormatNames();
        for (int j = 0; j < metanames.length; j++) System.out.println(metanames[j]);
        */
        final String stdmeta = "javax_imageio_1.0";
        final String jpegmeta = "javax_imageio_jpeg_image_1.0"; 

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
            
            iiometa.mergeTree(stdmeta, rootnode);
            
            //dumpMetadata(iiometa);
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
