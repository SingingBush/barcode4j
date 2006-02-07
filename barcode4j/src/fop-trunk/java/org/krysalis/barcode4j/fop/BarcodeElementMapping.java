/*
 * Copyright 2003-2006 Jeremias Maerki.
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
package org.krysalis.barcode4j.fop;

import java.util.HashMap;

import org.apache.fop.fo.ElementMapping;
import org.apache.fop.fo.FONode;

import org.krysalis.barcode4j.BarcodeConstants;
import org.w3c.dom.DOMImplementation;

/**
 * Registers the elements covered by Barcode4J's namespace.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeElementMapping.java,v 1.2 2006-02-07 14:06:04 jmaerki Exp $
 */
public class BarcodeElementMapping extends ElementMapping {

    /** Barcode4J Namespace */
    public static final String NAMESPACE = BarcodeConstants.NAMESPACE; 

    public BarcodeElementMapping() {
        this.namespaceURI = NAMESPACE;
        initialize();
    }

    /** @see org.apache.fop.fo.ElementMapping#getDOMImplementation() */
    public DOMImplementation getDOMImplementation() {
        return getDefaultDOMImplementation();
    }

    /** @see org.apache.fop.fo.ElementMapping#initialize() */
    protected void initialize() {
        if (foObjs == null) {
            foObjs = new HashMap();
            foObjs.put("barcode", new BarcodeRootMaker());
            foObjs.put(DEFAULT, new BarcodeMaker());

            //XMLReader.setConverter(this.namespaceURI, new BarcodeConverter());
        }
    }

    static class BarcodeMaker extends ElementMapping.Maker {
        public FONode make(FONode parent) {
            return new BarcodeObj(parent);
        }
    }

    static class BarcodeRootMaker extends ElementMapping.Maker {
        public FONode make(FONode parent) {
            return new BarcodeElement(parent);
        }
    }

}

