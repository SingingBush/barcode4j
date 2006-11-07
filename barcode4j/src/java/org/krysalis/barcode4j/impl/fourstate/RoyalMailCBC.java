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
package org.krysalis.barcode4j.impl.fourstate;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

/**
 * Implements the Royal Mail Customer Barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: RoyalMailCBC.java,v 1.1 2006-11-07 16:50:36 jmaerki Exp $
 */
public class RoyalMailCBC extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public RoyalMailCBC() {
        this.bean = new RoyalMailCBCBean();
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.4mm"), "mm");
        getRoyalMailCBCBean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
    
        //Checksum mode    
        getRoyalMailCBCBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue("1mw"), "mw");
        if (igw.getUnit().equalsIgnoreCase("mw")) {
            getRoyalMailCBCBean().setIntercharGapWidth(
                    igw.getValue() * getRoyalMailCBCBean().getModuleWidth());
        } else {
            getRoyalMailCBCBean().setIntercharGapWidth(igw.getValueAsMillimeter());
        }

        Length h = new Length(cfg.getChild("ascender-height").getValue("1.8mm"), "mm");
        getRoyalMailCBCBean().setAscenderHeight(h.getValueAsMillimeter());
        
        Length hbh = new Length(cfg.getChild("track-height").getValue("1.25mm"), "mm");
        getRoyalMailCBCBean().setTrackHeight(hbh.getValueAsMillimeter());

    }
   
    /**
     * @return the underlying RoyalMailCBCBean
     */
    public RoyalMailCBCBean getRoyalMailCBCBean() {
        return (RoyalMailCBCBean)getBean();
    }

    /**
     * @see org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator#getDefaultQuietZone()
     */
    protected String getDefaultQuietZone() {
        return "2mm";
    }

}