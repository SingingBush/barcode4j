package org.krysalis.barcode4j.configuration;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.ConfigurationException
 */
public class ConfigurationException extends Exception {

    private final Configuration m_config;

    public ConfigurationException(Configuration config) {
        this("Bad configuration: " + config.toString(), config);
    }

    public ConfigurationException(String message) {
        this(message, (Configuration) null);
    }

    public ConfigurationException(String message, Throwable throwable) {
        this(message, (Configuration) null, throwable);
    }

    public ConfigurationException(String message, Configuration config) {
        this(message, config, (Throwable) null);
    }

    public ConfigurationException(String message, Configuration config, Throwable throwable) {
        super(message, throwable);
        this.m_config = config;
    }

    @SuppressWarnings("unused")
    public Configuration getOffendingConfiguration() {
        return this.m_config;
    }

    @Override
    public String getMessage() {
        StringBuffer message = new StringBuffer(super.getMessage());
        if (null != this.m_config) {
            message.append(" @");
            message.append(this.m_config.getLocation());
        }

        return message.toString();
    }
}
