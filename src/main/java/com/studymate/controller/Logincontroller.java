
package com.studymate.controller;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.studymate.firebase_connection.Firebaseservice;
import com.studymate.page2.page2;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Logincontroller extends Application {
    private Stage primaryStage;
    private Firebaseservice firebaseService;

    public void setPrimaryStageScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public void initializeLoginScene() {
        Scene loginScene = createLoginAndSignUpScene();
        primaryStage.setScene(loginScene);
    }

    private Scene createLoginAndSignUpScene() {
        // Load and set background image
        Image img = new Image("file:src/main/resources/login page2.jpg", 800, 600, false, true);
        ImageView backgroundImageView = new ImageView(img);
        backgroundImageView.setFitHeight(1000);
        backgroundImageView.setFitWidth(1300);

        // Layout for login components with a semi-transparent overlay
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 20; -fx-background-insets: 15;");

        // Create and style the app logo with a sleek effect
        Image appLogo = new Image("file:src/main/resources/logo.jpg");
        ImageView logoImageView = new ImageView(appLogo);
        logoImageView.setFitHeight(120);
        logoImageView.setFitWidth(120);
        logoImageView.setPreserveRatio(true);
        logoImageView.setEffect(new DropShadow(15, Color.ORANGE));

        // Create and style the title label
        Label titleLabel = new Label("Welcome to StudyMate");
        titleLabel.setFont(Font.font("Roboto", 30));
        titleLabel.setTextFill(Color.DARKBLUE);
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Email and Password fields with new styles
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(350);
        emailField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #0099ff; -fx-border-radius: 5px; -fx-font-size: 14px; -fx-text-fill: #333;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(350);
        passwordField.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #0099ff; -fx-border-radius: 5px; -fx-font-size: 14px; -fx-text-fill: #333;");

        // Buttons with a modern gradient effect and hover animations
        Button loginButton = new Button("Log In");
        Button signUpButton = new Button("Sign Up");
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6f00, #ff8f00); -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 20px; -fx-padding: 10px 20px;");
        signUpButton.setStyle("-fx-background-color: linear-gradient(to right, #00bcd4, #00acc1); -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 20px; -fx-padding: 10px 20px;");
        loginButton.setPrefWidth(150);
        signUpButton.setPrefWidth(150);

        // Add animations to buttons
        addButtonAnimation(loginButton);
        addButtonAnimation(signUpButton);

        // Service for login and sign up
        firebaseService = new Firebaseservice(this, emailField, passwordField);

        loginButton.setOnAction(event -> firebaseService.login());
        signUpButton.setOnAction(event -> firebaseService.signUp());

        HBox buttonBox = new HBox(20, loginButton, signUpButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add components to layout
        layout.getChildren().addAll(logoImageView, titleLabel, emailField, passwordField, buttonBox);

        // Create and return scene
        StackPane stackPane = new StackPane(backgroundImageView, layout);
        return new Scene(stackPane, 1300, 1000);
    }

    private void addButtonAnimation(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.3), button);
        scaleTransition.setByX(0.05);
        scaleTransition.setByY(0.05);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        button.setOnMouseEntered(event -> scaleTransition.play());
        button.setOnMouseExited(event -> scaleTransition.stop());
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Initialize Firebase App
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/studymate.json");
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Create initial login and signup scene
        Scene scene = createLoginAndSignUpScene();
        primaryStage.setTitle("STUDYMATE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void navigateToPage2() {
        page2 page = new page2(this);
        try {
            page.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToFirebaseservice() {
        // Create an ImageView for the background image
        Image profileImage = new Image("file:src/main/resources/logout.jpg");
        ImageView profileImageView = new ImageView(profileImage);
        profileImageView.setFitWidth(1300);
        profileImageView.setFitHeight(1000);
        profileImageView.setPreserveRatio(true);
    
        // Create a VBox for content
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 10;");
    
        // Add the "About Us" label
        Label aboutUsLabel = new Label("About Us");
        aboutUsLabel.setFont(Font.font("Roboto", 24));
        aboutUsLabel.setTextFill(Color.DARKBLUE);
        aboutUsLabel.setStyle("-fx-font-weight: bold;");
    
        // Member 1
        Image memberImage1 = new Image("file:src/main/resources/member1.jpg");
        ImageView memberImageView1 = new ImageView(memberImage1);
        memberImageView1.setFitWidth(100);
        memberImageView1.setFitHeight(100);
        memberImageView1.setPreserveRatio(true);
        memberImageView1.setStyle("-fx-border-color: #ff6f00; -fx-border-width: 2px; -fx-border-radius: 5px;");
    
        Text memberText1 = new Text("Shubham Vilas Malve\nGroup Leader\nI am learning to code");
        memberText1.setFont(Font.font("Roboto", 14));
        memberText1.setFill(Color.DARKBLUE);  // Ensure text is visible
    
        HBox memberBox1 = new HBox(15, memberImageView1, memberText1);
        memberBox1.setPadding(new Insets(10));
        memberBox1.setStyle("-fx-background-color: rgba(255, 165, 0, 0.2); -fx-border-radius: 5px;");
    
        // Member 2
        Image memberImage2 = new Image("file:src/main/resources/member2.jpg");
        ImageView memberImageView2 = new ImageView(memberImage2);
        memberImageView2.setFitWidth(100);
        memberImageView2.setFitHeight(100);
        memberImageView2.setPreserveRatio(true);
        memberImageView2.setStyle("-fx-border-color: #00bcd4; -fx-border-width: 2px; -fx-border-radius: 5px;");
    
        Text memberText2 = new Text("Tushar Tukaram Landge\nGroup Member\nI am learning to code");
        memberText2.setFont(Font.font("Roboto", 14));
        memberText2.setFill(Color.DARKBLUE);  // Ensure text is visible
    
        HBox memberBox2 = new HBox(15, memberImageView2, memberText2);
        memberBox2.setPadding(new Insets(10));
        memberBox2.setStyle("-fx-background-color: rgba(0, 188, 212, 0.2); -fx-border-radius: 5px;");
    
        // Add components to VBox
        contentBox.getChildren().addAll(aboutUsLabel, memberBox1, memberBox2);

        StackPane stackPane = new StackPane(profileImageView,contentBox);
    
        // Add a logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #FF5722; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 20px;");
        logoutButton.setOnAction(event -> initializeLoginScene());
        
        // Create the main StackPane
        StackPane.setAlignment(logoutButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(logoutButton, new Insets(10));
        stackPane.getChildren().add(logoutButton);
        
        // Create and set the scene
        Scene scene = new Scene(stackPane, 1300, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        
}