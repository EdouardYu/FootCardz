package mobile.application.footcardz.service.exception;

public class AlreadyUsedException extends RuntimeException {
    public AlreadyUsedException(String msg) {
        super(msg);
    }
}
