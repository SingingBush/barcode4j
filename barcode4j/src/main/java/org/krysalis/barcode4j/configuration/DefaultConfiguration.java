package org.krysalis.barcode4j.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.DefaultConfiguration
 */
public class DefaultConfiguration extends AbstractConfiguration implements MutableConfiguration, Serializable {

    private final String m_name;
    private final String m_location;
    private final String m_namespace;
    private final String m_prefix;
    private @Nullable HashMap<String, String> m_attributes;
    private @Nullable ArrayList<Configuration> m_children;
    private @Nullable String m_value;
    private boolean m_readOnly;

    public DefaultConfiguration(@NotNull String name) {
        this(name, null, "", "");
    }

    public DefaultConfiguration(@NotNull String name, @Nullable String location) {
        this(name, location, "", "");
    }

    public DefaultConfiguration(@NotNull String name, @Nullable String location, @NotNull String ns, @NotNull String prefix) {
        this.m_name = name;
        this.m_location = location;
        this.m_namespace = ns;
        this.m_prefix = prefix;
    }

    @Override
    public @NotNull String getName() {
        return this.m_name;
    }

    @Override
    public @NotNull String getNamespace() throws ConfigurationException {
        return Optional.of(m_namespace)
            .orElseThrow(() -> new ConfigurationException("No namespace (not even default \"\") is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation()));
    }

    @Override
    protected String getPrefix() throws ConfigurationException {
        return Optional.of(m_prefix)
            .orElseThrow(() -> new ConfigurationException("No prefix (not even default \"\") is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation()));
    }

    @Override
    public String getLocation() {
        return this.m_location;
    }

    @Override
    public String getValue(String defaultValue) {
        return null != this.m_value ? this.m_value : defaultValue;
    }

    @Override
    public @NotNull String getValue() throws ConfigurationException {
        return Optional.ofNullable(m_value)
            .orElseThrow(() -> new ConfigurationException("No value is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation()));
    }

    @Override
    public String[] getAttributeNames() {
        return null == this.m_attributes ? new String[0] : this.m_attributes.keySet().toArray(new String[0]);
    }

    @Override
    public Configuration[] getChildren() {
        return null == this.m_children ? new Configuration[0] : this.m_children.toArray(new Configuration[0]);
    }

    @Override
    public @NotNull String getAttribute(@NotNull String name) throws ConfigurationException {
        final String value = null != this.m_attributes ? this.m_attributes.get(name) : null;

        return Optional.ofNullable(value)
            .orElseThrow(() -> new ConfigurationException("No attribute named \"" + name + "\" is " + "associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation()));
    }

    @Override
    @Nullable
    public Configuration getChild(@NotNull String name, boolean createNew) {
        if (null != this.m_children) {

            for (final Configuration config : this.m_children) {
                if (name.equals(config.getName())) {
                    return config;
                }
            }
        }

        return createNew ? new DefaultConfiguration(name, "<generated>" + this.getLocation(), this.m_namespace, this.m_prefix) : null;
    }

    @Override
    public Configuration[] getChildren(@NotNull String name) {
        if (null == this.m_children) {
            return new Configuration[0];
        } else {
            final List<Configuration> children = new ArrayList<>();

            for (final Configuration config : this.m_children) {
                if (name.equals(config.getName())) {
                    children.add(config);
                }
            }

            return children.toArray(new Configuration[0]);
        }
    }

    @Override
    public void setValue(String value) {
        this.checkWriteable();
        this.m_value = value;
    }

    @Override
    public void setValue(int value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setValue(long value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setValue(boolean value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setValue(float value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setValue(double value) {
        this.setValue(String.valueOf(value));
    }

    @Override
    public void setAttribute(String name, String value) {
        this.checkWriteable();
        if (null != value) {
            if (null == this.m_attributes) {
                this.m_attributes = new HashMap<>();
            }

            this.m_attributes.put(name, value);
        } else if (null != this.m_attributes) {
            this.m_attributes.remove(name);
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public String addAttribute(String name, String value) {
        this.checkWriteable();
        if (null == this.m_attributes) {
            this.m_attributes = new HashMap<>();
        }

        return this.m_attributes.put(name, value);
    }

    @Override
    public void addChild(Configuration configuration) {
        this.checkWriteable();
        if (null == this.m_children) {
            this.m_children = new ArrayList<>();
        }

        this.m_children.add(configuration);
    }

    @Override
    public void addAll(Configuration other) {
        this.checkWriteable();
        this.setValue(other.getValue(null));
        this.addAllAttributes(other);
        this.addAllChildren(other);
    }

    @Override
    public void addAllAttributes(Configuration other) {
        this.checkWriteable();

        Arrays.stream(other.getAttributeNames())
            .forEach(name -> this.setAttribute(name, other.getAttribute(name, null)));
    }

    @Override
    public void addAllChildren(Configuration other) {
        this.checkWriteable();

        Arrays.stream(other.getChildren())
            .forEach(this::addChild);
    }

    @Override
    public void removeChild(Configuration configuration) {
        this.checkWriteable();
        if (null != this.m_children) {
            this.m_children.remove(configuration);
        }
    }

    public int getChildCount() {
        return null == this.m_children ? 0 : this.m_children.size();
    }

    public void makeReadOnly() {
        this.m_readOnly = true;
    }

    protected final void checkWriteable() throws IllegalStateException {
        if (this.m_readOnly) {
            throw new IllegalStateException("Configuration is read only and can not be modified");
        }
    }

    protected final boolean isReadOnly() {
        return this.m_readOnly;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (!(other instanceof DefaultConfiguration)) {
            return false;
        } else {
            DefaultConfiguration c = (DefaultConfiguration) other;
            if (this.m_readOnly ^ c.m_readOnly) {
                return false;
            } else if (this.check(this.m_name, c.m_name)) {
                return false;
            } else if (this.check(this.m_location, c.m_location)) {
                return false;
            } else if (this.check(this.m_namespace, c.m_namespace)) {
                return false;
            } else if (this.check(this.m_prefix, c.m_prefix)) {
                return false;
            } else if (this.check(this.m_value, c.m_value)) {
                return false;
            } else if (this.check(this.m_attributes, c.m_attributes)) {
                return false;
            } else {
                return !this.check(this.m_children, c.m_children);
            }
        }
    }

    private boolean check(Object one, Object two) {
        if (one == null) {
            return two != null;
        } else {
            return !one.equals(two);
        }
    }

    public int hashCode() {
        int hash = this.m_prefix.hashCode();
        if (this.m_name != null) {
            hash ^= this.m_name.hashCode();
        }

        hash >>>= 7;
        if (this.m_location != null) {
            hash ^= this.m_location.hashCode();
        }

        hash >>>= 7;
        if (this.m_namespace != null) {
            hash ^= this.m_namespace.hashCode();
        }

        hash >>>= 7;
        if (this.m_attributes != null) {
            hash ^= this.m_attributes.hashCode();
        }

        hash >>>= 7;
        if (this.m_children != null) {
            hash ^= this.m_children.hashCode();
        }

        hash >>>= 7;
        if (this.m_value != null) {
            hash ^= this.m_value.hashCode();
        }

        hash >>>= this.m_readOnly ? 7 : 13;
        return hash;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultConfiguration.class.getSimpleName() + "[", "]")
            .add("m_name='" + m_name + "'")
            .add("m_location='" + m_location + "'")
            .add("m_namespace='" + m_namespace + "'")
            .add("m_prefix='" + m_prefix + "'")
            .add("m_attributes=" + m_attributes)
            .add("m_children=" + m_children)
            .add("m_value='" + m_value + "'")
            .add("m_readOnly=" + m_readOnly)
            .toString();
    }
}
