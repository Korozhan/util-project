package com.korozhan.app.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.Properties;

/**
 * fetches properties
 * Created by veronika.korozhan on 27.01.2017.
 */
public class ParamsManager {
    private final static Logger LOGGER = Logger.getLogger(ParamsManager.class.getName());

    private static Properties appParams = null;

    static {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            appParams = loadResource("application.properties", loader);
            LOGGER.debug("Application properties successfully loaded");
            PropertyConfigurator.configure(loader.getResource("log4j.properties"));
            LOGGER.debug("Log4j properties successfully loaded");
        } catch (IOException e) {
            LOGGER.error("Params Manager cannot load property data", e);
        }
    }

    private static Properties loadResource(String resourceName, ClassLoader loader) throws IOException {
        InputStream resourceStream = loader.getResourceAsStream(resourceName);
        Properties properties = new Properties();
        properties.load(resourceStream);
        return properties;
    }
    public static String getProperty(String property) {
        if (appParams == null) throw new IllegalStateException("Params Manager not configured: strings not defined");
        return appParams.getProperty(property);
    }

    public static boolean getBoolProperty(String property) {
        if (appParams == null) throw new IllegalStateException("Params Manager not configured: strings not defined");
        return "1".equals(appParams.getProperty(property));
    }

    public static Long getLongProperty(String property, Long defaultValue) {
        if (appParams == null) throw new IllegalStateException("Params Manager not configured: strings not defined");
        try {
            return Long.valueOf(appParams.getProperty(property));
        } catch (NumberFormatException e) {
            LOGGER.error("Params Manager cannot parse long parameter", e);
        }
        return defaultValue;
    }
}
