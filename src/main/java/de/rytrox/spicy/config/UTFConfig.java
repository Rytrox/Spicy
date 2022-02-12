package de.rytrox.spicy.config;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;

import de.rytrox.spicy.reflect.Reflections;

/**
 * This class represents a Yaml Document with UTF-8 Coding
 * @author timeout
 *
 */
public class UTFConfig extends YamlConfiguration {

	/**
	 * These are option fields of the Yaml-Configuration. It is important to have access.
	 */
	private static final Field optionField = Reflections.getField(YamlConfiguration.class, "yamlDumperOptions");

	/**
	 * Create a UTF-Config of a File
	 *
	 * @param file the required file
	 */
	public UTFConfig(File file) {
		this.options().parseComments(true);

		try {
			// load Config from file content
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not load Configuration " + file.getName());
		}
	}
	
	/**
	 * Create a UTF-Config of an InputStream
	 *
	 * @param stream the used inputsteam
	 */
	public UTFConfig(@NotNull InputStream stream) {
		this.options().parseComments(true);

		try {
			this.load(stream);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.WARNING, e, () -> "Could not load Configuration from String");
		}
	}
	
	public UTFConfig(@Language("YAML") String source) {
		this.options().parseComments(true);

		try {
			loadFromString(source);
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.WARNING, e, () -> "Could not load Configuration from String");
		}
	}

	/**
	 * This method converts the current configuration into a String 
	 * 
	 * @throws IllegalArgumentException if some attributes are null. Normally it does never come to this
	 */
	@Override
	public @NotNull String saveToString() {
		// Load attributes with Reflection-Utils
		DumperOptions yamlOptions = null;

		try {
			assert optionField != null;

			yamlOptions = (DumperOptions) Reflections.getValue(optionField, this);
		} catch (IllegalAccessException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Unable to save Yaml-File", e);
		}

		assert yamlOptions != null;

		// if all values are loaded
		// apply settings
		yamlOptions.setAllowUnicode(true);
		this.options().parseComments(true);

		return super.saveToString();
	}
	
	/**
	 * This method loads a Yaml-Configuration of a file
	 * @param file the file 
	 */
	@Override
	public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
		this.load(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
	}
	
	/**
	 * This method loads a Yaml-Configuration of an InputStream
	 * @param stream the InputStream
	 */
	public void load(@NotNull InputStream stream) throws IOException, InvalidConfigurationException {
		this.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
	}

}
