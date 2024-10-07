package org.krysalis.barcode4j.fop;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.fop.render.AbstractRenderer;
import org.apache.fop.render.Graphics2DAdapter;
import org.apache.fop.render.ImageAdapter;
import org.apache.fop.render.RendererContext;
import org.apache.fop.render.RendererContextConstants;
import org.apache.xmlgraphics.java2d.Graphics2DImagePainter;
import org.junit.jupiter.api.DisplayName;
import org.w3c.dom.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FOUserAgent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * @author Samael Bate (singingbush)
 * created on 06/10/2024
 */
class BarcodeXMLHandlerTest {

    final FOUserAgent userAgent = FopFactory.newInstance(new File(".").toURI())
        .newFOUserAgent();

    private BarcodeXMLHandler xmlHandler;

    @BeforeEach
    void setUp() {
        xmlHandler = new BarcodeXMLHandler();
    }

    @Test
    @DisplayName("Handler should support sub classes of Java2DRenderer")
    void testRendererSupport() {
        // should not support non Java 2d renderers
        assertFalse(xmlHandler.supportsRenderer(new org.apache.fop.render.txt.TXTRenderer(userAgent)));
        assertFalse(xmlHandler.supportsRenderer(new org.apache.fop.render.xml.XMLRenderer(userAgent)));

        // should support and Java2DRenderer based renderers:
        assertTrue(xmlHandler.supportsRenderer(new org.apache.fop.render.awt.AWTRenderer(userAgent)));
        assertTrue(xmlHandler.supportsRenderer(new org.apache.fop.render.bitmap.PNGRenderer(userAgent)));
        assertTrue(xmlHandler.supportsRenderer(new org.apache.fop.render.print.PageableRenderer(userAgent)));
        assertTrue(xmlHandler.supportsRenderer(new org.apache.fop.render.print.PrintRenderer(userAgent)));
        assertTrue(xmlHandler.supportsRenderer(new org.apache.fop.render.bitmap.TIFFRenderer(userAgent)));
    }

    //@Test // Native render mode
    //@DisplayName("Native render should result in use of EPSCanvasProvider")
    //void testNativeRenderMode() {}

    @Test // Graphics2D render mode
    @DisplayName("Graphics2D render should result in call to Graphics2DAdapter::paintImage()")
    void testGraphics2DRenderMode() throws Exception {
        final RendererContext ctx = mock(RendererContext.class);
        when(ctx.getProperty(RendererContextConstants.XPOS)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.YPOS)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.WIDTH)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.HEIGHT)).thenReturn(0);
        final AbstractRenderer renderer = mock(AbstractRenderer.class);
        final Graphics2DAdapter g2dAdapter = mock(Graphics2DAdapter.class);
        when(renderer.getGraphics2DAdapter()).thenReturn(g2dAdapter);
        when(ctx.getRenderer()).thenReturn(renderer);

        final String xmlCfg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<cfg msg=\"012345678905\" orientation=\"90\" render-mode=\"g2d\">\n" +
            "  <upc-A>\n" +
            "    <height>22mm</height>\n" +
            "    <human-readable>top</human-readable>\n" +
            "  </upc-A>\n" +
            "</cfg>";
        final InputStream stream = new ByteArrayInputStream(xmlCfg.getBytes(StandardCharsets.UTF_8));
        final Document doc = creatDocumentFromStream(stream);

        xmlHandler.handleXML(ctx, doc, "");

        verify(g2dAdapter, times(1)).paintImage(
            any(Graphics2DImagePainter.class),
            eq(ctx),
            eq(0),
            eq(0),
            eq(0),
            eq(0)
        );
        verify(renderer, times(0)).renderXML(eq(ctx), any(), eq(SVGDOMImplementation.SVG_NAMESPACE_URI));
    }

    @Test // bitmap render mode
    @DisplayName("Bitmap render should result in call to ImageAdapter::paintImage()")
    void testBitmapRenderMode() throws Exception {
        final RendererContext ctx = mock(RendererContext.class);
        when(ctx.getProperty(RendererContextConstants.XPOS)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.YPOS)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.WIDTH)).thenReturn(0);
        when(ctx.getProperty(RendererContextConstants.HEIGHT)).thenReturn(0);
        final AbstractRenderer renderer = mock(AbstractRenderer.class);
        final ImageAdapter imgAdapter = mock(ImageAdapter.class);
        when(renderer.getImageAdapter()).thenReturn(imgAdapter);
        when(ctx.getRenderer()).thenReturn(renderer);

        final String xmlCfg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<cfg msg=\"012345678905\" orientation=\"90\" render-mode=\"bitmap\">\n" +
            "  <upc-A>\n" +
            "    <height>22mm</height>\n" +
            "    <human-readable>top</human-readable>\n" +
            "  </upc-A>\n" +
            "</cfg>";
        final InputStream stream = new ByteArrayInputStream(xmlCfg.getBytes(StandardCharsets.UTF_8));
        final Document doc = creatDocumentFromStream(stream);

        xmlHandler.handleXML(ctx, doc, "");

        verify(imgAdapter, times(1)).paintImage(
            any(),
            eq(ctx),
            eq(0),
            eq(0),
            eq(0),
            eq(0)
        );

        verify(renderer, times(0)).renderXML(eq(ctx), any(), eq(SVGDOMImplementation.SVG_NAMESPACE_URI));
    }

    @Test // default will be SVG
    @DisplayName("Default render should result in call to renderXML()")
    void testDefaultRenderMode() throws Exception {
        final RendererContext ctx = mock(RendererContext.class);
        final AbstractRenderer renderer = mock(AbstractRenderer.class);
        when(ctx.getRenderer()).thenReturn(renderer);

        final String xmlCfg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<cfg msg=\"012345678905\" orientation=\"90\" render-mode=\"INVALID\">\n" +
            "  <upc-A>\n" +
            "    <height>22mm</height>\n" +
            "    <human-readable>top</human-readable>\n" +
            "  </upc-A>\n" +
            "</cfg>";
        final InputStream stream = new ByteArrayInputStream(xmlCfg.getBytes(StandardCharsets.UTF_8));
        final Document doc = creatDocumentFromStream(stream);

        xmlHandler.handleXML(ctx, doc, "");

        verify(renderer, times(1)).renderXML(
            eq(ctx),
            any(Document.class),
            eq(SVGDOMImplementation.SVG_NAMESPACE_URI)
        );
    }

    private Document creatDocumentFromStream(final InputStream inputStream) throws SAXException, ParserConfigurationException, IOException {
        return DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(inputStream);
    }

}
