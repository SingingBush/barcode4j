/*
 * $Id: AbstractSVGGeneratingCanvasProvider.java,v 1.1 2003-12-13 20:23:43 jmaerki Exp $
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
package org.krysalis.barcode4j.output.svg;

import org.krysalis.barcode4j.output.AbstractXMLGeneratingCanvasProvider;
import org.krysalis.barcode4j.output.BarcodeCanvasSetupException;

/**
 * Abstract base class for implementations that generate SVG.
 * 
 * @author Jeremias Maerki
 */
public abstract class AbstractSVGGeneratingCanvasProvider
    extends AbstractXMLGeneratingCanvasProvider {

    /** the SVG namespace: http://www.w3.org/2000/svg */
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    private boolean useNamespace = true;
    private String prefix = "";
    
    /**
     * Creates a new AbstractSVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public AbstractSVGGeneratingCanvasProvider(boolean useNamespace, String namespacePrefix) 
                throws BarcodeCanvasSetupException {
        if (!useNamespace && namespacePrefix != null) 
            throw new IllegalArgumentException("No prefix allow when namespaces are enabled");
        this.useNamespace = true;
        this.prefix = namespacePrefix;
    }
    
    /**
     * Creates a new AbstractSVGCanvasProvider with namespaces enabled.
     * @param namespacePrefix the namespace prefix to use, null for no prefix
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public AbstractSVGGeneratingCanvasProvider(String namespacePrefix) 
                throws BarcodeCanvasSetupException {
        this(true, namespacePrefix);
    }

    /**
     * Creates a new AbstractSVGCanvasProvider.
     * @param useNamespace Controls whether namespaces should be used
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public AbstractSVGGeneratingCanvasProvider(boolean useNamespace) 
                throws BarcodeCanvasSetupException {
        this(useNamespace, null);
    }

    /**
     * Creates a new AbstractSVGCanvasProvider with default settings (with 
     * namespaces, but without namespace prefix).
     * @throws BarcodeCanvasSetupException if setting up the provider fails
     */
    public AbstractSVGGeneratingCanvasProvider() 
                throws BarcodeCanvasSetupException {
        this(true, null);
    }

    /**
     * Indicates whether namespaces are enabled.
     * @return true if namespaces are enabled
     */
    public boolean isNamespaceEnabled() {
        return this.useNamespace;
    }
    
    /**
     * Returns the namespace prefix
     * @return the namespace prefix (may be null)
     */
    public String getNamespacePrefix() {
        return this.prefix;
    }
    
    /**
     * Constructs a fully qualified element name based on the namespace 
     * settings.
     * @param localName the local name
     * @return the fully qualified name
     */
    protected String getQualifiedName(String localName) {
        if (prefix == null || "".equals(prefix)) {
            return localName;
        } else {
            return prefix + ':' + localName;
        }
    }

}
