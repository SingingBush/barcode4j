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
package org.krysalis.barcode4j.foppost0205;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.FObj;
import org.apache.fop.fo.PropertyList;
import org.apache.fop.layout.FontState;

/**
 * Class representing bc:barcode pseudo flow object.
 * 
 * @version $Id: BarcodeElement.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
 */
public class BarcodeElement extends org.krysalis.barcode4j.fop0205.BarcodeElement {

    /**
     * inner class for making SVG objects.
     */
    public static class Maker extends FObj.Maker {

        /**
         * make an SVG object.
         *
         * @param parent the parent formatting object
         * @param propertyList the explicit properties of this object
         *
         * @return the SVG object
         */
        public FObj make(FObj parent, PropertyList propertyList,
                         String systemId, int line, int column)
                        throws FOPException {
            return new BarcodeElement(parent, propertyList,
                                  systemId, line, column);
        }
    }

    /**
     * returns the maker for this object.
     *
     * @return the maker for SVG objects
     */
    public static FObj.Maker maker() {
        return new BarcodeElement.Maker();
    }

    /**
     * constructs an SVG object (called by Maker).
     *
     * @param parent the parent formatting object
     * @param propertyList the explicit properties of this object
     */
    public BarcodeElement(FObj parent, PropertyList propertyList,
                      String systemId, int line, int column) {
        super(parent, propertyList, systemId, line, column);
        init();
    }

    /**
     * Factory method for creating BarcodeAreas.
     * @param fontState the font state
     * @param width the width of the area
     * @param height the height of the area
     * @return the newly created BarcodeArea
     */
    protected org.krysalis.barcode4j.fop0205.BarcodeArea createArea(
                FontState fontState, float width, float height) {
        return new BarcodeArea(fontState, width, height);
    }

}
