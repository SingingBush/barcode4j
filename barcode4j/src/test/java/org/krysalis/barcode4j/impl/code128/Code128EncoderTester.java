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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for the Code128 encoder.
 * @author branko
 */
public class Code128EncoderTester extends TestCase {

    /**
     * Create suite with testcases
     * @return Test
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(new Code128EncoderTester("Minimal codeset C",
                "StartC|idx10", "10"));
        suite.addTest(new Code128EncoderTester("Simple codeset C with FNC1",
                "StartC|FNC1|idx10", "\36110"));
        suite.addTest(new Code128EncoderTester(
                "Simple codeset C with 2 * FNC1", "StartC|FNC1|FNC1|idx10",
                "\361\36110"));
        suite.addTest(new Code128EncoderTester(
                "One digit short for code set C", "StartB|FNC1|idx17", "\3611"));
        suite.addTest(new Code128EncoderTester("Minimal code set B",
                "StartB|idx65", "a"));
        suite.addTest(new Code128EncoderTester("Minimal code set A",
                "StartA|idx64", "\000"));
        suite.addTest(new Code128EncoderTester("Long code set B",
                "StartB|idx17|idx16|idx33|idx33|idx33|idx33", "10AAAA"));
        suite.addTest(new Code128EncoderTester("Long code set A",
                "StartA|idx17|idx16|idx33|idx33|idx64", "10AA\000"));
        suite.addTest(new Code128EncoderTester("Shift to B from code set A",
                "StartA|idx33|idx64|Shift/98|idx65|idx64", "A\000a\000"));
        suite.addTest(new Code128EncoderTester("Switch to B from code set A",
                "StartA|idx65|CodeB/FNC4|idx65|idx65", "\001aa"));
        suite.addTest(new Code128EncoderTester("Switch to C from code set A",
                "StartA|idx64|CodeC/99|idx0|idx0", "\0000000"));
        suite.addTest(new Code128EncoderTester("Shift to A from code set B",
                "StartB|idx65|Shift/98|idx65|idx65", "a\001a"));
        suite.addTest(new Code128EncoderTester("Switch to A from code set B",
                "StartB|idx65|CodeA/FNC4|idx65|idx65", "a\001\001"));
        suite.addTest(new Code128EncoderTester("Switch to C from code set B",
                "StartB|idx65|CodeC/99|idx0|idx0", "a0000"));
        suite.addTest(new Code128EncoderTester("Switch to A from code set C",
                "StartC|idx0|idx0|CodeA/FNC4|idx64|idx64", "0000\000\000"));
        suite.addTest(new Code128EncoderTester("Switch to B from code set C",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65", "0000aa"));
        suite.addTest(new Code128EncoderTester(
                "All codeset and shifts",
                "StartC|idx0|idx0|CodeB/FNC4|idx65|idx65|Shift/98|idx64|idx65|CodeA/FNC4|idx64|idx64|Shift/98|idx65|idx64|CodeB/FNC4|idx65|idx65|CodeC/99|idx0|idx0",
                "0000aa\000a\000\000a\000aa0000"));

        return suite;

    }

    private final String message;
    private final String expected;
    private final String actual;

    /**
     * Create new Code128EncoderTester
     * @param message
     * @param expected
     * @param actual
     */
    public Code128EncoderTester(String message, String expected, String actual) {
        super("testEncoding");
        this.message = message;
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * 
     */
    final public void testEncoding() {
        assertEquals(
                message,
                expected,
                Code128LogicImpl.toString(new DefaultCode128Encoder().encode(actual)));
    }

}
