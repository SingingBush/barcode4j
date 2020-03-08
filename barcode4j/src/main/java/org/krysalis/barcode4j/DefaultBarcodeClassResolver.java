/*
 * Copyright 2002-2004,2006,2008-2009 Jeremias Maerki.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.krysalis.barcode4j.tools.ZXingUtil;

/**
 * This is a simple implementation of a BarcodeClassResolver.
 *
 * @version $Id: DefaultBarcodeClassResolver.java,v 1.14 2012-01-27 14:36:35 jmaerki Exp $
 */
public class DefaultBarcodeClassResolver implements BarcodeClassResolver {

    private Map classes;
    private Set mainIDs;

    /**
     * Main constructor.
     * <br>
     * Already registers a default set of implementations.
     */
    public DefaultBarcodeClassResolver() {
        registerBarcodeClass("codabar", "org.krysalis.barcode4j.impl.codabar.Codabar", true);
        registerBarcodeClass("code39", "org.krysalis.barcode4j.impl.code39.Code39", true);
        registerBarcodeClass("code128", "org.krysalis.barcode4j.impl.code128.Code128", true);
        registerBarcodeClass("ean-128", "org.krysalis.barcode4j.impl.code128.EAN128", true);
        registerBarcodeClass("ean128", "org.krysalis.barcode4j.impl.code128.EAN128");
        registerBarcodeClass("2of5", "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5");
        registerBarcodeClass("intl2of5",
                "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5", true);
        registerBarcodeClass("interleaved2of5",
                "org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5");
        registerBarcodeClass("itf-14", "org.krysalis.barcode4j.impl.int2of5.ITF14", true);
        registerBarcodeClass("itf14", "org.krysalis.barcode4j.impl.int2of5.ITF14");
        registerBarcodeClass("ean-13", "org.krysalis.barcode4j.impl.upcean.EAN13", true);
        registerBarcodeClass("ean13", "org.krysalis.barcode4j.impl.upcean.EAN13");
        registerBarcodeClass("ean-8", "org.krysalis.barcode4j.impl.upcean.EAN8", true);
        registerBarcodeClass("ean8", "org.krysalis.barcode4j.impl.upcean.EAN8");
        registerBarcodeClass("upc-a", "org.krysalis.barcode4j.impl.upcean.UPCA", true);
        registerBarcodeClass("upca", "org.krysalis.barcode4j.impl.upcean.UPCA");
        registerBarcodeClass("upc-e", "org.krysalis.barcode4j.impl.upcean.UPCE", true);
        registerBarcodeClass("upce", "org.krysalis.barcode4j.impl.upcean.UPCE");
        registerBarcodeClass("postnet", "org.krysalis.barcode4j.impl.postnet.POSTNET", true);
        registerBarcodeClass("royal-mail-cbc",
                "org.krysalis.barcode4j.impl.fourstate.RoyalMailCBC", true);
        registerBarcodeClass("usps4cb",
                "org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail", true);
        /*
        registerBarcodeClass("austpost",
                "org.krysalis.barcode4j.impl.fourstate.AustPost", true);
                */
        registerBarcodeClass("pdf417", "org.krysalis.barcode4j.impl.pdf417.PDF417", true);
        registerBarcodeClass("datamatrix",
                "org.krysalis.barcode4j.impl.datamatrix.DataMatrix", true);
        if (ZXingUtil.isZxingAvailable()) {
            //QR Code currently uses ZXing for encoding
            final String clazz = "org.krysalis.barcode4j.impl.qr.QRCode";
            registerBarcodeClass("qr", clazz, true);
            registerBarcodeClass("qrcode", clazz);
            registerBarcodeClass("qr-code", clazz);
        }
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param classname fully qualified classname
     */
    public void registerBarcodeClass(String id, String classname) {
        registerBarcodeClass(id, classname, false);
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param classname fully qualified classname
     * @param mainID indicates whether the name is the main name for the barcode
     */
    public void registerBarcodeClass(String id, String classname, boolean mainID) {
        if (this.classes == null) {
            this.classes = new java.util.HashMap();
            this.mainIDs = new java.util.HashSet();
        }
        this.classes.put(id.toLowerCase(), classname);
        if (mainID) {
            this.mainIDs.add(id);
        }
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

    /**
     * @see org.krysalis.barcode4j.BarcodeClassResolver#getBarcodeNames()
     */
    public Collection getBarcodeNames() {
        return Collections.unmodifiableCollection(this.mainIDs);
    }
}
