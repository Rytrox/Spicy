package de.timeout.libs.skin.mineskin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineskinHandler {

    public static long REQUEST_TIMEOUT = 0;

    private static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(0);

    private static final LoadingCache<Integer, Mineskin> mineskinCache = CacheBuilder.newBuilder()
            .maximumSize(100L)
            .build(new CacheLoader<>() {

                @Override
                public Mineskin load(@NotNull Integer id) throws Exception {
                    // Load from Mineskin-API:
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create("https://api.mineskin.org/get/id/" + id))
                            .build();
                    // Wait until next try
                    Thread.sleep(Math.max(System.currentTimeMillis() - REQUEST_TIMEOUT, 0));

                    // send Request
                    JsonObject response = new JsonParser()
                            .parse(client.send(request, HttpResponse.BodyHandlers.ofString()).body())
                            .getAsJsonObject();
                    REQUEST_TIMEOUT = System.currentTimeMillis() + response.get("nextRequest").getAsLong();

                    // Convert to MineskinSkin
                    return new Mineskin(response);
                }

            });

    public MineskinHandler() {

    }

    public Future<Mineskin> generate(@NotNull GenerateOptions options, @NotNull String url) {
        return scheduler.schedule(() -> {
            // Build body
            JsonObject reqBody = new Gson().toJsonTree(options).getAsJsonObject();
            reqBody.addProperty("url", url);

            // send request
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.mineskin.org/generate/url"))
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                    .build();
            try {
                return parseResponse(client.send(request, HttpResponse.BodyHandlers.ofString()));
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unable to fetch Skin from Mineskin-API", e);
            } catch (InterruptedException e) {
                Logger.getGlobal().log(Level.WARNING, "INTERRUPTED: END THREAD. FATAL ERROR");
                Thread.currentThread().interrupt();
            }
            return null;
        }, Math.max(System.currentTimeMillis() - REQUEST_TIMEOUT, 0), TimeUnit.MILLISECONDS);
    }

    private Mineskin parseResponse(HttpResponse<String> response) throws IOException {
        if(response.statusCode() == 200) {
            JsonObject object = new JsonParser().parse(response.body()).getAsJsonObject();
            Mineskin mineskin = new Mineskin(object);

            // cache
            mineskinCache.put(mineskin.getID(), mineskin);
            // update timeout
            REQUEST_TIMEOUT = System.currentTimeMillis() + object.get("nextRequest").getAsLong();

            return mineskin;
        } else throw new IOException("Error from Mineskin-API");
    }
}
