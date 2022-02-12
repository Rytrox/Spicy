package de.rytrox.spicy.skin.mineskin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class MineskinHandler {

    public static String GET_UUID = "https://api.mineskin.org/get/uuid/";
    public static String GENERATE_URL = "https://api.mineskin.org/generate/url";
    public static String GENERATE_FILE = "https://api.mineskin.org/generate/upload";
    public static String GENERATE_UUID = "https://api.mineskin.org/generate/user";

    public static long REQUEST_TIMEOUT = 0;

    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    private static final LoadingCache<UUID, Mineskin> mineskinCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .build(new CacheLoader<>() {

                @Override
                public @NotNull Mineskin load(@NotNull UUID id) throws Exception {
                    // Load from Mineskin-API:

                    try(CloseableHttpClient client = HttpClients.createDefault()) {
                        HttpGet get = new HttpGet(GET_UUID + id.toString().replace("-", ""));

                        // send Request
                        try(CloseableHttpResponse response = client.execute(get)) {
                            return Objects.requireNonNull(parseResponse(response));
                        }
                    }

                }

            });

    public static @NotNull Future<Mineskin> getMineskin(@NotNull UUID uuid) {
        return scheduler.submit(() -> mineskinCache.get(uuid));
    }

    /**
     * Generates a new skin on Mineskin based on the URL of a File on the internet. <br>
     * Note that the url must be accessed public, otherwise the skin can't generate
     *
     * @param options the custom generate options
     * @param url the public URL of the Skin-Image file
     * @return a Future containing the new MineSkin
     */
    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull String url) {
        return scheduler.schedule(() -> {
            // Build body
            JsonObject reqBody = new Gson().toJsonTree(options).getAsJsonObject();
            reqBody.addProperty("url", url);

            try(CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(GENERATE_URL);

                post.setEntity(new StringEntity(reqBody.toString()));
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/json");

                try(CloseableHttpResponse response = client.execute(post)) {
                    return parseResponse(response);
                }
            }
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    /**
     * Generates a new skin on Mineskin based on an image file of a skin. <br>
     *
     * @param options the custom generate options
     * @param file the file of the skin image
     * @return a future containing the new Mineskin
     */
    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull File file) {
        return scheduler.schedule(() -> {

            // Build body
            try(CloseableHttpClient client = HttpClients.createDefault()) {
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addTextBody("variant", options.getVariant())
                        .addTextBody("name", Optional.ofNullable(options.getName()).orElse(""))
                        .addTextBody("visibility", String.valueOf(options.getVisibility()))
                        .addBinaryBody("file", file)
                        .build();
                HttpPost post = new HttpPost(GENERATE_FILE);
                post.setEntity(entity);

                try(CloseableHttpResponse response =  client.execute(post)) {
                    return parseResponse(response);
                }
            }
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    /**
     * Generates a new Mineskin based on the UUID of a player
     *
     * @param options the custom generate options
     * @param uuid the uuid of the player
     * @return a future containing the new Mineskin
     */
    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull UUID uuid) {
        return scheduler.schedule(() -> {
            // Build body
            JsonObject reqBody = new Gson().toJsonTree(options).getAsJsonObject();
            reqBody.addProperty("uuid", uuid.toString());

            try(CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(GENERATE_UUID);

                post.setEntity(new StringEntity(reqBody.toString()));
                post.setHeader("Accept", "application/json");
                post.setHeader("Content-Type", "application/json");

                try(CloseableHttpResponse response = client.execute(post)) {
                    return parseResponse(response);
                }
            }
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    private static @Nullable Mineskin parseResponse(@NotNull CloseableHttpResponse response) throws IOException {
        if(response.getStatusLine().getStatusCode() == 200) {
            JsonObject object = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
            Mineskin mineskin = new Mineskin(object);

                // cache
                mineskinCache.put(mineskin.getUniqueID(), mineskin);
                // update timeout
                REQUEST_TIMEOUT = System.currentTimeMillis() + object.get("nextRequest").getAsLong();

                return mineskin;
        } else Logger.getGlobal().log(Level.WARNING, "Unhandled exception of Request. Code: {0}, body: " + EntityUtils.toString(response.getEntity()),
                response.getStatusLine().getStatusCode());

        return null;
    }
}
