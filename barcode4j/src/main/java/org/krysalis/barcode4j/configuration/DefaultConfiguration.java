package org.krysalis.barcode4j.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.DefaultConfiguration
 */
public class DefaultConfiguration extends AbstractConfiguration implements MutableConfiguration, Serializable {

    protected static final Configuration[] EMPTY_ARRAY = new Configuration[0];
    private final String m_name;
    private final String m_location;
    private final String m_namespace;
    private final String m_prefix;
    private HashMap<String, String> m_attributes;
    private ArrayList<Configuration> m_children;
    private String m_value;
    private boolean m_readOnly;

//    public DefaultConfiguration(Configuration config, boolean deepCopy) throws ConfigurationException {
//        this(config.getName(), config.getLocation(), config.getNamespace(), config instanceof AbstractConfiguration ? ((AbstractConfiguration)config).getPrefix() : "");
//        this.addAll(config, deepCopy);
//    }

//    public DefaultConfiguration(Configuration config) throws ConfigurationException {
//        this(config, false);
//    }

    public DefaultConfiguration(String name) {
        this(name, (String) null, "", "");
    }

    public DefaultConfiguration(String name, String location) {
        this(name, location, "", "");
    }

    public DefaultConfiguration(String name, String location, String ns, String prefix) {
        this.m_name = name;
        this.m_location = location;
        this.m_namespace = ns;
        this.m_prefix = prefix;
    }

    @Override
    public String getName() {
        return this.m_name;
    }

    @Override
    public String getNamespace() throws ConfigurationException {
        if (null != this.m_namespace) {
            return this.m_namespace;
        } else {
            throw new ConfigurationException("No namespace (not even default \"\") is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation());
        }
    }

    @Override
    protected String getPrefix() throws ConfigurationException {
        if (null != this.m_prefix) {
            return this.m_prefix;
        } else {
            throw new ConfigurationException("No prefix (not even default \"\") is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation());
        }
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
    public String getValue() throws ConfigurationException {
        if (null != this.m_value) {
            return this.m_value;
        } else {
            throw new ConfigurationException("No value is associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation());
        }
    }

    @Override
    public String[] getAttributeNames() {
        return null == this.m_attributes ? new String[0] : (String[]) this.m_attributes.keySet().toArray(new String[0]);
    }

    @Override
    public Configuration[] getChildren() {
        return null == this.m_children ? new Configuration[0] : (Configuration[]) this.m_children.toArray(new Configuration[0]);
    }

    @Override
    public String getAttribute(String name) throws ConfigurationException {
        String value = null != this.m_attributes ? (String) this.m_attributes.get(name) : null;
        if (null != value) {
            return value;
        } else {
            throw new ConfigurationException("No attribute named \"" + name + "\" is " + "associated with the configuration element \"" + this.getName() + "\" at " + this.getLocation());
        }
    }

    @Override
    public Configuration getChild(String name, boolean createNew) {
        if (null != this.m_children) {
            int size = this.m_children.size();

            for (int i = 0; i < size; ++i) {
                Configuration configuration = (Configuration) this.m_children.get(i);
                if (name.equals(configuration.getName())) {
                    return configuration;
                }
            }
        }

        return createNew ? new DefaultConfiguration(name, "<generated>" + this.getLocation(), this.m_namespace, this.m_prefix) : null;
    }

    @Override
    public Configuration[] getChildren(String name) {
        if (null == this.m_children) {
            return new Configuration[0];
        } else {
            ArrayList children = new ArrayList();
            int size = this.m_children.size();

            for (int i = 0; i < size; ++i) {
                Configuration configuration = (Configuration) this.m_children.get(i);
                if (name.equals(configuration.getName())) {
                    children.add(configuration);
                }
            }

            return (Configuration[]) children.toArray(new Configuration[0]);
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void appendValueData(String value) {
        this.checkWriteable();
        if (null == this.m_value) {
            this.m_value = value;
        } else {
            this.m_value = this.m_value + value;
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
                this.m_attributes = new HashMap();
            }

            this.m_attributes.put(name, value);
        } else if (null != this.m_attributes) {
            this.m_attributes.remove(name);
        }

    }

//    @Override
//    public void setAttribute(String name, int value) {
//        this.setAttribute(name, String.valueOf(value));
//    }
//
//    @Override
//    public void setAttribute(String name, long value) {
//        this.setAttribute(name, String.valueOf(value));
//    }
//
//    @Override
//    public void setAttribute(String name, boolean value) {
//        this.setAttribute(name, String.valueOf(value));
//    }
//
//    @Override
//    public void setAttribute(String name, float value) {
//        this.setAttribute(name, String.valueOf(value));
//    }
//
//    @Override
//    public void setAttribute(String name, double value) {
//        this.setAttribute(name, String.valueOf(value));
//    }

    /**
     * @deprecated
     */
    @Deprecated
    public String addAttribute(String name, String value) {
        this.checkWriteable();
        if (null == this.m_attributes) {
            this.m_attributes = new HashMap<>();
        }

        return (String) this.m_attributes.put(name, value);
    }

    @Override
    public void addChild(Configuration configuration) {
        this.checkWriteable();
        if (null == this.m_children) {
            this.m_children = new ArrayList<>();
        }

        this.m_children.add(configuration);
    }

//    public void addAll(Configuration other, boolean deepCopy) throws ConfigurationException {
//        this.checkWriteable();
//        this.setValue(other.getValue((String)null));
//        this.addAllAttributes(other);
//        this.addAllChildren(other, deepCopy);
//    }

    @Override
    public void addAll(Configuration other) {
        this.checkWriteable();
        this.setValue(other.getValue((String) null));
        this.addAllAttributes(other);
        this.addAllChildren(other);
    }

    @Override
    public void addAllAttributes(Configuration other) {
        this.checkWriteable();
        String[] attributes = other.getAttributeNames();

        for (int i = 0; i < attributes.length; ++i) {
            String name = attributes[i];
            String value = other.getAttribute(name, (String) null);
            this.setAttribute(name, value);
        }

    }

//    public void addAllChildren(Configuration other, boolean deepCopy) throws ConfigurationException {
//        this.checkWriteable();
//        Configuration[] children = other.getChildren();
//
//        for(int i = 0; i < children.length; ++i) {
//            if (deepCopy) {
//                this.addChild(new DefaultConfiguration(children[i], true));
//            } else {
//                this.addChild(children[i]);
//            }
//        }
//    }

    @Override
    public void addAllChildren(Configuration other) {
        this.checkWriteable();
        Configuration[] children = other.getChildren();

        for (int i = 0; i < children.length; ++i) {
            this.addChild(children[i]);
        }

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

//    private MutableConfiguration toMutable(Configuration child) throws ConfigurationException {
//        if (!(child instanceof MutableConfiguration) || child instanceof DefaultConfiguration && ((DefaultConfiguration)child).isReadOnly()) {
//            this.checkWriteable();
//            DefaultConfiguration config = new DefaultConfiguration(child);
//
//            for(int i = 0; i < this.m_children.size(); ++i) {
//                if (this.m_children.get(i) == child) {
//                    this.m_children.set(i, config);
//                    break;
//                }
//            }
//
//            return config;
//        } else {
//            return (MutableConfiguration)child;
//        }
//    }

//    public MutableConfiguration getMutableChild(String name) throws ConfigurationException {
//        return this.getMutableChild(name, true);
//    }

//    public MutableConfiguration getMutableChild(String name, boolean autoCreate) throws ConfigurationException {
//        Configuration child = this.getChild(name, false);
//        if (child == null) {
//            if (autoCreate) {
//                DefaultConfiguration config = new DefaultConfiguration(name, "-");
//                this.addChild(config);
//                return config;
//            } else {
//                return null;
//            }
//        } else {
//            return this.toMutable(child);
//        }
//    }

//    public MutableConfiguration[] getMutableChildren() throws ConfigurationException {
//        if (null == this.m_children) {
//            return new MutableConfiguration[0];
//        } else {
//            ArrayList children = new ArrayList();
//            int size = this.m_children.size();
//
//            for(int i = 0; i < size; ++i) {
//                Configuration configuration = (Configuration)this.m_children.get(i);
//                children.add(this.toMutable(configuration));
//            }
//
//            return (MutableConfiguration[])children.toArray(new MutableConfiguration[0]);
//        }
//    }
//
//    public MutableConfiguration[] getMutableChildren(String name) throws ConfigurationException {
//        if (null == this.m_children) {
//            return new MutableConfiguration[0];
//        } else {
//            ArrayList children = new ArrayList();
//            int size = this.m_children.size();
//
//            for(int i = 0; i < size; ++i) {
//                Configuration configuration = (Configuration)this.m_children.get(i);
//                if (name.equals(configuration.getName())) {
//                    children.add(this.toMutable(configuration));
//                }
//            }
//
//            return (MutableConfiguration[])children.toArray(new MutableConfiguration[0]);
//        }
//    }

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

}
