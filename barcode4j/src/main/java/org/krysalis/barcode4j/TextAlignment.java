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
package org.krysalis.barcode4j;

/**
 * Enumeration for horizontal alignment of the human-readable part of a barcode.
 *
 * @author Jeremias Maerki
 * @version $Id: TextAlignment.java,v 1.1 2008-05-13 13:00:45 jmaerki Exp $
 */
public enum TextAlignment {

    /** The human-readable part is left-aligned. */
    TA_LEFT("left"),
    /** The human-readable part is centered. */
    TA_CENTER("center"),
    /** The human-readable part is right-aligned. */
    TA_RIGHT("right"),
    /** The human-readable part is justified. */
    TA_JUSTIFY("justify");

    private final String name;

    TextAlignment(String name) {;
        this.name = name;
    }

    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a TextAlignment instance by name.
     * @param name the name of the instance
     * @return the requested instance
     * @deprecated not needed, just use the enum values
     */
    @Deprecated
    public static TextAlignment byName(final String name) {
        if (name.equalsIgnoreCase(TextAlignment.TA_LEFT.getName())) {
            return TextAlignment.TA_LEFT;
        } else if (name.equalsIgnoreCase(TextAlignment.TA_CENTER.getName())) {
            return TextAlignment.TA_CENTER;
        } else if (name.equalsIgnoreCase(TextAlignment.TA_RIGHT.getName())) {
            return TextAlignment.TA_RIGHT;
        } else if (name.equalsIgnoreCase(TextAlignment.TA_JUSTIFY.getName())) {
            return TextAlignment.TA_JUSTIFY;
        } else {
            throw new IllegalArgumentException("Invalid TextAlignment: " + name);
        }
    }

}
