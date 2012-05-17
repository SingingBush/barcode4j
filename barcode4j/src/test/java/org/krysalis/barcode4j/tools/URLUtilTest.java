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

/* $Id: URLUtilTest.java,v 1.1 2012-05-17 13:57:37 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import junit.framework.TestCase;

/**
 * Tests {@link URLUtil}.
 */
public class URLUtilTest extends TestCase {

    public void testIsURL() throws Exception {
        assertFalse(URLUtil.isURL("some message"));
        assertTrue(URLUtil.isURL("url(http://localhost/test.txt)"));
        assertFalse(URLUtil.isURL("url(http://localhost/test.txt"));
        assertFalse(URLUtil.isURL("(http://localhost/test.txt)"));
    }

    public void testGetURL() throws Exception {
        assertEquals("http://localhost/test.txt", URLUtil.getURL("url(http://localhost/test.txt)"));
        assertNull(URLUtil.getURL("some message"));
    }

    public void testGetData() throws Exception {
        byte[] data = URLUtil.getData("data:;base64,flRlc3R+", "US-ASCII");
        String text = new String(data, "US-ASCII");
        assertEquals("~Test~", text);

        data = URLUtil.getData("data:text/plain;charset=iso-8859-1,%7ETest%7E%E5", "ISO-8859-1");
        text = new String(data, "ISO-8859-1");
        assertEquals("~Test~\u00E5", text);
    }

    public void testGetDataEncoding() throws Exception {
        String encoding;
        encoding = URLUtil.getDataEncoding("data:;base64,flRlc3R+");
        assertNull(encoding);

        encoding = URLUtil.getDataEncoding("data:;charset=ISO-8859-1;base64,flRlc3R+");
        assertEquals("ISO-8859-1", encoding);
    }

}
