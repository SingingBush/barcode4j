/*
 * Copyright 2006-2009 Antun Oreskovic.
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
package org.krysalis.barcode4j.impl.fourstate;

/**
 * 
 * This converts the customer Sort code (DPID) into the bar representation of
 * the Australia Post bar code.
 * 
 * @author Antun Oreskovic
 * @version $Id: AbstractFourStateAustPostLogicImpl.java 2008/12/30 $
 * 
 */

import java.util.Arrays;

import org.krysalis.barcode4j.ChecksumMode;

public class AbstractAustPostLogicImpl extends
        AbstractFourStateLogicImpl {

    public AbstractAustPostLogicImpl(ChecksumMode mode) {
        super(mode);
    }

    // Encoding table for G3 Decimal conversion of barcode symbols. This is used
    // for Reed-Solomon error correction calculations.
    private static final String[] BarValuesTable = { "000", "001", "002",
            "003", "010", "011", "012", "013", "020", "021", "022", "023",
            "030", "031", "032", "033", "100", "101", "102", "103", "110",
            "111", "112", "113", "120", "121", "122", "123", "130", "131",
            "132", "133", "200", "201", "202", "203", "210", "211", "212",
            "213", "220", "221", "222", "223", "230", "231", "232", "233",
            "300", "301", "302", "303", "310", "311", "312", "313", "320",
            "321", "322", "323", "330", "331", "332", "333" };

    // C Encoding Table. This is used for the customer information.
    private static final String BarCinputChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz #0123456789";
    private static final String[] BarCinputTable = { "000", "001", "002",
            "010", "011", "012", "020", "021", "022", "100", "101", "102",
            "110", "111", "112", "120", "121", "122", "200", "201", "202",
            "210", "211", "212", "220", "221", "023", "030", "031", "032",
            "033", "103", "113", "123", "130", "131", "132", "133", "203",
            "213", "223", "230", "231", "232", "233", "303", "313", "323",
            "330", "331", "332", "333", "003", "013", "222", "300", "301",
            "302", "310", "311", "312", "320", "321", "322" };

    // The generator polynomial, g(x) for Galois field arithmetic
    private static final int[] galiosGen = { 48, 17, 29, 30, 1 };

    // barcode symbol tables for N encoding of digits
    private static final String BarNTable[] = { "00", "01", "02", "10", "11",
            "12", "20", "21", "22", "30" };

    // Barcode stop and start symbols - always constant.
    private static final String BC_START_STOP_SYMBOL = "13";

    // Barcode filler values, always constant
    private static final String BC_FILLER_SINGLE_VALUE = "3";
    private static final String BC_FILLER_TRIPLE_VALUE = "333";

    // FCC_nn_type: Decimal representation of the format control field for
    // barcode types, as defined in Australia Post Specification 203
    private static final String FCC_37_CUST = "11";
    private static final String FCC_37_ROUT = "87";
    private static final String FCC_37_REPL = "45";
    private static final String FCC_37_REDI = "92";
    private static final String FCC_52_FF_MET = "59";
    private static final String FCC_67_FF_MET = "62";
    private static final String FCC_67_FF_MAN = "44";

    // The multiplication table for Galois field
    private int[][] galiosMultTable = new int[64][64];

    private boolean galiosInitialised = false;
    private String workBarCode;

    
    /**
     * Call this to get the Australia Post Barcode value for your sorting code,
     * and any customer information that you want added to the barcode The FCC
     * needs to be correct, as this determines what type of barcode will be
     * returned. Please check Australia Post for specifications. There are 3
     * different sized barcodes. Any error, Barcode will return Blanks
     * 
     * @return Australian Post Barcode value
     */
    
    @Override
    protected String normalizeMessage(String msg) {
        workBarCode = "";
        String FCC = "";
        String sortCode = "";
        String custInfo = "";
        if (msg.length() >= 10) {
            FCC = msg.substring(0, 2);
            sortCode = msg.substring (2, 10);
            custInfo = msg.substring (10);
        }
        
        // This will build the 37 bar barcode the FCC and a sorting code
        if (FCC.equalsIgnoreCase(FCC_37_CUST)
                || FCC.equalsIgnoreCase(FCC_37_ROUT)
                || FCC.equalsIgnoreCase(FCC_37_REPL)
                || FCC.equalsIgnoreCase(FCC_37_REDI)) {

            buildFullBarcode(FCC, sortCode, 37, null, 0);

            // This will build the 52 bar barcode from the FCC, sort code and
            // customer info
        } else if (FCC.equalsIgnoreCase(FCC_52_FF_MET)) {
            buildFullBarcode(FCC, sortCode, 52, custInfo, 5);

            // This will build the 67 bar barcode from the FCC, sort code and
            // customer info
        } else if (FCC.equalsIgnoreCase(FCC_67_FF_MET)
                || FCC.equalsIgnoreCase(FCC_67_FF_MAN)) {
            buildFullBarcode(FCC, sortCode, 67, custInfo, 10);

        } else {
            workBarCode = "";
        }

        return workBarCode;
    }

    /*
     * Build a barcode to the specified length from an FCC, a sorting code and
     * customer info. The C encoding table is used for the customer info.
     */
    private void buildFullBarcode(String fcc, String sortCode, int barLength,
            String custInfo, int custInfoLength) {

        boolean success;

        // Convert the Sorting code (DPID) to its bar values
        success = convertSortCode(fcc, sortCode);

        if (success) {

            // Need to convert the Customer Information to bar values
            // This can only be 15 bars long (or 5 groups of 3 * bars)
            // or This can only be 30 bars long (or 10 groups of 3 * bars)
            if (custInfoLength > 0)
                convertCustomerInformation(custInfo, custInfoLength);

            // REQUIRED! Need to add a filler bar to the barcode
            setSingleFillerBar();

            // This will append the Reed Solomon Parity check
            convertReedSolomonParity(barLength);

            // REQUIRED! Append start and stop (13) bar values to the barcode.
            setStartStopBars();
        }

        // Any errors, set to blank
        if (!success)
            workBarCode = "";
    }

    /*
     * Convert the fcc and sort code characters to bar values using the N-Table
     */
    private boolean convertSortCode(String fcc, String sortcode) {

        boolean success = true;
        String inString = fcc.concat(sortcode);
        int i;

        // Loop through each character of the fcc and sortcode,
        // and convert to it bar value, using the N encoding table.
        for (i = 0; i < inString.length(); i++)
            workBarCode = workBarCode.concat(BarNTable[Integer
                    .parseInt(inString.substring(i, i + 1))]);

        if (i != inString.length()) {
            success = false;
        }

        return success;
    }

    /*
     * Convert characters to bar values using the C-Table This needs to fill the
     * entire bar size, either with customer information, or, it will be
     * required to add filler bars to the size of the barcode
     */
    private void convertCustomerInformation(String custInfo, int barSize) {

        int i;
        // Loop through each character of the Customer Information,
        // and convert to it bar value, using the C encoding table.
        for (i = 0; i < custInfo.length(); i++) {
            if (i + 1 > barSize)
                break;
            workBarCode = workBarCode.concat(BarCinputTable[BarCinputChar
                    .indexOf(custInfo.substring(i, i + 1))]);
        }

        // If custInfo does not match the size of barcode,
        // add the filler bars that are required.
        for (int j = i; j < barSize; j++)
            workBarCode = workBarCode.concat(BC_FILLER_TRIPLE_VALUE);
    }

    /*
     * This is the Reed Solomon parity checks, and appends the bar values of the
     * returned calculation.
     */
    private void convertReedSolomonParity(int barlength) {

        int sym;
        int infosym;
        int j;
        int[] temp = new int[31];

        // If not initialised, load values
        if (!galiosInitialised) {
            setGaliosValues();
        }

        /* Calc the number of symbols & info symbols for the barcode */
        sym = (barlength - 4) / 3;
        infosym = sym - 4;

        /*
         * For the purposes of RS Parity generation the barcode string consists
         * of a number of 3 bar information symbols. Each group of 3 bars must
         * be converted to it's decimal equivalent.
         */
        j = 0;
        for (int i = 0; i < infosym; i++)
            temp[sym - i - 1] = Arrays.binarySearch(BarValuesTable, workBarCode
                    .substring(j, j += 3));

        /*
         * Perform the division by the generator polynomial g(x). This is
         * accomplished using k iterations of long division, where g(x) times
         * the most significant symbol in the dividend are subtracted.
         */
        for (int i = infosym - 1; i >= 0; i--) {
            for (j = 0; j <= 4; j++) {
                temp[i + j] = temp[i + j]
                        ^ (galiosMultTable[galiosGen[j]][temp[4 + i]]);
            }
        }

        // Place the parity symbols in the array
        for (int i = 3; i >= 0; i--)
            workBarCode = workBarCode.concat(BarValuesTable[temp[i]]);
    }

    /*
     * Initialise the Galios calculation values, used in the reed parity checks
     * Only needs to be done once per object
     */
    private void setGaliosValues() {

        int primpoly, test, prev, next;
        int i, j;

        primpoly = 67;
        test = 64;
        for (i = 0; i < 64; i++) {
            galiosMultTable[0][i] = 0;
            galiosMultTable[1][i] = i;
        }

        prev = 1;
        for (i = 1; i < 64; i++) {
            next = prev << 1;
            if ((next & test) > 0)
                next ^= primpoly;
            for (j = 0; j < 64; j++) {
                galiosMultTable[next][j] = galiosMultTable[prev][j] << 1;
                if ((galiosMultTable[next][j] & test) > 0)
                    galiosMultTable[next][j] ^= primpoly;
            }
            prev = next;
        }
    }

    /*
     * Append add a filler bar (3) to the barcode
     */
    private void setSingleFillerBar() {
        workBarCode = workBarCode.concat(BC_FILLER_SINGLE_VALUE);
    }

    /*
     * Append start and stop (13) bar values to the barcode.
     */
    private void setStartStopBars() {
        workBarCode = BC_START_STOP_SYMBOL.concat(workBarCode
                .concat(BC_START_STOP_SYMBOL));
    }

    @Override
    public char calcChecksum(String msg) {
        return 0;
    }

    @Override
    protected String[] encodeHighLevel(String msg) {
        return null;
    }
}
