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

import java.util.List;
import org.krysalis.barcode4j.ChecksumMode;

/**
 * Implements the Australia Post Barcode.
 * 
 * @author Antun Oreskovic
 * @version $Id: AustPostLogicImpl.java 2008/12/30 $
 */
public class AustPostLogicImpl extends AbstractAustPostLogicImpl {

    /**
     * Main constructor
     * @param mode checksum mode
     */
    public AustPostLogicImpl(ChecksumMode mode) {
        super(mode);
    }

    @Override
    protected String[] encodeHighLevel(String msg) {
        List codewords = new java.util.ArrayList(msg.length());
        for (int i = 0, c = msg.length(); i < c; i++) {
            String code = msg.substring(i, i + 1);
            if (code == null) {
                throw new IllegalArgumentException("Illegal character: " + code);
            }
            codewords.add(code);
        }
        return (String[])codewords.toArray(new String[codewords.size()]);
    }

    @Override
    public char calcChecksum(String msg) {
        return 0;
    }
}
