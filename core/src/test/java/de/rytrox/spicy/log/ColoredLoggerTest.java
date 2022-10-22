package de.rytrox.spicy.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class ColoredLoggerTest {

    private LogLogger logLogger;
    private Logger logger;

    @BeforeEach
    public void setup() {
        logger = Logger.getLogger("Test");
        logLogger = new LogLogger();

        logger.addHandler(logLogger);
    }

    @Test
    public void printWithoutColorCode() {
        logger.log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        assertEquals("&1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
    }

    @Test
    public void enableColorCodeHandler() {
        logger.log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        ColoredLogger.enableColoredLogging('&', logger, "&8[&6Spicy&8]");
        logger.log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");
        logger.log(Level.INFO, "&1Das &2ist &3ein &5Test &7mit {0}", "&6Parameter");

        assertEquals("&1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
        assertEquals("&1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
        assertEquals("&1Das &2ist &3ein &5Test &7mit &6Parameter", logLogger.messages.remove(0));
    }
    private static class LogLogger extends ConsoleHandler {

        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            super.publish(record);

            messages.add(MessageFormat.format(record.getMessage(), record.getParameters()));
        }
    }
}
