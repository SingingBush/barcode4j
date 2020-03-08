/*
 * Copyright 2004,2006,2010 Jeremias Maerki.
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
package org.krysalis.barcode4j.fop;

import org.krysalis.barcode4j.tools.PageInfo;

import org.apache.fop.area.PageViewport;

/**
 * Helper class to replace certain variables in the barcode message.
 * @author Jeremias Maerki
 */
public class VariableUtil extends org.krysalis.barcode4j.tools.VariableUtil {

    /**
     * Legacy method to replace page number variables in the message.
     * @param page the FOP page
     * @param msg the message
     * @return the message after the variable processing
     */
    public static String getExpandedMessage(PageViewport page, String msg) {
        PageInfo pageInfo = null;
        if (page != null) {
            pageInfo = new PageInfo(page.getPageNumber(), page.getPageNumberString());
        }
        return getExpandedMessage(pageInfo, msg);
    }

}
