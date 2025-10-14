/*
 * Copyright 2005 Dietmar Bürkle.
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This class keeps Information about EAN 128 Application Identifiers (AIs).
 *
 * @author Dietmar Bürkle
 */
public class EAN128AI {

    private static final Logger log = LoggerFactory.getLogger(EAN128AI.class);

    /** Max length according to EAN128 specification */
    public final static byte CONSTLenMax = 48;

    /** Alpha Numeric */
    public final static byte TYPEAlphaNum = 0;

    /** Numeric */
    public final static byte TYPENum = 1;

    /** Alpha */
    // Unused at the moment, but mentioned by the EAN128 specification
    public final static byte TYPEAlpha = 2;

    public final static byte TYPENumDate = 3;

    /** Error */
    public final static byte TYPEError = 4;

    /** Check Digit */
    public final static byte TYPECD = 5;

    private final static String[] typeToString = {"an", "n", "a", "d", "e", "cd"};

//    public final static byte TYPEAlphaNum421 = 7;

    String id;
    byte lenID, lenMinAll, lenMaxAll;
    byte minLenAfterVariableLen;

    byte[] lenMin, lenMax, type, checkDigitStart;
    boolean fixed = false, canDoChecksumADD = false;

    private static final String[] fixedLenTable = new String[]{
        "00", "01", "02", "03", "04",
        "11", "12", "13", "14", "15", "16", "17", "18", "19",
        "20",
        "31", "32", "33", "34", "35", "36",
        "41"};

    private static final byte[] fixedLenValueTable = new byte[]{
        20, 16, 16, 16, 18,
        8, 8, 8, 8, 8, 8, 8, 8, 8,
        4,
        10, 10, 10, 10, 10, 10,
        16};

    private static final EAN128AI dft = parseSpecPrivate("xx", "an1-48");
    private static final Object[] aiTable = new Object[] { dft, dft, dft, dft, dft, dft, dft, dft, dft, dft };
    private static boolean propertiesLoaded = false;

    private static class AIProperties extends Properties {
        @Override
        public synchronized Object put(Object aiName, Object spec) {
            try {
                setAI((String)aiName, parseSpecPrivate((String)aiName, (String)spec));
            } catch (final Exception e) {
                log.error("Unable to set AI from spec", e);
            }
            return super.put(aiName, spec);
        }
    }

    private static void initFixedLen(@NotNull String aiName, byte aiLen) {
        byte lenID = (byte)aiName.length();

        try {
            setAI(aiName, new EAN128AI(aiName, "an" + aiLen, lenID, TYPEAlphaNum, aiLen));
        } catch (final Exception e) {
            log.error("Unable to set AI from aiLen", e);
        }
    }

    static {
        for (int i = 0; i <= 9; i++) {
            initFixedLen("23" + i, (byte)(1 + 9 * i));
        }
        for (int i = fixedLenValueTable.length - 1; i >= 0; i--) {
            initFixedLen(fixedLenTable[i], (byte)(fixedLenValueTable[i] - 2));
        }
//        loadProperties();
    }

    private static synchronized void loadProperties() throws Exception {
        if (propertiesLoaded) return;

        final String filename = "EAN128AIs.properties";

        try (InputStream is = EAN128AI.class.getClassLoader().getResourceAsStream(filename)) {
            final Properties p = new AIProperties();
            p.load(is);
        } catch (IOException e) {
            log.error(filename + " could not be loaded!", e);
            // Not loading EAN128AIs.properties is a severe error.
            // But the code is still usable, if you use templates or do not rely on check digits.
            // Maybe it would be better to throw this exception and find out how this cold happen.
        }
        propertiesLoaded = true;
    }

    private EAN128AI(String id, byte lenID, byte @NotNull [] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart){
        this.id = id;
        this.lenID = lenID;
        this.type = type;
        this.lenMin = lenMin;
        this.lenMax = lenMax;
        this.checkDigitStart = checkDigitStart;
        lenMinAll = lenMaxAll = minLenAfterVariableLen = 0;
        int idxVarLen = type.length;
        int idxFirstChecksum = -1;
//        canDoChecksumADD = true;
        for (int i = 0; i < type.length; i++) {
            lenMinAll += lenMin[i];
            lenMaxAll += lenMax[i];
            if (i > idxVarLen)
                minLenAfterVariableLen += lenMin[i];
            if (lenMin[i] != lenMax[i]) {
                if (idxVarLen < type.length)
                    throw new IllegalArgumentException("Only one Part with var len!"); //TODO
                idxVarLen = i;
            }
            if (idxFirstChecksum == -1 && type[i] == TYPECD)
                idxFirstChecksum = i;
        }
        canDoChecksumADD = (idxFirstChecksum == type.length - 1 && lenMinAll == lenMaxAll);
    }

    private EAN128AI(String id, String spec, byte lenID, byte type, byte len) {
        this(id, lenID,
                new byte[] {type}, new byte[] {len}, new byte[] {len},
                new byte[] {CheckDigit.CDNone});
        fixed = true;
    }

//    public boolean isCheckDigit(int i) {
//        return (type[i] == TYPECDWight31 || type[i] == TYPECDWight1);
//    }

    private static void checkFixed(EAN128AI aiNew, @NotNull EAN128AI aiOld) {
        if (aiOld.fixed && !aiNew.fixed) {
            if (aiNew.lenMaxAll != aiNew.lenMinAll || aiNew.lenID + aiNew.lenMinAll != aiOld.lenID + aiOld.lenMinAll) {
                throw new IllegalArgumentException("AI \"" + aiNew.toString() + "\" must have fixed len: " + aiOld.lenID + "+" + aiOld.lenMinAll);
            }
            aiNew.fixed = true;
        }
    }

    private static void SetAIHere(EAN128AI ai, Object[] aitParent) {
        for (int idx = 0; idx <= 9; idx++) {
            SetAIHere(ai, aitParent, idx);
        }
    }

    private static void SetAIHere(EAN128AI aiNew, Object @NotNull [] aitParent, int idx) {
        Object tmp = aitParent[idx];
        if (tmp instanceof EAN128AI) {
            EAN128AI aiOld = (EAN128AI)tmp;
            if (aiNew.type[0] == TYPEError) {
                aiOld.type[0] = TYPEError;
            } else {
                checkFixed(aiNew, aiOld);
                aitParent[idx] = aiNew;
            }
        } else { //tmp instanceof Object[]
            SetAIHere(aiNew, (Object[])tmp);
        }
    }

    private static void setAI(@NotNull String aiName, EAN128AI ai) {
        Object[] aitParent = aiTable;
        int aiLastRelevantIdx = aiName.length() - 1;
        while (aiLastRelevantIdx >= 0 && !Character.isDigit(aiName.charAt(aiLastRelevantIdx))) {
            aiLastRelevantIdx--;
        }
        Object tmp;
        for (int i = 0; i <= aiLastRelevantIdx; i++) {
            int idx = aiName.charAt(i) - '0';
            if (i == aiLastRelevantIdx) {
                SetAIHere(ai, aitParent, idx);
            } else {
                tmp = aitParent[idx];
                if (tmp instanceof EAN128AI) {
                    tmp = new Object[] {tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp, tmp};
                    aitParent[idx] = tmp;
                }
                aitParent = (Object[])tmp;
            }
        }
    }

    /**
     * @param ai String
     * @param spec String
     * @return EAN128AI
     */
    public static @NotNull EAN128AI parseSpec(String ai, String spec) {
        final EAN128AI ret = parseSpecPrivate(ai, spec);
        checkAI(ret);
        return ret;
    }

    private static void parseSpecPrivate(int i, String spec,
            byte[] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart) {
        int startLen = 0;
        checkDigitStart[i] = 1;
        lenMin[i] = lenMax[i] = -1;
        if (spec.startsWith("an")) {
            type[i] = TYPEAlphaNum;
            startLen = 2;
        } else if (spec.startsWith("a")) {
            type[i] = TYPEAlpha;
            startLen = 1;
        } else if (spec.startsWith("cd")) {
            type[i] = TYPECD;
            if (spec.length() > 2) {
                checkDigitStart[i] = Byte.parseByte(spec.substring(2));
            }
            lenMin[i] = lenMax[i] = 1;
            return;
        } else if (spec.startsWith("n")) {
            type[i] = TYPENum;
            startLen = 1;
        } else if (spec.startsWith("d")) {
            type[i] = TYPENumDate;
            lenMin[i] = lenMax[i] = 6;
            startLen = 1;
        } else if (spec.startsWith("e")) {
            type[i] = TYPEError;
            lenMin[i] = lenMax[i] = 0;
            return;
        } else {
            throw new IllegalArgumentException("Unknown type!");
        }

        int hyphenIdx = spec.indexOf('-', startLen);
        if (hyphenIdx < 0) {
            lenMin[i] = lenMax[i] = parseByte(spec.substring(startLen), lenMin[i], spec);
        } else if (hyphenIdx == startLen) {
            lenMin[i] = 1;
            lenMax[i] = parseByte(spec.substring(startLen + 1), lenMax[i], spec);
        } else { // hyphenIdx > startLen
            lenMin[i] = parseByte(spec.substring(startLen, hyphenIdx), lenMin[i], spec);
            lenMax[i] = parseByte(spec.substring(hyphenIdx + 1), lenMax[i], spec);
        }

        if (type[i] == TYPENumDate) {
            if (lenMin[i] != 6 || lenMax[i] != 6) {
                throw new IllegalArgumentException("Date field (" + spec + ") must have length 6!");
            }
        }
    }

    private static byte parseByte(@NotNull final String val, byte dft, String spec) {
        try {
            return Byte.parseByte(val);
        } catch (Exception e) {
            if (dft == -1) {
                throw new IllegalArgumentException("Can't read field length from \"" + spec + "\"");
            }
            return dft;
        }
    }

    @Contract("_, _ -> new")
    private static @NotNull EAN128AI parseSpecPrivate(@NotNull final String ai, @NotNull String spec) {
        try {
            byte lenID = (byte) ai.trim().length();
            spec = spec.trim();
            StringTokenizer st = new StringTokenizer(spec, "+", false);
            int count = st.countTokens();
            byte[] type = new byte[count];
            byte[] checkDigitStart = new byte[count];
            byte[] lenMin = new byte[count];
            byte[] lenMax = new byte[count];
            for (int i = 0; i < count; i++) {
                parseSpecPrivate(i, st.nextToken(), type, lenMin, lenMax, checkDigitStart);
            }
            return new EAN128AI(ai, lenID, type, lenMin, lenMax, checkDigitStart);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot Parse AI: \"" + ai + "\" spec: \"" + spec + "\" ");
        }
    }

    /**
     * @param ai EAN128AI
     * @return true
     */
    public static boolean checkAI(@NotNull final EAN128AI ai) {
        final EAN128AI aiCompare = getAIPrivate(ai.id + "0000", 0);
        checkFixed(ai, aiCompare);
        return true;
    }

    /**
     * @param msg String
     * @param msgStart int
     * @return EAN128AI
     * @throws Exception if properties cannot be loaded
     */
    public static EAN128AI getAI(@NotNull final String msg, int msgStart) throws Exception {
        loadProperties();
        return getAIPrivate(msg, msgStart);
    }

    private static EAN128AI getAIPrivate(@NotNull final String msg, int msgStart) {
        EAN128AI ret = dft;
        Object o = aiTable;
        int c;
        for (int i = 0; i < msg.length() - msgStart; i++) {
            c = getIDChar(msg, msgStart + i) - '0';
            o = ((Object[])o)[c];
            if (o == null) {
                return dft;
            }
            if (o instanceof EAN128AI) {
                ret = (EAN128AI)o;
                break;
            }
        }
        return ret;
    }

    private static char getIDChar(@NotNull final String msg, int msgStart) {
        char ret;
        try {
            ret = msg.charAt(msgStart);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read AI: Message too short!");
        }
        if (!Character.isDigit(ret)) {
            throw new IllegalArgumentException("Unable to read AI: Characters must be numerical!");
        }
        return ret;
    }

    public static boolean isCheckDigitType(byte type) {
        return (type == TYPECD);
    }
    public final boolean isCheckDigit(byte idx) {
        return isCheckDigitType(type[idx]);
    }

    public static String getType(byte type) {
        try {
            return typeToString[type];
        } catch (Exception e) {
            //ignore
        }
        return "?";
    }

    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append('(').append(id).append(")");
        for (int i = 0; i < lenMin.length; i++) {
            if (i != 0) {
                ret.append('+');
            }
            ret.append(getType(type[i]));
//            if (checkDigit[i] == CheckDigit.CD11)
//                ret.append("w1");
            if (type[i] < TYPEError) {
                ret.append(lenMin[i]);
                if (lenMin[i] != lenMax[i]) {
                    ret.append('-').append(lenMax[i]);
                }
            }
        }
        ret.append((fixed) ? " (fixed)" : "");
        return ret.toString();
     }

}
