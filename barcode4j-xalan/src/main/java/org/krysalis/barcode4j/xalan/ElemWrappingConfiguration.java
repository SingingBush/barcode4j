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
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.krysalis.barcode4j.configuration.AbstractConfiguration;
import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the Configuration interface (previously an Avalon Configuration) that wraps the
 * not fully implemented DOM nodes coming from Xalan-J.
 *
 * @author Jeremias Maerki
 * @version $Id: ElemWrappingConfiguration.java,v 1.2 2004-09-04 20:26:16 jmaerki Exp $
 */
public class ElemWrappingConfiguration extends AbstractConfiguration {

    private final Element elem;

    /**
     * Creates a new Configuration wrapper/adapter around a DOM element.
     * @param elem the DOM element
     */
    public ElemWrappingConfiguration(Element elem) {
        this.elem = elem;
    }

    /**
     * @see org.krysalis.barcode4j.configuration.AbstractConfiguration#getPrefix()
     */
    @Override
    protected String getPrefix() throws ConfigurationException {
        return null;
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getName()
     */
    @Override
    public @NotNull String getName() {
        return this.elem.getLocalName();
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getLocation()
     */
    @Override
    public String getLocation() {
        return "unknown";
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getNamespace()
     */
    @Override
    public @NotNull String getNamespace() throws ConfigurationException {
        return "";
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getChildren()
     */
    @Override
    public Configuration[] getChildren() {
        Configuration[] cfgList = new Configuration[this.elem.getChildNodes().getLength()];
        for (int i = 0; i < cfgList.length; i++) {
            cfgList[i] = new ElemWrappingConfiguration((Element)this.elem.getChildNodes().item(i));
        }
        return cfgList;
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getChildren(java.lang.String)
     */
    @Override
    public Configuration[] getChildren(@NotNull String name) {
        final List<Configuration> cfgList = new java.util.LinkedList<>();
        final NodeList elems = this.elem.getChildNodes();

        for (int i = 0; i < elems.getLength(); i++) {
            final Node node = elems.item(i);
            if ((node instanceof Element)
                    && (node.getLocalName().equals(name))) {
                cfgList.add(new ElemWrappingConfiguration((Element)node));
            }
        }
        return cfgList.toArray(new Configuration[cfgList.size()]);
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getAttributeNames()
     */
    @Override
    public String[] getAttributeNames() {
        throw new UnsupportedOperationException("getAttributeNames() is not supported");
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getAttribute(java.lang.String)
     */
    @Override
    public @NotNull String getAttribute(@NotNull String name) throws ConfigurationException {
        return Optional.of(this.elem.getAttribute(name))
            .orElseThrow(() -> new ConfigurationException("Attribute '" + name + "' does not exist"));
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configuration#getValue()
     */
    @Override
    public @NotNull String getValue() throws ConfigurationException {
        final NodeList nodes = elem.getChildNodes();
        final StringBuffer sb = new StringBuffer();

        for (int i = 0; i < nodes.getLength(); i++) {
            final Node node = nodes.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                sb.append(node.getNodeValue());
            }
        }
        return sb.toString();
    }

}
