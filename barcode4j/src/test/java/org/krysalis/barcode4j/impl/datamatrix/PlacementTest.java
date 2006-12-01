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

/* $Id: PlacementTest.java,v 1.1 2006-12-01 13:31:11 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

import org.krysalis.barcode4j.tools.TestHelper;

import junit.framework.TestCase;

/**
 * Tests the DataMatrix placement algorithm.
 */
public class PlacementTest extends TestCase {

    private static final boolean DEBUG = false;
    
    public void testPlacement() throws Exception {
        String codewords = TestHelper.unvisualize(
                "66 74 78 66 74 78 129 56 35 102 192 96 226 100 156 1 107 221"); //"AIMAIM" encoded
        DebugPlacement placement = new DebugPlacement(
                codewords.toString(), 12, 12);
        placement.place();
        String[] expected = new String[] {"011100001111",
                                          "001010101000",
                                          "010001010100",
                                          "001010100010",
                                          "000111000100",
                                          "011000010100",
                                          "000100001101",
                                          "011000010000",
                                          "001100001101",
                                          "100010010111",
                                          "011101011010",
                                          "001011001010"};
        String[] actual = placement.toBitFieldStringArray();
        for (int i = 0; i < actual.length; i++) {
            if (DEBUG) {
                System.out.println(expected[i] + "  " + actual[i]);
            }
            assertEquals("Row " + i, expected[i], actual[i]);
        }
    }
    
    private class DebugPlacement extends DefaultDataMatrixPlacement {

        public DebugPlacement(String codewords, int numcols, int numrows) {
            super(codewords, numcols, numrows);
        }

        protected void setBit(int col, int row, boolean bit) {
            if (DEBUG) {
                System.out.println("Set bit x=" + col + " y=" + row 
                        + " --> " + (bit ? "BLACK" : "white"));
            }
            super.setBit(col, row, bit);
        }

        public String toBitFieldString() {
            StringBuffer sb = new StringBuffer(bits.length);
            for (int i = 0, c = bits.length; i < c; i++) {
                sb.append(bits[i] == 1 ? '1' : '0');
            }
            return sb.toString();
        }
        
        public String[] toBitFieldStringArray() {
            String[] array = new String[this.numrows];
            int startpos = 0;
            for (int row = 0; row < numrows; row++) {
                StringBuffer sb = new StringBuffer(bits.length);
                for (int i = 0, c = numcols; i < c; i++) {
                    sb.append(bits[startpos + i] == 1 ? '1' : '0');
                }
                array[row] = sb.toString();
                startpos += numcols;
            }
            return array;
        }
        
    }
    
}
