package tv.geir.vaadin.router;

/**
 * Created by geir on 29/12/16.
 */
public interface State {

    /**
     * This state is navigated to.
     *
     * @param e
     */
    default void enter(StateChangeListener.StateChangeEvent e) {};
    default void leave(StateChangeListener.StateChangeEvent e) {};


    default void navigateTo(String stateName) {
        StateUI.get().getStateNavigator().navigateTo(stateName);
    }
}
