package de.rytrox.spicy.skin.mineskin;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the Mineskin-API Skin-DTO
 *
 * @author Timeout
 */
public class Mineskin implements Serializable {

    private static final Pattern UUID_PATTERN = Pattern.compile("(.{8})(.{4})(.{4})(.{4})(.{12})");

    private final int id;
    private final UUID uuid;
    private final long timestamp;
    private final int duration;
    private final int accountId;
    private final int views;

    private final String name;
    private final GameProfile data;
    private final boolean privateSkin;

    public Mineskin(@NotNull JsonObject data) {
        this.id = data.get("id").getAsInt();
        this.uuid = fetchTrimmedID(data.get("uuid").getAsString());
        this.name = data.get("name").getAsString();
        this.timestamp = data.get("timestamp").getAsLong();
        this.duration = data.get("duration").getAsInt();
        this.accountId = data.get("accountId").getAsInt();
        this.privateSkin = data.get("private").getAsBoolean();
        this.views = data.get("views").getAsInt();

        // fetch GameProfile
        JsonObject profileData = data.get("data").getAsJsonObject();
        this.data = new GameProfile(UUID.fromString(profileData.get("uuid").getAsString()), name);
        // fetch TextureData
        JsonObject textures = profileData.get("texture").getAsJsonObject();
        this.data.getProperties().clear();
        this.data.getProperties().put("textures", new Property("textures",
                        textures.get("value").getAsString(),
                        textures.get("signature").getAsString())
        );
    }

    @NotNull
    private UUID fetchTrimmedID(String trimmedID) {

        Matcher matcher = UUID_PATTERN.matcher(trimmedID);

        // Must call matched to load REGEX in Matcher
        if(matcher.matches()) {
            return UUID.fromString(
                    String.format("%s-%s-%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3),
                            matcher.group(4), matcher.group(5))
            );
        } else throw new IllegalArgumentException("JSON uuid field is not a trimmed uuid");
    }

    /**
     * Returns the Mineskin-ID of this skin
     *
     * @return the id of the skin
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the name of the Skin
     *
     * @return the name of the Skin
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the UUID of this GameProfile
     *
     * @return the Unique id of this Gameprofile
     */
    public UUID getUniqueID() {
        return uuid;
    }

    /**
     * Returns the GameProfile of the Skin
     *
     * @return the GameProfile of the skin
     */
    public GameProfile getData() {
        return data;
    }

    /**
     * Returns the timestamp when the skin was created
     *
     * @return the creation timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the duration how long this skin takes to be created
     *
     * @return the creation duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Gets the Account-Nr of the SkinHolder
     *
     * @return the Account ID
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Returns if it's a private or a public API-Skin
     *
     * @return true if it's private, false otherwise
     */
    public boolean isPrivateSkin() {
        return privateSkin;
    }

    /**
     * Returns the amount of views this skin gets
     *
     * @return the amount of views of this skin
     */
    public int getViews() {
        return views;
    }
}
