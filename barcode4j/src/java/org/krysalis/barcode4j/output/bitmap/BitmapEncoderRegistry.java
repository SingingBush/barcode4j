/*
 * $Id: BitmapEncoderRegistry.java,v 1.1 2003-12-13 20:23:41 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.output.bitmap;

import java.util.Iterator;
import java.util.Set;

/**
 * Registry class for BitmapEncoders.
 * 
 * @author Jeremias Maerki
 */
public class BitmapEncoderRegistry {

    private static Set encoders = new java.util.TreeSet();

    static {
        register("org.krysalis.barcode4j.output.bitmap.SunJPEGBitmapEncoder", 0,  false);
        register("org.krysalis.barcode4j.output.bitmap.ImageIOBitmapEncoder", 50, false);
    }

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected BitmapEncoderRegistry() {
        throw new UnsupportedOperationException();
    }
    
    private static class Entry implements Comparable {
        private BitmapEncoder encoder;
        private int priority;
        
        public Entry(BitmapEncoder encoder, int priority) {
            this.encoder = encoder;
            this.priority = priority;
        }
        
        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) {
            Entry e = (Entry)o;
            return e.priority - this.priority; //highest priority first
        }
    }
    
    private static void register(String classname, int priority, boolean complain) {
        boolean failed = false;
        try {
            Class clazz = Class.forName(classname);
            BitmapEncoder encoder = (BitmapEncoder)clazz.newInstance();
            encoders.add(new Entry(encoder, priority));
        } catch (Exception e) {
            failed = true;
        } catch (LinkageError le) {
            failed = true; //NoClassDefFoundError for example
        }
        if (failed) {
            if (complain) {
                throw new IllegalArgumentException(
                    "The implementation being registered is unavailable or "
                    + "cannot be instantiated: " + classname);
            } else {
                return;
            }
        }
    }

    /**
     * Register a new BitmapEncoder implementation.
     * @param classname fully qualified classname of the BitmapEncoder 
     *      implementation
     * @param priority lets you define a priority for an encoder. If you want
     *      to give an encoder a high priority, assign a value of 100 or higher. 
     */
    public static void register(String classname, int priority) {
        register(classname, priority, true);
    }

    /**
     * Indicates whether a specific BitmapEncoder implementation supports a
     * particular MIME type.
     * @param encoder BitmapEncoder to inspect
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(BitmapEncoder encoder, String mime) {
        String[] mimes = encoder.getSupportedMIMETypes();
        for (int i = 0; i < mimes.length; i++) {
            if (mimes[i].equals(mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether a particular MIME type is supported by one of the
     * registered BitmapEncoder implementations.
     * @param mime MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(String mime) {
        Iterator i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            BitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a BitmapEncoder instance for a particular MIME type.
     * @param mime desired MIME type
     * @return a BitmapEncoder instance (throws an UnsupportedOperationException
     *      if no suitable BitmapEncoder is available)
     */
    public static BitmapEncoder getInstance(String mime) {
        Iterator i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            BitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mime)) {
                return encoder;
            }
        }
        throw new UnsupportedOperationException(
            "No BitmapEncoder available for " + mime);
    }

    /**
     * Returns a Set of Strings with all the supported MIME types from all
     * registered BitmapEncoders.
     * @return a Set of Strings (MIME types)
     */
    public static Set getSupportedMIMETypes() {
        Set mimes = new java.util.HashSet();
        Iterator i = encoders.iterator();
        while (i.hasNext()) {
            Entry entry = (Entry)i.next();
            BitmapEncoder encoder = entry.encoder;
            String[] s = encoder.getSupportedMIMETypes();
            for (int j = 0; j < s.length; j++) {
                mimes.add(s[j]);
            }
        }
        return mimes;
    }

}
