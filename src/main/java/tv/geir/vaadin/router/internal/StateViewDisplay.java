package tv.geir.vaadin.router.internal;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import tv.geir.vaadin.router.FullSizeHorizontalLayout;
import tv.geir.vaadin.router.State;
import tv.geir.vaadin.router.StateNotComponentException;

/**
 * Displays a state in a container
 */
public class StateViewDisplay {


    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private ComponentContainer container;

    public StateViewDisplay(ComponentContainer container) {
        this.container = container;
    }

    public StateViewDisplay() {
        this.container = new FullSizeHorizontalLayout();
    }

    public void show(State state) {
        if (state instanceof Component) {
            container.removeAllComponents();
            container.addComponent((ComponentContainer) state);
        } else {
            throw new StateNotComponentException(state.getClass());
        }
    }

    public ComponentContainer getContainer() {
        return container;
    }

}
