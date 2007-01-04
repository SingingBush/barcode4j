/*
 * Copyright 2007 Jeremias Maerki
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

/* $Id: EAN128Test.java,v 1.1 2007-01-04 08:04:09 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.code128;

import org.krysalis.barcode4j.ChecksumMode;

import junit.framework.TestCase;

/**
 * Tests for EAN 128.
 */
public class EAN128Test extends TestCase {

    public void testAI() throws Exception {
        EAN128AI ret = null;
        ret = EAN128AI.parseSpec("230", "n1");
        assertEquals("(230)n1 (fixed)", ret.toString());
        
        final char FNC1 = Code128LogicImpl.FNC_1;
        final char GS = EAN128Bean.DEFAULT_GROUP_SEPARATOR;
        final char CD = EAN128Bean.DEFAULT_CHECK_DIGIT_MARKER;
        EAN128LogicImpl impl;

        //Test FNC1 position and basic stuff
        impl = new EAN128LogicImpl(ChecksumMode.CP_AUTO, null);
        impl.setMessage("8100712345" + GS + "2112345678");
        assertEquals("(8100)712345(21)12345678", impl.getHumanReadableMsg());
        assertEquals(FNC1 + "8100712345" + FNC1 + "2112345678", impl.getCode128Msg());
        
        //Test check digit generation (cd0)
        impl = new EAN128LogicImpl(ChecksumMode.CP_AUTO, "(420)n5(91)n2+n9+n2-8+cd0");
        impl.setMessage("42012345" + FNC1 + "910112345678912345678" + CD);
        assertEquals("(420)12345(91)01123456789123456780", impl.getHumanReadableMsg());
        assertEquals(FNC1 + "42012345" + FNC1 + "9101123456789123456780", impl.getCode128Msg());
    }
    
}
