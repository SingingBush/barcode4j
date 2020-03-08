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
package org.krysalis.barcode4j.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Helper class to replace certain variables in the barcode message.
 */
public class VariableUtil {

    private static String replace(String text, String repl, String with) {
        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    private static final String PAGE_NUMBER = "#page-number#";
    private static final String PAGE_NUMBER_WITH_FORMAT = "#page-number:";
    private static final String FORMATTED_PAGE_NUMBER = "#formatted-page-number#";

    /**
     * Method to replace page number variables in the message.
     * @param page the page information object
     * @param msg the message
     * @return the message after the variable processing
     */
    public static String getExpandedMessage(PageInfo page, String msg) {
        String s = msg;
        int idx;
        while ((idx = s.indexOf(PAGE_NUMBER_WITH_FORMAT)) >= 0) {
            int endidx = s.indexOf('#', idx + PAGE_NUMBER_WITH_FORMAT.length());
            if (endidx < 0) {
                break;
            }
            String fmt = s.substring(idx + PAGE_NUMBER_WITH_FORMAT.length(), endidx);
            StringBuffer sb = new StringBuffer(s);
            String value;
            if (page != null) {
                NumberFormat nf = new DecimalFormat(fmt);
                value = nf.format(page.getPageNumber());
            } else {
                StringBuffer blanks = new StringBuffer(fmt.length());
                blanks.setLength(fmt.length());
                for (int i = 0; i < blanks.length(); i++) {
                    blanks.setCharAt(i, '0');
                }
                value = blanks.toString();
            }
            sb.replace(idx, endidx + 1, value);
            s = sb.toString();
        }
        if (page != null) {
            s = replace(s, PAGE_NUMBER, Integer.toString(page.getPageNumber()));
            s = replace(s, FORMATTED_PAGE_NUMBER, page.getPageNumberString());
        } else {
            s = replace(s, PAGE_NUMBER, "000");
            s = replace(s, FORMATTED_PAGE_NUMBER, "000");
        }
        return s;
    }

}
