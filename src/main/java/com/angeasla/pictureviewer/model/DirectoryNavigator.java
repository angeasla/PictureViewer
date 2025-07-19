package com.angeasla.pictureviewer.model;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import com.angeasla.pictureviewer.util.ImageUtils; // Import the utility for image checking

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.ExecutorService;

/**
 * Manages directory navigation, file listing, and history within the Picture Viewer application.
 * It interacts with a ListView to display directory contents and a Label to show the current path.
 */
public class DirectoryNavigator {

    private final ListView<Path> listView;
    private final Label currentPathLabel;
    private final ExecutorService loader;

    private final Deque<Path> history = new ArrayDeque<>();
    private Path currentDir;

    /**
     * Constructs a DirectoryNavigator.
     *
     * @param listView       The ListView to populate with directory contents.
     * @param currentPathLabel The Label to update with the current directory path.
     * @param loader         The ExecutorService to use for background file operations.
     */
    public DirectoryNavigator(ListView<Path> listView, Label currentPathLabel, ExecutorService loader) {
        this.listView = listView;
        this.currentPathLabel = currentPathLabel;
        this.loader = loader;
    }

    /**
     * Populates the ListView with the system's root disk drives (e.g., C:\, D:\).
     * This method is called upon application start and when navigating "Up" from a root directory.
     */
    public void showAllRoots() {
        currentDir = null; // Clear the current directory as we're showing roots.
        history.clear(); // Clear navigation history.
        Platform.runLater(() -> { // Update UI on JavaFX Application Thread.
            listView.getItems().clear(); // Clear existing list items.
            Arrays.stream(File.listRoots()) // Get all root file systems.
                    .sorted((a, b) -> a.getAbsolutePath().compareToIgnoreCase(b.getAbsolutePath())) // Sort alphabetically.
                    .map(File::toPath) // Convert File objects to Path objects.
                    .forEach(listView.getItems()::add); // Add them to the ListView.

            // Display appropriate root path indicator based on OS
            String rootIndicator = System.getProperty("os.name").toLowerCase().contains("windows") ? 
                "Drives" : "File System Roots";
            currentPathLabel.setText(rootIndicator);
        });
    }

    /**
     * Opens a specified directory, populating the ListView with its contents (directories and images).
     * This operation is performed in a background thread.
     *
     * @param dir The Path object representing the directory to open.
     */
    public void openDir(Path dir) {
        if (currentDir != null) {
            history.push(currentDir); // Save current directory to history before navigating.
        }
        currentDir = dir; // Set the new current directory.

        // Submit directory listing and file filtering to a background thread.
        loader.submit(() -> {
            try (var s = Files.list(dir)) { // List contents of the directory.
                var files = s.filter(p -> Files.isDirectory(p) || ImageUtils.isImage(p.toFile())) // Filter for directories and images.
                        .sorted((a, b) -> { // Sort: directories first, then files, both alphabetically.
                            boolean da = Files.isDirectory(a);
                            boolean db = Files.isDirectory(b);
                            return da == db ? a.compareTo(b) : da ? -1 : 1;
                        })
                        .toArray(Path[]::new); // Collect results into an array.

                Platform.runLater(() -> { // Update UI on JavaFX Application Thread.
                    listView.getItems().setAll(files); // Update ListView with new directory contents.
                    currentPathLabel.setText(dir.toAbsolutePath().normalize().toString() + ">"); // Update path label.
                });

            } catch (Exception e) {
                System.err.println("Error opening directory: " + dir.toString() + " - " + e.getMessage());
                Platform.runLater(() -> { // Show error dialog on JavaFX Application Thread.
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("An error occurred while trying to open: " + dir.getFileName() + "\n" + e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

    /**
     * Returns the current directory.
     * @return The current directory Path.
     */
    public Path getCurrentDir() {
        return currentDir;
    }

    /**
     * Navigates back in history by retrieving the previous directory.
     * @return The previous directory Path, or null if history is empty.
     */
    public Path popHistory() {
        return history.isEmpty() ? null : history.pop();
    }

    /**
     * Checks if there are previous directories in the history.
     * @return true if history is not empty, false otherwise.
     */
    public boolean hasHistory() {
        return !history.isEmpty();
    }
}