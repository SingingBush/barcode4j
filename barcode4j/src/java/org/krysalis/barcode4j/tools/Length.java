/*
 * $Id: Length.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.krysalis.barcode4j.tools;

/**
 * This class represents a length (value plus unit). It is used to parse 
 * expressions like "0.21mm".
 * 
 * @author Jeremias Maerki
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
