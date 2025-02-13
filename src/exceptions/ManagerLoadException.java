package exceptions;

import java.io.File;

public class ManagerLoadException extends RuntimeException {
    File file;

    public ManagerLoadException(String message, File file) {
        super(message);
        this.file = file;
    }

    public String getDetailMessage() {
        return String.format("%s - %s", getMessage(), file.getAbsolutePath());
    }
}
