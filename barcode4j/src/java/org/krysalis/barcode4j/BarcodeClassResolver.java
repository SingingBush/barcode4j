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
 * This interface is used to resolve arbitrary string to classnames of Barcode
 * implementations.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeClassResolver.java,v 1.3 2004-09-12 17:57:54 jmaerki Exp $
 */
public interface BarcodeClassResolver {

    /**
     * Returns the Class object of a Barcode implementation.
     * 
     * @param name Name or Classname of a Barcode implementation class
     * @return Class The class requested
     * @throws ClassNotFoundException If the class could not be resolved
     */
    Class resolve(String name) throws ClassNotFoundException;

    /**
     * Returns the Class object of a Barcode bean implementation.
     * 
     * @param name Name or Classname of a Barcode bean implementation class
     * @return Class The class requested
     * @throws ClassNotFoundException If the class could not be resolved
     */
    Class resolveBean(String name) throws ClassNotFoundException;
}
