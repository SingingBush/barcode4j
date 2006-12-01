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

/* $Id: DataMatrixHighLevelEncodeTest.java,v 1.2 2006-12-01 13:31:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the high-level encoder.
 * 
 * @version $Id: DataMatrixHighLevelEncodeTest.java,v 1.2 2006-12-01 13:31:11 jmaerki Exp $
 */
public class DataMatrixHighLevelEncodeTest extends TestCase {

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
        //230 shifts to C40 encoding, 254 unlatches

        visualized = encodeHighLevel("AbAIMAI");
        assertEquals("230 87 211 91 11 90 241 254", visualized);

        visualized = encodeHighLevel("AIMAIMAIMË");
        assertEquals("230 91 11 91 11 91 11 11 9 254", visualized);

        visualized = encodeHighLevel("AIMAIMAIMë");
        assertEquals("230 91 11 91 11 91 11 10 243 254 235 107", visualized);
    }

    private String encodeHighLevel(String msg) {
        String encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg);
        String visualized = TestHelper.visualize(encoded);
        //System.out.println(msg + ": " + visualized);
        return visualized;
    }
    
}
