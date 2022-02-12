package de.rytrox.spicy.log;

import be.seeseemelk.mockbukkit.MockBukkit;
import de.timeout.libs.LibsTestPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.*;

public class ColoredLoggerTest {

    private LibsTestPlugin plugin;
    private LogLogger logLogger;

    @Before
    public void setup() {
        MockBukkit.mock();

        logLogger = new LogLogger();
        plugin = MockBukkit.load(LibsTestPlugin.class);
        plugin.getLogger().addHandler(logLogger);
    }

    @Test
    public void printWithoutColorCode() {
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        assertEquals("[Libs] &1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
    }

    @Test
    public void enableColorCodeHandler() {
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        ColoredLogger.enableColoredLogging('&', plugin.getLogger(), "&8[&6Libs&8]");
        plugin.getLogger().log(Level.INFO, "&1Das &2ist &3ein &4kleiner &5Test");

        assertEquals("[Libs] &1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
        assertNotEquals("&1Das &2ist &3ein &4kleiner &5Test", logLogger.messages.remove(0));
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();

    }

    private static class LogLogger extends ConsoleHandler {

        private final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            super.publish(record);

            messages.add(record.getMessage());
        }
    }
}
