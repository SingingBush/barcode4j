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
 * Proxy class for logging.
 * 
 * @author Jeremias Maerki
 * @version $Id: LoggingLogicHandlerProxy.java,v 1.3 2004-10-24 11:45:37 jmaerki Exp $
 */
public class LoggingLogicHandlerProxy implements ClassicBarcodeLogicHandler {

    private ClassicBarcodeLogicHandler delegate;
    
    /**
     * Main constructor.
     * @param delegate the logic handler that the method calls are passed to.
     */
    public LoggingLogicHandlerProxy(ClassicBarcodeLogicHandler delegate) {
        this.delegate = delegate;
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarGroup(BarGroup type, String submsg) {
        System.out.println("startBarGroup(" + type + ", " + submsg + ")");
        delegate.startBarGroup(type, submsg);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarGroup() {
        System.out.println("endBarGroup()");
        delegate.endBarGroup();
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void addBar(boolean black, int weight) {
        System.out.println("addBar(" + black + ", " + weight + ")");
        delegate.addBar(black, weight);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void startBarcode(String msg, String formattedMsg) {
        System.out.println("startBarcode(" + msg + ", " + formattedMsg + ")");
        delegate.startBarcode(msg, formattedMsg);
    }

    /** @see org.krysalis.barcode4j.ClassicBarcodeLogicHandler */
    public void endBarcode() {
        System.out.println("endBarcode()");
        delegate.endBarcode();
    }

}
