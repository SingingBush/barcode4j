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
package org.krysalis.barcode4j.fop0205;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.PropertyList;
import org.apache.fop.fo.XMLObj;
import org.apache.fop.fo.FObj;

import org.krysalis.barcode4j.BarcodeConstants;

/**
 * Base object for barcode nodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeObj.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class BarcodeObj extends XMLObj {
    
    /**
     * Inner class for making barcode objects.
     */
    public static class Maker extends FObj.Maker {
        String tag;

        Maker(String str) {
            tag = str;
        }

        /**
         * Make an svg object.
         *
         * @param parent the parent formatting object
         * @param propertyList the explicit properties of this object
         *
         * @return the barcode object
         */
        public FObj make(FObj parent, PropertyList propertyList,
                         String systemId, int line, int column)
                        throws FOPException {
            return new BarcodeObj(parent, propertyList, tag,
                              systemId, line, column);
        }
    }

    /**
     * returns the maker for this object.
     *
     * @return the maker for an svg object
     */
    public static FObj.Maker maker(String str) {
        return new BarcodeObj.Maker(str);
    }

    /**
     * constructs an svg object (called by Maker).
     *
     * @param parent the parent formatting object
     * @param propertyList the explicit properties of this object
     */
    protected BarcodeObj(FObj parent, PropertyList propertyList, String tag,
                     String systemId, int line, int column) {
        super(parent, propertyList, tag, systemId, line, column);
    }

    public String getName() {
        return "bc:" + tagName;
    }

    public String getNameSpace() {
        return BarcodeConstants.NAMESPACE;
    }
}

