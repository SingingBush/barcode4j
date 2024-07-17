package org.krysalis.barcode4j.configuration;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.SAXConfigurationHandler
 * <p>
 * A SAXConfigurationHandler helps build Configurations out of sax events.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: SAXConfigurationHandler.java 506231 2007-02-12 02:36:54Z crossley $
 */
public class SAXConfigurationHandler extends DefaultHandler implements ErrorHandler {

    /**
     * Likely number of nested configuration items. If more is
     * encountered the lists will grow automatically.
     */
    private static final int EXPECTED_DEPTH = 5;
    private final ArrayList<Configuration> m_elements = new ArrayList<>(EXPECTED_DEPTH);
    private final ArrayList<StringBuffer> m_values = new ArrayList<>(EXPECTED_DEPTH);

    /**
     * Contains true at index n if space in the configuration with
     * depth n is to be preserved.
     */
    private final BitSet m_preserveSpace = new BitSet();
    private Configuration m_configuration;
    private Locator m_locator;

    /**
     * Get the configuration object that was built.
     *
     * @return a <code>Configuration</code> object
     */
    public Configuration getConfiguration() {
        return m_configuration;
    }

    /**
     * Clears all data from this configuration handler.
     */
    public void clear() {
        m_elements.clear();
        m_values.clear();
        m_locator = null;
    }

    /**
     * Set the document <code>Locator</code> to use.
     *
     * @param locator a <code>Locator</code> value
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        m_locator = locator;
    }

    /**
     * Handling hook for character data.
     *
     * @param ch    a <code>char[]</code> of data
     * @param start offset in the character array from which to start reading
     * @param end   length of character data
     * @throws SAXException if an error occurs
     */
    @Override
    public void characters(final char[] ch, int start, int end)
        throws SAXException {
        // it is possible to play micro-optimization here by doing
        // manual trimming and thus preserve some precious bits
        // of memory, but it's really not important enough to justify
        // resulting code complexity
        final int depth = m_values.size() - 1;
        final StringBuffer valueBuffer = m_values.get(depth);
        valueBuffer.append(ch, start, end);
    }

    /**
     * Handling hook for finishing parsing of an element.
     *
     * @param namespaceURI a <code>String</code> value
     * @param localName    a <code>String</code> value
     * @param rawName      a <code>String</code> value
     * @throws SAXException if an error occurs
     */
    @Override
    public void endElement(final String namespaceURI,
                           final String localName,
                           final String rawName) throws SAXException {
        final int depth = m_elements.size() - 1;
        final DefaultConfiguration finishedConfiguration = (DefaultConfiguration) m_elements.remove(depth);
        final String accumulatedValue = (m_values.remove(depth)).toString();

        if (finishedConfiguration.getChildren().length == 0) {
            // leaf node
            String finishedValue;
            if (m_preserveSpace.get(depth)) {
                finishedValue = accumulatedValue;
            } else if (0 == accumulatedValue.length()) {
                finishedValue = null;
            } else {
                finishedValue = accumulatedValue.trim();
            }
            finishedConfiguration.setValue(finishedValue);
        } else {
            final String trimmedValue = accumulatedValue.trim();
            if (!trimmedValue.isEmpty()) {
                throw new SAXException("Not allowed to define mixed content in the "
                    + "element " + finishedConfiguration.getName() + " at "
                    + finishedConfiguration.getLocation());
            }
        }

        if (0 == depth) {
            m_configuration = finishedConfiguration;
        }
    }

    /**
     * Create a new <code>DefaultConfiguration</code> with the specified
     * local name and location.
     *
     * @param localName a <code>String</code> value
     * @param location  a <code>String</code> value
     * @return a <code>DefaultConfiguration</code> value
     */
    protected DefaultConfiguration createConfiguration(final String localName, final String location) {
        return new DefaultConfiguration(localName, location);
    }

    /**
     * Handling hook for starting parsing of an element.
     *
     * @param namespaceURI a <code>String</code> value
     * @param localName    a <code>String</code> value
     * @param rawName      a <code>String</code> value
     * @param attributes   an <code>Attributes</code> value
     * @throws SAXException if an error occurs
     */
    @Override
    public void startElement(final String namespaceURI,
                             final String localName,
                             final String rawName,
                             final Attributes attributes) throws SAXException {
        final DefaultConfiguration configuration = createConfiguration(rawName, getLocationString());
        // depth of new configuration (not decrementing here, configuration
        // is to be added)
        final int depth = m_elements.size();
        boolean preserveSpace = false; // top level element trims space by default

        if (depth > 0) {
            final DefaultConfiguration parent = (DefaultConfiguration) m_elements.get(depth - 1);
            parent.addChild(configuration);
            // inherits parent's space preservation policy
            preserveSpace = m_preserveSpace.get(depth - 1);
        }

        m_elements.add(configuration);
        m_values.add(new StringBuffer());

        final int attributesSize = attributes.getLength();

        for (int i = 0; i < attributesSize; i++) {
            final String name = attributes.getQName(i);
            final String value = attributes.getValue(i);

            if (!name.equals("xml:space")) {
                configuration.setAttribute(name, value);
            } else {
                preserveSpace = value.equals("preserve");
            }
        }

        if (preserveSpace) {
            m_preserveSpace.set(depth);
        } else {
            m_preserveSpace.clear(depth);
        }
    }

    /**
     * This just throws an exception on a parse error.
     *
     * @param exception the parse error
     * @throws SAXException if an error occurs
     */
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        // todo: log the error using slf4j
        throw exception;
    }

    /**
     * This just throws an exception on a parse error.
     *
     * @param exception the parse error
     * @throws SAXException if an error occurs
     */
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        // todo: log the warning using slf4j
        throw exception;
    }

    /**
     * This just throws an exception on a parse error.
     *
     * @param exception the parse error
     * @throws SAXException if an error occurs
     */
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        // todo: log the error using slf4j
        throw exception;
    }

    /**
     * Returns a string showing the current system ID, line number and column number.
     *
     * @return a <code>String</code> value
     */
    protected String getLocationString() {
        if (null == m_locator) {
            return "Unknown";
        } else {
            final int columnNumber = m_locator.getColumnNumber();
            return
                m_locator.getSystemId() + ":"
                    + m_locator.getLineNumber()
                    + (columnNumber >= 0 ? (":" + columnNumber) : "");
        }
    }
}
