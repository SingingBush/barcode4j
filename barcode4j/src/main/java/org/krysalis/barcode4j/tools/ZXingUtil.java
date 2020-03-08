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

/* $Id: ZXingUtil.java,v 1.2 2012-05-17 13:50:42 jmaerki Exp $ */

package org.krysalis.barcode4j.tools;

/**
 * Utilities concerning the ZXing dependency.
 *
 * @version $Id: ZXingUtil.java,v 1.2 2012-05-17 13:50:42 jmaerki Exp $
 */
public class ZXingUtil {

    /**
     * Indicates whether ZXing is available in the classpath.
     * @return true if ZXing is available, false otherwise
     */
    public static boolean isZxingAvailable() {
        try {
            Class.forName("com.google.zxing.qrcode.QRCodeWriter");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
