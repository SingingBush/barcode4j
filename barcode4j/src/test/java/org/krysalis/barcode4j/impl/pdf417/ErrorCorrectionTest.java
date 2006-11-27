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

/* $Id: ErrorCorrectionTest.java,v 1.2 2006-11-27 08:12:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the error correction code.
 * 
 * @version $Id: ErrorCorrectionTest.java,v 1.2 2006-11-27 08:12:11 jmaerki Exp $
 */
public class ErrorCorrectionTest extends TestCase {

    public void testErrorCorrection() throws Exception {
        String data = "\u0005\u01c5\u00b2\u0079\u00ef";
        assertEquals("5 453 178 121 239", TestHelper.visualize(data));
        int errorCorrectionLevel = 1;
        String ec = PDF417ErrorCorrection.generateErrorCorrection(data, errorCorrectionLevel);
        String expected = "452 327 657 619";
        assertEquals(expected, TestHelper.visualize(ec));
    }
    
    
}
