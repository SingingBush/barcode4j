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

/* $Id: DataMatrixSymbolInfo.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Symbol info table for DataMatrix. 
 *
 * @version $Id: DataMatrixSymbolInfo.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $
 */
public class DataMatrixSymbolInfo {

    private static final DataMatrixSymbolInfo[] SYMBOLS = new DataMatrixSymbolInfo[] {
        new DataMatrixSymbolInfo(3, 5, 8, 8, 1),
        new DataMatrixSymbolInfo(5, 7, 10, 10, 1),
        new DataMatrixSymbolInfo(8, 10, 12, 12, 1),
        new DataMatrixSymbolInfo(12, 12, 14, 14, 1),
        new DataMatrixSymbolInfo(18, 14, 16, 16, 1),
        new DataMatrixSymbolInfo(22, 18, 18, 18, 1),
        new DataMatrixSymbolInfo(30, 20, 20, 20, 1),
        new DataMatrixSymbolInfo(36, 24, 22, 22, 1),
        new DataMatrixSymbolInfo(44, 28, 24, 24, 1)
    };
    
    public int dataCapacity;
    public int errorCodewords;
    public int matrixWidth;
    public int matrixHeight;
    public int dataRegions;
    
    public DataMatrixSymbolInfo(int dataCapacity, int errorCodewords, 
            int matrixWidth, int matrixHeight, int dataRegions) {
        this.dataCapacity = dataCapacity;
        this.errorCodewords = errorCodewords;
        this.matrixWidth = matrixWidth;
        this.matrixHeight = matrixHeight;
        this.dataRegions = dataRegions;
    }
    
    public static DataMatrixSymbolInfo lookup(int dataCodewords) {
        return lookup(dataCodewords, true);
    }
    
    public static DataMatrixSymbolInfo lookup(int dataCodewords, boolean fail) {
        for (int i = 0, c = SYMBOLS.length; i < c; i++) {
            if (dataCodewords <= SYMBOLS[i].dataCapacity) {
                return SYMBOLS[i];
            }
        }
        if (fail) {
            throw new IllegalArgumentException(
            "Can't find a symbol arrangement that matches the message");
        }
        return null;
    }

    public int getCodewordCount() {
        return dataCapacity + errorCodewords;
    }
    
}