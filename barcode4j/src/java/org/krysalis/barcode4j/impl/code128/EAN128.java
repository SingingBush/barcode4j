/*
 * Copyright 2002-2004 Jeremias Maerki.
 * Copyright 2005 Jeremias Maerki, Dietmar Bürkle.
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


import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.tools.Length;

/**
 * This class is an implementation of the Code 128 barcode.
 * 
 * @author Jeremias Maerki, Dietmar Bürkle
 */
public class EAN128 extends Code128 
            implements Configurable {

    /** Create a new instance. */
    public EAN128() {
        this.bean = new EAN128Bean();
    }
    
    /**
     * @return the underlying Code128Bean
     */
    public EAN128Bean getEAN128Bean() {
        return (EAN128Bean)getBean();
    }
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(Configuration)
     */
    public void configure(Configuration cfg) throws ConfigurationException {
        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        Length mw = new Length(cfg.getChild("module-width").getValue("0.21mm"), "mm");
        getEAN128Bean().setModuleWidth(mw.getValueAsMillimeter());

        super.configure(cfg);
        
        //Checksum mode        
        getEAN128Bean().setChecksumMode(ChecksumMode.byName(
            cfg.getChild("checksum").getValue(ChecksumMode.CP_AUTO.getName())));
        //Checkdigit place holder
        getEAN128Bean().setCheckDigitMarker(getFirstChar(
                cfg.getChild("check-digit-marker").getValue("\u00f0")));
        //Template
        getEAN128Bean().setTemplate(cfg.getChild("template").getValue(""));
        //group seperator aka FNC_1 
        getEAN128Bean().setGroupSeparator(getFirstChar(
                cfg.getChild("group-separator").getValue("\u00f1")));

        Configuration hr = cfg.getChild("human-readable", false);
        if (hr != null) {
            //omit Brackets for AI
            getEAN128Bean().setOmitBrackets(
                    hr.getChild("omit-brackets").getValueAsBoolean(false));
        }
    }
    
    private char getFirstChar(String s) {
        s = s.trim();
        if (s != null && s.length() > 0) {
            return s.charAt(0);
        } else {
            throw new IllegalArgumentException("Value must have at least one character");
        }
    }
}