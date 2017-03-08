package tv.geir.vaadin.router.test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import tv.geir.vaadin.router.StateChangeListener;
import tv.geir.vaadin.router.StateView;

/**
 * Created by geir on 08/01/17.
 */
public class StateTop extends VerticalLayout implements StateView {

    @Override
    public void initState(Component viewPort) {
        setSizeFull();

        Button button = new Button("Go to Main StateView");
        addComponent(viewPort);
        addComponent(button);
    }

    @Override
    public void enter(StateChangeListener.StateChangeEvent e) {

    }
}
