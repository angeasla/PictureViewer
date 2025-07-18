# PictureViewerApp

A simple and modular JavaFX picture viewer application, designed for navigating local directories and viewing image files. Inspired by the retro DOS image viewer pv.exe, it features:
- Directory navigation
- Image display with zoom and panning
- Custom ListView cells for visual distinction between folders and files
- Modular architecture (Model-View-Controller pattern)
- Secret Easter Eggs!

## Technologies Used
- Java 17+
- JavaFX
- Maven (for dependency management and build)

## How to Run (Local)
1. Clone the repository: `git clone https://github.com/angeasla/PictureViewer`
2. Navigate to the project directory: `cd PictureViewerApp`
3. Build the project using Maven: `mvn clean javafx:run`
   (Note: Ensure you have Maven and a compatible JDK installed.)

## Structure
The application follows a modular structure:
- `com.angeasla.pictureviewer.model`: Contains data logic (e.g., `DirectoryNavigator`).
- `com.angeasla.pictureviewer.ui`: Manages UI components and layout (e.g., `MainView`, `ImageDisplayPane`).
- `com.angeasla.pictureviewer.handlers`: Handles user interactions and orchestrates between UI and model (e.g., `AppController`).
- `com.angeasla.pictureviewer.dialogs`: Contains custom dialog windows.
- `com.angeasla.pictureviewer.util`: Utility classes.

---
*Developed as a learning project.*