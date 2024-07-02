package org.krysalis.barcode4j.configuration;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.MutableConfiguration
 */
public interface MutableConfiguration extends Configuration {

    /**
     * Set the value of this <code>Configuration</code> object to the specified string.
     *
     * @param value a <code>String</code> value
     */
    void setValue(final String value);

    /**
     * Set the value of this <code>Configuration</code> object to the specified int.
     *
     * @param value a <code>int</code> value
     */
    void setValue(final int value);

    /**
     * Set the value of this <code>Configuration</code> object to the specified long.
     *
     * @param value a <code>long</code> value
     */
    void setValue(final long value);

    /**
     * Set the value of this <code>Configuration</code> object to the specified boolean.
     *
     * @param value a <code>boolean</code> value
     */
    void setValue(final boolean value);

    /**
     * Set the value of this <code>Configuration</code> object to the specified float.
     *
     * @param value a <code>float</code> value
     */
    void setValue(final float value);

    /**
     * Set the value of this <code>Configuration</code> object to the specified double.
     *
     * @param value a <code>double</code> value
     */
    void setValue(final double value);

    /**
     * Set the value of the specified attribute to the specified string.
     *
     * @param name  name of the attribute to set
     * @param value a <code>String</code> value. If null, the attribute is removed.
     */
    void setAttribute(final String name, final String value);

//    /**
//     * Set the value of the specified attribute to the specified int.
//     *
//     * @param name name of the attribute to set
//     * @param value an <code>int</code> value
//     */
//    void setAttribute( final String name, final int value );
//
//    /**
//     * Set the value of the specified attribute to the specified long.
//     *
//     * @param name name of the attribute to set
//     * @param value an <code>long</code> value
//     */
//    void setAttribute( final String name, final long value );
//
//    /**
//     * Set the value of the specified attribute to the specified boolean.
//     *
//     * @param name name of the attribute to set
//     * @param value an <code>boolean</code> value
//     */
//    void setAttribute( final String name, final boolean value );
//
//    /**
//     * Set the value of the specified attribute to the specified float.
//     *
//     * @param name name of the attribute to set
//     * @param value an <code>float</code> value
//     */
//    void setAttribute( final String name, final float value );
//
//    /**
//     * Set the value of the specified attribute to the specified double.
//     *
//     * @param name name of the attribute to set
//     * @param value an <code>double</code> value
//     */
//    void setAttribute( final String name, final double value );

    /**
     * Add a child <code>Configuration</code> to this configuration element.
     *
     * @param configuration a <code>Configuration</code> value
     */
    void addChild(final Configuration configuration);

    /**
     * Add all the attributes, children and value
     * from specified configuration element to current
     * configuration element.
     *
     * @param other the {@link Configuration} element
     */
    void addAll(final Configuration other);

    /**
     * Add all attributes from specified configuration
     * element to current configuration element.
     *
     * @param other the {@link Configuration} element
     */
    void addAllAttributes(final Configuration other);

    /**
     * Add all child <code>Configuration</code> objects from specified
     * configuration element to current configuration element.
     *
     * @param other the other {@link Configuration} value
     */
    void addAllChildren(final Configuration other);

    /**
     * Remove a child <code>Configuration</code> to this configuration element.
     *
     * @param configuration a <code>Configuration</code> value
     */
    void removeChild(final Configuration configuration);

//    /**
//     * Equivalent to <code>getMutableChild( name, true )</code>
//     */
//    MutableConfiguration getMutableChild(final String name ) throws ConfigurationException;

//    /**
//     * Gets a child node of this configuration. If a mutable child with the
//     * given name exists, it is returned. If an immutable child with the
//     * given name exists, it is converted into a mutable child and returned.
//     * In this case, the immutable child will be replaced with the mutable
//     * child in this configuration (that is, it will be as if the child
//     * node always had been mutable).
//     * If no child with the given name exists, and <code>autoCreate</code>
//     * is <code>true</code>, a new mutable child is created and added to
//     * this configuration before being returned.
//     *
//     * @return the child MutableConfiguration, or <code>null</code> if <code>autoCreate</code>
//     *          was false and no child by the given name existed.
//     * @param name the name of the child.
//     * @param autoCreate set to true to create the child node if it doesn't exist.
//     * @throws ConfigurationException if an error occurrs.
//     */
//    MutableConfiguration getMutableChild(final String name, boolean autoCreate ) throws ConfigurationException;

//    /**
//     * Returns an array of mutable children. Immutable children
//     * are converted just as for <code>getMutableChild</code>.
//     * @throws ConfigurationException if an error occurrs.
//     */
//    public MutableConfiguration[] getMutableChildren() throws ConfigurationException;
//
//    /**
//     * Returns an array of mutable children with the given name. Immutable children
//     * are converted just as for <code>getMutableChild</code>.
//     * @throws ConfigurationException if an error occurrs.
//     */
//    public MutableConfiguration[] getMutableChildren(final String name ) throws ConfigurationException;
}
