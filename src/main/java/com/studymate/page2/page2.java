/*MAIN */
package com.studymate.page2;

import com.studymate.controller.Logincontroller;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Side;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class page2 extends Application {
    private Logincontroller loginController;
    private Map<String, Map<String, List<Boolean>>> checkBoxStatesMap = new HashMap<>();
    private Map<String, VBox> subjectVBoxes = new HashMap<>();
    private Map<String, List<File>> uploadedFilesMap = new HashMap<>();

    public page2(Logincontroller loginController) {
        this.loginController = loginController;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("STUDYMATE");

        TabPane tabPane = new TabPane();
        tabPane.setSide(Side.LEFT); // Set the tabs to be displayed vertically on the left side
        tabPane.getTabs().add(createTab("First Year", "lightblue", "file:src/main/resources/First year.jpg", "file:src/main/resources/First year.jpg"));
        tabPane.getTabs().add(createTab("Second Year", "lightgreen", "file:src/main/resources/Second year.jpg", "file:src/main/resources/Second year.jpg"));
        tabPane.getTabs().add(createTab("Third Year", "lightcoral", "file:src/main/resources/Third year.jpg", "file:src/main/resources/Third year.jpg"));
        tabPane.getTabs().add(createTab("Final Year", "lightyellow", "file:src/main/resources/Fourth Year.jpg", "file:src/main/resources/Fourth Year.jpg"));

        // Increase the size of the tabs
        tabPane.setTabMinWidth(150);
        tabPane.setTabMinHeight(50);

        // Add the "Next" button
        Button nextButton = new Button("Next");
        nextButton.setOnAction(event -> loginController.navigateToFirebaseservice());
        nextButton.setStyle("-fx-background-color:LIGHTGRAY linear-gradient(to right, #ff6f00, #ff8f00); -fx-text-fill: ORANGE; -fx-font-size: 14px; -fx-border-radius: 20px; -fx-padding: 10px 20px;");

        VBox mainLayout = new VBox(10, tabPane, nextButton);
        mainLayout.setAlignment(Pos.CENTER);
        VBox.setMargin(nextButton, new Insets(10));
        mainLayout.setStyle("-fx-background-COLOR");

        Scene scene = new Scene(mainLayout, 1300, 1000);
        scene.setFill(Color.LIGHTGRAY);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Tab createTab(String title, String colorName, String leftImagePath, String rightImagePath) {
        Tab tab = new Tab(title);
        HBox container = createContent(title, colorName, leftImagePath, rightImagePath);
        tab.setContent(container);
        return tab;
    }

    private HBox createContent(String title, String colorName, String leftImagePath, String rightImagePath) {
        VBox vb1 = new VBox(10);
        vb1.setPadding(new Insets(10));
        vb1.setAlignment(Pos.TOP_CENTER);

        Label label1 = new Label("First Semester " + title);
        label1.setTextFill(Color.FIREBRICK);
        label1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<String> comboBox1 = new ComboBox<>();
        comboBox1.getItems().addAll(getUniqueSubjects(title + " First Semester"));

        Button uploadButton1 = new Button("Upload PDFs");
        VBox pdfListVBox1 = new VBox(5);
        pdfListVBox1.setPadding(new Insets(10));
        vb1.getChildren().addAll(label1, comboBox1, uploadButton1, pdfListVBox1);
        subjectVBoxes.put(title + " First Semester", pdfListVBox1);

        comboBox1.setOnAction(e -> {
            String selectedSubject = comboBox1.getSelectionModel().getSelectedItem();
            updateCheckBoxes(vb1, selectedSubject, title + " First Semester");
            updatePDFs(pdfListVBox1, selectedSubject, title + " First Semester");
        });

        uploadButton1.setOnAction(e -> uploadPDFs(comboBox1, title + " First Semester"));

        Image leftImage = new Image(leftImagePath);
        BackgroundImage bgLeftImage = new BackgroundImage(leftImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        vb1.setBackground(new Background(bgLeftImage));

        VBox vb2 = new VBox(10);
        vb2.setPadding(new Insets(10));
        vb2.setAlignment(Pos.TOP_CENTER);

        Label label2 = new Label("Second Semester " + title);
        label2.setTextFill(Color.DARKORANGE);
        label2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        ComboBox<String> comboBox2 = new ComboBox<>();
        comboBox2.getItems().addAll(getUniqueSubjects(title + " Second Semester"));

        Button uploadButton2 = new Button("Upload PDFs");
        VBox pdfListVBox2 = new VBox(5);
        pdfListVBox2.setPadding(new Insets(10));
        vb2.getChildren().addAll(label2, comboBox2, uploadButton2, pdfListVBox2);
        subjectVBoxes.put(title + " Second Semester", pdfListVBox2);

        comboBox2.setOnAction(e -> {
            String selectedSubject = comboBox2.getSelectionModel().getSelectedItem();
            updateCheckBoxes(vb2, selectedSubject, title + " Second Semester");
            updatePDFs(pdfListVBox2, selectedSubject, title + " Second Semester");
        });

        uploadButton2.setOnAction(e -> uploadPDFs(comboBox2, title + " Second Semester"));

        Image rightImage = new Image(rightImagePath);
        BackgroundImage bgRightImage = new BackgroundImage(rightImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        vb2.setBackground(new Background(bgRightImage));

        ScrollPane sp1 = new ScrollPane(vb1);
        sp1.setFitToWidth(true);
        sp1.setFitToHeight(true);
        HBox.setHgrow(sp1, Priority.ALWAYS);

        ScrollPane sp2 = new ScrollPane(vb2);
        sp2.setFitToWidth(true);
        sp2.setFitToHeight(true);
        HBox.setHgrow(sp2, Priority.ALWAYS);

        HBox container = new HBox(sp1, sp2);
        container.setSpacing(10);
        container.setAlignment(Pos.CENTER);
        container.setBackground(new Background(new BackgroundFill(Color.web(colorName), CornerRadii.EMPTY, Insets.EMPTY)));
        return container;
    }

    private String[] getUniqueSubjects(String context) {
        switch (context) {
            case "First Year First Semester":
                return new String[]{"Math", "Physics", "Chemistry", "Mechanics"};
            case "First Year Second Semester":
                return new String[]{"SME", "PPS", "BEE", "BXE"};
            case "Second Year First Semester":
                return new String[]{"EC", "DC", "ELE", "Math -III"};
            case "Second Year Second Semester":
                return new String[]{"OOP", "PCS", "CS", "SS"};
            case "Third Year First Semester":
                return new String[]{"MCA", "DC", "EFT", "CN"};
            case "Third Year Second Semester":
                return new String[]{"CN", "AJP", "PDC", "PM"};
            case "Final Year First Semester":
                return new String[]{"VLSI", "MIOT", "RMT", "DM"};
            case "Final Year Second Semester":
                return new String[]{"FOC", "EPD", "MC"};
            default:
                return new String[]{};
        }
    }

    private void updateCheckBoxes(VBox container, String selectedSubject, String semesterKey) {
        VBox checkBoxContainer = (VBox) container.lookup("#checkBoxContainer");
        if (checkBoxContainer == null) {
            checkBoxContainer = new VBox(5);
            checkBoxContainer.setId("checkBoxContainer");
            checkBoxContainer.setPadding(new Insets(10));
            container.getChildren().add(checkBoxContainer);
        } else {
            checkBoxContainer.getChildren().clear();
        }

        List<Boolean> checkBoxStates = checkBoxStatesMap
                .computeIfAbsent(semesterKey, k -> new HashMap<>())
                .computeIfAbsent(selectedSubject, k -> new ArrayList<>(Collections.nCopies(6, false)));

        for (int i = 0; i < 6; i++) {
            final int index = i; // Final variable
            CheckBox checkBox = new CheckBox("Unit " + (i + 1));
            checkBox.setSelected(checkBoxStates.get(i));

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                List<Boolean> currentStates = checkBoxStatesMap.get(semesterKey).get(selectedSubject);
                if (currentStates != null) {
                    currentStates.set(index, isSelected);
                }
            });

            checkBoxContainer.getChildren().add(checkBox);
        }
    }

    private void updatePDFs(VBox container, String selectedSubject, String semesterKey) {
        VBox pdfListVBox = (VBox) container.lookup("#pdfListVBox");
        if (pdfListVBox == null) {
            pdfListVBox = new VBox(5);
            pdfListVBox.setId("pdfListVBox");
            pdfListVBox.setPadding(new Insets(10));
            container.getChildren().add(pdfListVBox);
        } else {
            pdfListVBox.getChildren().clear();
        }

        List<File> files = uploadedFilesMap.getOrDefault(semesterKey + ":" + selectedSubject, new ArrayList<>());
        for (File file : files) {
            try {
                ImageView pdfView = renderPDF(file);
                pdfListVBox.getChildren().add(pdfView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPDFs(ComboBox<String> comboBox, String semesterKey) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && comboBox.getSelectionModel().getSelectedItem() != null) {
            String selectedSubject = comboBox.getSelectionModel().getSelectedItem();
            String subjectKey = semesterKey + ":" + selectedSubject;

            List<File> uploadedFiles = uploadedFilesMap.computeIfAbsent(subjectKey, k -> new ArrayList<>());
            VBox pdfListVBox = (VBox) comboBox.getParent().lookup("#pdfListVBox");
            if (pdfListVBox != null) {
                pdfListVBox.getChildren().clear();
            }

            for (File file : selectedFiles) {
                uploadedFiles.add(file);
                try {
                    ImageView pdfView = renderPDF(file);
                    if (pdfListVBox != null) {
                        pdfListVBox.getChildren().add(pdfView);
                    }
                    System.out.println("Uploaded PDF for " + subjectKey + ": " + file.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No subject selected or no files chosen.");
        }
    }

    private ImageView renderPDF(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        java.awt.image.BufferedImage bufferedImage = pdfRenderer.renderImage(0); // Render the first page as an image
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(600); // Adjust the width as needed
        imageView.setPreserveRatio(true);
        document.close();
       
