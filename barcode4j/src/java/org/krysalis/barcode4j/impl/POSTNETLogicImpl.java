/*
 * $Id: POSTNETLogicImpl.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2003 Nicola Ken Barozzi.  All rights reserved.
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
 * Implements the United States Postal Service Postnet barcode.
 * 
 * @author Chris Dolphy
 */
public class POSTNETLogicImpl {

    private static final byte[][] CHARSET = 
                        {{2, 2, 1, 1, 1},  //0
                         {1, 1, 1, 2, 2},  //1
                         {1, 1, 2, 1, 2},  //2
                         {1, 1, 2, 2, 1},  //3
                         {1, 2, 1, 1, 2},  //4
                         {1, 2, 1, 2, 1},  //5
                         {1, 2, 2, 1, 1},  //6
                         {2, 1, 1, 1, 2},  //7
                         {2, 1, 1, 2, 1},  //8
                         {2, 1, 2, 1, 1}}; //9

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private static final char DASH = '-';

    /**
     * Main constructor
     * @param mode checksum mode
     */
    public POSTNETLogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the currently active checksum mode.
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * Code39 barcode.
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        int tmp = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (isIgnoredChar(msg.charAt(i))) {
                continue;
            }
            tmp += Character.digit(msg.charAt(i), 10);
            if (tmp > 9) {
                tmp -= 10;
            }
        }
        return Character.forDigit((10 - tmp) % 10, 10);
    }


    /**
     * Verifies the checksum for a message.
     * @param msg message (check digit included)
     * @return boolean True, if the checksum is correct
     */
    public static boolean validateChecksum(String msg) {
        char actual = msg.charAt(msg.length() - 1);
        char expected = calcChecksum(msg.substring(0, msg.length() - 1));
        return (actual == expected);
    }

    private static boolean isValidChar(char ch) {
        return Character.isDigit(ch) || isIgnoredChar(ch);
    }
    
    /**
     * Checks if a character is an ignored character (such as a '-' (dash)).
     * @param c character to check
     * @return True if the character is ignored
     */
    public static boolean isIgnoredChar(char c) {
        return c == DASH;
    }
    
    /**
     * Removes ignored character from a valid POSTNET message.
     * @param msg valid POSTNET message
     * @return the message but without ignored characters
     */
    public static String removeIgnoredCharacters(final String msg) {
        StringBuffer sb = new StringBuffer(msg.length());
        for (int i = 0; i < msg.length(); i++) {
            final char ch = msg.charAt(i);
            if (!isValidChar(ch)) {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
            if (!isIgnoredChar(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private int heightAt(char ch, int index) {
        int chidx = Character.digit(ch, 10);
        if (chidx >= 0) {
            int height = CHARSET[chidx][index];
            return height;
        } else {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }
    }

    /**
     * Encodes a single character.
     * @param logic the logic handler to receive generated events
     * @param c the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char c) {
        if (isIgnoredChar(c)) {
            return;  // allow dash, but don't encode
        }
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(c).toString());
        for (byte i = 0; i < 5; i++) {
            int height = heightAt(c, i);
            logic.addBar(true, height);
            addIntercharacterGap(logic);
        }
        logic.endBarGroup();
    }

    private void addIntercharacterGap(ClassicBarcodeLogicHandler logic) {
        logic.addBar(false, -1); //-1 is special
    }
        
    private void handleChecksum(StringBuffer sb) {
        if (getChecksumMode() == ChecksumMode.CP_ADD) {
            sb.append(calcChecksum(sb.toString()));
        } else if (getChecksumMode() == ChecksumMode.CP_CHECK) {
            if (!validateChecksum(sb.toString())) {
                throw new IllegalArgumentException("Message '" 
                    + sb.toString()
                    + "' has a bad checksum. Expected: " 
                    + calcChecksum(sb.toString()));
            }
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return;
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return; //equals ignore
        }
    }

    /**
     * Generates the barcode logic
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        logic.startBarcode(msg);

        StringBuffer sb = new StringBuffer(msg);

        handleChecksum(sb);

        // start frame bar
        logic.addBar(true, 2);
        addIntercharacterGap(logic);

        // encode message
        for (int i = 0; i < sb.length(); i++) {
            final char ch = sb.charAt(i);
            if (!isValidChar(ch)) {
                throw new IllegalArgumentException("Invalid character: " + ch);
            } 
            encodeChar(logic, ch);
        }

        // end frame bar
        logic.addBar(true, 2);

        logic.endBarcode();
    }


}
