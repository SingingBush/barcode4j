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
 * ClassicBarcodeHandler generating a String representation of the barcode for
 * easy verification in tests.
 * 
 * @author Jeremias Maerki
 * @version $Id: MockClassicBarcodeLogicHandler.java,v 1.2 2004-09-04 20:25:55 jmaerki Exp $
 */
public class MockClassicBarcodeLogicHandler
            implements ClassicBarcodeLogicHandler {

    private StringBuffer sb;

    public MockClassicBarcodeLogicHandler(StringBuffer sb) {
        this.sb = sb;
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#startBarGroup(BarGroup, String)
     */
    public void startBarGroup(BarGroup type, String submsg) {
        sb.append("<SBG:");
        sb.append(type.getName());
        sb.append(":");
        sb.append(submsg);
        sb.append(">");
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#addBar(boolean, int)
     */
    public void addBar(boolean black, int weight) {
        if (black) {
            sb.append("B");
        } else {
            sb.append("W");
        }
        sb.append(weight);
    }

    /**
     * @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler#endBarGroup()
     */
    public void endBarGroup() {
        sb.append("</SBG>");
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#startBarcode(String)
     */
    public void startBarcode(String msg) {
        sb.append("<BC>");
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeLogicHandler#endBarcode()
     */
    public void endBarcode() {
        sb.append("</BC>");
    }

}
