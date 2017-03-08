package tv.geir.vaadin.router;

import tv.geir.vaadin.router.internal.StateBlueprint;

import java.util.Collection;

/**
 * Created by geir on 08/01/17.
 */
public interface StateProvider {

    public static class StateProviderException extends UIRouterException {
        public StateProviderException(String message, Throwable t) {
            super(message, t);
        }
    }

    State getState(String stateName);

    StateBlueprint getBlueprint(State right);

    Collection<StateBlueprint> getBluePrints();
}
