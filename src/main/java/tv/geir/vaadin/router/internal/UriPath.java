package tv.geir.vaadin.router.internal;

import org.springframework.util.StringUtils;
import tv.geir.commons.Parameters;
import tv.geir.vaadin.router.UIRouterException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Created by geir on 19/01/17.
 */
public class UriPath {

    private String path;

    public UriPath(String path) {
        path = swapShortHandParameters(trimSlashes(path)) ;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isMatch(String path) {
        return match(path).isMatch();
    }

    public PathMatcher.PathMatchResult match(String path) {
        return matcher().match(path);
    }

    public PathMatcher matcher() {
        return new PathMatcher(this.getPath());
    }

    public String getParameterized(Parameters params) {
        Pattern paramFinderPattern = compile("\\(\\?\\<(\\w+)\\>.+\\)");
        Matcher matcher = paramFinderPattern.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String paramName = matcher.group(1);

            if (!params.containsKey(paramName)) {
                throw new UIRouterException("Cannot parameterize path " + path + ", missing parameter: " + paramName);
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(params.get(paramName)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    public static String trimSlashes(String path) {
        path = StringUtils.trimLeadingCharacter(path, '/');
        path = StringUtils.trimTrailingCharacter(path, '/');
        return path;
    }

    private String swapShortHandParameters(String path) {
        Pattern shortHandPattern = compile("\\%\\(((?:\\w|[:\\\\+\\{\\}\\,])+)\\)");
        Matcher matcher = shortHandPattern.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String[] text = matcher.group(1).split(":");

            String regex = "\\w+";
            if (text.length == 2) {
                regex = text[1];
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement("(?<" + text[0]+ ">"+regex+")"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
