/*
 * Copyright 2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.fop0205;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.fop.layout.Page;

/**
 * @author Jeremias Maerki
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
    
    public static String getExpandedMessage(Page page, String msg) {
        String s = msg;
        int idx;
        while ((idx = s.indexOf(PAGE_NUMBER_WITH_FORMAT)) >= 0) {
            int endidx = s.indexOf('#', idx + PAGE_NUMBER_WITH_FORMAT.length());
            if (endidx < 0) {
                break;
            }
            String fmt = s.substring(idx + PAGE_NUMBER_WITH_FORMAT.length(), endidx);
            NumberFormat nf = new DecimalFormat(fmt);
            StringBuffer sb = new StringBuffer(s);
            sb.replace(idx, endidx + 1, nf.format(page.getNumber()));
            s = sb.toString();
        }
        s = replace(s, PAGE_NUMBER, Integer.toString(page.getNumber()));
        s = replace(s, FORMATTED_PAGE_NUMBER, page.getFormattedNumber());
        return s;
    }
    
}
