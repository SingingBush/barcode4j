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

/* $Id: USPSIntelligentMailTest.java,v 1.1 2008-05-13 13:00:44 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.fourstate;

import java.math.BigInteger;
import java.util.Arrays;

import junit.framework.TestCase;

import org.krysalis.barcode4j.impl.MockClassicBarcodeLogicHandler;

import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * Tests for the USPS Intelligent Mail Barcode (4-State Customer Barcode).
 */
public class USPSIntelligentMailTest extends TestCase {

    public void testCharLookupTableInitialization() throws Exception {
        //random/spot checks
        assertEquals(31, USPSIntelligentMailLogicImpl.TABLE5OF13[0]);
        assertEquals(7936, USPSIntelligentMailLogicImpl.TABLE5OF13[1]);
        assertEquals(47, USPSIntelligentMailLogicImpl.TABLE5OF13[2]);
        assertEquals(7808, USPSIntelligentMailLogicImpl.TABLE5OF13[3]);
        assertEquals(6368, USPSIntelligentMailLogicImpl.TABLE5OF13[93]);
        assertEquals(1077, USPSIntelligentMailLogicImpl.TABLE5OF13[500]);
        assertEquals(2641, USPSIntelligentMailLogicImpl.TABLE5OF13[1000]);
        assertEquals(856, USPSIntelligentMailLogicImpl.TABLE5OF13[1284]);
        assertEquals(744, USPSIntelligentMailLogicImpl.TABLE5OF13[1285]);
        assertEquals(496, USPSIntelligentMailLogicImpl.TABLE5OF13[1286]);

        assertEquals(3, USPSIntelligentMailLogicImpl.TABLE2OF13[0]);
        assertEquals(6144, USPSIntelligentMailLogicImpl.TABLE2OF13[1]);
        assertEquals(5, USPSIntelligentMailLogicImpl.TABLE2OF13[2]);
        assertEquals(5120, USPSIntelligentMailLogicImpl.TABLE2OF13[3]);
        assertEquals(144, USPSIntelligentMailLogicImpl.TABLE2OF13[50]);
        assertEquals(513, USPSIntelligentMailLogicImpl.TABLE2OF13[60]);
        assertEquals(520, USPSIntelligentMailLogicImpl.TABLE2OF13[75]);
        assertEquals(272, USPSIntelligentMailLogicImpl.TABLE2OF13[76]);
        assertEquals(160, USPSIntelligentMailLogicImpl.TABLE2OF13[77]);
    }
    
    private static final String[] EXAMPLE_MESSAGES = new String[] {
        "01234567094987654321",
        "0123456709498765432101234",
        "01234567094987654321012345678",
        "0123456709498765432101234567891"
    };
    
    public void testBinaryConversion() throws Exception {
        final String[] results = new String[] {
            "00 00 00 00 00 11 22 10 3B 5C 20 04 B1",
            "00 00 00 0D 13 8A 87 BA B5 CF 38 04 B1",
            "00 02 02 BD C0 97 71 12 04 D2 18 04 B1",
            "01 69 07 B2 A2 4A BC 16 A2 E5 C0 04 B1"
        };
        
        //non-formatted messages
        for (int i = 0; i < EXAMPLE_MESSAGES.length; i++) {
            String msg = EXAMPLE_MESSAGES[i];
            BigInteger binary = USPSIntelligentMailLogicImpl.convertToBinary(msg);
            String hexBinary = toHex(binary.toByteArray());
            assertEquals(results[i], hexBinary);
        }
    }
    
    private static final int[] EXAMPLE_FCS = new int[] {0x0051, 0x0065, 0x0606, 0x0751};
    
    public void testFCS() throws Exception {
        for (int i = 0; i < EXAMPLE_MESSAGES.length; i++) {
            String msg = EXAMPLE_MESSAGES[i];
            BigInteger binary = USPSIntelligentMailLogicImpl.convertToBinary(msg);
            int fcs = USPSIntelligentMailLogicImpl.calcFCS(
                    USPSIntelligentMailLogicImpl.to13ByteArray(binary));
            assertEquals(EXAMPLE_FCS[i], fcs);
        }
    }
    
    private static final int[][] EXAMPLE_CODEWORDS = new int[][] {
            {0, 0, 0, 0, 559, 202, 508, 451, 124, 17},
            {0, 0, 15, 14, 290, 567, 385, 48, 388, 333},
            {0, 110, 1113, 1363, 198, 413, 470, 468, 1333, 513},
            {14, 787, 607, 1022, 861, 19, 816, 1294, 35, 301}
        };
    
    public void testCodewords() throws Exception {
        for (int i = 0; i < EXAMPLE_MESSAGES.length; i++) {
            String msg = EXAMPLE_MESSAGES[i];
            BigInteger binary = USPSIntelligentMailLogicImpl.convertToBinary(msg);
            int[] codewords = USPSIntelligentMailLogicImpl.convertToCodewords(binary);
            assertTrue(Arrays.equals(EXAMPLE_CODEWORDS[i], codewords));
        }
    }

    private static final int[][] EXAMPLE_MODIFIED = new int[][] {
            {0, 0, 0, 0, 559, 202, 508, 451, 124, 34},
            {0, 0, 15, 14, 290, 567, 385, 48, 388, 666},
            {659, 110, 1113, 1363, 198, 413, 470, 468, 1333, 1026},
            {673, 787, 607, 1022, 861, 19, 816, 1294, 35, 602}
        };

    public void testCodewordModification() throws Exception {
        for (int i = 0; i < EXAMPLE_CODEWORDS.length; i++) {
            int[] modified = USPSIntelligentMailLogicImpl.modifyCodewords(
                    EXAMPLE_CODEWORDS[i], EXAMPLE_FCS[i]);
            assertTrue("Array " + i + " does not match",
                    Arrays.equals(EXAMPLE_MODIFIED[i], modified));
        }
    }

    static final char[][] EXAMPLE_CHARS = new char[][] {
            {'\u1FE0', '\u001F', '\u001F', '\u001F', '\u0ADB',
                '\u01A3', '\u1BC3', '\u1838', '\u012B', '\u0076'},
            {'\u1FE0', '\u001F', '\u02BF', '\u0057', '\u0255',
                '\u18DB', '\u1B17', '\u009D', '\u030B', '\u0583'},
            {'\u1154', '\u1F07', '\u01FE', '\u0110', '\u019A',
                '\u1298', '\u03A2', '\u03A1', '\u0084', '\u14EE'},
            {'\u0DCB', '\u085C', '\u08E4', '\u0B06', '\u06DD',
                 '\u1740', '\u17C6', '\u1200', '\u123F', '\u1B2B'}
        };
    
    public void testCharacterConversion() throws Exception {
        for (int i = 0; i < EXAMPLE_MODIFIED.length; i++) {
            char[] result = USPSIntelligentMailLogicImpl.convertToCharacters(
                    EXAMPLE_MODIFIED[i], EXAMPLE_FCS[i]);
            assertTrue("Array " + i + " does not match",
                    Arrays.equals(EXAMPLE_CHARS[i], result));
        }
    }

    private static final String[] EXAMPLE_BARS = new String[] {
        "ATTFATTDTTADTAATTDTDTATTDAFDDFADFDFTFFFFFTATFAAAATDFFTDAADFTFDTDT",
        "DTTAFADDTTFTDTFTFDTDDADADAFADFATDDFTAAAFDTTADFAAATDFDTDFADDDTDFFT",
        "ADFTTAFDTTTTFATTADTAAATFTFTATDAAAFDDADATATDTDTTDFDTDATADADTDFFTFA",
        "AADTFFDFTDADTAADAATFDTDDAAADDTDTTDAFADADDDTFFFDDTTTADFAAADFTDAADA"
    };
    
    public void testBarProduction() throws Exception {
        for (int i = 0; i < EXAMPLE_MODIFIED.length; i++) {
            String bars = USPSIntelligentMailLogicImpl.convertToBars(EXAMPLE_CHARS[i]);
            bars = bars.replace('0', 'T');
            bars = bars.replace('1', 'A');
            bars = bars.replace('2', 'D');
            bars = bars.replace('3', 'F');
            assertEquals(EXAMPLE_BARS[i], bars);
        }
    }
    
    public void testDefaults() throws Exception {
        USPSIntelligentMailBean bean = new USPSIntelligentMailBean();
        bean.verifySettings();
    }
    
    public void testDefaultsInXML() throws Exception {
        USPSIntelligentMailBean refBean = new USPSIntelligentMailBean();
        
        USPSIntelligentMail gen = new USPSIntelligentMail();
        DefaultConfiguration cfg = new DefaultConfiguration("usps4cb");
        gen.configure(cfg);
        USPSIntelligentMailBean xmlBean = gen.getUSPSIntelligentMailBean();
        assertEquals(refBean.getAscenderHeight(), xmlBean.getAscenderHeight(), 0.01);
        assertEquals(refBean.getBarHeight(), xmlBean.getBarHeight(), 0.01);
        assertEquals(refBean.getFontSize(), xmlBean.getFontSize(), 0.01);
        assertEquals(refBean.getHeight(), xmlBean.getHeight(), 0.01);
        assertEquals(refBean.getHumanReadableHeight(), xmlBean.getHumanReadableHeight(), 0.01);
        assertEquals(refBean.getIntercharGapWidth(), xmlBean.getIntercharGapWidth(), 0.01);
        assertEquals(refBean.getModuleWidth(), xmlBean.getModuleWidth(), 0.01);
        assertEquals(refBean.getQuietZone(), xmlBean.getQuietZone(), 0.01);
        assertEquals(refBean.getTrackHeight(), xmlBean.getTrackHeight(), 0.01);
        assertEquals(refBean.getVerticalQuietZone(), xmlBean.getVerticalQuietZone(), 0.01);
        assertEquals(refBean.hasQuietZone(), xmlBean.hasQuietZone());
        assertEquals(refBean.getChecksumMode(), xmlBean.getChecksumMode());
        assertEquals(refBean.getMsgPosition(), xmlBean.getMsgPosition());
        assertEquals(refBean.getPattern(), xmlBean.getPattern());
    }
    
    public void testLogic() throws Exception {
        final String[] formatted = new String[] {
                "01 234 567094 987654321",
                "01;234;567094;987654321;01234",
                "01,234,567094,987654321,012345678",
                "01 234 567094 987654321 01234 567891"
            };
        final String[] results = new String[] {
                "<BC>B1B0B0B3B1B0B0B2B0B0B1B2B0B1B1B0B0B2B0B2B0B1B0B0B2B1B3B2B2B3B1B2B3B2B3"
                    + "B0B3B3B3B3B3B0B1B0B3B1B1B1B1B0B2B3B3B0B2B1B1B2B3B0B3B2B0B2B0</BC>",
                "<BC>B2B0B0B1B3B1B2B2B0B0B3B0B2B0B3B0B3B2B0B2B2B1B2B1B2B1B3B1B2B3B1B0B2B2B3"
                    + "B0B1B1B1B3B2B0B0B1B2B3B1B1B1B0B2B3B2B0B2B3B1B2B2B2B0B2B3B3B0</BC>",
                "<BC>B1B2B3B0B0B1B3B2B0B0B0B0B3B1B0B0B1B2B0B1B1B1B0B3B0B3B0B1B0B2B1B1B1B3B2"
                    + "B2B1B2B1B0B1B0B2B0B2B0B0B2B3B2B0B2B1B0B1B2B1B2B0B2B3B3B0B3B1</BC>",
                "<BC>B1B1B2B0B3B3B2B3B0B2B1B2B0B1B1B2B1B1B0B3B2B0B2B2B1B1B1B2B2B0B2B0B0B2B1"
                    + "B3B1B2B1B2B2B2B0B3B3B3B2B2B0B0B0B1B2B3B1B1B1B2B3B0B2B1B1B2B1</BC>"
            };

        for (int i = 0; i < formatted.length; i++) {
            USPSIntelligentMailLogicImpl logic = new USPSIntelligentMailLogicImpl();
            StringBuffer sb = new StringBuffer();
            logic.generateBarcodeLogic(new MockClassicBarcodeLogicHandler(sb), formatted[i]);
            String expected = results[i];
            //System.out.println(expected);
            //System.out.println(sb.toString());
            assertEquals("Expected result n° " + i + " does not match", expected, sb.toString());
        }
    }
    
    private static final char[] DIGITS 
        = {'0', '1', '2', '3', '4', '5', '6', '7',
           '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    
    private String toHex(byte[] data) {
        final StringBuffer sb = new StringBuffer(data.length * 2);
        int start = data.length - 13;
        for (int i = start; i < data.length; i++) {
            if (i > start) {
                sb.append(' ');
            }
            if (i < 0) {
                sb.append("00");
            } else {
                sb.append(DIGITS[(data[i] >>> 4) & 0x0F]);
                sb.append(DIGITS[data[i] & 0x0F]);
            }
        }
        return sb.toString();
    }
    
}
