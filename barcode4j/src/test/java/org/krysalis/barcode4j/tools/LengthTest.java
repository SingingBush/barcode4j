/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.tools;

import junit.framework.TestCase;

/**
 * Tests the Length class.
 * 
 * @author Jeremias Maerki
 * @version $Id: LengthTest.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
 */
public class LengthTest extends TestCase {
    
    public LengthTest(String name) {
        super(name);
    }
    
    
    public void testLength() throws Exception {
        Length l = new Length(1.77, "cm");
        assertNotNull(l);
        assertEquals(1.77, l.getValue(), 0.001);
        assertEquals("cm", l.getUnit());
        
        l = new Length("1.77cm");
        assertNotNull(l);
        assertEquals(1.77, l.getValue(), 0.001);
        assertEquals("cm", l.getUnit());
        
        l = new Length("1.77 cm");
        assertNotNull(l);
        assertEquals(1.77, l.getValue(), 0.001);
        assertEquals("cm", l.getUnit());
        
        l = new Length("1.77 cm gugus");
        assertNotNull(l);
        assertEquals(1.77, l.getValue(), 0.001);
        assertEquals("cm", l.getUnit());
     
        l = new Length("2,33", "mm");   
        assertNotNull(l);
        assertEquals(2.33, l.getValue(), 0.001);
        assertEquals("mm", l.getUnit());
        
        l = new Length("2,33pt", "mm");   
        assertNotNull(l);
        assertEquals(2.33, l.getValue(), 0.001);
        assertEquals("pt", l.getUnit());

        try {
            l = new Length(null);   
            fail("Expected NPE on null parameter");
        } catch (NullPointerException npe) {
            //expected
        }
        
        try {
            l = new Length("garbage");   
            fail("Expected IllegalArgumentException on garbage parameter");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        
        try {
            l = new Length("2.33");   
            fail("Expected IllegalArgumentException on incomplete parameter");
        } catch (IllegalArgumentException iae) {
            //expected
        }

        l = new Length("2.34cm");
        assertEquals(23.4, l.getValueAsMillimeter(), 0.001);
        
        l = new Length("2.835pt");
        assertEquals(1, l.getValueAsMillimeter(), 0.001);
        
        l = new Length("0.0393700in");
        assertEquals(1, l.getValueAsMillimeter(), 0.001);
    }

}
