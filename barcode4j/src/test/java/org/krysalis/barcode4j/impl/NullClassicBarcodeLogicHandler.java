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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * ClassicBarcodeHandler that does absolutely nothing. Used for for testing.
 * 
 * @author Jeremias Maerki
 * @version $Id: NullClassicBarcodeLogicHandler.java,v 1.3 2004-10-24 11:45:55 jmaerki Exp $
 */
public class NullClassicBarcodeLogicHandler
            implements ClassicBarcodeLogicHandler {

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    public void startBarGroup(BarGroup type, String submsg) {
        //nop
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#addBar(boolean, int)
     */
    public void addBar(boolean black, int weight) {
        //nop
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#endBarGroup()
     */
    public void endBarGroup() {
        //nop
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#startBarcode(String, String)
     */
    public void startBarcode(String msg, String formattedMsg) {
        //nop
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#endBarcode()
     */
    public void endBarcode() {
        //nop
    }

}
