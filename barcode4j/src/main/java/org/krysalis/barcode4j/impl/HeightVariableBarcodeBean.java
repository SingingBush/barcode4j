/*
 * Copyright 2003,2004,2006 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl;

/**
 * Base class for barcodes that encode information by varying the height
 * of the bars.
 * 
 * @author Chris Dolphy
 * @version $Id: HeightVariableBarcodeBean.java,v 1.2 2006-11-07 16:43:37 jmaerki Exp $
 */
public abstract class HeightVariableBarcodeBean extends AbstractBarcodeBean {

    /**
     * Returns the effective height of a bar with a given logical height.
     * @param height the logical height (1=short, 2=tall)
     * @return double
     */
    public abstract double getBarHeight(int height);

}
