/*
 * $Id: EAN13LogicImpl.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * This class is an implementation of the EAN-13 barcode.
 * 
 * @author Jeremias Maerki
 */
public class EAN13LogicImpl extends UPCEANLogicImpl {

    private static final byte A = LEFT_HAND_A;
    private static final byte B = LEFT_HAND_B;

    private static final byte[][] FIRSTFLAG = {{A, A, A, A, A}, 
                                               {A, B, A, B, B}, 
                                               {A, B, B, A, B}, 
                                               {A, B, B, B, A}, 
                                               {B, A, A, B, B}, 
                                               {B, B, A, A, B}, 
                                               {B, B, B, A, A}, 
                                               {B, A, B, A, B}, 
                                               {B, A, B, B, A}, 
                                               {B, B, A, B, A}};

    /**
     * Main constructor
     * @param mode the checksum mode
     */
    public EAN13LogicImpl(ChecksumMode mode) {
        super(mode);
    }
    
    /**
     * Validates a EAN-13 message. The method throws IllegalArgumentExceptions
     * if an invalid message is passed.
     * @param msg the message to validate
     */
    public static void validateMessage(String msg) {
        UPCEANLogicImpl.validateMessage(msg);
        if ((msg.length() < 12) || (msg.length() > 13)) {
            throw new IllegalArgumentException(
                "Message must be 11 or 12 characters long. Message: " + msg);
        }
    }
    
    private String handleChecksum(String msg) {
        ChecksumMode mode = getChecksumMode();
        if (mode == ChecksumMode.CP_AUTO) {
            if (msg.length() == 12) {
                mode = ChecksumMode.CP_ADD;
            } else if (msg.length() == 13) {
                mode = ChecksumMode.CP_CHECK;
            } else {
                //Shouldn't happen because of validateMessage
                throw new RuntimeException("Internal error");
            }
        }
        if (mode == ChecksumMode.CP_ADD) {
            if (msg.length() > 12) {
                throw new IllegalArgumentException(
                    "Message is too long (max. 12 characters)");
            }
            if (msg.length() < 12) {
                throw new IllegalArgumentException(
                    "Message must be 12 characters long");
            }
            return msg + calcChecksum(msg);
        } else if (mode == ChecksumMode.CP_CHECK) {
            if (msg.length() > 13) {
                throw new IllegalArgumentException(
                    "Message is too long (max. 13 characters)");
            }
            if (msg.length() < 13) {
                throw new IllegalArgumentException(
                    "Message must be 13 characters long");
            }
            char check = msg.charAt(12);
            char expected = calcChecksum(msg.substring(0, 12));
            if (check != expected) {
                throw new IllegalArgumentException(
                    "Checksum is bad (" + check + "). Expected: " + expected);
            }
            return msg;
        } else if (mode == ChecksumMode.CP_IGNORE) {
            return msg;
        } else {
            throw new UnsupportedOperationException(
                "Unknown checksum mode: " + mode);
        }
    }
    
    /** @see org.krysalis.barcode4j.impl.UPCEANLogicImpl */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        String supp = retrieveSupplemental(msg);
        String s = removeSupplemental(msg); 
        validateMessage(s);
        s = handleChecksum(s);

        String canonicalMessage = s;
        if (supp != null) {
            canonicalMessage = canonicalMessage + "+" + supp;
        }
        logic.startBarcode(canonicalMessage);
        
        //Left guard
        drawSideGuard(logic);

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.charAt(0) + ":" + s.substring(1, 7));
        
        //Number system character
        encodeChar(logic, s.charAt(1), LEFT_HAND_A);

        //First five data characters
        byte flag = (byte)Character.digit(s.charAt(0), 10);
        for (int i = 2; i < 7; i++) {
            encodeChar(logic, s.charAt(i), 
                    FIRSTFLAG[flag][i - 2]);
        }

        logic.endBarGroup();
        
        //Center guard
        drawCenterGuard(logic);

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(7, 13));

        //Last five data characters
        for (int i = 7; i < 12; i++) {
            encodeChar(logic, s.charAt(i), RIGHT_HAND);
        }

        //Checksum
        final char check = s.charAt(12);
        logic.startBarGroup(BarGroup.UPC_EAN_CHECK, new Character(check).toString());
        encodeChar(logic, check, RIGHT_HAND);
        logic.endBarGroup();

        logic.endBarGroup();
        
        //Right guard
        drawSideGuard(logic);

        //Optional Supplemental
        if (supp != null) {
            drawSupplemental(logic, supp);
        }
        logic.endBarcode();
    }

}
