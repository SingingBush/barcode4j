/*
 * Copyright 2003,2004 Jeremias Maerki.
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
 * @version $Id: ElemWrappingConfiguration.java,v 1.2 2004-09-04 20:26:16 jmaerki Exp $
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
