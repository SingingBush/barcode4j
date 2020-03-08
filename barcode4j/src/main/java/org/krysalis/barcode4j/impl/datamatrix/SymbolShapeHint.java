/*
 * Copyright 2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl.datamatrix;

/**
 * Enumeration for DataMatrix symbol shape hint. It can be used to force square or rectangular
 * symbols.
 * 
 * @version $Id: SymbolShapeHint.java,v 1.2 2007-07-13 09:57:05 jmaerki Exp $
 */
public class SymbolShapeHint {

    /** The human-readable part is suppressed. */
    public static final SymbolShapeHint FORCE_NONE
                                    = new SymbolShapeHint("force-none");
    /** The human-readable part is placed at the top of the barcode. */
    public static final SymbolShapeHint FORCE_SQUARE
                                    = new SymbolShapeHint("force-square");
    /** The human-readable part is placed at the bottom of the barcode. */
    public static final SymbolShapeHint FORCE_RECTANGLE
                                    = new SymbolShapeHint("force-rectangle");

    private String name;
    
    /**
     * Creates a new SymbolShapeHint instance.
     * @param name the name for the instance
     */
    protected SymbolShapeHint(String name) {
        this.name = name;
    }
    
    /**
     * @return the name of the instance.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns a SymbolShapeHint instance by name.
     * @param name the name of the instance
     * @return the requested instance
     */
    public static SymbolShapeHint byName(String name) {
        if (name.equalsIgnoreCase(SymbolShapeHint.FORCE_NONE.getName())) {
            return SymbolShapeHint.FORCE_NONE;
        } else if (name.equalsIgnoreCase(SymbolShapeHint.FORCE_SQUARE.getName())) {
            return SymbolShapeHint.FORCE_SQUARE;
        } else if (name.equalsIgnoreCase(SymbolShapeHint.FORCE_RECTANGLE.getName())) {
            return SymbolShapeHint.FORCE_RECTANGLE;
        } else {
            throw new IllegalArgumentException(
                "Invalid SymbolShapeHint: " + name);
        }
    }
    
    /** @see java.lang.Object#toString() */
    public String toString() {
        return getName();
    }
}