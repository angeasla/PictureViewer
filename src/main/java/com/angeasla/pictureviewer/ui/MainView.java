package com.angeasla.pictureviewer.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.nio.file.Path;

/**
 * Represents the main graphical user interface (GUI) view of the Picture Viewer application.
 * It is responsible for assembling and arranging all the primary UI components
 * into a cohesive layout.
 */
public class MainView {

    private final BorderPane root;

    /**
     * Constructs the MainView, setting up the primary layout of the application.
     *
     * @param currentPathLabel     The Label displaying the current directory path.
     * @param upBtn                The button for navigating up to the parent directory.
     * @param listView             The ListView displaying files and directories.
     * @param aboutBtn             The button to show the About dialog.
     * @param fullscreenBtn        The button to toggle full screen mode.
     * @param imageScrollPane      The ScrollPane containing the image display.
     */
    public MainView(Label currentPathLabel, Button upBtn, ListView<Path> listView,
                    Button aboutBtn, Button fullscreenBtn, ScrollPane imageScrollPane) {

        // --- Left Sidebar UI Components ---
        // Configure the "Up" button within a ToolBar.
        upBtn.setId("upButton");
        HBox.setHgrow(upBtn, Priority.ALWAYS); // Allow button to grow horizontally.
        ToolBar navBar = new ToolBar(upBtn);
        navBar.setMaxWidth(Double.MAX_VALUE); // Ensure ToolBar expands.

        // Configure bottom buttons (About, Fullscreen) in a VBox.
        aboutBtn.setId("aboutButton");
        fullscreenBtn.setId("fullscreenButton");
        VBox bottomButtonsContainer = new VBox(5, aboutBtn, fullscreenBtn); // 5px spacing.
        bottomButtonsContainer.setAlignment(Pos.CENTER); // Center buttons.
        bottomButtonsContainer.setMaxWidth(Double.MAX_VALUE); // Ensure VBox expands.
        aboutBtn.setMaxWidth(Double.MAX_VALUE); // Ensure buttons expand.
        fullscreenBtn.setMaxWidth(Double.MAX_VALUE); // Ensure buttons expand.

        // Assemble the left sidebar: path label, navigation bar, list view, and bottom buttons.
        VBox leftSidebar = new VBox(5, currentPathLabel, navBar, listView, bottomButtonsContainer); // 5px spacing.
        VBox.setVgrow(listView, Priority.ALWAYS); // Allow ListView to grow vertically.
        leftSidebar.setPadding(new Insets(8)); // Padding around the sidebar.
        leftSidebar.setPrefWidth(300); // Fixed preferred width for the sidebar.


        // --- Root Layout (BorderPane) ---
        root = new BorderPane();
        root.setLeft(leftSidebar); // Place the left sidebar on the left.
        root.setCenter(imageScrollPane); // Place the image display in the center.
        BorderPane.setMargin(root.getCenter(), new Insets(8)); // Add margin around the center content.
    }

    /**
     * Returns the root layout (BorderPane) of the application, ready to be set in a Scene.
     * @return The BorderPane representing the main application layout.
     */
    public BorderPane getRoot() {
        return root;
    }
}