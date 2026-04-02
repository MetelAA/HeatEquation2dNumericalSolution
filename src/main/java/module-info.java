module org.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires org.apache.logging.log4j;

    opens org.example.testfx to javafx.fxml;
    opens org.example.testfx.DTO to com.google.gson;

    exports org.example.testfx;
    exports org.example.testfx.Ui;
    exports org.example.testfx.DTO;
    opens org.example.testfx.Ui to javafx.fxml;
}