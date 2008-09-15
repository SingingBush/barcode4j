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
package org.krysalis.barcode4j.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Test case for the Base64 decoder.
 */
public class Base64InputStreamTest extends TestCase {

    private static final boolean DEBUG = false;

    private static final int MODE_BUF = 0;
    private static final int MODE_BYTE = 1;

    /**
     * Tests the Base64 decoder.
     * @throws Exception if an error occurs
     */
    public void testDecoder() throws Exception {
        for (int mode = MODE_BUF; mode <= MODE_BYTE; mode++) {
            assertEquals("sure.", decode("c3VyZS4=", mode));
            assertEquals("asure.", decode("YXN1cmUu", mode));
            assertEquals("easure.", decode("ZWFzdXJlLg==", mode));
            assertEquals("leasure.", decode("bGVhc3VyZS4=", mode));
        }

        //Checks spaces and CR/LF combinations
        assertEquals("leasure.", decode("bGVh c3Vy Z\r\nS\n4="));

        //Alternative URL-safe encoding
        assertEquals("~Test~", decode("flRlc3R+"));
        assertEquals("~Test~", decode("flRlc3R-"));

        assertEquals("", decode(""));
    }

    /**
     * Tests invalid Base64 strings.
     * @throws Exception if an error occurs
     */
    public void testInvalid() throws Exception {
        //Incomplete quad at the end, only three characters expected
        assertEquals("sur", decode("c3VyZS4"));

        try {
            decode("c3%20VyZS4");
            fail("Expected IOException");
        } catch (IOException ioe) {
            //expected
        }

        try {
            decode("=c3VyZS4=");
            fail("Expected IOException");
        } catch (IOException ioe) {
            //expected
        }

        try {
            new Base64InputStream(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException npe) {
            //expected
        }

    }

    public void testCloseBehaviour() throws Exception {
        Base64InputStream in = new Base64InputStream(new StringReader("c3VyZS4="));
        assertEquals('s', in.read());
        assertEquals('u', in.read());
        in.close();
        try {
            in.read();
            fail("Expected IOException");
        } catch (IOException ioe) {
            //expected
        }
    }

    private String decode(String encoded) throws IOException {
        return decode(encoded, MODE_BUF);
    }

    private String decode(String encoded, int mode) throws IOException {
        if (DEBUG) {
            System.out.println("encoded: " + encoded);
        }
        Base64InputStream in = new Base64InputStream(new StringReader(encoded));
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        switch (mode) {
        case MODE_BUF:
            IOUtil.copy(in, baout);
            in.close();
            break;
        case MODE_BYTE:
            int b;
            while (-1 != (b = in.read())) {
                baout.write(b);
            }
            in.close();
            break;
        default:
            throw new UnsupportedOperationException("not supported");
        }
        String decoded =  baout.toString("ISO-8859-1");
        if (DEBUG) {
            System.out.println("decoded: " + decoded);
        }
        return decoded;
    }

}
