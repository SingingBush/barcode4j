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
package org.krysalis.barcode4j.impl.codabar;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

/**
 * This class is an implementation of the Codabar barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: Codabar.java,v 1.1 2004-09-12 17:57:53 jmaerki Exp $
 */
public class Codabar extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public Codabar() {
        this.bean = new CodabarBean();
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.21mm"), "mm");
        getBean().setModuleWidth(mw.getValueAsMillimeter());
        
        super.configure(cfg);

        //Checksum mode        
        getCodabarBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));

        //Wide factor
        getCodabarBean().setWideFactor(
            cfg.getChild("wide-factor").getValueAsFloat((float)CodabarBean.DEFAULT_WIDE_FACTOR));
    }

    /**
     * @return the underlying CodabarBean
     */
    public CodabarBean getCodabarBean() {
        return (CodabarBean)getBean();
    }

}