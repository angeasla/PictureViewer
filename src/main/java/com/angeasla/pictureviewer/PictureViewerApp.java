package com.angeasla.pictureviewer;

import com.angeasla.pictureviewer.handlers.AppController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import com.angeasla.pictureviewer.model.DirectoryNavigator;
import com.angeasla.pictureviewer.ui.ImageDisplayPane;
import com.angeasla.pictureviewer.ui.MainView;
import java.nio.file.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main application class for the Picture Viewer.
 * This JavaFX application serves as the entry point and orchestrator,
 * setting up the primary stage, instantiating core components (model, view, controller),
 * and initializing the application's overall structure.
 * It follows a modular design, delegating specific responsibilities to other classes.
 */
public class PictureViewerApp extends Application {

    // --- Core Components ---
    /**
     * ExecutorService for loading files in a background thread to prevent UI freezing.
     * Uses a fixed thread pool of 2 threads to manage concurrent operations efficiently.
     */
    private final ExecutorService loader = Executors.newFixedThreadPool(2);
    /**
     * ListView to display files and directories in the current path.
     * It shows a list of Path objects representing files and subdirectories.
     */
    private final ListView<Path> listView = new ListView<>();
    /**
     * Label to display the current directory path.
     * Provides visual feedback to the user about their current location in the file system.
     */
    private final Label currentPathLabel = new Label();

    // --- Component References ---
    /**
     * Manages directory navigation, file listing, and history within the application (Model).
     */
    private DirectoryNavigator directoryNavigator;
    /**
     * Encapsulates the image display area, including ImageView, ScrollPane, and logic for zooming and panning (View).
     */
    private ImageDisplayPane imageDisplayPane;
    /**
     * Represents the main graphical user interface (GUI) view of the application,
     * assembling and arranging all primary UI components (View).
     */
    private MainView mainView;
    /**
     * The controller for the Picture Viewer application, handling user interactions
     * and coordinating between the UI (View) and the data/logic (Model).
     */
    private AppController appController;

    /**
     * Static Image loaded once for the folder icon, used in the ListView to visually
     * distinguish directories from files.
     * The image file is expected to be in the 'src/main/resources' directory.
     */
    private static final Image FOLDER_ICON = new Image(Objects.requireNonNull(PictureViewerApp.class.getResourceAsStream("/folder_icon.png")));

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param stage The primary stage for this application, onto which
     * the application scene can be set.
     */
    @Override
    public void start(Stage stage) {
        // Load custom font from resources for consistent application styling.
        Font.loadFont(getClass().getResourceAsStream("/PressStart2P-Regular.ttf"), 10);

        // Configure the current path label's appearance and behavior.
        currentPathLabel.setId("currentPath"); // Assigns an ID for CSS styling.
        currentPathLabel.setWrapText(true);   // Allows text to wrap to multiple lines if needed.
        currentPathLabel.setMaxWidth(Double.MAX_VALUE); // Ensures the label expands to fill available width.

        // Initialize ImageDisplayPane, responsible for image rendering and interaction (zoom/pan).
        imageDisplayPane = new ImageDisplayPane(stage);

        // Initialize UI buttons. These are local to 'start' as their layout is managed by MainView,
        // and their actions are delegated to AppController.
        final Button upBtn = new Button("⬆ Up");
        final Button aboutBtn = new Button("About");
        final Button fullscreenBtn = new Button("Full screen");

        // Initialize DirectoryNavigator, which handles file system operations.
        // It requires the ListView, currentPathLabel, and the ExecutorService for its operations.
        directoryNavigator = new DirectoryNavigator(listView, currentPathLabel, loader);

        // Initialize AppController, connecting UI events to application logic.
        // It needs references to the model (directoryNavigator), view (imageDisplayPane),
        // the primary stage (for dialogs/fullscreen), and specific buttons for programmatic firing.
        appController = new AppController(directoryNavigator, imageDisplayPane, stage, upBtn);

        /* ---------- Set up Actions and Listeners via AppController ---------- */
        // Button actions are delegated to the AppController for centralized event handling logic.
        upBtn.setOnAction(appController.getUpButtonAction());
        aboutBtn.setOnAction(appController.getAboutButtonAction());
        fullscreenBtn.setOnAction(appController.getFullscreenButtonAction(fullscreenBtn));

        // Listener for the stage's full-screen property to dynamically update the fullscreen button's text.
        // This listener remains here as it directly modifies a local UI component (fullscreenBtn's text).
        stage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                fullscreenBtn.setText("Exit full screen");
            } else {
                fullscreenBtn.setText("Full screen");
            }
        });

        /* ---------- LIST VIEW CONFIGURATION AND LISTENERS ---------- */
        // Define how items in the ListView are displayed, including text and graphics.
        // This 'cell factory' creates custom ListCell instances for each item.
        listView.setCellFactory(lv -> new ListCell<>() {
            // ImageView instance for the folder icon, created once per cell for efficiency.
            private final ImageView icon = new ImageView();

            @Override
            protected void updateItem(Path item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // Clear text and graphic if the cell is empty or has no item.
                    setText(null);
                    setGraphic(null);
                } else {
                    // Set the cell's text based on the Path item.
                    // Root paths (e.g., "C:\") are displayed as is, while others show only the file name.
                    if (item.getParent() == null && item.getRoot() != null) {
                        setText(item.toString());
                    } else if (item.getFileName() != null) {
                        setText(item.getFileName().toString());
                    } else {
                        setText(item.toString());
                    }

                    // Set the cell's graphic (icon).
                    // If the item is a directory, display the FOLDER_ICON; otherwise, clear the graphic.
                    if (Files.isDirectory(item)) {
                        icon.setImage(FOLDER_ICON);
                        icon.setFitWidth(16);  // Set desired width for the icon.
                        icon.setFitHeight(16); // Set desired height for the icon.
                        setGraphic(icon);      // Apply the icon to the cell.
                    } else {
                        setGraphic(null); // No icon for regular files.
                    }
                }
            }
        });

        // Delegate ListView selection and key/mouse events to the AppController.
        listView.getSelectionModel().selectedItemProperty().addListener(appController.getListViewSelectionListener());
        listView.setOnMouseClicked(appController.getListViewMouseClickHandler());
        listView.setOnKeyPressed(appController.getListViewKeyHandler());

        // Initialize MainView, which constructs the primary layout (BorderPane) of the application.
        // It receives the necessary UI components to arrange them.
        mainView = new MainView(currentPathLabel, upBtn, listView, aboutBtn, fullscreenBtn, imageDisplayPane.getImageScrollPane());

        /* ---------- APPLICATION STARTUP ---------- */
        // Populate the file list with root directories on application start.
        directoryNavigator.showAllRoots();
        // Create the main scene using the root layout provided by MainView.
        Scene scene = new Scene(mainView.getRoot(), 1000, 700);
        // Apply the CSS stylesheet for application styling.
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        // Delegate global scene key press events (e.g., Backspace, Easter Egg code) to the AppController.
        scene.setOnKeyPressed(appController.getSceneKeyHandler());

        // Set the scene on the primary stage and configure its title.
        stage.setScene(scene);
        stage.setTitle("Picture Viewer");

        // Attempt to load and set the application icon from resources.
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/app_icon.png")));
            stage.getIcons().add(icon);
        } catch (NullPointerException e) {
            System.err.println("Warning: Icon file not found at /app_icon.png. Using default icon.");
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        // Display the primary stage.
        stage.show();
    }

    /**
     * Called when the application is stopped. This method is used to
     * perform necessary cleanup, such as gracefully shutting down the ExecutorService
     * to prevent resource leaks.
     */
    @Override
    public void stop() {
        loader.shutdown(); // Shuts down the background thread pool, completing pending tasks.
    }

    /**
     * The main method for launching the JavaFX application.
     * This is the standard entry point for all JavaFX applications.
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args); // Launches the JavaFX application.
    }
}