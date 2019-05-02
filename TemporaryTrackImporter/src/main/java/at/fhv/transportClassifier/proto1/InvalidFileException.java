package at.fhv.transportClassifier.proto1;

/**
 * Created by Johannes on 14.02.2017.
 */
public class InvalidFileException extends Exception{
    public InvalidFileException() {
    }

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFileException(Throwable cause) {
        super(cause);
    }

    public InvalidFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
