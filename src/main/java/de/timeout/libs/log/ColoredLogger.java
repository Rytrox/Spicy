package de.timeout.libs.log;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extends the normal Bukkit Logger to write Colors
 * @author Timeout
 *
 */
public final class ColoredLogger {

	private ColoredLogger() {
		/* EMPTY. IT IS NOT NECESSARY */
	}

	/**
	 * Enable colored logging for all plugins. Modifies Bukkit's internal logger to match color codes
	 * @param colorFormatter the color formatter character
	 */
	public static void enableColoredLogging(char colorFormatter) {
		enableColoredLogging(colorFormatter, Bukkit.getLogger(), null);
	}

	public static void enableColoredLogging(char colorFormatter,
											@NotNull Logger pluginLogger,
											@Nullable String prefix) {
		// remove old colored logger
		Arrays.stream(pluginLogger.getHandlers())
				.filter(handler -> handler instanceof ColorCodeHandler)
				.forEach(pluginLogger::removeHandler);

		// Add new colored logger
		pluginLogger.addHandler(new ColorCodeHandler(prefix, colorFormatter));
	}

	/**
	 * Handler to modify internal Logger messages
	 *
	 * @author Timeout
	 */
	private static class ColorCodeHandler extends ConsoleHandler {

		private final String prefix;
		private final char colorFormatter;

		public ColorCodeHandler(@Nullable String prefix, char colorFormatter) {
			this.colorFormatter = colorFormatter;
			this.prefix = convertStringMessage(prefix);
		}

		@Override
		public void publish(LogRecord record) {
			// replace prefix
			String[] message = record.getMessage().split(" ");

			if(prefix != null) {
				if(!message[0].matches("\\[.*\\]")) {
					List<String> list = new ArrayList<>();
					list.add(prefix);

					list.addAll(Arrays.asList(message));
					message = list.toArray(new String[0]);
				} else message[0] = prefix;
			}

			record.setMessage(convertStringMessage(String.join(" ", message)));
		}

		/**
		 * Converts a String with Minecraft-ColorCodes into Ansi-Colors.
		 * Returns null if the message is null
		 * @author Timeout
		 *
		 * @param message the message. Can be null
		 * @return the converted message or null if the message is null
		 */
		private String convertStringMessage(@Nullable String message) {
			// Continue if String is neither not null nor empty
			if(message != null && !message.isEmpty()) {
				// copy of string
				String messageCopy = String.copyValueOf(message.toCharArray()) + ConsoleColor.RESET.getAnsiColor();
				// create Matcher to search for color codes
				Matcher matcher = Pattern.compile(String.format("(%c[0-9a-fk-or])(?!.*\1)", colorFormatter)).matcher(message);
				// run through result
				while(matcher.find()) {
					// get Result
					String result = matcher.group(1);
					// get ColorCode
					ConsoleColor color = ConsoleColor.getColorByCode(result.charAt(1));
					// replace color
					messageCopy = messageCopy.replace(result, color.getAnsiColor());
				}
				// return converted String
				return messageCopy;
			}
			// return message for nothing to compile
			return message;
		}
	}
}
