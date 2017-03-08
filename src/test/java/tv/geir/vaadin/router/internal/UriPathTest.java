package tv.geir.vaadin.router.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tv.geir.commons.Parameters;
import tv.geir.vaadin.router.internal.UriPath;

import static org.junit.Assert.assertEquals;

/**
 * Created by geir on 19/01/17.
 */
@RunWith(JUnit4.class)
public class UriPathTest {

    @Test
    public void parameterize() throws Exception {

        String one = new UriPath("/one/(?<one>\\w+)/list")
                .getParameterized(new Parameters.Builder().put("one", "1").build());
        assertEquals("one/1/list", one);
    }
}
