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
 * This is an abstract base class for UPC and EAN barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCEANLogicImpl.java,v 1.1 2004-09-12 17:57:52 jmaerki Exp $
 */
public abstract class UPCEANLogicImpl {

    /** Left hand A character set */
    protected static final byte LEFT_HAND_A = 0;
    /** Left hand B character set */
    protected static final byte LEFT_HAND_B = 1;
    /** Right hand character set */
    protected static final byte RIGHT_HAND  = 2;
    /** Odd parity character set */
    protected static final byte ODD_PARITY  = LEFT_HAND_A;
    /** Even parity character set */
    protected static final byte EVEN_PARITY = LEFT_HAND_B;

    private static final byte[][] CHARSET = {{3, 2, 1, 1}, 
                                             {2, 2, 2, 1}, 
                                             {2, 1, 2, 2}, 
                                             {1, 4, 1, 1}, 
                                             {1, 1, 3, 2}, 
                                             {1, 2, 3, 1}, 
                                             {1, 1, 1, 4}, 
                                             {1, 3, 1, 2}, 
                                             {1, 2, 1, 3}, 
                                             {3, 1, 1, 2}};
        
    private static final byte O = ODD_PARITY;
    private static final byte E = EVEN_PARITY;
    
    private static final byte[][] SUPP2_PARITY =
            {{O, O}, {O, E}, {E, O}, {E, E}};
                                    
    private static final byte[][] SUPP5_PARITY =
            {{E, E, O, O, O},
             {E, O, E, O, O},
             {E, O, O, E, O},
             {E, O, O, O, E},
             {O, E, E, O, O},
             {O, O, E, E, O},
             {O, O, O, E, E},
             {O, E, O, E, O},
             {O, E, O, O, E},
             {O, O, E, O, E}};
                                             
    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;

    
    /**
     * Main constructor
     * @param mode the checksum mode
     */
    public UPCEANLogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Returns the current checksum mode.
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Validates a UPC/EAN message. The method throws IllegalArgumentExceptions
     * if an invalid message is passed.
     * @param msg the message to validate
     */
    public static void validateMessage(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            final char c = msg.charAt(i);
            if ((c < '0') || (c > '9')) {
                throw new IllegalArgumentException("Invalid characters found. "
                    + "Valid are 0-9 only. Message: " + msg);
            }
        }
    }
    
    /**
     * Calculates the check character for a given message
     * @param msg the message
     * @return char the check character
     */
    public static char calcChecksum(String msg) {
        int oddsum = 0;
        int evensum = 0;
        for (int i = msg.length() - 1; i >= 0; i--) {
            if ((msg.length() - i) % 2 == 0) {
                evensum += Character.digit(msg.charAt(i), 10);
            } else {
                oddsum += Character.digit(msg.charAt(i), 10);
            }
        }
        int check = 10 - ((evensum + 3 * oddsum) % 10);
        if (check >= 10) check = 0;
        return Character.forDigit(check, 10);
    }
    
    private int widthAt(char ch, int index) {
        if (Character.isDigit(ch)) {
            int digit = Character.digit(ch, 10);
            int width = CHARSET[digit][index];
            return width;
        } else {
            throw new IllegalArgumentException("Invalid character '" + ch + "'. Expected a digit.");
        }
    }

    /**
     * Encodes a character.
     * @param logic the logic handler to receive generated events
     * @param c the character to encode
     * @param charset the character set to use
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char c, int charset) {
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(c).toString());
        if (charset == LEFT_HAND_B) {
            for (byte i = 0; i < 4; i++) {
                final int width = widthAt(c, 3 - i);
                final boolean black = (i % 2 != 0);
                logic.addBar(black, width);
            }
        } else {
            for (byte i = 0; i < 4; i++) {
                final int width = widthAt(c, i);
                final boolean black = ((i % 2 == 0 && charset == RIGHT_HAND) 
                                    || (i % 2 != 0 && charset == LEFT_HAND_A));
                logic.addBar(black, width);
            }
        }
        logic.endBarGroup();
    }

    /**
     * Generates a side guard.
     * @param logic the logic handler to receive generated events
     */
    protected void drawSideGuard(ClassicBarcodeLogicHandler logic) {
        //draw guard bars 101
        logic.startBarGroup(BarGroup.UPC_EAN_GUARD, null);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();
    }

    /**
     * Generates a center guard.
     * @param logic the logic handler to receive generated events
     */
    protected void drawCenterGuard(ClassicBarcodeLogicHandler logic) {
        //draw guard bars 01010
        logic.startBarGroup(BarGroup.UPC_EAN_GUARD, null);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.endBarGroup();
    }

    /**
     * Generates a left guard for a supplemental.
     * @param logic the logic handler to receive generated events
     */
    private void drawSuppLeftGuard(ClassicBarcodeLogicHandler logic) {
        //draw guard bars 1011
        logic.startBarGroup(BarGroup.UPC_EAN_GUARD, null);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 2);
        logic.endBarGroup();
    }

    /**
     * Generates a supplemental separator.
     * @param logic the logic handler to receive generated events
     */
    private void drawSuppSeparator(ClassicBarcodeLogicHandler logic) {
        //draw inter-character separator 01
        logic.startBarGroup(BarGroup.UPC_EAN_GUARD, null);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();
    }

    /**
     * Generates a 2-character supplemental.
     * @param logic the logic handler to receive generated events
     * @param supp the two characters
     */
    private void drawSupplemental2(ClassicBarcodeLogicHandler logic, String supp) {
        int suppValue = Integer.parseInt(supp);
        int remainder = suppValue % 4;
        logic.startBarGroup(BarGroup.UPC_EAN_SUPP, supp);
        drawSuppLeftGuard(logic);
        encodeChar(logic, supp.charAt(0), SUPP2_PARITY[remainder][0]);
        drawSuppSeparator(logic);
        encodeChar(logic, supp.charAt(1), SUPP2_PARITY[remainder][1]);
        logic.endBarGroup();
    }

    /**
     * Generates a 5-character supplemental.
     * @param logic the logic handler to receive generated events
     * @param supp the five characters
     */
    private void drawSupplemental5(ClassicBarcodeLogicHandler logic, String supp) {
        int suppValue = Integer.parseInt(supp);
        int weightedSum = 
              3 * ((suppValue / 10000) % 10)
            + 9 * ((suppValue / 1000) % 10)
            + 3 * ((suppValue / 100) % 10)
            + 9 * ((suppValue / 10) % 10)
            + 3 * (suppValue % 10);
        byte checksum = (byte)(weightedSum % 10);
        logic.startBarGroup(BarGroup.UPC_EAN_SUPP, supp);
        drawSuppLeftGuard(logic);
        for (byte i = 0; i < 5; i++) {
            if (i > 0) {
                drawSuppSeparator(logic);
            }
            encodeChar(logic, supp.charAt(i), SUPP5_PARITY[checksum][i]);
        }
        logic.endBarGroup();
    }

    /**
     * Generates events for a supplemental.
     * @param logic the logic handler to receive generated events
     * @param supp the supplemental
     */
    protected void drawSupplemental(ClassicBarcodeLogicHandler logic, String supp) {
        if (supp == null) {
            throw new NullPointerException("Supplemental message must not be null");
        }
        if (supp.length() == 2) {
            drawSupplemental2(logic, supp);
        } else if (supp.length() == 5) {
            drawSupplemental5(logic, supp);
        } else {
            throw new IllegalArgumentException(
                "Only supplemental lengths 2 and 5 are allowed: " + supp.length());
        }
    }

    /**
     * Returns the length of the supplemental part of a UPC/EAN message.
     * The method throws an IllegalArgumentException if the supplement is 
     * malformed.
     * @param msg the UPC/EAN message
     * @return 2 or 5 if there is a supplemental, 0 if there's none.
     */
    protected static int getSupplementalLength(String msg) {
        String supp = retrieveSupplemental(msg);
        if (supp == null) {
            return 0;
        } else if (supp.length() == 2) {
            return 2;
        } else if (supp.length() == 5) {
            return 5;
        } else {
            throw new IllegalArgumentException(
                "Illegal supplemental length (valid: 2 or 5): " + supp);
        }
    }

    /**
     * Removes an optional supplemental (ex. "+20") from the message. 
     * @param msg a UPC/EAN message
     * @return the message without the supplemental
     */
    protected static String removeSupplemental(String msg) {
        int pos = msg.indexOf('+');
        if (pos >= 0) {
            return msg.substring(0, pos);
        } else {
            return msg;
        }
    }
    
    /**
     * Returns the supplemental part of a UPC/EAN message if there is one. 
     * Supplementals are added in the form: "+[supplemental]" (ex. "+20"). 
     * @param msg a UPC/EAN message
     * @return the supplemental part, null if there is none
     */
    protected static String retrieveSupplemental(String msg) {
        int pos = msg.indexOf('+');
        if (pos >= 0) {
            return msg.substring(pos + 1);
        } else {
            return null;
        }
    }
    
    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public abstract void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg);
                                             
}
