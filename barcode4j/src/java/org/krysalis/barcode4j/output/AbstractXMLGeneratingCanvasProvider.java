/*
 * Copyright 2002-2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.output;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Abstract base class that provides some commonly used methods for 
 * generating XML representations of barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: AbstractXMLGeneratingCanvasProvider.java,v 1.3 2006-11-07 16:43:37 jmaerki Exp $
 */
public abstract class AbstractXMLGeneratingCanvasProvider
        extends AbstractCanvasProvider {

    private DecimalFormat df;

    public AbstractXMLGeneratingCanvasProvider(int orientation) {
        super(orientation);
    }
    
    /**
     * Returns the DecimalFormat instance to use internally to format numbers.
     * @return a DecimalFormat instance
     */
    protected DecimalFormat getDecimalFormat() {
        if (this.df == null) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            this.df = new DecimalFormat("0.####", dfs);
        }
        return this.df;
    }

    /**
     * Formats a value and adds the unit specifier at the end.
     * @param value the value to format
     * @return the formatted value
     */
    protected String addUnit(double value) {
        return getDecimalFormat().format(value) + "mm"; //was mm
    }

}
