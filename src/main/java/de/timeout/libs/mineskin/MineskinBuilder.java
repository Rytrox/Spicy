package de.timeout.libs.mineskin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MineskinBuilder {

    private static final String POST_GENERATE_FILE = "https://api.mineskin.org/generate/file?name=%s&model=%s&visibility=%d";
    private static final String POST_GENERATE_URL = "https://api.mineskin.org/generate/url?url=%s&name=%s&model=%s&visibility=%d";
    private static final String GET_GENERATE_UUID = "https://api.mineskin.org/generate/user/%s?name=%s&model=%s&visibility=%d";

    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();

    private static int REQUEST_TIMEOUT = 0;

    private static final List<MineskinGameProfile> cache = new ArrayList<>();

    private String name;
    private Model model = Model.NORMAL;
    private int visibility = 0;

    /**
     * Creates a new MineskinBuilder
     */
    public MineskinBuilder() {

    }

    /**
     * Set the optional parameter name of this request
     *
     * @param name the name of the profile. It's an optional parameter and does not need to be set
     * @return the builder to continue
     */
    public MineskinBuilder name(@NotNull String name) {
        this.name = name;

        return this;
    }

    /**
     * Set the optional parameter model of the skin. The default value is {@link Model#NORMAL}
     *
     * @param model the model type you want to set
     * @return the model itself
     */
    public MineskinBuilder model(@NotNull Model model) {
        this.model = model;

        return this;
    }

    public MineskinBuilder visibility(boolean isPublic) {
        this.visibility = isPublic ? 0 : 1;

        return this;
    }



    public void generateByUUID(@NotNull UUID uuid, @NotNull Consumer<MineskinGameProfile> profileFunction) {
        SERVICE.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL(String.format(GET_GENERATE_UUID, uuid.toString(), name, model.type, visibility))
                                .openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                connection.setRequestProperty("Content-Type", "application/json");

                try(InputStream response = connection.getInputStream()) {

                    JsonObject object = new JsonParser().parse(new InputStreamReader(response)).getAsJsonObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void generateByFile(@NotNull Path path, @NotNull Consumer<MineskinGameProfile> profileConsumer) {
        SERVICE.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL(String.format(POST_GENERATE_FILE, name, model.type, visibility))
                                .openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                connection.setRequestProperty("Content-Type", "application/json");

                // perform file upload
                try(OutputStream out = connection.getOutputStream()) {
                    out.write(Files.readAllBytes(path));
                }

                // read response
                try(InputStream response = connection.getInputStream()) {
                    JsonObject object = new JsonParser().parse(new InputStreamReader(response)).getAsJsonObject();

                    System.out.println(object);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void generateByURL(@NotNull URL imageURL, @NotNull Consumer<MineskinGameProfile> profileConsumer) {
        SERVICE.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection)
                        new URL(String.format(POST_GENERATE_URL, imageURL.toString(), name, model.type, visibility))
                                .openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                connection.setRequestProperty("Content-Type", "application/json");

                try(InputStream response = connection.getInputStream()) {

                    JsonObject object = new JsonParser().parse(new InputStreamReader(response)).getAsJsonObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void executeProfileFunction(
            @NotNull MineskinGameProfile profile,
            @NotNull Consumer<MineskinGameProfile> function) {

    }

    public enum Model {

        NORMAL("steve"), SLIM("slim");

        private final String type;

        Model(@NotNull String type) {
            this.type = type;
        }

        @NotNull
        public String getType() {
            return type;
        }
    }
}
