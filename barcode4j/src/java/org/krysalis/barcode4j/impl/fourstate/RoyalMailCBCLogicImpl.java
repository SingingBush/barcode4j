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
package org.krysalis.barcode4j.impl.fourstate;

import java.util.List;
import java.util.Map;

import org.krysalis.barcode4j.ChecksumMode;

/**
 * Implements the Royal Mail Customer Barcode (CBC).
 * 
 * @author Jeremias Maerki
 * @version $Id: RoyalMailCBCLogicImpl.java,v 1.1 2006-11-07 16:50:36 jmaerki Exp $
 */
public class RoyalMailCBCLogicImpl extends AbstractFourStateLogicImpl {

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
    public RoyalMailCBCLogicImpl(ChecksumMode mode) {
        super(mode);
    }


    /**
     * @see org.krysalis.barcode4j.impl.fourstate.AbstractFourStateLogicImpl#calcChecksum(
     *          java.lang.String)
     */
    public char calcChecksum(String msg) {
        String[] codewords = encodeHighLevel(removeStartStop(msg));
        final int[] multiplier = new int[] {4, 2, 1, 0};
        int upperSum = 0;
        int lowerSum = 0;
        for (int i = 0; i < codewords.length; i++) {
            int upper = 0;
            int lower = 0;
            for (int j = 0; j < 4; j++) {
                int v = codewords[i].charAt(j) - '0';
                upper += (v & 1) * multiplier[j];
                lower += ((v & 2) >> 1) * multiplier[j];
            }
            upperSum += upper % 6;
            lowerSum += lower % 6;
        }
        int row = upperSum % 6;
        if (row == 0) {
            row = 5;
        } else {
            row -= 1;
        }
        int col = lowerSum % 6;
        if (col == 0) {
            col = 5;
        } else {
            col -= 1;
        }
        int idx = row * 6 + col;
        if (idx < 10) {
            return (char)('0' + idx);
        } else {
            return (char)('A' + idx - 10);
        }
    }


    /**
     * Handles the checksum, either checking if the right value was specified or adding the
     * missing checksum depending on the settings.
     * @param msg the message
     * @return the (possibly) modified message
     */
    protected String handleChecksum(String msg) {
        if (getChecksumMode() == ChecksumMode.CP_ADD 
                || getChecksumMode() == ChecksumMode.CP_AUTO) {
            return msg + calcChecksum(msg.toString());
        } else if (getChecksumMode() == ChecksumMode.CP_CHECK) {
            if (!validateChecksum(msg)) {
                throw new IllegalArgumentException("Message '" 
                    + msg
                    + "' has a bad checksum. Expected: " 
                    + calcChecksum(msg.substring(0, msg.length() - 1)));
            }
            return msg;
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return msg;
        } else {
            throw new UnsupportedOperationException(
                    "Unknown checksum mode: " + getChecksumMode());
        }
    }

    /**
     * Removes the start and stop characters from the message.
     * @param msg the message
     * @return the modified message
     */
    public static String removeStartStop(String msg) {
        StringBuffer sb = new StringBuffer(msg.length());
        for (int i = 0, c = msg.length(); i < c; i++) {
            char ch = msg.charAt(i);
            switch (ch) {
            case '(':
            case '[':
            case ')':
            case ']':
                break;
            default:
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.fourstate.AbstractFourStateLogicImpl#normalizeMessage(
     *          java.lang.String)
     */
    public String normalizeMessage(String msg) {
        String s = removeStartStop(msg);
        s = handleChecksum(s);
        return "(" + s + ")";
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.fourstate.AbstractFourStateLogicImpl#encodeHighLevel(
     *          java.lang.String)
     */
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
