/*
 * Copyright 2012 Jeremias Maerki, Switzerland
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

/* $Id: QRHighLevelEncoder.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.qr;

/**
 * High-level encoder for QR Code.
 *
 * @version $Id: QRHighLevelEncoder.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $
 */
/*
* Deprecated : this encoder isn't actually used in QRLogicImpl as Zxing's com.google.zxing.qrcode.encoder.Encoder is
* used instead. I've changed to package private for now just to be sure and will delete if proven to not be of any value.
*/
class QRHighLevelEncoder implements QRConstants {

    private final int encodingMode;

    // todo: should this constructor handle null msg?
    public QRHighLevelEncoder(String msg) {
        this.encodingMode = analyzeMessage(msg);
    }

    public int getEncodingMode() {
        return this.encodingMode;
    }

    private int analyzeMessage(String msg) {
        int mode = NUMERIC;

        for (final char ch : msg.toCharArray()) {
            if (Character.isDigit(ch)) {
                //nop
            } else if (mode == NUMERIC && isSupportedAlphanumeric(ch)) {
                mode = ALPHANUMERIC;
            } else if(!isSupportedAlphanumeric(ch)) {
                mode = BINARY;
                break;
            }
        }

        return mode;
    }

    /**
     * In Alphanumeric mode, QR Codes support a sub-set of ASCII printable characters (character codes 32-127).
     * The supported chars are 0–9, A–Z (upper-case only), space, $, %, *, +, -, ., /, :
     * @param ch a char to check against the supported character list
     * @return true if the char is supported by QR code alphanumeric mode
     */
    private boolean isSupportedAlphanumeric(char ch) {
        return (int)ch < ALPHANUMERIC_TABLE.length && ALPHANUMERIC_TABLE[ch] >= 0;
    }

}
