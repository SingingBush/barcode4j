/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j;

/**
 * Enumeration for the alignment of bars when the heights are not uniform.
 * 
 * @author Chris Dolphy
 * @version $Id: BaselineAlignment.java,v 1.3 2004-10-02 14:53:22 jmaerki Exp $
 */
public class BaselineAlignment {

    /** The bars are aligned to be even along the top. */
    public static final BaselineAlignment ALIGN_TOP = new BaselineAlignment("top");
    /** The bars are aligned to be even along the bottom. */
    public static final BaselineAlignment ALIGN_BOTTOM = new BaselineAlignment("bottom");

    private String name;
    
    /**
     * Creates a new BaselineAlignment instance.
     * @param name the name for the instance
     */
    protected BaselineAlignment(String name) {
        this.name = name;
    }

    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns a BaselineAlignment instance by name.
     * @param name the name of the instance
     * @return the requested instance
     */
    public static BaselineAlignment byName(String name) {
        if (name.equalsIgnoreCase(BaselineAlignment.ALIGN_TOP.getName())) {
            return BaselineAlignment.ALIGN_TOP;
        } else if (name.equalsIgnoreCase(BaselineAlignment.ALIGN_BOTTOM.getName())) {
            return BaselineAlignment.ALIGN_BOTTOM;
        } else {
            throw new IllegalArgumentException(
                "Invalid BaselineAlignment: " + name);
        }
    }
    



}
