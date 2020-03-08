/*
 * Copyright 2006-2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.fourstate;

import java.util.List;
import java.util.Map;

import org.krysalis.barcode4j.ChecksumMode;

/**
 * Abstract base class for Royal Mail Customer Barcode and the Dutch KIX Code.
 * 
 * @version $Id: AbstractRMCBCKIXLogicImpl.java,v 1.1 2008-05-13 13:00:43 jmaerki Exp $
 */
public abstract class AbstractRMCBCKIXLogicImpl extends AbstractFourStateLogicImpl {

    private static final Map CHARSET = new java.util.HashMap();
    
    static {
        //0 = track only, 1 = ascender, 2 = descender, 3 = 1 + 2 = full height
        CHARSET.put("(", "1");
        CHARSET.put("[", "1");
        CHARSET.put(")", "3");
        CHARSET.put("]", "3");
        CHARSET.put("0", "0033");
        CHARSET.put("1", "0213");
        CHARSET.put("2", "0231");
        CHARSET.put("3", "2013");
        CHARSET.put("4", "2031");
        CHARSET.put("5", "2211");
        CHARSET.put("6", "0123");
        CHARSET.put("7", "0303");
        CHARSET.put("8", "0321");
        CHARSET.put("9", "2103");
        CHARSET.put("A", "2121");
        CHARSET.put("B", "2301");
        CHARSET.put("C", "0132");
        CHARSET.put("D", "0312");
        CHARSET.put("E", "0330");
        CHARSET.put("F", "2112");
        CHARSET.put("G", "2130");
        CHARSET.put("H", "2310");
        CHARSET.put("I", "1023");
        CHARSET.put("J", "1203");
        CHARSET.put("K", "1221");
        CHARSET.put("L", "3003");
        CHARSET.put("M", "3021");
        CHARSET.put("N", "3201");
        CHARSET.put("O", "1032");
        CHARSET.put("P", "1212");
        CHARSET.put("Q", "1230");
        CHARSET.put("R", "3012");
        CHARSET.put("S", "3030");
        CHARSET.put("T", "3210");
        CHARSET.put("U", "1122");
        CHARSET.put("V", "1302");
        CHARSET.put("W", "1320");
        CHARSET.put("X", "3102");
        CHARSET.put("Y", "3120");
        CHARSET.put("Z", "3300");
    }
    

    /**
     * Main constructor
     * @param mode checksum mode
     */
    public AbstractRMCBCKIXLogicImpl(ChecksumMode mode) {
        super(mode);
    }

    /** {@inheritDoc} */
    protected String[] encodeHighLevel(String msg) {
        List codewords = new java.util.ArrayList(msg.length());
        for (int i = 0, c = msg.length(); i < c; i++) {
            String ch = msg.substring(i, i + 1);
            String code = (String)CHARSET.get(ch);
            if (code == null) {
                throw new IllegalArgumentException("Illegal character: " + ch);
            }
            codewords.add(code);
        }
        return (String[])codewords.toArray(new String[codewords.size()]);
    }



}
