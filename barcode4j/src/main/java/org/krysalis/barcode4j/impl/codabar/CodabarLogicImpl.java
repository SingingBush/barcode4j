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
package org.krysalis.barcode4j.impl.codabar;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the Codabar barcode.
 *
 * @author Jeremias Maerki
 * @todo Complete the implementation (checksum, automatic start/stops chars...)
 * @version $Id: CodabarLogicImpl.java,v 1.4 2008-11-22 09:57:10 jmaerki Exp $
 */
public class CodabarLogicImpl {

    private static final char[] CHARACTERS =
                            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                             'a', 'b', 'c', 'd', 'e', 'n', 't',
                             '-', '$', ':', '/', '.', '+', '*'};

    /** Defines the Codabar character set. */
    static final byte[][] CHARSET =
                                            {{0, 0, 0, 0, 0, 1, 1},  //0
                                             {0, 0, 0, 0, 1, 1, 0},  //1
                                             {0, 0, 0, 1, 0, 0, 1},  //2
                                             {1, 1, 0, 0, 0, 0, 0},  //3
                                             {0, 0, 1, 0, 0, 1, 0},  //4
                                             {1, 0, 0, 0, 0, 1, 0},  //5
                                             {0, 1, 0, 0, 0, 0, 1},  //6
                                             {0, 1, 0, 0, 1, 0, 0},  //7
                                             {0, 1, 1, 0, 0, 0, 0},  //8
                                             {1, 0, 0, 1, 0, 0, 0},  //9
                                             {0, 0, 1, 1, 0, 1, 0},  //a
                                             {0, 1, 0, 1, 0, 0, 1},  //b
                                             {0, 0, 0, 1, 0, 1, 1},  //c
                                             {0, 0, 0, 1, 1, 1, 0},  //d
                                             {0, 0, 0, 1, 1, 1, 0},  //e
                                             {0, 1, 0, 1, 0, 0, 1},  //n
                                             {0, 0, 1, 1, 0, 1, 0},  //t
                                             {0, 0, 0, 1, 1, 0, 0},  //-
                                             {0, 0, 1, 1, 0, 0, 0},  //$
                                             {1, 0, 0, 0, 1, 0, 1},  //:
                                             {1, 0, 1, 0, 0, 0, 1},  ///
                                             {1, 0, 1, 0, 1, 0, 0},  //.
                                             {0, 0, 1, 0, 1, 0, 1},  //+
                                             {0, 0, 0, 1, 0, 1, 1}}; //*

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private boolean displayStartStop = false;


    /**
     * Main constructor
     * @param mode Determines how checksums are to be treated.
     */
    public CodabarLogicImpl(ChecksumMode mode) {
        this.checksumMode = mode;
    }

    /**
     * Main constructor
     * @param mode Determines how checksums are to be treated.
     * @param displayStartStop enables or disables suppressing the start/stop characters from
     *          the human-readable part
     */
    public CodabarLogicImpl(ChecksumMode mode, boolean displayStartStop) {
        this.checksumMode = mode;
        this.displayStartStop = displayStartStop;
    }

    /**
     * Returns the checksum mode.
     * @return the current checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Returns the index of a character within the character set.
     * @param ch the character to lookup
     * @return the index of the character or -1 if it isn't supported
     */
    protected static int getCharIndex(char ch) {
        for (int i = 0; i < CHARACTERS.length; i++) {
            if (ch == CHARACTERS[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines whether a character is a valid message character.
     * @param ch the character to check
     * @return true if it is a valid character, false otherwise
     */
    protected static boolean isValidChar(char ch) {
        return (getCharIndex(Character.toLowerCase(ch)) >= 0);
    }

    /**
     * Determines whether a character is on of the start/stop characters.
     * @param ch the character to check
     * @return true if it is a start/stop character
     */
    protected static boolean isStartStopChar(char ch) {
        ch = Character.toLowerCase(ch);
        return ((ch == 'a') || (ch == 'b')
             || (ch == 'c') || (ch == 'd')
             || (ch == 'e') || (ch == '*')
             || (ch == 'n') || (ch == 't'));
    }

    private int widthAt(char ch, int index) throws IllegalArgumentException {
        ch = Character.toLowerCase(ch);
        int chidx = getCharIndex(ch);
        if (chidx >= 0) {
            int binary = CHARSET[chidx][index];
            return binary + 1;
        } else {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }
    }

    /**
     * Encodes a character to a logic handler.
     * @param logic the logic handler to send events to
     * @param ch the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char ch) {
        ch = Character.toLowerCase(ch);
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(ch).toString());
        for (byte i = 0; i < 7; i++) {
            final int width = widthAt(ch, i);
            final boolean black = ((i % 2) == 0);
            logic.addBar(black, width);
        }
        logic.endBarGroup();
    }

    private void handleChecksum(StringBuffer sb) {
        if ((getChecksumMode() == ChecksumMode.CP_ADD)
                || (getChecksumMode() == ChecksumMode.CP_CHECK)) {
            throw new UnsupportedOperationException(
                "No checksums are currently supported for Codabar symbols");
        } else if (getChecksumMode() == ChecksumMode.CP_IGNORE) {
            return;
        } else if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            return; //equals ignore
        }
    }

    private StringBuffer prepareMessage(String msg) {
        //Remove start/stop in standard mode if present
        if (isStartStopChar(msg.charAt(0)) && isStartStopChar(msg.charAt(msg.length() - 1))) {
            return new StringBuffer(msg.substring(1, msg.length() - 1));
        } else {
            return new StringBuffer(msg);
        }
    }

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to send generated events to
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        StringBuffer sb = new StringBuffer(msg);

        handleChecksum(sb);

        //Checksum handling as requested
        String effMsg = sb.toString();
        String displayMsg;

        if (displayStartStop) {
            displayMsg = sb.toString();
        } else {
            displayMsg = prepareMessage(msg).toString();
        }

        logic.startBarcode(effMsg, displayMsg);

        for (int i = 0; i < sb.length(); i++) {
            if (i > 0) {
                //Intercharacter gap
                logic.addBar(false, 1);
            }
            final char ch = sb.charAt(i);
            if (!isValidChar(ch)) {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
            encodeChar(logic, ch);
        }

        logic.endBarcode();
    }

}
