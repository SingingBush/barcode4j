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

/* $Id: DataMatrixLogicImpl.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.util.Arrays;

import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

/**
 * Top-level class for the logic part of the DataMatrix implementation.
 * 
 * @version $Id: DataMatrixLogicImpl.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $
 */
public class DataMatrixLogicImpl {


    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     * @param cols the number of columns
     */
    public void generateBarcodeLogic(TwoDimBarcodeLogicHandler logic, String msg) {

        //ECC 200
        //1. step: Data encodation
        String encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg);
        
        DataMatrixSymbolInfo symbolInfo = DataMatrixSymbolInfo.lookup(encoded.length());
        StringBuffer codewords = new StringBuffer(symbolInfo.getCodewordCount());
        codewords.append(encoded);
        while (codewords.length() < symbolInfo.dataCapacity) {
            codewords.append("\171");
        }
        //TODO PADDING! Padding correct?
        //2. step: ECC generation
        String ecc = DataMatrixErrorCorrection.encodeECC200(encoded, symbolInfo.errorCodewords);
        codewords.append(ecc);

        //3. step: Module placement in Matrix
        DefaultDataMatrixPlacement placement = new DefaultDataMatrixPlacement(
                    codewords.toString(), symbolInfo.matrixWidth, symbolInfo.matrixHeight);
        placement.place();
        
        //4. step: low-level encoding
        logic.startBarcode(msg, msg);
        encodeLowLevel(logic, placement, symbolInfo);
        logic.endBarcode();
    }

    private void encodeLowLevel(TwoDimBarcodeLogicHandler logic, 
            DataMatrixPlacement placement, DataMatrixSymbolInfo symbolInfo) {
        if (symbolInfo.dataRegions > 1) {
            throw new UnsupportedOperationException("Handling of multiple data regions NYI");
        }
        logic.startRow();
        for (int x = 0; x < symbolInfo.matrixWidth + 2; x++) {
            logic.addBar((x % 2) == 0, 1);
        }
        logic.endRow();
        for (int y = 0; y < symbolInfo.matrixHeight; y++) {
            logic.startRow();
            logic.addBar(true, 1); //left finder edge
            for (int x = 0; x < symbolInfo.matrixWidth; x++) {
                logic.addBar(placement.getBit(x, y), 1);
            }
            logic.addBar((y % 2) == 0, 1); //right finder edge
            logic.endRow();
        }
        logic.startRow();
        for (int x = 0; x < symbolInfo.matrixWidth + 2; x++) {
            logic.addBar(true, 1);
        }
        logic.endRow();
    }

    private static class DefaultDataMatrixPlacement extends DataMatrixPlacement {
        
        private byte[] bits;
        
        public DefaultDataMatrixPlacement(String codewords, int numcols, int numrows) {
            super(codewords, numcols, numrows);
            this.bits = new byte[numcols * numrows];
            Arrays.fill(this.bits, (byte)-1);
        }
        
        protected boolean getBit(int col, int row) {
            return bits[row * numcols + col] == 1;
        }

        protected void setBit(int col, int row, boolean bit) {
            bits[row * numcols + col] = (bit ? (byte)1 : (byte)0);
        }

        protected boolean hasBit(int col, int row) {
            return bits[row * numcols + col] >= 0;
        }
        
    }
    
}
