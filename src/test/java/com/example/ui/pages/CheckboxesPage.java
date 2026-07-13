package com.example.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for https://the-internet.herokuapp.com/checkboxes
 */
public class CheckboxesPage {

    private final WebDriver driver;

    @FindBy(css = "form#checkboxes input[type='checkbox']")
    private List<WebElement> checkboxes;

    public CheckboxesPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public List<WebElement> getCheckboxes() {
        return checkboxes;
    }

    public boolean isChecked(int index) {
        return checkboxes.get(index).isSelected();
    }

    public void toggleCheckbox(int index) {
        checkboxes.get(index).click();
    }
}

/**
 * Page Object for https://the-internet.herokuapp.com/dropdown
 */
class DropdownPage {

    private final WebDriver driver;

    @FindBy(id = "dropdown")
    private WebElement dropdown;

    public DropdownPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public Select getDropdown() {
        return new Select(dropdown);
    }

    public String getSelectedOption() {
        return new Select(dropdown).getFirstSelectedOption().getText();
    }

    public void selectByValue(String value) {
        new Select(dropdown).selectByValue(value);
    }

    public void selectByVisibleText(String text) {
        new Select(dropdown).selectByVisibleText(text);
    }
}
