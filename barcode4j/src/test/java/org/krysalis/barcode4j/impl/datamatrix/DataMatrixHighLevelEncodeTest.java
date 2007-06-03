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

/* $Id: DataMatrixHighLevelEncodeTest.java,v 1.8 2007-06-03 08:24:10 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the high-level encoder.
 * 
 * @version $Id: DataMatrixHighLevelEncodeTest.java,v 1.8 2007-06-03 08:24:10 jmaerki Exp $
 */
public class DataMatrixHighLevelEncodeTest extends TestCase {

    private static final boolean DEBUG = false;
    
    public static final DataMatrixSymbolInfo[] TEST_SYMBOLS = new DataMatrixSymbolInfo[] {
        new DataMatrixSymbolInfo(false, 3, 5, 8, 8, 1),
        new DataMatrixSymbolInfo(false, 5, 7, 10, 10, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 5, 7, 16, 6, 1),
        new DataMatrixSymbolInfo(false, 8, 10, 12, 12, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 10, 11, 14, 6, 2),
        new DataMatrixSymbolInfo(false, 13, 0, 0, 0, 1),
        new DataMatrixSymbolInfo(false, 77, 0, 0, 0, 1)
        //The last entries are fake entries to test special conditions with C40 encoding
    };

    public void useTestSymbols() {
        DataMatrixSymbolInfo.overrideSymbolSet(TEST_SYMBOLS);
    }

    public void resetSymbols() {
        DataMatrixSymbolInfo.overrideSymbolSet(DataMatrixSymbolInfo.PROD_SYMBOLS);
    }
    
    public void testASCIIEncodation() throws Exception {
        String visualized;

        visualized = encodeHighLevel("123456");
        assertEquals("142 164 186", visualized);

        visualized = encodeHighLevel("123456£");
        assertEquals("142 164 186 235 36", visualized);

        visualized = encodeHighLevel("30Q324343430794<OQQ");
        assertEquals("160 82 162 173 173 173 137 224 61 80 82 82", visualized);
    }
    
    public void testC40EncodationBasic1() throws Exception {
        String visualized;

        visualized = encodeHighLevel("AIMAIMAIM");
        assertEquals("230 91 11 91 11 91 11 254", visualized);
        //230 shifts to C40 encodation, 254 unlatches, "else" case
    }
    
    public void testC40EncodationBasic2() throws Exception {
        String visualized;
        
        visualized = encodeHighLevel("AIMAIAB");
        assertEquals("230 91 11 90 255 254 67 129", visualized);
        //"B" is normally encoded as "15" (one C40 value)
        //"else" case: "B" is encoded as ASCII
        
        visualized = encodeHighLevel("AIMAIAb");
        assertEquals("66 74 78 66 74 66 99 129", visualized); //Encoded as ASCII
        //Alternative solution:
        //assertEquals("230 91 11 90 255 254 99 129", visualized);
        //"b" is normally encoded as "Shift 3, 2" (two C40 values)
        //"else" case: "b" is encoded as ASCII

        visualized = encodeHighLevel("AIMAIMAIMË");
        assertEquals("230 91 11 91 11 91 11 254 235 76", visualized);
        //Alternative solution:
        //assertEquals("230 91 11 91 11 91 11 11 9 254", visualized);
        //Expl: 230 = shift to C40, "91 11" = "AIM",
        //"11 9" = "Ë" = "Shift 2, UpperShift, <char>
        //"else" case

        visualized = encodeHighLevel("AIMAIMAIMë");
        assertEquals("230 91 11 91 11 91 11 254 235 108", visualized); //Activate when additional rectangulars are available
        //Expl: 230 = shift to C40, "91 11" = "AIM",
        //"ë" in C40 encodes to: 1 30 2 11 which doesn't fit into a triplet
        //"10 243" = 
        //254 = unlatch, 235 = Upper Shift, 108 = ë = 0xEB/235 - 128 + 1
        //"else" case
    }
    
    public void testC40EncodationSpecExample() throws Exception {
        String visualized;
        //Example in Figure 1 in the spec
        visualized = encodeHighLevel("A1B2C3D4E5F6G7H8I9J0K1L2");
        assertEquals("230 88 88 40 8 107 147 59 67 126 206 78 126 144 121 35 47 254", visualized);
    }
    
    public void testC40EncodationSpecialCases1() throws Exception {
        String visualized;

        //Special tests avoiding ultra-long test strings because these tests are only used
        //with the 16x48 symbol (47 data codewords)
        useTestSymbols(); 
        
        visualized = encodeHighLevel("AIMAIMAIMAIMAIMAIM");
        assertEquals("230 91 11 91 11 91 11 91 11 91 11 91 11", visualized);
        //case "a": Unlatch is not required

        visualized = encodeHighLevel("AIMAIMAIMAIMAIMAI");
        assertEquals("230 91 11 91 11 91 11 91 11 91 11 90 241", visualized);
        //case "b": Add trailing shift 0 and Unlatch is not required

        visualized = encodeHighLevel("AIMAIMAIMAIMAIMA");
        assertEquals("230 91 11 91 11 91 11 91 11 91 11 254 66", visualized);
        //case "c": Unlatch and write last character in ASCII
        
        resetSymbols();

        visualized = encodeHighLevel("AIMAIMAIMAIMAIMAI");
        assertEquals("230 91 11 91 11 91 11 91 11 91 11 254 66 74 129 237", visualized);
        
        visualized = encodeHighLevel("AIMAIMAIMA");
        assertEquals("230 91 11 91 11 91 11 66", visualized);
        //case "d": Skip Unlatch and write last character in ASCII
    }
    
    public void testC40EncodationSpecialCases2() throws Exception {
        String visualized;

        visualized = encodeHighLevel("AIMAIMAIMAIMAIMAIMAI");
        assertEquals("230 91 11 91 11 91 11 91 11 91 11 91 11 254 66 74", visualized);
        //available > 2, rest = 2 --> unlatch and encode as ASCII
    }

    public void testTextEncodation() throws Exception {
        String visualized;

        visualized = encodeHighLevel("aimaimaim");
        assertEquals("239 91 11 91 11 91 11 254", visualized);
        //239 shifts to Text encodation, 254 unlatches

        visualized = encodeHighLevel("aimaimaim'");
        assertEquals("239 91 11 91 11 91 11 254 40 129", visualized);
        //assertEquals("239 91 11 91 11 91 11 7 49 254", visualized);
        //This is an alternative, but doesn't strictly follow the rules in the spec.

        visualized = encodeHighLevel("aimaimaIm");
        assertEquals("239 91 11 91 11 87 218 110", visualized);

        visualized = encodeHighLevel("aimaimaimB");
        assertEquals("239 91 11 91 11 91 11 254 67 129", visualized);
    }

    public void testX12Encodation() throws Exception {
        String visualized;

        //238 shifts to X12 encodation, 254 unlatches

        visualized = encodeHighLevel("ABC>ABC123>AB");
        assertEquals("238 89 233 14 192 100 207 44 31 67", visualized);

        visualized = encodeHighLevel("ABC>ABC123>ABC");
        assertEquals("238 89 233 14 192 100 207 44 31 254 67 68", visualized);

        visualized = encodeHighLevel("ABC>ABC123>ABCD");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 254", visualized);
        
        visualized = encodeHighLevel("ABC>ABC123>ABCDE");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 70", visualized);
        
        visualized = encodeHighLevel("ABC>ABC123>ABCDEF");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 254 70 71 129 237", visualized);
        
    }
    
    public void testEDIFACTEncodation() throws Exception {
        String visualized;

        //240 shifts to EDIFACT encodation

        visualized = encodeHighLevel(".A.C1.3.DATA.123DATA.123DATA");
        assertEquals("240 184 27 131 198 236 238 16 21 1 187 28 179 16 21 1 187 28 179 16 21 1", 
                visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2..");
        assertEquals("240 184 27 131 198 236 238 98 230 50 47 47", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2.");
        assertEquals("240 184 27 131 198 236 238 98 230 50 47 129", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2");
        assertEquals("240 184 27 131 198 236 238 98 230 50", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X");
        assertEquals("240 184 27 131 198 236 238 98 230 31", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.");
        assertEquals("240 184 27 131 198 236 238 98 231 192", visualized);

        visualized = encodeHighLevel(".A.C1.3.X");
        assertEquals("240 184 27 131 198 236 238 89", visualized);

        //Checking temporary unlatch from EDIFACT
        visualized = encodeHighLevel(".XXX.XXX.XXX.XXX.XXX.XXX.üXX.XXX.XXX.XXX.XXX.XXX.XXX");
        assertEquals("240 185 134 24 185 134 24 185 134 24 185 134 24 185 134 24 185 134 24"
                + " 124 47 235 125 240" //<-- this is the temporary unlatch
                + " 97 139 152 97 139 152 97 139 152 97 139 152 97 139 152 97 139 152 89 89", 
                    visualized);
    }

    public void testBase256Encodation() throws Exception {
        String visualized;

        //231 shifts to Base256 encodation

        visualized = encodeHighLevel("«äöüé»");
        assertEquals("231 44 108 59 226 126 1 104", visualized);
        visualized = encodeHighLevel("«äöüéà»");
        assertEquals("231 51 108 59 226 126 1 141 254 254", visualized);

        visualized = encodeHighLevel(" 23£"); //ASCII only (for reference)
        assertEquals("33 153 235 36 129", visualized);
        
        visualized = encodeHighLevel("«äöüé» 234"); //Mixed Base256 + ASCII
        assertEquals("231 51 108 59 226 126 1 104 99 153 53 129", visualized);
        
        visualized = encodeHighLevel("«äöüé» 23£ 1234567890123456789");
        assertEquals("231 55 108 59 226 126 1 104 99 10 161 167 185 142 164 186 208 220 142 164 186 208 58 129 59 209 104 254 150 45", visualized);
    }
    
    public void testUnlatchingFromC40() throws Exception {
        String visualized;

        visualized = encodeHighLevel("AIMAIMAIMAIMaimaimaim");
        assertEquals("230 91 11 91 11 91 11 254 66 74 78 239 91 11 91 11 91 11", visualized);
    }

    public void testUnlatchingFromText() throws Exception {
        String visualized;

        visualized = encodeHighLevel("aimaimaimaim12345678");
        assertEquals("239 91 11 91 11 91 11 91 11 254 142 164 186 208 129 237", visualized);
    }

    public void testHelloWorld() throws Exception {
        String visualized;

        visualized = encodeHighLevel("Hello World!");
        assertEquals("73 239 116 130 175 123 148 64 158 233 254 34", visualized);
    }

    public void testBug1664266() throws Exception {
        String visualized;
        //There was an exception and the encoder did not handle the unlatching from 
        //EDIFACT encoding correctly
        
        visualized = encodeHighLevel("CREX-TAN:h");
        assertEquals("240 13 33 88 181 64 78 124 59 105", visualized);

        visualized = encodeHighLevel("CREX-TAN:hh");
        assertEquals("240 13 33 88 181 64 78 124 59 105 105 129", visualized);

        visualized = encodeHighLevel("CREX-TAN:hhh");
        assertEquals("240 13 33 88 181 64 78 124 59 105 105 105", visualized);

    }
    
    public void testMacroCharacters() throws Exception {
        String visualized;

        visualized = encodeHighLevel("[)>\u001E05\u001D5555\u001C6666\u001E\u0004");
        //assertEquals("92 42 63 31 135 30 185 185 29 196 196 31 5 129 87 237", visualized);
        assertEquals("236 185 185 29 196 196 129 56", visualized);
    }

    private String encodeHighLevel(String msg) {
        String encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg);
        String visualized = TestHelper.visualize(encoded);
        if (DEBUG) {
            System.out.println(msg + ": " + visualized);
        }
        return visualized;
    }
    
}
