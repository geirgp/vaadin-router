package tv.geir.vaadin.router.internal;

import tv.geir.vaadin.router.StateProvider;

import java.util.Optional;

public class StateBlueprint {
    private String name;
    private String path;
    private StateProvider provider;
    private String[] parameterNames;

    public StateBlueprint(String name, String path, StateProvider provider) {
        this.name = name;
        this.path = path;
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public StateProvider getProvider() {
        return provider;
    }

    public Optional<String> getParentStateName() {
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return Optional.of(name.substring(0, lastDot));
        }
        else return Optional.empty();
    }

}