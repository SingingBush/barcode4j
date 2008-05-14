/*
 * Copyright 2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code39;

import junit.framework.TestCase;

/**
 * Tests for the extended character set for Code 39.
 */
public class Code39ExtendedTest extends TestCase {

    public void testEscaping() throws Exception {
        String msg = "\0\1\2\3";
        assertEquals("%U$A$B$C", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "\u0018\u0019\u001A\u001B";
        assertEquals("$X$Y$Z%A", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "\u001C\u001D\u001E\u001F\u0020";
        assertEquals("%B%C%D%E ", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "!\"#$%&";
        assertEquals("/A/B/C/D/E/F", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "/0123";
        assertEquals("/O0123", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "9:;<";
        assertEquals("9/Z%F%G", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "?@ABC";
        assertEquals("%J%VABC", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "XYZ[\\]";
        assertEquals("XYZ%K%L%M", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "^_`abc";
        assertEquals("%N%O%W+A+B+C", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "xyz{|}~\u007F";
        assertEquals("+X+Y+Z%P%Q%R%S%T", Code39LogicImpl.escapeExtended(msg, null).toString());
        msg = "Aaä";
        try {
            Code39LogicImpl.escapeExtended(msg, null);
            fail("Expected an IllegalArgumentException for unsupported character");
        } catch (IllegalArgumentException iae) {
            //expected
        }
    }
    
}
