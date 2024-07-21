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
package org.krysalis.barcode4j.impl.datamatrix;

import java.awt.Dimension;

import org.jetbrains.annotations.NotNull;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.krysalis.barcode4j.configuration.Configurable;
import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;

/**
 * This class is an implementation of the DataMatrix barcode.
 * <p>
 * A DataMatrix 2D Barcode can handle various message sizes depending on the size of the grid. They can
 * generally store up to 3,116 numeric or 2,335 alphanumeric characters or up to 1,555 bytes
 * of binary information and can also include a checksum. Symbol sizes vary from 8×8 to 144×144.
 * Six encoding modes coexist: ASCII, Text, C40, X12, EDIFACT and Base 256.
 * The ASCII encoding is the default encoding and the most used.
 * </p>
 *
 * @version $Id: DataMatrix.java,v 1.4 2008-09-22 08:59:08 jmaerki Exp $
 */
public class DataMatrix extends ConfigurableBarcodeGenerator implements Configurable {

    /** Create a new instance. */
    public DataMatrix() {
        this.bean = new DataMatrixBean();
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configurable#configure(Configuration)
     */
    @Override
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        final String mws = cfg.getChild("module-width").getValue(null);
        if (mws != null) {
            Length mw = new Length(mws, "mm");
            getDataMatrixBean().setModuleWidth(mw.getValueAsMillimeter());
        }

        super.configure(cfg);

        final String shape = cfg.getChild("shape").getValue(null);
        if (shape != null) {
            getDataMatrixBean().setShape(SymbolShapeHint.byName(shape));
        }

        String size;
        size = cfg.getChild("min-symbol-size").getValue(null);
        if (size != null) {
            getDataMatrixBean().setMinSize(parseSymbolSize(size));
        }
        size = cfg.getChild("max-symbol-size").getValue(null);
        if (size != null) {
            getDataMatrixBean().setMaxSize(parseSymbolSize(size));
        }
    }

    private Dimension parseSymbolSize(@NotNull final String size) {
        int idx = size.indexOf('x');
        Dimension dim;
        if (idx > 0) {
            dim = new Dimension(Integer.parseInt(size.substring(0, idx)),
                    Integer.parseInt(size.substring(idx + 1)));
        } else {
            int extent = Integer.parseInt(size);
            dim = new Dimension(extent, extent);
        }
        return dim;
    }

    /**
     * @return the underlying DataMatrix bean
     */
    public DataMatrixBean getDataMatrixBean() {
        return (DataMatrixBean)getBean();
    }

}
