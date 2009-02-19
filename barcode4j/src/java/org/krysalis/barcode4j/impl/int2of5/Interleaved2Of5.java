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
package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This class is an implementation of the Interleaved 2 of 5 barcode.
 *
 * @author Jeremias Maerki
 * @version $Id: Interleaved2Of5.java,v 1.3 2009-02-19 10:14:54 jmaerki Exp $
 */
public class Interleaved2Of5 extends ConfigurableBarcodeGenerator
            implements Configurable {

    /** Create a new instance. */
    public Interleaved2Of5() {
        this.bean = createBean();
    }

    /**
     * Creates the Bean instance.
     * @return the Bean instance
     */
    protected AbstractBarcodeBean createBean() {
        return new Interleaved2Of5Bean();
    }

    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        Interleaved2Of5Bean bean = getInterleaved2Of5Bean();
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width")
                        .getValue(bean.getModuleWidth() + "mm"), "mm");
        bean.setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);

        //Checksum mode
        bean.setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));

        //Wide factor
        bean.setWideFactor(
            cfg.getChild("wide-factor").getValueAsFloat((float)bean.getWideFactor()));

        Configuration hr = cfg.getChild("human-readable", false);
        if (hr != null) {
            //Display checksum in hr-message or not
            bean.setDisplayChecksum(
                    hr.getChild("display-checksum").getValueAsBoolean(false));
        }
    }

    /**
     * Returns the underlying {@code Interleaved2Of5Bean}.
     * @return the underlying Interleaved2Of5Bean
     */
    public Interleaved2Of5Bean getInterleaved2Of5Bean() {
        return (Interleaved2Of5Bean)getBean();
    }

}