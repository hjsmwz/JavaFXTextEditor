module ETTT {
    requires javafx.fxml;
    requires javafx.controls;

    opens texteditor;
    opens texteditor.model;
    opens texteditor.model.dao;
    opens texteditor.viewcontroller;
}