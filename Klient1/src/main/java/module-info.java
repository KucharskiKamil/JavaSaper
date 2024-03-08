module com.example.klient1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;


    opens com.example.klient1 to javafx.fxml;
    exports com.example.klient1;
}