/*
 * Copyright 2006-2007 Jeremias Maerki.
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

/* $Id: DataMatrixHighLevelEncoder.java,v 1.18 2011-10-15 13:37:18 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.krysalis.barcode4j.tools.URLUtil;

/**
 * DataMatrix ECC 200 data encoder following the algorithm described in ISO/IEC 16022:200(E) in
 * annex S.
 *
 * @version $Id: DataMatrixHighLevelEncoder.java,v 1.18 2011-10-15 13:37:18 jmaerki Exp $
 */
public class DataMatrixHighLevelEncoder implements DataMatrixConstants {

    private static final boolean DEBUG = false;

    private static final int ASCII_ENCODATION = 0;
    private static final int C40_ENCODATION = 1;
    private static final int TEXT_ENCODATION = 2;
    private static final int X12_ENCODATION = 3;
    private static final int EDIFACT_ENCODATION = 4;
    private static final int BASE256_ENCODATION = 5;

    private static final String[] ENCODATION_NAMES
        = new String[] {"ASCII", "C40", "Text", "ANSI X12", "EDIFACT", "Base 256"};

    private static final String DEFAULT_ASCII_ENCODING = "ISO-8859-1";

    /**
     * Converts the message to a byte array using the default encoding (cp437) as defined by the
     * specification
     * @param msg the message
     * @return the byte array of the message
     */
    public static byte[] getBytesForMessage(String msg) {
        final String charset = "cp437"; //See 4.4.3 and annex B of ISO/IEC 15438:2001(E)
        try {
            return msg.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(
                    "Incompatible JVM! The '" + charset + "' charset is not available!");
        }
    }

    private static char randomize253State(char ch, int codewordPosition) {
        int pseudoRandom = ((149 * codewordPosition) % 253) + 1;
        int tempVariable = ch + pseudoRandom;
        if (tempVariable <= 254) {
            return (char)tempVariable;
        } else {
            return (char)(tempVariable - 254);
        }
    }

    private static char randomize255State(char ch, int codewordPosition) {
        int pseudoRandom = ((149 * codewordPosition) % 255) + 1;
        int tempVariable = ch + pseudoRandom;
        if (tempVariable <= 255) {
            return (char)tempVariable;
        } else {
            return (char)(tempVariable - 256);
        }
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm described in annex P
     * of ISO/IEC 16022:2000(E).
     * @param msg the message
     * @return the encoded message (the char values range from 0 to 255)
     * @throws IOException if an I/O error occurs while fetching external data
     */
    public static String encodeHighLevel(String msg) throws IOException {
        return encodeHighLevel(msg, SymbolShapeHint.FORCE_NONE, null, null);
    }

    /**
     * Performs message encoding of a DataMatrix message using the algorithm described in annex P
     * of ISO/IEC 16022:2000(E).
     * @param msg the message
     * @param shape requested shape. May be <code>SymbolShapeHint.FORCE_NONE</code>,
     * <code>SymbolShapeHint.FORCE_SQUARE</code> or <code>SymbolShapeHint.FORCE_RECTANGLE</code>.
     * @param minSize the minimum symbol size constraint or null for no constraint
     * @param maxSize the maximum symbol size constraint or null for no constraint
     * @return the encoded message (the char values range from 0 to 255)
     * @throws IOException if an I/O error occurs while fetching external data
     */
    public static String encodeHighLevel(String msg,
            SymbolShapeHint shape, Dimension minSize, Dimension maxSize) throws IOException {
        //the codewords 0..255 are encoded as Unicode characters
        Encoder[] encoders = new Encoder[] {new ASCIIEncoder(),
                new C40Encoder(), new TextEncoder(), new X12Encoder(), new EdifactEncoder(),
                new Base256Encoder()};

        int encodingMode = ASCII_ENCODATION; //Default mode
        EncoderContext context = createEncoderContext(msg);
        context.setSymbolShape(shape);
        context.setSizeConstraints(minSize, maxSize);

        if (msg.startsWith(MACRO_05_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_05);
            context.setSkipAtEnd(2);
            context.pos += MACRO_05_HEADER.length();
        } else if (msg.startsWith(MACRO_06_HEADER) && msg.endsWith(MACRO_TRAILER)) {
            context.writeCodeword(MACRO_06);
            context.setSkipAtEnd(2);
            context.pos += MACRO_06_HEADER.length();
        }

        while (context.hasMoreCharacters()) {
            encoders[encodingMode].encode(context);
            if (context.newEncoding >= 0) {
                encodingMode = context.newEncoding;
                context.resetEncoderSignal();
            }
        }
        int len = context.codewords.length();
        context.updateSymbolInfo();
        int capacity = context.symbolInfo.dataCapacity;
        if (len < capacity) {
            if (encodingMode != ASCII_ENCODATION && encodingMode != BASE256_ENCODATION) {
                if (DEBUG) {
                    System.out.println("Unlatch because symbol isn't filled up");
                }
                context.writeCodeword('\u00fe'); //Unlatch (254)
            }
        }
        //Padding
        StringBuffer codewords = context.codewords;
        if (codewords.length() < capacity) {
            codewords.append(DataMatrixConstants.PAD);
        }
        while (codewords.length() < capacity) {
            codewords.append(randomize253State(DataMatrixConstants.PAD, codewords.length() + 1));
        }

        return context.codewords.toString();
    }

    private static EncoderContext createEncoderContext(String msg) throws IOException {
        String url = URLUtil.getURL(msg);
        if (url != null) {
            //URL processing
            byte[] data = URLUtil.getData(url, DEFAULT_ASCII_ENCODING);
            return new EncoderContext(data);
        } else {
            return new EncoderContext(msg);
        }
    }

    private static class EncoderContext {

        private String msg;
        private SymbolShapeHint shape = SymbolShapeHint.FORCE_NONE;
        private Dimension minSize;
        private Dimension maxSize;
        private StringBuffer codewords;
        private int pos = 0;
        private int newEncoding = -1;
        private DataMatrixSymbolInfo symbolInfo;
        private int skipAtEnd = 0;

        public EncoderContext(String msg) {
            //From this point on Strings are not Unicode anymore!
            byte[] msgBinary;
            try {
                msgBinary = msg.getBytes(DEFAULT_ASCII_ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new UnsupportedOperationException("Unsupported encoding: " + e.getMessage());
            }
            StringBuffer sb = new StringBuffer(msgBinary.length);
            for (int i = 0, c = msgBinary.length; i < c; i++) {
                char ch = (char)(msgBinary[i] & 0xff);
                if (ch == '?' && msg.charAt(i) != '?') {
                    throw new IllegalArgumentException("Message contains characters outside "
                            + DEFAULT_ASCII_ENCODING + " encoding.");
                }
                sb.append(ch);
            }
            this.msg = sb.toString(); //Not Unicode here!
            this.codewords = new StringBuffer(msg.length());
        }

        public EncoderContext(byte[] data) {
            //From this point on Strings are not Unicode anymore!
            StringBuffer sb = new StringBuffer(data.length);
            for (int i = 0, c = data.length; i < c; i++) {
                char ch = (char)(data[i] & 0xff);
                sb.append(ch);
            }
            this.msg = sb.toString(); //Not Unicode here!
            this.codewords = new StringBuffer(msg.length());
        }

        public void setSymbolShape(SymbolShapeHint shape) {
            this.shape = shape;
        }

        public void setSizeConstraints(Dimension minSize, Dimension maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public String getMessage() {
            return this.msg;
        }

        public void setSkipAtEnd(int count) {
            this.skipAtEnd = count;
        }

        public char getCurrentChar() {
            return msg.charAt(pos);
        }

        public char getCurrent() {
            return msg.charAt(pos);
        }

        public void writeCodewords(String codewords) {
            this.codewords.append(codewords);
        }

        public void writeCodeword(char codeword) {
            this.codewords.append(codeword);
        }

        public int getCodewordCount() {
            return this.codewords.length();
        }

        public void signalEncoderChange(int encoding) {
            this.newEncoding = encoding;
        }

        public void resetEncoderSignal() {
            this.newEncoding = -1;
        }

        public boolean hasMoreCharacters() {
            return pos < getTotalMessageCharCount();
        }

        private int getTotalMessageCharCount() {
            return msg.length() - skipAtEnd;
        }

        public int getRemainingCharacters() {
            return getTotalMessageCharCount() - pos;
        }

        public void updateSymbolInfo() {
            updateSymbolInfo(getCodewordCount());
        }

        public void updateSymbolInfo(int len) {
            if (this.symbolInfo == null || len > this.symbolInfo.dataCapacity) {
                this.symbolInfo = DataMatrixSymbolInfo.lookup(len, shape, minSize, maxSize, true);
            }
        }

        public void resetSymbolInfo() {
            this.symbolInfo = null;
        }
    }

    private interface Encoder {
        int getEncodingMode();
        void encode(EncoderContext context);
    }

    private static class ASCIIEncoder implements Encoder {

        public int getEncodingMode() {
            return ASCII_ENCODATION;
        }

        public void encode(EncoderContext context) {
            //step B
            int n = determineConsecutiveDigitCount(context.msg, context.pos);
            if (n >= 2) {
                context.writeCodeword(encodeASCIIDigits(context.msg.charAt(context.pos),
                        context.msg.charAt(context.pos + 1)));
                context.pos += 2;
            } else {
                char c = context.getCurrentChar();
                int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                if (newMode != getEncodingMode()) {
                    switch (newMode) {
                    case BASE256_ENCODATION:
                        context.writeCodeword(LATCH_TO_BASE256);
                        context.signalEncoderChange(BASE256_ENCODATION);
                        return;
                    case C40_ENCODATION:
                        context.writeCodeword(LATCH_TO_C40);
                        context.signalEncoderChange(C40_ENCODATION);
                        return;
                    case X12_ENCODATION:
                        context.writeCodeword(LATCH_TO_ANSIX12);
                        context.signalEncoderChange(X12_ENCODATION);
                        break;
                    case TEXT_ENCODATION:
                        context.writeCodeword(LATCH_TO_TEXT);
                        context.signalEncoderChange(TEXT_ENCODATION);
                        break;
                    case EDIFACT_ENCODATION:
                        context.writeCodeword(LATCH_TO_EDIFACT);
                        context.signalEncoderChange(EDIFACT_ENCODATION);
                        break;
                    default:
                        throw new IllegalStateException("Illegal mode: " + newMode);
                    }
                } else if (isExtendedASCII(c)) {
                    context.writeCodeword(UPPER_SHIFT);
                    context.writeCodeword((char)(c - 128 + 1));
                    context.pos++;
                } else {
                    if (DEBUG) {
                        if (!isASCII7(c)) {
                            throw new IllegalArgumentException("Not an ASCII-7 character");
                        }
                    }
                    context.writeCodeword((char)(c + 1));
                    context.pos++;
                }

            }
        }

    }

    private static class C40Encoder implements Encoder {

        public int getEncodingMode() {
            return C40_ENCODATION;
        }

        public void encode(EncoderContext context) {
            //step C
            int lastCharSize = -1;
            StringBuffer buffer = new StringBuffer();
            outerloop: while (context.hasMoreCharacters()) {
                char c = context.getCurrentChar();
                context.pos++;

                lastCharSize = encodeChar(c, buffer);

                int unwritten = (buffer.length() / 3) * 2;

                int curCodewordCount = context.getCodewordCount() + unwritten;
                context.updateSymbolInfo(curCodewordCount);
                int available = context.symbolInfo.dataCapacity - curCodewordCount;

                if (!context.hasMoreCharacters()) {
                    //Avoid having a single C40 value in the last triplet
                    StringBuffer removed = new StringBuffer();
                    if ((buffer.length() % 3) == 2) {
                        if (available < 2 || available > 2) {
                            lastCharSize = backtrackOneCharacter(context, buffer, removed,
                                    lastCharSize);
                        }
                    }
                    while ((buffer.length() % 3) == 1
                            && ((lastCharSize <= 3 && available != 1) || lastCharSize > 3)) {
                        lastCharSize = backtrackOneCharacter(context, buffer, removed,
                                lastCharSize);
                    }
                    break outerloop;
                }

                int count = buffer.length();
                if ((count % 3) == 0) {
                    int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                    if (newMode != getEncodingMode()) {
                        context.signalEncoderChange(newMode);
                        break;
                    }
                }
            }
            handleEOD(context, buffer, lastCharSize);
        }

        private int backtrackOneCharacter(EncoderContext context,
                StringBuffer buffer, StringBuffer removed, int lastCharSize) {
            int count = buffer.length();
            buffer.delete(count - lastCharSize, count);
            context.pos--;
            char c = context.getCurrentChar();
            lastCharSize = encodeChar(c, removed);
            context.resetSymbolInfo(); //Deal with possible reduction in symbol size
            return lastCharSize;
        }

        protected void writeNextTriplet(EncoderContext context, StringBuffer buffer) {
            context.writeCodewords(encodeToCodewords(buffer, 0));
            buffer.delete(0, 3);
        }

        /**
         * Handle "end of data" situations
         * @param context the encoder context
         * @param buffer the buffer with the remaining encoded characters
         */
        protected void handleEOD(EncoderContext context, StringBuffer buffer, int lastCharSize) {
            int unwritten = (buffer.length() / 3) * 2;
            int rest = buffer.length() % 3;

            int curCodewordCount = context.getCodewordCount() + unwritten;
            context.updateSymbolInfo(curCodewordCount);
            int available = context.symbolInfo.dataCapacity - curCodewordCount;

            if (rest == 2) {
                buffer.append('\0'); //Shift 1
                while (buffer.length() >= 3) {
                    writeNextTriplet(context, buffer);
                }
                if (context.hasMoreCharacters()) {
                    context.writeCodeword(C40_UNLATCH);
                }
            } else if (available == 1 && rest == 1) {
                while (buffer.length() >= 3) {
                    writeNextTriplet(context, buffer);
                }
                if (context.hasMoreCharacters()) {
                    context.writeCodeword(C40_UNLATCH);
                } else {
                    //No unlatch
                }
                context.pos--;
            } else if (rest == 0) {
                while (buffer.length() >= 3) {
                    writeNextTriplet(context, buffer);
                }
                if (available > 0 || context.hasMoreCharacters()) {
                    context.writeCodeword(C40_UNLATCH);
                }
            } else {
                throw new IllegalStateException("Unexpected case. Please report!");
            }
            context.signalEncoderChange(ASCII_ENCODATION);
        }

        protected int encodeChar(char c, StringBuffer sb) {
            if (c == ' ') {
                sb.append('\3');
                return 1;
            } else if (c >= '0' && c <= '9') {
                sb.append((char)(c - 48 + 4));
                return 1;
            } else if (c >= 'A' && c <= 'Z') {
                sb.append((char)(c - 65 + 14));
                return 1;
            } else if (c >= '\0' && c <= '\u001f') {
                sb.append('\0'); //Shift 1 Set
                sb.append(c);
                return 2;
            } else if (c >= '!' && c <= '/') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 33));
                return 2;
            } else if (c >= ':' && c <= '@') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 58 + 15));
                return 2;
            } else if (c >= '[' && c <= '_') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 91 + 22));
                return 2;
            } else if (c >= '\u0060' && c <= '\u007f') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 96));
                return 2;
            } else if (c >= '\u0080') {
                sb.append("\1\u001e"); //Shift 2, Upper Shift
                int len = 2;
                len += encodeChar((char)(c - 128), sb);
                return len;
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
        }

        protected String encodeToCodewords(StringBuffer sb, int startPos) {
            char c1 = sb.charAt(startPos);
            char c2 = sb.charAt(startPos + 1);
            char c3 = sb.charAt(startPos + 2);
            int v = (1600 * c1) + (40 * c2) + c3 + 1;
            char cw1 = (char)(v / 256);
            char cw2 = (char)(v % 256);
            return "" + cw1 + cw2;
        }

    }

    private static class TextEncoder extends C40Encoder {

        public int getEncodingMode() {
            return TEXT_ENCODATION;
        }

        protected int encodeChar(char c, StringBuffer sb) {
            if (c == ' ') {
                sb.append('\3');
                return 1;
            } else if (c >= '0' && c <= '9') {
                sb.append((char)(c - 48 + 4));
                return 1;
            } else if (c >= 'a' && c <= 'z') {
                sb.append((char)(c - 97 + 14));
                return 1;
            } else if (c >= '\0' && c <= '\u001f') {
                sb.append('\0'); //Shift 1 Set
                sb.append(c);
                return 2;
            } else if (c >= '!' && c <= '/') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 33));
                return 2;
            } else if (c >= ':' && c <= '@') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 58 + 15));
                return 2;
            } else if (c >= '[' && c <= '_') {
                sb.append('\1'); //Shift 2 Set
                sb.append((char)(c - 91 + 22));
                return 2;
            } else if (c == '\u0060') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 96));
                return 2;
            } else if (c >= 'A' && c <= 'Z') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 65 + 1));
                return 2;
            } else if (c >= '{' && c <= '\u007f') {
                sb.append('\2'); //Shift 3 Set
                sb.append((char)(c - 123 + 27));
                return 2;
            } else if (c >= '\u0080') {
                sb.append("\1\u001e"); //Shift 2, Upper Shift
                int len = 2;
                len += encodeChar((char)(c - 128), sb);
                return len;
            } else {
                illegalCharacter(c);
                return -1;
            }
        }

    }

    private static class X12Encoder extends C40Encoder {

        public int getEncodingMode() {
            return X12_ENCODATION;
        }

        public void encode(EncoderContext context) {
            //step C
            StringBuffer buffer = new StringBuffer();
            while (context.hasMoreCharacters()) {
                char c = context.getCurrentChar();
                context.pos++;

                encodeChar(c, buffer);

                int count = buffer.length();
                if ((count % 3) == 0) {
                    writeNextTriplet(context, buffer);

                    int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                    if (newMode != getEncodingMode()) {
                        context.signalEncoderChange(newMode);
                        break;
                    }
                }
            }
            handleEOD(context, buffer);
        }

        protected int encodeChar(char c, StringBuffer sb) {
            if (c == '\r') {
                sb.append('\0');
            } else if (c == '*') {
                sb.append('\1');
            } else if (c == '>') {
                sb.append('\2');
            } else if (c == ' ') {
                sb.append('\3');
            } else if (c >= '0' && c <= '9') {
                sb.append((char)(c - 48 + 4));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append((char)(c - 65 + 14));
            } else {
                illegalCharacter(c);
            }
            return 1;
        }

        protected void handleEOD(EncoderContext context, StringBuffer buffer) {
            context.updateSymbolInfo();
            int available = context.symbolInfo.dataCapacity - context.getCodewordCount();
            int count = buffer.length();
            if (count == 2) {
                context.writeCodeword(X12_UNLATCH);
                context.pos -= 2;
                context.signalEncoderChange(ASCII_ENCODATION);
            } else if (count == 1) {
                context.pos--;
                if (available > 1) {
                    context.writeCodeword(X12_UNLATCH);
                } else {
                    //NOP - No unlatch necessary
                }
                context.signalEncoderChange(ASCII_ENCODATION);
            }
        }
    }

    private static class EdifactEncoder implements Encoder {

        public int getEncodingMode() {
            return EDIFACT_ENCODATION;
        }

        public void encode(EncoderContext context) {
            //step F
            StringBuffer buffer = new StringBuffer();
            while (context.hasMoreCharacters()) {
                char c = context.getCurrentChar();
                encodeChar(c, buffer);
                context.pos++;

                int count = buffer.length();
                if (count >= 4) {
                    context.writeCodewords(encodeToCodewords(buffer, 0));
                    buffer.delete(0, 4);

                    int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                    if (newMode != getEncodingMode()) {
                        context.signalEncoderChange(ASCII_ENCODATION);
                        break;
                    }
                }
            }
            buffer.append((char)31); //Unlatch
            handleEOD(context, buffer);
        }

        /**
         * Handle "end of data" situations
         * @param context the encoder context
         * @param buffer the buffer with the remaining encoded characters
         */
        protected void handleEOD(EncoderContext context, StringBuffer buffer) {
            try {
                int count = buffer.length();
                if (count == 0) {
                    return; //Already finished
                } else if (count == 1) {
                    //Only an unlatch at the end
                    context.updateSymbolInfo();
                    int available = context.symbolInfo.dataCapacity - context.getCodewordCount();
                    int remaining = context.getRemainingCharacters();
                    if (remaining == 0 && available <= 2) {
                        return; //No unlatch
                    }
                }

                if (count > 4) {
                    throw new IllegalStateException("Count must not exceed 4");
                }
                int restChars = count - 1;
                String encoded = encodeToCodewords(buffer, 0);
                boolean endOfSymbolReached = !context.hasMoreCharacters();
                boolean restInAscii = endOfSymbolReached && restChars <= 2;

                int available;
                if (restChars <= 2) {
                    context.updateSymbolInfo(context.getCodewordCount() + restChars);
                    available = context.symbolInfo.dataCapacity - context.getCodewordCount();
                    if (available >= 3) {
                        restInAscii = false;
                        context.updateSymbolInfo(context.getCodewordCount() + encoded.length());
                        available = context.symbolInfo.dataCapacity - context.getCodewordCount();
                    }
                }

                if (restInAscii) {
                    context.resetSymbolInfo();
                    context.pos -= restChars;
                } else {
                    context.writeCodewords(encoded);
                }
            } finally {
                context.signalEncoderChange(ASCII_ENCODATION);
            }
        }

        protected void encodeChar(char c, StringBuffer sb) {
            if (c >= ' ' && c <= '?') {
                sb.append(c);
            } else if (c >= '@' && c <= '^') {
                sb.append((char)(c - 64));
            } else {
                illegalCharacter(c);
            }
        }

        protected String encodeToCodewords(StringBuffer sb, int startPos) {
            int len = sb.length() - startPos;
            if (len == 0) {
                throw new IllegalStateException("StringBuffer must not be empty");
            }
            char c1 = sb.charAt(startPos);
            char c2 = (len >= 2 ? sb.charAt(startPos + 1) : 0);
            char c3 = (len >= 3 ? sb.charAt(startPos + 2) : 0);
            char c4 = (len >= 4 ? sb.charAt(startPos + 3) : 0);

            int v = (c1 << 18) + (c2 << 12) + (c3 << 6) + c4;
            char cw1 = (char)((v >> 16) & 255);
            char cw2 = (char)((v >> 8) & 255);
            char cw3 = (char)(v & 255);
            StringBuffer res = new StringBuffer(3);
            res.append(cw1);
            if (len >= 2) {
                res.append(cw2);
            }
            if (len >= 3) {
                res.append(cw3);
            }
            return res.toString();
        }

    }

    private static class Base256Encoder implements Encoder {

        public int getEncodingMode() {
            return BASE256_ENCODATION;
        }

        public void encode(EncoderContext context) {
            StringBuffer buffer = new StringBuffer();
            buffer.append('\0'); //Initialize length field
            while (context.hasMoreCharacters()) {
                char c = context.getCurrentChar();
                buffer.append(c);

                context.pos++;

                int newMode = lookAheadTest(context.msg, context.pos, getEncodingMode());
                if (newMode != getEncodingMode()) {
                    context.signalEncoderChange(newMode);
                    break;
                }
            }
            int dataCount = buffer.length() - 1;
            int lengthFieldSize = 1;
            int currentSize = (context.getCodewordCount() + dataCount + lengthFieldSize);
            context.updateSymbolInfo(currentSize);
            boolean mustPad = ((context.symbolInfo.dataCapacity - currentSize) > 0);
            if (context.hasMoreCharacters() || mustPad) {
                if (dataCount <= 249) {
                    buffer.setCharAt(0, (char)dataCount);
                } else if (dataCount > 249 && dataCount <= 1555) {
                    buffer.setCharAt(0, (char)((dataCount / 250) + 249));
                    buffer.insert(1, (char)(dataCount % 250));
                } else {
                    throw new IllegalStateException(
                            "Message length not in valid ranges: " + dataCount);
                }
            }
            for (int i = 0, c = buffer.length(); i < c; i++) {
                context.writeCodeword(randomize255State(
                        buffer.charAt(i), context.getCodewordCount() + 1));
            }
        }

    }

    private static char encodeASCIIDigits(char digit1, char digit2) {
        if (isDigit(digit1) && isDigit(digit2)) {
            int num = (digit1 - 48) * 10 + (digit2 - 48);
            return (char)(num + 130);
        } else {
            throw new IllegalArgumentException("not digits: " + digit1 + digit2);
        }
    }

    private static int lookAheadTest(String msg, int startpos, int currentMode) {
        if (startpos >= msg.length()) {
            return currentMode;
        }
        float[] charCounts;
        //step J
        if (currentMode == ASCII_ENCODATION) {
            charCounts = new float[] {0, 1, 1, 1, 1, 1.25f};
        } else {
            charCounts = new float[] {1, 2, 2, 2, 2, 2.25f};
            charCounts[currentMode] = 0;
        }

        int charsProcessed = 0;
        while (true) {
            //step K
            if ((startpos + charsProcessed) == msg.length()) {
                int min = Integer.MAX_VALUE;
                byte[] mins = new byte[6];
                int[] intCharCounts = new int[6];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                int minCount = getMinimumCount(mins);

                if (intCharCounts[ASCII_ENCODATION] == min) {
                    return ASCII_ENCODATION;
                } else if (minCount == 1 && mins[BASE256_ENCODATION] > 0) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                } else {
                    return C40_ENCODATION;
                }
            }

            char c = msg.charAt(startpos + charsProcessed);
            charsProcessed++;

            //step L
            if (isDigit(c)) {
                charCounts[ASCII_ENCODATION] += 0.5;
            } else if (isExtendedASCII(c)) {
                charCounts[ASCII_ENCODATION] = (int)Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION] += 2;
            } else {
                charCounts[ASCII_ENCODATION] = (int)Math.ceil(charCounts[ASCII_ENCODATION]);
                charCounts[ASCII_ENCODATION] += 1;
            }

            //step M
            if (isNativeC40(c)) {
                charCounts[C40_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[C40_ENCODATION] += 8f / 3f;
            } else {
                charCounts[C40_ENCODATION] += 4f / 3f;
            }

            //step N
            if (isNativeText(c)) {
                charCounts[TEXT_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[TEXT_ENCODATION] += 8f / 3f;
            } else {
                charCounts[TEXT_ENCODATION] += 4f / 3f;
            }

            //step O
            if (isNativeX12(c)) {
                charCounts[X12_ENCODATION] += 2f / 3f;
            } else if (isExtendedASCII(c)) {
                charCounts[X12_ENCODATION] += 13f / 3f;
            } else {
                charCounts[X12_ENCODATION] += 10f / 3f;
            }

            //step P
            if (isNativeEDIFACT(c)) {
                charCounts[EDIFACT_ENCODATION] += 3f / 4f;
            } else if (isExtendedASCII(c)) {
                charCounts[EDIFACT_ENCODATION] += 17f / 4f;
            } else {
                charCounts[EDIFACT_ENCODATION] += 13f / 4f;
            }

            // step Q
            if (isSpecialB256(c)) {
                charCounts[BASE256_ENCODATION] += 4;
            } else {
                charCounts[BASE256_ENCODATION] += 1;
            }

            //step R
            if (charsProcessed >= 4) {
                /*
                for (int a = 0; a < charCounts.length; a++) {
                    System.out.println(a + " " + ENCODATION_NAMES[a] + " " + charCounts[a]);
                }*/

                int min = Integer.MAX_VALUE;
                int[] intCharCounts = new int[6];
                byte[] mins = new byte[6];
                min = findMinimums(charCounts, intCharCounts, min, mins);
                int minCount = getMinimumCount(mins);

                if (intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[C40_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[TEXT_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[X12_ENCODATION]
                        && intCharCounts[ASCII_ENCODATION] + 1 <= intCharCounts[EDIFACT_ENCODATION]) {
                    return ASCII_ENCODATION;
                } else if (intCharCounts[BASE256_ENCODATION] + 1 <= intCharCounts[ASCII_ENCODATION]
                        || (mins[C40_ENCODATION]
                                 + mins[TEXT_ENCODATION]
                                 + mins[X12_ENCODATION]
                                 + mins[EDIFACT_ENCODATION]) == 0) {
                    return BASE256_ENCODATION;
                } else if (minCount == 1 && mins[EDIFACT_ENCODATION] > 0) {
                    return EDIFACT_ENCODATION;
                } else if (minCount == 1 && mins[TEXT_ENCODATION] > 0) {
                    return TEXT_ENCODATION;
                } else if (minCount == 1 && mins[X12_ENCODATION] > 0) {
                    return X12_ENCODATION;
                } else if (intCharCounts[C40_ENCODATION] + 1 < intCharCounts[ASCII_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[BASE256_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[EDIFACT_ENCODATION]
                        && intCharCounts[C40_ENCODATION] + 1 < intCharCounts[TEXT_ENCODATION]) {
                    if (intCharCounts[C40_ENCODATION] < intCharCounts[X12_ENCODATION]) {
                        return C40_ENCODATION;
                    } else if (intCharCounts[C40_ENCODATION] == intCharCounts[X12_ENCODATION]) {
                        int p = startpos + charsProcessed + 1;
                        while (p < msg.length()) {
                            char tc = msg.charAt(p);
                            if (isX12TermSep(tc)) {
                                return X12_ENCODATION;
                            } else if (!isNativeX12(tc)) {
                                break;
                            }
                            p++;
                        }
                        return C40_ENCODATION;
                    }
                }
            }
        }
    }

    private static int findMinimums(float[] charCounts, int[] intCharCounts,
            int min, byte[] mins) {
        Arrays.fill(mins, (byte)0);
        for (int i = 0; i < 6; i++) {
            intCharCounts[i] = (int)Math.ceil(charCounts[i]);
            int current = intCharCounts[i];
            if (min > current) {
                min = current;
                Arrays.fill(mins, (byte)0);
            }
            if (min == current) {
                mins[i]++;

            }
        }
        return min;
    }

    private static int getMinimumCount(byte[] mins) {
        int minCount = 0;
        for (int i = 0; i < 6; i++) {
            minCount += mins[i];
        }
        return minCount;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /*
    private static final String EXTENDED_ASCII;
    static {
        EXTENDED_ASCII = buildExtendedASCIIMap(DEFAULT_ASCII_ENCODING);
    }

    private static String buildExtendedASCIIMap(String encoding) {
        byte[] buf = new byte[128];
        for (int i = 0; i < 128; i++) {
            buf[i] = (byte)(i + 128);
        }
        try {
            String extendedASCII = new String(buf, encoding);
            if (extendedASCII.length() != buf.length) {
                throw new UnsupportedOperationException(
                        "Cannot deal with encodings that don't have"
                            + " a 1:1 character/byte relationship!");
            }
            return extendedASCII;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }*/

    private static boolean isExtendedASCII(char ch) {
        return (ch >= 128 && ch <= 255);
    }

    private static boolean isASCII7(char ch) {
        return (ch >= 0 && ch <= 127);
    }

    private static boolean isNativeC40(char ch) {
        return (ch == 32)
                || (ch >= 48 && ch <= 57) //0..9
                || (ch >= 65 && ch <= 90); //A..Z
    }

    private static boolean isNativeText(char ch) {
        return (ch == 32)
        || (ch >= 48 && ch <= 57) //0..9
        || (ch >= 97 && ch <= 122); //a..z
    }

    private static boolean isNativeX12(char ch) {
        return isX12TermSep(ch)
                || (ch == 32) //SPACE
                || (ch >= 48 && ch <= 57)
                || (ch >= 65 && ch <= 90);
    }

    private static boolean isX12TermSep(char ch) {
        return (ch == 13) //CR
                || (ch == 42) //"*"
                || (ch == 62); //">"
    }

    private static boolean isNativeEDIFACT(char ch) {
        return (ch >= 32 && ch <= 94);
    }

    private static boolean isSpecialB256(char ch) {
        return false; //TODO NOT IMPLEMENTED YET!!!
    }

    /**
     * Determines the number of consecutive characters that are encodable using numeric compaction.
     * @param msg the message
     * @param startpos the start position within the message
     * @return the requested character count
     */
    public static int determineConsecutiveDigitCount(String msg, int startpos) {
        int count = 0;
        int len = msg.length();
        int idx = startpos;
        if (idx < len) {
            char ch = msg.charAt(idx);
            while (isDigit(ch) && idx < len) {
                count++;
                idx++;
                if (idx < len) {
                    ch = msg.charAt(idx);
                }
            }
        }
        return count;
    }

    private static void illegalCharacter(char c) {
        String hex = Integer.toHexString(c);
        final String padding = "0000";
        hex = padding.substring(0, 4 - hex.length()) + hex;
        throw new IllegalArgumentException("Illegal character: " + c
                + " (0x" + hex + ")");
    }

}
