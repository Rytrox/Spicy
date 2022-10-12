package de.rytrox.spicy.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.io.Files;

/**
 * This class represents a Yaml Document with UTF-8 Coding
 * @author timeout
 *
 */
public class UTFConfig extends YamlConfiguration {

	private static final Pattern keyPattern = Pattern.compile("^(\\s*)([^#\\n -].*):(.*)");
	private static final Pattern commentPattern = Pattern.compile("(\\s*)(#.*)");

	/**
	 * These are option fields of the Yaml-Configuration. It is important to have access.
	 */
	private static final Field optionField = FieldUtils.getField(YamlConfiguration.class, "yamlOptions", true);
	private static final Field representerField = FieldUtils.getField(YamlConfiguration.class, "yamlRepresenter", true);
	private static final Field yamlField = FieldUtils.getField(YamlConfiguration.class, "yaml", true);

	private final Map<String, List<String>> comments = new HashMap<>();

	/**
	 * Create a UTF-Config of a File
	 * @param file the required file
	 */
	public UTFConfig(File file) {
		try {
			// load Config from file content
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.SEVERE, e, () -> "Could not load Configuration " + file.getName());
		}
	}

	/**
	 * Create a UTF-Config of an InputStream
	 * @param stream the used inputsteam
	 * @throws IOException If the stream cannot be read
	 */
	public UTFConfig(InputStream stream) throws IOException {
		this(IOUtils.toString(new InputStreamReader(stream)));
	}

	public UTFConfig(String source) {
		try {
			loadFromString(source);
		} catch (InvalidConfigurationException e) {
			Bukkit.getLogger().log(Level.WARNING, e, () -> "Could not load Configuration from String");
		}
	}

	/**
	 * This method write the current configuration into the file.
	 * @param file the file where the config should be written in
	 */
	@Override
	public void save(@NotNull File file) throws IOException {
		// File cannot be null
		Validate.notNull(file, "File can't be null");
		// Create parent dirs of the file if they don't exist
		Files.createParentDirs(file);
		// Convert configuration into a String
		String data = this.saveToString();

		// writes String into the file and close writer due AutoCloseable
		try(Writer writer = new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
			writer.write(data);
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
		Representer yamlRepresenter = null;
		Yaml yaml = null;

		try {
			yamlOptions = (DumperOptions) FieldUtils.readField(optionField, this, true);
			yamlRepresenter = (Representer) FieldUtils.readField(representerField, this, true);
			yaml = (Yaml) FieldUtils.readField(yamlField, this, true);

		} catch (IllegalAccessException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Unable to save Yaml-File", e);
		}

		assert yamlOptions != null && yamlRepresenter != null && yaml != null;

		// if all values are loaded
		// apply settings
		yamlOptions.setIndent(this.options().indent());
		yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yamlOptions.setAllowUnicode(true);
		yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		String valueDump = yaml.dump(this.getValues(false)).replace("\\{\\}\n", "");

		LinkedList<String> pathBuilder = new LinkedList<>();
		List<String> newLines = new ArrayList<>();

		Arrays.stream(valueDump.split("\n"))
				.forEach(line -> {
					Matcher keyMatcher = keyPattern.matcher(line);

					if(keyMatcher.matches()) {
						// update path
						updateKeyPath(pathBuilder, keyMatcher);
						String key = String.join(".", pathBuilder);

						// append comments before line if exists...
						List<String> commentList = comments.get(key);
						if (commentList != null && !commentList.isEmpty()) {
							for (String commentLine : commentList) {

								String lineBuilder = new String(new char[Math.max(0, StringUtils.countMatches(key, "."))]).replace("\0", "  ") +
										commentLine;

								newLines.add(lineBuilder);
							}
						}
					}
					// append line at last
					newLines.add(line);
				});

		return String.join("\n", newLines);
	}

	/**
	 * This method loads a Yaml-Configuration of a file
	 * @param file the file
	 */
	@Override
	public void load(@NotNull File file) throws IOException, InvalidConfigurationException {
		this.load(new InputStreamReader(java.nio.file.Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
	}

	/**
	 * This method loads a Yaml-Configuration of an InputStream
	 * @param stream the InputStream
	 */
	public void load(@NotNull InputStream stream) throws IOException, InvalidConfigurationException {
		this.load(new InputStreamReader(stream, StandardCharsets.UTF_8));
	}

	/**
	 * Comment Section Implementation!!
	 */

	@Override
	public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
		super.loadFromString(contents);

		LinkedList<String> pathBuilder = new LinkedList<>();
		List<String> currentComments = new ArrayList<>();

		Arrays.stream(contents.split("\n"))
				.forEach(line -> {
					// Search for Comment in line
					Matcher commentMatcher = commentPattern.matcher(line);
					Matcher keyMatcher = keyPattern.matcher(line);

					// add comments or whitespaces
					if(line.trim().isEmpty() || commentMatcher.matches()) {
						// add comment or white line to list
						String comment = commentMatcher.matches() ? commentMatcher.group(2) : line;
						currentComments.add(comment);
					} else if(keyMatcher.matches()) {
						updateKeyPath(pathBuilder, keyMatcher);

						// add comment to line if found next to key
						int commentBegin = line.indexOf("#");
						if(commentBegin > -1) currentComments.add(line.substring(commentBegin));

						// set comments to map
						setComments(String.join(".", pathBuilder), currentComments.toArray(new String[0]));
						currentComments.clear();
					}
				});
	}

	private void updateKeyPath(@NotNull LinkedList<String> pathBuilder, @NotNull Matcher keyMatcher) {
		// get Layer
		int layer = keyMatcher.group(1).length() / 2;

		// remove all unnecessary paths...
		while(pathBuilder.size() > layer) pathBuilder.removeLast();

		// push current key
		pathBuilder.add(keyMatcher.group(2));
	}

	/**
	 * Adds a new comment line to a certain path
	 * @param path the path of the new comment
	 * @param comment the comment itself
	 */
	public void addComment(@NotNull String path, @NotNull String comment) {
		// validate comment
		if(!comment.trim().isEmpty() && !comment.startsWith("#"))
			comment = "# " + comment;

		// check if path is not the header
		List<String> currentComments = Optional
				.ofNullable(comments.get(path))
				.orElse(new ArrayList<>());

		currentComments.add(comment);
		comments.put(path, currentComments);
	}

	/**
	 * Removes all Comment line from a certain path
	 * @param path the path you want to remove
	 * @return if there were some comments before and could be removed
	 */
	public boolean removeComments(@NotNull String path) {
		return comments.remove(path) != null;
	}

	/**
	 * Sets a new bundle of comments to a certain path
	 * @param path the path you want to set
	 * @param comments the comments you want to set
	 */
	public void setComments(@NotNull String path, @NotNull String... comments) {
		List<String> commentList = new ArrayList<>();
		Arrays.stream(comments)
				.map(comment -> comment.trim().isEmpty() || comment.startsWith("#") ? comment : "# " + comment)
				.forEach(commentList::add);

		// add to comment or header
		if(!path.isEmpty()) {
			// add to comment
			this.comments.put(path, commentList);
		}
	}

	@NotNull
	public List<String> getComments(@NotNull String path) {
		return new ArrayList<>(Optional.ofNullable(comments.get(path))
				.orElse(new ArrayList<>()));
	}

	@ApiStatus.Internal
	Map<String, Object> getMap() {
		return new HashMap<>(map);
	}
}
