/*
 * $Id: Interleaved2Of5LogicImpl.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * This class is an implementation of the Interleaved 2 of 5 barcode.
 * 
 * @author Jeremias Maerki
 */
public class Interleaved2Of5LogicImpl {

    private static final byte[][] CHARSET = {{1, 1, 2, 2, 1}, 
                                             {2, 1, 1, 1, 2}, 
                                             {1, 2, 1, 1, 2}, 
                                             {2, 2, 1, 1, 1}, 
                                             {1, 1, 2, 1, 2}, 
                                             {2, 1, 2, 1, 1}, 
                                             {1, 2, 2, 1, 1}, 
                                             {1, 1, 1, 2, 2}, 
                                             {2, 1, 1, 2, 1}, 
                                             {1, 2, 1, 2, 1}};

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    
    /**
     * Main constructor.
     * @param mode the checksum mode
     */
    public Interleaved2Of5LogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the current checksum mode
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * @param msg message to calculate the check digit for
     * @param oddMultiplier multiplier to be used for odd positions (usually 3 or 4)
     * @param evenMultiplier multiplier to be used for even positions (usually 1 or 9)
     * @return char the check digit
     */
    public static char calcChecksum(String msg, int oddMultiplier, int evenMultiplier) {
        int oddsum = 0;
        int evensum = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (i % 2 == 0) {
                evensum += Character.digit(msg.charAt(i), 10);
            } else {
                oddsum += Character.digit(msg.charAt(i), 10);
            }
        }
        int check = 10 - ((evensum * oddMultiplier + oddsum * evenMultiplier) % 10);
        if (check >= 10) check = 0;
        return Character.forDigit(check, 10);
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * This method uses the default specification 
     * (ITF-14, EAN-14, SSC-14, DUN14 and USPS).
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        return calcChecksum(msg, 3, 1);
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

    private int widthAt(char ch, int index) {
        if (Character.isDigit(ch)) {
            int digit = Character.digit(ch, 10);
            int width = CHARSET[digit][index];
            return width;
        } else {
            throw new IllegalArgumentException("Invalid character '" + ch 
                    + " (" + Character.getNumericValue(ch) 
                    + ")'. Expected a digit.");
        }
    }

    private void encodeGroup(ClassicBarcodeLogicHandler logic, String group) {
        if (group.length() != 2) {
            throw new IllegalArgumentException("Parameter group must have two characters");
        }
        
        logic.startBarGroup(BarGroup.MSG_CHARACTER, group);
        for (int index = 0; index < 5; index++) {
            logic.addBar(true, widthAt(group.charAt(0), index));
            logic.addBar(false, widthAt(group.charAt(1), index));
        }
        logic.endBarGroup();
    }
    
    private String handleChecksum(String msg) {
        if (getChecksumMode() == ChecksumMode.CP_ADD) {
            return msg + calcChecksum(msg);
        } else if (getChecksumMode() == ChecksumMode.CP_CHECK) {
            if (!validateChecksum(msg)) {
                throw new IllegalArgumentException("Message '" 
                    + msg
                    + "' has a bad checksum. Expected: " 
                    + calcChecksum(msg.substring(0, msg.length() - 1)));
            }
            return msg;
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return msg;
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return msg; //equals ignore
        } else {
            throw new UnsupportedOperationException(
                "Unknown checksum mode: " + getChecksumMode());
        }
    }
    
    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        //Checksum handling as requested
        String s = handleChecksum(msg);
        
        //Length must be even
        if ((s.length() % 2) != 0) {
            s = "0" + s;
        }

        logic.startBarcode(msg);

        //Start character
        logic.startBarGroup(BarGroup.START_CHARACTER, null);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.endBarGroup();

        //Process string
        int i = 0;
        do {
            encodeGroup(logic, s.substring(i, i + 2));
            i += 2;
        } while (i < s.length());

        //End character
        logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
        logic.addBar(true, 2);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();

        logic.endBarcode();
    }

}