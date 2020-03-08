/*
 * Copyright 2006 Jeremias Maerki
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

/* $Id: DataMatrixSymbolInfo.java,v 1.5 2008-09-22 08:59:08 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;

/**
 * Symbol info table for DataMatrix.
 *
 * @version $Id: DataMatrixSymbolInfo.java,v 1.5 2008-09-22 08:59:08 jmaerki Exp $
 */
public class DataMatrixSymbolInfo {

    public static final DataMatrixSymbolInfo[] PROD_SYMBOLS = new DataMatrixSymbolInfo[] {
        new DataMatrixSymbolInfo(false, 3, 5, 8, 8, 1),
        new DataMatrixSymbolInfo(false, 5, 7, 10, 10, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 5, 7, 16, 6, 1),
        new DataMatrixSymbolInfo(false, 8, 10, 12, 12, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 10, 11, 14, 6, 2),
        new DataMatrixSymbolInfo(false, 12, 12, 14, 14, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 16, 14, 24, 10, 1),

        new DataMatrixSymbolInfo(false, 18, 14, 16, 16, 1),
        new DataMatrixSymbolInfo(false, 22, 18, 18, 18, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 22, 18, 16, 10, 2),
        new DataMatrixSymbolInfo(false, 30, 20, 20, 20, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 32, 24, 16, 14, 2),
        new DataMatrixSymbolInfo(false, 36, 24, 22, 22, 1),
        new DataMatrixSymbolInfo(false, 44, 28, 24, 24, 1),
        /*rect*/new DataMatrixSymbolInfo(true, 49, 28, 22, 14, 2),

        new DataMatrixSymbolInfo(false, 62, 36, 14, 14, 4),
        new DataMatrixSymbolInfo(false, 86, 42, 16, 16, 4),
        new DataMatrixSymbolInfo(false, 114, 48, 18, 18, 4),
        new DataMatrixSymbolInfo(false, 144, 56, 20, 20, 4),
        new DataMatrixSymbolInfo(false, 174, 68, 22, 22, 4),

        new DataMatrixSymbolInfo(false, 204, 84, 24, 24, 4, 102, 42),
        new DataMatrixSymbolInfo(false, 280, 112, 14, 14, 16, 140, 56),
        new DataMatrixSymbolInfo(false, 368, 144, 16, 16, 16, 92, 36),
        new DataMatrixSymbolInfo(false, 456, 192, 18, 18, 16, 114, 48),
        new DataMatrixSymbolInfo(false, 576, 224, 20, 20, 16, 144, 56),
        new DataMatrixSymbolInfo(false, 696, 272, 22, 22, 16, 174, 68),
        new DataMatrixSymbolInfo(false, 816, 336, 24, 24, 16, 136, 56),
        new DataMatrixSymbolInfo(false, 1050, 408, 18, 18, 36, 175, 68),
        new DataMatrixSymbolInfo(false, 1304, 496, 20, 20, 36, 163, 62),
        new DataMatrixSymbolInfo144(),
    };

    private static DataMatrixSymbolInfo[] symbols = PROD_SYMBOLS;

    /**
     * Overrides the symbol info set used by this class. Used for testing purposes.
     * @param override the symbol info set to use
     */
    public static void overrideSymbolSet(DataMatrixSymbolInfo[] override) {
        symbols = override;
    }

    public boolean rectangular;
    public int dataCapacity;
    public int errorCodewords;
    public int matrixWidth;
    public int matrixHeight;
    public int dataRegions;
    public int rsBlockData;
    public int rsBlockError;

    public DataMatrixSymbolInfo(boolean rectangular, int dataCapacity, int errorCodewords,
            int matrixWidth, int matrixHeight, int dataRegions) {
        this(rectangular, dataCapacity, errorCodewords, matrixWidth, matrixHeight, dataRegions,
                dataCapacity, errorCodewords);
    }

    public DataMatrixSymbolInfo(boolean rectangular, int dataCapacity, int errorCodewords,
            int matrixWidth, int matrixHeight, int dataRegions,
            int rsBlockData, int rsBlockError) {
        this.rectangular = rectangular;
        this.dataCapacity = dataCapacity;
        this.errorCodewords = errorCodewords;
        this.matrixWidth = matrixWidth;
        this.matrixHeight = matrixHeight;
        this.dataRegions = dataRegions;
        this.rsBlockData = rsBlockData;
        this.rsBlockError = rsBlockError;
    }

    public static DataMatrixSymbolInfo lookup(int dataCodewords) {
        return lookup(dataCodewords, SymbolShapeHint.FORCE_NONE, true);
    }

    public static DataMatrixSymbolInfo lookup(int dataCodewords, SymbolShapeHint shape) {
        return lookup(dataCodewords, shape, true);
    }

    public static DataMatrixSymbolInfo lookup(int dataCodewords,
                boolean allowRectangular, boolean fail) {
        SymbolShapeHint shape = allowRectangular
                ? SymbolShapeHint.FORCE_NONE : SymbolShapeHint.FORCE_SQUARE;
        return lookup(dataCodewords, shape, fail);
    }

    public static DataMatrixSymbolInfo lookup(int dataCodewords,
            SymbolShapeHint shape, boolean fail) {
        return lookup(dataCodewords, shape, null, null, fail);
    }

    public static DataMatrixSymbolInfo lookup(int dataCodewords,
            SymbolShapeHint shape, Dimension minSize, Dimension maxSize, boolean fail) {
        for (int i = 0, c = symbols.length; i < c; i++) {
            DataMatrixSymbolInfo symbol = symbols[i];
            if (shape == SymbolShapeHint.FORCE_SQUARE && symbol.rectangular) {
                continue;
            }
            if (shape == SymbolShapeHint.FORCE_RECTANGLE && !symbol.rectangular) {
                continue;
            }
            if (minSize != null
                    && (symbol.getSymbolWidth() < minSize.width
                            || symbol.getSymbolHeight() < minSize.height)) {
                continue;
            }
            if (maxSize != null
                    && (symbol.getSymbolWidth() > maxSize.width
                            || symbol.getSymbolHeight() > maxSize.height)) {
                continue;
            }
            if (dataCodewords <= symbol.dataCapacity) {
                return symbol;
            }
        }
        if (fail) {
            throw new IllegalArgumentException(
                "Can't find a symbol arrangement that matches the message. Data codewords: "
                    + dataCodewords);
        }
        return null;
    }

    public int getHorzDataRegions() {
        switch (dataRegions) {
        case 1: return 1;
        case 2: return 2;
        case 4: return 2;
        case 16: return 4;
        case 36: return 6;
        default:
            throw new IllegalStateException("Cannot handle this number of data regions");
        }
    }

    public int getVertDataRegions() {
        switch (dataRegions) {
        case 1: return 1;
        case 2: return 1;
        case 4: return 2;
        case 16: return 4;
        case 36: return 6;
        default:
            throw new IllegalStateException("Cannot handle this number of data regions");
        }
    }

    public int getSymbolDataWidth() {
        return getHorzDataRegions() * matrixWidth;
    }

    public int getSymbolDataHeight() {
        return getVertDataRegions() * matrixHeight;
    }

    public int getSymbolWidth() {
        return getSymbolDataWidth() + (getHorzDataRegions() * 2);
    }

    public int getSymbolHeight() {
        return getSymbolDataHeight() + (getVertDataRegions() * 2);
    }

    public int getCodewordCount() {
        return dataCapacity + errorCodewords;
    }

    public int getInterleavedBlockCount() {
        return dataCapacity / rsBlockData;
    }

    public int getDataLengthForInterleavedBlock(int index) {
        return rsBlockData;
    }

    public int getErrorLengthForInterleavedBlock(int index) {
        return rsBlockError;
    }

    /** @see java.lang.Object#toString() */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(rectangular ? "Rectangular Symbol:" : "Square Symbol:");
        sb.append(" data region " + matrixWidth + "x" + matrixHeight);
        sb.append(", symbol size " + getSymbolWidth() + "x" + getSymbolHeight());
        sb.append(", symbol data size " + getSymbolDataWidth() + "x" + getSymbolDataHeight());
        sb.append(", codewords " + dataCapacity + "+" + errorCodewords);
        return sb.toString();
    }

    private static class DataMatrixSymbolInfo144 extends DataMatrixSymbolInfo {

        public DataMatrixSymbolInfo144() {
            super(false, 1558, 620, 22, 22, 36);
            this.rsBlockData = -1; //special! see below
            this.rsBlockError = 62;
        }

        public int getInterleavedBlockCount() {
            return 10;
        }

        public int getDataLengthForInterleavedBlock(int index) {
            return (index <= 8) ? 156 : 155;
        }

    }

}