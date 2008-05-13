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

import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This class is an implementation of the DataMatrix barcode.
 * 
 * @version $Id: DataMatrix.java,v 1.3 2008-05-13 13:00:43 jmaerki Exp $
 */
public class DataMatrix extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public DataMatrix() {
        this.bean = new DataMatrixBean();
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        String mws = cfg.getChild("module-width").getValue(null);
        if (mws != null) {
            Length mw = new Length(mws, "mm");
            getDataMatrixBean().setModuleWidth(mw.getValueAsMillimeter());
        }

        super.configure(cfg);
        
        String shape = cfg.getChild("shape").getValue(null);
        if (shape != null) {
            getDataMatrixBean().setShape(SymbolShapeHint.byName(shape));
        }
    }
   
    /**
     * @return the underlying DataMatrix bean
     */
    public DataMatrixBean getDataMatrixBean() {
        return (DataMatrixBean)getBean();
    }

}