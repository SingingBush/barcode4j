/*
 * Copyright 2012 Jeremias Maerki, Switzerland
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

/* $Id: ECIUtil.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utilities around ECI (extended channel interpretation).
 *
 * @version $Id: ECIUtil.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $
 */
public class ECIUtil {

    /** ECI code for 8-bit binary data */
    public static final int ECI_BINARY = 899;

    private static final Map<String, Integer> encodingToECI = new java.util.HashMap<>();

    static {
        mapEncoding("Cp437", 2);        // MS-DOS United States
        mapEncoding("ISO-8859-1", 3);   // StandardCharsets.ISO_8859_1
        mapEncoding("ISO-8859-2", 4);
        mapEncoding("ISO-8859-3", 5);
        mapEncoding("ISO-8859-4", 6);
        mapEncoding("ISO-8859-5", 7);
        mapEncoding("ISO-8859-6", 8);
        mapEncoding("ISO-8859-7", 9);
        mapEncoding("ISO-8859-8", 10);
        mapEncoding("ISO-8859-9", 11);
        mapEncoding("ISO-8859-10", 12);
        mapEncoding("ISO-8859-11", 13);
        mapEncoding("ISO-8859-13", 15);
        mapEncoding("ISO-8859-14", 16);
        mapEncoding("ISO-8859-15", 17);
        mapEncoding("ISO-8859-16", 18);
        mapEncoding("SJIS", 20);        // Shift-JIS, Japanese
        mapEncoding("Cp1250", 21);      // a.k.a: windows-1250
        mapEncoding("Cp1251", 22);      // a.k.a: windows-1251
        mapEncoding("Cp1252", 23);      // a.k.a: windows-1252
        mapEncoding("Cp1256", 24);      // a.k.a: windows-1256
        mapEncoding("UnicodeBigUnmarked", 25); // StandardCharsets.UTF_16BE
        mapEncoding("UTF-8", 26);       // StandardCharsets.UTF_8
        mapEncoding("US-ASCII", 27);    // StandardCharsets.US_ASCII
        mapEncoding("Big5", 28);        // Traditional Chinese
        mapEncoding("GB18030", 29);     // Simplified Chinese, PRC standard
    }

    private static void mapEncoding(final @NotNull String encoding, int eci) {
        encodingToECI.put(encoding, eci);
    }

    /**
     * Returns the ECI code for a given encoding.
     *
     * @param encoding the encoding
     * @return the corresponding ECI code or -1 if not defined
     */
    public static int getECIForEncoding(final @NotNull String encoding) {
        return encodingToECI.getOrDefault(encoding, -1);
    }

    /**
     * Returns the ECI code for a given charset.
     * <p>Typical charsets, available for all JDK's, can be found from {@link StandardCharsets}:</p>
     * <ul>
     *   <li>{@link StandardCharsets#US_ASCII}</li>
     *   <li>{@link StandardCharsets#ISO_8859_1}</li>
     *   <li>{@link StandardCharsets#UTF_8}</li>
     * </ul>
     * <p>
     *     Further charsets may be available for your system and can potentially be used if the barcode supports it.
     *     Consider checking {@link Charset#availableCharsets()} if you need to get an ECI code for a charset that's
     *     not in {@link StandardCharsets}. eg; Japanese <code>Charset.forName("SJIS")</code>
     * </p>
     * @param charset the charset
     * @return the corresponding ECI code or -1 if not defined
     * @since 2.5.0
     */
    public static int getECIForCharset(final @NotNull Charset charset) {
        return encodingToECI.getOrDefault(charset.name(), -1);
    }
}
