package tv.geir.vaadin.router;

import com.vaadin.ui.Component;

/**
 * Created by geir on 14/01/17.
 */
public class StateNotComponentException extends StateConfigException {
    public StateNotComponentException(Class c) {
        super(c.getName() + " must implement " + Component.class.getName());
    }
}
