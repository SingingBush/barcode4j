/*
 * Copyright 2006 Jeremias Maerki
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

/* $Id: SymbolInfoTest.java,v 1.1 2006-12-22 15:58:27 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import junit.framework.TestCase;

/**
 * Tests the SymbolInfo class.
 */
public class SymbolInfoTest extends TestCase {

    public void testSymbolInfo() throws Exception {
        DataMatrixSymbolInfo info;
        info = DataMatrixSymbolInfo.lookup(3);
        assertEquals(5, info.errorCodewords);
        assertEquals(8, info.matrixWidth);
        assertEquals(8, info.matrixHeight);
        assertEquals(10, info.getSymbolWidth());
        assertEquals(10, info.getSymbolHeight());

        info = DataMatrixSymbolInfo.lookup(9);
        assertEquals(11, info.errorCodewords);
        assertEquals(14, info.matrixWidth);
        assertEquals(6, info.matrixHeight);
        assertEquals(32, info.getSymbolWidth());
        assertEquals(8, info.getSymbolHeight());
    }
    
}
