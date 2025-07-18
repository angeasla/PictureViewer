package com.angeasla.pictureviewer.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * A utility class to display the application's "About" dialog.
 */
public class AboutDialog {

    /**
     * Displays a modal "About" dialog with information about the application.
     *
     * @param ownerStage The primary stage, used as the owner for the modal dialog.
     */
    public static void show(Stage ownerStage) {
        Stage aboutStage = new Stage();
        aboutStage.initOwner(ownerStage); // Set the owner stage (main application window).
        aboutStage.initModality(Modality.APPLICATION_MODAL); // Make dialog modal.
        aboutStage.setTitle("About Picture Viewer"); // Dialog window title.
        aboutStage.setResizable(false); // Prevent resizing of the dialog.

        // About text content.
        Label aboutText = new Label(
                "Picture Viewer\n" +
                        "Version: 1.0\n" +
                        "Developer: Angelos Aslanidis\n" + "\n" +
                        "Inspired by the retro image viewer pv.exe and the BBS era.\n" + "\n" +
                        "Created with JavaFX, a little assistance from Gemini and a lot of 90s nostalgia.\n" +
                        "\n" +
                        "Special thanks to my best friend KYV-4- who was my companion in our first steps in computers.\n"
        );
        aboutText.setId("aboutText"); // ID for CSS styling.

        Button okButton = new Button("OK");
        okButton.setId("okButton"); // ID for CSS styling.
        okButton.setOnAction(e -> aboutStage.close()); // Close dialog on OK button click.

        VBox content = new VBox(15, aboutText, okButton); // VBox for text and button with spacing.
        content.setId("aboutContent"); // ID for styling.
        content.setAlignment(Pos.CENTER); // Center content.
        content.setPadding(new Insets(20)); // Padding around dialog content.

        // Configure OK button to not grow vertically and potentially expand horizontally.
        // Using Region.USE_COMPUTED_SIZE for preferred width allows CSS/layout to define it.
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefWidth(Region.USE_COMPUTED_SIZE);

        Scene aboutScene = new Scene(content, 500, 300); // Create scene for the dialog.
        // Apply CSS stylesheet. Use Objects.requireNonNull for clarity on resource loading.
        aboutScene.getStylesheets().add(Objects.requireNonNull(AboutDialog.class.getResource("/style.css")).toExternalForm());
        aboutStage.setScene(aboutScene);
        aboutStage.showAndWait(); // Display dialog and wait for it to be closed.
    }
}