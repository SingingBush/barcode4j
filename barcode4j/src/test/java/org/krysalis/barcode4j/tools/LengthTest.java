/*
 * $Id: LengthTest.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.tools;

import junit.framework.TestCase;

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
