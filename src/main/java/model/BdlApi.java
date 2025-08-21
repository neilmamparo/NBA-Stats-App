package model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Fetches player data from the Ball Don't Lie API
public class BdlApi {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BDL_URL = "https://api.balldontlie.io/v1/players";
    private static final String API_KEY = "a74e63de-b6be-40e5-8ffb-c933a08809d2";

    public static Player getOnePlayer(String fullName) throws IOException, InterruptedException {
        if (fullName == null || fullName.trim().isEmpty()) {
            System.out.println("Please enter a valid player name.");
            return null;
        }

        // Pick last word of name (likely last name or unique part) to search
        String[] nameParts = fullName.trim().split("\\s+");
        String searchKey = nameParts[nameParts.length - 1];
        String encoded = URLEncoder.encode(searchKey, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BDL_URL + "?search=" + encoded))
                .header("Authorization", API_KEY)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("BDL" + response.body());

        if (response.statusCode() == 200) {
            List<Player> players = parsePlayer(response.body());

            for (Player player : players) {
                String apiName = (player.getFirstName() + " " + player.getLastName()).toLowerCase().replaceAll("[^a-z]", "");
                String inputName = fullName.toLowerCase().replaceAll("[^a-z]", "");

                if (apiName.equals(inputName)) {
                    return player;
                }
            }
        }

        return null;
    }

    // Parses the JSON response from the API and returns a list of Player objects
    private static List<Player> parsePlayer(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode dataNode = rootNode.path("data");

        if (dataNode.isEmpty() || dataNode.isNull()) {
            return List.of();
        }

        return objectMapper.readValue(dataNode.toString(), new TypeReference<List<Player>>() {});
    }
}