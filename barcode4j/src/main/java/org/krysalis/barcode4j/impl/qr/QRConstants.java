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

/* $Id: QRConstants.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.qr;

/**
 * Constants for QR Code.
 *
 * @version $Id: QRConstants.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $
 */
public interface QRConstants {

    char ERROR_CORRECTION_LEVEL_L = 'L';
    char ERROR_CORRECTION_LEVEL_M = 'M';
    char ERROR_CORRECTION_LEVEL_Q = 'Q';
    char ERROR_CORRECTION_LEVEL_H = 'H';

    int QUIET_ZONE_SIZE = 4;
    int QUIET_ZONE_SIZE_MICRO = 2;

    int NUMERIC = 0; // Numeric encoding (10 bits per 3 digits)
    int ALPHANUMERIC = 1; // Alphanumeric encoding (11 bits per 2 characters)
    int BINARY = 2; // Byte encoding (8 bits per character)

    // int KANJI_KANA_MODE = 3; // todo: Add support for Kanji encoding (13 bits per character)

    /**
     * In Alphanumeric mode, QR Codes support a sub-set of ASCII printable characters.
     * The supported chars are 0–9, A–Z (upper-case only), space, $, %, *, +, -, ., /, :
     */
    int[] ALPHANUMERIC_TABLE = {
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  //0x00-0x0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  //0x10-0x1f
            // ASCII printable characters (character code 32-127)
            32, -1, -1, -1, 36, 37, -1, -1, -1, -1, 42, 43, -1, 45, 46, 47,  //0x20-0x2f
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, -1, -1, -1, -1, -1,  //0x30-0x3f (digits on this row)
            -1, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,  //0x40-0x4f (A-O)
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, -1, -1, -1, -1, -1,  //0x50-0x5f (P-Z)
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  //0x60-0x6f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  //0x70-0x7f
    };

}
