package org.krysalis.barcode4j.impl.aztec;

import com.google.zxing.aztec.encoder.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;

import static com.google.zxing.aztec.encoder.Encoder.DEFAULT_EC_PERCENT;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class AztecTest {

    @InjectMocks
    private Aztec aztec;

    @Mock(name = "bean")
    private AztecBean mockBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConfigureWithDefaults() throws ConfigurationException {
        verifyNoInteractions(this.mockBean);

        this.aztec.configure(new DefaultConfiguration(""));

        // verify bean doesn't call configure() on the ConfigurableBarcodeGenerator super class
        verify(this.mockBean, never()).doQuietZone(anyBoolean());

        // verify default configuration in Aztec
        verify(this.mockBean, times(1)).setModuleWidth(eq(AztecBean.DEFAULT_MODULE_WIDTH));
        verify(this.mockBean, times(1)).setEncoding(eq(StandardCharsets.ISO_8859_1.name()));
        verify(this.mockBean, times(1)).setErrorCorrectionLevel(eq(DEFAULT_EC_PERCENT));
        verify(this.mockBean, times(1)).setLayers(eq(Encoder.DEFAULT_AZTEC_LAYERS));

        verifyNoMoreInteractions(this.mockBean);
    }

    @Test
    void testConfigureWithSpecificValues() throws ConfigurationException {
        verifyNoInteractions(this.mockBean);

        final DefaultConfiguration configuration = new DefaultConfiguration("");

        final DefaultConfiguration moduleWidth = new DefaultConfiguration("module-width");
        moduleWidth.setValue(3.3);
        configuration.addChild(moduleWidth);

        final DefaultConfiguration encoding = new DefaultConfiguration("encoding");
        encoding.setValue(StandardCharsets.US_ASCII.name());
        configuration.addChild(encoding);

        final DefaultConfiguration ecLevel = new DefaultConfiguration("ec-level");
        ecLevel.setValue(50);
        configuration.addChild(ecLevel);

        final DefaultConfiguration layers = new DefaultConfiguration("layers");
        layers.setValue(22);
        configuration.addChild(layers);

        this.aztec.configure(configuration);

        // verify bean doesn't call configure() on the ConfigurableBarcodeGenerator super class
        verify(this.mockBean, never()).doQuietZone(anyBoolean());

        // verify configuration in Aztec has used values from the config
        verify(this.mockBean, times(1)).setModuleWidth(eq(3.3));
        verify(this.mockBean, times(1)).setEncoding(eq(StandardCharsets.US_ASCII.name()));
        verify(this.mockBean, times(1)).setErrorCorrectionLevel(eq(50));
        verify(this.mockBean, times(1)).setLayers(eq(22));

        verifyNoMoreInteractions(this.mockBean);
    }
}
