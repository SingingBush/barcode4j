/*
 * Copyright 2006-2009 Antun Oreskovic.
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

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Implements the Australia Post Barcode.
 * 
 * @author Antun Oreskovic
 * @version $Id: AustPost.java 2008/12/30 $
 */
public class AustPost extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public AustPost() {
        this.bean = new AustPostBean();
    }
    
    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.4mm"), "mm");
        getAustPostBean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
    
        //Checksum mode    
        getAustPostBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue("0.7mw"), "mw");
        if (igw.getUnit().equalsIgnoreCase("mw")) {
            getAustPostBean().setIntercharGapWidth(
                    igw.getValue() * getAustPostBean().getModuleWidth());
        } else {
            getAustPostBean().setIntercharGapWidth(igw.getValueAsMillimeter());
        }

        Length h = new Length(cfg.getChild("ascender-height").getValue("5.4mm"), "mm");
        getAustPostBean().setAscenderHeight(h.getValueAsMillimeter());
        
        Length hbh = new Length(cfg.getChild("track-height").getValue("1.4mm"), "mm");
        getAustPostBean().setTrackHeight(hbh.getValueAsMillimeter());
    }
   
    /**
     * @return the underlying AustPostBean
     */
    public AustPostBean getAustPostBean() {
        return (AustPostBean)getBean();
    }
}