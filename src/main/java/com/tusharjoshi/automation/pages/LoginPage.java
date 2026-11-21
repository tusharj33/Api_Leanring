package com.tusharjoshi.automation.pages;

import com.tusharjoshi.automation.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By flashMessage = By.id("flash");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get("https://the-internet.herokuapp.com/login");
        return this;
    }

    public LoginPage loginAs(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(loginButton);
        return this;
    }

    public String getMessage() {
        return getText(flashMessage);
    }
}