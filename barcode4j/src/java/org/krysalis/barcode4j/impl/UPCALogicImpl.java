/*
 * $Id: UPCALogicImpl.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
 * This class is an implementation of the UPC-A barcode.
 * 
 * @author Jeremias Maerki
 */
public class UPCALogicImpl extends UPCEANLogicImpl {

    /**
     * Main constructor
     * @param mode the checksum mode
     */
    public UPCALogicImpl(ChecksumMode mode) {
        super(mode);
    }

    /**
     * Validates a UPC-A message. The method throws IllegalArgumentExceptions
     * if an invalid message is passed.
     * @param msg the message to validate
     */
    public static void validateMessage(String msg) {
        UPCEANLogicImpl.validateMessage(msg);
        if ((msg.length() < 11) || (msg.length() > 12)) {
            throw new IllegalArgumentException(
                "Message must be 11 or 12 characters long. Message: " + msg);
        }
    }
    
    /**
     * Does checksum processing according to the checksum mode.
     * @param msg the message to process
     * @param mode the checksum mode
     * @return the possibly modified message
     */
    public static String handleChecksum(String msg, ChecksumMode mode) {
        if (mode == ChecksumMode.CP_AUTO) {
            if (msg.length() == 11) {
                mode = ChecksumMode.CP_ADD;
            } else if (msg.length() == 12) {
                mode = ChecksumMode.CP_CHECK;
            } else {
                //Shouldn't happen because of validateMessage
                throw new RuntimeException("Internal error");
            }
        }
        if (mode == ChecksumMode.CP_ADD) {
            if (msg.length() != 11) {
                throw new IllegalArgumentException(
                    "Message must be 11 characters long");
            }
            return msg + calcChecksum(msg);
        } else if (mode == ChecksumMode.CP_CHECK) {
            if (msg.length() != 12) {
                throw new IllegalArgumentException(
                    "Message must be 12 characters long");
            }
            char check = msg.charAt(11);
            char expected = calcChecksum(msg.substring(0, 11));
            if (check != expected) {
                throw new IllegalArgumentException(
                    "Checksum is bad (" + check + "). Expected: " + expected);
            }
            return msg;
        } else if (mode == ChecksumMode.CP_IGNORE) {
            if (msg.length() != 12) {
                throw new IllegalArgumentException(
                    "Message must be 12 characters long");
            }
            return msg;
        } else {
            throw new UnsupportedOperationException(
                "Unknown checksum mode: " + mode);
        }
    }

    private String handleChecksum(String msg) {
        return handleChecksum(msg, getChecksumMode());
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

        //Number system character
        final char lead = s.charAt(0);
        logic.startBarGroup(BarGroup.UPC_EAN_LEAD, new Character(lead).toString());
        encodeChar(logic, lead, LEFT_HAND_A);
        logic.endBarGroup();

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(1, 6));
        
        //First five data characters
        for (int i = 1; i < 6; i++) {
            encodeChar(logic, s.charAt(i), LEFT_HAND_A);
        }
        
        logic.endBarGroup();

        //Center guard
        drawCenterGuard(logic);

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(6, 11));
        
        //Last five data characters
        for (int i = 6; i < 11; i++) {
            encodeChar(logic, s.charAt(i), RIGHT_HAND);
        }

        logic.endBarGroup();

        //Checksum
        final char check = s.charAt(11);
        logic.startBarGroup(BarGroup.UPC_EAN_CHECK, new Character(check).toString());
        encodeChar(logic, check, RIGHT_HAND);
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
