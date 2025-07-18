package com.angeasla.pictureviewer.ui;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Encapsulates the image display area, including the ImageView, ScrollPane,
 * and logic for zooming and panning.
 */
public class ImageDisplayPane {

    private final ImageView imageView;
    private final ScrollPane imageScrollPane;
    private final StackPane centerPane;

    private double initialX; // For panning
    private double initialY; // For panning

    /**
     * Constructs an ImageDisplayPane.
     *
     * @param stage The primary stage of the application, used for binding image view dimensions.
     */
    public ImageDisplayPane(Stage stage) {
        this.imageView = new ImageView();
        this.imageView.setPreserveRatio(true);
        // Bind image view dimensions to stage dimensions, with offsets for UI elements.
        // These offsets (360 and 150) are specific to the current layout.
        this.imageView.fitWidthProperty().bind(stage.widthProperty().subtract(360));
        this.imageView.fitHeightProperty().bind(stage.heightProperty().subtract(150));

        // Implement Zoom functionality with mouse scroll wheel.
        this.imageView.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaY = event.getDeltaY();

            final double MAX_ZOOM_SCALE = 10.0;
            final double MIN_ZOOM_SCALE = 0.4;

            if (deltaY > 0) { // Zoom in
                double newScaleX = imageView.getScaleX() * zoomFactor;
                double newScaleY = imageView.getScaleY() * zoomFactor;

                if (newScaleX <= MAX_ZOOM_SCALE) {
                    imageView.setScaleX(newScaleX);
                    imageView.setScaleY(newScaleY);
                } else {
                    imageView.setScaleX(MAX_ZOOM_SCALE);
                    imageView.setScaleY(MAX_ZOOM_SCALE);
                }
            } else { // Zoom out
                if (imageView.getScaleX() / zoomFactor >= MIN_ZOOM_SCALE) {
                    imageView.setScaleX(imageView.getScaleX() / zoomFactor);
                    imageView.setScaleY(imageView.getScaleY() / zoomFactor);
                } else {
                    imageView.setScaleX(MIN_ZOOM_SCALE);
                    imageView.setScaleY(MIN_ZOOM_SCALE);
                }
            }
            applyPanningBounds(); // Adjust panning bounds after zoom
            event.consume();
        });

        this.centerPane = new StackPane(imageView);

        // Implement Panning (image dragging) functionality with primary mouse button.
        this.centerPane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                initialX = event.getX();
                initialY = event.getY();
                centerPane.setCursor(Cursor.HAND);
            }
        });

        this.centerPane.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double deltaX = event.getX() - initialX;
                double deltaY = event.getY() - initialY;

                imageView.setTranslateX(imageView.getTranslateX() + deltaX);
                imageView.setTranslateY(imageView.getTranslateY() + deltaY);

                applyPanningBounds(); // Keep image within scroll pane bounds after panning

                initialX = event.getX();
                initialY = event.getY();
            }
        });

        this.centerPane.setOnMouseReleased(event -> {
            centerPane.setCursor(Cursor.DEFAULT);
        });

        this.imageScrollPane = new ScrollPane(centerPane);
        this.imageScrollPane.setFitToWidth(true);
        this.imageScrollPane.setFitToHeight(true);
    }

    /**
     * Returns the ScrollPane containing the image display, ready to be added to a layout.
     * @return The ScrollPane instance.
     */
    public ScrollPane getImageScrollPane() {
        return imageScrollPane;
    }

    /**
     * Returns the ImageView instance, allowing external classes to set the image.
     * @return The ImageView instance.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Loads and displays a new image, resetting zoom and pan.
     * @param image The Image object to display.
     */
    public void displayImage(Image image) {
        // Reset zoom and pan when a new image is selected.
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setTranslateX(0.0);
        imageView.setTranslateY(0.0);
        imageView.setImage(image);
    }

    /**
     * Adjusts the ImageView's translation to keep it within the bounds of the ScrollPane
     * during panning and zooming, preventing it from going completely off-screen.
     */
    private void applyPanningBounds() {
        Bounds imageViewBounds = imageView.getBoundsInLocal();
        double scaledImageWidth = imageViewBounds.getWidth() * imageView.getScaleX();
        double scaledImageHeight = imageViewBounds.getHeight() * imageView.getScaleY();

        if (imageScrollPane == null) {
            return; // Should not happen after constructor, but as a safeguard.
        }

        Bounds viewportBounds = imageScrollPane.getViewportBounds();
        double viewportWidth = viewportBounds.getWidth();
        double viewportHeight = viewportBounds.getHeight();

        double newTranslateX = imageView.getTranslateX();
        double newTranslateY = imageView.getTranslateY();

        // Calculate maximum translation allowed to keep image within viewport.
        double maxTranslateX = (scaledImageWidth - viewportWidth) / 2;
        double maxTranslateY = (scaledImageHeight - viewportHeight) / 2;

        // Apply horizontal bounds.
        if (scaledImageWidth > viewportWidth) {
            newTranslateX = Math.max(-maxTranslateX, Math.min(maxTranslateX, newTranslateX));
        } else {
            newTranslateX = 0; // Center image if it's smaller than the viewport.
        }

        // Apply vertical bounds.
        if (scaledImageHeight > viewportHeight) {
            newTranslateY = Math.max(-maxTranslateY, Math.min(maxTranslateY, newTranslateY));
        } else {
            newTranslateY = 0; // Center image if it's smaller than the viewport.
        }

        imageView.setTranslateX(newTranslateX);
        imageView.setTranslateY(newTranslateY);
    }
}