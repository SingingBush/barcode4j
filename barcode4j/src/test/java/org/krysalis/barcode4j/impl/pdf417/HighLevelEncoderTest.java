/*
 * Copyright 2006 Jeremias Maerki.
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

/* $Id: HighLevelEncoderTest.java,v 1.10 2012-05-17 13:57:37 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import junit.framework.TestCase;

import org.krysalis.barcode4j.tools.TestHelper;

public class HighLevelEncoderTest extends TestCase implements PDF417Constants {

    public void testFindNumericSequence() throws Exception {
        String msg = "1234567890ABC";
        int count = PDF417HighLevelEncoder.determineConsecutiveDigitCount(msg, 0);
        assertEquals(10, count);
    }

    public void testFindTextSequence() throws Exception {
        String msg = "1234567890ABCDÄ123";
        int count = PDF417HighLevelEncoder.determineConsecutiveTextCount(msg, 0);
        assertEquals(14, count);

        msg = "123456789012345ABCDÄ123";
        count = PDF417HighLevelEncoder.determineConsecutiveTextCount(msg, 0);
        assertEquals(0, count); //0 because the string has a 13+ numeric sequence
    }

    private byte[] getBytesForMessage(String msg) {
        return PDF417HighLevelEncoder.getBytesForMessage(msg, PDF417Constants.DEFAULT_ENCODING);
    }

    public void testFindBinarySequence() throws Exception {
        String msg;
        byte[] bytes;
        int count;

        msg = "A10200124040182000";
        bytes = getBytesForMessage(msg);
        count = PDF417HighLevelEncoder.determineConsecutiveBinaryCount(msg, bytes, 0);
        assertEquals(0, count);

        msg = "‰ˆ¸TestÈË‡1234567890123456789";
        bytes = getBytesForMessage(msg);
        count = PDF417HighLevelEncoder.determineConsecutiveBinaryCount(msg, bytes, 0);
        assertEquals(10, count);

        msg = "‰ˆ¸Test";
        bytes = getBytesForMessage(msg);
        count = PDF417HighLevelEncoder.determineConsecutiveBinaryCount(msg, bytes, 0);
        assertEquals(7, count);

        msg = "‰ˆ¸‰‰‰‰‰‰‰TestTest";
        bytes = getBytesForMessage(msg);
        count = PDF417HighLevelEncoder.determineConsecutiveBinaryCount(msg, bytes, 0);
        assertEquals(10, count);

        msg = "‰ˆ¸TestÈË‡Ä1234567890";
        bytes = getBytesForMessage(msg);
        try {
            count = PDF417HighLevelEncoder.determineConsecutiveBinaryCount(msg, bytes, 0);
            fail("The Euro character is not encodable in cp437."
                    + " An IllegalArgumentException is expected.");
        } catch (IllegalArgumentException iae) {
            //exception is expected
        }

    }

    public void testEncodeText() throws Exception {
        String msg = "PDF417";
        StringBuffer sb = new StringBuffer();
        PDF417HighLevelEncoder.encodeText(msg, 0, msg.length(), sb, SUBMODE_ALPHA);
        String expected = "\u01c5\u00b2\u0079\u00ef";
        assertEquals(expected, sb.toString());

        String result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "453 178 121 239";
        assertEquals(expected, result);

        msg = "PDF PDF";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "453 176 453 179";
        assertEquals(expected, result);

        msg = "PDF417 Symbology Standard";
        sb.setLength(0);
        PDF417HighLevelEncoder.encodeText(msg, 0, msg.length(), sb, SUBMODE_ALPHA);
        //There was a bug with an endless loop here, just check that it doesn't hang.
    }

    public void testEncodeTextLatching() throws Exception {
        String msg = "417'<x>";
        String result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        String expected = "844 37 778 59 833 872";
        assertEquals(expected, result);

        msg = "417'417";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "844 37 898 121 239";
        assertEquals(expected, result);

        msg = "417abc417";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "844 37 810 32 844 37";
        assertEquals(expected, result);

        msg = "PDF@417";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "453 179 118 121 239";
        assertEquals(expected, result);
    }

    public void testEncodeNumeric() throws Exception {
        String msg = "000213298174000";
        StringBuffer sb = new StringBuffer();
        PDF417HighLevelEncoder.encodeNumeric(msg, 0, msg.length(), sb);
        String expected = "\u0001\u0270\u01b2\u0278\u011a\u00c8";
        assertEquals(expected, sb.toString());
    }

    private void log(String expected, String actual) {
        /*
        System.out.println("expected: " + expected);
        System.out.println("actual: " + actual);
        */
    }

    public void testEncodeBinary() throws Exception {
        //Example from Annex C
        byte[] bytes = new byte[] {(byte)231, 101, 11, 97, (byte)205, 2};
        String msg = new String(bytes, "cp437");
        StringBuffer sb = new StringBuffer();
        PDF417HighLevelEncoder.encodeBinary(msg, bytes, 0, msg.length(), TEXT_COMPACTION, sb);
        String expected = "924 387 700 208 213 302";
        log(expected, TestHelper.visualize(sb.toString()));
        assertEquals(expected, TestHelper.visualize(sb.toString()));

        msg = msg + msg + msg.substring(0, 1);
        sb.setLength(0);
        bytes = getBytesForMessage(msg);
        PDF417HighLevelEncoder.encodeBinary(msg, bytes, 0, msg.length(), TEXT_COMPACTION, sb);
        expected = TestHelper.visualize("\u0385\u0183\u02bc\u00d0\u00d5\u012e\u0183\u02bc\u00d0\u00d5\u012e\u00e7");
        assertEquals(expected, TestHelper.visualize(sb.toString()));

        msg = "‰‰‰‰‰‰‰‰‰‰‰‰"; //12 binary characters = 2x6
        bytes = getBytesForMessage(msg);
        sb.setLength(0);
        PDF417HighLevelEncoder.encodeBinary(msg, bytes, 0, msg.length(), TEXT_COMPACTION, sb);
        expected = "924 222 69 238 51 792 222 69 238 51 792";
        assertEquals(expected, TestHelper.visualize(sb.toString()));

        msg = "‰‰‰‰‰‰‰‰‰‰"; //10 binary characters = 1x6 + 4
        bytes = getBytesForMessage(msg);
        sb.setLength(0);
        PDF417HighLevelEncoder.encodeBinary(msg, bytes, 0, msg.length(), TEXT_COMPACTION, sb);
        expected = "901 222 69 238 51 792 132 132 132 132";
        assertEquals(expected, TestHelper.visualize(sb.toString()));
    }

    public void testEncodeHighLevel() throws Exception {
        String msg = "000213298174000";
        String result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        String expected = "902 1 624 434 632 282 200";
        assertEquals(expected, result);

        msg = "PDF417";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "453 178 121 239";
        assertEquals(expected, result);

        msg = "000213298174000PDF417";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "902 1 624 434 632 282 200 900 453 178 121 239";
        assertEquals(expected, result);

        msg = "TestTest‰‰‰‰‰‰‰‰‰‰‰‰"; //12 binary characters = 2x6
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "597 138 597 574 559 924 222 69 238 51 792 222 69 238 51 792";
        assertEquals(expected, result);

        msg = "TestTest‰‰‰‰‰‰‰‰‰‰"; //10 binary characters = 1x6 + 4
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "597 138 597 574 559 901 222 69 238 51 792 132 132 132 132";
        log(expected, result);
        assertEquals(expected, result);

        msg = "A10200124040182000";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "913 65 902 186 562 350 852 68 800";
        log(expected, result);
        assertEquals(expected, result);

        msg = "A10200124040 182000";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "28 30 60 1 64 4 26 38 60 0";
        log(expected, result);
        assertEquals(expected, result);

        msg = "A1234567890123456789012 1365465465464";
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "913 65 902 23 439 739 333 729 883 621 112 901 32 902 17 290 438 761 564";
        log(expected, result);
        assertEquals(expected, result);
    }

    public void testBug1970483() throws Exception {
        String msg = "<FIELDS><FIELD NAME=\"DEALER #\">550";
        //The bug was an invalid value for switching back from Punctuation to Alpha in
        //Text Compaction Mode (right before "550" in the example above)
        String result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        String expected = "871 158 131 108 872 871 158 131 116 390 364 863 890 843 120 334 536 855 770 89 845 150";
        assertEquals(expected, result);
    }

    public void testBug2482570() throws Exception {
        String msg = "UNT+11+123'";
        String result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        String expected = "613 598 601 50 32 119 869";
        assertEquals(expected, result);
    }

    /**
     * Tests bug https://sourceforge.net/tracker/?func=detail&atid=615504&aid=2804024&group_id=96670
     * @throws Exception if an error occurs
     */
    public void testBug2804024() throws Exception {
        String msg, result, expected;
        msg = "5789\u001dB0KLT3215\u001e\u0004"; //good
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "901 53 55 56 57 29 900 58 28 311 598 92 35 901 30 4";
        //latch to byte, 5 bytes, latch to text, 9 bytes, latch to byte, 2 bytes
        assertEquals(expected, result);

        msg = "45789\u001dB0KLT3215\u001e\u0004"; //bad
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "844 157 249 913 29 841 840 850 349 843 61 179 901 30 4";
        //5 bytes in text, shift to byte, 1 byte, 9 bytes in text, latch to byte, 2 bytes
        //Problem here was: shift to byte (913) does not reset text sub-mode!
        assertEquals(expected, result);
    }

    public void testBinaryData() throws Exception {
        String msg, result, expected;
        msg = "url(data:;base64,flRlc3R+)"; //~Test~
        //System.out.println(new String(URLUtil.getData(URLUtil.getURL(msg), "UTF-8")));
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "924 211 636 247 386 518";
        assertEquals(expected, result);
    }

    public void testCharsets() throws Exception {
        String msg;
        String result;
        String expected;

        //Note: \u00e4 is 0x84 in Cp437 and 0xE4 in ISO-8859-1
        msg = "Test\u00e4\u00e4\u00e4\u00e4\u00e4\u00e4"; //10 characters encoded as binary
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(msg));
        expected = "901 141 390 364 673 320 132 132 132 132";
        log(expected, result);
        assertEquals(expected, result);

        //Same text but this time with ISO-8859-1 (but with no ECI!!!)
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(
                msg, "ISO-8859-1", false));
        expected = "901 141 390 364 700 692 228 228 228 228";
        log(expected, result);
        assertEquals(expected, result);

        //Same text but this time with ISO-8859-1 and ECI enabled
        result = TestHelper.visualize(PDF417HighLevelEncoder.encodeHighLevel(
                msg, "ISO-8859-1", true));
        expected = "927 3 901 141 390 364 700 692 228 228 228 228";
        log(expected, result);
        assertEquals(expected, result);
    }
}
