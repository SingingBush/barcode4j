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

/* $Id: MessageUtilTest.java,v 1.1 2008-11-29 16:42:09 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import junit.framework.TestCase;

/**
 * Tests the {@code MessageUtil} class.
 */
public class MessageUtilTest extends TestCase {

    /**
     * Tests unescaping.
     * @throws Exception If an error occurs
     */
    public void testUnescaping() throws Exception {
        String msg = "12345\\u001E00\\\\u001e11\\u0004";
        String processed = MessageUtil.unescapeUnicode(msg);
        assertEquals("12345\u001E00\\u001e11\u0004", processed);

        //Testing an unfinished Unicode escape sequence
        msg = "1\\u00\\x";
        try {
            processed = MessageUtil.unescapeUnicode(msg);
            fail("Expected an IllegalArgumentException for an unfinished Unicode escape sequence");
        } catch (IllegalArgumentException e) {
            //expected
        }

        //A double backslash should generate a single backslash and not trigger the Unicode
        //escaping
        msg = "1\\\\u001E";
        processed = MessageUtil.unescapeUnicode(msg);
        assertEquals("1\\u001E", processed);
    }

}
