/*
 * Copyright 2006 Jeremias Maerki.
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

/* $Id: DataMatrixErrorCorrection.java,v 1.2 2006-12-22 15:58:27 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Error Correction Code for ECC200.
 * 
 * @version $Id: DataMatrixErrorCorrection.java,v 1.2 2006-12-22 15:58:27 jmaerki Exp $
 */
public class DataMatrixErrorCorrection implements DataMatrixReedSolomonFactors {

    private static final int MODULO_VALUE = 0x12d;

    private static final int[] LOG;
    private static final int[] ALOG;
    
    static {
        //Create log and antilog table
        LOG = new int[256];
        ALOG = new int[255];
        
        int p = 1;
        for (int i = 0; i < 255; i++) {
            ALOG[i] = p;
            LOG[p] = i;
            p <<= 1;
            if (p >= 256) {
                p ^= MODULO_VALUE;
            }
        }
    }
    
    /**
     * Creates the ECC200 error correction for an encoded message.
     * @param codewords the codewords
     * @param symbolInfo information about the symbol to be encoded
     * @return the codewords with interleaved error correction.
     */
    public static String encodeECC200(String codewords, DataMatrixSymbolInfo symbolInfo) {
        if (codewords.length() != symbolInfo.dataCapacity) {
            throw new IllegalArgumentException(
                    "The number of codewords does not match the selected symbol");
        }
        StringBuffer sb = new StringBuffer(symbolInfo.dataCapacity + symbolInfo.errorCodewords);
        sb.append(codewords);
        int blockCount = symbolInfo.getInterleavedBlockCount();
        if (blockCount == 1) {
            String ecc = createECCBlock(codewords, symbolInfo.errorCodewords);
            sb.append(ecc);
        } else {
            sb.setLength(sb.capacity());
            int[] dataSizes = new int[blockCount];
            int[] errorSizes = new int[blockCount];
            int[] startPos = new int[blockCount];
            for (int i = 0; i < blockCount; i++) {
                dataSizes[i] = symbolInfo.getDataLengthForInterleavedBlock(i + 1);
                errorSizes[i] = symbolInfo.getErrorLengthForInterleavedBlock(i + 1);
                startPos[i] = 0;
                if (i > 0) {
                    startPos[i] = startPos[i - 1] + dataSizes[i];
                }
            }
            for (int block = 0; block < blockCount; block++) {
                StringBuffer temp = new StringBuffer(dataSizes[block]);
                for (int d = block; d < symbolInfo.dataCapacity; d += blockCount) {
                    temp.append(codewords.charAt(d));
                }
                String ecc = createECCBlock(temp.toString(), errorSizes[block]);
                int pos = 0;
                for (int e = block; e < errorSizes[block] * blockCount; e += blockCount) {
                    sb.setCharAt(symbolInfo.dataCapacity + e, ecc.charAt(pos++));
                }
            }
        }
        return sb.toString();
        
    }

    private static String createECCBlock(String codewords, int numECWords) {
        return createECCBlock(codewords, 0, codewords.length(), numECWords);
    }
    
    private static String createECCBlock(String codewords, int start, int len, int numECWords) {
        int table = -1;
        for (int i = 0; i < FACTOR_SETS.length; i++) {
            if (FACTOR_SETS[i] == numECWords) {
                table = i;
                break;
            }
        }
        if (table < 0) {
            throw new IllegalArgumentException(
                    "Illegal number of error correction codewords specified: " + numECWords);
        }
        int[] poly = DataMatrixReedSolomonFactors.FACTORS[table];
        char[] ecc = new char[numECWords];
        for (int i = 0; i < numECWords; i++) {
            ecc[i] = 0;
        }
        for (int i = start; i < start + len; i++) {
            int m = ecc[numECWords - 1] ^ codewords.charAt(i);
            for (int k = numECWords - 1; k > 0; k--) {
                if (m != 0 && poly[k] != 0) {
                    ecc[k] = (char)(ecc[k - 1] ^ ALOG[(LOG[m] + LOG[poly[k]]) % 255]);
                } else {
                    ecc[k] = ecc[k - 1];
                }
            }
            if (m != 0 && poly[0] != 0) {
                ecc[0] = (char)(ALOG[(LOG[m] + LOG[poly[0]]) % 255]);
            } else {
                ecc[0] = 0;
            }
        }
        char[] eccReversed = new char[numECWords];
        for (int i = 0; i < numECWords; i++) {
            eccReversed[i] = ecc[numECWords - i - 1]; 
        }
        return String.valueOf(eccReversed);
    }
    
}
