
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
import java.util.Arrays;
import java.util.List;

// Fetches player data from the NBA API
public class NbaApi {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String NBA_URL = "https://api-nba-v1.p.rapidapi.com/players";
    private static final String STATS_URL = "https://api-nba-v1.p.rapidapi.com/players/statistics";
    private static final String API_KEY = "cdc4933bbdmsh68beb46b641ab9fp145c9fjsnc3f540cb02e1";
    private static final String API_HOST = "api-nba-v1.p.rapidapi.com";

    // Fetches a player by their full name from the NBA API
    public static Player getOnePlayer(String fullName) throws IOException, InterruptedException {
        System.out.println("getOnePlayer input: " + fullName);
        // Split the input full name into first and last names
        String[] nameParts = fullName.split(" ");
        if (nameParts.length < 2) {
            System.out.println("Please enter full name.");
            return null;
        }

        // Use the last name for the search
        String lastName = String.join(" ", Arrays.copyOfRange(nameParts, 1, nameParts.length));
        if (lastName.contains("-")) {
            String[] lastNameParts = lastName.split("-");
            lastName = lastNameParts[0];
        }
        String encodedLastName = URLEncoder.encode(lastName, StandardCharsets.UTF_8); // Encode the last name

        // Request to the NBA API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(NBA_URL + "?search=" + encodedLastName))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Search Response: " + response.body());

        // Check if the response is successful
        if (response.statusCode() == 200) {
            List<Player> players = parsePlayers(response.body());

            // Check if players list is null or empty
            if (players == null || players.isEmpty()) {
                System.out.println("No players found in the response.");
                return null;
            }

            // Print parsed players for debug
            System.out.println("Parsed Players:");
            for (Player player : players) {
                System.out.println("Player from API: " + player.getFirstName() + " " + player.getLastName());
            }

            // Handle suffixes like Jr. or Sr.
            if (nameParts.length == 3) {
                if (nameParts[2].equals("Jr") || nameParts[2].equals("Sr")) {
                    fullName = fullName + ".";
                }
            }

            // Compare the input name with the API names then return player if found
            for (Player player : players) {
                String fullApiName = player.getFirstName() + " " + player.getLastName();

                System.out.println("Comparing: Input Name = \"" + fullName + "\", API Name = \"" + fullApiName + "\"");

                if (fullApiName.equalsIgnoreCase(fullName)) {
                    return player;
                }
            }
        } else {
            System.out.println("Failed to fetch player data. Status code: " + response.statusCode());
        }

        return null;
    }

    // Fetches player statistics for specific season
    public static void addPlayerStats(Player player, String season) throws IOException, InterruptedException {
        if (player == null || player.getId() == null) {
            System.out.println("Cannot fetch stats, player or ID is null.");
            return;
        }

        String statsUrl = STATS_URL + "?id=" + player.getId() + "&season=" + season;

        // Request to the NBA API for player statistics
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(statsUrl))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", API_HOST)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Stats Response: " + response.body());

        // Check if the response is successful
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.body()).path("response");

            if (rootNode.isArray() && rootNode.size() > 0) {
                double totalPoints = 0, totalRebounds = 0, totalAssists = 0;
                int count = 0;

                for (JsonNode game : rootNode) {
                    totalPoints += game.path("points").asDouble(0);
                    totalRebounds += game.path("totReb").asDouble(0);
                    totalAssists += game.path("assists").asDouble(0);
                    count++;
                }

                player.setPointsPerGame(totalPoints / count);
                player.setReboundsPerGame(totalRebounds / count);
                player.setAssistsPerGame(totalAssists / count);
            }
        }
    }

    // Parses the JSON response to extract player data
    private static List<Player> parsePlayers(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode data = mapper.readTree(json).path("response");

        System.out.println("Raw parsed response: " + data.toString()); // Debug print

        try {
            return mapper.readValue(data.toString(), new TypeReference<List<Player>>() {});
        } catch (Exception e) {
            System.err.println("Error mapping Player list: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // return empty list on error
        }
    }
}
