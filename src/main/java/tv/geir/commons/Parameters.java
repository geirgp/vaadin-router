package tv.geir.commons;

import java.util.HashMap;

/**
 * Created by geir on 08/01/17.
 */
public class Parameters extends HashMap<String, String> {

    public Parameters with(String key, String value) {
        put(key,value);
        return this;
    }

    public static class Builder {

        private final Parameters parameters;

        public Builder() {
            parameters = new Parameters();
        }

        public Builder put(String key, String val) {
            parameters.put(key,val);
            return this;
        }

        public Parameters build() {
            return parameters;
        }
    }
}
