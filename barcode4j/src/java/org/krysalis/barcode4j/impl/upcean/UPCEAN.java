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
package org.krysalis.barcode4j.impl.upcean;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

/**
 * This is an abstract base class for UPC and EAN barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCEAN.java,v 1.1 2004-09-12 17:57:52 jmaerki Exp $
 */
public abstract class UPCEAN extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.33mm"), "mm");
        getUPCEANBean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
        
        //Checksum mode        
        getUPCEANBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
    }

    /**
     * @return the underlying UPCEANBean
     */
    public UPCEANBean getUPCEANBean() {
        return (UPCEANBean)getBean();
    }
    
}