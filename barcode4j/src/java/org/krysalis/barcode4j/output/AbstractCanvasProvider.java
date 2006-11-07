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
package org.krysalis.barcode4j.output;

import org.krysalis.barcode4j.BarcodeDimension;

/**
 * Abstract base class for most CanvasProvider implementations.
 * 
 * @author Jeremias Maerki
 * @version $Id: AbstractCanvasProvider.java,v 1.3 2006-11-07 16:43:37 jmaerki Exp $
 */
public abstract class AbstractCanvasProvider implements CanvasProvider {

    /** the cached barcode dimensions */
    protected BarcodeDimension bardim;

    /** the barcode orientation (0, 90, 180, 270) */
    int orientation;
    
    public AbstractCanvasProvider() {
        this(0);
    }

    public AbstractCanvasProvider(int orientation) {
        this.orientation = BarcodeDimension.normalizeOrientation(orientation);
    }
    
    /**
     * @see org.krysalis.barcode4j.output.CanvasProvider
     */
    public void establishDimensions(BarcodeDimension dim) {
        this.bardim = dim;
    }

    /**
     * @see org.krysalis.barcode4j.output.CanvasProvider#getDimensions()
     */
    public BarcodeDimension getDimensions() {
        return this.bardim;
    }
    
    /** @see org.krysalis.barcode4j.output.CanvasProvider#getOrientation() */
    public int getOrientation() {
        return this.orientation;
    }

}
