package best.tigers.tigersbot.services;

import best.tigers.tigersbot.error.MissingEnvironmentVariableException;
import best.tigers.tigersbot.util.Environment;
import best.tigers.tigersbot.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ImagesService {
    private static ImagesService instance;
    private final String googleCustomSearchKey;
    private final String googleCustomSearchCx;

    private ImagesService() throws MissingEnvironmentVariableException {
        googleCustomSearchCx = Environment.get("GOOGLE_CUSTOM_SEARCH_CX");
        googleCustomSearchKey = Environment.get("GOOGLE_CUSTOM_SEARCH_KEY");
    }

    public static ImagesService getInstance() throws MissingEnvironmentVariableException {
        if (instance == null) {
            instance = new ImagesService();
        }
        return instance;
    }

    public String getImage(String query) {
        var conn = getConnection(query);
        if (conn == null) {
            return "";
        }
        return getImageLink(conn);
    }

    private HttpURLConnection getConnection(String query) {
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(buildUrl(query));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
        } catch (IOException e) {
            Log.severe(e.getMessage());
        }
        return connection;
    }

    private String getImageLink(HttpURLConnection conn) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray items = jsonResponse.getJSONArray("items");
            JSONObject firstResult = items.getJSONObject(0);
            return firstResult.getString("link");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String buildUrl(String query) {
        var encodedQuery = URLEncoder.encode(query);
        var urlBuilder = new StringBuilder();
        urlBuilder.append("https://www.googleapis.com/customsearch/v1?q=")
                .append(encodedQuery)
                .append("&cx=")
                .append(googleCustomSearchCx)
                .append("&searchType=image&key=")
                .append(googleCustomSearchKey);
        return urlBuilder.toString();
    }
}
