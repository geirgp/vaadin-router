package tv.geir.vaadin.router.test;

import com.vaadin.ui.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import tv.geir.vaadin.router.FullSizeHorizontalLayout;
import tv.geir.vaadin.router.State;
import tv.geir.vaadin.router.UrlState;

import static tv.geir.vaadin.router.test.States.*;

/**
 * Created by geir on 29/12/16.
 */


@Service
@Scope("prototype")
@UrlState(name = CONTACTS, url = "/")
public class Contacts extends FullSizeHorizontalLayout implements State {

    public Contacts() {
        addComponent(new Label("contacts"));
    }

    @Service
    @UrlState(name = CONTACTS_LIST, url = "/list")
    public static class ContactList implements State {

    }

    @Service
    @UrlState(name = CONTACTS_DETAIL, url = "/:id")
    public static class ContactDetail implements State {


    }
}