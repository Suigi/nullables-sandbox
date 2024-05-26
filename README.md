# My Nullables Sandbox

This is a collection of experiments playing with [Nullable Infrastructure Wrappers](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#infrastructure-wrappers)
and other parts of James Shore's [Testing Without Mocks Pattern Language](https://www.jamesshore.com/v2/projects/nullables).

## High-Level Infrastructure Wrappers

[DailyQuoteClient](./src/main/java/ninja/ranner/nullables/DailyQuoteClient.java) is an instance of a high-level
infrastructure wrapper. It uses a low-level wrapper ([JsonHttpClient](./src/main/java/ninja/ranner/nullables/JsonHttpClient.java)) to 
talk to an imaginary _Daily Quote Web Service_.

In order to use the DailyQuoteClient in a test, it can be instantiated using
the `DailyQuoteClient.createNull()` static factory methods. 

There is a version 
of it that does not take any arguments, in case you need a dummy version of the 
client, that does not interfere with the rest of your test:

```java
@Test
void returnsDefaultQuoteIfNoQuoteIsConfigured() {
    DailyQuoteClient quoteClient = DailyQuoteClient.createNull();

    String quote = quoteClient.fetchQuote();

    assertThat(quote)
            .isEqualTo("""
                       Default Quote
                       - Unknown""");
}
```

I interpret this as a variation of James' [Parameterless Instantiation](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#instantiation).

The other version of `createNull` takes a configuration `Function` as a parameter. I like
setting up the client in a test using a lambda, like this:

```java
@Test
void returnsConfiguredQuote() {
    DailyQuoteClient quoteClient = DailyQuoteClient.createNull(c -> c
            .quote("Make many more much smaller steps.", "GeePaw Hill"));

    String quote = quoteClient.fetchQuote();

    assertThat(quote)
            .isEqualTo("""
                       Make many more much smaller steps.
                       - GeePaw Hill""");
}
```

### Transforming [Configurable Responses](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#configurable-responses)

In my (limited) experience it can take quite some effort to build 
a low-level Infrastructure Wrapper. Java needs some convincing sometimes for
setting up an [Embedded Stub](https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks#embedded-stub).
I suppose that's true for any statically typed language. Side note: I didn't 
actually implement a working HTTP infrastructure wrapper in this project. The 
`JsonHttpClient` can only serve configured responses.

However, once the low-level wrapper is done, it's very straight-forward
to build higher-level wrappers on top of it. The high-level `createNull` 
is implemented in the high-level terms of the application (e.g. configuring a quote).
Internally, it transforms the high-level response to a low-level one (e.g. returning an HTTP response for a specific URL):

```java
public class DailyQuoteClient {
    
    // ...
    
    public static class Config {
        
        // ...
        
        private JsonHttpClient toHttpClient() {
            if (throwTimeout) {
                return JsonHttpClient.createNull(c -> c
                    .timeout());
            }
            return JsonHttpClient.createNull(c -> c
                    .responseForUrl(
                            "https://example.com/quote",
                            new QuoteResponse(
                                    configuredQuote,
                                    configuredAuthor))
            );
        }
    }
}
```

