/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.postnet;

import org.krysalis.barcode4j.BaselineAlignment;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Implements the United States Postal Service POSTNET barcode.
 * 
 * @author Chris Dolphy
 * @version $Id: POSTNET.java,v 1.3 2008-05-13 13:00:44 jmaerki Exp $
 */
public class POSTNET extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public POSTNET() {
        this.bean = new POSTNETBean();
    }
    
    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue(
                POSTNETBean.DEFAULT_MODULE_WIDTH + Length.INCH), Length.MM);
        getPOSTNETBean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
    
        //Checksum mode    
        getPOSTNETBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue(
                POSTNETBean.DEFAULT_MODULE_WIDTH + Length.INCH), Length.MM);
        getPOSTNETBean().setIntercharGapWidth(igw.getValueAsMillimeter());

        Length h = new Length(cfg.getChild("tall-bar-height").getValue(
                POSTNETBean.DEFAULT_TALL_BAR_HEIGHT + Length.INCH), Length.MM);
        getPOSTNETBean().setBarHeight(h.getValueAsMillimeter());
        
        Length hbh = new Length(cfg.getChild("short-bar-height").getValue(
                POSTNETBean.DEFAULT_SHORT_BAR_HEIGHT + Length.INCH), Length.MM);
        getPOSTNETBean().setShortBarHeight(hbh.getValueAsMillimeter());

        getPOSTNETBean().setBaselinePosition(BaselineAlignment.byName(
            cfg.getChild("baseline-alignment").getValue(BaselineAlignment.ALIGN_BOTTOM.getName())));

        Configuration hr = cfg.getChild("human-readable", false);
        if (hr != null) {
            //Display checksum in hr-message or not
            getPOSTNETBean().setDisplayChecksum(
                    hr.getChild("display-checksum").getValueAsBoolean(false));
        }
    }
   
    /**
     * @return the underlying POSTNETBean
     */
    public POSTNETBean getPOSTNETBean() {
        return (POSTNETBean)getBean();
    }

}