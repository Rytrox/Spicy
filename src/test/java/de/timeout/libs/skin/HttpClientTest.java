package de.timeout.libs.skin;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpClientTest {

    @Test
    public void shouldReceiveGET() throws IOException {
        HttpClient client = new HttpClient();

        JsonObject object = client.get("http://ip.jsontest.com/").getAsJsonObject();

        assertNotNull(object);
        assertNotNull(object.get("ip").getAsString());
        assertTrue(object.get("ip").getAsString().matches("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)"));
    }

    @Test
    public void shouldReceivePOST() throws IOException {
        HttpClient client = new HttpClient();

        Map<String, String> params = new HashMap<>();
        params.put("json", new JsonObject().toString());
        JsonObject response = client.post("http://validate.jsontest.com/", params).getAsJsonObject();

        assertNotNull(response);
    }
}
