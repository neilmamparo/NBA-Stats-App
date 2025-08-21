package model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

// This class represents a Player object with attributes and methods to handle data from multiple APIs.
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    // Name (both apis)
    @JsonProperty("firstname")  // NBA-API
    @JsonAlias("first_name")    // BallDontLie
    private String firstName;

    @JsonProperty("lastname")   // NBA-API
    @JsonAlias("last_name")     // BallDontLie
    private String lastName;

    // ID (both apis)
    @JsonProperty("id")         // NBA-API and BallDontLie
    private String id;

    // Position (BDL API)
    @JsonProperty("position")
    private String position;     // e.g. "SG", "SF", "G", etc.

    // Physical Attributes (BDL API)
    private String height;      // Formatted as "6'8""
    private Integer weight;     // In pounds

    // Team (BDL API)
    @JsonProperty("team")
    private Team team;

    // Stats (NBA-API)
    @JsonProperty("points")
    private Double pointsPerGame;

    @JsonProperty("totReb")
    private Double reboundsPerGame;

    @JsonProperty("assists")
    private Double assistsPerGame;

    // Default Constructor
    public Player() {
    }

    // Custom Deserializers
    @JsonSetter("height")
    public void setHeight(JsonNode heightNode) {
        if (heightNode.isTextual()) {
            // BallDontLie format: "6-8" -> "6'8""
            this.height = heightNode.asText().replace("-", "'") + "\"";
        } else if (heightNode.isObject()) {
            // API-NBA format: {"feets":6,"inches":8}
            JsonNode feetNode = heightNode.path("feets");
            JsonNode inchesNode = heightNode.path("inches");
            if (!feetNode.isMissingNode() && !inchesNode.isMissingNode()) {
                this.height = String.format("%d'%d\"",
                        feetNode.asInt(),
                        inchesNode.asInt()
                );
            }
        }
    }

    @JsonSetter("weight")
    public void setWeight(JsonNode weightNode) {
        // Check if the weightNode is a number or an object
        if (weightNode.isNumber()) {
            // BallDontLie direct integer
            this.weight = weightNode.asInt();
        } else if (weightNode.isObject()) {
            // API-NBA format: {"pounds":200}
            JsonNode poundsNode = weightNode.path("pounds");
            if (!poundsNode.isMissingNode()) {
                this.weight = poundsNode.asInt();
            }
        }
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPosition() {
        return position != null ? position : "N/A";
    }

    public String getHeight() {
        return height != null ? height : "N/A";
    }

    public String getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    public Team getTeam() {
        return team;
    }

    // Setters
    public void setPosition(String position) {
        this.position = position;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void setPointsPerGame(Double pointsPerGame) {
        this.pointsPerGame = pointsPerGame;
    }

    public void setReboundsPerGame(Double reboundsPerGame) {
        this.reboundsPerGame = reboundsPerGame;
    }

    public void setAssistsPerGame(Double assistsPerGame) {
        this.assistsPerGame = assistsPerGame;
    }

    // toString method for displaying player information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Player name and team
        sb.append(String.format("%s %s", firstName, lastName));
        if (team != null) {
            sb.append(String.format(" | Team: %s", team.getFullName()));
        }

        // Position
        sb.append(String.format("\nPosition: %s", (position != null ? position : "N/A")));

        // Physical attributes
        sb.append(String.format("\nHeight: %s | Weight: %s lbs",
                (height != null ? height : "N/A"),
                (weight != null ? weight : "N/A")));

        // If stats are available, display them, if not, show "Did not play"
        if (pointsPerGame != null && reboundsPerGame != null && assistsPerGame != null) {
            sb.append(String.format("\nPPG: %.1f | RPG: %.1f | APG: %.1f",
                    pointsPerGame, reboundsPerGame, assistsPerGame));
        } else {
            sb.append("\nDid not play");
        }
        return sb.toString();
    }
}