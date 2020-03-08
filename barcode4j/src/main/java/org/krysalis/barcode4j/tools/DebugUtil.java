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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

/**
 * Useful stuff for debugging.
 * 
 * @author Jeremias Maerki
 * @version $Id: DebugUtil.java,v 1.2 2004-09-04 20:25:56 jmaerki Exp $
 */
public class DebugUtil {
    
    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected DebugUtil() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Serializes a W3C DOM node to a String and dumps it to System.out.
     * @param node a W3C DOM node
     */
    public static void dumpNode(Node node) {
        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            Source src = new DOMSource(node);
            Result res = new StreamResult(System.out);
            trans.transform(src, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
