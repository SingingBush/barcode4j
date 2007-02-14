/*
 * Copyright 2003-2004,2007 Jeremias Maerki or contributors to Barcode4J, as applicable
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
package org.krysalis.barcode4j.fop0205;

import java.util.HashMap;

import org.apache.fop.fo.DirectPropertyListBuilder;
import org.apache.fop.fo.ElementMapping;
import org.apache.fop.fo.TreeBuilder;
import org.apache.fop.fo.FObj;

import org.krysalis.barcode4j.BarcodeConstants;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;

/**
 * Registers the elements covered by Barcode4J's namespace.
 * 
 * @version $Id: BarcodeElementMapping.java,v 1.8 2007-02-14 10:19:07 jmaerki Exp $
 */
public class BarcodeElementMapping implements ElementMapping {

    private static HashMap foObjs = null;    
    
    protected FObj.Maker getBarcodeElementMaker() {
        return BarcodeElement.maker();
    }
    
    protected FObj.Maker getBarcodeObjMaker(String name) {
        return BarcodeObj.maker(name);
    }
    
    private synchronized void setupBarcodeElements() {
        if (foObjs == null) {
            foObjs = new HashMap();
            String[] elements = ConfigurableBarcodeGenerator.BARCODE_ELEMENTS;
            foObjs.put("barcode", getBarcodeElementMaker());
            for (int i = 0; i < elements.length; i++) {
                foObjs.put(elements[i], getBarcodeObjMaker(elements[i]));
            }
        }
    }

    public void addToBuilder(TreeBuilder builder) {
        setupBarcodeElements();
        builder.addMapping(BarcodeConstants.NAMESPACE, foObjs);

        builder.addPropertyListBuilder(BarcodeConstants.NAMESPACE, new DirectPropertyListBuilder());
        
        //for compatibility (Krysalis Barcode)
        builder.addMapping(BarcodeConstants.OLD_NAMESPACE, foObjs);
        builder.addPropertyListBuilder(BarcodeConstants.OLD_NAMESPACE, 
                new DirectPropertyListBuilder());
    }
}

