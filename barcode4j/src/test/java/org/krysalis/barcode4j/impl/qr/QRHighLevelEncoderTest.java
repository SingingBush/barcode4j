/*
 * Copyright 2024 Samael Bate.
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
package org.krysalis.barcode4j.impl.qr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Samael Bate (singingbush)
 * created on 03/03/2024
 */
class QRHighLevelEncoderTest {

    @Test
    void getEncodingModeDefault() {
        final QRHighLevelEncoder encoder = new QRHighLevelEncoder("");

        assertEquals(QRHighLevelEncoder.NUMERIC, encoder.getEncodingMode());
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "456" , "789" , "111" , "123456789" })
    void getEncodingModeNumeric(final String msg) {
        final QRHighLevelEncoder encoder = new QRHighLevelEncoder(msg);

        assertEquals(QRHighLevelEncoder.NUMERIC, encoder.getEncodingMode());
    }

    @ParameterizedTest
    @ValueSource(strings = { "SPACES ARE FINE", "ABCDEFG", "ABC123", "$%*+-./:" })
    void getEncodingModeAlphaNumeric(final String msg) {
        final QRHighLevelEncoder encoder = new QRHighLevelEncoder(msg);

        assertEquals(QRHighLevelEncoder.ALPHANUMERIC, encoder.getEncodingMode());
    }

    @ParameterizedTest
    @ValueSource(strings = { "{}", "<>", "lowercase text", "2.0 = 2 x 6", "2.0 = 2x6" })
    void getEncodingModeBinary(final String msg) {
        final QRHighLevelEncoder encoder = new QRHighLevelEncoder(msg);

        assertEquals(QRHighLevelEncoder.BINARY, encoder.getEncodingMode());
    }
}
