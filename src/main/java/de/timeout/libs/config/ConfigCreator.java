package de.timeout.libs.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.ByteStreams;
import de.timeout.libs.log.ColoredLogger;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class ConfigCreator {
	
	private static final Logger logger = Logger.getLogger("ConfigCreator");

	static {
		ColoredLogger.enableColoredLogging('&', logger, "&8[&6Libs&8] ");
	}

	protected final File pluginDataFolder;
	protected final Path configDirectory;
	
	public ConfigCreator(@NotNull File pluginDataFolder, @NotNull Path configDirectory) {
		this.configDirectory = configDirectory;
		this.pluginDataFolder = pluginDataFolder;
	}

	/**
	 * Copies a file and its content from the Java archive to the specified toPath location in the plugin's folder.
	 * If the specified file cannot be found, a FileNotFoundException is thrown.
	 *
	 * @param fromPath the subpath of the config you want to load from
	 * @param toPath the subpath where the file should be copied
	 * @return the file with the content inside the datafolder
	 * @throws IOException if your disk is unwritable or any path could not be found
	 * @throws IllegalArgumentException if any argument is null
	 */
	public File copyDefaultFile(@NotNull Path fromPath, @NotNull Path toPath) throws IOException {
		// create Path
		Path internalPath = this.configDirectory.resolve(fromPath);
		Validate.isTrue(internalPath.toFile().exists(),
				String.format("Unable to find file inside JAR: %s", internalPath.toString()));

		// call loadFile()
		File configuration = createFile(toPath);
		// If file is empty
		if(configuration.length() == 0L) {
			// copy files into subfolder
			try(InputStream in = this.getClass().getClassLoader().getResourceAsStream(internalPath.toString());
					OutputStream out = new FileOutputStream(configuration)) {
				if(in != null)
					ByteStreams.copy(in, out);
			}
		}
		logger.log(Level.INFO, "&7Loaded File {0} &asuccessfully", configuration.getName());
		return configuration;
	}

	/**
	 * Creates a new file inside the plugin's datafolder.
	 *
	 * @param filePath the subpath of the new file. Starting from the plugin's datafolder. Cannot be null
	 * @return the created file
	 * @throws IOException if the disk is not writable
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public File createFile(@NotNull Path filePath) throws IOException {
		File configFile = pluginDataFolder.toPath().resolve(filePath).toFile();

		// Create datafolder
		Files.createDirectories(pluginDataFolder.toPath());

		if(configFile.createNewFile())
			logger.log(Level.INFO, "&7Created new file {0} in datafolder", configFile.getName());
		
		return configFile;
	}
}
