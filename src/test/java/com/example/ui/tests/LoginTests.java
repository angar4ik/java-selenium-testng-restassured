package com.example.ui.tests;

import com.example.ui.base.BaseUiTest;
import com.example.ui.pages.LoginPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium tests against https://the-internet.herokuapp.com/login
 */
public class LoginTests extends BaseUiTest {

    @Test(description = "Login with valid credentials")
    public void shouldLoginWithValidCredentials() {
        navigateTo("/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("tomsmith", "SuperSecretPassword!");

        String flash = loginPage.getFlashMessage();
        assertThat(flash).contains("You logged into a secure area!");
        assertThat(loginPage.isLogoutButtonDisplayed()).isTrue();
    }

    @Test(description = "Login with invalid credentials shows error")
    public void shouldShowErrorOnInvalidLogin() {
        navigateTo("/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("tomsmith", "wrong-password");

        String flash = loginPage.getFlashMessage();
        assertThat(flash).contains("Your password is invalid!");
    }

    @Test(description = "Login with blank username and password")
    public void shouldShowErrorOnEmptyCredentials() {
        navigateTo("/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.loginAs("", "");

        String flash = loginPage.getFlashMessage();
        assertThat(flash).contains("Your username is invalid!");
    }
}
