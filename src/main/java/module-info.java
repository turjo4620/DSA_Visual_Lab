module com.example.dsa_visual_lab {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    exports com.example.dsa_visual_lab;

    opens com.example.dsa_visual_lab to javafx.fxml;
    opens com.example.dsa_visual_lab.controller.home to javafx.fxml;

    opens com.example.dsa_visual_lab.controller.linear to javafx.fxml;

    opens com.example.dsa_visual_lab.controller.sorting to javafx.fxml;

    opens com.example.dsa_visual_lab.controller.BSTController to javafx.fxml;

    exports com.example.dsa_visual_lab.controller.dp;
    opens com.example.dsa_visual_lab.controller.dp to javafx.fxml;

    opens com.example.dsa_visual_lab.controller.graph to javafx.fxml;
}
