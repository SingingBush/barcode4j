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
package org.krysalis.barcode4j.output.bitmap;

import java.util.Iterator;
import java.util.Set;

/**
 * Registry class for BitmapEncoders.
 *
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoderRegistry.java,v 1.3 2010-10-05 06:57:44 jmaerki Exp $
 */
public class BitmapEncoderRegistry {

    private static Set encoders = new java.util.TreeSet();

    static {
        register(org.krysalis.barcode4j.output.bitmap.ImageIOBitmapEncoder.class.getName(),
                0, false);
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

        /** {@inheritDoc} */
        public int compareTo(Object o) {
            Entry e = (Entry)o;
            return e.priority - this.priority; //highest priority first
        }

    }

    private static synchronized void register(String classname, int priority, boolean complain) {
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
