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

import org.krysalis.barcode4j.ChecksumMode;

/**
 * Implements the Royal Mail Customer Barcode (CBC).
 * 
 * @version $Id: RoyalMailCBCLogicImpl.java,v 1.2 2008-05-13 13:00:43 jmaerki Exp $
 */
public class RoyalMailCBCLogicImpl extends AbstractRMCBCKIXLogicImpl {

    /**
     * Main constructor
     * @param mode checksum mode
     */
    public RoyalMailCBCLogicImpl(ChecksumMode mode) {
        super(mode);
    }

    /** {@inheritDoc} */
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
            return msg + calcChecksum(msg);
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
    
    /** {@inheritDoc} */
    public String normalizeMessage(String msg) {
        String s = removeStartStop(msg);
        s = handleChecksum(s);
        return "(" + s + ")";
    }
    
}
