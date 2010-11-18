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

import java.util.Map;

/**
 * Holds information on the page a barcode is painted on.
 */
public class PageInfo {

    private int pageNumber;
    private String pageNumberString;

    /**
     * Creates a new object.
     * @param pageNumber the page number
     * @param pageNumberString the string representation of the page number (ex. "12" or "XII")
     */
    public PageInfo(int pageNumber, String pageNumberString) {
        this.pageNumber = pageNumber;
        this.pageNumberString = pageNumberString;
    }

    /**
     * Creates a {@link PageInfo} from a {@link Map} containing processing hints.
     * @param hints the processing hints
     * @return the page info object or null if no such information is available
     */
    public static PageInfo fromProcessingHints(Map hints) {
        if (hints.containsKey("page-number")) {
            int pageNumber = ((Number)hints.get("page-number")).intValue();
            String pageName = (String)hints.get("page-name");
            return new PageInfo(pageNumber, pageName);
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
    public String getPageNumberString() {
        return this.pageNumberString;
    }

}
