package tv.geir.vaadin.router;

import com.vaadin.ui.Component;

/**
 * Created by geir on 12/01/17.
 */
public interface StateView extends State {

    /**
     *
     * @param
     */
    void initState(Component view) ;
}
