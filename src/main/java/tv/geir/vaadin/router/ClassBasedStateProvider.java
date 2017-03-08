package tv.geir.vaadin.router;

import tv.geir.vaadin.router.internal.StateBlueprint;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by geir on 15/01/17.
 */
public class ClassBasedStateProvider implements StateProvider {

    private StateBlueprint blueprint;

    private Class<? extends State> clazz;

    public ClassBasedStateProvider(Class<? extends State> type, String name, String urlFragment, String... parameterNames) {
        blueprint = new StateBlueprint(name, urlFragment, this);
        clazz = type;
    }

    @Override
    public State getState(String stateName) {
        return blueprint.getName().equals(stateName) ? newInstance() : null;
    }

    private State newInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException| IllegalAccessException e) {
            throw new StateProviderException(e.getMessage(), e);
        }
    }

    @Override
    public StateBlueprint getBlueprint(State right) {
        return blueprint;
    }

    @Override
    public Collection<StateBlueprint> getBluePrints() {
        return Arrays.asList(blueprint);
    }
}
