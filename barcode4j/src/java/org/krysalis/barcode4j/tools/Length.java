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
 * This class represents a length (value plus unit). It is used to parse 
 * expressions like "0.21mm".
 * 
 * @author Jeremias Maerki
 * @version $Id: Length.java,v 1.2 2004-09-04 20:25:56 jmaerki Exp $
 */
public class Length {
    
    private double value;
    private String unit;
    
    /**
     * Creates a Length instance.
     * @param value the value
     * @param unit the unit (ex. "cm")
     */
    public Length(double value, String unit) {
        this.value = value;
        this.unit = unit.toLowerCase();
    }
    
    /**
     * Creates a Length instance.
     * @param text the String to parse
     * @param defaultUnit the default unit to assume
     */
    public Length(String text, String defaultUnit) {
        parse(text, defaultUnit);
    }
    
    /**
     * Creates a Length instance. The default unit assumed is "mm".
     * @param text the String to parse
     */
    public Length(String text) {
        this(text, null);
    }
    
    /**
     * Parses a value with unit.
     * @param text the String to parse
     * @param defaultUnit the default unit to assume
     */
    protected void parse(String text, String defaultUnit) {
        final String s = text.trim();
        if (s.length() == 0) {
            throw new IllegalArgumentException("Length is empty");
        }
        StringBuffer sb = new StringBuffer(s.length());
        int mode = 0;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            
            if (mode == 0) {
                //Parse value
                if (Character.isDigit(c) || c == '.' || c == ',') {
                    if (c == ',') {
                        c = '.';
                    }
                    sb.append(c);
                    i++;
                } else {
                    this.value = Double.parseDouble(sb.toString());
                    sb.setLength(0);
                    mode = 1;
                }
            } else if (mode == 1) {
                //Parse optional whitespace
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                mode = 2;
            } else if (mode == 2) {
                //Parse unit
                if (!Character.isWhitespace(c)) {
                    sb.append(c);
                    i++;
                } else {
                    //Break on first white space after unit
                    break;
                }
                
            }
        }
        if (mode == 0) {
            this.value = Double.parseDouble(sb.toString());
            mode = 1;
        }
        if (mode != 2) {
            if ((mode > 0) && (defaultUnit != null)) {
                this.unit = defaultUnit.toLowerCase();
                return;
            }
            throw new IllegalArgumentException("Invalid length specified. "
                    + "Expected '<value> <unit>' (ex. 1.7mm) but got: " + text);
        }
        this.unit = sb.toString().toLowerCase();
    }

    /**
     * Returns the unit.
     * @return String
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Returns the value.
     * @return double
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the value converted to internal units (mm).
     * @return the value (in mm)
     */
    public double getValueAsMillimeter() {
        if (this.unit.equals("mm")) {
            return this.value;
        } else if (this.unit.equals("cm")) {
            return this.value * 10;
        } else if (this.unit.equals("pt")) {
            return UnitConv.pt2mm(this.value);
        } else if (this.unit.equals("in")) {
            return UnitConv.in2mm(this.value);
        } else {
            throw new IllegalStateException("Don't know how to convert " 
                    + this.unit + " to mm");
        }
    }
    
    /** {@inheritDoc} */
    public String toString() {
        return getValue() + getUnit();
    }
    
}
