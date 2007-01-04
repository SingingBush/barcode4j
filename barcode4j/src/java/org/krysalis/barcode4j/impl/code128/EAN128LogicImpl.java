/*
 * Copyright 2005 Dietmar Bürkle.
 * generateBarcodeLogic: 
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
package org.krysalis.barcode4j.impl.code128;

import java.util.StringTokenizer;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the EAN 128 barcode.
 * 
 * @author Dietmar Bürkle, Jeremias Maerki (generateBarcodeLogic)
 */
public class EAN128LogicImpl { //extends Code128LogicImpl{
    public final static byte CONSTLenMax = 48; // Max according to EAN128 specification.

    public final static byte TYPENumTestCheckDigit = 4;
    public final static byte TYPENumReplaceCheckDigit = 5;
    public final static byte TYPENumAddCheckDigit = 6;


    private EAN128AI[] ais = null;
    private char groupSeparator = EAN128Bean.DEFAULT_GROUP_SEPARATOR; //GroupSeperator not Code128LogicImpl.FNC_1; 
    private char checkDigitMarker = EAN128Bean.DEFAULT_CHECK_DIGIT_MARKER; 
    private boolean omitBrackets = false;
    
    private String msgCache = null; 
    private StringBuffer code128Msg = new StringBuffer(CONSTLenMax);
    private StringBuffer humanReadableMsg = new StringBuffer(CONSTLenMax);
    private int[] encodedMsg = new int[]{};
    private IllegalArgumentException exception = null;
    
    private boolean checksumADD = true;
    private boolean checksumCHECK = true;
    private boolean strictFixedLen = true;

    public EAN128LogicImpl(ChecksumMode mode, String template, char fnc1) {
        setChecksumMode(mode);
        setTemplate(template);
        this.groupSeparator = fnc1;
    }

    public EAN128LogicImpl(ChecksumMode mode, String template) {
        setChecksumMode(mode);
        setTemplate(template);
    }

    protected void setMessage(String msg) {
        if (msg == null || !msg.equals(msgCache)) { 
            code128Msg.setLength(0);
            humanReadableMsg.setLength(0);
            exception = null;
            if (msg == null) {
                msgCache = null; 
            } else {
                msgCache = msg;
                
                code128Msg.append(Code128LogicImpl.FNC_1);
                addAIs(msg);
                
                Code128Encoder encoder = new DefaultCode128Encoder();
                encodedMsg = encoder.encode(getCode128Msg());
            }
        } else if (exception != null) {
            throw exception;
        }
    }

    /** @return the original message */
    public String getMessage() {
        return this.msgCache;
    }
    
    /**
     * Encodes a message into an array of character set indexes. 
     * @param msg the message to encode
     * @return the requested array of character set indexes
     * @see #getEncoder()
     */
    public int[] getEncodedMessage(String msg) {
        setMessage(msg);
        return encodedMsg;
    }

    /**
     * Generates the barcode logic
     * @param logic the logic handler to receive the generated events
     * @param msg the message to encode
     */
    public void generateBarcodeLogic(ClassicBarcodeLogicHandler logic, String msg) {
        setMessage(msg);
        
        Code128LogicImpl c128 = new Code128LogicImpl(); 
        logic.startBarcode(msg, getHumanReadableMsg());
        for (int i = 0; i < encodedMsg.length; i++) {
            c128.encodeChar(logic, encodedMsg[i]);
        }
        
        //Calculate checksum
        int checksum = encodedMsg[0];
        for (int i = 1; i < encodedMsg.length; i++) {
            checksum += i * encodedMsg[i];
        }
        checksum = checksum % 103;
        c128.encodeChar(logic, checksum);
        
        c128.encodeStop(logic);

        logic.endBarcode();
    }


    public void addAIs(String msg) {
        int offset = 0;
        int i = 0;
        EAN128AI ai = null;
        while (offset < msg.length()) {
            if (ais == null) {
                ai = null;
            } else { 
                try {
                    ai = ais[i++]; 
                } catch (IndexOutOfBoundsException e) {
                    throw getException("Message has more AIs than template (template has " 
                            + ais.length +")");
                }
            }
            offset = addAI(msg, offset, ai);
        }
    }
    
    private int findGroupSeparator(String msg, int start){
        int retGS = msg.indexOf(groupSeparator, start);
        if (groupSeparator == Code128LogicImpl.FNC_1) return retGS;
        int retF = msg.indexOf(Code128LogicImpl.FNC_1, start);
        if (retGS <= 0) return retF;
        if (retF  <= 0) return retGS;
        return Math.min(retGS, retF);
    }
    
    public int addAI(String msg, int offset, EAN128AI ai) {
        if (msg == null) {
            throw getException("Message is empty!");
        }
        try {
            if (ai == null) {
                ai = EAN128AI.getAI(msg, offset);
            }
        } catch (Exception e) {
            throw getException(e.getMessage());
        }
        byte lenID = ai.lenID;
//        byte type = ai.type[0];
        byte lenMin = ai.lenMinAll;
        byte lenMax = ai.lenMaxAll;
        
        if (!omitBrackets) humanReadableMsg.append('(');
        humanReadableMsg.append(msg.substring(offset, offset + lenID));
        code128Msg.append(msg.substring(offset, offset + lenID)); //BUG fixed 15.08!!
        if (!omitBrackets) humanReadableMsg.append(')');
        boolean doChecksumADD = false;
        int[] startA = new int[ai.type.length + 1]; 
        startA[0] = offset;
//        start[1] = offset + lenID;
        int newOffset = findGroupSeparator(msg, offset);
        if (newOffset < 0) {
            newOffset = msg.length();
        }
        if (newOffset < offset + lenID + lenMin) {
            if (checksumADD && ai.canDoChecksumADD 
                    && newOffset == offset + lenID + lenMin - 1) {
                doChecksumADD = true;
            } else if (ai.fixed && newOffset < msg.length()) {
                throw getException("FNC1 not allowed in fixed length field: \"" 
                    + msg.substring(offset + lenID, 
                            Math.min(msg.length(), offset + lenID + lenMax)) + "\"!");
            } else {
                throw getException("Field \"" + msg.substring(offset + lenID, newOffset) 
                    + "\" too short! Length should be " + lenMin + " at least!");
            }
            
        }
        if (newOffset > offset + lenID + lenMax) {
            if (ai.fixed || !strictFixedLen) {
                newOffset = offset + lenID + lenMax;
            } else {
                throw getException(
                        "Variable length field \"" + msg.substring(offset + lenID, newOffset) 
                        + "\" too long! Length should be " + lenMax + " at the most!");
            }
        }
        int start = offset + lenID, end;
        for (byte i = 0; i < ai.type.length; i++, start = end) {
            startA[i + 1] = start;
            if (ai.lenMin[i] == ai.lenMax[i]) { 
                end = start + ai.lenMin[i];
            } else {
                end = newOffset - ai.minLenAfterVariableLen;
            }
//            if (ai.checkDigit[i] != CheckDigit.CDNone && !doChecksumADD && checksumCHECK){
//                char cd1 = CheckDigit.calcCheckdigit(msg, cdStart, start, ai.checkDigit[i]);
//                char cd2 = msg.charAt(start);
//                if (cd1 != cd2)
//                    throw getException("Checkdigit is wrong! Correct is " + cd1  
//                        + " but I found " + cd2 + "!", msg.substring(cdStart, start));
//                humanReadableMsg.append(cd1);
//                code128Msg.append(cd1);
//            } else if (ai.checkDigit[i] == CheckDigit.CDNone || !doChecksumADD) {
            if (doChecksumADD && i == ai.type.length - 1) { //ai.checkDigit[i] != CheckDigit.CDNone) {
                char c = CheckDigit.calcCheckdigit(msg, 
                        startA[ai.checkDigitStart[i]], start, CheckDigit.CD31);
                humanReadableMsg.append(c);
                code128Msg.append(c);
                if (newOffset < msg.length() 
                    && (msg.charAt(newOffset) == groupSeparator 
                            || msg.charAt(newOffset) == Code128LogicImpl.FNC_1)) {
                    newOffset++;
                }
            } else {
                checkType(ai, i, msg, start, end, startA[ai.checkDigitStart[i]]);
//                humanReadableMsg.append(msg.substring(startI, end));
//                code128Msg.append(msg.substring(startI, end));
            }
        }
//        if (doChecksumADD) {
//            char c = CheckDigit.calcCheckdigit(msg, cdStart, newOffset, ai.checkDigit[ai.checkDigit.length-1]);
//            humanReadableMsg.append(c);
//            code128Msg.append(c);
//            if (newOffset < msg.length() && msg.charAt(newOffset) == groupSeparator) {
//                newOffset++;
//            }
//        }
        if (newOffset < msg.length()
                && (msg.charAt(newOffset) == groupSeparator
                        || msg.charAt(newOffset) == Code128LogicImpl.FNC_1)) {
            //TODO Needed for 8001?...
            newOffset++;
//            code128Msg.append(Code128LogicImpl.FNC_1);
        }
        if (!ai.fixed && newOffset < msg.length()) {
            code128Msg.append(Code128LogicImpl.FNC_1);
        }
        return newOffset;
        //TODO check that every id appears only once in the barcode
        //TODO check that encodedMsg is never larger than 35
        //TODO check that message never larger than 48 (does this include all groupSeparator?)
        //TODO show only 13 digits of the EAN in human readable part (if the first digit is '0')   
    }

    private void checkType(EAN128AI ai, byte idx, String msg, int start, int end, int cdStart) {
        byte type = ai.type[idx]; 
        if (type == EAN128AI.TYPEError) {
            throw getException("This AI is not allowed by configuration! (" 
                + ai.toString() + ")");
            
        } else if (type == EAN128AI.TYPEAlpha) {
            for (int i = end - 1; i >= start; i--) {
                if (msg.charAt(i) > 128 || Character.isDigit(msg.charAt(i))) 
                    throw getException("Character \'" + msg.charAt(i) 
                        + "\' must be a valid ASCII byte but not number!",
                        msg.substring(start, i));
            }
        } else if (type == EAN128AI.TYPEAlphaNum) {
            for (int i = end - 1; i >= start; i--) {
                if (msg.charAt(i) > 128) 
                    throw getException("Character \'" + msg.charAt(i) 
                        + "\' must be a valid ASCII byte!",
                        msg.substring(start, i));
            }
        } else {
            if (ai.isCheckDigit(idx) && checksumCHECK) {
                char cd1 = CheckDigit.calcCheckdigit(msg, cdStart, start, CheckDigit.CD31);
                char cd2 = msg.charAt(start);
                if (cd2 == checkDigitMarker) cd2 = cd1;
                if (cd1 != cd2) {
                    throw getException("Checkdigit is wrong! Correct is " + cd1  
                        + " but I found " + cd2 + "!");
                }
                humanReadableMsg.append(cd1);
                code128Msg.append(cd1);
                return;                        
            }
            for (int i = end - 1; i >= start; i--) {
                if (!Character.isDigit(msg.charAt(i))) 
                    throw getException("Character \'" + msg.charAt(i) 
                        + "\' must be a Digit!",
                        msg.substring(start, i));
            }
            if (type == EAN128AI.TYPENumDate) {
                char cm1 = msg.charAt(start + 2), cm2 = msg.charAt(start + 3);
                char cd1 = msg.charAt(start + 4), cd2 = msg.charAt(start + 5);
                if ((cm1 == '0' && cm2 == '0')
                        || (cm1 == '1' && cm2 > '2')
                        || cm1 > '1') 
                    throw getException("Illegal Month \"" + cm1 + cm2 + "\"!", 
                        msg.substring(start, start + 2));
                if ((cd1 == '3' && cd2 > '1') || cd1 > '3') 
                    throw getException("Illegal Day \"" + cd1 + cd2 + "\"!", 
                        msg.substring(start, start + 4));
            }
        }    
        humanReadableMsg.append(msg.substring(start, end));
        code128Msg.append(msg.substring(start, end));
    }

    private char getIDChar(String msg, int offset) {
        char ret;
        try {
            ret = msg.charAt(offset);
        } catch (Exception e) {
            throw getException("Unable to read last ID: Message too short!");
        }
        if (!Character.isDigit(ret))
            throw getException("Unable to read last ID: Characters must be numerical!");
        return ret;
    }

    private IllegalArgumentException getException(String text) {
        return getException(text, "");
    }
    
    private IllegalArgumentException getException(String text, String msgOk) {
        if (msgOk == null) msgOk = "";
        if (humanReadableMsg.length() > 1 || msgOk.length() > 0) 
            text = text + " Accepted start of Message: \"" 
                + humanReadableMsg.toString() + msgOk + "\"";
        exception = new IllegalArgumentException(text);
        return exception;
    }


    public String getCode128Msg() {
        return code128Msg.toString();
    }
    
    public String getHumanReadableMsg() {
        return humanReadableMsg.toString();
    }
    
    /** @see java.lang.Object#toString() */
    public String toString() {
        return getHumanReadableMsg();
    }

    public void setChecksumMode(ChecksumMode mode) {
        if (mode == ChecksumMode.CP_AUTO) {
            checksumADD = true;
            checksumCHECK = true;
        } else if (mode == ChecksumMode.CP_ADD) {
            checksumADD = true;
            checksumCHECK = false;
        } else if (mode == ChecksumMode.CP_CHECK) {
            checksumADD = false;
            checksumCHECK = false;
        } else {
            checksumADD = false;
            checksumCHECK = false;
            //Shouldn't happen because of validateMessage
            throw new RuntimeException("Internal error");
        }
    }
    /**
     * @return
     */
    public char getGroupSeparator() {
        return groupSeparator;
    }

    /**
     * @param c
     */
    public void setGroupSeparator(char c) {
        groupSeparator = c;
    }

    /**
     * @param string
     */
    public void setTemplate(String string) {
        ais = null;
        if (string == null || string.trim().length() == 0) return;
        StringTokenizer st = new StringTokenizer(string, "()", false);
        int count = st.countTokens();
        if (count % 2 != 0) return; //TODO ERROR!
        count /= 2;
        ais = new EAN128AI[count];
        try {
            for (int i = 0; i < count; i++) {
                ais[i] = EAN128AI.parseSpec(st.nextToken(), st.nextToken());
            }
        } catch (Exception e) {
            // TODO: handle exception
            ais = null;
        }
    }

    /**
     * @return
     */
    public char getCheckDigitMarker() {
        return checkDigitMarker;
    }

    /**
     * @param c
     */
    public void setCheckDigitMarker(char c) {
        checkDigitMarker = c;
    }

    /**
     * @return
     */
    public boolean isOmitBrackets() {
        return omitBrackets;
    }

    /**
     * @param b
     */
    public void setOmitBrackets(boolean b) {
        omitBrackets = b;
    }

}
