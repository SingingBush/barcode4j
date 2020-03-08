/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.postnet;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * Implements the United States Postal Service Postnet barcode.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNETLogicImpl.java,v 1.3 2006-11-07 16:42:17 jmaerki Exp $
 */
public class POSTNETLogicImpl {

    private static final byte[][] CHARSET = 
                        {{2, 2, 1, 1, 1},  //0
                         {1, 1, 1, 2, 2},  //1
                         {1, 1, 2, 1, 2},  //2
                         {1, 1, 2, 2, 1},  //3
                         {1, 2, 1, 1, 2},  //4
                         {1, 2, 1, 2, 1},  //5
                         {1, 2, 2, 1, 1},  //6
                         {2, 1, 1, 1, 2},  //7
                         {2, 1, 1, 2, 1},  //8
                         {2, 1, 2, 1, 1}}; //9

    private static final char DASH = '-';

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private boolean displayChecksum = false;

    /**
     * Main constructor
     * @param mode checksum mode
     * @param displayChecksum Controls whether to display checksum
     *   in the human-readable message
     */
    public POSTNETLogicImpl(ChecksumMode mode, boolean displayChecksum) {
        this.checksumMode = mode;
        this.displayChecksum = displayChecksum;
    }

    /**
     * Returns the currently active checksum mode.
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Calculates the checksum for a message to be encoded as an 
     * POSTNET barcode.
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        int tmp = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (isIgnoredChar(msg.charAt(i))) {
                continue;
            }
            tmp += Character.digit(msg.charAt(i), 10);
            if (tmp > 9) {
                tmp -= 10;
            }
        }
        return Character.forDigit((10 - tmp) % 10, 10);
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

    private static boolean isValidChar(char ch) {
        return Character.isDigit(ch) || isIgnoredChar(ch);
    }
    
    /**
     * Checks if a character is an ignored character (such as a '-' (dash)).
     * @param c character to check
     * @return True if the character is ignored
     */
    public static boolean isIgnoredChar(char c) {
        return c == DASH;
    }
    
    /**
     * Removes ignored character from a valid POSTNET message.
     * @param msg valid POSTNET message
     * @return the message but without ignored characters
     */
    public static String removeIgnoredCharacters(final String msg) {
        StringBuffer sb = new StringBuffer(msg.length());
        for (int i = 0; i < msg.length(); i++) {
            final char ch = msg.charAt(i);
            if (!isValidChar(ch)) {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
            if (!isIgnoredChar(ch)) {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private int heightAt(char ch, int index) {
        int chidx = Character.digit(ch, 10);
        if (chidx >= 0) {
            int height = CHARSET[chidx][index];
            return height;
        } else {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }
    }

    /**
     * Encodes a single character.
     * @param logic the logic handler to receive generated events
     * @param c the character to encode
     */
    protected void encodeChar(ClassicBarcodeLogicHandler logic, char c) {
        if (isIgnoredChar(c)) {
            return;  // allow dash, but don't encode
        }
        logic.startBarGroup(BarGroup.MSG_CHARACTER, new Character(c).toString());
        for (byte i = 0; i < 5; i++) {
            int height = heightAt(c, i);
            logic.addBar(true, height);
            addIntercharacterGap(logic);
        }
        logic.endBarGroup();
    }

    private void addIntercharacterGap(ClassicBarcodeLogicHandler logic) {
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
                    + calcChecksum(sb.substring(0, sb.length() - 1)));
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
            throw new UnsupportedOperationException(
                    "Unknown checksum mode: " + getChecksumMode());
        }
    }

    /**
     * Generates the barcode logic
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        StringBuffer sb = new StringBuffer(msg);
        String formattedMsg = handleChecksum(sb);

        logic.startBarcode(sb.toString(), formattedMsg);
        
        // start frame bar
        logic.addBar(true, 2);
        addIntercharacterGap(logic);

        // encode message
        for (int i = 0; i < sb.length(); i++) {
            final char ch = sb.charAt(i);
            if (!isValidChar(ch)) {
                throw new IllegalArgumentException("Invalid character: " + ch);
            } 
            encodeChar(logic, ch);
        }

        // end frame bar
        logic.addBar(true, 2);

        logic.endBarcode();
    }


}
