package com.example.config;

/**
 * Central configuration constants.
 * In a real project these would come from properties / env vars.
 */
public final class TestConfig {

    private TestConfig() {}

    // --- REST APIs ---
    public static final String REQRES_BASE_URL  = "https://reqres.in/api";
    public static final String JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";

    // --- UI ---
    public static final String THE_INTERNET_BASE_URL = "https://the-internet.herokuapp.com";

    // --- Timeouts ---
    public static final int IMPLICIT_WAIT_SECONDS = 5;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 10;
}
