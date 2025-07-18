module com.angeasla.pictureviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;


    opens com.angeasla.pictureviewer to javafx.fxml;
    exports com.angeasla.pictureviewer;
}