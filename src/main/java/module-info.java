module com.example.travel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires javafx.base;
    requires java.rmi;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.mail;

    opens com.example.travel to javafx.fxml, javafx.graphics;
    exports com.example.travel.controllers;
    opens com.example.travel.controllers to javafx.fxml;
    exports com.example.travel.util;
    opens com.example.travel.util to javafx.fxml;
    exports com.example.travel.models;
    opens com.example.travel.models to org.hibernate.orm.core;
}