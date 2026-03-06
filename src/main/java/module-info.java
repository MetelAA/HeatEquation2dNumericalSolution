module org.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens org.example.testfx to javafx.fxml;
    exports org.example.testfx;
    exports org.example.testfx.UiSlop;
    opens org.example.testfx.UiSlop to javafx.fxml;
}