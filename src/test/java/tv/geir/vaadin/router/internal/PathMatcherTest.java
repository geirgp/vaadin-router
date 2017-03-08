package tv.geir.vaadin.router.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import tv.geir.commons.Parameters;

import static org.junit.Assert.*;

/**
 * Created by geir on 16/01/17.
 */
@RunWith(JUnit4.class)
public class PathMatcherTest {

    private PathMatcher pathMatcher;

    @Test
    public void root() {
        pathMatcher = new PathMatcher("");

        assertMatch("", pathMatcher);
        assertMatch("/", pathMatcher);
        assertMatch(null, pathMatcher);
        assertNoMatch("*", pathMatcher);
        assertNoMatch("/a", pathMatcher);
    }

    @Test
    public void sub() {
        pathMatcher = new PathMatcher("/sub/");

        assertTrue(pathMatcher.match("/sub").isMatch());
        assertTrue(pathMatcher.match("/sub/").isMatch());
        assertTrue(pathMatcher.match("sub").isMatch());
        assertTrue(pathMatcher.match("sub/").isMatch());

        assertFalse(pathMatcher.match("sub/a").isMatch());
        assertFalse(pathMatcher.match("su").isMatch());
    }

    @Test
    public void oneParameter() {
        pathMatcher = new PathMatcher("/sub/(?<id>\\w+)");

        assertMatch("/sub/abc123", pathMatcher, new Parameters.Builder().put("id", "abc123").build());
        assertMatch("/sub/abc122/", pathMatcher, new Parameters.Builder().put("id", "abc122").build());
        assertMatch("sub/abc121/", pathMatcher, new Parameters.Builder().put("id", "abc121").build());
        assertMatch("sub/abc120", pathMatcher, new Parameters.Builder().put("id", "abc120").build());

        assertNoMatch("/sub/", pathMatcher);
        assertNoMatch("sub/", pathMatcher);
        assertNoMatch("/sub", pathMatcher);
        assertNoMatch("sub", pathMatcher);
        assertNoMatch("", pathMatcher);
    }

    @Test
    public void multipleParameters() {
        pathMatcher = new PathMatcher("/products/(?<productId>\\w+)/comments/(?<commentId>\\w+)");

        assertMatch("/products/1/comments/a", pathMatcher, new Parameters.Builder()
                .put("productId", "1")
                .put("commentId", "a")
                .build());

    }

    @Test
    public void shortHandParameters() {
        pathMatcher = new PathMatcher("/products/%(productId)/comments/%(commentId)");

        assertMatch("/products/1/comments/a", pathMatcher, new Parameters.Builder()
                .put("productId", "1")
                .put("commentId", "a")
                .build());

    }

    @Test
    public void shortHandParameters_withRegexNumber() {
        pathMatcher = new PathMatcher("/products/%(productId:\\d+)/comments/%(commentId)");

        assertMatch("/products/1/comments/a", pathMatcher, new Parameters.Builder()
                .put("productId", "1")
                .put("commentId", "a")
                .build());
        assertNoMatch("Should not match custom regex (number)", "/products/a/comments/b", pathMatcher);

    }

    @Test
    public void shortHandParameters_withRangedRegexNumber() {
        pathMatcher = new PathMatcher("/products/%(productId:\\d{1,2})/comments/%(commentId)");

        assertMatch("/products/1/comments/a", pathMatcher, new Parameters.Builder()
                .put("productId", "1")
                .put("commentId", "a")
                .build());
        assertNoMatch("3 digits should not match 2 digit regex", "/products/123/comments/b", pathMatcher);

    }


    public void assertMatch(String search, PathMatcher matcher) {
        assertMatch(search, matcher, new Parameters());
    }

    public void assertMatch(String search, PathMatcher matcher, Parameters expectedParameters) {
        PathMatcher.PathMatchResult result = matcher.match(search);
        assertTrue("'" + search + "' should match '" + matcher.getRegex() + "'", result.isMatch());
        assertEquals(expectedParameters, result.getParameters());

    }

    public void assertNoMatch(String search, PathMatcher matcher) {
        assertNoMatch("'" + search + "' should NOT match '" + matcher.getRegex() + "'", search, matcher);

    }

    public void assertNoMatch(String msg, String search, PathMatcher matcher) {
        PathMatcher.PathMatchResult result = matcher.match(search);
        assertFalse(msg, result.isMatch());

    }
}
