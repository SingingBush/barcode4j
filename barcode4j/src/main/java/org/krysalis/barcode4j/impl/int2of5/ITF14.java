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
package org.krysalis.barcode4j.impl.int2of5;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This class is an implementation of ITF-14 (as defined by the
 * <a href="http://www.gs1.org">GS1 standards organization</a>).
 * ITF-14 is basically an Interleaved 2 of 5 barcode with an added, so-called bearer bar.
 * @version $Id: ITF14.java,v 1.1 2009-02-19 10:14:54 jmaerki Exp $
 */
public class ITF14 extends Interleaved2Of5 {

    /** Create a new instance. */
    public ITF14() {
        super();
    }

    /** {@inheritDoc} */
    protected AbstractBarcodeBean createBean() {
        return new ITF14Bean();
    }

    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        super.configure(cfg);

        //Bearer bar width
        Configuration c = cfg.getChild("bearer-bar-width", false);
        if (c != null) {
            Length w = new Length(c.getValue(), "mw");
            if (w.getUnit().equalsIgnoreCase("mw")) {
                getITFBean().setBearerBarWidth(w.getValue() * getBean().getModuleWidth());
            } else {
                getITFBean().setBearerBarWidth(w.getValueAsMillimeter());
            }
        }

        //Bearer ox
        c = cfg.getChild("bearer-box", false);
        if (c != null) {
            getITFBean().setBearerBox(c.getValueAsBoolean());
        }
    }

    /**
     * Returns the underlying {@code ITF14Bean}.
     * @return the underlying {@code ITF14Bean}
     */
    public ITF14Bean getITFBean() {
        return (ITF14Bean)getBean();
    }

}