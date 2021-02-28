package de.timeout.libs.mineskin;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpUtil;
import net.minecraft.server.v1_16_R2.HttpUtilities;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class MineskinBuilderTest {

    @Before
    public void mock() {
        MockBukkit.mock();
    }

    @Test
    public void shouldUploadFile() throws IOException {
        Path path = Paths.get("src", "test", "resources", "skin.png");

        HttpPostMultipart multipart = new HttpPostMultipart(
                String.format("https://api.mineskin.org/generate/upload?name=%s&model=%s&visibility=%d", "", "steve", 1),
                "UTF-8",
                new HashMap<>());

        multipart.addFilePart("file", path.toFile());

        String response = multipart.finish();
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
