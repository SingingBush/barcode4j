/*
 * Copyright 2002-2004,2008-2009 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code128;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.tools.MessageUtil;

/**
 * This class is an implementation of the Code 128 barcode.
 *
 * @version $Id: Code128LogicImpl.java,v 1.5 2009-02-20 13:07:21 jmaerki Exp $
 */
public class Code128LogicImpl {

    /** The function 1 command. ASCII: 0xF1 */
    public static final char FNC_1 = 0xF1;
    /** The function 2 command. ASCII: 0xF2 */
    public static final char FNC_2 = 0xF2;
    /** The function 3 command. ASCII: 0xF3 */
    public static final char FNC_3 = 0xF3;
    /** The function 4 command. ASCII: 0xF4 */
    public static final char FNC_4 = 0xF4;

    private static final byte[][] CHARSET =
                                      {{2, 1, 2, 2, 2, 2}, //000, SP, #032
                                       {2, 2, 2, 1, 2, 2},
                                       {2, 2, 2, 2, 2, 1},
                                       {1, 2, 1, 2, 2, 3},
                                       {1, 2, 1, 3, 2, 2},
                                       {1, 3, 1, 2, 2, 2},
                                       {1, 2, 2, 2, 1, 3},
                                       {1, 2, 2, 3, 1, 2},
                                       {1, 3, 2, 2, 1, 2},
                                       {2, 2, 1, 2, 1, 3},
                                       {2, 2, 1, 3, 1, 2},
                                       {2, 3, 1, 2, 1, 2},
                                       {1, 1, 2, 2, 3, 2},
                                       {1, 2, 2, 1, 3, 2},
                                       {1, 2, 2, 2, 3, 1},
                                       {1, 1, 3, 2, 2, 2},
                                       {1, 2, 3, 1, 2, 2}, //016, '0', #048
                                       {1, 2, 3, 2, 2, 1},
                                       {2, 2, 3, 2, 1, 1},
                                       {2, 2, 1, 1, 3, 2},
                                       {2, 2, 1, 2, 3, 1},
                                       {2, 1, 3, 2, 1, 2},
                                       {2, 2, 3, 1, 1, 2},
                                       {3, 1, 2, 1, 3, 1},
                                       {3, 1, 1, 2, 2, 2},
                                       {3, 2, 1, 1, 2, 2}, //025, '9', #057
                                       {3, 2, 1, 2, 2, 1}, //026, ':', #058
                                       {3, 1, 2, 2, 1, 2},
                                       {3, 2, 2, 1, 1, 2},
                                       {3, 2, 2, 2, 1, 1},
                                       {2, 1, 2, 1, 2, 3},
                                       {2, 1, 2, 3, 2, 1},
                                       {2, 3, 2, 1, 2, 1},
                                       {1, 1, 1, 3, 2, 3}, //033, 'A', #065
                                       {1, 3, 1, 1, 2, 3},
                                       {1, 3, 1, 3, 2, 1},
                                       {1, 1, 2, 3, 1, 3},
                                       {1, 3, 2, 1, 1, 3},
                                       {1, 3, 2, 3, 1, 1},
                                       {2, 1, 1, 3, 1, 3},
                                       {2, 3, 1, 1, 1, 3},
                                       {2, 3, 1, 3, 1, 1},
                                       {1, 1, 2, 1, 3, 3},
                                       {1, 1, 2, 3, 3, 1},
                                       {1, 3, 2, 1, 3, 1},
                                       {1, 1, 3, 1, 2, 3},
                                       {1, 1, 3, 3, 2, 1},
                                       {1, 3, 3, 1, 2, 1},
                                       {3, 1, 3, 1, 2, 1},
                                       {2, 1, 1, 3, 3, 1},
                                       {2, 3, 1, 1, 3, 1},
                                       {2, 1, 3, 1, 1, 3},
                                       {2, 1, 3, 3, 1, 1},
                                       {2, 1, 3, 1, 3, 1},
                                       {3, 1, 1, 1, 2, 3},
                                       {3, 1, 1, 3, 2, 1},
                                       {3, 3, 1, 1, 2, 1},
                                       {3, 1, 2, 1, 1, 3},
                                       {3, 1, 2, 3, 1, 1}, //058, 'Z', #090
                                       {3, 3, 2, 1, 1, 1}, //059, '[', #091
                                       {3, 1, 4, 1, 1, 1},
                                       {2, 2, 1, 4, 1, 1},
                                       {4, 3, 1, 1, 1, 1},
                                       {1, 1, 1, 2, 2, 4}, //063, '_', #095
                                       {1, 1, 1, 4, 2, 2}, //064, A:NUL/B:'`', #000/#096
                                       {1, 2, 1, 1, 2, 4}, //065, A:SOH/B:'a'. #001/#097
                                       {1, 2, 1, 4, 2, 1},
                                       {1, 4, 1, 1, 2, 2},
                                       {1, 4, 1, 2, 2, 1},
                                       {1, 1, 2, 2, 1, 4},
                                       {1, 1, 2, 4, 1, 2},
                                       {1, 2, 2, 1, 1, 4},
                                       {1, 2, 2, 4, 1, 1},
                                       {1, 4, 2, 1, 1, 2},
                                       {1, 4, 2, 2, 1, 1},
                                       {2, 4, 1, 2, 1, 1},
                                       {2, 2, 1, 1, 1, 4},
                                       {4, 1, 3, 1, 1, 1},
                                       {2, 4, 1, 1, 1, 2},
                                       {1, 3, 4, 1, 1, 1},
                                       {1, 1, 1, 2, 4, 2},
                                       {1, 2, 1, 1, 4, 2},
                                       {1, 2, 1, 2, 4, 1},
                                       {1, 1, 4, 2, 1, 2},
                                       {1, 2, 4, 1, 1, 2},
                                       {1, 2, 4, 2, 1, 1},
                                       {4, 1, 1, 2, 1, 2},
                                       {4, 2, 1, 1, 1, 2},
                                       {4, 2, 1, 2, 1, 1},
                                       {2, 1, 2, 1, 4, 1},
                                       {2, 1, 4, 1, 2, 1}, //090, A:SUB/B:'z', #026/#122
                                       {4, 1, 2, 1, 2, 1},
                                       {1, 1, 1, 1, 4, 3},
                                       {1, 1, 1, 3, 4, 1},
                                       {1, 3, 1, 1, 4, 1}, //094, A:RS/B:tilde, #030/#126
                                       {1, 1, 4, 1, 1, 3}, //095, A:US/B:DEL, #031/#127
                                       {1, 1, 4, 3, 1, 1},
                                       {4, 1, 1, 1, 1, 3},
                                       {4, 1, 1, 3, 1, 1},
                                       {1, 1, 3, 1, 4, 1},
                                       {1, 1, 4, 1, 3, 1},
                                       {3, 1, 1, 1, 4, 1},
                                       {4, 1, 1, 1, 3, 1},
                                       {2, 1, 1, 4, 1, 2},  //103, Start A
                                       {2, 1, 1, 2, 1, 4},  //104, Start B
                                       {2, 1, 1, 2, 3, 2}}; //105, Start C

    private static final byte[] STOP = {2, 3, 3, 1, 1, 1, 2}; //106, STOP

    private int codeset;

    /**
     * Default constructor.
     */
    public Code128LogicImpl() {
        this(Code128Constants.CODESET_ALL);
    }

    /**
     * Main constructor.
     * @param codeset the enabled codeset
     */
    public Code128LogicImpl(int codeset) {
        this.codeset = codeset;
    }

    /**
     * Determines whether a character can be encoded in Code 128.
     * @param ch the character to check
     * @return true if it is a valid character
     */
    public static boolean isValidChar(char ch) {
        return (ch >= 0 && ch <= 127)
            || (ch >= FNC_1 && ch <= FNC_4);
    }

    /**
     * Determines whether a character is defined in codeset A.
     * @param ch the character to check
     * @return true if it is found in codeset A
     */
    public static boolean isInCodeSetA(char ch) {
        return (ch >= 0 && ch <= 95)
            || (ch >= FNC_1 && ch <= FNC_4);
    }

    /**
     * Determines whether a character is defined in codeset B.
     * @param ch the character to check
     * @return true if it is found in codeset B
     */
    public static boolean isInCodeSetB(char ch) {
        return (ch >= 32 && ch <= 127)
            || (ch >= FNC_1 && ch <= FNC_4);
    }

    /**
     * Determines whether a character is a digit or a function 1 command.
     * @param ch the character to check
     * @param second true if checking the character for the second position in
     *   a duo.
     * @return true if the above condition is met
     */
    public static boolean canBeInCodeSetC(char ch, boolean second) {
        if (second) {
            return (ch >= '0' && ch <= '9');
        } else {
            return (ch >= '0' && ch <= '9') || (ch == FNC_1);
        }
    }

    /**
     * Converts a character set index to a String representation. This is
     * primarily used for debugging purposes.
     * @param index the character set index
     * @return the String representation
     */
    public static String symbolCharToString(int index) {
        switch (index) {
            case 96: return "FNC3/96";
            case 97: return "FNC2/97";
            case 98: return "Shift/98";
            case 99: return "CodeC/99";
            case 100: return "CodeB/FNC4";
            case 101: return "CodeA/FNC4";
            case 102: return "FNC1";
            case 103: return "StartA";
            case 104: return "StartB";
            case 105: return "StartC";
            default: return "idx" + Integer.toString(index);
        }
    }

    /**
     * Converts an encoded Code 128 message into a String for debugging
     * purposes.
     * @param encodedMsg the encoded message
     * @return the String representation
     */
    public static String toString(int[] encodedMsg) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < encodedMsg.length; i++) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(symbolCharToString(encodedMsg[i]));
        }
        return sb.toString();
    }

    /**
     * Encodes a character.
     * @param logic LogicHandler to send the barcode events to
     * @param index index withing the character set of the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, int index) {
        logic.startBarGroup(BarGroup.MSG_CHARACTER, symbolCharToString(index));
        for (byte i = 0; i < 6; i++) {
            final int width = CHARSET[index][i];
            final boolean black = ((i % 2) == 0);
            logic.addBar(black, width);
        }
        logic.endBarGroup();
    }

    /**
     * Encodes the special stop character.
     * @param logic LogicHandler to send the barcode events to
     */
    protected void encodeStop(ClassicBarcodeLogicHandler logic) {
        logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
        for (byte i = 0; i < 7; i++) {
            final int width = STOP[i];
            final boolean black = ((i % 2) == 0);
            logic.addBar(black, width);
        }
        logic.endBarGroup();
    }

    /**
     * Returns the encoder to be used. The encoder is responsible for turning
     * a String message into an array of character set indexes.
     * <p>
     * Override this method to supply your own implementation.
     * @return the requested encoder
     */
    protected Code128Encoder getEncoder() {
        return new DefaultCode128Encoder(this.codeset);
    }

    /**
     * Encodes a message into an array of character set indexes.
     * @param msg the message to encode
     * @return the requested array of character set indexes
     * @see #getEncoder()
     */
    int[] createEncodedMessage(String msg) {
        return getEncoder().encode(msg);
    }

    /**
     * Generates the barcode logic
     * @param logic the logic handler to receive the generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        logic.startBarcode(msg, MessageUtil.filterNonPrintableCharacters(msg));

        int[] encodedMsg = createEncodedMessage(msg);
        for (int i = 0; i < encodedMsg.length; i++) {
            encodeChar(logic, encodedMsg[i]);
        }

        //Calculate checksum
        int checksum = encodedMsg[0];
        for (int i = 1; i < encodedMsg.length; i++) {
            checksum += i * encodedMsg[i];
        }
        checksum = checksum % 103;
        encodeChar(logic, checksum);

        encodeStop(logic);

        logic.endBarcode();
    }

}
