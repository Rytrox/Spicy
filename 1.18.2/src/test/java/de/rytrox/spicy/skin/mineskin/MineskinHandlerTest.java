package de.rytrox.spicy.skin.mineskin;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.JsonBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.*;

import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

public class MineskinHandlerTest {

    @BeforeAll
    public static void setupServer() throws IOException {
        MineskinHandler.GET_UUID = "http://localhost:1080/get/uuid/";
        MineskinHandler.GENERATE_URL = "http://localhost:1080/generate/url";
        MineskinHandler.GENERATE_FILE = "http://localhost:1080/generate/upload";
        MineskinHandler.GENERATE_UUID = "http://localhost:1080/generate/user";
        MineskinHandler.MOJANG_UUID = "http://localhost:1080/users/profiles/minecraft/";

        ClientAndServer server = startClientAndServer(1080);
        server.when(
                request()
                        .withPath("/get/uuid/f0765f67ef4a4a34a3a1a048e8ecd822")
                        .withMethod("GET")
        ).respond(
                response()
                        .withDelay(TimeUnit.MILLISECONDS, 20)
                        .withStatusCode(200)
                        .withBody(FileUtils.readFileToString(Paths.get("src", "test", "resources", "mineskin", "skin.json").toFile(), StandardCharsets.UTF_8))
        );

        server.when(
                request()
                        .withMethod("POST")
                        .withPath("/generate/url")
                        .withBody(JsonBody.json("""
                                        {"variant": "classic","visibility": 1,"url": "http://test.url/image.png"}
                                  """))
        ).respond(
                response()
                        .withDelay(TimeUnit.MILLISECONDS, 20)
                        .withStatusCode(200)
                        .withBody(FileUtils.readFileToString(Paths.get("src", "test", "resources", "mineskin", "skin.json").toFile(), StandardCharsets.UTF_8))
        );

        server.when(
                request()
                        .withMethod("POST")
                        .withPath("/generate/user")
                        .withBody(JsonBody.json("""
                                {"variant": "classic","visibility": 1,"uuid": "94dd234a-2320-4f17-9f40-a1cc80afab2d"}
                                """))
        ).respond(
                response()
                        .withDelay(TimeUnit.MILLISECONDS, 20)
                        .withStatusCode(200)
                        .withBody(FileUtils.readFileToString(Paths.get("src", "test", "resources", "mineskin", "skin.json").toFile(), StandardCharsets.UTF_8))
        );

        server.when(
                request()
                        .withMethod("POST")
                        .withPath("/generate/upload")
        ).respond(
                response()
                        .withDelay(TimeUnit.MILLISECONDS, 20)
                        .withStatusCode(200)
                        .withBody(FileUtils.readFileToString(Paths.get("src", "test", "resources", "mineskin", "skin.json").toFile(), StandardCharsets.UTF_8))
        );

        server.when(
                request()
                        .withMethod("GET")
                        .withPath("/users/profiles/minecraft/DerTimeout")
        ).respond(
                response()
                        .withDelay(TimeUnit.MILLISECONDS, 20)
                        .withStatusCode(200)
                        .withBody(JsonBody.json(
                                """
                                { "id": "94dd234a23204f179f40a1cc80afab2d", "name": "DerTimeout" }
                                """
                        ))
        );
    }

    @Test
    public void shouldCacheValue() throws ExecutionException, InterruptedException, TimeoutException {
        Mineskin skin = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertNotNull(skin);
        assertEquals(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"), skin.getUniqueID());
        assertEquals(101, skin.getID());
        assertEquals("test", skin.getName());

        Mineskin cached = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertEquals(skin, cached);
    }

    @Test
    public void shouldGenerateAndCacheSkinFromURL() throws ExecutionException, InterruptedException, TimeoutException {
        MineskinHandler handler = new MineskinHandler();

        Mineskin skin = handler.generate(new GenerateOptions(), "http://test.url/image.png")
                .get(5, TimeUnit.SECONDS);
        assertNotNull(skin);

        Mineskin cached = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertEquals(skin, cached);
    }

    @Test
    public void shouldGenerateAndCacheSkinFromUUID() throws ExecutionException, InterruptedException, TimeoutException {
        MineskinHandler handler = new MineskinHandler();

        Mineskin skin = handler.generate(new GenerateOptions(), UUID.fromString("94dd234a-2320-4f17-9f40-a1cc80afab2d"))
                .get(5, TimeUnit.SECONDS);
        assertNotNull(skin);

        Mineskin cached = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertEquals(skin, cached);
    }

    @Test
    public void shouldGenerateAndCacheSkinFromFile() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        MineskinHandler handler = new MineskinHandler();

        Mineskin skin = handler.generate(new GenerateOptions(),
                    Paths.get("src", "test", "resources", "mineskin", "skin.png").toFile())
                .get(5, TimeUnit.SECONDS);
        assertNotNull(skin);

        Mineskin cached = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertEquals(skin, cached);
    }

    @Test
    public void shouldGenerateSkinByUsername() throws ExecutionException, InterruptedException, TimeoutException {
        MineskinHandler handler = new MineskinHandler();

        Mineskin mineskin = handler.generateByUsername(new GenerateOptions(), "DerTimeout")
                .get(5, TimeUnit.SECONDS);

        assertNotNull(mineskin);

        Mineskin cached = MineskinHandler.getMineskin(UUID.fromString("f0765f67-ef4a-4a34-a3a1-a048e8ecd822"))
                .get(5, TimeUnit.SECONDS);

        assertEquals(mineskin, cached);
    }
}
