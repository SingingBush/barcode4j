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

/* $Id: DataMatrixHighLevelEncodeTest.java,v 1.4 2006-12-09 13:01:26 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the high-level encoder.
 * 
 * @version $Id: DataMatrixHighLevelEncodeTest.java,v 1.4 2006-12-09 13:01:26 jmaerki Exp $
 */
public class DataMatrixHighLevelEncodeTest extends TestCase {

    private static final boolean DEBUG = true;
    
    public void testASCIIEncodation() throws Exception {
        String visualized;

        visualized = encodeHighLevel("123456");
        assertEquals("142 164 186", visualized);

        visualized = encodeHighLevel("30Q324343430794<OQQ");
        assertEquals("160 82 162 173 173 173 137 224 61 80 82 82", visualized);
    }
    
    public void testC40Encodation() throws Exception {
        String visualized;

        visualized = encodeHighLevel("AIMAIMAIM");
        assertEquals("230 91 11 91 11 91 11 254", visualized);
        //230 shifts to C40 encodation, 254 unlatches

        visualized = encodeHighLevel("AIMAIAb");
        assertEquals("230 91 11 90 255 12 209 254", visualized);

        visualized = encodeHighLevel("AIMAIMAIMË");
        assertEquals("230 91 11 91 11 91 11 11 9 254 129 147", visualized);

        visualized = encodeHighLevel("AIMAIMAIMë");
        assertEquals("230 91 11 91 11 91 11 10 243 254 235 107", visualized);

        visualized = encodeHighLevel("A1B2C3D4E5F6G7H8I9J0K1L2");
        assertEquals("230 88 88 40 8 107 147 59 67 126 206 78 126 144 121 35 47 254", visualized);
    }

    public void testTextEncodation() throws Exception {
        String visualized;

        visualized = encodeHighLevel("aimaimaim");
        assertEquals("239 91 11 91 11 91 11 254", visualized);
        //239 shifts to Text encodation, 254 unlatches

        visualized = encodeHighLevel("aimaimaim'");
        assertEquals("239 91 11 91 11 91 11 7 49 254 129 147", visualized);

        visualized = encodeHighLevel("aimaimaIm");
        assertEquals("239 91 11 91 11 87 218 254 110 129 251 147", visualized);

        visualized = encodeHighLevel("aimaimaimB");
        assertEquals("239 91 11 91 11 91 11 12 209 254 129 147", visualized);
    }

    public void testX12Encodation() throws Exception {
        String visualized;

        //238 shifts to X12 encodation, 254 unlatches

        visualized = encodeHighLevel("ABC>ABC123>AB");
        assertEquals("238 89 233 14 192 100 207 44 31 254 67 129", visualized);

        visualized = encodeHighLevel("ABC>ABC123>ABC");
        assertEquals("238 89 233 14 192 100 207 44 31 254 67 68", visualized);

        visualized = encodeHighLevel("ABC>ABC123>ABCD");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 254", visualized);
        
        visualized = encodeHighLevel("ABC>ABC123>ABCDE");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 70", visualized);
        
        visualized = encodeHighLevel("ABC>ABC123>ABCDEF");
        assertEquals("238 89 233 14 192 100 207 44 31 96 82 254 70 71 129 237 133 28", visualized);
        
    }
    
    public void testEDIFACTEncodation() throws Exception {
        String visualized;

        //240 shifts to EDIFACT encodation

        visualized = encodeHighLevel(".A.C1.3.DATA.123DATA.123DATA");
        assertEquals("240 184 27 131 198 236 238 16 21 1 187 28 179 16 21 1 187 28 179 16 21 1", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2..");
        assertEquals("240 184 27 131 198 236 238 98 230 50 47 47", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2.");
        assertEquals("240 184 27 131 198 236 238 98 230 50 47 129", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X2");
        assertEquals("240 184 27 131 198 236 238 98 230 50 129 147", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.X");
        assertEquals("240 184 27 131 198 236 238 98 230 31 129 147", visualized);

        visualized = encodeHighLevel(".A.C1.3.X.");
        assertEquals("240 184 27 131 198 236 238 98 231 192 129 147", visualized);

        visualized = encodeHighLevel(".A.C1.3.X");
        assertEquals("240 184 27 131 198 236 238 89", visualized);

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
