package texteditor;

import javafx.application.Platform;
import texteditor.model.DataToSaveToSpecifiedFile;
import texteditor.model.dao.EditorDAO;
import texteditor.viewcontroller.ChangeViewStateCallback;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Utilities {
    public static final ExecutorService THREAD_POOL =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static <T extends List<String>> void saveDataImpl(DataToSaveToSpecifiedFile dataToFile,
                                                             EditorDAO editorDAO,
                                                             Path pathToFile,
                                                             Main exposedMainClassInstanceForChangingStateTitle,
                                                             ChangeViewStateCallback changeViewStateCallback,
                                                             AtomicBoolean isSaved) {
        try {
            editorDAO.save(dataToFile);
            Platform.runLater(() -> {
                changeViewStateCallback.setSaveStatusLabelText(Constants.SAVED);
                setAppTitleName(pathToFile, exposedMainClassInstanceForChangingStateTitle);
            });
            isSaved.set(true);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> changeViewStateCallback.setSaveStatusLabelText(Constants.NOT_SAVED));
            isSaved.set(false);
        }
    }

    public static void setAppTitleName(Path path, Main exposedMainClassInstanceForChangingStateTitle) {
        exposedMainClassInstanceForChangingStateTitle.setAppTitle(path.getFileName().toFile().getName());
    }

    public static void exitApp(Main exposedMainClassInstanceForChangingStateTitle) {
        THREAD_POOL.shutdown();
        try {
            if (!THREAD_POOL.awaitTermination(Constants.closeTimeOut, Constants.TIME_UNIT)) {
                THREAD_POOL.shutdownNow();
                if (!THREAD_POOL.awaitTermination(Constants.closeTimeOut, Constants.TIME_UNIT)) {
                    // Log error......things have gone south all the way down to Antarctica
                    System.err.println("Things have gone horribly south"); // Log something here in debug configuration
                }
            }
        } catch (InterruptedException e) {
            THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
        exposedMainClassInstanceForChangingStateTitle.getStage().close();
        System.exit(0); // Make sure it's really closed
    }
}