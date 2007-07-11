/*
 * Copyright 2002-2004,2007 Jeremias Maerki.
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

/**
 * Default encoder algorithm for Code128 barcode messages.
 *  
 * @version $Id: DefaultCode128Encoder.java,v 1.3 2007-07-11 06:41:00 jmaerki Exp $
 */
public class DefaultCode128Encoder implements Code128Encoder {

    private static final int CODESET_A = 1;
    private static final int CODESET_B = 2;
    private static final int CODESET_C = 4;

    private static final int START_A = 103;
    private static final int START_B = 104;
    private static final int START_C = 105;
    private static final int GOTO_A = 101;
    private static final int GOTO_B = 100;
    private static final int GOTO_C = 99;
    private static final int FNC_1 = 102;
    private static final int FNC_2 = 97;
    private static final int FNC_3 = 96;
    private static final int SHIFT = 98;

    private boolean needA(char c) {
        //Character can't be encoded in B
        return (c < 32);
    }

    private boolean needB(char c) {
        //Character can't be encoded in A
        return (c >= 96) && (c < 128);
    }

    private int encodeAorB(char c, int codeset) {
        //Function chars
        if (c == Code128LogicImpl.FNC_1) {
            return FNC_1;
        }
        if (c == Code128LogicImpl.FNC_2) {
            return FNC_2;
        }
        if (c == Code128LogicImpl.FNC_3) {
            return FNC_3;
        }
        if (c == Code128LogicImpl.FNC_4) {
            if (codeset == CODESET_A) {
                return 101;
            } else {
                return 100;
            }
        }
        //Convert normal characters 
        if (codeset == CODESET_A) {
            if ((c >= 0) && (c < 32)) {
                return c + 64;
            } else if ((c >= 32) && (c <= 95)) {
                return c - 32;
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
        } else if (codeset == CODESET_B) {
            if ((c >= 32) && (c < 128)) {
                return c - 32;
            } else {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
        } else {
            throw new IllegalArgumentException("Only A or B allowed");
        }
    }

    /**
     * Encodes message using code set A, B and C. Tries to use as few characters
     * as possibe 
     * @param message to encoded
     * @return array of code set caracters
     * @see org.krysalis.barcode4j.impl.code128.Code128Encoder#encode(java.lang.String)
     */
    public int[] encode(String message) {

        // Allocate enough space
        int[] encoded = new int[message.length() * 2];
        int encodedPos = 0;
        int startAorBPos = 0;
        int messageLength = message.length();
        int messagePos = 0;

        // iterate over all characters in message
        while (messagePos < messageLength) {

            // count number of C characters starting from current character
            int countC = 0;

            // determine number of characters saved by using codeset C
            int saveChar = 0;

            boolean extraDigitAtEnd = false;
            while (messagePos + countC < messageLength) {
                char character = message.charAt(messagePos + countC);
                if (character >= '0' && character <= '9') {

                    // check for uneven number of digits
                    if (messagePos + countC + 1 == messageLength) {
                        extraDigitAtEnd = true;
                        break;
                    }

                    // check if next character is digit as well
                    character = message.charAt(messagePos + countC + 1);
                    if ((character < '0' || character > '9')) {
                        break;
                    }

                    saveChar++;
                    countC += 2;
                } else if (character == Code128LogicImpl.FNC_1) {
                    countC += 1;
                } else {
                    break;
                }
            }

            // at least 2 characters should be saved to switch to codeset C
            // or whole message is in code set C
            if (saveChar >= 2 || countC == messageLength) {

                // if extra digit at end then skip first digit
                if (extraDigitAtEnd) {
                    messagePos++;
                }

                // write A or B section preceeding this C section
                encodedPos += encodeAordB(message, startAorBPos, messagePos,
                        encoded, encodedPos);

                // set new start to end of C section
                startAorBPos = messagePos + countC;

                // write codeset C section
                encodedPos += encodeC(message, messagePos, startAorBPos,
                        encoded, encodedPos);
            }

            // skip the current codeset C section and the character following it
            messagePos += countC + 1;

        }

        // write A or B section after the (optional) C section
        encodedPos += encodeAordB(message, startAorBPos, messageLength,
                encoded, encodedPos);

        int[] result = new int[encodedPos];
        System.arraycopy(encoded, 0, result, 0, result.length);

        return result;
    }

    /**
     * Encodes section of message in codeset C
     * @param message to encode
     * @param start position of section in message
     * @param finish first position after section in message
     * @param encoded int array to hold encoded message
     * @param startEncodedPos start index in encoded array
     * @return number of integers added to encoding
     */
    private int encodeC(String message, int start, int finish, int[] encoded,
            int startEncodedPos) {

        if (start == finish) {
            return 0;
        }

        int encodedPos = startEncodedPos;

        // start or switch to code set C
        encoded[encodedPos++] = start == 0 ? START_C : GOTO_C;

        int messagePos = start;

        while (messagePos < finish) {
            char character = message.charAt(messagePos);

            if (character == Code128LogicImpl.FNC_1) {
                encoded[encodedPos++] = FNC_1;
                messagePos++;
            } else {
                //Encode the next two digits
                encoded[encodedPos++] = Character.digit(character, 10) * 10
                        + Character.digit(message.charAt(messagePos + 1), 10);
                messagePos += 2;
            }
        }

        // number of characters added
        return encodedPos - startEncodedPos;

    }

    /**
     * Encodes section of message in codeset A or B
     * @param message to encode
     * @param start position of section in message
     * @param finish first position after section in message
     * @param encoded int array to hold encoded message
     * @param startEncodedPos start index in encoded array
     * @return number of integers added to encoding
     */
    private int encodeAordB(String message, int start, int finish,
            int[] encoded, int startEncodedPos) {

        if (start == finish) {
            return 0;
        }

        int encodedPos = startEncodedPos;

        // determine to start with codeset A or B
        boolean inB = true;
        for (int messagePos = start; messagePos < finish; messagePos++) {

            char character = message.charAt(messagePos);

            if (needA(character)) {
                inB = false;
                break;
            } else if (needB(character)) {
                inB = true;
                break;
            }
        }

        // start or switch to correct code set
        if (inB) {
            encoded[encodedPos++] = (start == 0) ? START_B : GOTO_B;
        } else {
            encoded[encodedPos++] = (start == 0) ? START_A : GOTO_A;
        }

        // iterate over characters in message
        for (int messagePos = start; messagePos < finish; messagePos++) {

            char character = message.charAt(messagePos);

            if (inB) {
                // check if current character is not in code set B
                if (needA(character)) {

                    // check for switch or shift
                    if (messagePos + 1 < finish
                            && needA(message.charAt(messagePos + 1))) {
                        encoded[encodedPos++] = GOTO_A;
                        inB = false;
                    } else {
                        encoded[encodedPos++] = SHIFT;
                    }

                    encoded[encodedPos++] = encodeAorB(character, CODESET_A);
                } else {
                    encoded[encodedPos++] = encodeAorB(character, CODESET_B);
                }

            } else {
                // check if current character is not in code set A
                if (needB(character)) {

                    // check for switch or shift
                    if (messagePos + 1 < finish
                            && needB(message.charAt(messagePos + 1))) {
                        encoded[encodedPos++] = GOTO_B;
                        inB = true;
                    } else {
                        encoded[encodedPos++] = SHIFT;
                    }

                    encoded[encodedPos++] = encodeAorB(character, CODESET_B);
                } else {
                    encoded[encodedPos++] = encodeAorB(character, CODESET_A);
                }
            }
        }

        // number of characters added
        return encodedPos - startEncodedPos;

    }
}
