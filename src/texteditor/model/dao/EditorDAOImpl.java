package texteditor.model.dao;

import texteditor.Constants;
import texteditor.model.DataToSaveToSpecifiedFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

public class EditorDAOImpl implements EditorDAO {

    @Override
    public void save(DataToSaveToSpecifiedFile dataToSaveToSpecifiedFile) {
        List<String> lines = dataToSaveToSpecifiedFile.getFileContents();
        lines.forEach(line -> {
            try {
                Files.write(dataToSaveToSpecifiedFile.getFilePath(),
                        line.getBytes(Constants.UTF8_CHARSET),
                        StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Optional<List<String>> load(Path path) throws IOException {
        return Optional.of(Files.readAllLines(path, Constants.UTF8_CHARSET));
    }
}