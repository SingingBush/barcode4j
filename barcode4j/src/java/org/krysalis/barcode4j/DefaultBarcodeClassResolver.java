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

import java.util.Map;

import org.krysalis.barcode4j.impl.codabar.Codabar;
import org.krysalis.barcode4j.impl.code128.Code128;
import org.krysalis.barcode4j.impl.code39.Code39;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5;
import org.krysalis.barcode4j.impl.postnet.POSTNET;
import org.krysalis.barcode4j.impl.upcean.EAN13;
import org.krysalis.barcode4j.impl.upcean.EAN8;
import org.krysalis.barcode4j.impl.upcean.UPCA;
import org.krysalis.barcode4j.impl.upcean.UPCE;

/**
 * This is a simple implementation of a BarcodeClassResolver.
 * 
 * @author Jeremias Maerki
 * @version $Id: DefaultBarcodeClassResolver.java,v 1.4 2004-10-02 14:53:22 jmaerki Exp $
 */
public class DefaultBarcodeClassResolver implements BarcodeClassResolver {

    private Map classes;

    /**
     * Main constructor.
     * <br>
     * Already registers a default set of implementations.
     */
    public DefaultBarcodeClassResolver() {
        registerBarcodeClass("codabar", Codabar.class.getName());
        registerBarcodeClass("code39", Code39.class.getName());
        registerBarcodeClass("code128", Code128.class.getName());
        registerBarcodeClass("2of5", Interleaved2Of5.class.getName());
        registerBarcodeClass("intl2of5", Interleaved2Of5.class.getName());
        registerBarcodeClass("interleaved2of5", Interleaved2Of5.class.getName());
        registerBarcodeClass("ean-13", EAN13.class.getName());
        registerBarcodeClass("ean13", EAN13.class.getName());
        registerBarcodeClass("ean-8", EAN8.class.getName());
        registerBarcodeClass("ean8", EAN8.class.getName());
        registerBarcodeClass("upc-a", UPCA.class.getName());
        registerBarcodeClass("upca", UPCA.class.getName());
        registerBarcodeClass("upc-e", UPCE.class.getName());
        registerBarcodeClass("upce", UPCE.class.getName());
        registerBarcodeClass("postnet", POSTNET.class.getName());
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
