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

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Registry class for BitmapEncoders.
 *
 * @author Jeremias Maerki
 * @version $Id: BitmapEncoderRegistry.java,v 1.3 2010-10-05 06:57:44 jmaerki Exp $
 */
public class BitmapEncoderRegistry {

    private static final Logger log = Logger.getLogger(BitmapEncoderRegistry.class.getName());

    private static final Set<Entry> encoders = new java.util.TreeSet<>();

    static {
        register(new ImageIOBitmapEncoder(), 0);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry entry = (Entry) o;
            return priority == entry.priority && Objects.equals(encoder, entry.encoder);
        }

        @Override
        public int hashCode() {
            return Objects.hash(encoder, priority);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Entry{");
            sb.append("encoder=").append(encoder);
            sb.append(", priority=").append(priority);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Register a new BitmapEncoder implementation.
     * @param classname fully qualified classname of the BitmapEncoder implementation
     * @param priority lets you define a priority for an encoder. If you want to give an encoder a high priority, assign a value of 100 or higher.
     * @param complain will log an error and throw an IllegalArgumentException only if set to true
     * @deprecated please use {@link BitmapEncoderRegistry#register(BitmapEncoder, int)}
     */
    @Deprecated
    private static synchronized void register(String classname, int priority, boolean complain) {
        boolean failed = false;
        try {
            final Class<?> clazz = Class.forName(classname);
            final BitmapEncoder encoder = (BitmapEncoder)clazz.getDeclaredConstructor().newInstance();
            register(encoder, priority);
        } catch (final Exception e) {
            log.warning("Failed to create BitmapEncoder of class " + classname + ": " + e.getMessage());
            failed = true;
        } catch (final LinkageError e) {
            log.warning("Failed to create BitmapEncoder of class " + classname + ": " + e.getMessage());
            failed = true; //NoClassDefFoundError for example
        }
        if (failed && complain) {
            final String msg = "The implementation being registered is unavailable or cannot be instantiated: " + classname;
            log.severe(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Register a BitmapEncoder implementation.
     * @param <T> a class that extends BitmapEncoder
     * @param bitmapEncoder an implementation of BitmapEncoder
     * @param priority lets you define a priority for an encoder. If you want to give an encoder a high priority, assign a value of 100 or higher.
     * @return true if the bitmap encoder has been registered
     * created on 23/02/2024
     * @since 2.2.3
     */
    public static synchronized <T extends BitmapEncoder> boolean register(final T bitmapEncoder, final int priority) {
        return encoders.add(new Entry(bitmapEncoder, priority));
    }

    /**
     * Register a new BitmapEncoder implementation.
     * @param classname fully qualified classname of the BitmapEncoder implementation
     * @param priority lets you define a priority for an encoder. If you want to give an encoder a high priority, assign a value of 100 or higher.
     * @deprecated please use {@link BitmapEncoderRegistry#register(BitmapEncoder, int)}
     */
    @Deprecated
    public static void register(String classname, int priority) {
        register(classname, priority, true);
    }

    /**
     * Indicates whether a specific BitmapEncoder implementation supports a
     * particular MIME type.
     * @param encoder BitmapEncoder to inspect
     * @param mimeType MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(@NotNull final BitmapEncoder encoder, final String mimeType) {
        return Arrays.asList(encoder.getSupportedMIMETypes())
            .contains(mimeType);
    }

    /**
     * Indicates whether a particular MIME type is supported by one of the
     * registered BitmapEncoder implementations.
     * @param mimeType MIME type to check
     * @return true if the MIME type is supported
     */
    public static boolean supports(String mimeType) {
        return encoders.stream()
            .map((entry) -> entry.encoder)
            .anyMatch((encoder) -> supports(encoder, mimeType));
    }

    /**
     * Returns a BitmapEncoder instance for a particular MIME type.
     * @param mimeType desired MIME type
     * @return a BitmapEncoder instance (throws an UnsupportedOperationException
     *      if no suitable BitmapEncoder is available)
     */
    public static BitmapEncoder getInstance(String mimeType) {
        return encoders.stream()
            .map((entry) -> entry.encoder)
            .filter((encoder) -> supports(encoder, mimeType))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException("No BitmapEncoder available for " + mimeType));
    }

    /**
     * Returns a Set of Strings with all the supported MIME types from all
     * registered BitmapEncoders.
     * @return a Set of Strings (MIME types)
     */
    public static Set<String> getSupportedMIMETypes() {
        final Set<String> mimes = new HashSet<>();
        for (final Entry entry : encoders) {
            Collections.addAll(mimes, entry.encoder.getSupportedMIMETypes());
        }
        return mimes;
    }

}
