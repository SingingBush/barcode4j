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
package org.krysalis.barcode4j.impl.upcean;

/**
 * This class is an implementation of the UPC-A barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCABean.java,v 1.1 2004-09-12 17:57:52 jmaerki Exp $
 */
public class UPCABean extends UPCEANBean {

    /** @see org.krysalis.barcode4j.impl.upcean.UPCEAN */
    public UPCEANLogicImpl createLogicImpl() {
        return new UPCALogicImpl(getChecksumMode());
    }

}