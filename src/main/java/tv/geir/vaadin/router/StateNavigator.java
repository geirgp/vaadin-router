package tv.geir.vaadin.router;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tv.geir.commons.Pair;
import tv.geir.commons.Parameters;
import tv.geir.vaadin.router.internal.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by geir on 29/12/16.
 */
public class StateNavigator {

    private Logger log = LoggerFactory.getLogger(StateNavigator.class);
    private UriFragmentManager uriManager;

    public static class StateDisplayException extends UIRouterException {
        public StateDisplayException(String message) {
            super(message);
        }
    }

    public static class MissingViewException extends StateDisplayException {
        public MissingViewException(String state) {
            super("Parent state has no view to display " + state);
        }
    }

    private UI ui;
    private List<StateInstance> currentStateInstances = Collections.emptyList();
    private StateViewDisplay rootDisplay;
    private List<StateChangeListener> listeners = new LinkedList<>();
    private List<StateProvider> stateProviders = new CopyOnWriteArrayList<>();

    private Parameters parameters;


    public StateNavigator(UI ui) {
        if (ui == null) {
            throw new UIRouterException("ui is null");
        }
        StateViewDisplay stateViewDisplay = new StateViewDisplay();
        ui.setContent(stateViewDisplay.getContainer());
        init(ui, stateViewDisplay);

    }

    public StateNavigator(UI ui, ComponentContainer view) {
        init(ui, new StateViewDisplay(view));
    }

    private void init(UI ui, StateViewDisplay display) {
        this.rootDisplay = display;
        this.ui = ui;
        this.uriManager = new UriFragmentManager(ui.getPage());
        uriManager.setNavigator(this);
    }

    public void addState(Class<? extends State> type, String name, String urlFragment, String... parameterNames) {
        addStateProvider(new ClassBasedStateProvider(type, name, urlFragment, parameterNames));
    }

    public void addStateProvider(StateProvider provider) {
        stateProviders.add(provider);
    }


    public void navigateTo(String newStateName) {
        navigateTo(newStateName, new Parameters());
    }


    public void navigateTo(String newStateName, Parameters parameters) {
        log.info("Navigating to " + newStateName);
        // Produce a list of all stateName's parent states
        List<String> stateNames = new ArrayList<>();
        for (String stateFragment : newStateName.split("\\.")) {
            String prefix = "";
            if (stateNames.size() > 0) {
                prefix = stateNames.get(stateNames.size() - 1) + ".";
            }
            stateNames.add(prefix + stateFragment);
        }

        // get all levels of state instances
        List<StateInstance> nextStateInstances = stateNames.stream()
                // map state name to state instance
                .map(stateName -> getStateInstance(stateName, parameters))
                // remove all no-match
                .flatMap(o -> o.map(Stream::of).orElse(Stream.empty()))
                // sort by name length
                .sorted(Comparator.comparingInt(si -> si.getBlueprint().getName().length()))
                .collect(tv.geir.commons.Collectors.toImmutableList());

        // validate that target state exists
        nextStateInstances.parallelStream()
                .filter(instance -> newStateName.equals(instance.getBlueprint().getName()))
                .findFirst()
                .orElseThrow(() -> new StateNotFoundException(newStateName));


        StateChangeListener.StateChangeEvent event = new StateChangeListener.StateChangeEvent(
                this,
                this.currentStateInstances,
                nextStateInstances,
                parameters
        );

        boolean navigationAllowed = beforeViewChange(event);
        if (!navigationAllowed) {
            // Revert URL to previous state if back-button navigation
            // was canceled
            revertNavigation();
            return;
        }

        updateNavigationState(event);

        // show states from top to bottom
        switchStates(event);

        fireAfterViewChange(event);


    }

    public String getStateUri() {
        return uriManager.getState();
    }

    private void updateNavigationState(StateChangeListener.StateChangeEvent event) {

        String oldPath = event.getOldState().map(this::getParameterizedPath).orElse(null);
        String newPath = getParameterizedPath(event.getNewState());
        if (!newPath.equals(uriManager.getState())) {
            uriManager.setState(newPath);
        }
        this.currentStateInstances = event.getNewStates();
    }

    private String getParameterizedPath(StateInstance state) {
        return getPathTemplate(state.getBlueprint(), getBlueprintByStateNameMap())
                .getParameterized(state.getParameters());

    }

    public void navigateToPath(String path) {
        if (path == null) {
            log.warn("navigate to path 'null'");
            path = "";
        }
        final String targetPath = path;

        getNavigationTarget(path)
                .map(t -> {
                    navigateTo(t.getBlueprint().getName(), t.getParameters());
                    return t;
                })
                .orElseThrow(() -> new UIRouterException("Unknown path: " + targetPath));
    }


    public Optional<String> findStateNameByPath(String path) {
        return getNavigationTarget(path).map(p -> p.getBlueprint().getName());
    }

    public static class NavigationTarget {
        private StateBlueprint blueprint;
        private Parameters parameters;

        public NavigationTarget(StateBlueprint blueprint, Parameters parameters) {
            this.blueprint = blueprint;
            this.parameters = parameters;
        }

        public StateBlueprint getBlueprint() {
            return blueprint;
        }

        public Parameters getParameters() {
            return parameters;
        }

    }

    public Optional<NavigationTarget> getNavigationTarget(String path) {
        Map<String, StateBlueprint> bluePrintByStateName = getBlueprintByStateNameMap();

        return bluePrintByStateName.entrySet().stream()
                // check against known
                .map(e -> new Pair<StateBlueprint, PathMatcher.PathMatchResult>(
                                e.getValue(),
                                new PathMatcher(getPathTemplate(e.getValue(), bluePrintByStateName)).match(path)
                        )
                )
                .filter(pair -> pair.right.isMatch())
                .map(p -> new NavigationTarget(p.left, p.right.getParameters()))
                .findFirst();
    }

    private Map<String, StateBlueprint> getBlueprintByStateNameMap() {
        return stateProviders.stream()
                .flatMap(providers -> providers.getBluePrints().stream())
                .collect(java.util.stream.Collectors.toMap(StateBlueprint::getName, Function.identity()));
    }


    public UriPath getPathTemplate(StateBlueprint blueprint, Map<String, StateBlueprint> bluePrints) {
        return blueprint.getParentStateName()
                .map(parentName -> bluePrints.get(parentName).getPath() + blueprint.getPath())
                .map(UriPath::new)
                .orElse(new UriPath(blueprint.getPath()));
    }

    private void switchStates(StateChangeListener.StateChangeEvent evt) {
        Optional<StateViewDisplay> stateDisplay = Optional.of(rootDisplay);

        for (StateInstance instance : evt.getNewStates()) {
            if (stateDisplay.isPresent()) {

                stateDisplay.get().show(instance.getState());
                instance.getState().enter(evt);
                stateDisplay = instance.getStateViewDisplay();
            } else {
                throw new MissingViewException(instance.getBlueprint().getName());
            }
        }
    }

    private Optional<StateInstance> getStateInstance(String stateName, Parameters parameters) {
        return stateProviders.parallelStream()
                // Create a StaceInstance if provider returns a state
                .map(provider -> Optional.ofNullable(provider.getState(stateName))
                        .map(state -> new StateInstance(
                                state,
                                provider.getBlueprint(state),
                                parameters,
                                getViewDisplay(state)
                        ))
                        .orElse(null))
                // remove all no-match
                .filter(Objects::nonNull)
                .peek(StateInstance::initializeView)
                // use the first StaceInstance todo: error handling for +1 match?
                .findFirst();
    }

    private WeakHashMap<State, StateViewDisplay> viewDisplays = new WeakHashMap<>();

    private StateViewDisplay getViewDisplay(State state) {
        StateViewDisplay display = viewDisplays.get(state);

        if (display == null && state instanceof StateView) {
            display = new StateViewDisplay();
            viewDisplays.put(state, display);
        }
        return display;
    }


    /**
     * Check whether view change is allowed by view change listeners (
     * {@link ViewChangeListener#beforeViewChange(ViewChangeListener.ViewChangeEvent)}).
     * <p>
     * This method can be overridden to extend the behavior, and should not be
     * called directly except by {@link #(com.vaadin.navigator.View, String, String)}.
     *
     * @param event the event to fire as the before view change event
     * @return true if view change is allowed
     * @since 7.6
     */
    protected boolean beforeViewChange(StateChangeListener.StateChangeEvent event) {
        return fireBeforeViewChange(event);
    }

    /**
     * Revert the changes to the navigation state. When navigation fails, this
     * method can be called by {@link #(com.vaadin.navigator.View, String, String)} to
     * revert the URL fragment to point to the previous view to which navigation
     * succeeded.
     * <p>
     * This method should only be called by
     * {@link #(com.vaadin.navigator.View, String, String)}. Normally it should not be
     * overridden, but can be by frameworks that need to hook into view change
     * cancellations of this type.
     *
     * @since 7.6
     */
    protected void revertNavigation() {
//        if (currentNavigationState != null) {
//            getStateManager().setState(currentNavigationState);
//        }
    }

    /**
     * Fires an event before an imminent view change.
     * <p>
     * Listeners are called in registration order. If any listener returns
     * <code>false</code>, the rest of the listeners are not called and the view
     * change is blocked.
     * <p>
     * The view change listeners may also e.g. open a warning or question dialog
     * and save the parameters to re-initiate the navigation operation upon user
     * action.
     *
     * @param event view change event (not null, view change not yet performed)
     * @return true if the view change should be allowed, false to silently
     * block the navigation operation
     */
    protected boolean fireBeforeViewChange(StateChangeListener.StateChangeEvent event) {
        // a copy of the listener list is needed to avoid
        // ConcurrentModificationException as a listener can add/remove
        // listeners
        for (StateChangeListener l : new ArrayList<StateChangeListener>(listeners)) {
            if (!l.beforeViewChange(event)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fires an event before an imminent view change.
     * <p>
     * Listeners are called in registration order. If any listener returns
     * <code>false</code>, the rest of the listeners are not called and the view
     * change is blocked.
     * <p>
     * The view change listeners may also e.g. open a warning or question dialog
     * and save the parameters to re-initiate the navigation operation upon user
     * action.
     *
     * @param event view change event (not null, view change not yet performed)
     * @return true if the view change should be allowed, false to silently
     * block the navigation operation
     */
    protected void fireAfterViewChange(StateChangeListener.StateChangeEvent event) {
        // a copy of the listener list is needed to avoid
        // ConcurrentModificationException as a listener can add/remove
        // listeners
        for (StateChangeListener l : new ArrayList<StateChangeListener>(listeners)) {
            l.afterViewChange(event);
        }
    }


    public static class UriFragmentManager implements Page.UriFragmentChangedListener {
        private Logger log = LoggerFactory.getLogger(UriFragmentManager.class);
        private final Page page;
        private StateNavigator navigator;

        public UriFragmentManager(Page page) {
            this.page = page;
        }


        public String getState() {

            String fragment = this.getFragment();


            return fragment != null ? UriPath.trimSlashes(fragment) : "";
        }

        public void setState(String state) {

            this.setFragment(state);
        }

        public void setNavigator(StateNavigator navigator) {
            if (this.navigator == null && navigator != null) {
                this.page.addUriFragmentChangedListener(this);
            } else if (this.navigator != null && navigator == null) {
                this.page.removeUriFragmentChangedListener(this);
            }

            this.navigator = navigator;
        }

        public void uriFragmentChanged(Page.UriFragmentChangedEvent event) {
            log.info("uriFragmentChanged " + getState());
            this.navigator.navigateToPath(this.getState());
        }

        protected String getFragment() {
            return this.page.getUriFragment();
        }

        protected void setFragment(String fragment) {
            this.page.setUriFragment(fragment, false);
        }
    }


    public static class StateNotFoundException extends StateNavigationException {
        public StateNotFoundException(String message) {
            super(message);
        }
    }


    public static class StateNavigationException extends RuntimeException {
        public StateNavigationException() {
        }

        public StateNavigationException(String message) {
            super(message);
        }

        public StateNavigationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}