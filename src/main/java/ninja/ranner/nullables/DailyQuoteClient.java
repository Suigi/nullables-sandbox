package ninja.ranner.nullables;

import java.util.function.Function;

public class DailyQuoteClient {

    private final JsonHttpClient jsonHttpClient;

    public DailyQuoteClient(JsonHttpClient jsonHttpClient) {
        this.jsonHttpClient = jsonHttpClient;
    }

    public String fetchQuote() {
        try {
            QuoteResponse quoteResponse = jsonHttpClient.get("https://example.com/quote", QuoteResponse.class);
            return "%s\n- %s".formatted(quoteResponse.message(), quoteResponse.author());
        } catch(Exception e) {
            throw new RuntimeException("Failed to fetch quote", e);
        }
    }

    public record QuoteResponse(String message, String author) {}

    // ~~~ Configurable responses ~~~

    public static DailyQuoteClient createNull() {
        return createNull(Function.identity());
    }

    public static DailyQuoteClient createNull(Function<Config, Config> configure) {
        Config config = configure.apply(new Config());
        return new DailyQuoteClient(config.toHttpClient());
    }

    public static class Config {

        private String configuredQuote = "Default Quote";
        private String configuredAuthor = "Unknown";
        private boolean throwTimeout;

        public Config quote(String configuredQuote, String author) {
            this.configuredQuote = configuredQuote;
            this.configuredAuthor = author;
            return this;
        }

        public Config timeout() {
            this.throwTimeout = true;
            return this;
        }

        @SuppressWarnings("Convert2MethodRef")
        private JsonHttpClient toHttpClient() {
            if (throwTimeout) {
                return JsonHttpClient.createNull(c -> c.timeout());
            }
            return JsonHttpClient.createNull(c -> c
                    .responseForUrl(
                            "https://example.com/quote",
                            new QuoteResponse(configuredQuote, configuredAuthor))
            );
        }
    }
}
