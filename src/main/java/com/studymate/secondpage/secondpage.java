package com.studymate.secondpage;

import com.studymate.controller.Logincontroller;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class secondpage extends Application {
    private Logincontroller loginController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Image backgroundImage = new Image("file:src/main/resources/Profileinfo.jpg");
        ImageView backgroundImageView = new ImageView(backgroundImage);

        // Ensure the image fills the entire window
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.setFitWidth(800); // Initial width
        backgroundImageView.setFitHeight(600); // Initial height

        // Create labels and text fields
        Label lb1 = new Label("Name:          ");
        TextField tx1 = new TextField();
        tx1.setPrefWidth(200);

        Label lb2 = new Label("Branch:        ");
        TextField tx2 = new TextField();
        tx2.setPrefWidth(200);

        Label lb3 = new Label("Semester:     ");
        TextField tx3 = new TextField();
        tx3.setPrefWidth(200);

        Label lb4 = new Label("Roll Number: ");
        TextField tx4 = new TextField();
        tx4.setPrefWidth(200);

        // Change the font, color, and weight of the labels
        String labelFont = "-fx-font-family: 'Verdana'; -fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;";
        lb1.setStyle(labelFont);
        lb2.setStyle(labelFont);
        lb3.setStyle(labelFont);
        lb4.setStyle(labelFont);

        // Create a "Next" button
        Button nextButton = new Button("Next");
        HBox next = new HBox(nextButton);
        next.setAlignment(Pos.CENTER);

        // Create HBoxes for each label and text field pair
        HBox hb1 = new HBox(20, lb1, tx1);
        hb1.setAlignment(Pos.CENTER);
        HBox hb2 = new HBox(20, lb2, tx2);
        hb2.setAlignment(Pos.CENTER);
        HBox hb3 = new HBox(20, lb3, tx3);
        hb3.setAlignment(Pos.CENTER);
        HBox hb4 = new HBox(20, lb4, tx4);
        hb4.setAlignment(Pos.CENTER);

        // Create another image and add it to the VBox
        Image additionalImage = new Image("file:src/main/resources/Profileinfo2.jpeg");
        ImageView additionalImageView = new ImageView(additionalImage);
        additionalImageView.setFitWidth(500);
        additionalImageView.setFitHeight(200);

        // Create a VBox and add all HBoxes, the button, and the additional image to it
        VBox vb = new VBox(15, hb1, hb2, hb3, hb4, next);
        vb.setMaxSize(500, 200); // Adjusted height to accommodate the additional image
        vb.setStyle("-fx-background-image: url('file:src/main/resources/Profileinfo2.jpeg');"
                    + "-fx-background-size: cover; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 20;");

        HBox hb = new HBox(vb);
        hb.setAlignment(Pos.CENTER);

        // Create a StackPane and add the ImageView and HBox to it
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(Pos.BASELINE_CENTER);
        stackPane.getChildren().addAll(backgroundImageView, hb);

        // Create a scene with the StackPane as the root node
        Scene scene = new Scene(stackPane, 800, 600);

        // Set a resize listener on the stage to adjust the ImageView size dynamically
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            backgroundImageView.setFitWidth(newVal.doubleValue());
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            backgroundImageView.setFitHeight(newVal.doubleValue());
        });

        // Set the scene to the primary stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Second Page");
        primaryStage.show();
        primaryStage.setFullScreen(true); // Enable full screen mode
    }


}
