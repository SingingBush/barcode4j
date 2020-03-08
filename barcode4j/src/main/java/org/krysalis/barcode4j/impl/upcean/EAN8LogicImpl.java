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
 * This class is an implementation of the EAN-8 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: EAN8LogicImpl.java,v 1.2 2004-10-24 11:45:53 jmaerki Exp $
 */
public class EAN8LogicImpl extends UPCEANLogicImpl {

    /**
     * Main constructor.
     * @param mode the checksum mode
     */
    public EAN8LogicImpl(ChecksumMode mode) {
        super(mode);
    }
    
    /**
     * Validates a EAN-8 message. The method throws IllegalArgumentExceptions
     * if an invalid message is passed.
     * @param msg the message to validate
     */
    public static void validateMessage(String msg) {
        UPCEANLogicImpl.validateMessage(msg);
        if ((msg.length() < 7) || (msg.length() > 8)) {
            throw new IllegalArgumentException(
                "Message must be 7 or 8 characters long. Message: " + msg);
        }
    }
    
    private String handleChecksum(String msg) {
        ChecksumMode mode = getChecksumMode();
        if (mode == ChecksumMode.CP_AUTO) {
            if (msg.length() == 7) {
                mode = ChecksumMode.CP_ADD;
            } else if (msg.length() == 8) {
                mode = ChecksumMode.CP_CHECK;
            } else {
                //Shouldn't happen because of validateMessage
                throw new RuntimeException("Internal error");
            }
        }
        if (mode == ChecksumMode.CP_ADD) {
            if (msg.length() > 7) {
                throw new IllegalArgumentException(
                    "Message is too long (max. 7 characters)");
            }
            if (msg.length() < 7) {
                throw new IllegalArgumentException(
                    "Message must be 7 characters long");
            }
            return msg + calcChecksum(msg);
        } else if (mode == ChecksumMode.CP_CHECK) {
            if (msg.length() > 8) {
                throw new IllegalArgumentException(
                    "Message is too long (max. 8 characters)");
            }
            if (msg.length() < 8) {
                throw new IllegalArgumentException(
                    "Message must be 8 characters long");
            }
            char check = msg.charAt(7);
            char expected = calcChecksum(msg.substring(0, 7));
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

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(0, 4));
        
        //First four data characters
        for (int i = 0; i < 4; i++) {
            encodeChar(logic, s.charAt(i), LEFT_HAND_A);
        }

        logic.endBarGroup();
        
        //Center guard
        drawCenterGuard(logic);

        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(4, 8));

        //Last three data characters
        for (int i = 4; i < 7; i++) {
            encodeChar(logic, s.charAt(i), RIGHT_HAND);
        }

        //Checksum
        final char check = s.charAt(7);
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
