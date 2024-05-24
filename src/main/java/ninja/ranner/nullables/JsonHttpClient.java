package ninja.ranner.nullables;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JsonHttpClient {

    private final Config config;

    // In a real Infrastructure Wrapper, I would pass the Config into the Embedded Stub
    // but since this wrapper does not actually make any HTTP calls, I pass in the Config directly.
    public JsonHttpClient(Config config) {
        this.config = config;
    }

    public String get(String url) {
        // Please imagine a real HTTP call here
        return config.forUrl(url);
    }

    // ~~~ Configurable responses ~~~

    public static JsonHttpClient createNull() {
        return createNull(Function.identity());
    }

    public static JsonHttpClient createNull(Function<Config, Config> configure) {
        Config config = Config.apply(configure);
        return new JsonHttpClient(config);
    }

    public static class Config {

        private final Map<String, String> responsesByUrl = new HashMap<>();
        private boolean throwTimeout;

        public static Config apply(Function<Config, Config> configure) {
            return configure.apply(new Config());
        }

        public Config respondWith(String url, String resopnse) {
            this.responsesByUrl.put(url, resopnse);
            return this;
        }

        public Config timeout() {
            this.throwTimeout = true;
            return this;
        }

        private String forUrl(String url) {
            if (throwTimeout) {
                throw new RuntimeException("Request timed out");
            }
            String response = responsesByUrl.get(url);
            if (response == null) {
                throw new RuntimeException("404 Not Found");
            }
            return response;
        }
    }

}
