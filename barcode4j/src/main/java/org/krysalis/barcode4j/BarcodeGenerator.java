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
package org.krysalis.barcode4j;

import org.jetbrains.annotations.NotNull;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * This interface is used to generate whole barcodes.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeGenerator.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public interface BarcodeGenerator {

    /**
     * Generates a barcode using the given Canvas to render the barcode to its
     * output format.
     * @param canvas CanvasProvider that the barcode is to be rendered on.
     * @param msg message to encode
     */
    void generateBarcode(@NotNull CanvasProvider canvas, String msg);

    /**
     * Calculates the dimension of a barcode with the given message. The
     * dimensions are dependent on the configuration of the barcode generator.
     * When rendering with XSL-FOP, calcDimensions will be called prior to generateBarcode.
     * @param msg message to use for calculation.
     * @return BarcodeDimension the barcodes dimensions
     */
    BarcodeDimension calcDimensions(@NotNull final String msg);
}
