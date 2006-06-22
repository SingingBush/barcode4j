/*
 * Copyright 2002-2004,2006 Jeremias Maerki.
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

import java.util.Map;

/**
 * This is a simple implementation of a BarcodeClassResolver.
 * 
 * @author Jeremias Maerki
 * @version $Id: DefaultBarcodeClassResolver.java,v 1.6 2006-06-22 09:01:19 jmaerki Exp $
 */
public class DefaultBarcodeClassResolver implements BarcodeClassResolver {

    private Map classes;

    /**
     * Main constructor.
     * <br>
     * Already registers a default set of implementations.
     */
    public DefaultBarcodeClassResolver() {
        registerBarcodeClass("codabar", "org.krysalis.barcode4j.impl.codabar.Codabar");
        registerBarcodeClass("code39", "org.krysalis.barcode4j.impl.code39.Code39");
        registerBarcodeClass("code128", "org.krysalis.barcode4j.impl.code128.Code128");
        registerBarcodeClass("2of5", "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5");
        registerBarcodeClass("intl2of5", "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5");
        registerBarcodeClass("interleaved2of5", 
                "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5");
        registerBarcodeClass("ean-13", "org.krysalis.barcode4j.impl.upcean.EAN13");
        registerBarcodeClass("ean13", "org.krysalis.barcode4j.impl.upcean.EAN13");
        registerBarcodeClass("ean-8", "org.krysalis.barcode4j.impl.upcean.EAN8");
        registerBarcodeClass("ean8", "org.krysalis.barcode4j.impl.upcean.EAN8");
        registerBarcodeClass("upc-a", "org.krysalis.barcode4j.impl.upcean.UPCA");
        registerBarcodeClass("upca", "org.krysalis.barcode4j.impl.upcean.UPCA");
        registerBarcodeClass("upc-e", "org.krysalis.barcode4j.impl.upcean.UPCE");
        registerBarcodeClass("upce", "org.krysalis.barcode4j.impl.upcean.UPCE");
        registerBarcodeClass("postnet", "org.krysalis.barcode4j.impl.postnet.POSTNET");
        registerBarcodeClass("pdf417", "org.krysalis.barcode4j.impl.pdf417.PDF417");
    }

    /**
     * Registers a barcode implementation.
     * @param name short name to use as a key
     * @param classname fully qualified classname
     */
    public void registerBarcodeClass(String name, String classname) {
        if (this.classes == null) {
            this.classes = new java.util.HashMap();
        }
        this.classes.put(name.toLowerCase(), classname);
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeClassResolver#resolve(String)
     */
    public Class resolve(String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = (String)this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        Class cl = Class.forName(clazz);
        return cl;
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeClassResolver#resolveBean(String)
     */
    public Class resolveBean(String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = (String)this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        Class cl = Class.forName(clazz + "Bean");
        return cl;
    }
}
