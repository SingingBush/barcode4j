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

/* $Id: PDF417ErrorCorrection.java,v 1.1 2006-06-22 09:01:16 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

/**
 * PDF417 error correction code following the algorithm described in ISO/IEC 15438:2001(E) in
 * chapter 4.10.
 * 
 * @version $Id: PDF417ErrorCorrection.java,v 1.1 2006-06-22 09:01:16 jmaerki Exp $
 */
public class PDF417ErrorCorrection implements PDF417Constants {

    /**
     * Determines the number of error correction codewords for a specified error correction
     * level.
     * @param errorCorrectionLevel the error correction level (0-8)
     * @return the number of codewords generated for error correction
     */
    public static int getErrorCorrectionCodewordCount(int errorCorrectionLevel) {
        if (errorCorrectionLevel < 0 || errorCorrectionLevel > 8) {
            throw new IllegalArgumentException("Error correction level must be between 0 and 8!");
        }
        return 1 << (errorCorrectionLevel + 1);
    }
    
    /**
     * Returns the recommended minimum error correction level as described in annex E of
     * ISO/IEC 15438:2001(E).
     * @param n the number of data codewords
     * @return the recommended minimum error correction level
     */
    public static int getRecommendedMinimumErrorCorrectionLevel(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        } else if (n >= 1 && n <= 40) {
            return 2;
        } else if (n >= 41 && n <= 160) {
            return 3;
        } else if (n >= 161 && n <= 320) {
            return 4;
        } else if (n >= 321 && n <= 863) {
            return 5;
        } else {
            throw new IllegalArgumentException("No recommendation possible");
        }
    }
    
    /**
     * Generates the error correction codewords according to 4.10 in ISO/IEC 15438:2001(E).
     * @param dataCodewords the data codewords
     * @param errorCorrectionLevel the error correction level (0-8)
     * @return the String representing the error correction codewords
     */
    public static String generateErrorCorrection(String dataCodewords, int errorCorrectionLevel) {
        int k = getErrorCorrectionCodewordCount(errorCorrectionLevel);
        char[] e = new char[k];
        int sld = dataCodewords.length();
        int t1, t2, t3;
        for (int i = 0; i < sld; i++) {
            t1 = (dataCodewords.charAt(i) + e[e.length - 1]) % 929;
            for (int j = k - 1; j >= 1; j--) {
                t2 = (t1 * EC_COEFFICIENTS[errorCorrectionLevel][j]) % 929;
                t3 = 929 - t2;
                e[j] = (char)((e[j - 1] + t3) % 929);
            }
            t2 = (t1 * EC_COEFFICIENTS[errorCorrectionLevel][0]) % 929;
            t3 = 929 - t2;
            e[0] = (char)(t3 % 929);
            //System.out.println(HighLevelEncoderTest.visualize(new String(e)));
        }
        StringBuffer sb = new StringBuffer(k);
        for (int j = k - 1; j >= 0; j--) {
            if (e[j] != 0) {
                e[j] = (char)(929 - e[j]);
            }
            sb.append(e[j]);
        }
        return sb.toString();
    }
    
}
