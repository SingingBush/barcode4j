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

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.ZXingUtil;

/**
 * This is a simple implementation of a BarcodeClassResolver.
 *
 * @version $Id: DefaultBarcodeClassResolver.java,v 1.14 2012-01-27 14:36:35 jmaerki Exp $
 */
public class DefaultBarcodeClassResolver implements BarcodeClassResolver {

    private Map<String, String> classes;
    private Set<String> mainIDs;

    /**
     * Main constructor.
     * <br>
     * Already registers a default set of implementations.
     */
    public DefaultBarcodeClassResolver() {
        registerBarcodeClass("codabar", org.krysalis.barcode4j.impl.codabar.Codabar.class, true);
        registerBarcodeClass("code39", org.krysalis.barcode4j.impl.code39.Code39.class, true);
        registerBarcodeClass("code128", org.krysalis.barcode4j.impl.code128.Code128.class, true);
        registerBarcodeClass("ean-128", org.krysalis.barcode4j.impl.code128.EAN128.class, true);
        registerBarcodeClass("ean128", org.krysalis.barcode4j.impl.code128.EAN128.class);
        registerBarcodeClass("2of5", org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5.class);
        registerBarcodeClass("intl2of5", org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5.class, true);
        registerBarcodeClass("interleaved2of5", org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5.class);
        registerBarcodeClass("itf-14", org.krysalis.barcode4j.impl.int2of5.ITF14.class, true);
        registerBarcodeClass("itf14", org.krysalis.barcode4j.impl.int2of5.ITF14.class);
        registerBarcodeClass("ean-13", org.krysalis.barcode4j.impl.upcean.EAN13.class, true);
        registerBarcodeClass("ean13", org.krysalis.barcode4j.impl.upcean.EAN13.class);
        registerBarcodeClass("ean-8", org.krysalis.barcode4j.impl.upcean.EAN8.class, true);
        registerBarcodeClass("ean8", org.krysalis.barcode4j.impl.upcean.EAN8.class);
        registerBarcodeClass("upc-a", org.krysalis.barcode4j.impl.upcean.UPCA.class, true);
        registerBarcodeClass("upca", org.krysalis.barcode4j.impl.upcean.UPCA.class);
        registerBarcodeClass("upc-e", org.krysalis.barcode4j.impl.upcean.UPCE.class, true);
        registerBarcodeClass("upce", org.krysalis.barcode4j.impl.upcean.UPCE.class);
        registerBarcodeClass("postnet", org.krysalis.barcode4j.impl.postnet.POSTNET.class, true);
        registerBarcodeClass("royal-mail-cbc", org.krysalis.barcode4j.impl.fourstate.RoyalMailCBC.class, true);
        registerBarcodeClass("usps4cb", org.krysalis.barcode4j.impl.fourstate.USPSIntelligentMail.class, true);
        /* registerBarcodeClass("austpost", "org.krysalis.barcode4j.impl.fourstate.AustPost", true); */
        registerBarcodeClass("pdf417", org.krysalis.barcode4j.impl.pdf417.PDF417.class, true);
        registerBarcodeClass("datamatrix", org.krysalis.barcode4j.impl.datamatrix.DataMatrix.class, true);

        if (ZXingUtil.isZxingAvailable()) {
            // QR Code currently uses ZXing for encoding
            registerBarcodeClass("qr", org.krysalis.barcode4j.impl.qr.QRCode.class, true);
            registerBarcodeClass("qrcode", org.krysalis.barcode4j.impl.qr.QRCode.class);
            registerBarcodeClass("qr-code", org.krysalis.barcode4j.impl.qr.QRCode.class);

            // Aztec also requires ZXing
            registerBarcodeClass("aztec", org.krysalis.barcode4j.impl.aztec.Aztec.class, true);
        }
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param clazz the class to register
     * @param mainID indicates whether the name is the main name for the barcode
     * @since 2.3.2
     */
    public <T extends ConfigurableBarcodeGenerator> void registerBarcodeClass(String id, Class<T> clazz, boolean mainID) {
        registerBarcodeClass(id, clazz.getCanonicalName(), mainID);
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param clazz the class to register
     * @since 2.3.2
     */
    public <T extends ConfigurableBarcodeGenerator> void registerBarcodeClass(String id, Class<T> clazz) {
        registerBarcodeClass(id, clazz.getCanonicalName(), false);
    }

    /**
     * Registers a barcode implementation.
     * @param id short name to use as a key
     * @param classname fully qualified classname
     */
    @Deprecated
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
            this.classes = new java.util.HashMap<>();
            this.mainIDs = new java.util.HashSet<>();
        }
        this.classes.put(id.toLowerCase(), classname);
        if (mainID) {
            this.mainIDs.add(id);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.krysalis.barcode4j.BarcodeClassResolver#resolve(String)
     */
    @Override
    public Class<BarcodeGenerator> resolve(final String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        return (Class<BarcodeGenerator>) Class.forName(clazz);
    }

    /**
     * {@inheritDoc}
     * @see org.krysalis.barcode4j.BarcodeClassResolver#resolveBean(String)
     */
    @Override
    public Class<AbstractBarcodeBean> resolveBean(String name) throws ClassNotFoundException {
        String clazz = null;
        if (this.classes != null) {
            clazz = this.classes.get(name.toLowerCase());
        }
        if (clazz == null) {
            clazz = name;
        }
        return (Class<AbstractBarcodeBean>) Class.forName(clazz + "Bean"); // todo: get rid of this as it won't work with GraalVM native image
    }

    /**
     * {@inheritDoc}
     * @see org.krysalis.barcode4j.BarcodeClassResolver#getBarcodeNames()
     */
    @Override
    public Collection<String> getBarcodeNames() {
        return Collections.unmodifiableCollection(this.mainIDs);
    }
}
