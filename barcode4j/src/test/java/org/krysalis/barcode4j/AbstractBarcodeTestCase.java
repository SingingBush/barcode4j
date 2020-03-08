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

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import junit.framework.TestCase;

/**
 * Abstract base test class for tests. Provides a bad directory that is 
 * configurable from outside.
 * 
 * @author Jeremias Maerki
 * @version $Id: AbstractBarcodeTestCase.java,v 1.2 2004-09-04 20:26:21 jmaerki Exp $
 */
public abstract class AbstractBarcodeTestCase extends TestCase {

    /**
     * @see junit.framework.TestCase#TestCase(String)
     */
    public AbstractBarcodeTestCase(String name) {
        super(name);
    }

    /**
     * Returns the base directory to use for the tests.
     * @return the base directory
     */
    protected File loadTestFile(final String file) {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource(file).toURI()).toFile();
        } catch (final URISyntaxException e) {
            fail("Could no load file : "+file);
        }
        return null;
    }

}
