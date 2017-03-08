package tv.geir.vaadin.router.internal;

import tv.geir.commons.Parameters;
import tv.geir.vaadin.router.State;
import tv.geir.vaadin.router.StateView;

import java.util.Optional;

/**
 * Runtime context for a state
 */
public class StateInstance {

    /**
     * The state instance
     */
    private State state;


    private NavigationTarget navigationTarget;

    /**
     * Displays state's view (child) when state is a StateView instance
     */
    private Optional<StateViewDisplay> stateViewDisplay;

    public StateInstance(State state, StateBlueprint blueprint, Parameters parameters, StateViewDisplay stateViewDisplay) {
        this.state = state;
        this.navigationTarget = new NavigationTarget(blueprint, parameters);
        if (state instanceof StateView) {
            this.stateViewDisplay = Optional.of(stateViewDisplay);
        } else {
            this.stateViewDisplay = Optional.empty();
        }
    }

    public void initializeView() {
        stateViewDisplay.ifPresent(display -> {
            if (display.isInitialized()) {
                return;
            }
            display.setInitialized(true);
            ((StateView) state).initState(display.getContainer());
        });
    }

    public State getState() {
        return state;
    }

    public StateBlueprint getBlueprint() {
        return navigationTarget.getBlueprint();
    }

    public Optional<StateViewDisplay> getStateViewDisplay() {
        return stateViewDisplay;
    }

    public Parameters getParameters() {
        return navigationTarget.getParameters();
    }


}