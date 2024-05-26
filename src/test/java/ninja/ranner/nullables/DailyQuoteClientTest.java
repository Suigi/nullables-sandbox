package ninja.ranner.nullables;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DailyQuoteClientTest {

    @Nested
    class ConfiguredResponses {

        @Test
        void returnsDefaultQuoteIfNoQuoteIsConfigured() {
            DailyQuoteClient quoteClient = DailyQuoteClient.createNull();

            String quote = quoteClient.fetchQuote();

            assertThat(quote)
                    .isEqualTo("""
                               Default Quote
                               - Unknown""");
        }

        @Test
        void returnsConfiguredQuote() {
            DailyQuoteClient quoteClient = DailyQuoteClient.createNull(c -> c
                    .quote(
                            "Make many more much smaller steps.",
                            "GeePaw Hill"));

            String quote = quoteClient.fetchQuote();

            assertThat(quote)
                    .isEqualTo("""
                            Make many more much smaller steps.
                            - GeePaw Hill""");
        }

        @Test
        @SuppressWarnings("Convert2MethodRef")
        void timesOutWhenConfigured() {
            DailyQuoteClient quoteClient = DailyQuoteClient.createNull(c -> c
                    .timeout());

            assertThatThrownBy(quoteClient::fetchQuote)
                    .hasMessage("Failed to fetch quote")
                    .hasRootCauseMessage("Request timed out");
        }
    }

}