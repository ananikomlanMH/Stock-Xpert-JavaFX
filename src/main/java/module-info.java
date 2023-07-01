module com.anani.stockxpert {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires org.controlsfx.controls;
    requires jbcrypt;
    requires kernel;
    requires layout;
    requires java.desktop;
    requires io;

    opens com.anani.stockxpert to javafx.fxml;
    exports com.anani.stockxpert;
    exports com.anani.stockxpert.Controllers;
    exports com.anani.stockxpert.Model;
    opens com.anani.stockxpert.Controllers to javafx.fxml;
    opens com.anani.stockxpert.Model to org.hibernate.orm.core;
}

