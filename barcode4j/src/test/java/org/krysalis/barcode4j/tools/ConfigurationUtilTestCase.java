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
package org.krysalis.barcode4j.tools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.krysalis.barcode4j.configuration.Configuration;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test case for ConfigurationUtil.
 *
 * @author Jeremias Maerki
 * @version $Id: ConfigurationUtilTestCase.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
 */
public class ConfigurationUtilTestCase {

    @Test
    void testDOMLevel1ToConfiguration() throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.newDocument();
        final Element root = doc.createElement("root");
        root.setAttribute("name", "value");
        doc.appendChild(root);
        final Element child = doc.createElement("child");
        child.setAttribute("foo", "bar");
        child.appendChild(doc.createTextNode("hello"));
        root.appendChild(child);

        final Configuration cfg = ConfigurationUtil.buildConfiguration(root);
        //Configuration cfg = ConfigurationUtil.toConfiguration(root);
        //System.out.println(org.apache.avalon.framework.configuration.ConfigurationUtil.toString(cfg));

        checkCfgTree(cfg);
    }

    @Test
    void testDOMLevel2ToConfiguration() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        final String NS = "http://somenamespace";
        final Element root = doc.createElementNS(NS, "root");
        root.setAttribute("name", "value");
        doc.appendChild(root);
        final Element child = doc.createElementNS(NS, "child");
        child.setAttribute("foo", "bar");
        child.appendChild(doc.createTextNode("hello"));
        root.appendChild(child);

        final Configuration cfg = ConfigurationUtil.buildConfiguration(root);
        //Configuration cfg = ConfigurationUtil.toConfiguration(root);
        //System.out.println(org.apache.avalon.framework.configuration.ConfigurationUtil.toString(cfg));

        checkCfgTree(cfg);
    }

    private void checkCfgTree(final Configuration cfg) throws Exception {
        assertNotNull(cfg);
        assertEquals("root", cfg.getName());
        assertEquals("value", cfg.getAttribute("name"));
        assertNull(cfg.getValue(null));
        final Configuration childcfg = cfg.getChild("child");
        assertNotNull(childcfg);
        assertEquals("child", childcfg.getName());
        assertEquals("bar", childcfg.getAttribute("foo"));
        assertEquals("hello", childcfg.getValue());
    }

}
