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

/* $Id: PDF417LogicImpl.java,v 1.1 2006-06-22 09:01:16 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.pdf417;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

/**
 * Top-level class for the logic part of the PDF417 implementation.
 * 
 * @version $Id: PDF417LogicImpl.java,v 1.1 2006-06-22 09:01:16 jmaerki Exp $
 */
public class PDF417LogicImpl {

    /*
    private int cols;
    private int errorCorrectionLevel;
    
    public PDF417LogicImpl(int columns, int errorCorrectionLevel) {
        this.cols = columns;
        this.errorCorrectionLevel = errorCorrectionLevel;
    }*/
    
    /**
     * Calculates the necessary number of rows as described in annex Q of ISO/IEC 15438:2001(E).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param k the number of error correction codewords
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @return the number of rows in the symbol (r)
     */
    public static int getNumberOfRows(int m, int k, int c) {
        int r = ((m + 1 + k) / c) + 1;
        if (c * r >= (m + 1 + k + c)) {
            r--;
        }
        if (r > 90) {
            throw new IllegalArgumentException(
                    "The resultant number of rows for this barcode exceeds 90."
                    + " Please increase the number of columns or decrease the error correction"
                    + " level to reduce the number of rows.");
        }
        if (r < 3) {
            throw new IllegalArgumentException(
                    "The resultant number of rows is less than 3."
                    + " Please decrease the number of columns or increase the error correction"
                    + " level to increase the number of rows.");
        }
        return r;
    }
    
    /**
     * Calculates the number of pad codewords as described in 4.9.2 of ISO/IEC 15438:2001(E). 
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param k the number of error correction codewords
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @param r the number of rows in the symbol
     * @return the number of pad codewords
     */
    public static int getNumberOfPadCodewords(int m, int k, int c, int r) {
        int n = c * r - k;
        if (n > m + 1) {
            return (n - m) - 1;
        } else {
            return 0;
        }
    }
    
    /**
     * Calculates the number of data codewords (equals the Symbol Length Descriptor).
     * @param m the number of source codewords prior to the additional of the Symbol Length
     *          Descriptor and any pad codewords
     * @param errorCorrectionLevel the error correction level (value between 0 and 8) 
     * @param c the number of columns in the symbol in the data region (excluding start, stop and
     *          row indicator codewords)
     * @return the number of data codewords
     */
    public static int getNumberOfDataCodewords(int m, int errorCorrectionLevel, int c) {
        int k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(errorCorrectionLevel);
        int r = getNumberOfRows(m, k, c);
        return c * r - k;
    }
    
    private void encodeChar(int pattern, int len, ClassicBarcodeLogicHandler logic) {
        int map = 1 << len - 1;
        boolean last = (pattern & map) != 0; //Initialize to inverse of first bit
        int width = 0;
        for (int i = 0; i < len; i++) {
            boolean black = (pattern & map) != 0;
            if (last == black) {
                width++;
            } else {
                logic.addBar(last, width);
                last = black;
                width = 1;
            }
            map >>= 1;
        }
        logic.addBar(last, width);
    }
    
    private void encodeLowLevel(String fullCodewords, int c, int r, int errorCorrectionLevel, 
            TwoDimBarcodeLogicHandler logic) {
        int idx = 0;
        for (int y = 0; y < r; y++) {
            int cluster = (y % 3);
            logic.startRow();
            logic.startBarGroup(BarGroup.START_CHARACTER, null);
            encodeChar(PDF417Constants.START_PATTERN, 17, logic);
            logic.endBarGroup();
            
            int left, right;
            if (cluster == 0) {
                left = (30 * (y / 3)) + ((r - 1) / 3);
                right = (30 * (y / 3)) + (c - 1);
            } else if (cluster == 1) {
                left = (30 * (y / 3)) + (errorCorrectionLevel * 3) + ((r - 1) % 3);
                right = (30 * (y / 3)) + ((r - 1) / 3);
            } else {
                left = (30 * (y / 3)) + (c - 1);
                right = (30 * (y / 3)) + (errorCorrectionLevel * 3) + ((r - 1) % 3);
            }
            int pattern;
            
            logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
            pattern = PDF417Constants.CODEWORD_TABLE[cluster][left];
            encodeChar(pattern, 17, logic);
            logic.endBarGroup();
            
            for (int x = 0; x < c; x++) {
                logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
                pattern = PDF417Constants.CODEWORD_TABLE[cluster][fullCodewords.charAt(idx)];
                encodeChar(pattern, 17, logic);
                logic.endBarGroup();
                idx++;
            }
            
            logic.startBarGroup(BarGroup.MSG_CHARACTER, null);
            pattern = PDF417Constants.CODEWORD_TABLE[cluster][right];
            encodeChar(pattern, 17, logic);
            logic.endBarGroup();
            
            logic.startBarGroup(BarGroup.STOP_CHARACTER, null);
            encodeChar(PDF417Constants.STOP_PATTERN, 18, logic);
            logic.endBarGroup();
            logic.endRow();
        }
    }

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     * @param cols the number of columns
     * @param errorCorrectionLevel the error correction level (value between 0 and 8) 
     */
    public void generateBarcodeLogic(TwoDimBarcodeLogicHandler logic, String msg, 
            int cols, int errorCorrectionLevel) {
        if (cols < 1 || cols > 30) {
            throw new IllegalArgumentException("The number of columns must be between 1 and 30");
        }
        
        //1. step: High-level encoding
        int k = PDF417ErrorCorrection.getErrorCorrectionCodewordCount(errorCorrectionLevel); 
        String highLevel = PDF417HighLevelEncoder.encodeHighLevel(msg);
        int m = highLevel.length();
        int r = getNumberOfRows(m, k, cols);
        int pad = getNumberOfPadCodewords(m, k, cols, r);
        
        //2. step: construct data codewords
        int n = getNumberOfDataCodewords(m, errorCorrectionLevel, cols);
        StringBuffer sb = new StringBuffer(n);
        sb.append((char)n);
        sb.append(highLevel);
        for (int i = 0; i < pad; i++) {
            sb.append((char)900); //PAD characters
        }
        String dataCodewords = sb.toString();

        //3. step: Error correction
        String ec = PDF417ErrorCorrection.generateErrorCorrection(
                dataCodewords, errorCorrectionLevel);
        String fullCodewords = dataCodewords + ec;
        
        //4. step: low-level encoding
        logic.startBarcode(msg, msg);
        encodeLowLevel(fullCodewords, cols, r, errorCorrectionLevel, logic);
        logic.endBarcode();
    }

}
