package com.studymate;

// Main Application Class
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.Preferences;

public class StudyMaterialsManager extends Application {
    
    // User preferences
    private Preferences userPrefs = Preferences.userNodeForPackage(StudyMaterialsManager.class);
    private String currentTheme = "Light";
    private String userName = "Student";
    
    // Main components
    private Stage primaryStage;
    private BorderPane mainLayout;
    private VBox sidePanel;
    private VBox contentArea;
    private ListView<StudyMaterial> materialsListView;
    private ObservableList<StudyMaterial> studyMaterials;
    private Label welcomeLabel;
    private ProgressBar studyProgress;
    private Label statsLabel;
    
    // File management
    private Path studyDirectory;
    private Map<String, Integer> categoryStats = new HashMap<>();
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadUserPreferences();
        initializeStudyDirectory();
        
        studyMaterials = FXCollections.observableArrayList();
        materialsListView = new ListView<>(studyMaterials);
        
        setupMainLayout();
        loadExistingMaterials();
        updateStats();
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        applyTheme(scene);
        
        primaryStage.setTitle("Study Materials Manager - " + userName);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Set up auto-save
        primaryStage.setOnCloseRequest(e -> saveUserPreferences());
    }
    
    private void setupMainLayout() {
        mainLayout = new BorderPane();
        
        // Create header
        createHeader();
        
        // Create side panel
        createSidePanel();
        
        // Create main content area
        createContentArea();
        
        // Set up drag and drop
        setupDragAndDrop();
        
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(sidePanel);
        mainLayout.setCenter(contentArea);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        
        // App title and icon
        Label titleLabel = new Label("📚 Study Materials Manager");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User welcome
        welcomeLabel = new Label("Welcome, " + userName + "!");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        welcomeLabel.setTextFill(Color.web("#7f8c8d"));
        
        // Settings button
        Button settingsBtn = createStyledButton("⚙️ Settings", "#95a5a6");
        settingsBtn.setOnAction(e -> showSettingsDialog());
        
        header.getChildren().addAll(titleLabel, spacer, welcomeLabel, settingsBtn);
        
        // Styling
        header.setStyle("-fx-background-color: linear-gradient(to right, #ecf0f1, #bdc3c7); " +
                       "-fx-border-color: #bdc3c7; -fx-border-width: 0 0 2 0;");
        
        return header;
    }
    
    private void createSidePanel() {
        sidePanel = new VBox(15);
        sidePanel.setPadding(new Insets(20));
        sidePanel.setPrefWidth(280);
        sidePanel.setStyle("-fx-background-color: #34495e; -fx-background-radius: 0 10 10 0;");
        
        // Quick actions section
        Label actionsLabel = new Label("📁 Quick Actions");
        actionsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        actionsLabel.setTextFill(Color.WHITE);
        
        Button addFileBtn = createSideButton("📄 Add Files", "#27ae60");
        addFileBtn.setOnAction(e -> addFiles());
        
        Button addFolderBtn = createSideButton("📁 Add Folder", "#3498db");
        addFolderBtn.setOnAction(e -> addFolder());
        
        Button organizeBtn = createSideButton("🗂️ Auto Organize", "#e67e22");
        organizeBtn.setOnAction(e -> autoOrganizeMaterials());
        
        // Categories section
        Label categoriesLabel = new Label("📊 Categories");
        categoriesLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        categoriesLabel.setTextFill(Color.WHITE);
        
        VBox categoriesBox = new VBox(8);
        updateCategoriesDisplay(categoriesBox);
        
        // Progress section
        Label progressLabel = new Label("📈 Study Progress");
        progressLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        progressLabel.setTextFill(Color.WHITE);
        
        studyProgress = new ProgressBar(0.0);
        studyProgress.setPrefWidth(240);
        studyProgress.setStyle("-fx-accent: #27ae60;");
        
        statsLabel = new Label("0 materials added");
        statsLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        statsLabel.setTextFill(Color.web("#ecf0f1"));
        
        sidePanel.getChildren().addAll(
            actionsLabel, addFileBtn, addFolderBtn, organizeBtn,
            new Separator(), categoriesLabel, categoriesBox,
            new Separator(), progressLabel, studyProgress, statsLabel
        );
        
        // Store reference to categories box for easy access
        sidePanel.setUserData(categoriesBox);
    }
    
    private void createContentArea() {
        contentArea = new VBox(15);
        contentArea.setPadding(new Insets(20));
        
        // Search and filter bar
        HBox searchBar = createSearchBar();
        
        // Materials list
        setupMaterialsList();
        
        // Empty state
        VBox emptyState = createEmptyState();
        
        contentArea.getChildren().addAll(searchBar, materialsListView);
        
        // Show empty state if no materials
        if (studyMaterials.isEmpty()) {
            contentArea.getChildren().clear();
            contentArea.getChildren().addAll(searchBar, emptyState);
        }
    }
    
    private HBox createSearchBar() {
        HBox searchBar = new HBox(10);
        searchBar.setPadding(new Insets(10));
        searchBar.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search materials...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        
        ComboBox<String> filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "PDF", "Images", "Documents", "Videos", "Others");
        filterCombo.setValue("All");
        filterCombo.setStyle("-fx-font-size: 14;");
        
        ComboBox<String> sortCombo = new ComboBox<>();
        sortCombo.getItems().addAll("Name", "Date Added", "Size", "Type");
        sortCombo.setValue("Date Added");
        sortCombo.setStyle("-fx-font-size: 14;");
        
        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterMaterials(newText, filterCombo.getValue(), sortCombo.getValue());
        });
        
        filterCombo.setOnAction(e -> {
            filterMaterials(searchField.getText(), filterCombo.getValue(), sortCombo.getValue());
        });
        
        sortCombo.setOnAction(e -> {
            filterMaterials(searchField.getText(), filterCombo.getValue(), sortCombo.getValue());
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshBtn = createStyledButton("🔄 Refresh", "#3498db");
        refreshBtn.setOnAction(e -> {
            loadExistingMaterials();
            updateStats();
        });
        
        searchBar.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Filter:"), filterCombo,
            new Label("Sort:"), sortCombo,
            spacer, refreshBtn
        );
        
        return searchBar;
    }
    
    private void setupMaterialsList() {
        materialsListView.setPrefHeight(500);
        materialsListView.setCellFactory(lv -> new StudyMaterialCell());
        materialsListView.setStyle("-fx-background-color: transparent;");
        
        // Double click to open
        materialsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                StudyMaterial selected = materialsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openMaterial(selected);
                }
            }
        });
    }
    
    private VBox createEmptyState() {
        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(50));
        
        Label emptyIcon = new Label("📚");
        emptyIcon.setFont(Font.font(72));
        
        Label emptyTitle = new Label("No Study Materials Yet");
        emptyTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        emptyTitle.setTextFill(Color.web("#7f8c8d"));
        
        Label emptyDesc = new Label("Start by adding your first study materials\nDrag & drop files here or use the buttons on the left");
        emptyDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        emptyDesc.setTextFill(Color.web("#95a5a6"));
        emptyDesc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Button getStartedBtn = createStyledButton("🚀 Get Started", "#27ae60");
        getStartedBtn.setOnAction(e -> addFiles());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyDesc, getStartedBtn);
        
        return emptyState;
    }
    
    private void setupDragAndDrop() {
        contentArea.setOnDragOver(event -> {
            if (event.getGestureSource() != contentArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        
        contentArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    addStudyMaterial(file);
                }
                success = true;
                updateStats();
                refreshContentArea();
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-padding: 10 20; " +
            "-fx-background-radius: 5; -fx-cursor: hand;", color));
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2);", "")));
        
        return btn;
    }
    
    private Button createSideButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(240);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-padding: 12 15; " +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14;", color));
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(btn.getStyle() + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);");
            btn.setScaleX(1.02);
            btn.setScaleY(1.02);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(btn.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 3);", ""));
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        
        return btn;
    }
    
    // File Management Methods
    private void addFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Study Materials");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx", "*.txt"),
            new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov")
        );
        
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files != null) {
            for (File file : files) {
                addStudyMaterial(file);
            }
            updateStats();
            refreshContentArea();
        }
    }
    
    private void addFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Study Materials Folder");
        
        File directory = dirChooser.showDialog(primaryStage);
        if (directory != null) {
            addFolderRecursively(directory);
            updateStats();
            refreshContentArea();
        }
    }
    
    private void addStudyMaterial(File file) {
        try {
            // Copy file to study directory
            Path targetPath = studyDirectory.resolve(file.getName());
            if (!Files.exists(targetPath)) {
                Files.copy(file.toPath(), targetPath);
            }
            
            StudyMaterial material = new StudyMaterial(targetPath.toFile());
            if (!studyMaterials.contains(material)) {
                studyMaterials.add(material);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to add file: " + e.getMessage());
        }
    }
    
    private void addFolderRecursively(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    addStudyMaterial(file);
                } else if (file.isDirectory()) {
                    addFolderRecursively(file);
                }
            }
        }
    }
    
    private void loadExistingMaterials() {
        studyMaterials.clear();
        if (Files.exists(studyDirectory)) {
            try {
                Files.walk(studyDirectory)
                     .filter(Files::isRegularFile)
                     .forEach(path -> {
                         StudyMaterial material = new StudyMaterial(path.toFile());
                         studyMaterials.add(material);
                     });
            } catch (IOException e) {
                showAlert("Error", "Failed to load existing materials: " + e.getMessage());
            }
        }
    }
    
    private void autoOrganizeMaterials() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Auto Organize");
        confirmDialog.setHeaderText("Organize Materials by Type");
        confirmDialog.setContentText("This will move your files into categorized folders. Continue?");
        
        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                organizeByType();
                loadExistingMaterials();
                updateStats();
                refreshContentArea();
                showAlert("Success", "Materials have been organized successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to organize materials: " + e.getMessage());
            }
        }
    }
    
    private void organizeByType() throws IOException {
        Map<String, Path> categoryFolders = new HashMap<>();
        
        for (StudyMaterial material : studyMaterials) {
            String category = material.getCategory();
            Path categoryFolder = categoryFolders.computeIfAbsent(category, 
                cat -> studyDirectory.resolve(cat));
            
            if (!Files.exists(categoryFolder)) {
                Files.createDirectories(categoryFolder);
            }
            
            Path newPath = categoryFolder.resolve(material.getFile().getName());
            if (!material.getFile().toPath().equals(newPath)) {
                Files.move(material.getFile().toPath(), newPath);
            }
        }
    }
    
    private void openMaterial(StudyMaterial material) {
        try {
            java.awt.Desktop.getDesktop().open(material.getFile());
        } catch (IOException e) {
            showAlert("Error", "Cannot open file: " + e.getMessage());
        }
    }
    
    private void filterMaterials(String searchText, String filter, String sortBy) {
        ObservableList<StudyMaterial> filteredList = FXCollections.observableArrayList();
        
        for (StudyMaterial material : studyMaterials) {
            boolean matchesSearch = searchText.isEmpty() || 
                material.getName().toLowerCase().contains(searchText.toLowerCase());
            boolean matchesFilter = filter.equals("All") || 
                material.getCategory().equals(filter);
            
            if (matchesSearch && matchesFilter) {
                filteredList.add(material);
            }
        }
        
        // Sort the filtered list
        switch (sortBy) {
            case "Name":
                filteredList.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "Size":
                filteredList.sort((a, b) -> Long.compare(a.getSize(), b.getSize()));
                break;
            case "Type":
                filteredList.sort((a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
                break;
            default: // Date Added
                filteredList.sort((a, b) -> b.getDateAdded().compareTo(a.getDateAdded()));
        }
        
        materialsListView.setItems(filteredList);
    }
    
    // UI Update Methods
    private void updateStats() {
        categoryStats.clear();
        for (StudyMaterial material : studyMaterials) {
            categoryStats.merge(material.getCategory(), 1, Integer::sum);
        }
        
        int totalMaterials = studyMaterials.size();
        if (statsLabel != null) {
            statsLabel.setText(totalMaterials + " materials in library");
        }
        
        // Update progress (example: based on number of materials)
        double progress = Math.min(totalMaterials / 50.0, 1.0); // Max 50 for full progress
        if (studyProgress != null) {
            studyProgress.setProgress(progress);
        }
        
        // Update categories display - Find the VBox in the side panel safely
        if (sidePanel != null && sidePanel.getChildren().size() > 0) {
            VBox categoriesBox = findCategoriesBox();
            if (categoriesBox != null) {
                updateCategoriesDisplay(categoriesBox);
            }
        }
    }
    
    private VBox findCategoriesBox() {
        // Use the stored reference if available
        if (sidePanel.getUserData() instanceof VBox) {
            return (VBox) sidePanel.getUserData();
        }
        
        // Fallback: Look for the VBox that comes after the "Categories" label
        for (int i = 0; i < sidePanel.getChildren().size(); i++) {
            if (sidePanel.getChildren().get(i) instanceof Label) {
                Label label = (Label) sidePanel.getChildren().get(i);
                if (label.getText().contains("Categories") && i + 1 < sidePanel.getChildren().size()) {
                    if (sidePanel.getChildren().get(i + 1) instanceof VBox) {
                        return (VBox) sidePanel.getChildren().get(i + 1);
                    }
                }
            }
        }
        return null;
    }
    
    private void updateCategoriesDisplay(VBox categoriesBox) {
        categoriesBox.getChildren().clear();
        
        for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
            HBox categoryItem = new HBox(10);
            categoryItem.setAlignment(Pos.CENTER_LEFT);
            
            Label categoryLabel = new Label(getCategoryIcon(entry.getKey()) + " " + entry.getKey());
            categoryLabel.setTextFill(Color.web("#ecf0f1"));
            categoryLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            Label countLabel = new Label(entry.getValue().toString());
            countLabel.setTextFill(Color.web("#bdc3c7"));
            countLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            
            categoryItem.getChildren().addAll(categoryLabel, spacer, countLabel);
            categoriesBox.getChildren().add(categoryItem);
        }
    }
    
    private void refreshContentArea() {
        if (studyMaterials.isEmpty()) {
            contentArea.getChildren().clear();
            contentArea.getChildren().addAll(createSearchBar(), createEmptyState());
        } else if (contentArea.getChildren().size() < 2 || 
                   !(contentArea.getChildren().get(1) instanceof ListView)) {
            contentArea.getChildren().clear();
            contentArea.getChildren().addAll(createSearchBar(), materialsListView);
        }
    }
    
    // Settings and Personalization
    private void showSettingsDialog() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings & Personalization");
        settingsStage.initOwner(primaryStage);
        
        VBox settingsLayout = new VBox(20);
        settingsLayout.setPadding(new Insets(25));
        settingsLayout.setPrefWidth(400);
        
        // User name setting
        Label nameLabel = new Label("👤 Your Name:");
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        TextField nameField = new TextField(userName);
        nameField.setPromptText("Enter your name");
        
        // Theme setting
        Label themeLabel = new Label("🎨 Theme:");
        themeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Light", "Dark", "Blue");
        themeCombo.setValue(currentTheme);
        
        // Study directory setting
        Label dirLabel = new Label("📁 Study Directory:");
        dirLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        
        HBox dirBox = new HBox(10);
        TextField dirField = new TextField(studyDirectory.toString());
        dirField.setEditable(false);
        dirField.setPrefWidth(250);
        
        Button browseDirBtn = new Button("Browse");
        browseDirBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File dir = chooser.showDialog(settingsStage);
            if (dir != null) {
                dirField.setText(dir.getAbsolutePath());
            }
        });
        
        dirBox.getChildren().addAll(dirField, browseDirBtn);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button saveBtn = createStyledButton("💾 Save", "#27ae60");
        Button cancelBtn = createStyledButton("❌ Cancel", "#e74c3c");
        
        saveBtn.setOnAction(e -> {
            userName = nameField.getText().trim().isEmpty() ? "Student" : nameField.getText().trim();
            currentTheme = themeCombo.getValue();
            
            if (!dirField.getText().equals(studyDirectory.toString())) {
                studyDirectory = Paths.get(dirField.getText());
                initializeStudyDirectory();
                loadExistingMaterials();
                updateStats();
                refreshContentArea();
            }
            
            welcomeLabel.setText("Welcome, " + userName + "!");
            primaryStage.setTitle("Study Materials Manager - " + userName);
            applyTheme(primaryStage.getScene());
            
            saveUserPreferences();
            settingsStage.close();
        });
        
        cancelBtn.setOnAction(e -> settingsStage.close());
        
        buttonBox.getChildren().addAll(saveBtn, cancelBtn);
        
        settingsLayout.getChildren().addAll(
            nameLabel, nameField,
            themeLabel, themeCombo,
            dirLabel, dirBox,
            new Separator(),
            buttonBox
        );
        
        Scene settingsScene = new Scene(settingsLayout);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }
    
    private void applyTheme(Scene scene) {
        String cssFile = "";
        switch (currentTheme) {
            case "Dark":
                cssFile = "-fx-base: #2c3e50;";
                break;
            case "Blue":
                cssFile = "-fx-base: #3498db;";
                break;
            default: // Light
                cssFile = "-fx-base: #ecf0f1;";
        }
        scene.getRoot().setStyle(cssFile);
    }
    
    // Utility Methods
    private void initializeStudyDirectory() {
        if (studyDirectory == null) {
            studyDirectory = Paths.get(System.getProperty("user.home"), "StudyMaterials");
        }
        
        try {
            if (!Files.exists(studyDirectory)) {
                Files.createDirectories(studyDirectory);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to create study directory: " + e.getMessage());
        }
    }
    
    private void loadUserPreferences() {
        userName = userPrefs.get("userName", "Student");
        currentTheme = userPrefs.get("theme", "Light");
        String dirPath = userPrefs.get("studyDirectory", 
            Paths.get(System.getProperty("user.home"), "StudyMaterials").toString());
        studyDirectory = Paths.get(dirPath);
    }
    
    private void saveUserPreferences() {
        userPrefs.put("userName", userName);
        userPrefs.put("theme", currentTheme);
        userPrefs.put("studyDirectory", studyDirectory.toString());
    }
    
    private String getCategoryIcon(String category) {
        switch (category) {
            case "PDF": return "📄";
            case "Images": return "🖼️";
            case "Documents": return "📝";
            case "Videos": return "🎬";
            default: return "📋";
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

// Study Material Model Class
class StudyMaterial {
    private File file;
    private String name;
    private String category;
    private long size;
    private LocalDateTime dateAdded;
    
    public StudyMaterial(File file) {
        this.file = file;
        this.name = file.getName();
        this.size = file.length();
        this.dateAdded = LocalDateTime.now();
        this.category = determineCategory(file);
    }
    
    private String determineCategory(File file) {
        String extension = getFileExtension(file).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return "PDF";
            case "png":
            case "jpg":
            case "jpeg":
            case "gif":
            case "bmp":
                return "Images";
            case "doc":
            case "docx":
            case "txt":
            case "rtf":
                return "Documents";
            case "mp4":
            case "avi":
            case "mov":
            case "wmv":
                return "Videos";
            default:
                return "Others";
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return lastDot > 0 ? name.substring(lastDot + 1) : "";
    }
    
    // Getters
    public File getFile() { return file; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public long getSize() { return size; }
    public LocalDateTime getDateAdded() { return dateAdded; }
    
    public String getFormattedSize() {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        else return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    public String getFormattedDate() {
        return dateAdded.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StudyMaterial that = (StudyMaterial) obj;
        return Objects.equals(file.getAbsolutePath(), that.file.getAbsolutePath());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(file.getAbsolutePath());
    }
    
    @Override
    public String toString() {
        return name;
    }
}

// Custom ListView Cell for Study Materials
class StudyMaterialCell extends ListCell<StudyMaterial> {
    private HBox content;
    private VBox textContent;
    private Label nameLabel;
    private Label detailsLabel;
    private Label categoryLabel;
    private Button actionButton;
    private Button deleteButton;
    
    public StudyMaterialCell() {
        super();
        createLayout();
    }
    
    private void createLayout() {
        content = new HBox(15);
        content.setPadding(new Insets(12));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                        "-fx-border-color: #ecf0f1; -fx-border-radius: 8; -fx-border-width: 1;");
        
        // Icon/Category indicator
        categoryLabel = new Label();
        categoryLabel.setFont(Font.font(24));
        categoryLabel.setMinWidth(40);
        categoryLabel.setAlignment(Pos.CENTER);
        
        // Text content
        textContent = new VBox(4);
        nameLabel = new Label();
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web("#2c3e50"));
        
        detailsLabel = new Label();
        detailsLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        detailsLabel.setTextFill(Color.web("#7f8c8d"));
        
        textContent.getChildren().addAll(nameLabel, detailsLabel);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Action buttons
        actionButton = new Button("📖 Open");
        actionButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                             "-fx-font-size: 11; -fx-padding: 6 12; -fx-background-radius: 4;");
        
        deleteButton = new Button("🗑️");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                             "-fx-font-size: 11; -fx-padding: 6 8; -fx-background-radius: 4;");
        
        HBox buttonBox = new HBox(6);
        buttonBox.getChildren().addAll(actionButton, deleteButton);
        
        content.getChildren().addAll(categoryLabel, textContent, spacer, buttonBox);
        
        // Hover effects
        content.setOnMouseEntered(e -> {
            content.setStyle(content.getStyle() + 
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
            content.setScaleX(1.02);
            content.setScaleY(1.02);
        });
        
        content.setOnMouseExited(e -> {
            content.setStyle(content.getStyle().replaceAll(
                "-fx-effect: dropshadow\\(three-pass-box, rgba\\(0,0,0,0\\.1\\), 10, 0, 0, 2\\);", ""));
            content.setScaleX(1.0);
            content.setScaleY(1.0);
        });
    }
    
    @Override
    protected void updateItem(StudyMaterial material, boolean empty) {
        super.updateItem(material, empty);
        
        if (empty || material == null) {
            setGraphic(null);
        } else {
            // Update content
            categoryLabel.setText(getCategoryIcon(material.getCategory()));
            nameLabel.setText(material.getName());
            detailsLabel.setText(String.format("%s • %s • Added %s", 
                material.getCategory(), 
                material.getFormattedSize(), 
                material.getFormattedDate()));
            
            // Set up button actions
            actionButton.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(material.getFile());
                } catch (IOException ex) {
                    showAlert("Error", "Cannot open file: " + ex.getMessage());
                }
            });
            
            deleteButton.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Material");
                confirm.setHeaderText("Delete " + material.getName() + "?");
                confirm.setContentText("This will permanently delete the file from your study directory.");
                
                if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    try {
                        Files.delete(material.getFile().toPath());
                        getListView().getItems().remove(material);
                    } catch (IOException ex) {
                        showAlert("Error", "Failed to delete file: " + ex.getMessage());
                    }
                }
            });
            
            setGraphic(content);
        }
    }
    
    private String getCategoryIcon(String category) {
        switch (category) {
            case "PDF": return "📄";
            case "Images": return "🖼️";
            case "Documents": return "📝";
            case "Videos": return "🎬";
            default: return "📋";
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Study Session Tracker (Additional Feature)
class StudySession {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String materialName;
    private long durationMinutes;
    
    public StudySession(String materialName) {
        this.materialName = materialName;
        this.startTime = LocalDateTime.now();
    }
    
    public void endSession() {
        this.endTime = LocalDateTime.now();
        this.durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
    }
    
    // Getters
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getMaterialName() { return materialName; }
    public long getDurationMinutes() { return durationMinutes; }
    
    public String getFormattedDuration() {
        if (durationMinutes < 60) {
            return durationMinutes + " min";
        } else {
            long hours = durationMinutes / 60;
            long minutes = durationMinutes % 60;
            return hours + "h " + minutes + "m";
        }
    }
}

// Note-taking Feature
class StudyNote {
    private String title;
    private String content;
    private String associatedMaterial;
    private LocalDateTime created;
    private LocalDateTime lastModified;
    
    public StudyNote(String title, String content, String associatedMaterial) {
        this.title = title;
        this.content = content;
        this.associatedMaterial = associatedMaterial;
        this.created = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        this.lastModified = LocalDateTime.now();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content; 
        this.lastModified = LocalDateTime.now();
    }
    
    public String getAssociatedMaterial() { return associatedMaterial; }
    public LocalDateTime getCreated() { return created; }
    public LocalDateTime getLastModified() { return lastModified; }
    
    public String getFormattedCreated() {
        return created.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}

// Bookmark/Favorites Feature
class MaterialBookmark {
    private String materialPath;
    private String customName;
    private String notes;
    private int priority; // 1-5 stars
    private LocalDateTime bookmarked;
    
    public MaterialBookmark(String materialPath, String customName, int priority) {
        this.materialPath = materialPath;
        this.customName = customName;
        this.priority = Math.max(1, Math.min(5, priority));
        this.bookmarked = LocalDateTime.now();
        this.notes = "";
    }
    
    // Getters and Setters
    public String getMaterialPath() { return materialPath; }
    public String getCustomName() { return customName; }
    public void setCustomName(String customName) { this.customName = customName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = Math.max(1, Math.min(5, priority)); }
    public LocalDateTime getBookmarked() { return bookmarked; }
    
    public String getPriorityStars() {
        StringBuilder stars = new StringBuilder();
        // Add filled stars
        for (int i = 0; i < priority; i++) {
            stars.append("⭐");
        }
        // Add empty stars
        for (int i = 0; i < (5 - priority); i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
}