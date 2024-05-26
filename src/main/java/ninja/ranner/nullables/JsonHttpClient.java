package ninja.ranner.nullables;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JsonHttpClient {

    private final Config config;
    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // In a real Infrastructure Wrapper, I would pass the Config into the Embedded Stub
    // but since this wrapper does not actually make any HTTP calls, I pass in the Config directly.
    public JsonHttpClient(Config config) {
        this.config = config;
    }

    public <T> T get(String url, Class<T> testResponseClass) {
        // Please imagine a real HTTP call here
        String response = config.forUrl(url);
        try {
            return jsonMapper.readValue(response, testResponseClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse response:\n" + response, e);
        }
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

        public Config responseForUrl(String url, Object response) {
            try {
                this.responsesByUrl.put(url, jsonMapper.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Could not serialize configured response", e);
            }
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
