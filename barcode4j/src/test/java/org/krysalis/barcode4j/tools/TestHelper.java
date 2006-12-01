/*
 * Copyright 2006 Jeremias Maerki.
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

/* $Id: TestHelper.java,v 1.2 2006-12-01 13:28:40 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import java.util.StringTokenizer;

/**
 * Helper methods for testing.
 * 
 * @version $Id: TestHelper.java,v 1.2 2006-12-01 13:28:40 jmaerki Exp $
 */
public class TestHelper {

    /**
     * Convert a string of char codewords into a different string which lists each character 
     * using its decimal value.
     * @param codewords the codewords 
     * @return the visualized codewords
     */
    public static String visualize(String codewords) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < codewords.length(); i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append((int)codewords.charAt(i));
        }
        return sb.toString();
    }
    
    public static String unvisualize(String visualized) {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(visualized, " ");
        while (st.hasMoreTokens()) {
            sb.append((char)Integer.parseInt(st.nextToken()));
        }
        return sb.toString();
    }
    
}
