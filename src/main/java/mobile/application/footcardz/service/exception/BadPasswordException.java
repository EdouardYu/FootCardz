package mobile.application.footcardz.service.exception;

public class BadPasswordException extends RuntimeException {
    public BadPasswordException(String msg) {
        super(msg);
    }
}
