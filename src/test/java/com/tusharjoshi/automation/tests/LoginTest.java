package com.tusharjoshi.automation.tests;

import com.tusharjoshi.automation.base.BaseTest;
import com.tusharjoshi.automation.core.DriverFactory;
import com.tusharjoshi.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(groups = "smoke")
    public void validLoginShowsSuccess() {
        String msg = new LoginPage(DriverFactory.getDriver())
                .open()
                .loginAs("tomsmith", "SuperSecretPassword!")
                .getMessage();
        Assert.assertTrue(msg.contains("You logged into a secure area!"));
    }

    @Test(groups = "regression")
    public void invalidLoginShowsError() {
        String msg = new LoginPage(DriverFactory.getDriver())
                .open()
                .loginAs("wronguser", "wrongpass")
                .getMessage();
        Assert.assertTrue(msg.contains("Your username is invalid!"));
    }
}