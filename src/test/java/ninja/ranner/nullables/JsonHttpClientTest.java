package ninja.ranner.nullables;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonHttpClientTest {

    @Nested
    class ConfiguredResponses {

        @Test
        void returnsConfiguredResponseBody() {
            // language=JSON
            String configuredResponseBody = """
                    {
                        "socks": "off"
                    }
                    """;
            JsonHttpClient client = JsonHttpClient.createNull(c -> c
                    .respondWith(
                            "https://example.com",
                            configuredResponseBody));

            String response = client.get("https://example.com");

            assertThat(response)
                    .isEqualTo(configuredResponseBody);
        }

        @Test
        void throwsNotFoundForUrlsWithoutConfiguredResponses() {
            JsonHttpClient client = JsonHttpClient.createNull();

            assertThatThrownBy(() -> client.get("https://example.com/does-not-exist"))
                    .hasMessage("404 Not Found");
        }

        @Test
        @SuppressWarnings("Convert2MethodRef")
        void throwsTimeoutException() {
            JsonHttpClient client = JsonHttpClient.createNull(c -> c.timeout());

            assertThatThrownBy(() -> client.get("https://example.com"))
                    .hasMessage("Request timed out");
        }
    }

}