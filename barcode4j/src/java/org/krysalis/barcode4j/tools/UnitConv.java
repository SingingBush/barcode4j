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
package org.krysalis.barcode4j.tools;

/**
 * Utility class for unit conversions.
 * 
 * @author Jeremias Maerki
 * @version $Id: UnitConv.java,v 1.2 2004-09-04 20:25:56 jmaerki Exp $
 */
public class UnitConv {

    /**
     * Utility class: Constructor prevents instantiating when subclassed.
     */
    protected UnitConv() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Converts millimeters (mm) to points (pt)
     * @param mm the value in mm
     * @return the value in pt
     */
    public static double mm2pt(double mm) {
        return mm * 2.835;
    }

    /**
     * Converts points (pt) to millimeters (mm)
     * @param pt the value in pt
     * @return the value in mm
     */
    public static double pt2mm(double pt) {
        return pt / 2.835;
    }
    
    /**
     * Converts millimeters (mm) to inches (in)
     * @param mm the value in mm
     * @return the value in inches
     */
    public static double mm2in(double mm) {
        return mm / 25.4;
    }
    
    /**
     * Converts inches (in) to millimeters (mm)
     * @param in the value in inches
     * @return the value in mm
     */
    public static double in2mm(double in) {
        return in * 25.4;
    }
    
    /**
     * Converts millimeters (mm) to pixels (px)
     * @param mm the value in mm
     * @param resolution the resolution in dpi (dots per inch)
     * @return the value in pixels
     */
    public static int mm2px(double mm, int resolution) {
        return (int)Math.round(mm2in(mm) * resolution);
    }

}
