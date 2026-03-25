module org.example.testfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.example.testfx;


    opens org.example.testfx to javafx.fxml;
    exports org.example.testfx;
    exports org.example.testfx.Ui;
    opens org.example.testfx.Ui to javafx.fxml;
}