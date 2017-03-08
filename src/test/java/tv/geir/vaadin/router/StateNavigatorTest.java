package tv.geir.vaadin.router;

import com.vaadin.ui.UI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import tv.geir.vaadin.router.State;
import tv.geir.vaadin.router.StateNavigator;
import tv.geir.vaadin.router.StateProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class StateNavigatorTest {

    @Mock
    private UI ui;

    @Mock
    private StateProvider mockSP;

    @Mock
    private State mockState;

    @Test
    public void navigateTo_callsEnterState() {

//        StateNavigator s = new StateNavigator(ui);
//        when(mockSP.getState(any())).thenReturn(mockState);
//        s.addStateProvider(mockSP);
//        s.navigateTo("");
//        verify(mockSP).getState(any());
//        verify(mockState).enter(any());
    }


    @Test
    public void findStateNameByPath_NoParams() {
        StateNavigator nav = new StateNavigator(ui);

        assertTrue(nav.findStateNameByPath("").isPresent() == false);
        assertTrue(nav.findStateNameByPath("/").isPresent() == false);

        nav.addState(Tests.S1.class, "main", "");
        nav.addState(Tests.S2.class, "main.b", "/b");
        nav.addState(Tests.S3.class, "main.c", "/c");
        nav.addState(Tests.S4.class, "main.c.d", "/d");

        assertEquals("main", nav.findStateNameByPath("").get());
        assertEquals("main.b", nav.findStateNameByPath("/b").get());
        assertEquals("main.c", nav.findStateNameByPath("/c").get());
        assertEquals("main.c.d", nav.findStateNameByPath("/c/d").get());
    }

    public static class Tests {
        public abstract static class S1 implements State {}
        public abstract static class S2 implements State {}
        public abstract static class S3 implements State {}
        public abstract static class S4 implements State {}
    }

}
