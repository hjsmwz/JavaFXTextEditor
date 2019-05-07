package texteditor.model;

import java.nio.file.Path;
import java.util.List;

public class DataToSaveToSpecifiedFile {

    private Path filePath;
    private List<String> fileContents;

    public DataToSaveToSpecifiedFile(Path filePath, List<String> fileContents) {
        this.filePath = filePath;
        this.fileContents = fileContents;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public List<String> getFileContents() {
        return fileContents;
    }

    public void setFileContents(List<String> fileContents) {
        this.fileContents = fileContents;
    }
}