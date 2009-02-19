/*
 * Copyright 2009 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.ChecksumMode;

/**
 * This class is an implementation of the ITF-14 barcode.
 *
 * @version $Id: ITF14LogicImpl.java,v 1.1 2009-02-19 10:14:54 jmaerki Exp $
 */
public class ITF14LogicImpl extends Interleaved2Of5LogicImpl {

    /**
     * Main constructor.
     * @param mode the checksum mode
     * @param displayChecksum true if the checksum shall be displayed
     */
    public ITF14LogicImpl(ChecksumMode mode, boolean displayChecksum) {
        super(mode, displayChecksum);
    }

    /** {@inheritDoc} */
    protected String handleChecksum(StringBuffer sb) {
        int msgLen = sb.length();
        if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            switch (msgLen) {
            case 13:
                return doHandleChecksum(sb, ChecksumMode.CP_ADD);
            case 14:
                return doHandleChecksum(sb, ChecksumMode.CP_CHECK);
            default:
                throw new IllegalArgumentException(
                        "Message must have a length of exactly 13 or 14 digits. This message has "
                            + msgLen + " characters.");
            }
        } else {
            if (getChecksumMode() == ChecksumMode.CP_ADD) {
                verifyMessageLength(msgLen, 13);
            } else {
                verifyMessageLength(msgLen, 14);
            }
            return super.handleChecksum(sb);
        }
    }

    private void verifyMessageLength(int actualLength, int expectedLength) {
        if (actualLength != expectedLength) {
            throw new IllegalArgumentException(
                    "Message must have a length of exactly " + expectedLength
                    + " digits. This message has "
                        + actualLength + " characters.");
        }
    }

}
