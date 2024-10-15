package mobile.application.footcardz.service.exception;

public class AlreadyProcessedException extends RuntimeException {
    public AlreadyProcessedException(String msg) {
        super(msg);
    }
}
