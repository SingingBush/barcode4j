/*
 * $Id: CodabarLogicImpl.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the Codabar barcode.
 * 
 * @author Jeremias Maerki
 * @todo Complete the implementation (checksum, automatic start/stops chars...)
 */
public class CodabarLogicImpl {

    private static final char[] CHARACTERS = 
                            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
                             'a', 'b', 'c', 'd', 'e', 'n', 't', 
                             '-', '$', ':', '/', '.', '+', '*'};

    /** Defines the Codabar character set. */
    protected static final byte[][] CHARSET = 
                                            {{0, 0, 0, 0, 0, 1, 1},  //0
                                             {0, 0, 0, 0, 1, 1, 0},  //1
                                             {0, 0, 0, 1, 0, 0, 1},  //2
                                             {1, 1, 0, 0, 0, 0, 0},  //3
                                             {0, 0, 1, 0, 0, 1, 0},  //4
                                             {1, 0, 0, 0, 0, 1, 0},  //5
                                             {0, 1, 0, 0, 0, 0, 1},  //6
                                             {0, 1, 0, 0, 1, 0, 0},  //7
                                             {0, 1, 1, 0, 0, 0, 0},  //8
                                             {1, 0, 0, 1, 0, 0, 0},  //9
                                             {0, 0, 1, 1, 0, 1, 0},  //a
                                             {0, 1, 0, 1, 0, 0, 1},  //b
                                             {0, 0, 0, 1, 0, 1, 1},  //c
                                             {0, 0, 0, 1, 1, 1, 0},  //d
                                             {0, 0, 0, 1, 1, 1, 0},  //e
                                             {0, 1, 0, 1, 0, 0, 1},  //n
                                             {0, 0, 1, 1, 0, 1, 0},  //t
                                             {0, 0, 0, 1, 1, 0, 0},  //-
                                             {0, 0, 1, 1, 0, 0, 0},  //$
                                             {1, 0, 0, 0, 1, 0, 1},  //:
                                             {1, 0, 1, 0, 0, 0, 1},  ///
                                             {1, 0, 1, 0, 1, 0, 0},  //.
                                             {0, 0, 1, 0, 1, 0, 1},  //+
                                             {0, 0, 0, 1, 0, 1, 1}}; //*

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    
    /**
     * Main constructor
     * @param mode Determines how checksums are to be treated.
     */
    public CodabarLogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the checksum mode.
     * @return the current checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Returns the index of a character within the character set.
     * @param ch the character to lookup
     * @return the index of the character or -1 if it isn't supported
     */
    protected static int getCharIndex(char ch) {
        for (int i = 0; i < CHARACTERS.length; i++) {
            if (ch == CHARACTERS[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines whether a character is a valid message character.
     * @param ch the character to check
     * @return true if it is a valid character, false otherwise
     */
    protected static boolean isValidChar(char ch) {
        return (getCharIndex(ch) >= 0);
    }
    
    /**
     * Determines whether a character is on of the start/stop characters.
     * @param ch the character to check
     * @return true if it is a start/stop character
     */
    protected static boolean isStartStopChar(char ch) {
        return ((ch == 'a') || (ch == 'b') 
             || (ch == 'c') || (ch == 'd') 
             || (ch == 'e') || (ch == '*') 
             || (ch == 'n') || (ch == 't'));
    }

    private int widthAt(char ch, int index) throws IllegalArgumentException {
        int chidx = getCharIndex(ch);
        if (chidx >= 0) {
            int binary = CHARSET[chidx][index];
            return binary + 1;
        } else {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }
    }

    /**
     * Encodes a character to a logic handler.
     * @param logic the logic handler to send events to
     * @param c the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char c) {
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(c).toString());
        for (byte i = 0; i < 7; i++) {
            final int width = widthAt(c, i);
            final boolean black = ((i % 2) == 0);
            logic.addBar(black, width);
        }
        logic.endBarGroup();
    }

    private void handleChecksum(StringBuffer sb) {
        if ((getChecksumMode() == ChecksumMode.CP_ADD)
                || (getChecksumMode() == ChecksumMode.CP_CHECK)) {
            throw new UnsupportedOperationException(
                "No checksums are currently supported for Codabar symbols");
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return;
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return; //equals ignore
        }
    }
    
    /**
     * Generates the barcode logic.
     * @param logic the logic handler to send generated events to
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        StringBuffer sb = new StringBuffer(msg);
        
        handleChecksum(sb);
        
        logic.startBarcode(sb.toString());

        for (int i = 0; i < sb.length(); i++) {
            if (i > 0) {
                //Intercharacter gap
                logic.addBar(false, 1);
            }
            final char ch = sb.charAt(i);
            if (!isValidChar(ch)) throw new IllegalArgumentException("Invalid character: " + ch);
            encodeChar(logic, ch);
        }

        logic.endBarcode();
    }

}
