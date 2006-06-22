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
package org.krysalis.barcode4j.impl.pdf417;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

/**
 * This class is an implementation of the PDF417 barcode.
 * 
 * @version $Id: PDF417.java,v 1.1 2006-06-22 09:01:16 jmaerki Exp $
 */
public class PDF417 extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public PDF417() {
        this.bean = new PDF417Bean();
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        String mws = cfg.getChild("module-width").getValue(null);
        if (mws != null) {
            Length mw = new Length(mws, "mm");
            getPDF417Bean().setModuleWidth(mw.getValueAsMillimeter());
        }

        super.configure(cfg);

        getPDF417Bean().setColumns(cfg.getChild("columns").getValueAsInteger(
                PDF417Bean.DEFAULT_COLUMN_COUNT));
        
        getPDF417Bean().setErrorCorrectionLevel(cfg.getChild("columns").getValueAsInteger(
                PDF417Bean.DEFAULT_ERROR_CORRECTION_LEVEL));
        
        //Vertical quiet zone
        String qzvs = cfg.getChild("vertical-quiet-zone").getValue(null);
        if (qzvs != null) {
            Length qz = new Length(qzvs, "mw");
            if (qz.getUnit().equalsIgnoreCase("mw")) {
                getPDF417Bean().setVerticalQuietZone(qz.getValue() * getBean().getModuleWidth());
            } else {
                getPDF417Bean().setVerticalQuietZone(qz.getValueAsMillimeter());
            }
        }
        
        String rhs = cfg.getChild("row-height").getValue(null);
        if (rhs != null) {
            Length rh = new Length(rhs, "mw");
            if (rh.getUnit().equalsIgnoreCase("mw")) {
                getPDF417Bean().setBarHeight(rh.getValue() * getBean().getModuleWidth());
            } else {
                getPDF417Bean().setBarHeight(rh.getValueAsMillimeter());
            }
        }
    }
   
    /**
     * @return the underlying PDF417Bean
     */
    public PDF417Bean getPDF417Bean() {
        return (PDF417Bean)getBean();
    }

}