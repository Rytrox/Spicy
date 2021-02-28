package de.timeout.libs.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

import com.google.gson.*;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
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
	public JsonConfig(@Nullable String json) {
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
			loadFromString(Optional.ofNullable(IOUtils.toString(json)).orElse("{}"));
		} catch (JsonParseException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load Configuration from InputStream", e);			
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load Json from InputStream. IO-Exception: ", e);
		}
	}

	@Override
	public void loadFromString(@NotNull String arg0) throws InvalidConfigurationException {
		Map<?, ?> map = GSON.fromJson(arg0, Map.class);

		convertMapsToSections(map, this);
	}

	private void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
		input.forEach((key, value) -> {
			if (value instanceof Map) {
				convertMapsToSections((Map<?, ?>)value, section.createSection(key.toString()));
			} else section.set(key.toString(), value);
		});
	}

	private JsonObject convertSectionsToJson(ConfigurationSection currentSection) {
		JsonObject json = new JsonObject();

		// convert map to json
		currentSection.getValues(false).forEach((key, value) -> {
			if(value instanceof ConfigurationSection) {
				json.add(key, convertSectionsToJson((ConfigurationSection) value));
			} else if(value instanceof List) {
				json.add(key, convertListToArray((List<?>) value));
			} else {
				if(value instanceof Boolean) {
					json.addProperty(key, (Boolean) value);
				} else if(value instanceof Number) {
					json.addProperty(key, (Number) value);
				} else if(value instanceof Character) {
					json.addProperty(key, (Character) value);
				} else json.addProperty(key, value.toString());
			}
		});

		return json;
	}

	private JsonArray convertListToArray(List<?> currentList) {
		JsonArray array = new JsonArray();

		currentList.forEach(element -> {
			if(element instanceof List) {
				array.add(convertListToArray((List<?>) element));
			} else if(element instanceof ConfigurationSection) {
				array.add(convertSectionsToJson((ConfigurationSection) element));
			} else {
				if(element instanceof Boolean) {
					array.add((Boolean) element);
				} else if(element instanceof Number) {
					array.add((Number) element);
				} else if(element instanceof Character) {
					array.add((Character) element);
				} else array.add(element.toString());
			}
		});

		return array;
	}

	@Override
	protected @NotNull String buildHeader() {
		return "";
	}

	@Override
	public @NotNull String saveToString() {
		return GSON.toJson(convertSectionsToJson(this));
	}
}
