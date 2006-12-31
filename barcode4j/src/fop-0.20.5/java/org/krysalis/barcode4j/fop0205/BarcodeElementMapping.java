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
package org.krysalis.barcode4j.fop0205;

import java.util.HashMap;

import org.apache.fop.fo.DirectPropertyListBuilder;
import org.apache.fop.fo.ElementMapping;
import org.apache.fop.fo.TreeBuilder;
import org.apache.fop.fo.FObj;

import org.krysalis.barcode4j.BarcodeConstants;

/**
 * Registers the elements covered by Barcode4J's namespace.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeElementMapping.java,v 1.5 2006-12-31 16:48:18 buerkle Exp $
 */
public class BarcodeElementMapping implements ElementMapping {

    private static final String[] BARCODE_ELEMENTS =
        {"intl2of5", "code39", "codabar", "code128", 
	     "ean128", "template", "group-separator", "check-digit-marker", "omit-brackets",
         "upc-a", "upc-e", "ean-13", "ean-8",
         "postnet",
         "height", "module-width", "wide-factor", "quiet-zone",
         "checksum", "human-readable",
         "human-readable-font", "human-readable-size",
         "font-name", "font-size", "placement", 
         "display-start-stop", "display-checksum",
         "interchar-gap-width",
         "tall-bar-height", "short-bar-height", "baseline-alignment"
         };

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
            foObjs.put("barcode", getBarcodeElementMaker());
            for (int i = 0; i < BARCODE_ELEMENTS.length; i++) {
                foObjs.put(BARCODE_ELEMENTS[i], getBarcodeObjMaker(BARCODE_ELEMENTS[i]));
            }
        }
    }

    public void addToBuilder(TreeBuilder builder) {
        setupBarcodeElements();
        builder.addMapping(BarcodeConstants.NAMESPACE, foObjs);

        builder.addPropertyListBuilder(BarcodeConstants.NAMESPACE, new DirectPropertyListBuilder());
        
        //for compatibility (Krysalis Barcode)
        builder.addMapping(BarcodeConstants.OLD_NAMESPACE, foObjs);
		builder.addPropertyListBuilder(BarcodeConstants.OLD_NAMESPACE, new DirectPropertyListBuilder());
    }
}

