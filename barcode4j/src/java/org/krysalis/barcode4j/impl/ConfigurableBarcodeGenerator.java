/*
 * Copyright 2002-2007 Jeremias Maerki or contributors to Barcode4J, as applicable
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

import java.util.List;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.tools.Length;

/**
 * Base class for most Avalon-Configurable barcode implementation proxies.
 *
 * @version $Id: ConfigurableBarcodeGenerator.java,v 1.7 2007-04-18 12:00:42 jmaerki Exp $
 */
public abstract class ConfigurableBarcodeGenerator
            implements BarcodeGenerator, Configurable {

    /**
     * Contains all possible element names that may appear in barcode XML.
     */
    public static final String[] BARCODE_ELEMENTS;
    
    static {
        List elements = new java.util.ArrayList();
        //All barcode names
        elements.addAll(BarcodeUtil.getInstance().getClassResolver().getBarcodeNames());
        //All configuration element names
        elements.add("height");
        elements.add("module-width");
        elements.add("wide-factor");
        elements.add("quiet-zone");
        elements.add("checksum");
        elements.add("human-readable");
        elements.add("human-readable-font");
        elements.add("human-readable-size");
        elements.add("font-name");
        elements.add("font-size");
        elements.add("placement");
        elements.add("pattern");
        elements.add("display-start-stop");
        elements.add("display-checksum");
        elements.add("interchar-gap-width");
        elements.add("tall-bar-height");
        elements.add("short-bar-height");
        elements.add("baseline-alignment");
        elements.add("template");
        elements.add("group-separator");
        elements.add("check-digit-marker");
        elements.add("omit-brackets");
        elements.add("shape");
        BARCODE_ELEMENTS = (String[])elements.toArray(new String[elements.size()]);
    }
    
    /** Proxy target. Barcode bean to configure. */
    protected AbstractBarcodeBean bean;

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Height (must be evaluated after the font size because of setHeight())
        Length h = new Length(cfg.getChild("height").getValue("15mm"), "mm");
        getBean().setHeight(h.getValueAsMillimeter());

        //Quiet zone
        getBean().doQuietZone(cfg.getChild("quiet-zone").getAttributeAsBoolean("enabled", true));
        Length qz = new Length(cfg.getChild("quiet-zone").getValue(getDefaultQuietZone()), "mw");
        if (qz.getUnit().equalsIgnoreCase("mw")) {
            getBean().setQuietZone(qz.getValue() * getBean().getModuleWidth());
        } else {
            getBean().setQuietZone(qz.getValueAsMillimeter());
        }

        Configuration hr = cfg.getChild("human-readable", false);
        if ((hr != null) && (hr.getChildren().length > 0)) {
            //Human-readable placement
            getBean().setMsgPosition(HumanReadablePlacement.byName(
                hr.getChild("placement").getValue(HumanReadablePlacement.HRP_BOTTOM.getName())));

            Length fs = new Length(hr.getChild("font-size").getValue("8pt"));
            getBean().setFontSize(fs.getValueAsMillimeter());

            getBean().setFontName(hr.getChild("font-name").getValue("Helvetica"));

            getBean().setPattern(hr.getChild("pattern").getValue(""));
        } else {
            //Legacy code for compatibility

            //Human-readable placement
            getBean().setMsgPosition(HumanReadablePlacement.byName(
                cfg.getChild("human-readable").getValue(
                        HumanReadablePlacement.HRP_BOTTOM.getName())));

            Length fs = new Length(cfg.getChild("human-readable-size").getValue("8pt"));
            getBean().setFontSize(fs.getValueAsMillimeter());

            getBean().setFontName(cfg.getChild("human-readable-font").getValue("Helvetica"));
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

    /** @return the default quiet zone */
    protected String getDefaultQuietZone() {
        return "10mw";
    }

}
