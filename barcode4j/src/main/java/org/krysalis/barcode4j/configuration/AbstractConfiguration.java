package org.krysalis.barcode4j.configuration;

import org.jetbrains.annotations.Nullable;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.AbstractConfiguration
 *
 * This is an abstract <code>Configuration</code> implementation that deals
 * with methods that can be abstracted away from underlying implementations.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: AbstractConfiguration.java 506231 2007-02-12 02:36:54Z crossley $
 */
public abstract class AbstractConfiguration implements Configuration {

    /**
     * Returns the prefix of the namespace.  This is only used as a serialization
     * hint, therefore is not part of the client API.  It should be included in
     * all Configuration implementations though.
     *
     * @return A non-null String (defaults to "")
     * @throws ConfigurationException if no prefix was defined (prefix is <code>null</code>).
     * @since 4.1
     */
    protected abstract String getPrefix() throws ConfigurationException;

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public int getValueAsInteger() throws ConfigurationException {
        final String value = getValue().trim();
        try {
            if (value.startsWith("0x")) {
                return Integer.parseInt(value.substring(2), 16);
            } else if (value.startsWith("0o")) {
                return Integer.parseInt(value.substring(2), 8);
            } else if (value.startsWith("0b")) {
                return Integer.parseInt(value.substring(2), 2);
            } else {
                return Integer.parseInt(value);
            }
        } catch (final NumberFormatException e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as an integer in the configuration element \"%s\" at %s", value, getName(), getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the configuration element as an <code>int</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public int getValueAsInteger(final int defaultValue) {
        try {
            return getValueAsInteger();
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public long getValueAsLong() throws ConfigurationException {
        final String value = getValue().trim();

        try {
            if (value.startsWith("0x")) {
                return Long.parseLong(value.substring(2), 16);
            } else if (value.startsWith("0o")) {
                return Long.parseLong(value.substring(2), 8);
            } else if (value.startsWith("0b")) {
                return Long.parseLong(value.substring(2), 2);
            } else {
                return Long.parseLong(value);
            }
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as a long in the configuration element \"%s\" at %s", value, getName(), getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>long</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public long getValueAsLong(final long defaultValue) {
        try {
            return getValueAsLong();
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     *
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public float getValueAsFloat()
        throws ConfigurationException {
        final String value = getValue().trim();
        try {
            return Float.parseFloat(value);
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as a float in the configuration element \"%s\" at %s", value, getName(), getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>float</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public float getValueAsFloat(final float defaultValue) {
        try {
            return getValueAsFloat();
        } catch (final ConfigurationException ce) {
            return (defaultValue);
        }
    }

    /**
     * Returns the value of the configuration element as a <code>double</code>.
     *
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public double getValueAsDouble()
        throws ConfigurationException {
        final String value = getValue().trim();
        try {
            return Double.parseDouble(value);
        } catch (final Exception nfe) {
            final String message = String.format("Cannot parse the value \"%s\" as a double in the configuration element \"%s\" at %s", value, getName(), getLocation());
            throw new ConfigurationException(message);
        }
    }

    /**
     * Returns the value of the configuration element as a <code>double</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public double getValueAsDouble(final double defaultValue) {
        try {
            return getValueAsDouble();
        } catch (final ConfigurationException ce) {
            return (defaultValue);
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     *
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public boolean getValueAsBoolean()
        throws ConfigurationException {
        final String value = getValue().trim();

        if (isTrue(value)) {
            return true;
        } else if (isFalse(value)) {
            return false;
        } else {
            throw new ConfigurationException(
                String.format(
                    "Cannot parse the value \"%s\" as a boolean in the configuration element \"%s\" at %s",
                    value,
                    getName(),
                    getLocation()
                )
            );
        }
    }

    /**
     * Returns the value of the configuration element as a <code>boolean</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public boolean getValueAsBoolean(final boolean defaultValue) {
        try {
            return getValueAsBoolean();
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the configuration element as a <code>String</code>.
     *
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public String getValue(final String defaultValue) {
        try {
            return getValue();
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public int getAttributeAsInteger(final String name)
        throws ConfigurationException {
        final String value = getAttribute(name).trim();
        try {
            if (value.startsWith("0x")) {
                return Integer.parseInt(value.substring(2), 16);
            } else if (value.startsWith("0o")) {
                return Integer.parseInt(value.substring(2), 8);
            } else if (value.startsWith("0b")) {
                return Integer.parseInt(value.substring(2), 2);
            } else {
                return Integer.parseInt(value);
            }
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as an integer in the attribute \"%s\" at %s", value, name, getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as an
     * <code>int</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public int getAttributeAsInteger(final String name, final int defaultValue) {
        try {
            return getAttributeAsInteger(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name the name of the attribute
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public long getAttributeAsLong(final String name)
        throws ConfigurationException {
        final String value = getAttribute(name);

        try {
            if (value.startsWith("0x")) {
                return Long.parseLong(value.substring(2), 16);
            } else if (value.startsWith("0o")) {
                return Long.parseLong(value.substring(2), 8);
            } else if (value.startsWith("0b")) {
                return Long.parseLong(value.substring(2), 2);
            } else {
                return Long.parseLong(value);
            }
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as a long in the attribute \"%s\" at %s", value, name, getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>long</code>.
     * <p>
     * Hexadecimal numbers begin with 0x, Octal numbers begin with 0o and binary
     * numbers begin with 0b, all other values are assumed to be decimal.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public long getAttributeAsLong(final String name, final long defaultValue) {
        try {
            return getAttributeAsLong(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     *
     * @param name the name of the attribute
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public float getAttributeAsFloat(final String name)
        throws ConfigurationException {
        final String value = getAttribute(name);
        try {
            return Float.parseFloat(value);
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as a float in the attribute \"%s\" at %s", value, name, getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>float</code>.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public float getAttributeAsFloat(final String name, final float defaultValue) {
        try {
            return getAttributeAsFloat(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>double</code>.
     *
     * @param name the name of the attribute
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public double getAttributeAsDouble(final String name) throws ConfigurationException {
        final String value = getAttribute(name);
        try {
            return Double.parseDouble(value);
        } catch (final Exception e) {
            throw new ConfigurationException(
                String.format("Cannot parse the value \"%s\" as a double in the attribute \"%s\" at %s", value, name, getLocation()),
                e
            );
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>double</code>.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public double getAttributeAsDouble(final String name, final double defaultValue) {
        try {
            return getAttributeAsDouble(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     *
     * @param name the name of the attribute
     * @return the value
     * @throws ConfigurationException if an error occurs
     */
    @Override
    public boolean getAttributeAsBoolean(final String name)
        throws ConfigurationException {
        final String value = getAttribute(name);

        if (isTrue(value)) {
            return true;
        } else if (isFalse(value)) {
            return false;
        } else {
            throw new ConfigurationException(
                String.format(
                    "Cannot parse the value \"%s\" as a boolean in the attribute \"%s\" at %s",
                    value,
                    name,
                    getLocation()
                )
            );
        }
    }

    private boolean isTrue(final String value) {
        return value.equalsIgnoreCase("true")
            || value.equalsIgnoreCase("yes")
            || value.equalsIgnoreCase("on")
            || value.equalsIgnoreCase("1");
    }

    private boolean isFalse(final String value) {
        return value.equalsIgnoreCase("false")
            || value.equalsIgnoreCase("no")
            || value.equalsIgnoreCase("off")
            || value.equalsIgnoreCase("0");
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>boolean</code>.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public boolean getAttributeAsBoolean(final String name, final boolean defaultValue) {
        try {
            return getAttributeAsBoolean(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Returns the value of the attribute specified by its name as a
     * <code>String</code>.
     *
     * @param name         the name of the attribute
     * @param defaultValue the default value to return if value malformed or empty
     * @return the value
     */
    @Override
    public String getAttribute(final String name, final String defaultValue) {
        try {
            return getAttribute(name);
        } catch (final ConfigurationException ce) {
            return defaultValue;
        }
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name. If no such child exists, a new one
     * will be created.
     *
     * @param name the name of the child
     * @return the child Configuration
     */
    @Override
    public Configuration getChild(final String name) {
        return getChild(name, true);
    }

    /**
     * Return the first <code>Configuration</code> object child of this
     * associated with the given name.
     *
     * @param name      the name of the child
     * @param createNew true if you want to create a new Configuration object if none exists
     * @return the child Configuration or null
     */
    @Override
    @Nullable
    public Configuration getChild(final String name, final boolean createNew) {
        final Configuration[] children = getChildren(name);
        if (children.length > 0) {
            return children[0];
        } else {
            if (createNew) {
                return new DefaultConfiguration(name, "-");
            } else {
                return null;
            }
        }
    }

    /**
     * The toString() operation is used for debugging information.  It does
     * not create a deep reproduction of this configuration and all child configurations,
     * instead it displays the name, value, and location.
     *
     * @return getName() + "::" + getValue() + ":@" + getLocation();
     */
    @Override
    public String toString() {
        return getName() + "::" + getValue("<no value>") + ":@" + getLocation();
    }
}
