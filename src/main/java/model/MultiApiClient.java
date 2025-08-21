package model;

import java.io.IOException;

// Responsible for fetching player data from multiple APIs
public class MultiApiClient {

    // Method that fetches a singular player
    public static Player getOnePlayer(String fullName, String api) throws IOException, InterruptedException {
        if (api.equalsIgnoreCase("Nba")) {
            return NbaApi.getOnePlayer(fullName);
        } else {
            if (api.equalsIgnoreCase("Bdl")) {
                return BdlApi.getOnePlayer(fullName);
            } else {
                System.out.println("Invalid API specified. Please use 'Nba' or 'Bdl'.");
                return null;
            }
        }
    }

    // Method that fetches player data from both APIs and merges the results
    public static Player fetchPlayerData(String playerName, String year) throws IOException, InterruptedException {
        Player player = getOnePlayer(playerName, "Nba");
        System.out.println("Calling addPlayerStats for: " + player.getFirstName() + " " + player.getLastName());
        if (player != null) {
            NbaApi.addPlayerStats(player, year);
        }

        Player bdlPlayer = getOnePlayer(playerName, "Bdl");

        // If both APIs failed
        if (player == null && bdlPlayer == null) {
            return null;
        }

        // If NBA failed but BDL succeeded
        if (player == null) {
            player = bdlPlayer;
        } else if (bdlPlayer != null) {
            // Merge BDL data into NBA player
            if (bdlPlayer.getHeight() != null) player.setHeight(bdlPlayer.getHeight());
            if (bdlPlayer.getWeight() != null) player.setWeight(bdlPlayer.getWeight());
            if (bdlPlayer.getTeam() != null) player.setTeam(bdlPlayer.getTeam());
            if (bdlPlayer.getPosition() != null) player.setPosition(bdlPlayer.getPosition());
        }

        return player;
    }

}