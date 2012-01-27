/*
 * Copyright 2012 Jeremias Maerki, Switzerland
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

/* $Id: QRCodeBeanTest.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.qr;

import java.awt.Dimension;

import junit.framework.TestCase;

/**
 * Tests the QR Code bean.
 *
 * @version $Id: QRCodeBeanTest.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $
 */
public class QRCodeBeanTest extends TestCase {

    public void testParameters() throws Exception {
        QRCodeBean bean = new QRCodeBean();

        bean.setMaxSize(new Dimension(20, 20));
        assertEquals(20, bean.getMaxSize().width);
        bean.setMaxSize(null);
        assertNull(bean.getMaxSize());

        bean.setMinSize(new Dimension(24, 24));
        assertEquals(24, bean.getMinSize().width);
        bean.setMinSize(null);
        assertNull(bean.getMinSize());

        assertEquals("ISO-8859-1", bean.getEncoding()); //the default
        try {
            bean.setEncoding("DoesNotExist");
            fail("Need exception on bad encoding");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        bean.setEncoding("UTF-8");
        assertEquals("UTF-8", bean.getEncoding());

        assertEquals('L', bean.getErrorCorrectionLevel());
        try {
            bean.setEncoding("Z");
            fail("Need exception on bad error correction level");
        } catch (IllegalArgumentException iae) {
            //expected
        }
        bean.setErrorCorrectionLevel('M');
        assertEquals('M', bean.getErrorCorrectionLevel());
    }

}
