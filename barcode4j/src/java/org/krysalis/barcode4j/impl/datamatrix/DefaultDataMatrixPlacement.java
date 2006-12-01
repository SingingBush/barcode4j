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

/* $Id: DefaultDataMatrixPlacement.java,v 1.1 2006-12-01 13:31:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import java.util.Arrays;

/**
 * Default implementation of DataMatrixPlacement which uses a byte array to store the bits
 * (one bit per byte to allow for checking whether a bit has been set or not).
 */
class DefaultDataMatrixPlacement extends DataMatrixPlacement {
    
    /** Buffer for the bits */
    protected byte[] bits;
    
    /**
     * Main constructor
     * @param codewords the codewords to place
     * @param numcols the number of columns
     * @param numrows the number of rows
     */
    public DefaultDataMatrixPlacement(String codewords, int numcols, int numrows) {
        super(codewords, numcols, numrows);
        this.bits = new byte[numcols * numrows];
        Arrays.fill(this.bits, (byte)-1); //Initialize with "not set" value
    }
    
    /** @see org.krysalis.barcode4j.impl.datamatrix.DataMatrixPlacement#getBit(int, int) */
    protected boolean getBit(int col, int row) {
        return bits[row * numcols + col] == 1;
    }

    /** @see org.krysalis.barcode4j.impl.datamatrix.DataMatrixPlacement#setBit(int, int, boolean) */
    protected void setBit(int col, int row, boolean bit) {
        bits[row * numcols + col] = (bit ? (byte)1 : (byte)0);
    }

    /** @see org.krysalis.barcode4j.impl.datamatrix.DataMatrixPlacement#hasBit(int, int) */
    protected boolean hasBit(int col, int row) {
        return bits[row * numcols + col] >= 0;
    }
    
}