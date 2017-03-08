package tv.geir.vaadin.router;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

/**
 * Created by geir on 16/01/17.
 */
public abstract class StateUI extends UI {

    private StateNavigator stateNavigator;

    private ComponentContainer view = new FullSizeHorizontalLayout();

    public StateUI() {
        stateNavigator = new StateNavigator(this);
    }

    public StateNavigator getStateNavigator() {
        return stateNavigator;
    }

    public static StateUI get() {
        return ((StateUI) UI.getCurrent());
    }

}
