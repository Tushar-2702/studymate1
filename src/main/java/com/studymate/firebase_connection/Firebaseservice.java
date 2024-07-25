
package com.studymate.firebase_connection;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.studymate.controller.Logincontroller;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;



public class Firebaseservice {
    private TextField emailField;
    private PasswordField passwordField;
    private Logincontroller loginController;

    public Firebaseservice(Logincontroller loginController, TextField emailField, PasswordField passwordField) {
        this.loginController = loginController;
        this.emailField = emailField;
        this.passwordField = passwordField;
    }



    public boolean signUp() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            // Create user request
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setDisabled(false);

            // Create user in Firebase Auth
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created user: " + userRecord.getUid());
            showAlert("Success", "User created successfully.");
            return true;
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create user: " + e.getMessage());
            return false;
        }
    }

    public boolean login() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            // Firebase API key (replace with your actual API key)
            String apiKey = "AIzaSyC7r2-wanHR0n7c8XCT63b0YpiQu9k4-Dw";

            // Firebase authentication URL
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Request body for login
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("password", password);
            jsonRequest.put("returnSecureToken", true);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response
            if (conn.getResponseCode() == 200) {
                showAlert(true);
                return true;
            } else {
                showAlert("Invalid Login", "Invalid credentials!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to login: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

   
    private void showAlert(boolean isLoggedIn) {
        if (isLoggedIn) {
            loginController.navigateToPage2();
        } else {
            Label msg = new Label("Welcome");
            msg.setStyle("-fx-font-family:Times New Roman;-fx-font-size:30px;-fx-font-weight:Bold;");
            msg.setAlignment(Pos.CENTER);

            Button logoutButton = new Button("Logout");
            logoutButton.setAlignment(Pos.BASELINE_CENTER);

            Image img = new Image("file:src/main/resources/note.jpg");
            ImageView iv = new ImageView(img);
            iv.setFitHeight(1000);
            iv.setFitWidth(800);

            VBox vBox = new VBox(10, msg, logoutButton);
            vBox.setAlignment(Pos.CENTER);

            StackPane stackPane = new StackPane();
            stackPane.getChildren().addAll(iv, vBox);
            stackPane.setAlignment(vBox, Pos.CENTER);

            logoutButton.setOnAction(event -> loginController.initializeLoginScene());

            // Update LoginController's primary stage scene
            Scene scene = new Scene(stackPane, 1000, 800);
            loginController.setPrimaryStageScene(scene);
        }
    }
}
