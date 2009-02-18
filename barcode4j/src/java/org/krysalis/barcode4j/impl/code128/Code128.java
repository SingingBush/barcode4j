/*
 * Copyright 2002-2004,2009 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.code128;


import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * This class is an implementation of the Code 128 barcode.
 *
 * @version $Id: Code128.java,v 1.2 2009-02-18 16:22:23 jmaerki Exp $
 */
public class Code128 extends ConfigurableBarcodeGenerator
            implements Configurable {

    /** Create a new instance. */
    public Code128() {
        this.bean = new Code128Bean();
    }

    /** {@inheritDoc} */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.21mm"), "mm");
        getCode128Bean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);

        String codesets = cfg.getChild("codesets").getValue(null);
        if (codesets != null) {
            codesets = codesets.toUpperCase();
            int bits = 0;
            if (codesets.indexOf('A') >= 0) {
                bits |= Code128Constants.CODESET_A;
            }
            if (codesets.indexOf('B') >= 0) {
                bits |= Code128Constants.CODESET_B;
            }
            if (codesets.indexOf('C') >= 0) {
                bits |= Code128Constants.CODESET_C;
            }
            getCode128Bean().setCodeset(bits);
        }
    }

    /**
     * @return the underlying Code128Bean
     */
    public Code128Bean getCode128Bean() {
        return (Code128Bean)getBean();
    }


}