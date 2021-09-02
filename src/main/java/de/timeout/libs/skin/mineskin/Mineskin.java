package de.timeout.libs.skin.mineskin;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private String name;
    private GameProfile data;
    private boolean privateSkin;

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
        GameProfile profile = new GameProfile(UUID.fromString(profileData.get("uuid").getAsString()), name);
        // fetch TextureData
        JsonObject textures = profileData.get("texture").getAsJsonObject();
        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures",
                        textures.get("value").getAsString(),
                        textures.get("signature").getAsString())
        );
    }

    private @Nullable UUID fetchTrimmedID(String trimmedID) {

        Matcher matcher = UUID_PATTERN.matcher(trimmedID);

        // Must call matched to load REGEX in Matcher
        if(matcher.matches()) {
            return UUID.fromString(
                    String.format("%s-%s-%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3),
                            matcher.group(4), matcher.group(5))
            );
        }

        return null;
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
     * Sets the name of the skin
     *
     * @param name the name of the skin
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the Gameprofile of the Skin
     *
     * @param data the GameProfile of the Skin
     */
    public void setData(GameProfile data) {
        this.data = data;
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
     * Returns if its a private or a public API-Skin
     *
     * @return true if it's private, false otherwise
     */
    public boolean isPrivateSkin() {
        return privateSkin;
    }

    /**
     * Sets if the skin should be private
     * @param privateSkin true if the Skin should be private, false otherwise
     */
    public void setPrivateSkin(boolean privateSkin) {
        this.privateSkin = privateSkin;
    }

    /**
     * Returns the amount of views this skin gets
     *
     * @return the amount of views of this skin
     */
    public int getViews() {
        return views;
    }

    /**
     * Enum for Skin-Variant
     */
    public enum Variant {

        CLASSIC("classic"),
        SLIM("slim");

        private final String type;

        Variant(@NotNull String type) {
            this.type = type;
        }

        /**
         * Returns the name of the Variant
         *
         * @return the name of the Variant
         */
        public String getType() {
            return type;
        }
    }

    public enum Visibility {

        PUBLIC(0),
        PRIVATE(1);

        private final int type;

        Visibility(int type) {
            this.type = type;
        }

        /**
         * Returns the id of the Visibility
         *
         * @return the ID of the Visibility
         */
        public int getType() {
            return type;
        }
    }
}
