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
package org.krysalis.barcode4j.impl.upcean;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the EAN-13 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: EAN13LogicImpl.java,v 1.3 2009-07-03 06:23:49 jmaerki Exp $
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
                "Message must be 12 or 13 characters long. Message: " + msg);
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
    
    /** @see org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        String supp = retrieveSupplemental(msg);
        String s = removeSupplemental(msg); 
        validateMessage(s);
        s = handleChecksum(s);

        String canonicalMessage = s;
        if (supp != null) {
            canonicalMessage = canonicalMessage + "+" + supp;
        }
        logic.startBarcode(canonicalMessage, canonicalMessage);
        
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
