/*
 * Copyright 2024
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
package org.krysalis.barcode4j.impl.qr;

import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Dimension;
import java.nio.charset.StandardCharsets;

import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.tools.Length;

import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Samael Bate (singingbush)
 * created on 03/03/2024
 */
public class QRCodeTest {

    @InjectMocks
    private QRCode qrCode;

    @Mock(name = "bean")
    private QRCodeBean mockBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConfigureWithDefaults() throws ConfigurationException {
        verifyNoInteractions(this.mockBean);

        this.qrCode.configure(new DefaultConfiguration(""));

        // verify bean has defaults set in the ConfigurableBarcodeGenerator super class
        verify(this.mockBean, times(1)).doQuietZone(eq(true)); // Horizontal
        verify(this.mockBean, never()).setVerticalQuietZone(anyDouble());
        verify(this.mockBean, never()).setMsgPosition(any(HumanReadablePlacement.class));
        verify(this.mockBean, never()).setFontSize(anyDouble());
        verify(this.mockBean, times(1)).setFontName(eq("Helvetica"));
        verify(this.mockBean, never()).setPattern(anyString());

        // verify default configuration in QRCode
        verify(this.mockBean, never()).setModuleWidth(anyDouble());
        verify(this.mockBean, never()).setEncoding(anyString());
        verify(this.mockBean, never()).setErrorCorrectionLevel(anyChar());
        verify(this.mockBean, never()).setMinSize(any(Dimension.class));
        verify(this.mockBean, never()).setMaxSize(any(Dimension.class));

        verifyNoMoreInteractions(this.mockBean);
    }

    @Test
    void testConfigureWithValues() throws ConfigurationException {
        final Configuration config = mock(Configuration.class);

        final DefaultConfiguration moduleWidth = new DefaultConfiguration("");
        moduleWidth.setValue("100");
        when(config.getChild(eq("module-width"))).thenReturn(moduleWidth);

        final DefaultConfiguration quietZone = new DefaultConfiguration("");
        quietZone.setAttribute("quiet-zone", String.valueOf(true));
        quietZone.setValue("8mw");
        when(this.mockBean.getModuleWidth()).thenReturn(1.0d); // needed as QZ is multiplied by width
        when(config.getChild(eq("quiet-zone"))).thenReturn(quietZone);

        final DefaultConfiguration verticalQuietZone = new DefaultConfiguration("");
        verticalQuietZone.setValue("1"); // inch
        when(config.getChild(eq("vertical-quiet-zone"))).thenReturn(verticalQuietZone);

        // human-readable has 4 child nodes
        final DefaultConfiguration humanReadable = new DefaultConfiguration("");

        final DefaultConfiguration humanReadablePlacement = new DefaultConfiguration("placement");
        humanReadablePlacement.setValue(HumanReadablePlacement.HRP_TOP.getName());
        humanReadable.addChild(humanReadablePlacement);

        final DefaultConfiguration humanReadableFontSize = new DefaultConfiguration("font-size");
        humanReadableFontSize.setValue("11mm");
        humanReadable.addChild(humanReadableFontSize);

        final DefaultConfiguration humanReadableFontName = new DefaultConfiguration("font-name");
        humanReadableFontName.setValue("Some Font");
        humanReadable.addChild(humanReadableFontName);

        final DefaultConfiguration humanReadableFontPattern = new DefaultConfiguration("pattern");
        humanReadable.addChild(humanReadableFontPattern);

        when(config.getChild(eq("human-readable"), eq(false))).thenReturn(humanReadable);

        final DefaultConfiguration encoding = new DefaultConfiguration("");
        encoding.setValue(StandardCharsets.US_ASCII.name());
        when(config.getChild(eq("encoding"))).thenReturn(encoding);

        final DefaultConfiguration ecLevel = new DefaultConfiguration("");
        ecLevel.setValue(String.valueOf(QRConstants.ERROR_CORRECTION_LEVEL_M));
        when(config.getChild(eq("ec-level"))).thenReturn(ecLevel);

        final DefaultConfiguration minSymbolSize = new DefaultConfiguration("");
        minSymbolSize.setValue(8);
        when(config.getChild(eq("min-symbol-size"))).thenReturn(minSymbolSize);

        final DefaultConfiguration maxSymbolSize = new DefaultConfiguration("");
        maxSymbolSize.setValue(12);
        when(config.getChild(eq("max-symbol-size"))).thenReturn(maxSymbolSize);

        verifyNoInteractions(this.mockBean);

        this.qrCode.configure(config);

        // verify bean has values set in the ConfigurableBarcodeGenerator super class
        verify(this.mockBean, times(1)).doQuietZone(eq(true)); // Horizontal
        verify(this.mockBean, times(1)).setQuietZone(eq(8.0d));
        verify(this.mockBean, times(1)).setVerticalQuietZone(eq(new Length("1", Length.INCH).getValueAsMillimeter()));
        verify(this.mockBean, times(1)).setMsgPosition(eq(HumanReadablePlacement.HRP_TOP));
        verify(this.mockBean, times(1)).setFontSize(eq(11.0d));
        verify(this.mockBean, times(1)).setFontName(eq("Some Font"));
        verify(this.mockBean, times(1)).setPattern(eq(""));

        // verify configuration in QRCode
        verify(this.mockBean, times(1)).setModuleWidth(eq(100.0d));
        verify(this.mockBean, times(1)).setEncoding(eq("US-ASCII"));
        verify(this.mockBean, times(1)).setErrorCorrectionLevel(eq(QRConstants.ERROR_CORRECTION_LEVEL_M));
        verify(this.mockBean, times(1)).setMinSize(any(Dimension.class));
        verify(this.mockBean, times(1)).setMaxSize(any(Dimension.class));
    }
}
