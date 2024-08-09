package org.krysalis.barcode4j.configuration;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.DefaultConfigurationBuilder with some code removed
 * namespaced xml is not supported
 * <p>
 * Please note that this class should not be used. It's only used internal by barcode4j-ant and barcode4j-cli which are not published to maven central. This class will be removed
 * </p>
 */
@Deprecated
public class DefaultConfigurationBuilder {

    private final SAXConfigurationHandler m_handler;

    private final XMLReader m_parser;


    /**
     * Create a Configuration Builder, specifying a flag that determines namespace support.
     * <p>
     * The default JAXP <code>SAXParser</code> is used.
     * </p>
     */
    public DefaultConfigurationBuilder() {
        try {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            final SAXParser saxParser = saxParserFactory.newSAXParser();
            this.m_parser = saxParser.getXMLReader();

            this.m_handler = new SAXConfigurationHandler();

            this.m_parser.setContentHandler(m_handler);
            this.m_parser.setErrorHandler(m_handler);
        } catch (final Exception se) {
            throw new Error("Unable to setup SAX parser" + se);
        }
    }

    /**
     * Build a configuration object from a file using a File object.
     *
     * @param file a <code>File</code> object
     * @return a <code>Configuration</code> object
     * @throws SAXException           if a parsing error occurs
     * @throws IOException            if an I/O error occurs
     * @throws ConfigurationException if an error occurs
     */
    public Configuration buildFromFile(final File file) throws SAXException, IOException, ConfigurationException {
        synchronized (this) {
            m_handler.clear();
            m_parser.parse(file.toURI().toURL().toString());
            return m_handler.getConfiguration();
        }
    }
}
