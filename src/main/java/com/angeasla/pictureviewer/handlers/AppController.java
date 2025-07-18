package com.angeasla.pictureviewer.handlers;

import com.angeasla.pictureviewer.dialogs.AboutDialog;
import com.angeasla.pictureviewer.dialogs.EasterEggDialog;
import com.angeasla.pictureviewer.model.DirectoryNavigator;
import com.angeasla.pictureviewer.ui.ImageDisplayPane;
import com.angeasla.pictureviewer.util.ImageUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The controller for the Picture Viewer application, handling user interactions
 * and coordinating between the UI (View) and the data/logic (Model).
 */
public class AppController {

    private final DirectoryNavigator directoryNavigator;
    private final ImageDisplayPane imageDisplayPane;
    private final Stage primaryStage; // Reference to the main stage for dialogs/fullscreen
    private final Button upButton; // Reference to the up button for firing events

    // For Easter Egg
    private static final String EASTER_EGG_CODE = "LALUNA";
    private StringBuilder typedCode = new StringBuilder();

    /**
     * Constructs an AppController.
     *
     * @param directoryNavigator The DirectoryNavigator instance to manage file system operations.
     * @param imageDisplayPane   The ImageDisplayPane instance to display images.
     * @param primaryStage     The main application stage.
     * @param upButton         The "Up" button, needed to programmatically fire its action.
     */
    public AppController(DirectoryNavigator directoryNavigator, ImageDisplayPane imageDisplayPane, Stage primaryStage, Button upButton) {
        this.directoryNavigator = directoryNavigator;
        this.imageDisplayPane = imageDisplayPane;
        this.primaryStage = primaryStage;
        this.upButton = upButton; // Store reference to the up button
    }

    /**
     * Returns an EventHandler for the "Up" button.
     * Navigates up one directory or shows all roots if at a root.
     * @return An EventHandler for ActionEvents.
     */
    public EventHandler<ActionEvent> getUpButtonAction() {
        return e -> {
            if (directoryNavigator.getCurrentDir() != null && directoryNavigator.getCurrentDir().getParent() != null) {
                directoryNavigator.openDir(directoryNavigator.getCurrentDir().getParent());
            } else if (directoryNavigator.getCurrentDir() != null) {
                directoryNavigator.showAllRoots();
            }
        };
    }

    /**
     * Returns an EventHandler for the "About" button.
     * Displays the About dialog.
     * @return An EventHandler for ActionEvents.
     */
    public EventHandler<ActionEvent> getAboutButtonAction() {
        return e -> AboutDialog.show(primaryStage);
    }

    /**
     * Returns an EventHandler for the "Full screen" button.
     * Toggles the primary stage's full screen mode.
     * @param fullscreenBtn The fullscreen button whose text needs to be updated.
     * @return An EventHandler for ActionEvents.
     */
    public EventHandler<ActionEvent> getFullscreenButtonAction(Button fullscreenBtn) {
        return e -> {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
            // Listener for full screen property remains in PictureViewerApp for now to update button text
            // as it involves listening to a stage property.
            // Could be moved here if a direct reference to the button's text property was maintained.
        };
    }

    /**
     * Returns a ChangeListener for the ListView's selected item property.
     * Displays the selected image if it's a valid image file.
     * @return A ChangeListener for Path objects.
     */
    public ChangeListener<Path> getListViewSelectionListener() {
        return (obs, old, path) -> {
            if (path != null && Files.isRegularFile(path) && ImageUtils.isImage(path.toFile())) {
                imageDisplayPane.displayImage(new Image(path.toUri().toString(), true));
            }
        };
    }

    /**
     * Returns an EventHandler for the ListView's mouse click event.
     * Opens a directory on double-click.
     * @return An EventHandler for MouseEvents.
     */
    public EventHandler<javafx.scene.input.MouseEvent> getListViewMouseClickHandler() {
        return e -> {
            if (e.getClickCount() == 2) {
                ListView<Path> listView = (ListView<Path>) e.getSource();
                Path p = listView.getSelectionModel().getSelectedItem();
                if (p != null && Files.isDirectory(p)) directoryNavigator.openDir(p);
            }
        };
    }

    /**
     * Returns an EventHandler for the ListView's keyboard key press event.
     * Handles ENTER key for opening files/directories.
     * @return An EventHandler for KeyEvents.
     */
    public EventHandler<KeyEvent> getListViewKeyHandler() {
        return e -> {
            ListView<Path> listView = (ListView<Path>) e.getSource();
            Path p = listView.getSelectionModel().getSelectedItem();
            if (p == null) return;
            switch (e.getCode()) {
                case ENTER -> {
                    if (Files.isDirectory(p)) directoryNavigator.openDir(p);
                    else if (ImageUtils.isImage(p.toFile()))
                        imageDisplayPane.displayImage(new Image(p.toUri().toString(), true));
                }
            }
        };
    }

    /**
     * Returns an EventHandler for global scene key press events.
     * Handles Backspace for "Up" navigation and the Easter Egg code input.
     * @return An EventHandler for KeyEvents.
     */
    public EventHandler<KeyEvent> getSceneKeyHandler() {
        return event -> {
            // Handle Backspace key to trigger the "Up" button action.
            if (event.getCode() == KeyCode.BACK_SPACE) {
                upButton.fire(); // Simulate a click on the "Up" button.
                event.consume(); // Consume the event to prevent default system behavior.
            }

            // Easter Egg key sequence handling.
            String character = event.getText();
            if (character != null && !character.isEmpty()) {
                typedCode.append(character.toUpperCase()); // Append typed char (convert to uppercase)
                // Keep the typedCode buffer length limited to the EASTER_EGG_CODE length.
                if (typedCode.length() > EASTER_EGG_CODE.length()) {
                    typedCode.delete(0, typedCode.length() - EASTER_EGG_CODE.length());
                }

                // Check if the typed sequence matches the Easter Egg code.
                if (typedCode.toString().equals(EASTER_EGG_CODE)) {
                    EasterEggDialog.show(primaryStage); // Show Easter Egg dialog.
                    typedCode.setLength(0); // Reset typed code after activation.
                    event.consume(); // Consume event to prevent other handlers from processing.
                }
            }
        };
    }
}