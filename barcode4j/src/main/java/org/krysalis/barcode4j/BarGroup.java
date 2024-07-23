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
package org.krysalis.barcode4j;

/**
 * Enumeration type for bar groups.
 *
 * @author Jeremias Maerki
 * @version $Id: BarGroup.java,v 1.3 2004-10-02 14:53:22 jmaerki Exp $
 */
public enum BarGroup {

    /**
     * Used to indicate a start character when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    START_CHARACTER("start-char"),

    /**
     * Used to indicate a stop character when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    STOP_CHARACTER("stop-char"),

    /**
     * Used to indicate a message character when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    MSG_CHARACTER("msg-char"),

    /**
     * Used to indicate a UPC/EAN Guard when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    UPC_EAN_GUARD("upc-ean-guard"),

    /**
     * Used to indicate a UPC/EAN Lead when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    UPC_EAN_LEAD("upc-ean-lead"),

    /**
     * Used to indicate a UPC/EAN character group when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    UPC_EAN_GROUP("upc-ean-group"),

    /**
     * Used to indicate a UPC/EAN check character when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    UPC_EAN_CHECK("upc-ean-check"),

    /**
     * Used to indicate a UPC/EAN supplemental when calling startBarGroup()
     *
     * @see ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    UPC_EAN_SUPP("upc-ean-supp");

    private final String name;

    /**
     * Creates a new BarGroup instance.
     * @param name name of the BarGroup
     */
    BarGroup(final String name) {
        this.name = name;
    }

    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }

}
