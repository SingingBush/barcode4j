/*
 * Copyright 2007-2008 Jeremias Maerki.
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

/* $Id: MessagePatternUtilTest.java,v 1.3 2008-11-29 16:27:25 jmaerki Exp $ */
package org.krysalis.barcode4j.tools;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the class MessagePatternUtil.
 * @version $Id: MessagePatternUtilTest.java,v 1.3 2008-11-29 16:27:25 jmaerki Exp $
 */
public class MessagePatternUtilTest {

    /**
     * Tests the message pattern feature.
     */
    @ParameterizedTest
    @MethodSource("messagePatternArgs")
    void testMessagePattern(final String msg, final String pattern, final String expected) {
        final String result = MessagePatternUtil.applyCustomMessagePattern(msg, pattern);
        assertEquals(expected, result);
    }

    public static Stream<Arguments> messagePatternArgs() {
        return Stream.of(
            Arguments.of("148.99", "$_", "$148.99"),

            // todo: consider supporting unicode. See: https://stackoverflow.com/questions/65897123/trouble-parsing-%e2%82%ac-with-barcode4j-messagepatternutil
            //Arguments.of("148.99", "£_", "£148.99"),
            //Arguments.of("148.99", "€_", "€148.99"),

            Arguments.of("1234567890", "____ ____ __", "1234 5678 90"),
            Arguments.of("444408246137", "(+__)___ _______", "(+44)440 8246137"),
            Arguments.of("01012001103854", "__/__/____ __:__:__ UTC", "01/01/2001 10:38:54 UTC"),
            // Test with pattern ____\\___\____
            Arguments.of("1234567890", "____\\\\___\\____", "1234\\567_890"),
            // Test with deletion of hyphens and addition of forward slash
            Arguments.of("2008-11-28", "____#/__#/__", "2008/11/28"),

            Arguments.of("0120070119", "__:____/__/__", "01:2007/01/19"),
            // Test case where the message pattern will be exhausted:
            Arguments.of("0120070119abc", "__:____/__/__", "01:2007/01/19abc"),
            // Test with null pattern, message should not be changed:
            Arguments.of("123", null, "123"),
            // Test with empty pattern, message should not be changed
            Arguments.of("123", "", "123"),
            // Test with null message, message should not be changed
            Arguments.of(null, "__:____/__/__", null),
            // Test with null message & null pattern
            Arguments.of(null, null, null),
            // Test with empty message, message should not be changed
            Arguments.of("", "__:____/__/__", ""),
            // Test with escape
            Arguments.of("AB", "_\\__", "A_B"),
            // Test with additional chars at the end
            Arguments.of("ABCD", "____>>>>", "ABCD>>>>"),
            // Test with under-full message
            Arguments.of("AB", "____>>>>", "AB>>>>"),
            // Test with under-full message with escape
            Arguments.of("AB", "____>>>>\\_", "AB>>>>_"),
            // Tests the deletion placeholder (#)
            Arguments.of("2008-11-28", "____#/__#/__", "2008/11/28")
        );
    }
}
