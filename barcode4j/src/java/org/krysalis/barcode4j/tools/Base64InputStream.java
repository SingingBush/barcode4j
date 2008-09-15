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

/* $Id: Base64InputStream.java,v 1.1 2008-09-15 07:10:28 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;

/**
 * Base64-implementation as an {@code InputStream} reading Base64-encoded data
 * from a {@code Reader}.
 */
public class Base64InputStream extends InputStream {

    private static final int EOF = -1;

    private static final byte[] LOOKUP = new byte[128];
    static {
        Arrays.fill(LOOKUP, (byte)-1);
        int idx = 0;
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            LOOKUP[ch] = (byte)idx++;
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            LOOKUP[ch] = (byte)idx++;
        }
        for (char ch = '0'; ch <= '9'; ch++) {
            LOOKUP[ch] = (byte)idx++;
        }
        LOOKUP['-'] = (byte)idx; //URL- & filename-safe
        LOOKUP['+'] = (byte)idx++;

        LOOKUP['_'] = (byte)idx; //URL- & filename-safe
        LOOKUP['/'] = (byte)idx++;
    }

    private Reader source;

    private char[] quadBuffer = new char[4];

    private byte[] triple = new byte[3];
    private int tripleIndex = 4;
    private int tripleFilled;

    /**
     * Constructs a new instance.
     * @param source the Reader to read the Base64-encoded data from
     */
    public Base64InputStream(Reader source) {
        if (source == null) {
            throw new NullPointerException("source must not be null");
        }
        this.source = source;
    }

    /** {@inheritDoc} */
    public int read() throws IOException {
        checkOpen();
        if (tripleIndex >= tripleFilled) {
            if (!readNextTriple()) {
                return EOF;
            }
        }
        return triple[tripleIndex++];
    }

    private boolean readNextTriple() throws IOException {
        int offset = 0;
        while (offset < 4) {
            int ch = source.read();
            if (ch < 0) {
                return false;
            } else if (ch == '\r' || ch == '\n' || ch == ' ') {
                continue;
            }
             quadBuffer[offset++] = (char)ch;
        }
        int quad = 0;
        tripleFilled = 3;
        for (int i = 0; i < 4; i++) {
            byte b = -1;
            char ch = quadBuffer[i];
            if ('=' == ch) {
                if (i < 2) {
                    throw new IOException("Padding character at invalid position");
                } else {
                    tripleFilled = Math.min(i - 1, tripleFilled);
                    break;
                }
            }
            if (ch < 128) {
                b = LOOKUP[ch];
            }
            if (b < 0) {
                throw new IOException("Illegal Base64 character encountered: " + ch);
            }
            quad |= b << ((3 - i) * 6);
        }
        triple[0] = (byte)((quad & 0xFF0000) >> 16);
        triple[1] = (byte)((quad & 0xFF00) >> 8);
        triple[2] = (byte)(quad & 0xFF);
        tripleIndex = 0;
        return true;
    }

    private void checkOpen() throws IOException {
        if (this.source == null) {
            throw new IOException("Stream is already closed");
        }
    }

    /** {@inheritDoc} */
    public void close() throws IOException {
        this.source.close();
        this.source = null;
    }

}
