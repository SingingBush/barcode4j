/*
 * Copyright 2006-2007 Jeremias Maerki.
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

import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This class is an implementation of the PDF417 barcode.
 *
 * @version $Id: PDF417.java,v 1.7 2012-05-17 13:57:37 jmaerki Exp $
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

        Configuration child;
        child = cfg.getChild("min-columns", false);
        if (child != null) {
            getPDF417Bean().setMinCols(child.getValueAsInteger());
        }
        child = cfg.getChild("max-columns", false);
        if (child != null) {
            getPDF417Bean().setMaxCols(child.getValueAsInteger());
        }
        child = cfg.getChild("min-rows", false);
        if (child != null) {
            getPDF417Bean().setMinRows(child.getValueAsInteger());
        }
        child = cfg.getChild("max-rows", false);
        if (child != null) {
            getPDF417Bean().setMaxRows(child.getValueAsInteger());
        }

        //Setting "columns" will override min/max-columns and min/max-rows!!!
        child = cfg.getChild("columns", false);
        if (child != null) {
            getPDF417Bean().setColumns(child.getValueAsInteger());
        }

        getPDF417Bean().setErrorCorrectionLevel(cfg.getChild("ec-level").getValueAsInteger(
                PDF417Bean.DEFAULT_ERROR_CORRECTION_LEVEL));

        String rhs = cfg.getChild("row-height").getValue(null);
        if (rhs != null) {
            Length rh = new Length(rhs, "mw");
            if (rh.getUnit().equalsIgnoreCase("mw")) {
                getPDF417Bean().setRowHeight(rh.getValue() * getBean().getModuleWidth());
            } else {
                getPDF417Bean().setRowHeight(rh.getValueAsMillimeter());
            }
        } else {
            getPDF417Bean().setRowHeight(
                    PDF417Bean.DEFAULT_X_TO_Y_FACTOR * getBean().getModuleWidth());
        }

        child = cfg.getChild("width-to-height-ratio", false);
        if (child != null) {
            getPDF417Bean().setWidthToHeightRatio(child.getValueAsFloat());
        }

        child = cfg.getChild("encoding", false);
        if (child != null) {
            getPDF417Bean().setEncoding(child.getValue(null));
            getPDF417Bean().setECIEnabled(child.getAttributeAsBoolean("enable-eci", false));
        }
    }

    /**
     * @return the underlying PDF417Bean
     */
    public PDF417Bean getPDF417Bean() {
        return (PDF417Bean)getBean();
    }

}