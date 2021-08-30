package com.kms.example.aw_ios.utils;

import com.katalon.utils.Logger;

public class ConsoleLogger implements Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Logger.class.getName());

    public static Logger instance;

    private ConsoleLogger() {
        // Hide the default constructor
    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new ConsoleLogger();
        }
        return instance;
    }

    @Override
    public void info(String message) {
        logger.info(message + "\n");
    }

    public static void logInfo(String message) {
        getInstance().info(message);
    }
}
