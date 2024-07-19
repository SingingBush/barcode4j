/*
 * Copyright 2008 Jeremias Maerki
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

/* $Id: MessageUtil.java,v 1.2 2009-02-20 13:07:21 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for pre-processing messages.
 */
public class MessageUtil {

    /**
     * Un-escapes escaped Unicode characters in a message. This is used to support characters that
     * cannot be encoded in XML, such as the RS or GS characters.
     * @param message the message to be un-escaped
     * @return the processed message or null if the message arg was null
     */
    @Nullable
    public static String unescapeUnicode(@Nullable final String message) {
        if (message == null) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        final StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if (inUnicode) {
                unicode.append(ch);
                if (unicode.length() == 4) {
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        sb.append((char)value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new java.lang.IllegalArgumentException("Unable to parse Unicode value: " + unicode);
                    }
                }
                continue;
            }
            if (hadSlash) {
                hadSlash = false;
                if (ch == 'u') {
                    inUnicode = true;
                } else {
                    sb.append(ch);
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Filters non-printable ASCII characters (0-31 and 127) from a string with spaces and
     * returns that. Please note that non-printable characters outside the ASCII character
     * set are not touched by this method.
     * @param text the text to be filtered.
     * @return the filtered text
     */
    @NotNull
    public static String filterNonPrintableCharacters(@NotNull final String text) {
        final int len = text.length();
        final StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            final char ch = text.charAt(i);
            if (ch < 32 || ch == 127) {
                sb.append(' '); //Replace non-printables with a space
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

}
