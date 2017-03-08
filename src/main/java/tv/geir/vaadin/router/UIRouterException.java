package tv.geir.vaadin.router;

/**
 * Created by geir on 11/01/17.
 */
public class UIRouterException extends RuntimeException {

    public UIRouterException() {
    }

    public UIRouterException(String message) {
        super(message);
    }

    public UIRouterException(String message, Throwable cause) {
        super(message, cause);
    }
}
