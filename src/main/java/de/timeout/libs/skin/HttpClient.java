package de.timeout.libs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which handles HTTP Requests
 *
 * @author Timeout
 */
public class HttpClient {

    /**
     * Sends a GET-Request to a server and receives an answer as JSON
     *
     * @param url the url you want to connect
     * @return the answer as JSON
     */
    public JsonElement get(@NotNull String url) throws IOException {
        return get(url, new HashMap<>());
    }

    /**
     * Sends a GET Request to a server and receives an answer as JSON
     *
     * @param urlString the url you want to connect
     * @param params A map containing all relevant parameters
     * @return the answer as JSON
     */
    public JsonElement get(@NotNull String urlString, @NotNull Map<String, String> params) throws IOException {
        // Create URL and Connection
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return request(connection, params);
    }

    /**
     * Sends a POST-Request to a server and receives an answer as JSON
     *
     * @param url the url you want to connect
     * @return the answer as JSON
     */
    public JsonElement post(@NotNull String url) throws IOException {
        return post(url, new HashMap<>());
    }

    /**
     * Sends a POST-Request to a server and receives an answer as JSON
     *
     * @param urlString the url you want to connect
     * @param params a map containing all relevant
     * @return the answer as JSON
     */
    public JsonElement post(@NotNull String urlString, @NotNull Map<String, String> params) throws IOException {
        // Create URL and Connection
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        return request(connection, params);
    }

    /**
     * Performs a Request to a server and receives and answer as JSON
     *
     * @param connection the Connection of the request
     * @param params a map containing all relevant parameters
     * @return the answer as JSON
     */
    private JsonElement request(@NotNull HttpURLConnection connection, Map<String, String> params) throws IOException {
        StringBuilder response = new StringBuilder();

        // Set Methods
        connection.setDoOutput(true);

        try(DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            // write params in stream
            out.writeBytes(buildParams(params));

            String line;
            while((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        // disconnect
        connection.disconnect();

        // parse answer and return
        return new JsonParser().parse(response.toString());
    }

    /**
     * Parse Parameters in a valid String format for HTTP-Requests
     *
     * @param params A map containing all params
     * @return A valid string format for HTTP requests
     *
     * @throws UnsupportedEncodingException if the params could not be translated into UTF-8 URL format
     */
    private static String buildParams(@NotNull Map<String, String> params) throws UnsupportedEncodingException {
        List<String> paramsOut = new ArrayList<>();

        // format params
        for(Map.Entry<String, String> entry : params.entrySet()) {
            paramsOut.add(
                    String.format("%s=%s",
                            URLEncoder.encode(entry.getKey(), "UTF-8"),
                            URLEncoder.encode(entry.getValue(), "UTF-8")
                    )
            );
        }

        // return joined values
        return String.join("&", paramsOut);
    }
}
