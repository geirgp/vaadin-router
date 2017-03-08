package tv.geir.vaadin.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import tv.geir.vaadin.router.internal.SpringStateBlueprint;
import tv.geir.vaadin.router.internal.StateBlueprint;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringStateProvider implements StateProvider {

    private Logger log = LoggerFactory.getLogger(SpringStateProvider.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, SpringStateBlueprint> blueprintByStateName;


    public SpringStateProvider() {
        log.info("new");
    }

    public SpringStateProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    WeakHashMap<State, StateBlueprint> blueprintByState = new WeakHashMap<>();

    @PostConstruct
    private void loadApplicationStates() {
        blueprintByStateName = Stream.of(applicationContext.getBeanNamesForAnnotation(UrlState.class))
                .map(beanName -> {
                    SpringStateBlueprint i = new SpringStateBlueprint();
                    i.beanName = beanName;
                    i.type = applicationContext.getType(beanName);
                    i.blueprint = new StateBlueprint(
                            getClassAnnotationValue(i.type, UrlState.class, "name"),
                            getClassAnnotationValue(i.type, UrlState.class, "url"),
                            this
                    );
                    return i;
                })
                .collect(Collectors.toMap(i -> i.blueprint.getName(), Function.identity()));

        log.info("Found " + blueprintByStateName.size() + " Spring states");
        blueprintByStateName.keySet().stream().forEach(n -> log.info(n));
    }


    @Override
    public State getState(String stateName) {
        return Optional.ofNullable(blueprintByStateName.get(stateName))
                .map(stateInfo -> {
                    State state = (State) applicationContext.getBean(stateInfo.beanName);
                    blueprintByState.put(state, stateInfo.blueprint);
                    return state;
                })
                .map(State.class::cast)
                .orElseThrow(() -> new NullPointerException("cannot find state " + stateName));
    }

    @Override
    public StateBlueprint getBlueprint(State right) {
        return Optional.ofNullable(blueprintByState.get(right))
                .orElse(null);
    }

    @Override
    public Collection<StateBlueprint> getBluePrints() {
        return blueprintByStateName.values().stream()
                .map(springStateBlueprint -> springStateBlueprint.blueprint)
                .collect(Collectors.toList());
    }

    public String getClassAnnotationValue(Class classType, Class annotationType, String attributeName) {
        String value = null;

        Annotation annotation = classType.getAnnotation(annotationType);
        if (annotation != null) {
            try {
                value = (String) annotation.annotationType().getMethod(attributeName).invoke(annotation);
            } catch (Exception ex) {
            }
        }

        return value;
    }

}