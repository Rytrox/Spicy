package de.rytrox.spicy.skin.mineskin;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Builder-Class that creates new Skins in Mineskin. <br>
 * DONT USE THIS CLASS TOO OFTEN!! <br>
 * <br>
 * Best practise is creating one skin once and save his uuid in a file or database and grab the skin with
 * {@link MineskinBuilder#loadMineskin(UUID)}
 *
 * @author Timeout
 */
public class MineskinBuilder {

    private final GenerateOptions options = new GenerateOptions();

    /**
     * Loads a Mineskin by its UUID. <br>
     * <br>
     * THIS IS NOT A REAL PLAYER's UUID, JUST A FAKE PLAYER's!!
     *
     * @param uuid the uuid of the mineskin
     * @return a Future containing the mineskin
     */
    public static @NotNull Future<Mineskin> loadMineskin(@NotNull UUID uuid) {
        return MineskinHandler.getMineskin(uuid);
    }

    /**
     * Sets the variant of the Mineskin. <br>
     * The variant is the Model of the Skin (Steve, Alex)
     *
     * @param variant the variant you want to have. Standard value: Steve
     * @return the builder to continue
     */
    public MineskinBuilder variant(@NotNull GenerateOptions.Variant variant) {
        this.options.setVariant(variant);

        return this;
    }

    /**
     * Sets the visibility of the skin. <br>
     * <br>
     * Skins with visibility 'private' will not be shown at mineskin.org gallery. <br>
     * Skins with visibility 'public' will be shown at mineskin.org gallery. <br>
     * <br>
     * Both types will be saved internally by Mineskin.
     *
     * @param visibility the visibility you want to use. Standard value: private
     * @return the builder to continue
     */
    public MineskinBuilder visibility(@NotNull GenerateOptions.Visibility visibility) {
        this.options.setVisibility(visibility);

        return this;
    }

    /**
     * Sets the name of the skin. <br>
     * This has no effect to the skin. It's just a nice way to name your skins
     *
     * @param name the name you want to use. Standard value: ''
     * @return the builder to continue
     */
    public MineskinBuilder name(@NotNull String name) {
        Validate.isTrue(name.length() <= 20, "Name cannot be longer than 20 characters");
        this.options.setName(name);

        return this;
    }

    /**
     * Creates a new Mineskin. <br>
     * This method returns a Future in a new Task, you need to wait or react to it with chainers
     *
     * @param url the url of the skin in the internet
     * @return A Future containing the Mineskin
     */
    public Future<Mineskin> generate(@NotNull String url) {
        return new MineskinHandler().generate(options, url);
    }

    /**
     * Creates a new Mineskin. <br>
     * The method returns a Future in a new Task, you need to wait or react to it with chainers
     *
     * @param file the png file of the Skin
     * @return A Future containing the Mineskin
     */
    public Future<Mineskin> generate(@NotNull File file) {
        return new MineskinHandler().generate(options, file);
    }

    /**
     * Creates a new Mineskin. <br>
     * This method returns a Future in a new Task, you need to wait or react to it with chainers
     *
     * @param uuid the uuid of the player you want to grab the skin from
     * @return A future containing the Mineskin
     */
    public Future<Mineskin> generate(@NotNull UUID uuid) {
        return new MineskinHandler().generate(options, uuid);
    }
}
