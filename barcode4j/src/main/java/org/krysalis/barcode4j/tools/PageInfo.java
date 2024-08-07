/*
 * Copyright 2010 Jeremias Maerki
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

/* $Id: PageInfo.java,v 1.1 2010-11-18 09:29:20 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Holds information on the page a barcode is painted on.
 */
public class PageInfo {

    // constants key values used as ProcessingHints for PageInfo
    private static final String PAGE_NUMBER = "page-number";
    private static final String PAGE_NAME = "page-name";

    private final int pageNumber;
    private final String pageNumberString;

    /**
     * Creates a new object.
     * @param pageNumber the page number
     * @param pageNumberString the string representation of the page number (ex. "12" or "XII")
     */
    public PageInfo(final int pageNumber, @Nullable final String pageNumberString) {
        this.pageNumber = pageNumber;
        this.pageNumberString = pageNumberString != null ? pageNumberString : String.valueOf(pageNumber);
    }

    /**
     * Creates a {@link PageInfo} from a {@link Map} containing processing hints.
     * @param hints the processing hints
     * @return the page info object or null if no such information is available
     */
    public static PageInfo fromProcessingHints(@Nullable final Map<String,Object> hints) {
        if (hints != null && hints.containsKey(PAGE_NUMBER)) {
            int pageNumber = ((Number)hints.get(PAGE_NUMBER)).intValue();
            @Nullable String pageName = (String)hints.get(PAGE_NAME);
            return new PageInfo(pageNumber, pageName != null ? pageName : String.valueOf(pageNumber));
        }
        return null;
    }

    /**
     * Returns the page number
     * @return the page number
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    /**
     * Returns the string representation of the page number (ex. "12" or "XII").
     * @return the page number as a string
     */
    @NotNull
    public String getPageNumberString() {
        return this.pageNumberString;
    }

}
