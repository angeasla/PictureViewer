package com.angeasla.pictureviewer.util;

import java.io.File;

/**
 * Utility class for image-related helper methods.
 */
public class ImageUtils {

    /**
     * Checks if a given File represents a recognized image file type based on its extension.
     *
     * @param f The File object to check.
     * @return true if the file extension indicates it's an image, false otherwise.
     */
    public static boolean isImage(File f) {
        String n = f.getName().toLowerCase(); // Get file name in lowercase.
        // Check for common image file extensions.
        return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") ||
                n.endsWith(".gif") || n.endsWith(".bmp") || n.endsWith(".webp");
    }
}