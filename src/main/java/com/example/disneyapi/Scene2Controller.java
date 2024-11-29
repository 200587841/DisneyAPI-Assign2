package com.example.disneyapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Scene2Controller {
    @FXML
    private ImageView image;

    @FXML
    private Label characterNameLabel;

    @FXML
    private Label movieLabel;

    @FXML
    private Label parkAttractionLabel;

    @FXML
    private Label alliesLabel;

    @FXML
    private Label enemiesLabel;

    @FXML
    private Button supriseMe;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    public void getRandomData() {
        // Generate a random character ID between 1 and 7438
        int characterId = (int) (Math.random() * 7438) + 1;

        // Create an HttpClient object
        HttpClient client = HttpClient.newHttpClient();

        // Create a GET request with the randomly generated character ID
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.disneyapi.dev/character/" + characterId))
                .build();

        // Send the request asynchronously
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    // Try to parse the JSON response of one single object
                    try {
                        // Parse the response string to a JSON object
                        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                        // Extract the "data" object
                        JsonObject dataObject = jsonObject.getAsJsonObject("data");

                        String name = dataObject.get("name").getAsString();
                        String imageUrl = dataObject.get("imageUrl").getAsString();
                        String media = formatJsonArray(dataObject, "films");
                        String parkAttraction = formatJsonArray(dataObject, "parkAttractions");
                        String allies = formatJsonArray(dataObject, "allies");
                        String enemies = formatJsonArray(dataObject, "enemies");

                        // Update the UI on the JavaFX thread
                        Platform.runLater(() -> {
                            // Update the name
                            characterNameLabel.setText(name);

                            // Update the image
                            Image newImage = new Image(imageUrl);
                            image.setImage(newImage);

                            // Update the other fields
                            this.movieLabel.setText(media);
                            this.parkAttractionLabel.setText(parkAttraction);
                            this.alliesLabel.setText(allies);
                            this.enemiesLabel.setText(enemies);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    public void searchCharacter() {
        String characterName = searchField.getText().trim(); // Get user input

        if (characterName.isEmpty()) {
            // You can display a message if the field is empty (optional)
            characterNameLabel.setText("Please enter a character name.");
            return;
        }

        // Create an HttpClient object
        HttpClient client = HttpClient.newHttpClient();

        // Create a GET request to search by character name (ensure the API supports name-based search)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.disneyapi.dev/character?name=" + characterName))
                .build();

        // Send the request asynchronously
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    try {
                        // Parse the response string to a JSON object
                        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                        // Extract the first result from the response (assuming the response contains an array)
                        if (jsonObject.has("data") && !jsonObject.getAsJsonArray("data").isEmpty()) {
                            JsonObject dataObject = jsonObject.getAsJsonArray("data").get(0).getAsJsonObject();

                            // Extract character details
                            String name = dataObject.get("name").getAsString();
                            String imageUrl = dataObject.get("imageUrl").getAsString();
                            String media = formatJsonArray(dataObject, "films");
                            String parkAttraction = formatJsonArray(dataObject, "parkAttractions");
                            String allies = formatJsonArray(dataObject, "allies");
                            String enemies = formatJsonArray(dataObject, "enemies");

                            // Update the UI on the JavaFX thread
                            Platform.runLater(() -> {
                                // Update the name
                                characterNameLabel.setText(name);

                                // Update the image
                                Image newImage = new Image(imageUrl);
                                image.setImage(newImage);

                                // Update the other fields
                                this.movieLabel.setText(media);
                                this.parkAttractionLabel.setText(parkAttraction);
                                this.alliesLabel.setText(allies);
                                this.enemiesLabel.setText(enemies);
                            });
                        } else {
                            // Handle the case when no character is found
                            Platform.runLater(() -> {
                                characterNameLabel.setText("Character not found.");
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            characterNameLabel.setText("Error fetching data.");
                        });
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        characterNameLabel.setText("Error fetching data.");
                    });
                    return null;
                });
    }

    // Utility method to format JSON arrays into a readable string
    private String formatJsonArray(JsonObject jsonObject, String key) {
        if (jsonObject == null || !jsonObject.has(key)) {
            return "None";
        }

        // Check if the value is a JsonArray
        if (jsonObject.get(key).isJsonArray()) {
            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            if (jsonArray.size() == 0) {
                return "None";
            }

            List<String> values = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                values.add(jsonArray.get(i).getAsString());
            }
            return String.join(", ", values);
        }
        // If it's a JsonObject (in case the API sometimes returns an object)
        else if (jsonObject.get(key).isJsonObject()) {
            JsonObject nestedObject = jsonObject.getAsJsonObject(key);
            return nestedObject.toString(); // Convert object to string (or extract fields if needed)
        }
        // If it's a JsonPrimitive (a single value)
        else if (jsonObject.get(key).isJsonPrimitive()) {
            return jsonObject.getAsJsonPrimitive(key).getAsString();
        }

        return "None"; // Default if it's neither an array, object, nor primitive
    }
}