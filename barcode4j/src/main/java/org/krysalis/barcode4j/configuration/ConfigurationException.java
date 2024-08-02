package org.krysalis.barcode4j.configuration;

import org.jetbrains.annotations.Nullable;

/**
 * This class is essentially a copy of org.apache.avalon.framework.configuration.ConfigurationException
 * with some of the constructors removed
 */
public class ConfigurationException extends Exception {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, @Nullable Throwable throwable) {
        super(message, throwable);
    }

}
