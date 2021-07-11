package de.timeout.libs.skin.mineskin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class MineskinHandler {

    public static long REQUEST_TIMEOUT = 0;

    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(0);
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final LoadingCache<UUID, Mineskin> mineskinCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .build(new CacheLoader<>() {

                @Override
                public Mineskin load(@NotNull UUID id) throws Exception {
                    // Load from Mineskin-API:
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create("https://api.mineskin.org/get/uuid/" + id))
                            .build();
                    // Wait until next try
                    Thread.sleep(Math.max(System.currentTimeMillis() - REQUEST_TIMEOUT, 0));

                    // send Request
                    return parseResponse(request);
                }

            });

    public static Future<Mineskin> getMineskin(@NotNull UUID uuid) {
        return scheduler.submit(() -> mineskinCache.get(uuid));
    }

    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull String url) {
        return scheduler.schedule(() -> {
            // Build body
            JsonObject reqBody = new Gson().toJsonTree(options).getAsJsonObject();
            reqBody.addProperty("url", url);

            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mineskin.org/generate/url"))
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                    .header("Content-Type", "application/json")
                    .build();

            return parseResponse(request);
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull File file) {
        return scheduler.schedule(() -> {
            // Build body
            try {
                Map<String, String> body = new HashMap<>();
                body.put("variant", options.getVariant());
                body.put("name", options.getName());
                body.put("visibility", String.valueOf(options.getVisibility()));
                body.put("file", readFile(file));

                String form = body.keySet().stream()
                        .map(key -> key + "=" + URLEncoder.encode(body.get(key), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));

                HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mineskin.org/generate/upload"))
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build();

                return parseResponse(request);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to read File", e);
            }

            return null;
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull UUID uuid) {
        return scheduler.schedule(() -> {
            // Build body
            JsonObject reqBody = new Gson().toJsonTree(options).getAsJsonObject();
            reqBody.addProperty("user", uuid.toString());

            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mineskin.org/generate/user"))
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                    .header("Content-Type", "application/json")
                    .build();

            return parseResponse(request);
        }, Math.max(REQUEST_TIMEOUT - System.currentTimeMillis(), 0), TimeUnit.MILLISECONDS);
    }

    private static Mineskin parseResponse(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JsonObject object = new JsonParser().parse(response.body()).getAsJsonObject();
                Mineskin mineskin = new Mineskin(object);

                // cache
                System.out.println(mineskin);
                System.out.println(mineskin.getUniqueID());
                mineskinCache.put(mineskin.getUniqueID(), mineskin);
                // update timeout
                REQUEST_TIMEOUT = System.currentTimeMillis() + object.get("nextRequest").getAsLong();

                return mineskin;
            } else Logger.getGlobal().log(Level.WARNING, "Unhandled exception of Request. Code: {0}", response.statusCode());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Unable to fetch Skin from Mineskin-API", e);
        } catch (InterruptedException e) {
            Logger.getGlobal().log(Level.WARNING, "INTERRUPTED: END THREAD. FATAL ERROR");
            Thread.currentThread().interrupt();
        }

        return null;
    }

    private String readFile(File file) throws IOException {
        // File to byte-array
        byte[] data = FileUtils.readFileToByteArray(file);

        return new BigInteger(data).toString();
    }
}
