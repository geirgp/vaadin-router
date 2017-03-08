package tv.geir.vaadin.router.internal;

import org.springframework.util.StringUtils;

public class SpringStateBlueprint {

    public String beanName;
    public Class type;
    public StateBlueprint blueprint;

    public StateBlueprint getBlueprint() {
        return blueprint;
    }

    public int getStateDepth() {
        return StringUtils.countOccurrencesOf(blueprint.getName(), ".") + 1;
    }
}