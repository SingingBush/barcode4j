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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * This class is an implementation of the EAN 128 barcode.
 *
 * @author Dietmar Bürkle, Jeremias Maerki (generateBarcodeLogic)
 */
public class EAN128LogicImpl {
    private static final byte MAX_LENGTH = 48; // Max according to EAN128 specification.

//    private static final byte TYPENumTestCheckDigit = 4;
//    private static final byte TYPENumReplaceCheckDigit = 5;
//    private static final byte TYPENumAddCheckDigit = 6;


    private EAN128AI[] ais = null;

    private char groupSeparator;

    private char checkDigitMarker = EAN128Bean.DEFAULT_CHECK_DIGIT_MARKER;
    private boolean omitBrackets = false;

    private String msgCache = null;
    private final StringBuffer code128Msg = new StringBuffer(MAX_LENGTH);
    private final StringBuffer humanReadableMsg = new StringBuffer(MAX_LENGTH);
    private int[] encodedMsg = new int[] {};
    private IllegalArgumentException exception = null;

    private boolean checksumADD = true;
    private boolean checksumCHECK = true;

    /**
     * @param mode ChecksumMode
     * @param template String
     * @param groupSeparator char
     */
    public EAN128LogicImpl(ChecksumMode mode, @Nullable String template, char groupSeparator) {
        setChecksumMode(mode);
        setTemplate(template);
        this.groupSeparator = groupSeparator;
    }

    /**
     * Constructor that defaults to using {@link EAN128Bean#DEFAULT_GROUP_SEPARATOR}
     * @param mode ChecksumMode
     * @param template String
     */
    public EAN128LogicImpl(ChecksumMode mode, @Nullable String template) {
        this(mode, template, EAN128Bean.DEFAULT_GROUP_SEPARATOR);
    }

    protected void setMessage(@Nullable String msg) {
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

                final Code128Encoder encoder = new DefaultCode128Encoder();
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
     * @see DefaultCode128Encoder#encode(String)
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

        if (!(encodedMsg.length > 0)) {
            // this should never happen unless perhaps an empty string is used for the message
            throw new RuntimeException("Encoded message was empty");
        }

        final Code128LogicImpl c128 = new Code128LogicImpl();
        logic.startBarcode(msg, getHumanReadableMsg());
        for (int i : encodedMsg) {
            c128.encodeChar(logic, i);
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

    /**
     * @param msg The barcode message
     */
    public void addAIs(@NotNull String msg) {
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
                    throw getException("Message has more AIs than template (template has " + ais.length + ")");
                }
            }
            offset = addAI(msg, offset, ai);
        }
    }

    private int findGroupSeparator(String msg, int start) {
        int retGS = msg.indexOf(groupSeparator, start);
        if (groupSeparator == Code128LogicImpl.FNC_1) {
            return retGS;
        }
        int retF = msg.indexOf(Code128LogicImpl.FNC_1, start);
        if (retGS <= 0) {
            return retF;
        }
        if (retF <= 0) {
            return retGS;
        }
        return Math.min(retGS, retF);
    }

    /**
     * @param msg String
     * @param offset int
     * @param ai EAN128AI
     * @return int
     */
    public int addAI(@NotNull String msg, int offset, @Nullable EAN128AI ai) {
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

        if (!omitBrackets) {
            humanReadableMsg.append('(');
        }
        humanReadableMsg.append(msg.substring(offset, offset + lenID));
        code128Msg.append(msg.substring(offset, offset + lenID)); //BUG fixed 15.08!!
        if (!omitBrackets) {
            humanReadableMsg.append(')');
        }
        boolean doChecksumADD = false;
        int[] startA = new int[ai.type.length + 1];
        startA[0] = offset;
//        start[1] = offset + lenID;
        int newOffset = findGroupSeparator(msg, offset);
        if (newOffset < 0) {
            newOffset = msg.length();
        }
        if (newOffset < offset + lenID + lenMin) {
            if (checksumADD && ai.canDoChecksumADD && newOffset == offset + lenID + lenMin - 1) {
                doChecksumADD = true;
            } else if ((ai.fixed || lenMin == lenMax) && newOffset < msg.length()) {
                throw getException("FNC1 not allowed in fixed length field: \""
                    + msg.substring(offset + lenID,
                            Math.min(msg.length(), offset + lenID + lenMax)) + "\"!");
            } else {
                throw getException("Field \"" + msg.substring(offset + lenID, newOffset)
                    + "\" too short! Length should be " + lenMin + " at least!");
            }

        }
        if (newOffset > offset + lenID + lenMax) {
            if (ai.fixed || lenMin == lenMax) {
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
                char c = CheckDigit.calcCheckdigit(msg, startA[ai.checkDigitStart[i]], start, CheckDigit.CD31);
                humanReadableMsg.append(c);
                code128Msg.append(c);
                if (newOffset < msg.length() && isGroupSeparator(msg.charAt(newOffset))) {
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
        if (newOffset < msg.length() && isGroupSeparator(msg.charAt(newOffset))) {
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

    private boolean isGroupSeparator(char ch) {
        return (ch == groupSeparator || ch == Code128LogicImpl.FNC_1);
    }

    private void checkType(@NotNull EAN128AI ai, byte idx, @NotNull String msg, int start, int end, int cdStart) {
        byte type = ai.type[idx];
        if (type == EAN128AI.TYPEError) {
            throw getException("This AI is not allowed by configuration! (" + ai.toString() + ")");
        } else if (type == EAN128AI.TYPEAlpha) {
            for (int i = end - 1; i >= start; i--) {
                if (msg.charAt(i) > 128 || Character.isDigit(msg.charAt(i))) {
                    throw getException("Character '" + msg.charAt(i)
                            + "' must be a valid ASCII byte but not number!",
                            msg.substring(start, i));
                }
            }
        } else if (type == EAN128AI.TYPEAlphaNum) {
            for (int i = end - 1; i >= start; i--) {
                if (msg.charAt(i) > 128) {
                    throw getException("Character '" + msg.charAt(i)
                            + "' must be a valid ASCII byte!",
                            msg.substring(start, i));
                }
            }
        } else {
            if (ai.isCheckDigit(idx) && checksumCHECK) {
                char cd1 = CheckDigit.calcCheckdigit(msg, cdStart, start, CheckDigit.CD31);
                char cd2 = msg.charAt(start);
                if (cd2 == checkDigitMarker) {
                    cd2 = cd1;
                }
                if (cd1 != cd2) {
                    throw getException("Check digit is wrong! Correct is " + cd1 + " but I found " + cd2 + "!");
                }
                humanReadableMsg.append(cd1);
                code128Msg.append(cd1);
                return;
            }
            for (int i = end - 1; i >= start; i--) {
                if (!Character.isDigit(msg.charAt(i))) {
                    throw getException("Character '" + msg.charAt(i)
                        + "' must be a Digit!",
                        msg.substring(start, i));
                }
            }
            if (type == EAN128AI.TYPENumDate) {
                char cm1 = msg.charAt(start + 2), cm2 = msg.charAt(start + 3);
                char cd1 = msg.charAt(start + 4), cd2 = msg.charAt(start + 5);
                if ((cm1 == '0' && cm2 == '0')
                        || (cm1 == '1' && cm2 > '2')
                        || cm1 > '1') {
                    throw getException("Illegal Month \"" + cm1 + cm2 + "\"!",
                        msg.substring(start, start + 2));
                }
                if ((cd1 == '3' && cd2 > '1') || cd1 > '3') {
                    throw getException("Illegal Day \"" + cd1 + cd2 + "\"!",
                        msg.substring(start, start + 4));
                }
            }
        }
        humanReadableMsg.append(msg, start, end);
        code128Msg.append(msg, start, end);
    }

    private char getIDChar(String msg, int offset) {
        char ret;
        try {
            ret = msg.charAt(offset);
        } catch (Exception e) {
            throw getException("Unable to read last ID: Message too short!");
        }
        if (!Character.isDigit(ret)) {
            throw getException("Unable to read last ID: Characters must be numerical!");
        }
        return ret;
    }

    private IllegalArgumentException getException(String text) {
        return getException(text, "");
    }

    private IllegalArgumentException getException(String text, @Nullable String msgOk) {
        if (msgOk == null) {
            msgOk = "";
        }
        if (humanReadableMsg.length() > 1 || !msgOk.isEmpty()) {
            text = text + " Accepted start of Message: \""
                + humanReadableMsg.toString() + msgOk + "\"";
        }
        exception = new IllegalArgumentException(text);
        return exception;
    }

    /**
     * @return a String of the Code 128 message
     */
    public String getCode128Msg() {
        return code128Msg.toString();
    }

    /**
     * @return a String of the human-readable form of the barcode message
     */
    public String getHumanReadableMsg() {
        return humanReadableMsg.toString();
    }

    /** {@inheritDoc} */
    public String toString() {
        return getHumanReadableMsg();
    }

    /**
     * @param mode Can be one of: auto, ignore, add, check
     */
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
     * @return the group separator character. This defaults to {@link EAN128Bean#DEFAULT_GROUP_SEPARATOR}
     */
    public char getGroupSeparator() {
        return groupSeparator;
    }

    /**
     * @param c specify a new group separator
     */
    public void setGroupSeparator(char c) {
        groupSeparator = c;
    }

    /**
     * @param string template string
     */
    public void setTemplate(@Nullable String string) {
        if (string == null || string.trim().isEmpty()) {
            return;
        }
        StringTokenizer st = new StringTokenizer(string, "()", false);
        int count = st.countTokens();
        if (count % 2 != 0) {
            throw new IllegalArgumentException("Cannot parse template: \"" + string);
        }
        count /= 2;

        final EAN128AI[] newTemplates = new EAN128AI[count];
        for (int i = 0; i < count; i++) {
            newTemplates[i] = EAN128AI.parseSpec(st.nextToken(), st.nextToken());
        }
        ais = newTemplates;
    }

    /**
     * @return check digit marker
     */
    public char getCheckDigitMarker() {
        return checkDigitMarker;
    }

    /**
     * @param c check digit marker
     */
    public void setCheckDigitMarker(char c) {
        checkDigitMarker = c;
    }

    /**
     * @return Whether brackets are omitted. By default, this value is false.
     */
    public boolean isOmitBrackets() {
        return omitBrackets;
    }

    /**
     * @param b specify if brackets should be omitted. By default, this value is false.
     */
    public void setOmitBrackets(boolean b) {
        omitBrackets = b;
    }

}
