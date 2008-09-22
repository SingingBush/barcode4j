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

/* $Id: DataMatrixLogicImpl.java,v 1.10 2008-09-22 08:59:07 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;
import java.io.IOException;

import org.krysalis.barcode4j.TwoDimBarcodeLogicHandler;

/**
 * Top-level class for the logic part of the DataMatrix implementation.
 *
 * @version $Id: DataMatrixLogicImpl.java,v 1.10 2008-09-22 08:59:07 jmaerki Exp $
 */
public class DataMatrixLogicImpl {

    private static final boolean DEBUG = false;

    /**
     * Generates the barcode logic.
     * @param logic the logic handler to receive generated events
     * @param msg the message to encode
     * @param shape the symbol shape constraint
     * @param minSize the minimum symbol size constraint or null for no constraint
     * @param maxSize the maximum symbol size constraint or null for no constraint
     */
    public void generateBarcodeLogic(TwoDimBarcodeLogicHandler logic, String msg,
            SymbolShapeHint shape, Dimension minSize, Dimension maxSize) {

        //ECC 200
        //1. step: Data encodation
        String encoded;
        try {
            encoded = DataMatrixHighLevelEncoder.encodeHighLevel(msg, shape, minSize, maxSize);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot fetch data: " + e.getLocalizedMessage());
        }

        DataMatrixSymbolInfo symbolInfo = DataMatrixSymbolInfo.lookup(encoded.length(),
                shape, minSize, maxSize, true);
        if (DEBUG) {
            System.out.println(symbolInfo);
        }

        //2. step: ECC generation
        String codewords = DataMatrixErrorCorrection.encodeECC200(
                encoded, symbolInfo);

        //3. step: Module placement in Matrix
        DefaultDataMatrixPlacement placement = new DefaultDataMatrixPlacement(
                    codewords,
                    symbolInfo.getSymbolDataWidth(), symbolInfo.getSymbolDataHeight());
        placement.place();

        //4. step: low-level encoding
        logic.startBarcode(msg, msg);
        encodeLowLevel(logic, placement, symbolInfo);
        logic.endBarcode();
    }

    private void encodeLowLevel(TwoDimBarcodeLogicHandler logic,
            DataMatrixPlacement placement, DataMatrixSymbolInfo symbolInfo) {
        int symbolWidth = symbolInfo.getSymbolDataWidth();
        int symbolHeight = symbolInfo.getSymbolDataHeight();
        for (int y = 0; y < symbolHeight; y++) {
            if ((y % symbolInfo.matrixHeight) == 0) {
                logic.startRow();
                for (int x = 0; x < symbolInfo.getSymbolWidth(); x++) {
                    logic.addBar((x % 2) == 0, 1);
                }
                logic.endRow();
            }
            logic.startRow();
            for (int x = 0; x < symbolWidth; x++) {
                if ((x % symbolInfo.matrixWidth) == 0) {
                    logic.addBar(true, 1); //left finder edge
                }
                logic.addBar(placement.getBit(x, y), 1);
                if ((x % symbolInfo.matrixWidth) == symbolInfo.matrixWidth - 1) {
                    logic.addBar((y % 2) == 0, 1); //right finder edge
                }
            }
            logic.endRow();
            if ((y % symbolInfo.matrixHeight) == symbolInfo.matrixHeight - 1) {
                logic.startRow();
                for (int x = 0; x < symbolInfo.getSymbolWidth(); x++) {
                    logic.addBar(true, 1);
                }
                logic.endRow();
            }
        }
    }

}
