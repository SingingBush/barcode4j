/*
 * $Id: ElemWrappingConfiguration.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2003 Nicola Ken Barozzi.  All rights reserved.
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
package org.krysalis.barcode4j.xalan;

import java.util.List;

import org.apache.avalon.framework.configuration.AbstractConfiguration;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the Avalon Configuration interface that wraps the
 * not fully implemented DOM nodes coming from Xalan-J.
 * 
 * @author Jeremias Maerki
 */
public class ElemWrappingConfiguration extends AbstractConfiguration {

    private Element elem;

    /**
     * Creates a new Configuration wrapper/adapter around a DOM element.
     * @param elem the DOM element
     */
    public ElemWrappingConfiguration(Element elem) {
        this.elem = elem;
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.AbstractConfiguration#getPrefix()
     */
    protected String getPrefix() throws ConfigurationException {
        return null;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getName()
     */
    public String getName() {
        return this.elem.getLocalName();
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getLocation()
     */
    public String getLocation() {
        return "unknown";
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getNamespace()
     */
    public String getNamespace() throws ConfigurationException {
        return null;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChildren()
     */
    public Configuration[] getChildren() {
        Configuration[] cfgList = new Configuration[this.elem.getChildNodes().getLength()];
        for (int i = 0; i < cfgList.length; i++) {
            cfgList[i] = new ElemWrappingConfiguration((Element)this.elem.getChildNodes().item(i));
        }
        return cfgList;
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getChildren(java.lang.String)
     */
    public Configuration[] getChildren(String name) {
        List cfgList = new java.util.LinkedList();
        NodeList elems = this.elem.getChildNodes();
        for (int i = 0; i < elems.getLength(); i++) {
            final Node node = elems.item(i);
            if ((node instanceof Element)
                    && (node.getLocalName().equals(name))) {
                cfgList.add(new ElemWrappingConfiguration((Element)node));
            }
        }
        return (Configuration[])cfgList.toArray(new Configuration[cfgList.size()]);
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttributeNames()
     */
    public String[] getAttributeNames() {
        throw new UnsupportedOperationException("getAttributeNames() is not supported");
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getAttribute(java.lang.String)
     */
    public String getAttribute(String name) throws ConfigurationException {
        final String s = this.elem.getAttribute(name);
        if (s != null) {
            return s;
        } else {
            throw new ConfigurationException("Attribut '" + name + "' does not exist");
        }
    }

    /**
     * @see org.apache.avalon.framework.configuration.Configuration#getValue()
     */
    public String getValue() throws ConfigurationException {
        //System.out.println(elem.getClass().getName() + " " + elem.getLocalName());
        //System.out.println(elem.hasChildNodes() + " " + elem.getChildNodes().getLength());
        //System.out.println(elem.getNodeValue());
        NodeList nodes = elem.getChildNodes();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            //System.out.println(node + " " + node.getNodeType() 
            //    + " " + node.getChildNodes().getLength());
            //System.out.println(node.getNodeValue());
            if (node.getNodeType() != Node.TEXT_NODE) {
                sb.append(node.getNodeValue());
            }
        }
        return sb.toString();
    }

}
