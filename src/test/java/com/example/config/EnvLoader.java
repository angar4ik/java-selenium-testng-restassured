package com.example.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Minimal .env file loader.
 * Reads key=value pairs from {@code .env} and sets them as system properties
 * only if the environment variable is not already set (so CI takes priority).
 *
 * Usage — call once at startup, e.g. from a @BeforeSuite listener or base class:
 * <pre>{@code
 *   EnvLoader.loadIfPresent();
 * }</pre>
 */
public final class EnvLoader {

    private static final Path DOT_ENV = Paths.get(".env").toAbsolutePath();

    private EnvLoader() {}

    /**
     * Loads {@code .env} file if it exists and env vars are not already set.
     * Call early in test setup (e.g. {@code @BeforeSuite}) for local runs.
     */
    public static void loadIfPresent() {
        if (!Files.exists(DOT_ENV)) {
            return; // not a local dev environment — rely on CI env vars
        }

        try (Stream<String> lines = Files.lines(DOT_ENV)) {
            lines
                .map(String::trim)
                .filter(line -> !line.startsWith("#") && line.contains("="))
                .forEach(EnvLoader::setIfMissing);
        } catch (IOException e) {
            System.err.println("[EnvLoader] Could not read .env: " + e.getMessage());
        }
    }

    private static void setIfMissing(String line) {
        int eq = line.indexOf('=');
        String key = line.substring(0, eq).trim();
        String value = line.substring(eq + 1).trim();

        // Strip surrounding quotes if present (e.g. KEY="value")
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        // Environment variable takes precedence over .env
        if (System.getenv(key) == null) {
            System.setProperty(key, value);
        }
    }
}
