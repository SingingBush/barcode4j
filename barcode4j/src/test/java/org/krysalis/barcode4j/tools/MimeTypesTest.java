/*
 * $Id: MimeTypesTest.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
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

/**
 * Tests for the MimeTypes class.
 * 
 * @author Jeremias Maerki
 */
public class MimeTypesTest extends TestCase {

    public MimeTypesTest(String name) {
        super(name);
    }

    public void testExpandFormat() throws Exception {
        assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat("svg"));
        assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat("sVG"));
        assertEquals(MimeTypes.MIME_SVG, MimeTypes.expandFormat(MimeTypes.MIME_SVG));
        assertEquals(MimeTypes.MIME_EPS, MimeTypes.expandFormat("EPS"));
        assertEquals("image/bmp", MimeTypes.expandFormat("image/bmp"));
        assertEquals("anything", MimeTypes.expandFormat("anything"));
        assertNull(MimeTypes.expandFormat(""));
        assertNull(MimeTypes.expandFormat(null));
    }
    
    public void testIsBitmapFormat() throws Exception {
        assertTrue(MimeTypes.isBitmapFormat("tiff"));
        assertTrue(MimeTypes.isBitmapFormat("tif"));
        assertTrue(MimeTypes.isBitmapFormat("jpeg"));
        assertTrue(MimeTypes.isBitmapFormat("jpg"));
        assertTrue(MimeTypes.isBitmapFormat("gif"));
        assertTrue(MimeTypes.isBitmapFormat("png"));
        assertTrue(MimeTypes.isBitmapFormat("image/png"));
        assertTrue(MimeTypes.isBitmapFormat("image/x-png"));
        assertFalse(MimeTypes.isBitmapFormat("svg"));
        assertFalse(MimeTypes.isBitmapFormat("eps"));
    }

}
