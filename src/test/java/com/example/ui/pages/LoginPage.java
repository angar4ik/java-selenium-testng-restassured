package com.example.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for https://the-internet.herokuapp.com/login
 */
public class LoginPage {

    private final WebDriver driver;

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public LoginPage enterUsername(String username) {
        usernameInput.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordInput.sendKeys(password);
        return this;
    }

    public void clickLogin() {
        loginButton.click();
    }

    /** Convenience: fill both fields and submit. */
    public void loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getFlashMessage() {
        return flashMessage.getText().trim();
    }

    public boolean isLogoutButtonDisplayed() {
        return logoutButton.isDisplayed();
    }

    public boolean isOnLoginPage() {
        return loginButton.isDisplayed();
    }
}
