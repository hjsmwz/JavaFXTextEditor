package texteditor.viewcontroller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import texteditor.Constants;
import texteditor.Main;
import texteditor.Utilities;
import texteditor.model.DataToSaveToSpecifiedFile;
import texteditor.model.dao.EditorDAO;
import texteditor.model.dao.EditorDAOImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditorController implements ChangeViewStateCallback {

    private static final AtomicBoolean isSaved = new AtomicBoolean(false);

    private Main exposedMainClassInstanceForChangingStateTitle;
    private EditorDAO editorDAO;
    private DataToSaveToSpecifiedFile currentDataToSaveToSpecifiedFile;
    private FileChooser fileChooser;

    private ChangeViewStateCallback changeViewStateCallback = this;
    private ChangeListener<String> changeListener =
            (observable, oldVale, newValue) -> handleTextChangeInTextArea(observable);

    private void handleTextChangeInTextArea(ObservableValue<? extends String> observable) {
        setSaveStatusLabelText(Constants.NOT_SAVED);
        isSaved.set(false);
        if (!(currentDataToSaveToSpecifiedFile == null || currentDataToSaveToSpecifiedFile.getFilePath() == null)) {
            if (!isSaved.get()) {
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        saveAfterTextChange(observable);
                        return null;
                    }
                };
                Utilities.THREAD_POOL.submit(task);
            }
        }
    }

    @FXML TextArea userInputTextArea;
    @FXML private Label saveStatusLabel;

    public EditorController(EditorDAO editorDAO, Main exposedMainClassInstanceForChangingStateTitle) {
        this.editorDAO = editorDAO;
        this.exposedMainClassInstanceForChangingStateTitle = exposedMainClassInstanceForChangingStateTitle;
        fileChooser = new FileChooser();
    }

    @FXML
    public void initialize() {
        saveStatusLabel.setText(Constants.OKAY);
        userInputTextArea.textProperty().addListener(changeListener);
        exposedMainClassInstanceForChangingStateTitle.getStage().setOnCloseRequest(this::handleCloseButton);
    }

    @FXML
    public void onSave() {
        List<String> dataToFile = Arrays.asList(userInputTextArea.getText().split(Constants.LINE_SEPARATOR));
        Path path;
        if (null == this.currentDataToSaveToSpecifiedFile || null == this.currentDataToSaveToSpecifiedFile.getFilePath()) {
            path = Optional.ofNullable(fileChooser.showSaveDialog(null))
                    .orElseThrow()
                    .toPath();
        } else {
            path = this.currentDataToSaveToSpecifiedFile.getFilePath();
        }
        this.currentDataToSaveToSpecifiedFile = new DataToSaveToSpecifiedFile(path, dataToFile);
        Utilities.THREAD_POOL.execute(() -> saveData(currentDataToSaveToSpecifiedFile));
    }

    @FXML
    public void onSaveAs() {
        List<String> dataToFile = Arrays.asList(userInputTextArea.getText().split(Constants.LINE_SEPARATOR));
        Path path = Optional.ofNullable(fileChooser.showSaveDialog(null))
                .orElseThrow()
                .toPath();
        this.currentDataToSaveToSpecifiedFile = new DataToSaveToSpecifiedFile(path, dataToFile);
        saveData(currentDataToSaveToSpecifiedFile);
    }

    @FXML
    public void onOpen() {
        try {
            Path path = Optional.of(fileChooser.showOpenDialog(null))
                    .orElseThrow(FileNotFoundException::new)
                    .toPath();
            List<String> dataFromFile =  ((EditorDAOImpl)editorDAO)
                    .load(path)
                    .orElseThrow(IOException::new);
            this.currentDataToSaveToSpecifiedFile = new DataToSaveToSpecifiedFile(path, dataFromFile);

            Utilities.setAppTitleName(path, exposedMainClassInstanceForChangingStateTitle);
            userInputTextArea.clear();
            dataFromFile.forEach(line -> userInputTextArea.appendText(line + Constants.LINE_SEPARATOR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSaveStatusLabelText(String text) {
        this.saveStatusLabel.setText(text);
    }

    private void saveData(DataToSaveToSpecifiedFile data) {
        Utilities.saveDataImpl(data,
                editorDAO,
                currentDataToSaveToSpecifiedFile.getFilePath(),
                exposedMainClassInstanceForChangingStateTitle,
                changeViewStateCallback,
                isSaved);
    }

    private void saveAfterTextChange(ObservableValue<? extends String> observable) {
        final var data = observable.getValue();
        List<String> updatedValue = Arrays.asList(data.split(Constants.LINE_SEPARATOR));
        this.currentDataToSaveToSpecifiedFile = new DataToSaveToSpecifiedFile(currentDataToSaveToSpecifiedFile.getFilePath(), updatedValue);

        saveData(currentDataToSaveToSpecifiedFile);
    }

    private void handleCloseButton(WindowEvent event) {
        if (!isSaved.get()) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    Constants.CLOSE_WITHOUT_SAVE_ALERT_MESSAGE,
                    makeButtonTypesForClosingAlertBox());

            alert.showAndWait();

            ButtonType buttonResult = alert.getResult();

            if (buttonResult == ButtonType.APPLY) { // This is the save and exit button click
                this.currentDataToSaveToSpecifiedFile = new DataToSaveToSpecifiedFile(this.currentDataToSaveToSpecifiedFile.getFilePath(),
                        Arrays.asList(userInputTextArea.getText().split(Constants.LINE_SEPARATOR)));

                saveData(this.currentDataToSaveToSpecifiedFile);
                Utilities.exitApp(exposedMainClassInstanceForChangingStateTitle);
            }
            if (buttonResult == ButtonType.YES) {
                Utilities.exitApp(exposedMainClassInstanceForChangingStateTitle);
            }
            if (buttonResult == ButtonType.NO) {
                if (event == null) { // This is the close button event from the File menu option
                    alert.close();
                } else {
                    alert.close();
                    event.consume();
                }
            }
        } else {
            Utilities.exitApp(exposedMainClassInstanceForChangingStateTitle);
        }
    }

    private ButtonType[] makeButtonTypesForClosingAlertBox() {
        return new ButtonType[]{new ButtonType(Constants.SAVE_EXIT, ButtonBar.ButtonData.APPLY),
                new ButtonType(Constants.YES, ButtonBar.ButtonData.YES),
                new ButtonType(Constants.NO, ButtonBar.ButtonData.NO)};
    }
}