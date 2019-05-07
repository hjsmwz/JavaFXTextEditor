package texteditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import texteditor.model.dao.EditorDAOImpl;
import texteditor.viewcontroller.EditorController;

/**
 * @author Shankhadeep Ghoshal  (ghoshalshankhadeep@Hotmail.com)
 */
public class Main extends Application {
    private static final String APP_TITLE = "Text Editor";

    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resources/UI.fxml"));

        fxmlLoader.setControllerFactory(param -> new EditorController(new EditorDAOImpl(), this));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setTitle(APP_TITLE);
        primaryStage.show();
    }

    public void setAppTitle(String appTitle) {
        stage.setTitle(appTitle);
    }

    public Stage getStage() {
        return this.stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}