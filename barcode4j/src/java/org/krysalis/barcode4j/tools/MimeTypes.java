/*
 * $Id: MimeTypes.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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

/**
 * Defines MIME types used in Barcode4J.
 * 
 * @author Jeremias Maerki
 */
public class MimeTypes {

    /** SVG MIME type: image/svg+xml */
    public static final String MIME_SVG  = "image/svg+xml";
    /** EPS MIME type: image/x-eps */
    public static final String MIME_EPS  = "image/x-eps";
    /** TIFF MIME type: image/tiff */
    public static final String MIME_TIFF = "image/tiff";
    /** JPEG MIME type: image/jpeg */
    public static final String MIME_JPEG = "image/jpeg";
    /** PNG MIME type: image/x-png */
    public static final String MIME_PNG  = "image/x-png";
    /** GIF MIME type: image/gif */
    public static final String MIME_GIF  = "image/gif";

    private static final String[][] FORMAT_MAPPINGS =
            {{"svg", MIME_SVG},
             {"eps", MIME_EPS},
             {"image/eps", MIME_EPS},
             {"tif", MIME_TIFF},
             {"tiff", MIME_TIFF},
             {"jpg", MIME_JPEG},
             {"jpeg", MIME_JPEG},
             {"png", MIME_PNG},
             {"image/png", MIME_PNG},
             {"gif", MIME_GIF}};

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected MimeTypes() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Converts a short format name, such as "svg" or "eps", to its MIME type,
     * if necessary. Known and unknown MIME types are passed through.
     * @param format short format name or MIME type
     * @return MIME type
     */
    public static String expandFormat(String format) {
        if (format == null || format.length() == 0) {
            return null;
        }
        for (int i = 0; i < FORMAT_MAPPINGS.length; i++) {
            if (format.equalsIgnoreCase(FORMAT_MAPPINGS[i][0])
                || format.equals(FORMAT_MAPPINGS[i][1])) {
                return FORMAT_MAPPINGS[i][1];
            }
        }
        return format.toLowerCase();
    }

    /**
     * Indicates whether a format is a bitmap format.
     * @param format short format name or MIME type
     * @return true if format is a bitmap format
     */
    public static boolean isBitmapFormat(String format) {
        String fmt = expandFormat(format);
        if (fmt == null) {
            return false;
        }
        return (fmt.equals(MIME_JPEG)
            || fmt.equals(MIME_TIFF)
            || fmt.equals(MIME_PNG)
            || fmt.equals(MIME_GIF));
    }

}
