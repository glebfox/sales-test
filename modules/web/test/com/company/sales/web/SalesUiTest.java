package com.company.sales.web;

import org.junit.Ignore;
import org.junit.Test;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.openqa.selenium.By.xpath;

@Ignore
public class SalesUiTest {

    @Test
    public void createCustomer() {
        open("http://localhost:8080/app");

        // Click the 'Submit' button
        $(xpath("//*[@cuba-id='loginButton']")).shouldBe(visible).click();

        // Open the 'Shop' menu
        $(xpath("//*[@cuba-id='application-sales']")).shouldBe(visible).click();

        // Click the 'Customers' menu item
        $(xpath("//*[@cuba-id='sales$Customer.browse']")).shouldBe(visible).click();

        // Click the 'Create' button
        $(xpath("//*[@cuba-id='createBtn']")).shouldBe(visible).click();

        // Fill the fields
        $(xpath("//*[@cuba-id='name']")).shouldBe(visible).setValue("Alex");
        $(xpath("//*[@cuba-id='email']")).shouldBe(visible).setValue("alex@home.com");

        // Commit the Customer
        $(xpath("//*[@cuba-id='windowCommit']")).shouldBe(visible).click();

        // Remove the Customer
        $(xpath("//*[@cuba-id='removeBtn']")).shouldBe(visible).click();
        $(xpath("//*[@cuba-id='optionDialog_ok']")).shouldBe(visible).click();

        // Click the 'Logout' button
        $(xpath("//*[@cuba-id='logoutButton']")).shouldBe(visible).click();
    }
}