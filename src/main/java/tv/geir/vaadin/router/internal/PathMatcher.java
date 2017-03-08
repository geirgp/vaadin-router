package tv.geir.vaadin.router.internal;

import tv.geir.commons.Parameters;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created by geir on 16/01/17.
 */
public class PathMatcher {

    private final Pattern pattern;
    private final UriPath path;
    private final Set<String> namedGroupCandidates;


    public PathMatcher(String path) {
        this(new UriPath(path));
    }

    public PathMatcher(UriPath path) {
        this.path = path;
        this.pattern = compile( "^" + path.getPath() + "$");

        namedGroupCandidates = getNamedGroupCandidates(path.getPath());
    }

    public PathMatchResult match(String actualPath) {

        actualPath = actualPath != null ? actualPath : "";
        Matcher matcher = pattern.matcher(UriPath.trimSlashes(actualPath));
        if (matcher.matches()) {
            Parameters parameters = new Parameters();
            for (String paramName : namedGroupCandidates) {
                String paramValue = matcher.group(paramName);
                if (paramValue != null) {
                    parameters.put(paramName, paramValue);
                }
            }
            return new PathMatchResult(actualPath, true, parameters);
        }
        return new PathMatchResult(actualPath, false);
    }

    private Set<String> getNamedGroupCandidates(String regex) {
        Set<String> namedGroups = new TreeSet<>();

        Matcher m = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>").matcher(regex);

        while (m.find()) {
            namedGroups.add(m.group(1));
        }

        return namedGroups;
    }

    public String getRegex() {
        return path.getPath();
    }

    public static class PathMatchResult {
        private String path;
        private boolean match;
        private Parameters parameters;

        public PathMatchResult(String path, boolean match) {
            this.match = match;
            this.path = path;
        }

        public PathMatchResult(String path, boolean match, Parameters parameters) {
            this.path = path;
            this.match = match;
            this.parameters = parameters;
        }

        public String getPath() {
            return path;
        }

        public boolean isMatch() {
            return match;
        }

        public Parameters getParameters() {
            return parameters;
        }
    }

}
