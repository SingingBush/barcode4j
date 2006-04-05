/*
 * Copyright 2003-2004 Jeremias Maerki.
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

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.krysalis.barcode4j.AbstractBarcodeTestCase;

/**
 * Test class for the Xalan-J extension.
 * 
 * @author Jeremias Maerki
 * @version $Id: XalanExtTest.java,v 1.5 2006-04-05 15:53:40 jmaerki Exp $
 */
public class XalanExtTest extends AbstractBarcodeTestCase {
    
    public XalanExtTest(String name) {
        super(name);
    }
    
    public void testXalanExtGenerate() throws Exception {
        innerXalanExt("xalan-test1.xsl");
    }

    public void testXalanExtBarcodeElement() throws Exception {
        innerXalanExt("xalan-test2.xsl");
    }

    public void innerXalanExt(String xslt) throws Exception {
        Class clazz = Class.forName("org.apache.xalan.processor.TransformerFactoryImpl");
        TransformerFactory factory = (TransformerFactory)clazz.newInstance();
        Transformer trans = factory.newTransformer(new StreamSource(
                new File(getBaseDir(), "src/xalan/test/xml/" + xslt)));
        Source src = new StreamSource(
                new File(getBaseDir(), "src/test/xml/xslt-test.xml"));
        StringWriter writer = new StringWriter();
        Result res = new StreamResult(writer);
        
        trans.transform(src, res);
        String output = writer.getBuffer().toString();
        assertTrue(output.indexOf("svg") >= 0);
        //System.out.println(writer.getBuffer());
    }

    public void testXalanExtSAXOutputGenerate() throws Exception {
        innerXalanExtSAXOutput("xalan-test1.xsl");
    }

    public void testXalanExtSAXOutputBarcodeElement() throws Exception {
        innerXalanExtSAXOutput("xalan-test2.xsl");
        //System.out.println("Skipping test for Xalan barcode element extension because of Xalan bug XALANJ-1706");
    }

    /* This test is done because FOP reacts with an NPE when endDocument is
     * called twice.
     */
    public void innerXalanExtSAXOutput(String xslt) throws Exception {
        Class clazz = Class.forName("org.apache.xalan.processor.TransformerFactoryImpl");
        TransformerFactory factory = (TransformerFactory)clazz.newInstance();
        Transformer trans = factory.newTransformer(new StreamSource(
                new File(getBaseDir(), "src/xalan/test/xml/" +xslt)));
        Source src = new StreamSource(
                new File(getBaseDir(), "src/test/xml/xslt-test.xml"));
        Result res = new SAXResult(new DefaultHandler() {
            private boolean endDocumentCalled = false;
            
            public void endDocument() throws SAXException {
                if (!this.endDocumentCalled) {
                    this.endDocumentCalled = true;
                } else throw new SAXException("endDocument() called twice. "
                    + "This may be due to this Xalan-J bug: "
                    + "http://issues.apache.org/jira/browse/XALANJ-1706");
            }
        });
        trans.transform(src, res);
    }

}
