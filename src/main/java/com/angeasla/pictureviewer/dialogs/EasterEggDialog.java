package com.angeasla.pictureviewer.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * A utility class to display the secret "Easter Egg" dialog.
 */
public class EasterEggDialog {

    /**
     * Path to the Easter Egg image, expected to be in src/main/resources.
     */
    private static final String EASTER_EGG_IMAGE_PATH = "/easter_egg.jpg";

    /**
     * Displays a modal "Easter Egg" dialog, showing a secret image.
     *
     * @param ownerStage The primary stage, used as the owner for the modal dialog.
     */
    public static void show(Stage ownerStage) {
        Stage easterEggStage = new Stage();
        easterEggStage.initOwner(ownerStage); // Set owner stage.
        easterEggStage.initModality(Modality.APPLICATION_MODAL); // Make dialog modal.
        easterEggStage.setTitle("Toumba!"); // Dialog window title.
        easterEggStage.setResizable(false); // Prevent resizing.

        ImageView easterEggImageView = new ImageView();
        try {
            // Load the Easter Egg image from resources.
            // Using Objects.requireNonNull ensures a NullPointerException if resource is not found,
            // which is then caught by the outer try-catch for better error handling.
            Image image = new Image(Objects.requireNonNull(EasterEggDialog.class.getResourceAsStream(EASTER_EGG_IMAGE_PATH)));
            if (image.isError()) { // Check for image loading errors reported by Image.
                throw image.exceptionProperty().get(); // Throw the actual exception if Image loading failed.
            }
            easterEggImageView.setImage(image);
            easterEggImageView.setPreserveRatio(true); // Maintain aspect ratio.
            easterEggImageView.setFitWidth(300); // Set preferred width for the image.
            easterEggImageView.setFitHeight(300); // Set preferred height for the image.
        } catch (Exception e) {
            System.err.println("Error loading Easter Egg image: " + e.getMessage());
            // Fallback UI if image loading fails.
            Label errorLabel = new Label("Error loading Easter Egg image!");
            errorLabel.setTextFill(javafx.scene.paint.Color.RED);
            VBox errorBox = new VBox(errorLabel);
            errorBox.setAlignment(Pos.CENTER);
            easterEggImageView.setImage(null); // Clear any partial image.
            easterEggImageView.setManaged(false); // Hide ImageView as it's not functional.

            Button okButton = new Button("OK");
            okButton.setId("okButton");
            okButton.setOnAction(ev -> easterEggStage.close());

            VBox errorContent = new VBox(15, errorBox, okButton);
            errorContent.setId("easterEggLayout"); // ID for styling.
            errorContent.setAlignment(Pos.CENTER);
            errorContent.setPadding(new Insets(20));

            Scene errorScene = new Scene(errorContent, 350, 200); // Smaller scene for error.
            errorScene.getStylesheets().add(Objects.requireNonNull(EasterEggDialog.class.getResource("/style.css")).toExternalForm());
            easterEggStage.setScene(errorScene);
            easterEggStage.showAndWait();
            return; // Exit method as the error dialog was shown.
        }

        Button okButton = new Button("OK");
        okButton.setId("okButton");
        okButton.setOnAction(e -> easterEggStage.close());

        VBox content = new VBox(15, easterEggImageView, okButton);
        content.setId("easterEggLayout"); // ID for styling.
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        // Configure OK button.
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefWidth(Region.USE_COMPUTED_SIZE);

        Scene easterEggScene = new Scene(content, 400, 450); // Set dialog size.
        easterEggScene.getStylesheets().add(Objects.requireNonNull(EasterEggDialog.class.getResource("/style.css")).toExternalForm()); // Apply CSS.
        easterEggStage.setScene(easterEggScene);
        easterEggStage.showAndWait(); // Display dialog and wait.
    }
}