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
package org.krysalis.barcode4j.impl;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;

/**
 * Base class for most Avalon-Configurable barcode implementation proxies.
 * 
 * @author Jeremias Maerki
 * @version $Id: ConfigurableBarcodeGenerator.java,v 1.1 2004-09-12 17:57:51 jmaerki Exp $
 */
public abstract class ConfigurableBarcodeGenerator 
            implements BarcodeGenerator, Configurable {

    /** Proxy target. Barcode bean to configure. */ 
    protected AbstractBarcodeBean bean;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Human-readable placement
        getBean().setMsgPosition(HumanReadablePlacement.byName(
            cfg.getChild("human-readable").getValue(HumanReadablePlacement.HRP_BOTTOM.getName())));

        Length fs = new Length(cfg.getChild("human-readable-size").getValue("8pt"));
        getBean().setFontSize(fs.getValueAsMillimeter());

        //TODO: this does not seem to work
        getBean().setFontName(cfg.getChild("human-readable-font").getValue("Helvetica"));
        
        //Height (must be evaluated after the font size because of setHeight())
        Length h = new Length(cfg.getChild("height").getValue("15mm"), "mm");
        getBean().setHeight(h.getValueAsMillimeter());
        
        //Quiet zone
        getBean().doQuietZone(cfg.getChild("quiet-zone").getAttributeAsBoolean("enabled", true));
        Length qz = new Length(cfg.getChild("quiet-zone").getValue("10mw"), "mw");
        if (qz.getUnit().equalsIgnoreCase("mw")) {
            getBean().setQuietZone(qz.getValue() * getBean().getModuleWidth());
        } else {
            getBean().setQuietZone(qz.getValueAsMillimeter());
        }
    }

    /**
     * Provides access to the prodxa target.
     * @return the underlying barcode bean
     */
    public AbstractBarcodeBean getBean() {
        return this.bean;
    }
    
    /** @see org.krysalis.barcode4j.BarcodeGenerator */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        getBean().generateBarcode(canvas, msg);
    }
    
    /** @see org.krysalis.barcode4j.BarcodeGenerator */
    public BarcodeDimension calcDimensions(String msg) {
        return getBean().calcDimensions(msg);
    }
    
}
