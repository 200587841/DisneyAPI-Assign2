package com.example.disneyapi;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class Scene1Controller {
    @FXML
    private AnchorPane scene1AnchorPane;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        // Switch to 2nd Scene
        new SceneSwitch(scene1AnchorPane, "scene2-view.fxml");
    }
}