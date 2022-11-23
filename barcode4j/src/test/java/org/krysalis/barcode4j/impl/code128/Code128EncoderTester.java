/*
 * Copyright (C) 2007 by Edmond R&D B.V.
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
package org.krysalis.barcode4j.impl.code128;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the Code128 encoder.
 * @author branko
 */
public class Code128EncoderTester {

    @ParameterizedTest
    @MethodSource("provideCode128EncoderTestData")
    void testEncoding(final String message, final String expected, final String actual) {
        assertEquals(
                expected,
                Code128LogicImpl.toString(new DefaultCode128Encoder().encode(actual)),
                message
        );
    }

    private static Stream<Arguments> provideCode128EncoderTestData() {
        return Stream.of(
                Arguments.of("Minimal codeset C", "StartC|idx10", "10"),
                Arguments.of("Simple codeset C with FNC1", "StartC|FNC1|idx10", "\36110"),
                Arguments.of("Simple codeset C with 2 * FNC1", "StartC|FNC1|FNC1|idx10", "\361\36110"),
                Arguments.of("One digit short for code set C", "StartB|FNC1|idx17", "\3611"),
                Arguments.of("Minimal code set B", "StartB|idx65", "a"),
                Arguments.of("Minimal code set A", "StartA|idx64", "\000"),
                Arguments.of("Long code set B", "StartB|idx17|idx16|idx33|idx33|idx33|idx33", "10AAAA"),
                Arguments.of("Long code set A", "StartA|idx17|idx16|idx33|idx33|idx64", "10AA\000"),
                Arguments.of("Shift to B from code set A", "StartA|idx33|idx64|Shift/98|idx65|idx64", "A\000a\000"),
                Arguments.of("Switch to B from code set A", "StartA|idx65|CodeB/FNC4|idx65|idx65", "\001aa"),
                Arguments.of("Switch to C from code set A", "StartA|idx64|CodeC/99|idx0|idx0", "\0000000"),
                Arguments.of("Shift to A from code set B", "StartB|idx65|Shift/98|idx65|idx65", "a\001a"),
                Arguments.of("Switch to A from code set B", "StartB|idx65|CodeA/FNC4|idx65|idx65", "a\001\001"),
                Arguments.of("Switch to C from code set B", "StartB|idx65|CodeC/99|idx0|idx0", "a0000"),
                Arguments.of("Switch to A from code set C", "StartC|idx0|idx0|CodeA/FNC4|idx64|idx64", "0000\000\000"),
                Arguments.of("Switch to B from code set C", "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65", "0000aa"),
                Arguments.of("All codeset and shifts",
                        "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65|Shift/98|idx64|idx65|CodeA/FNC4|idx64|idx64|Shift/98|idx65|idx64|CodeB/FNC4|idx65|idx65|CodeC/99|idx0|idx0",
                        "0000aa\000a\000\000a\000aa0000")
        );
    }

}
