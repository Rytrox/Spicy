package de.rytrox.spicy.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.rytrox.spicy.log.ColoredLogger;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

public record ConfigCreator(File pluginDataFolder, Path configDirectory) {

	private static final Logger logger = Logger.getLogger("ConfigCreator");

	static {
		ColoredLogger.enableColoredLogging('&', logger, "&8[&6Spicy&8]");
	}

	/**
	 * Creates a new ConfigCreator
	 *
	 * @param pluginDataFolder the Plugin-Folder of your plugin
	 * @param configDirectory  the Path of your Config directory. Start like this:
	 *                         Paths.get("subfolder1", "subfolder2");
	 */
	public ConfigCreator(@NotNull File pluginDataFolder, @NotNull Path configDirectory) {
		this.configDirectory = configDirectory;
		this.pluginDataFolder = pluginDataFolder;
	}

	/**
	 * Creates a new ConfigCreator with internat config directory on archive's root
	 *
	 * @param pluginDataFolder the Plugin-Folder of your plugin
	 */
	public ConfigCreator(@NotNull File pluginDataFolder) {
		this(pluginDataFolder, Paths.get(""));
	}

	/**
	 * Copies a file and its content from the Java archive to the specified toPath location in the plugin's folder.
	 * If the specified file cannot be found, a FileNotFoundException is thrown.
	 *
	 * @param fromPath the subpath of the config you want to load from
	 * @param toPath   the subpath where the file should be copied
	 * @return the file with the content inside the datafolder
	 * @throws IOException              if your disk is unwritable or any path could not be found
	 * @throws IllegalArgumentException if any argument is null
	 */
	@NotNull
	public File copyDefaultFile(@NotNull Path fromPath, @NotNull Path toPath) throws IOException {
		// create new file if not exists
		File file = createFile(toPath);

		// try to get FileStream
		if (file.length() <= 2) {
			try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(fromPath.toString());
				 FileWriter out = new FileWriter(file)) {
				// check if file was found
				if (in != null) {
					IOUtils.copy(in, out, StandardCharsets.UTF_8);
				} else
					logger.log(Level.WARNING, "&cUnable to copy content of {0} to Plugin folder", fromPath.getFileName());
			}
		}

		return file;
	}

	/**
	 * Creates a new file inside the plugin's datafolder.
	 *
	 * @param filePath the subpath of the new file. Starting from the plugin's datafolder. Cannot be null
	 * @return the created file
	 * @throws IOException              if the disk is not writable
	 * @throws IllegalArgumentException if the parameter is null
	 */
	@NotNull
	public File createFile(@NotNull Path filePath) throws IOException {
		Path configFile = pluginDataFolder.toPath().resolve(filePath);

		// Create datafolder
		Files.createDirectories(configFile.getParent());

		if (!configFile.toFile().exists()) {
			Files.createFile(configFile);

			logger.log(Level.FINE, "&7Created new file {0} in datafolder", configFile.getFileName());
		}

		return configFile.toFile();
	}
}
