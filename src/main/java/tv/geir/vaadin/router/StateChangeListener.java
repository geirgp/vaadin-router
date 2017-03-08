package tv.geir.vaadin.router;

import tv.geir.commons.Parameters;
import tv.geir.vaadin.router.internal.StateInstance;

import java.io.Serializable;
import java.util.EventObject;
import java.util.List;
import java.util.Optional;

/**
 * Created by geir on 29/12/16.
 */
public interface StateChangeListener extends Serializable {

    /**
     * Event received by the listener for attempted and executed view changes.
     */
    public static class StateChangeEvent extends EventObject {
        private final List<StateInstance> oldStates;
        private final List<StateInstance> newStates;
        private final Parameters parameters;

        /**
         * Create a new view change event.
         *
         * @param stateManager
         *            Navigator that triggered the event, not null
         */
        public StateChangeEvent(StateNavigator stateManager, List<StateInstance> oldStates, List<StateInstance> newStates,
                                 Parameters  parameters) {
            super(stateManager);
            this.oldStates = oldStates;
            this.newStates = newStates;
            this.parameters = parameters;
        }

        /**
         * Returns the navigator that triggered this event.
         *
         * @return Navigator (not null)
         */
        public StateNavigator getStateManager() {
            return (StateNavigator) getSource();
        }

        /**
         * Returns the view being deactivated.
         *
         * @return old State
         */
        public List<StateInstance> getOldStates() {
            return oldStates;
        }

        /**
         * Returns the view being activated.
         *
         * @return new State
         */
        public List<StateInstance> getNewStates() {
            return newStates;
        }

        public StateInstance getNewState() {
            return newStates.get(newStates.size()-1);
        }

        public Optional<StateInstance> getOldState() {
            return oldStates.stream().findFirst();
        }

        public String getNewStateName() {
            return getNewState().getBlueprint().getName();
        }


        /**
         * Returns the parameters for the view being activated.
         *
         * @return navigation parameters (potentially bookmarkable) for the new
         *         view
         */
        public Parameters getParameters() {
            return parameters;
        }

    }

    /**
     * Invoked before the view is changed.
     * <p>
     * This method may e.g. open a "save" dialog or question about the change,
     * which may re-initiate the navigation operation after user action.
     * <p>
     * If this listener does not want to block the view change (e.g. does not
     * know the view in question), it should return true. If any listener
     * returns false, the view change is not allowed and
     * <code>afterViewChange()</code> methods are not called.
     *
     * @param event
     *            view change event
     * @return true if the view change should be allowed or this listener does
     *         not care about the view change, false to block the change
     */
    public boolean beforeViewChange(StateChangeEvent event);

    /**
     * Invoked after the view is changed. If a <code>beforeViewChange</code>
     * method blocked the view change, this method is not called. Be careful of
     * unbounded recursion if you decide to change the view again in the
     * listener.
     *
     * @param event
     *            view change event
     */
    public void afterViewChange(StateChangeEvent event);
}