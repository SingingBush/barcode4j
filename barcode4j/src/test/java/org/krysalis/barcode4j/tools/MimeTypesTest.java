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
 * Tests for the MimeTypes class.
 * 
 * @author Jeremias Maerki
 * @version $Id: MimeTypesTest.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
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
