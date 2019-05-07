package texteditor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Constants {
    public static final long closeTimeOut = 2000;

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String OKAY = "OKAY";
    public static final String NOT_SAVED = "NOT SAVED";
    public static final String SAVED = "SAVED";
    public static final String CLOSE_WITHOUT_SAVE_ALERT_MESSAGE = "You have unsaved data. Are you sure you want to close without saving?";
    public static final String SAVE_EXIT = "Save&Exit";
    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
}