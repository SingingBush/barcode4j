/*
 * $Id: BarcodeErrorServlet.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.avalon.framework.CascadingException;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;

/**
 * Error handler servlet for Barcode exceptions.
 * 
 * @author Jeremias Maerki
 */
public class BarcodeErrorServlet extends HttpServlet {

    private Logger log = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);

    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

        Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
        try {
            SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
            java.net.URL xslt = getServletContext().getResource("/WEB-INF/exception2svg.xslt");
            TransformerHandler thandler;
            if (xslt != null) {
                log.debug(xslt.toExternalForm());
                Source xsltSource = new StreamSource(xslt.toExternalForm());
                thandler = factory.newTransformerHandler(xsltSource);
                response.setContentType("image/svg+xml");
            } else {
                log.error("Exception stylesheet not found, sending back raw XML");
                thandler = factory.newTransformerHandler();
                response.setContentType("application/xml");
            }

            ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            try {
                Result res = new javax.xml.transform.stream.StreamResult(bout);
                thandler.setResult(res);
                generateSAX(t, thandler);
            } finally {
                bout.close();
            }
    
            response.setContentLength(bout.size());
            response.getOutputStream().write(bout.toByteArray());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Error in error servlet", e);
            throw new ServletException(e);
        }
    }

    private void generateSAX(Throwable t, ContentHandler handler) throws SAXException {
        if (t == null) {
            throw new NullPointerException("Throwable must not be null");
        }
        if (handler == null) {
            throw new NullPointerException("ContentHandler not set");
        }
    
        handler.startDocument();
        generateSAXForException(t, handler, "exception");
        handler.endDocument();
    }
    
    private void generateSAXForException(Throwable t, 
                ContentHandler handler, String elName) throws SAXException {
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute(null, "classname", "classname", "CDATA", t.getClass().getName());
        handler.startElement(null, elName, elName, attr);
        attr.clear();
        handler.startElement(null, "msg", "msg", attr);
        char[] chars = t.getMessage().toCharArray();
        handler.characters(chars, 0, chars.length);
        handler.endElement(null, "msg", "msg");
        
        if (t instanceof CascadingException) {
            Throwable nested = ((CascadingException)t).getCause();
            generateSAXForException(nested, handler, "nested");
        }
        
        handler.endElement(null, elName, elName);
    }
}
