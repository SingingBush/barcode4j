/*
 * $Id: ConfigurationUtil.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.tools;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * This utility class provides helper methods for Avalon Configuration objects.
 * 
 * @author Jeremias Maerki
 */
public class ConfigurationUtil {

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected ConfigurationUtil() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Builds a Configuration object from a DOM node.
     * @param node the DOM node
     * @return the Configuration object
     */
    public static Configuration buildConfiguration(Node node) {
        return processNode(node);
    }
    
    private static Element findDocumentElement(Document document) {
        //return document.getDocumentElement(); Xalan-bug, doesn't work (2.4.1)
        Node nd = null;
        for (int i = 0; i < document.getChildNodes().getLength(); i++) {
            nd = document.getChildNodes().item(i);
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)nd;
            }
        }
        return null;
    }
    
    private static DefaultConfiguration processNode(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return processElement((Element)node);
        } else if (node.getNodeType() == Node.DOCUMENT_NODE) {
            return processElement(findDocumentElement((Document)node));
        } else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
            DocumentFragment df = (DocumentFragment)node;
            return processNode(df.getFirstChild());
        } else {
            return null;
        }
    }
    
    private static DefaultConfiguration processElement(Element el) {
        String name = el.getLocalName();
        if (name == null) {
            name = el.getTagName();
        }
        DefaultConfiguration cfg = new DefaultConfiguration(name);
        NamedNodeMap atts = el.getAttributes();
        for (int i = 0; i < atts.getLength(); i++) {
            Attr attr = (Attr)atts.item(i);
            cfg.setAttribute(attr.getName(), attr.getValue());
        }
        for (int i = 0; i < el.getChildNodes().getLength(); i++) {
            Node node = el.getChildNodes().item(i);
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                Attr attr = (Attr)node;
                cfg.setAttribute(attr.getName(), attr.getNodeValue());
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                cfg.addChild(processElement((Element)node));
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                String s = cfg.getValue("") + ((Text)node).getData();
                cfg.setValue(s.trim());
            } else {
                //ignore
            }
        }
        return cfg;
    }

}
