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
 * This class is an implementation of the UPC-E barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCELogicImpl.java,v 1.2 2004-10-24 11:45:53 jmaerki Exp $
 */
public class UPCELogicImpl extends UPCEANLogicImpl {

    private static final byte O = ODD_PARITY;
    private static final byte E = EVEN_PARITY;

    private static final byte[][] NUMBER_SYSTEM_0 = 
                {{E, E, E, O, O, O},
                 {E, E, O, E, O, O},
                 {E, E, O, O, E, O},
                 {E, E, O, O, O, E},
                 {E, O, E, E, O, O},
                 {E, O, O, E, E, O},
                 {E, O, O, O, E, E},
                 {E, O, E, O, E, O},
                 {E, O, E, O, O, E},
                 {E, O, O, E, O, E}};

    /**
     * Main constructor
     * @param mode the checksum mode
     */
    public UPCELogicImpl(ChecksumMode mode) {
        super(mode);
    }
    
    private static String substring(String s, int idx, int len) {
        return s.substring(idx, idx + len);
    }
    
    /**
     * Compacts an UPC-A message to an UPC-E message if possible.
     * @param msg an UPC-A message
     * @return String the derived UPC-E message (with checksum), 
     * null if the message is a valid UPC-A message but no UPC-E representation 
     * is possible
     */
    public static String compactMessage(String msg) {
        UPCALogicImpl.validateMessage(msg);
        String upca = UPCALogicImpl.handleChecksum(msg, ChecksumMode.CP_AUTO);
        final byte numberSystem = extractNumberSystem(upca);
        if ((numberSystem != 0) && (numberSystem != 1)) {
            return null;
        }
        final byte check = Byte.parseByte(upca.substring(11, 12));
        StringBuffer upce = new StringBuffer();
        upce.append(Byte.toString(numberSystem));
        try {
            String manufacturer = substring(upca, 1, 5);
            String product = substring(upca, 6, 5);
            String mtemp, ptemp;
            //Rule 1
            mtemp = substring(manufacturer, 2, 3);
            ptemp = substring(product, 0, 2);
            if (("000|100|200".indexOf(mtemp) >= 0) 
                    && ("00".equals(ptemp))) {
                upce.append(substring(manufacturer, 0, 2));
                upce.append(substring(product, 2, 3));
                upce.append(mtemp.charAt(0));
            } else {
                //Rule 2
                ptemp = substring(product, 0, 3);
                if (("300|400|500|600|700|800|900".indexOf(mtemp) >= 0) 
                        && ("000".equals(ptemp))) {
                    upce.append(substring(manufacturer, 0, 3));
                    upce.append(substring(product, 3, 2));
                    upce.append("3");
                } else {
                    //Rule 3
                    mtemp = substring(manufacturer, 3, 2);
                    ptemp = substring(product, 0, 4);
                    if (("10|20|30|40|50|60|70|80|90".indexOf(mtemp) >= 0) 
                            && ("0000".equals(ptemp))) {
                        upce.append(substring(manufacturer, 0, 4));
                        upce.append(substring(product, 4, 1));
                        upce.append("4");
                    } else {
                        //Rule 4
                        mtemp = substring(manufacturer, 4, 1);
                        ptemp = substring(product, 4, 1);
                        if (!"0".equals(mtemp) 
                            && ("5|6|7|8|9".indexOf(ptemp) >= 0)) {
                            upce.append(manufacturer);
                            upce.append(ptemp);
                        } else {
                            return null;
                        }
                    }        
                }
            }
        } catch (NumberFormatException nfe) {
            return null;
        }
        upce.append(Byte.toString(check));
        return upce.toString();
    }

    /**
     * Expands an UPC-E message to an UPC-A message.
     * @param msg an UPC-E message (7 or 8 characters)
     * @return String the expanded UPC-A message (with checksum, 12 characters)
     */
    public static String expandMessage(String msg) {
        char check = '\u0000';
        if (msg.length() == 8) {
            check = msg.charAt(7);
        }
        String upce = substring(msg, 0, 7);
        final byte numberSystem = extractNumberSystem(upce);
        if ((numberSystem != 0) && (numberSystem != 1)) {
            throw new IllegalArgumentException("Invalid UPC-E message: " + msg);
        }
        StringBuffer upca = new StringBuffer();
        upca.append(Byte.toString(numberSystem));
        byte mode = Byte.parseByte(substring(upce, 6, 1));
        if ((mode >= 0) && (mode <= 2)) {
            upca.append(substring(upce, 1, 2));
            upca.append(Byte.toString(mode));
            upca.append("0000");
            upca.append(substring(upce, 3, 3));
        } else if (mode == 3) {
            upca.append(substring(upce, 1, 3));
            upca.append("00000");
            upca.append(substring(upce, 4, 2));
        } else if (mode == 4) {
            upca.append(substring(upce, 1, 4));
            upca.append("00000");
            upca.append(substring(upce, 5, 1));
        } else if ((mode >= 5) && (mode <= 9)) {
            upca.append(substring(upce, 1, 5));
            upca.append("0000");
            upca.append(Byte.toString(mode));
        } else {
            //Shouldn't happen
            throw new RuntimeException("Internal error");
        }
        String upcaFinished = upca.toString();
        char expectedCheck = calcChecksum(upcaFinished);
        if ((check != '\u0000') && (check != expectedCheck)) {
            throw new IllegalArgumentException("Invalid checksum. Expected "
                + expectedCheck + " but was " + check);
        }
        return upcaFinished + expectedCheck;
    }
    
    private static byte extractNumberSystem(String msg) {
        return Byte.parseByte(msg.substring(0, 1));
    }
    
    private String convertUPCAtoUPCE(String msg) {
        if ((msg.length() == 11) || (msg.length() == 12)) {
            final String s = compactMessage(msg);
            if (s == null) {
                throw new IllegalArgumentException(
                    "UPC-A message cannot be compacted to UPC-E. Message: " + msg);
            }
            return s;
        }
        return msg;
    }

    /**
     * Validates an UPC-E message. The message can also be UPC-A in which case
     * the message is compacted to a UPC-E message if possible. If it's not
     * possible an IllegalArgumentException is thrown
     * @param msg the message to validate
     */
    public static void validateMessage(String msg) {
        UPCEANLogicImpl.validateMessage(msg);
        if ((msg.length() < 7) || (msg.length() > 8)) {
            throw new IllegalArgumentException(
                "Message must be 7 or 8 characters long. Message: " + msg);
        }
        byte numberSystem = extractNumberSystem(msg);
        if ((numberSystem < 0) || (numberSystem > 1)) {
            throw new IllegalArgumentException(
                "Valid number systems for UPC-E are 0 or 1. Found: " 
                    + numberSystem);
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
            if (msg.length() != 7) {
                throw new IllegalArgumentException(
                    "Message must be 7 characters long");
            }
            return msg + expandMessage(msg).charAt(11);
        } else if (mode == ChecksumMode.CP_CHECK) {
            if (msg.length() != 8) {
                throw new IllegalArgumentException(
                    "Message must be 8 characters long");
            }
            char check = msg.charAt(7);
            char expected = expandMessage(msg).charAt(11);
            if (check != expected) {
                throw new IllegalArgumentException(
                    "Checksum is bad (" + check + "). Expected: " + expected);
            }
            return msg;
        } else if (mode == ChecksumMode.CP_IGNORE) {
            if (msg.length() != 8) {
                throw new IllegalArgumentException(
                    "Message must be 8 characters long");
            }
            return msg;
        } else {
            throw new UnsupportedOperationException(
                "Unknown checksum mode: " + mode);
        }
    }
    
    private byte selectCharset(byte check, byte numberSystem, int position) {
        byte charset = NUMBER_SYSTEM_0[check][position];
        if (numberSystem == 1) {
            //Number System 1 is inversion of Number System 0
            if (charset == ODD_PARITY) {
                charset = EVEN_PARITY;
            } else {
                charset = ODD_PARITY;
            }
        }
        return charset;
    }
    
    /**
     * Generates a UPC-E right guard.
     * @param logic the logic handler to receive generated events
     */
    protected void drawUPCERightGuard(ClassicBarcodeLogicHandler logic) {
        //draw guard bars 010101
        logic.startBarGroup(BarGroup.UPC_EAN_GUARD, null);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();
    }

    /** @see org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        String supp = retrieveSupplemental(msg);
        String s = removeSupplemental(msg); 
        s = convertUPCAtoUPCE(s);
        validateMessage(s);
        s = handleChecksum(s);

        String canonicalMessage = s;
        if (supp != null) {
            canonicalMessage = canonicalMessage + "+" + supp;
        }
        logic.startBarcode(canonicalMessage, canonicalMessage);
        
        //Left guard
        drawSideGuard(logic);

        //Number system character
        final byte numberSystem = extractNumberSystem(msg);
        logic.startBarGroup(BarGroup.UPC_EAN_LEAD, Byte.toString(numberSystem));
        //No bars, number system is implicitly encoded
        logic.endBarGroup();

        //Checksum
        final byte check = Byte.parseByte(s.substring(7, 8));
        
        logic.startBarGroup(BarGroup.UPC_EAN_GROUP, s.substring(1, 7));
        
        //First five data characters
        for (int i = 1; i < 7; i++) {
            final byte charset = selectCharset(check, numberSystem, i - 1);
            encodeChar(logic, s.charAt(i), charset);
        }
        
        logic.endBarGroup();

        //Checksum
        logic.startBarGroup(BarGroup.UPC_EAN_CHECK, Byte.toString(check));
        //No bars, checksum is implicitly encoded
        logic.endBarGroup();

        //Right guard
        drawUPCERightGuard(logic);
        
        //Optional Supplemental
        if (supp != null) {
            drawSupplemental(logic, supp);
        }
        logic.endBarcode();
    }

}
