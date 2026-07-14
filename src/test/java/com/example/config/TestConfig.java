package com.example.config;

/**
 * Central configuration constants.
 * Secrets are resolved from environment variables at runtime.
 * No hardcoded secrets — for local runs, set the env var in your IDE or use a .env file.
 *
 * <h3>Resolution order:</h3>
 * <ol>
 *   <li>Environment variable {@code REQRES_API_KEY} (CI / GitHub Actions)</li>
 *   <li>System property {@code REQRES_API_KEY} (loaded from .env by {@link EnvLoader})</li>
 *   <li>{@link IllegalStateException} — never falls back to a hardcoded value</li>
 * </ol>
 */
public final class TestConfig {

    private TestConfig() {}

    // --- REST APIs ---
    public static final String REQRES_BASE_URL          = "https://reqres.in/api";
    public static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";

    /**
     * Reads the ReqRes API key from environment variable or system property.
     */
    public static final String REQRES_API_KEY = resolveApiKey();

    private static String resolveApiKey() {
        // 1. Environment variable (CI / GitHub Actions)
        String value = System.getenv("REQRES_API_KEY");
        if (value != null && !value.isBlank()) {
            return value;
        }

        // 2. System property (loaded from .env by EnvLoader)
        value = System.getProperty("REQRES_API_KEY");
        if (value != null && !value.isBlank()) {
            return value;
        }

        // 3. No secret available
        throw new IllegalStateException(
            "REQRES_API_KEY is not set! " +
            "For CI: ensure the 'test' environment secret is configured on GitHub. " +
            "For local: copy .env.example to .env and fill in your key, " +
            "or set the REQRES_API_KEY environment variable in your IDE run config / terminal."
        );
    }

    // --- UI ---
    public static final String THE_INTERNET_BASE_URL = "https://the-internet.herokuapp.com";

    // --- Timeouts ---
    public static final int IMPLICIT_WAIT_SECONDS = 5;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 10;
}
