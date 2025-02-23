/*
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
package org.krysalis.barcode4j.impl.aztec;

import com.google.zxing.aztec.encoder.Encoder;
import org.krysalis.barcode4j.configuration.Configurable;
import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.krysalis.barcode4j.impl.ConfigurableBarcodeGenerator;
import org.krysalis.barcode4j.tools.Length;

import java.nio.charset.StandardCharsets;

import static com.google.zxing.aztec.encoder.Encoder.DEFAULT_EC_PERCENT;

/**
 * This class is an implementation of the Aztec barcode.
 *
 * @author Samael Bate (singingbush)
 */
public class Aztec extends ConfigurableBarcodeGenerator implements Configurable {

    public Aztec() {
        this.bean = new AztecBean();
    }

    /**
     * @see org.krysalis.barcode4j.configuration.Configurable#configure(Configuration)
     */
    @Override
    public void configure(Configuration cfg) throws ConfigurationException {
        final AztecBean bean = (AztecBean) super.getBean();

        final Length mw = new Length(cfg.getChild("module-width").getValueAsDouble(AztecBean.DEFAULT_MODULE_WIDTH), "mm");
        bean.setModuleWidth(mw.getValueAsMillimeter());

        bean.setEncoding(cfg.getChild("encoding").getValue(StandardCharsets.ISO_8859_1.name()));

        // Set the values that are used by ZXing
        bean.setErrorCorrectionLevel(cfg.getChild("ec-level").getValueAsInteger(DEFAULT_EC_PERCENT));
        bean.setLayers(cfg.getChild("layers").getValueAsInteger(Encoder.DEFAULT_AZTEC_LAYERS));
    }
}
