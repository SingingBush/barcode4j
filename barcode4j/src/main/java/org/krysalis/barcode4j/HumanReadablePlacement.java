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
package org.krysalis.barcode4j;

/**
 * Enumeration for placement of the human readable part of a barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: HumanReadablePlacement.java,v 1.3 2004-10-02 14:53:22 jmaerki Exp $
 */
public class HumanReadablePlacement {

    /** The human-readable part is suppressed. */
    public static final HumanReadablePlacement HRP_NONE
                                    = new HumanReadablePlacement("none");
    /** The human-readable part is placed at the top of the barcode. */
    public static final HumanReadablePlacement HRP_TOP
                                    = new HumanReadablePlacement("top");
    /** The human-readable part is placed at the bottom of the barcode. */
    public static final HumanReadablePlacement HRP_BOTTOM
                                    = new HumanReadablePlacement("bottom");

    private String name;
    
    /**
     * Creates a new HumanReadablePlacement instance.
     * @param name the name for the instance
     */
    protected HumanReadablePlacement(String name) {
        this.name = name;
    }
    
    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns a HumanReadablePlacement instance by name.
     * @param name the name of the instance
     * @return the requested instance
     */
    public static HumanReadablePlacement byName(String name) {
        if (name.equalsIgnoreCase(HumanReadablePlacement.HRP_NONE.getName())) {
            return HumanReadablePlacement.HRP_NONE;
        } else if (name.equalsIgnoreCase(HumanReadablePlacement.HRP_TOP.getName())) {
            return HumanReadablePlacement.HRP_TOP;
        } else if (name.equalsIgnoreCase(HumanReadablePlacement.HRP_BOTTOM.getName())) {
            return HumanReadablePlacement.HRP_BOTTOM;
        } else {
            throw new IllegalArgumentException(
                "Invalid HumanReadablePlacement: " + name);
        }
    }
    

}