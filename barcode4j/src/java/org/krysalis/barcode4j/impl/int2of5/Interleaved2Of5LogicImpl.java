/*
 * Copyright 2002-2004,2009 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the Interleaved 2 of 5 barcode.
 *
 * @version $Id: Interleaved2Of5LogicImpl.java,v 1.3 2009-02-19 10:14:54 jmaerki Exp $
 */
public class Interleaved2Of5LogicImpl {

    private static final byte[][] CHARSET = {{1, 1, 2, 2, 1},
                                             {2, 1, 1, 1, 2},
                                             {1, 2, 1, 1, 2},
                                             {2, 2, 1, 1, 1},
                                             {1, 1, 2, 1, 2},
                                             {2, 1, 2, 1, 1},
                                             {1, 2, 2, 1, 1},
                                             {1, 1, 1, 2, 2},
                                             {2, 1, 1, 2, 1},
                                             {1, 2, 1, 2, 1}};

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private boolean displayChecksum = false;

    /**
     * Main constructor.
     * @param mode the checksum mode
     * @param displayChecksum Controls whether to display checksum
     *   in the human-readable message
     */
    public Interleaved2Of5LogicImpl(ChecksumMode mode, boolean displayChecksum) {
        this.checksumMode = mode;
        this.displayChecksum = displayChecksum;
    }

    /**
     * Returns the current checksum mode
     * @return the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }

    /**
     * Calculates the checksum for a message to be encoded as an
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * @param msg message to calculate the check digit for
     * @param oddMultiplier multiplier to be used for odd positions (usually 3 or 4)
     * @param evenMultiplier multiplier to be used for even positions (usually 1 or 9)
     * @return char the check digit
     */
    public static char calcChecksum(String msg, int oddMultiplier, int evenMultiplier) {
        int oddsum = 0;
        int evensum = 0;
        for (int i = 0; i < msg.length(); i++) {
            if (i % 2 == 0) {
                evensum += Character.digit(msg.charAt(i), 10);
            } else {
                oddsum += Character.digit(msg.charAt(i), 10);
            }
        }
        int check = 10 - ((evensum * oddMultiplier + oddsum * evenMultiplier) % 10);
        if (check >= 10) {
            check = 0;
        }
        return Character.forDigit(check, 10);
    }

    /**
     * Calculates the checksum for a message to be encoded as an
     * Interleaved 2 of 5 barcode. The algorithm is a weighted modulo 10 scheme.
     * This method uses the default specification
     * (ITF-14, EAN-14, SSC-14, DUN14 and USPS).
     * @param msg message to calculate the check digit for
     * @return char the check digit
     */
    public static char calcChecksum(String msg) {
        return calcChecksum(msg, 3, 1);
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

    private int widthAt(char ch, int index) {
        if (Character.isDigit(ch)) {
            int digit = Character.digit(ch, 10);
            int width = CHARSET[digit][index];
            return width;
        } else {
            throw new IllegalArgumentException("Invalid character '" + ch
                    + " (" + Character.getNumericValue(ch)
                    + ")'. Expected a digit.");
        }
    }

    private void encodeGroup(ClassicBarcodeLogicHandler logic, String group) {
        if (group.length() != 2) {
            throw new IllegalArgumentException("Parameter group must have two characters");
        }

        logic.startBarGroup(BarGroup.MSG_CHARACTER, group);
        for (int index = 0; index < 5; index++) {
            logic.addBar(true, widthAt(group.charAt(0), index));
            logic.addBar(false, widthAt(group.charAt(1), index));
        }
        logic.endBarGroup();
    }

    /**
     * Handles the checksum based on the given checksum mode. The checksum is either checked,
     * ignored or attached to the given string buffer.
     * @param sb the string buffer containing the message
     * @param mode the checksum mode
     * @return the updated string after checksum processing (for the human-readable part)
     */
    protected String doHandleChecksum(StringBuffer sb, ChecksumMode mode) {
        if (mode == ChecksumMode.CP_ADD) {
            if (displayChecksum) {
                sb.append(calcChecksum(sb.toString()));
                return sb.toString();
            } else {
                String msg = sb.toString();
                sb.append(calcChecksum(msg));
                return msg;
            }
        } else if (mode == ChecksumMode.CP_CHECK) {
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
        } else if (mode == ChecksumMode.CP_IGNORE) {
            return sb.toString();
        } else {
            throw new UnsupportedOperationException(
                "Invalid checksum mode: " + getChecksumMode());
        }
    }

    /**
     * Handles the checksum based on the checksum mode. The checksum is either checked, ignored
     * or attached to the given string buffer.
     * @param sb the string buffer containing the message
     * @return the updated string after checksum processing (for the human-readable part)
     */
    protected String handleChecksum(StringBuffer sb) {
        if (getChecksumMode() == ChecksumMode.CP_AUTO) {
            //auto = ignore
            return doHandleChecksum(sb, ChecksumMode.CP_IGNORE);
        } else {
            return doHandleChecksum(sb, getChecksumMode());
        }
    }

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        //Checksum handling as requested
        StringBuffer sb = new StringBuffer(msg);
        String formattedMsg = handleChecksum(sb);

        //Length must be even
        if ((sb.length() % 2) != 0) {
            sb.insert(0, '0');
        }

        logic.startBarcode(msg, formattedMsg);

        //Start character
        logic.startBarGroup(BarGroup.START_CHARACTER, null);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.addBar(false, 1);
        logic.endBarGroup();

        //Process string
        int i = 0;
        do {
            encodeGroup(logic, sb.substring(i, i + 2));
            i += 2;
        } while (i < sb.length());

        //End character
        logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
        logic.addBar(true, 2);
        logic.addBar(false, 1);
        logic.addBar(true, 1);
        logic.endBarGroup();

        logic.endBarcode();
    }

}