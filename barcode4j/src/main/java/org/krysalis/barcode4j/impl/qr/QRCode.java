/*
 * Copyright 2012 Jeremias Maerki.
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

/* $Id: QRCode.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $ */

package org.krysalis.barcode4j.impl.qr;

import java.awt.Dimension;

import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import org.krysalis.barcode4j.configuration.Configurable;
import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;

/**
 * This class is an implementation of QR Code.
 *
 * @version $Id: QRCode.java,v 1.1 2012-01-27 14:36:35 jmaerki Exp $
 */
public class QRCode extends ConfigurableBarcodeGenerator implements Configurable {

    /** Create a new instance. */
    public QRCode() {
        this.bean = new QRCodeBean();
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configurable#configure(Configuration)
     */
    @Override
    public void configure(Configuration cfg) throws ConfigurationException {
        final QRCodeBean qrCodeBean = (QRCodeBean) getBean();

        //Module width (MUST ALWAYS BE FIRST BECAUSE QUIET ZONE MAY DEPEND ON IT)
        final String mws = cfg.getChild("module-width").getValue(null);
        if (mws != null) {
            final Length mw = new Length(mws, "mm");
            qrCodeBean.setModuleWidth(mw.getValueAsMillimeter());
        }

        super.configure(cfg);

        final String encoding = cfg.getChild("encoding").getValue(null);
        if (encoding != null) {
            qrCodeBean.setEncoding(encoding);
        }

        final String ecLevel = cfg.getChild("ec-level").getValue(null);
        if (ecLevel != null && !ecLevel.isEmpty()) {
            qrCodeBean.setErrorCorrectionLevel(ecLevel.charAt(0));
        }

        final String minSize = cfg.getChild("min-symbol-size").getValue(null);
        if (minSize != null) {
            qrCodeBean.setMinSize(parseSymbolSize(minSize));
        }

        final String maxSize = cfg.getChild("max-symbol-size").getValue(null);
        if (maxSize != null) {
            qrCodeBean.setMaxSize(parseSymbolSize(maxSize));
        }
    }

    private Dimension parseSymbolSize(String size) {
        final int idx = size.indexOf('x');

        if (idx > 0) {
            return new Dimension(
                Integer.parseInt(size.substring(0, idx)),
                Integer.parseInt(size.substring(idx + 1))
            );
        } else {
            int extent = Integer.parseInt(size);
            return new Dimension(extent, extent);
        }
    }

}
