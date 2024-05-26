package ninja.ranner.nullables;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonHttpClientTest {

    @Nested
    class ConfiguredResponses {

        record TestResponse(String socks) {}

        @Test
        void returnsConfiguredResponseBody() {
            JsonHttpClient client = JsonHttpClient.createNull(c -> c
                    .responseForUrl(
                            "https://example.com",
                            new TestResponse("off")));

            TestResponse response = client.get("https://example.com", TestResponse.class);

            assertThat(response)
                    .isEqualTo(new TestResponse("off"));
        }

        @Test
        void throwsNotFoundForUrlsWithoutConfiguredResponses() {
            JsonHttpClient client = JsonHttpClient.createNull();

            assertThatThrownBy(() -> client.get("https://example.com/does-not-exist", TestResponse.class))
                    .hasMessage("404 Not Found");
        }

        @Test
        @SuppressWarnings("Convert2MethodRef")
        void throwsTimeoutException() {
            JsonHttpClient client = JsonHttpClient.createNull(c -> c.timeout());

            assertThatThrownBy(() -> client.get("https://example.com", TestResponse.class))
                    .hasMessage("Request timed out");
        }
    }

}