package com.example.disneyapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView image;

    @FXML
    protected void onHelloButtonClick() {
        // Call the get data method
        getData();
    }

    private void getData() {
        // Create an HttpClient object
        HttpClient client = HttpClient.newHttpClient();

        // Create a GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.disneyapi.dev/character/308"))
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

                        // Extract the "name" field
                        String name = dataObject.get("name").getAsString();
                        String imageUrl = dataObject.get("imageUrl").getAsString();

                        // Update the image
                        Image newImage = new Image(imageUrl); // Load the image from the URL
                        // Update the text of the welcome text on the JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            welcomeText.setText(name); // Update the welcomeText with the name

                            image.setImage(newImage); // Set the new image in the ImageView
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
}