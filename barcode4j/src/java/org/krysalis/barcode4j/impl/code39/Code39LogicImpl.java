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
package org.krysalis.barcode4j.impl.code39;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the Code39 barcode.
 * 
 * @author Jeremias Maerki
 * @todo Add ASCII-7bit encoding table
 * @version $Id: Code39LogicImpl.java,v 1.2 2004-10-24 11:45:55 jmaerki Exp $
 */
public class Code39LogicImpl {

    private static final char[] CHARACTERS = 
                        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
                         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
                         'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
                         'U', 'V', 'W', 'X', 'Y', 'Z', 
                         '-', '.', ' ', '$', '/', '+', '%', '*'};

    private static final char STARTSTOP = '*'; //Not used as normal character

    private static final byte[][] CHARSET = 
                        {{0, 0, 0, 1, 1, 0, 1, 0, 0}, //0
                         {1, 0, 0, 1, 0, 0, 0, 0, 1}, //1
                         {0, 0, 1, 1, 0, 0, 0, 0, 1}, //2
                         {1, 0, 1, 1, 0, 0, 0, 0, 0}, //3
                         {0, 0, 0, 1, 1, 0, 0, 0, 1}, //4
                         {1, 0, 0, 1, 1, 0, 0, 0, 0}, //5
                         {0, 0, 1, 1, 1, 0, 0, 0, 0}, //6
                         {0, 0, 0, 1, 0, 0, 1, 0, 1}, //7
                         {1, 0, 0, 1, 0, 0, 1, 0, 0}, //8
                         {0, 0, 1, 1, 0, 0, 1, 0, 0}, //9
                         {1, 0, 0, 0, 0, 1, 0, 0, 1}, //A
                         {0, 0, 1, 0, 0, 1, 0, 0, 1}, //B
                         {1, 0, 1, 0, 0, 1, 0, 0, 0}, //C
                         {0, 0, 0, 0, 1, 1, 0, 0, 1}, //D
                         {1, 0, 0, 0, 1, 1, 0, 0, 0}, //E
                         {0, 0, 1, 0, 1, 1, 0, 0, 0}, //F
                         {0, 0, 0, 0, 0, 1, 1, 0, 1}, //G
                         {1, 0, 0, 0, 0, 1, 1, 0, 0}, //H
                         {0, 0, 1, 0, 0, 1, 1, 0, 0}, //I
                         {0, 0, 0, 0, 1, 1, 1, 0, 0}, //J
                         {1, 0, 0, 0, 0, 0, 0, 1, 1}, //K
                         {0, 0, 1, 0, 0, 0, 0, 1, 1}, //L
                         {1, 0, 1, 0, 0, 0, 0, 1, 0}, //M
                         {0, 0, 0, 0, 1, 0, 0, 1, 1}, //N
                         {1, 0, 0, 0, 1, 0, 0, 1, 0}, //O
                         {0, 0, 1, 0, 1, 0, 0, 1, 0}, //P
                         {0, 0, 0, 0, 0, 0, 1, 1, 1}, //Q
                         {1, 0, 0, 0, 0, 0, 1, 1, 0}, //R
                         {0, 0, 1, 0, 0, 0, 1, 1, 0}, //S
                         {0, 0, 0, 0, 1, 0, 1, 1, 0}, //T
                         {1, 1, 0, 0, 0, 0, 0, 0, 1}, //U
                         {0, 1, 1, 0, 0, 0, 0, 0, 1}, //V
                         {1, 1, 1, 0, 0, 0, 0, 0, 0}, //W
                         {0, 1, 0, 0, 1, 0, 0, 0, 1}, //X
                         {1, 1, 0, 0, 1, 0, 0, 0, 0}, //Y
                         {0, 1, 1, 0, 1, 0, 0, 0, 0}, //Z
                         {0, 1, 0, 0, 0, 0, 1, 0, 1}, //-
                         {1, 1, 0, 0, 0, 0, 1, 0, 0}, //.
                         {0, 1, 1, 0, 0, 0, 1, 0, 0}, //SP
                         {0, 1, 0, 1, 0, 1, 0, 0, 0}, //$
                         {0, 1, 0, 1, 0, 0, 0, 1, 0}, //"/"
                         {0, 1, 0, 0, 0, 1, 0, 1, 0}, //+
                         {0, 0, 0, 1, 0, 1, 0, 1, 0}, //%
                         {0, 1, 0, 0, 1, 0, 1, 0, 0}}; //*

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private boolean displayStartStop = false;
    private boolean displayChecksum = false;

    /**
     * Main constructor
     * @param mode checksum mode
     * @param displayStartStop Controls whether to display start and stop
     *   characters in the human-readable message
     * @param displayChecksum Controls whether to display checksum
     *   in the human-readable message
     */
    public Code39LogicImpl(ChecksumMode mode, boolean displayStartStop, 
                boolean displayChecksum) {
        this.checksumMode = mode;
        this.displayStartStop = displayStartStop;
        this.displayChecksum = displayChecksum;
    }

    /**
     * Returns the currently active checksum mode.
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    private static void invalidCharacter(char c) {
        if (c != STARTSTOP) {
            throw new IllegalArgumentException("Invalid character: " + c);
        } else {
            throw new IllegalArgumentException("You may not include '*' as "
                    + "part of the message. This start/stop character is "
                    + "automatically added before and after the message.");
        }
    }
    
    /**
     * Calculates the checksum for a message to be encoded as an 
     * Code39 barcode.
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        int checksum = 0;
        for (int i = 0; i < msg.length(); i++) {
            final int chidx = getCharIndex(msg.charAt(i));
            if (chidx >= 0) {
                checksum += chidx;
            } else {
                invalidCharacter(msg.charAt(i));
            }
        }
        return CHARACTERS[checksum % 43];
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

    private static int getCharIndex(char ch) {
        final char effch;
        if ((ch >= 'a') && (ch <= 'z')) {
            effch = Character.toUpperCase(ch);
        } else {
            effch = ch;
        }
        for (int i = 0; i < CHARACTERS.length; i++) {
            if (effch == CHARACTERS[i]) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isValidChar(char ch) {
        if (ch == STARTSTOP) return false;
        return (getCharIndex(ch) >= 0);
    }

    private int widthAt(char ch, int index) {
        final int chidx = getCharIndex(ch);
        if (chidx >= 0) {
            int binary = CHARSET[chidx][index];
            return binary + 1;
        } else {
            invalidCharacter(ch);
            return 0; //this is unreachable code.
        }
    }

    /**
     * Encodes a single character.
     * @param logic the logic handler to receive generated events
     * @param c the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char c) {
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(c).toString());
        for (byte i = 0; i < 9; i++) {
            int width = widthAt(c, i);
            boolean black = (i % 2 == 0);
            logic.addBar(black, width);
        }
        logic.endBarGroup();
    }

    private void addIntercharacterGap(ClassicBarcodeLogicHandler logic) {
        //Add intercharacter gap (currently assumed to be narrow width)
        logic.addBar(false, -1); //-1 is special
    }
        
    private String handleChecksum(StringBuffer sb) {
        if (getChecksumMode() == ChecksumMode.CP_ADD) {
            if (displayChecksum) {
                sb.append(calcChecksum(sb.toString()));
                return sb.toString();
            } else {
                String msg = sb.toString();
                sb.append(calcChecksum(msg));
                return msg;
            }
        } else if (getChecksumMode() == ChecksumMode.CP_CHECK) {
            if (!validateChecksum(sb.toString())) {
                throw new IllegalArgumentException("Message '" 
                    + sb.toString()
                    + "' has a bad checksum. Expected: " 
                    + calcChecksum(sb.toString()));
            }
            if (displayChecksum) {
                return sb.toString();
            } else {
                return sb.substring(0, sb.length() - 1);
            }
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return sb.toString();
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return sb.toString(); //equals ignore
        } else {
            throw new IllegalStateException("Unknown checksum mode");
        }
    }

    /**
     * Generates the barcode logic
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        StringBuffer sb = new StringBuffer(msg);
        
        //Checksum handling as requested
        String formattedMsg = handleChecksum(sb);

        if (displayStartStop) {
            logic.startBarcode(sb.toString(), STARTSTOP + formattedMsg + STARTSTOP);
        } else {
            logic.startBarcode(sb.toString(), formattedMsg);
        }

        //Start character
        logic.startBarGroup(BarGroup.START_CHARACTER, new Character(STARTSTOP).toString());
        encodeChar(logic, STARTSTOP);
        logic.endBarGroup();

        for (int i = 0; i < sb.length(); i++) {
            addIntercharacterGap(logic);
            
            final char ch = sb.charAt(i);
            if (!isValidChar(ch)) throw new IllegalArgumentException("Invalid character: " + ch);
            encodeChar(logic, ch);
        }

        addIntercharacterGap(logic);

        //Start character
        logic.startBarGroup(BarGroup.STOP_CHARACTER, new Character(STARTSTOP).toString());
        encodeChar(logic, STARTSTOP);
        logic.endBarGroup();

        logic.endBarcode();
    }


}
