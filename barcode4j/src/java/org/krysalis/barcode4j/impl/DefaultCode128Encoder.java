/*
 * $Id: DefaultCode128Encoder.java,v 1.2 2004-04-28 19:31:00 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2004 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.impl;

/**
 * Default encoder algorithm for Code128 barcode messages.
 *  
 * @author Jeremias Maerki
 */
public class DefaultCode128Encoder implements Code128Encoder {

    private static final int CODESET_A              = 1;
    private static final int CODESET_B              = 2;
    private static final int CODESET_C              = 4;

    private static final int START_A = 103;
    private static final int START_B = 104;
    private static final int START_C = 105;
    private static final int GOTO_A  = 101;
    private static final int GOTO_B  = 100;
    private static final int GOTO_C  = 99;
    private static final int FNC_1   = 102;
    private static final int FNC_2   = 97;
    private static final int FNC_3   = 96;
    private static final int SHIFT   = 98;
    

    /**
     * Determine whether a character is in a particular codeset.
     * @param c character to test
     * @param codeset codeset to check
     * @param second For codeset C only: true if we're checking for the second 
     *   position in a duo.
     * @return true if the character is in the codeset
     */
    private final boolean inCodeset(char c, int codeset, boolean second) {
        switch (codeset) {
            case CODESET_A: return Code128LogicImpl.isInCodeSetA(c);
            case CODESET_B: return Code128LogicImpl.isInCodeSetB(c);
            case CODESET_C: return Code128LogicImpl.canBeInCodeSetC(c, second);
            default: throw new IllegalArgumentException("Invalid codeset");
        }
    }
    
    private boolean needA(char c) {
        //Character can't be encoded in B
        return (c < 32);
    }
    
    private boolean needB(char c) {
        //Character can't be encoded in A
        return (c >= 96) && (c < 128);
    }
    
    private int determineAorB(char c) {
        if (needA(c)) {
            return CODESET_A;
        } else if (Code128LogicImpl.isInCodeSetB(c)) {
            return CODESET_B;
        }
        return 0;
    }
    
    private int getStartControl(int codeset) {
        switch (codeset) {
            case CODESET_A: return START_A;
            case CODESET_B: return START_B;
            case CODESET_C: return START_C;
            default: throw new IllegalArgumentException("Invalid codeset");
        }
    }

    private boolean nextLotInCodeset(String msg, int startpos, int codeset, int count) {
        if (startpos + count > msg.length()) {
            //Prevent ArrayOutOfBoundsException
            return false;
        }
        for (int i = 0; i < count; i++) {
            char c = msg.charAt(startpos + i);
            boolean second = (codeset == CODESET_C) && ((i % 2) != 0);
            if (!inCodeset(c, codeset, second)) {
                return false;
            }
        }
        return true;
    }
    
    private int countCharsInSameCodeset(String msg, int startpos, int codeset) {
        int count = 0;
        while (startpos + count < msg.length()) {
            boolean second = (codeset == CODESET_C) && ((count % 2) != 0);
            if (!inCodeset(msg.charAt(startpos + count), codeset, second)) {
                break;
            }
            count++;
        }
        return count;
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

    /** @see org.krysalis.barcode4j.impl.Code128Encoder */
    public int[] encode(String msg) {
        //Allocate enough space
        int[] encoded = new int[msg.length() * 2 + 5];
        int currentCodeset;
        int respos = 0;
        int msgpos = 0;
        //Determine start control
        if ((msg.length() == 2) 
                && nextLotInCodeset(msg, 0, CODESET_C, 2)) {
            currentCodeset = CODESET_C;
        } else if ((msg.length() >= 4)
                && nextLotInCodeset(msg, 0, CODESET_C, 4)) {
            currentCodeset = CODESET_C;
        } else {
            currentCodeset = determineAorB(msg.charAt(0));
            if (currentCodeset == 0) {
                currentCodeset = CODESET_B; //default
            }
        }
        encoded[respos] = getStartControl(currentCodeset);
        respos++;
        
        //Start encoding
        while (msgpos < msg.length()) {
            switch (currentCodeset) {
                case CODESET_C:
                    if (msg.charAt(msgpos) == Code128LogicImpl.FNC_1) {
                        //FNC_1 is the only valid function in Codeset C
                        encoded[respos] = FNC_1;
                        respos++;
                        msgpos++;
                    } else if (nextLotInCodeset(msg, msgpos, CODESET_C, 2)) {
                        //Encode the next two digits
                        encoded[respos] = 
                            Character.digit(msg.charAt(msgpos), 10) * 10 
                            + Character.digit(msg.charAt(msgpos + 1), 10);
                        respos++;
                        msgpos += 2;
                    } else {
                        //Need to change codeset
                        currentCodeset = determineAorB(msg.charAt(msgpos));
                        if (currentCodeset == 0) {
                            currentCodeset = CODESET_B;
                        }
                        if (currentCodeset == CODESET_A) {
                            encoded[respos] = GOTO_A;  
                        } else {
                            encoded[respos] = GOTO_B;
                        }
                        respos++;
                    }
                    break;
                case CODESET_B:
                case CODESET_A:
                    int c = countCharsInSameCodeset(msg, msgpos, CODESET_C);
                    if (c >= 4) {
                        //Change to Codeset C
                        if ((c % 2) != 0) {
                            //Odd number of digits in next passage
                            //Encode the first digit in the old codeset
                            //encoded[respos] = Character.digit(msg.charAt(msgpos), 10) + 16;
                            encoded[respos] = encodeAorB(msg.charAt(msgpos), currentCodeset);
                            respos++;
                            msgpos++;
                        }
                        encoded[respos] = GOTO_C;
                        respos++;
                        currentCodeset = CODESET_C;
                    } else if ((currentCodeset == CODESET_A) && needB(msg.charAt(msgpos))) {
                        //SHIFT or GOTO?
                        if (msgpos + 1 < msg.length()) {
                            int preview = determineAorB(msg.charAt(msgpos + 1));
                            if (preview == CODESET_B) {
                                //More than one B character
                                encoded[respos] = GOTO_B;
                                respos++;
                                currentCodeset = CODESET_B;
                            }
                        }
                        if (currentCodeset == CODESET_A) {
                            //No GOTO issued, we encode with SHIFT
                            encoded[respos] = SHIFT;
                            respos++;
                            encoded[respos] = encodeAorB(msg.charAt(msgpos), CODESET_B);
                            respos++;
                            msgpos++;
                        }
                    } else if ((currentCodeset == CODESET_B) && needA(msg.charAt(msgpos))) {
                        //SHIFT or GOTO?
                        if (msgpos + 1 < msg.length()) {
                            int preview = determineAorB(msg.charAt(msgpos + 1));
                            if (preview == CODESET_A) {
                                //More than one A character
                                encoded[respos] = GOTO_A;
                                respos++;
                                currentCodeset = CODESET_A;
                            }
                        }
                        if (currentCodeset == CODESET_B) {
                            //No GOTO issued, we encode with SHIFT
                            encoded[respos] = SHIFT;
                            respos++;
                            encoded[respos] = encodeAorB(msg.charAt(msgpos), CODESET_A);
                            respos++;
                            msgpos++;
                        }
                    } else {
                        encoded[respos] = encodeAorB(msg.charAt(msgpos), currentCodeset);
                        respos++;
                        msgpos++;
                    }
                    break;
            } /*switch*/
        }
        int[] result = new int[respos];
        System.arraycopy(encoded, 0, result, 0, result.length);
        return result;
    }


}
