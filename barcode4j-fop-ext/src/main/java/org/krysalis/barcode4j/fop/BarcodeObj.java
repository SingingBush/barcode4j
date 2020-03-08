/*
 * Copyright 2003-2005 Jeremias Maerki.
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

import org.apache.fop.fo.FONode;
import org.apache.fop.fo.XMLObj;

/**
 * Base object for barcode nodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeObj.java,v 1.1 2006-01-20 08:16:49 jmaerki Exp $
 */
public class BarcodeObj extends XMLObj {
    
    /** @see org.apache.fop.fo.FONode#FONode(FONode) */
    public BarcodeObj(FONode parent) {
        super(parent);
    }

    /** @see org.apache.fop.fo.FONode#getNamespaceURI() */
    public String getNamespaceURI() {
        return BarcodeElementMapping.NAMESPACE;
    }

    /*" @see org.apache.fop.fo.FONode#getNormalNamespacePrefix() */
    public String getNormalNamespacePrefix() {
        return "bc";
    }
    
}

