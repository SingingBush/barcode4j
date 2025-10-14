/*
 * Copyright 2008 Jeremias Maerki.
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

/* $Id: URLUtil.java,v 1.3 2012-05-17 13:57:37 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Utility functions for handling URLs.
 */
public class URLUtil {

    public static final String URL_START = "url(";
    public static final String URL_END = ")";

    private static final String DATA_PROTOCOL = "data:";

    /**
     * Returns the data pointed at by a URL as a byte array.
     * @param url the URL
     * @param encoding the encoding to use for converting text content to binary content
     * @return the data as a byte array
     * @throws IOException if an I/O error occurs
     */
    public static byte[] getData(String url, String encoding) throws IOException {
        if (url.startsWith(DATA_PROTOCOL)) {
            return parseDataURL(url, encoding);
        } else {
            final URL u = new URL(url);

            try (final InputStream in = u.openStream();
                 final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                IOUtil.copy(in, out);
                return out.toByteArray();
            }
        }
    }

    /**
     * Returns the data pointed at by a URL as a byte array.
     * @param url the URL
     * @return encoding text as a string or null
     */
    @Nullable
    public static String getDataEncoding(@NotNull final String url) {
        int commaPos = url.indexOf(',');
        // header is of the form data:[<mediatype>][;charset=<encoding>][;base64]
        return getEncoding(url.substring(0, commaPos));
    }

    private static byte[] parseDataURL(String url, String encoding) throws IOException {
        int commaPos = url.indexOf(',');
        // header is of the form data:[<mediatype>][;charset=<encoding>][;base64]
        final String header = url.substring(0, commaPos);
        final String data = url.substring(commaPos + 1);

        if (header.endsWith(";base64")) {
            try (final Base64InputStream in = new Base64InputStream(new java.io.StringReader(data));
                 final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                IOUtil.copy(in, out);
                return out.toByteArray();
            }
        } else {
            String urlEncoding = getEncoding(header);
            if (urlEncoding == null) {
                urlEncoding = "US-ASCII";
            }
            final String unescapedString = URLDecoder.decode(data, urlEncoding);
            return unescapedString.getBytes(encoding);
        }
    }

    @Nullable
    private static String getEncoding(@NotNull final String header) {
        String urlEncoding = null;
        final int charsetpos = header.indexOf(";charset=");
        if (charsetpos > 0) {
            urlEncoding = header.substring(charsetpos + 9);
            int pos = urlEncoding.indexOf(';');
            if (pos > 0) {
                urlEncoding = urlEncoding.substring(0, pos);
            }
        }
        return urlEncoding;
    }

    /**
     * @param message String
     * @return String
     */
    public static boolean isURL(@NotNull final String message) {
        return message.startsWith(URLUtil.URL_START) && message.endsWith(URLUtil.URL_END);
    }

    /**
     * @param message String
     * @return String
     */
    @Nullable
    public static String getURL(@NotNull final String message) {
        return URLUtil.isURL(message) ?
            message.substring(URLUtil.URL_START.length(), message.length() - URLUtil.URL_END.length()) :
            null;
    }

}
