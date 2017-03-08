package tv.geir.vaadin.router;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by geir on 29/12/16.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UrlStates {
    UrlState[] states();
}


