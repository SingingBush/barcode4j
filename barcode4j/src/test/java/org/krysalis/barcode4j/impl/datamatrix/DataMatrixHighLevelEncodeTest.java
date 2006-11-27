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

/* $Id: DataMatrixHighLevelEncodeTest.java,v 1.1 2006-11-27 08:12:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests for the high-level encoder.
 * 
 * @version $Id: DataMatrixHighLevelEncodeTest.java,v 1.1 2006-11-27 08:12:11 jmaerki Exp $
 */
public class DataMatrixHighLevelEncodeTest extends TestCase {

    public void testASCIIEncodation() throws Exception {
        String encoded = DataMatrixHighLevelEncoder.encodeHighLevel("123456");
        assertEquals("142 164 186", TestHelper.visualize(encoded));

        encoded = DataMatrixHighLevelEncoder.encodeHighLevel("30Q324343430794<OQQ");
        assertEquals("160 82 162 173 173 173 137 224 61 80 82 82", TestHelper.visualize(encoded));
    }
    
}
