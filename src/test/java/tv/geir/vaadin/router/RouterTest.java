package tv.geir.vaadin.router;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import tv.geir.vaadin.router.SpringStateProvider;
import tv.geir.vaadin.router.StateNavigator;
import tv.geir.vaadin.router.UrlState;
import tv.geir.vaadin.router.test.Contacts;
import tv.geir.vaadin.router.test.States;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by geir on 29/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@Ignore
public class RouterTest {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StateNavigator stateManager;

    @Autowired
    private SpringStateProvider viewProvider;




    @Test
    public void testIt() {

        String[] beanNamesForAnnotation1 = applicationContext.getBeanNamesForAnnotation(UrlState.class);

//        Map<String, Object> beanNamesForAnnotation = applicationContext.getBeansWithAnnotation(UrlState.class);
        assertEquals(3, beanNamesForAnnotation1.length);
        for (String beanName : beanNamesForAnnotation1
                ) {
            System.out.print( beanName + "::");
            System.out.println( applicationContext.getType(beanName));

        }

    }

    @Test
    public void getState() {
        assertTrue(viewProvider.getState(States.CONTACTS) instanceof Contacts);
        assertTrue(viewProvider.getState(States.CONTACTS_LIST) instanceof Contacts.ContactList);
        assertTrue(viewProvider.getState(States.CONTACTS_DETAIL) instanceof Contacts.ContactDetail);
    }

    @Configuration
    @ComponentScan(basePackages = "tv.geir.vaadin.router")
    public static class SpringConfig {

        @Autowired
        private ApplicationContext applicationContext;

        @Bean
        public StateNavigator stateManager(SpringStateProvider provider) {
            StateNavigator stateNavigator = new StateNavigator(null);
            stateNavigator.addStateProvider(provider);
            return stateNavigator;
        }

        @Bean
        public SpringStateProvider stateViewProvider() {
            return new SpringStateProvider(applicationContext);
        }

    }
}
