/*
 * $Id: BarcodeServlet.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

/**
 * Simple barcode servlet.
 * 
 * @author Jeremias Maerki
 */
public class BarcodeServlet extends HttpServlet {

    /** Parameter name for the message */
    public static final String BARCODE_MSG                 = "msg";
    /** Parameter name for the barcode type */
    public static final String BARCODE_TYPE                = "type";
    /** Parameter name for the barcode height */
    public static final String BARCODE_HEIGHT              = "height";
    /** Parameter name for the module width */
    public static final String BARCODE_MODULE_WIDTH        = "mw";
    /** Parameter name for the wide factor */
    public static final String BARCODE_WIDE_FACTOR         = "wf";
    /** Parameter name for the quiet zone */
    public static final String BARCODE_QUIET_ZONE          = "qz";
    /** Parameter name for the human-readable placement */
    public static final String BARCODE_HUMAN_READABLE_POS  = "hrp";
    /** Parameter name for the output format */
    public static final String BARCODE_FORMAT              = "fmt";
    /** Parameter name for the image resolution (for bitmaps) */
    public static final String BARCODE_IMAGE_RESOLUTION    = "res";
    /** Parameter name for the grayscale or b/w image (for bitmaps) */
    public static final String BARCODE_IMAGE_GRAYSCALE     = "gray";
    /** Parameter name for the font size of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_SIZE = "hrsize";
    /** Parameter name for the font name of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_FONT = "hrfont";

    private Logger log = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

        try {
            String format = determineFormat(request);
            
            Configuration cfg = buildCfg(request);

            String msg = request.getParameter(BARCODE_MSG);
            if (msg == null) msg = "0123456789";
            
            BarcodeUtil util = BarcodeUtil.getInstance();
            BarcodeGenerator gen = util.createBarcodeGenerator(cfg, log);
            
            ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            try {
                if (format.equals(MimeTypes.MIME_SVG)) {
                    //Create Barcode and render it to SVG
                    SVGCanvasProvider svg = new SVGCanvasProvider(false);
                    try {
                        gen.generateBarcode(svg, msg);
                    } catch (Exception e) {
                        throw new BarcodeException("Error while generating barcode", e);
                    }
                    org.w3c.dom.DocumentFragment frag = svg.getDOMFragment();
                     
                    //Serialize SVG barcode
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(frag);
                    Result res = new javax.xml.transform.stream.StreamResult(bout);
                    trans.transform(src, res);
                } else if (format.equals(MimeTypes.MIME_EPS)) {
                    EPSCanvasProvider eps = new EPSCanvasProvider(bout);
                    gen.generateBarcode(eps, msg);
                    eps.finish();
                } else {
                    String resText = request.getParameter(BARCODE_IMAGE_RESOLUTION);
                    int resolution = 300; //dpi
                    if (resText != null) {
                        resolution = Integer.parseInt(resText); 
                    }
                    if (resolution > 2400) {
                        throw new IllegalArgumentException(
                            "Resolutions above 2400dpi are not allowed");
                    }
                    if (resolution < 10) {
                        throw new IllegalArgumentException(
                            "Minimum resolution must be 10dpi");
                    }
                    String gray = request.getParameter(BARCODE_IMAGE_GRAYSCALE);
                    BitmapCanvasProvider bitmap = ("true".equalsIgnoreCase(gray)
                        ? new BitmapCanvasProvider(
                                bout, format, resolution, 
                                BufferedImage.TYPE_BYTE_GRAY, true)
                        : new BitmapCanvasProvider(
                                bout, format, resolution, 
                                BufferedImage.TYPE_BYTE_BINARY, false));
                    gen.generateBarcode(bitmap, msg);
                    bitmap.finish();
                }
            } finally {
                bout.close();
            }
            response.setContentType(format);
            response.setContentLength(bout.size());
            response.getOutputStream().write(bout.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Error while generating barcode", e);
            throw new ServletException(e);
        } catch (Throwable t) {
            log.error("Error while generating barcode", t);
            throw new ServletException(t);
        }
    }
    
    /**
     * Check the request for the desired output format.
     * @param request the request to use
     * @return MIME type of the desired output format.
     */
    protected String determineFormat(HttpServletRequest request) {
        String format = request.getParameter(BARCODE_FORMAT);
        format = MimeTypes.expandFormat(format);
        if (format == null) format = MimeTypes.MIME_SVG;
        return format;
    }
    
    /**
     * Build an Avalon Configuration object from the request.
     * @param request the request to use
     * @return the newly built COnfiguration object
     */
    protected Configuration buildCfg(HttpServletRequest request) {
        DefaultConfiguration cfg = new DefaultConfiguration("barcode");
        //Get type
        String type = request.getParameter(BARCODE_TYPE);
        if (type == null) type = "code128";
        DefaultConfiguration child = new DefaultConfiguration(type);
        cfg.addChild(child);
        //Get additional attributes
        DefaultConfiguration attr;
        String height = request.getParameter(BARCODE_HEIGHT);
        if (height != null) {
            attr = new DefaultConfiguration("height");
            attr.setValue(height);
            child.addChild(attr);
        }
        String moduleWidth = request.getParameter(BARCODE_MODULE_WIDTH);
        if (moduleWidth != null) {
            attr = new DefaultConfiguration("module-width");
            attr.setValue(moduleWidth);
            child.addChild(attr);
        }
        String wideFactor = request.getParameter(BARCODE_WIDE_FACTOR);
        if (wideFactor != null) {
            attr = new DefaultConfiguration("wide-factor");
            attr.setValue(wideFactor);
            child.addChild(attr);
        }
        String quietZone = request.getParameter(BARCODE_QUIET_ZONE);
        if (quietZone != null) {
            attr = new DefaultConfiguration("quiet-zone");
            if (quietZone.startsWith("disable")) {
                attr.setAttribute("enabled", "false");
            } else {
                attr.setValue(quietZone);
            }
            child.addChild(attr);
        }
        String humanReadable = request.getParameter(BARCODE_HUMAN_READABLE_POS);
        if (humanReadable != null) {
            attr = new DefaultConfiguration("human-readable");
            attr.setValue(humanReadable);
            child.addChild(attr);
        }
        String humanReadableSize = request.getParameter(BARCODE_HUMAN_READABLE_SIZE);
        if (humanReadableSize != null) {
            attr = new DefaultConfiguration("human-readable-size");
            attr.setValue(humanReadableSize);
            child.addChild(attr);
        }
        String humanReadableFont = request.getParameter(BARCODE_HUMAN_READABLE_FONT);
        if (humanReadableFont != null) {
            attr = new DefaultConfiguration("human-readable-font");
            attr.setValue(humanReadableFont);
            child.addChild(attr);
        }
        return cfg;
    }

}
