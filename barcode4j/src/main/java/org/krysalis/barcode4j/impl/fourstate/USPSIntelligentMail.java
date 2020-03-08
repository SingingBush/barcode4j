/*
 * Copyright 2008 Jeremias Maerki.
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
 * Implements the USPS Intelligent Mail Barcode (Four State Customer Barcode).
 * 
 * @author Jeremias Maerki
 * @version $Id: USPSIntelligentMail.java,v 1.1 2008-05-13 13:00:43 jmaerki Exp $
 */
public class USPSIntelligentMail extends ConfigurableBarcodeGenerator 
            implements Configurable {

    /** Create a new instance. */
    public USPSIntelligentMail() {
        this.bean = new USPSIntelligentMailBean();
    }
    
    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue(
                USPSIntelligentMailBean.DEFAULT_MODULE_WIDTH_INCH + Length.INCH), Length.INCH);
        getUSPSIntelligentMailBean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
    
        //Checksum mode    
        getUSPSIntelligentMailBean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
    
        //Inter-character gap width    
        Length igw = new Length(cfg.getChild("interchar-gap-width").getValue(
                USPSIntelligentMailBean.DEFAULT_INTERCHAR_GAP_WIDTH_INCH + Length.INCH),
                    Length.INCH);
        if (igw.getUnit().equalsIgnoreCase("mw")) {
            getUSPSIntelligentMailBean().setIntercharGapWidth(
                    igw.getValue() * getUSPSIntelligentMailBean().getModuleWidth());
        } else {
            getUSPSIntelligentMailBean().setIntercharGapWidth(igw.getValueAsMillimeter());
        }

        Length ah = new Length(cfg.getChild("ascender-height").getValue(
                USPSIntelligentMailBean.DEFAULT_ASCENDER_HEIGHT_INCH + Length.INCH), Length.INCH);
        getUSPSIntelligentMailBean().setAscenderHeight(ah.getValueAsMillimeter());
        
        Length th = new Length(cfg.getChild("track-height").getValue(
                USPSIntelligentMailBean.DEFAULT_TRACK_HEIGHT_INCH + Length.INCH), Length.INCH);
        getUSPSIntelligentMailBean().setTrackHeight(th.getValueAsMillimeter());
    }
   
    /**
     * Returns the underlying USPSIntelligentMailBean.
     * @return the underlying USPSIntelligentMailBean
     */
    public USPSIntelligentMailBean getUSPSIntelligentMailBean() {
        return (USPSIntelligentMailBean)getBean();
    }

}