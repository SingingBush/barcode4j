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

/* $Id: ECC200Test.java,v 1.1 2006-11-27 08:12:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the ECC200 error correction.
 * 
 * @version $Id: ECC200Test.java,v 1.1 2006-11-27 08:12:11 jmaerki Exp $
 */
public class ECC200Test extends TestCase {


    public void testRS() throws Exception {
        //Sample from Annexe R in ISO/IEC 16022:2000(E)
        char[] cw = new char[] {142, 164, 186};
        String s = DataMatrixErrorCorrection.encodeECC200(String.valueOf(cw), 5);
        assertEquals("114 25 5 88 102", TestHelper.visualize(s));
    }
    
}
