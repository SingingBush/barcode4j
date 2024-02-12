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

import java.util.Collections;
import java.util.Set;

/**
 * Registry class for BitmapEncoders.
 *
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoderRegistry.java,v 1.3 2010-10-05 06:57:44 jmaerki Exp $
 */
public class BitmapEncoderRegistry {

    private static final Set<Entry> encoders = new java.util.TreeSet<>();

    static {
        register(org.krysalis.barcode4j.output.bitmap.ImageIOBitmapEncoder.class, 0, false);
    }

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected BitmapEncoderRegistry() {
        throw new UnsupportedOperationException();
    }

    private static class Entry implements Comparable<Entry> {
        private final BitmapEncoder encoder;
        private final int priority;

        public Entry(BitmapEncoder encoder, int priority) {
            this.encoder = encoder;
            this.priority = priority;
        }

        /** {@inheritDoc} */
        @Override
        public int compareTo(Entry other) {
            return other.priority - this.priority; //highest priority first
        }

    }

    private static synchronized void register(String classname, int priority, boolean complain) {
        boolean failed = false;
        try {
            final Class<?> clazz = Class.forName(classname);
            final BitmapEncoder encoder = (BitmapEncoder)clazz.newInstance();
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
     * @param classname fully qualified classname of the BitmapEncoder implementation
     * @param priority lets you define a priority for an encoder. If you want
     *      to give an encoder a high priority, assign a value of 100 or higher.
     * @deprecated please use {@link BitmapEncoderRegistry#register(Class, int)}
     */
    @Deprecated
    public static void register(String classname, int priority) {
        register(classname, priority, true);
    }

    /**
     * Register a new BitmapEncoder implementation.
     * @param bitmapEncoderClass a class that implements BitmapEncoder
     * @param priority the priority for an encoder. zero being low and 100 or higher being high
     */
    @SuppressWarnings("unused")
    public static <BitmapEncoder> void register(final Class<BitmapEncoder> bitmapEncoderClass, final int priority) {
        register(bitmapEncoderClass, priority, true);
    }

    // todo: consider making this public
    private static <BitmapEncoder> void register(final Class<BitmapEncoder> bitmapEncoderClass, final int priority, final boolean complain) {
        register(bitmapEncoderClass.getName(), priority, complain);
    }

    /**
     * Indicates whether a specific BitmapEncoder implementation supports a
     * particular MIME type.
     * @param encoder BitmapEncoder to inspect
     * @param mimeType MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(final BitmapEncoder encoder, final String mimeType) {
        final String[] mimes = encoder.getSupportedMIMETypes();

        for (final String s : mimes) {
            if (s.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicates whether a particular MIME type is supported by one of the
     * registered BitmapEncoder implementations.
     * @param mimeType MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(String mimeType) {
        for (Entry encoder : encoders) {
            if (supports(encoder.encoder, mimeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a BitmapEncoder instance for a particular MIME type.
     * @param mimeType desired MIME type
     * @return a BitmapEncoder instance (throws an UnsupportedOperationException
     *      if no suitable BitmapEncoder is available)
     */
    public static BitmapEncoder getInstance(String mimeType) {
        for (Entry entry : encoders) {
            BitmapEncoder encoder = entry.encoder;
            if (supports(encoder, mimeType)) {
                return encoder;
            }
        }
        throw new UnsupportedOperationException("No BitmapEncoder available for " + mimeType);
    }

    /**
     * Returns a Set of Strings with all the supported MIME types from all
     * registered BitmapEncoders.
     * @return a Set of Strings (MIME types)
     */
    public static Set<String> getSupportedMIMETypes() {
        Set<String> mimes = new java.util.HashSet<>();
        for (Entry entry : encoders) {
            Collections.addAll(mimes, entry.encoder.getSupportedMIMETypes());
        }
        return mimes;
    }

}
