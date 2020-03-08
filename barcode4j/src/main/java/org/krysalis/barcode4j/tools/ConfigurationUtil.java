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
package org.krysalis.barcode4j.tools;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * This utility class provides helper methods for Avalon Configuration objects.
 *
 * @author Jeremias Maerki
 * @version $Id: ConfigurationUtil.java,v 1.4 2008-12-10 15:52:37 jmaerki Exp $
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
        try {
            return document.getDocumentElement(); //Xalan-bug, doesn't work (2.4.1)
        } catch (Exception e) {
            //Alternative method
            Node nd = null;
            for (int i = 0; i < document.getChildNodes().getLength(); i++) {
                nd = document.getChildNodes().item(i);
                if (nd.getNodeType() == Node.ELEMENT_NODE) {
                    return (Element)nd;
                }
            }
            return null;
        }
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

    /**
     * Extracts the message from the barcode XML. Escaped Unicode characters are unescaped.
     * @param cfg the configuration object containing the barcode XML
     * @return the message
     * @throws ConfigurationException if an error occurs retrieving values from the configuration
     */
    public static String getMessage(Configuration cfg) throws ConfigurationException {
        String msg;
        try {
            msg = cfg.getAttribute("message");
        } catch (ConfigurationException ce) {
            try {
                msg = cfg.getAttribute("msg"); //for compatibility
            } catch (ConfigurationException ce1) {
                throw ce;
            }
        }
        msg = MessageUtil.unescapeUnicode(msg);
        return msg;
    }

}
