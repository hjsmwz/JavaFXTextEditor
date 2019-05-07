package texteditor.model.dao;

import texteditor.model.DataToSaveToSpecifiedFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public interface EditorDAO {
    void save(DataToSaveToSpecifiedFile dataToSaveToSpecifiedFile) throws IOException;
    Optional<?> load(Path path) throws IOException;
}