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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This class keeps Informations about EAN 128 Application Identifiers (AIs).
 * 
 * @author Dietmar Bürkle
 */
public class EAN128AI {
    
    public final static byte CONSTLenMax = 48; // Max according to EAN128 specification.
    public final static byte TYPEAlphaNum = 0;
    public final static byte TYPENum = 1;
    public final static byte TYPEAlpha = 2;    //Unused at the moment, but mentioned by  
                                                //the EAN128 specification.
    public final static byte TYPENumDate = 3;
    public final static byte TYPEError = 4;
    public final static byte TYPECD = 5; //Check digit
    private final static String[] typeToString = {"an", "n", "a", "d", "e", "cd"};

//    public final static byte TYPEAlphaNum421 = 7;


    String id;
    byte lenID, lenMinAll, lenMaxAll;
    byte minLenAfterVariableLen;
    
    byte[] lenMin, lenMax, type, checkDigitStart;
    boolean fixed = false, canDoChecksumADD = false;

    private static String[] fixedLenTable = new String[]{
        "00", "01", "02", "03", "04", 
        "11", "12", "13", "14", "15", "16", "17", "18", "19", 
        "20", 
        "31", "32", "33", "34", "35", "36", 
        "41"};
    private static byte[] fixedLenValueTable = new byte[]{
        20, 16, 16, 16, 18, 
        8, 8, 8, 8, 8, 8, 8, 8, 8, 
        4, 
        10, 10, 10, 10, 10, 10, 
        16};
    private static EAN128AI dft = parseSpecPrivate("xx", "an1-48");
    private static Object[] aiTable = new Object[] 
                                        {dft, dft, dft, dft, dft, dft, dft, dft, dft, dft};
    private static boolean propertiesLoaded = false;

    
    private static class AIProperties extends Properties {
        public synchronized Object put(Object arg0, Object arg1) {
            EAN128AI ai = parseSpecPrivate((String)arg0, (String)arg1);
            try { 
                setAI((String)arg0, ai);
            } catch (Exception e) {
                System.err.println(e);
            }
            return super.put(arg0, arg1);
        }
    }
    
    private static void initFixedLen(String aiName, byte aiLen) {
        byte lenID = (byte)aiName.length();
        EAN128AI ai = new EAN128AI(aiName, "an" + aiLen, lenID, TYPEAlphaNum, aiLen);
        try { 
            setAI(aiName, ai);
        } catch (Exception e) {
            System.err.println(e);
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
    
    public static synchronized void loadProperties(){
        if (propertiesLoaded) return;

        final String filename = "EAN128AIs.properties"; 
        Properties p = new AIProperties();
        try {
            InputStream is = EAN128AI.class.getResourceAsStream(filename);
            if (is == null) {
                throw new FileNotFoundException(filename + " could not be loaded!");
            }
            try {
                p.load(is);
            } finally {
                is.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        propertiesLoaded = true;
    }    
    
    private EAN128AI(String id, byte lenID, byte[] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart){
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
    private static void checkFixed(EAN128AI aiNew, EAN128AI aiOld) {
        if (aiOld.fixed && !aiNew.fixed) {
            if (aiNew.lenMaxAll != aiNew.lenMinAll
                    || aiNew.lenID + aiNew.lenMinAll != aiOld.lenID + aiOld.lenMinAll) {
                throw new IllegalArgumentException("AI \"" + aiNew.toString()
                        + "\" must have fixed len: " + aiOld.lenID + "+" + aiOld.lenMin);
            }
            aiNew.fixed = true;
        }
    }
    private static void SetAIHere(EAN128AI ai, Object[] aitParent) {
        for (int idx = 0; idx <= 9; idx++) {
            SetAIHere(ai, aitParent, idx);
        }
    }
    private static void SetAIHere(EAN128AI aiNew, Object[] aitParent, int idx) {
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
    private static void setAI(String aiName, EAN128AI ai) {
        Object[] aitParent = aiTable; 
        int aiLastRelevantIdx = aiName.length() - 1;
        while (aiLastRelevantIdx >= 0 
                && !Character.isDigit(aiName.charAt(aiLastRelevantIdx))) {
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

    public static EAN128AI parseSpec(String ai, String spec) throws Exception {
        EAN128AI  ret = parseSpecPrivate(ai, spec);
        checkAI(ret);
        return ret;
    }
    
    private static void parseSpecPrivate(int i, String spec, 
            byte[] type, byte[] lenMin, byte[] lenMax, byte[] checkDigitStart) {
        int startLen = 0; 
        checkDigitStart[i] = 1;
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
            lenMin[i] = lenMax[i] = Byte.parseByte(spec.substring(startLen));
        } else if (hyphenIdx == startLen) {
            lenMin[i] = 1;
            lenMax[i] = Byte.parseByte(spec.substring(startLen + 1));
        } else { // hyphenIdx > startLen
            lenMin[i] = Byte.parseByte(spec.substring(startLen, hyphenIdx));
            lenMax[i] = Byte.parseByte(spec.substring(hyphenIdx + 1));
        }

        if (type[i] == TYPENumDate) {
            if (lenMin[i] != 6 || lenMax[i] != 6) { 
                throw new IllegalArgumentException("Date Field must have length 6");
            }
        }
    }
    private static EAN128AI parseSpecPrivate(String ai, String spec) {
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
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot Parse AI: \"" + ai + "\" spec: \"" + spec + "\" ");
        }
    }

    public static boolean checkAI(EAN128AI ai) throws Exception {
        EAN128AI aiCompare = getAIPrivate(ai.id + "0000", 0);
        checkFixed(ai, aiCompare);
        return true;
    }
    
    public static EAN128AI getAI(String msg, int msgStart) throws Exception {
        loadProperties();
        return getAIPrivate(msg, msgStart);
    }
    
    private static EAN128AI getAIPrivate(String msg, int msgStart) throws Exception {
        EAN128AI ret = dft;
        Object o = aiTable;
        int c;
        for (int i = 0; i < msg.length() - msgStart; i++) {
            c = getIDChar(msg, msgStart + i) - '0';
            o = ((Object[])o)[c];
            if (o == null) return dft;
            if (o instanceof EAN128AI) {
                ret = (EAN128AI)o;
                break;
            }
        }
        return ret;
    }
    
    private static char getIDChar(String msg, int msgStart) {
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

    public static final boolean isCheckDigitType(byte type) {
        return (type == TYPECD);
    }
    public final boolean isCheckDigit(byte idx) {
        return isCheckDigitType(type[idx]);
    }

    public static final String getType(byte type) {
        String ret = "?";
        try {
            ret = typeToString[type];
        } catch (Exception e) {}
        return ret;
    }
    
    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append('(').append(id).append(")");
        for (int i = 0; i < lenMin.length; i++) {
            if (i != 0) ret.append('+');
            ret.append(getType(type[i]));
//            if (checkDigit[i] == CheckDigit.CD11)
//                ret.append("w1");
            if (type[i] < TYPEError) {
                ret.append(lenMin[i]);
                if (lenMin[i] != lenMax[i]) 
                    ret.append('-').append(lenMax[i]);                
            } 
        }
        ret.append((fixed) ? " (fixed)" : ""); 
        return ret.toString();
     }
    
}
