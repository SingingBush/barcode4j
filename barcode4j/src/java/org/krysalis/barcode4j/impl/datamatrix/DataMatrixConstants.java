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

/* $Id: DataMatrixConstants.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Constants for DataMatrix.
 * 
 * @version $Id: DataMatrixConstants.java,v 1.1 2006-11-27 08:10:58 jmaerki Exp $
 */
public interface DataMatrixConstants {

    /** Padding character */
    char PAD = 129;
    /** mode latch to C40 encodation mode */
    char LATCH_TO_C40 = 230;
    /** mode latch to Base 256 encodation mode */
    char LATCH_TO_BASE256 = 231;
    /** FNC1 Codeword */
    char FNC1 = 232;
    /** Structured Append Codeword */
    char STRUCTURED_APPEND = 233;
    /** Reader Programming */
    char READER_PROGRAMMING = 234;
    /** Upper Shift */
    char UPPER_SHIFT = 235;
    /** 05 Macro */
    char MACRO_05 = 236;
    /** 06 Macro */
    char MACRO_06 = 237;
    /** mode latch to ANSI X.12 encodation mode */
    char LATCH_TO_ANSIX12 = 238;
    /** mode latch to Text encodation mode */
    char LATCH_TO_TEXT = 239;
    /** mode latch to EDIFACT encodation mode */
    char LATCH_TO_EDIFACT = 240;
    /** ECI character (Extended Channel Interpretation) */
    char ECI = 241;
    

}
