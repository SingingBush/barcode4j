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

import org.apache.avalon.framework.configuration.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * Test case for ConfigurationUtil.
 * 
 * @author Jeremias Maerki
 * @version $Id: ConfigurationUtilTestCase.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
 */
public class ConfigurationUtilTestCase extends TestCase {

    public ConfigurationUtilTestCase(String name) {
        super(name);
    }

    public void testDOMLevel1ToConfiguration() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("root");
        root.setAttribute("name", "value");
        doc.appendChild(root);
        Element child = doc.createElement("child");
        child.setAttribute("foo", "bar"); 
        child.appendChild(doc.createTextNode("hello"));
        root.appendChild(child); 
        
        Configuration cfg = ConfigurationUtil.buildConfiguration(root);
        //Configuration cfg = ConfigurationUtil.toConfiguration(root);
        //System.out.println(org.apache.avalon.framework.configuration.ConfigurationUtil.toString(cfg));
        
        checkCfgTree(cfg);
    }
    
    public void testDOMLevel2ToConfiguration() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        final String NS = "http://somenamespace";
        Element root = doc.createElementNS(NS, "root");
        root.setAttribute("name", "value");
        doc.appendChild(root);
        Element child = doc.createElementNS(NS, "child");
        child.setAttribute("foo", "bar");
        child.appendChild(doc.createTextNode("hello"));
        root.appendChild(child); 
        
        Configuration cfg = ConfigurationUtil.buildConfiguration(root);
        //Configuration cfg = ConfigurationUtil.toConfiguration(root);
        //System.out.println(org.apache.avalon.framework.configuration.ConfigurationUtil.toString(cfg));
        
        checkCfgTree(cfg);
    }
    
    private void checkCfgTree(final Configuration cfg) throws Exception {
        assertNotNull(cfg);
        assertEquals("root", cfg.getName());
        assertEquals("value", cfg.getAttribute("name"));
        assertNull(cfg.getValue(null));
        Configuration childcfg = cfg.getChild("child");
        assertNotNull(childcfg);
        assertEquals("child", childcfg.getName());
        assertEquals("bar", childcfg.getAttribute("foo"));
        assertEquals("hello", childcfg.getValue());
    }

}
