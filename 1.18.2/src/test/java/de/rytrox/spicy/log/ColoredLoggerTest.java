package de.rytrox.spicy.log;

import be.seeseemelk.mockbukkit.MockBukkit;

import de.rytrox.spicy.SpicyTestPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

public class ColoredLoggerTest {

    private static SpicyTestPlugin plugin;
    private static LogLogger logLogger;

    @BeforeAll
    public static void setup() {
        MockBukkit.mock();

        logLogger = new LogLogger();
        plugin = MockBukkit.load(SpicyTestPlugin.class);
        plugin.getLogger().addHandler(logLogger);
    }

    @Test
    public void printWithoutColorCode() {
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        assertEquals("[Spicy] &1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
    }

    @Test
    public void enableColorCodeHandler() {
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        ColoredLogger.enableColoredLogging('&', plugin.getLogger(), "&8[&6Spicy&8]");
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &5Test &7mit {0}", "&6Parameter");

        assertEquals("[Spicy] &1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
        assertNotEquals("&1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
        assertEquals("[Spicy] &1Das &2ist &3ein &5Test &7mit &6Parameter", logLogger.messages.remove(0));
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();

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
