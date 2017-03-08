package tv.geir.vaadin.router.internal;

import tv.geir.commons.Parameters;

public class NavigationTarget {
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