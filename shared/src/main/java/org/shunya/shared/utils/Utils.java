package org.shunya.shared.utils;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import java.util.function.Supplier;

public class Utils {
    private static Random rand = new Random();
    public static void info(Logger logger, Supplier<String> message) {
        if (logger.isInfoEnabled())
            logger.info(message.get());
    }

    public static void debug(Logger logger, Supplier<String> message) {
        if (logger.isDebugEnabled())
            logger.info(message.get());
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
