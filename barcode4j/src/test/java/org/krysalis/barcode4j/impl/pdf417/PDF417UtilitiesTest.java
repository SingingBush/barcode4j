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

/* $Id: PDF417UtilitiesTest.java,v 1.1 2006-06-22 09:01:17 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import junit.framework.TestCase;

public class PDF417UtilitiesTest extends TestCase {

    public void testSymbolSizeCalculation() throws Exception {
        int k;
        k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(0);
        assertEquals(2, k);
        k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(1);
        assertEquals(4, k);
        k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(8);
        assertEquals(512, k);
        
        //What follows is the example in 4.9.2 in the ISO spec.
        int ecl = 4;
        k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(ecl);
        assertEquals(32, k);
        int m = 246;
        int c = 12;
        
        int r;
        r = PDF417LogicImpl.getNumberOfRows(m, k, c);
        assertEquals(24, r);
        
        int pad;
        pad = PDF417LogicImpl.getNumberOfPadCodewords(m, k, c, r);
        assertEquals(9, pad);
        
        int sld = PDF417LogicImpl.getNumberOfDataCodewords(m, ecl, c);
        assertEquals(256, sld);
    }
    
}
