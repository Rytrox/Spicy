package de.timeout.libs.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import com.google.gson.*;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemoryConfigurationOptions;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.configuration.file.FileConfigurationOptions;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a FileConfiguration which is written in JSON
 * 
 * @author Timeout
 *
 */
public class JsonConfig extends FileConfiguration {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Creates a new JsonConfiguration from a Json-String
	 * 
	 * @param json the Json-String. Cannot be null
	 */
	public JsonConfig(@Nullable @Language("JSON") String json) {
		try {
			loadFromString(Optional.ofNullable(json).orElse("{}"));
		} catch (JsonParseException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load Configuration from String", e);			
		}
	}
	
	/**
	 * Creates a new JsonConfiguration from a File
	 * 
	 * @param json the .json file
	 * @throws IllegalArgumentException if the file is null
	 */
	public JsonConfig(@NotNull File json) {
		try {
			loadFromString(Optional.ofNullable(FileUtils.readFileToString(json, StandardCharsets.UTF_8)).orElse("{}"));
		} catch (JsonParseException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, e, () -> "Cannot load Configuration from " + json.getName());
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, String.format("Cannot load %s. IO-Exception: ", json.getName()), e);
		}
	}
	
	public JsonConfig(@NotNull InputStream json) {
		try {
			loadFromString(Optional.ofNullable(JsonParser.parseReader(new InputStreamReader(json)).toString()).orElse("{}"));
		} catch (JsonParseException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load Configuration from InputStream", e);			
		}
	}

	public JsonConfig() {
		super();
	}

	@Override
	public void loadFromString(@NotNull String arg0) throws InvalidConfigurationException {
		Map<?, ?> map = GSON.fromJson(arg0, Map.class);

		convertMapsToSections(map, this);
	}

	private void convertMapsToSections(@NotNull Map<?, ?> input, ConfigurationSection section) {
		input.forEach((key, value) -> {
			if (value instanceof Map) {
				convertMapsToSections((Map<?, ?>)value, section.createSection(key.toString()));
			} else section.set(key.toString(), value);
		});
	}

	private @NotNull JsonObject convertSectionsToJson(@NotNull ConfigurationSection currentSection) {
		JsonObject json = new JsonObject();

		// convert map to json
		currentSection.getValues(false).forEach((key, value) -> {
			switch (value) {
				case ConfigurationSection section -> json.add(key, convertSectionsToJson(section));
				case List<?> list -> json.add(key, convertListToArray(list));
				case null, default -> {
					json.add(key, GSON.toJsonTree(value));
				}
			}
		});

		return json;
	}

	private @NotNull JsonArray convertListToArray(@NotNull List<?> currentList) {
		return GSON.toJsonTree(currentList).getAsJsonArray();
	}

	@NotNull
	@Override
	public FileConfigurationOptions options() {
		if(this.options == null) {
			this.options = new JsonFileConfigurationOptions(this);
		}

		return super.options();
	}

	@Override
	public @NotNull String saveToString() {
		return GSON.toJson(convertSectionsToJson(this));
	}

	private static final class JsonFileConfigurationOptions extends FileConfigurationOptions {

		private JsonFileConfigurationOptions(@NotNull MemoryConfiguration configuration) {
			super(configuration);
		}

		@Override
		public @NotNull List<String> getHeader() {
			return new ArrayList<>();
		}

		@Override
		public @NotNull String header() {
			return "";
		}

		@Override
		public @NotNull FileConfigurationOptions setHeader(@Nullable List<String> value) {
			throw new UnsupportedOperationException("JSON does not support Comments");
		}

		@Override
		public @NotNull FileConfigurationOptions header(@Nullable String value) {
			throw new UnsupportedOperationException("JSON does not support Comments");
		}

		@Override
		public @NotNull List<String> getFooter() {
			return new ArrayList<>();
		}

		@Override
		public @NotNull FileConfigurationOptions setFooter(@Nullable List<String> value) {
			throw new UnsupportedOperationException("JSON does not support Comments");
		}

		@Override
		public boolean parseComments() {
			return false;
		}

		@Override
		public @NotNull MemoryConfigurationOptions parseComments(boolean value) {
			throw new UnsupportedOperationException("JSON does not support Comments");
		}

		@Override
		public boolean copyHeader() {
			return false;
		}

		@Override
		public @NotNull FileConfigurationOptions copyHeader(boolean value) {
			throw new UnsupportedOperationException("JSON does not support Comments");
		}
	}
}
