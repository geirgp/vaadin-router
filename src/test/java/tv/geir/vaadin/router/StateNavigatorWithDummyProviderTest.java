package tv.geir.vaadin.router;

import com.vaadin.ui.UI;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tv.geir.vaadin.router.State;
import tv.geir.vaadin.router.StateNavigator;
import tv.geir.vaadin.router.StateProvider;
import tv.geir.vaadin.router.internal.StateBlueprint;
import tv.geir.vaadin.router.test.States;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class StateNavigatorWithDummyProviderTest {

    @Mock
    private UI ui;


    @Mock
    private State contacts;
    @Mock
    private State contactsList;
    @Mock
    private State contactsDetail;

    @Test
    public void navigateToTopState() {

        // setup
        StateNavigator s = new StateNavigator(ui);
        s.addStateProvider(new Provider());

        // act
        s.navigateTo(States.CONTACTS);
        s.navigateTo(States.CONTACTS);

        // check
        InOrder inOrder = inOrder(contacts);
//        inOrder.verify(contacts, times(1)).(any());
        inOrder.verify(contacts, times(2)).enter(any());
    }

    @Test
    public void navigateToMiddleState() {

        // setup
        StateNavigator s = new StateNavigator(ui);
        s.addStateProvider(new Provider());

        // act
        s.navigateTo(States.CONTACTS_DETAIL);

        // check
        InOrder inOrder = inOrder(contacts, contactsDetail);
        // should always start from the top
//        inOrder.verify(contacts, times(1)).init(any());
//        inOrder.verify(contactsDetail, times(1)).init(any());
        inOrder.verify(contacts, times(1)).enter(any());
        inOrder.verify(contactsDetail, times(1)).enter(any());
    }

    class Provider implements StateProvider {

        public List<State> getStateHierarchy(String stateName) {

            return new HashMap<String, List<State>>() {{
                put(States.CONTACTS, Arrays.asList(contacts));
                put(States.CONTACTS_DETAIL, Arrays.asList(contacts, contactsDetail));
                put(States.CONTACTS_LIST, Arrays.asList(contacts, contactsList));
            }}
                    .get(stateName);
        }

        public Collection<String> getParameterNames(String stateName) {
            return new HashMap<String, Collection<String>>() {{
                put(States.CONTACTS, Arrays.asList());
                put(States.CONTACTS_DETAIL, Arrays.asList("contactId"));
                put(States.CONTACTS_LIST, Arrays.asList());
            }}
                    .get(stateName);
        }

        @Override
        public State getState(String stateName) {
            List<State> stateHierarchy = getStateHierarchy(stateName);
            if (stateHierarchy.size() > 0) {
                return stateHierarchy.get(stateHierarchy.size() - 1);
            }
            throw new RuntimeException("State not found exception " + stateName);
        }

        @Override
        public StateBlueprint getBlueprint(State right) {
            return null;
        }

        @Override
        public Collection<StateBlueprint> getBluePrints() {
            return null;
        }
    }


}
