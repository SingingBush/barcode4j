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

import junit.framework.TestCase;

/**
 * Tests the class MessagePatternUtil.
 * @version $Id: MessagePatternUtilTest.java,v 1.3 2008-11-29 16:27:25 jmaerki Exp $
 */
public class MessagePatternUtilTest extends TestCase {

    /**
     * Tests the message pattern feature.
     * @throws Exception If an error occurs
     */
    public void testMessagePattern() throws Exception {
        String msg, pattern;
        String result;

        pattern = "__:____/__/__";

        msg = "0120070119";
        result = MessagePatternUtil.applyCustomMessagePattern(msg, pattern);
        assertEquals("01:2007/01/19", result);

        //Test case where the message pattern will be exhausted
        msg = "0120070119abc";
        result = MessagePatternUtil.applyCustomMessagePattern(msg, pattern);
        assertEquals("01:2007/01/19abc", result);

        //Test with no pattern, message should not be changed
        msg = "123";
        result = MessagePatternUtil.applyCustomMessagePattern(msg, null);
        assertEquals("123", result);

        //Test with no pattern, message should not be changed
        msg = "123";
        result = MessagePatternUtil.applyCustomMessagePattern(msg, "");
        assertEquals("123", result);

        //Test with no message, message should not be changed
        result = MessagePatternUtil.applyCustomMessagePattern(null, pattern);
        assertNull(result);

        //Test with no message, message should not be changed
        result = MessagePatternUtil.applyCustomMessagePattern("", pattern);
        assertEquals("", result);

        pattern = "_\\__"; //with escape
        result = MessagePatternUtil.applyCustomMessagePattern("AB", pattern);
        assertEquals("A_B", result);

        pattern = "____>>>>"; //additional chars at the end
        result = MessagePatternUtil.applyCustomMessagePattern("ABCD", pattern);
        assertEquals("ABCD>>>>", result);

        pattern = "____>>>>"; //underfull message
        result = MessagePatternUtil.applyCustomMessagePattern("AB", pattern);
        assertEquals("AB>>>>", result);

        pattern = "____>>>>\\_"; //underfull message with escape
        result = MessagePatternUtil.applyCustomMessagePattern("AB", pattern);
        assertEquals("AB>>>>_", result);
    }

    /**
     * Tests the deletion placeholder (#).
     * @throws Exception if an error occurs
     */
    public void testDeletion() throws Exception {
        String msg;
        String result;

        String pattern = "____#/__#/__";

        msg = "2008-11-28";
        result = MessagePatternUtil.applyCustomMessagePattern(msg, pattern);
        assertEquals("2008/11/28", result);
    }

}
