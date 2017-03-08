package tv.geir.vaadin.router;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComponentContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by geir on 17/01/17.
 */
public class SpringStateUI extends StateUI{

    private StateNavigator stateNavigator;

    private ComponentContainer view = new FullSizeHorizontalLayout();

    private Logger log = LoggerFactory.getLogger(SpringStateUI.class);

    private StateNavigator navigator;

    @Autowired
    private SpringStateProvider provider;

    @Override
    protected void init(VaadinRequest request) {
        navigator = new StateNavigator(this);
        navigator.addStateProvider(provider);
    }
}
