package best.tigers.services;

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

    private ImagesService(){
        var environmentVariables = System.getenv();
        googleCustomSearchCx = environmentVariables.get("GOOGLE_CUSTOM_SEARCH_CX");
        googleCustomSearchKey = environmentVariables.get("GOOGLE_CUSTOM_SEARCH_KEY");
    };

    public static ImagesService getInstance() {
        if (instance == null) {
            instance = new ImagesService();
        }
        return instance;
    }

    public String getImage(String query) {
        try {
            // build the URL for the Google CSE API request
            var encodedQuery = URLEncoder.encode(query);
            String urlStr = "https://www.googleapis.com/customsearch/v1?q=" +
                    encodedQuery +
                    "&cx=" +
                    googleCustomSearchCx +
                    "&searchType=image&key=" +
                    googleCustomSearchKey;

            // send the API request and get the response
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // parse the JSON response to get the URL of the first image result
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray items = jsonResponse.getJSONArray("items");
            JSONObject firstResult = items.getJSONObject(0);
            String imageUrl = firstResult.getString("link");

            return imageUrl;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
