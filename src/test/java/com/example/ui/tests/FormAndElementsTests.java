package com.example.ui.tests;

import com.example.ui.base.BaseUiTest;
import com.example.ui.pages.CheckboxesPage;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Selenium tests for checkboxes, dropdown, and add/remove elements
 * on https://the-internet.herokuapp.com
 */
public class FormAndElementsTests extends BaseUiTest {

    // ── Checkboxes ─────────────────────────────────────────────────────

    @Test(description = "Checkbox 1 is unchecked by default; can toggle")
    public void shouldToggleCheckboxes() {
        navigateTo("/checkboxes");
        CheckboxesPage page = new CheckboxesPage(driver);

        // Checkbox 1 (index 0) is unchecked by default
        assertThat(page.isChecked(0)).isFalse();
        assertThat(page.isChecked(1)).isTrue();           // Checkbox 2 is pre-checked

        // Toggle first checkbox
        page.toggleCheckbox(0);
        assertThat(page.isChecked(0)).isTrue();

        // Toggle it back
        page.toggleCheckbox(0);
        assertThat(page.isChecked(0)).isFalse();
    }

    // ── Add / Remove Elements ──────────────────────────────────────────

    @Test(description = "Add and then delete an element")
    public void shouldAddAndDeleteElement() {
        navigateTo("/add_remove_elements/");

        var addButton = driver.findElement(org.openqa.selenium.By.cssSelector("button[onclick='addElement()']"));
        addButton.click();
        addButton.click();
        addButton.click();

        var deleteButtons = driver.findElements(org.openqa.selenium.By.cssSelector("button.added-manually"));
        assertThat(deleteButtons).hasSize(3);

        // Delete the first one
        deleteButtons.get(0).click();
        deleteButtons = driver.findElements(org.openqa.selenium.By.cssSelector("button.added-manually"));
        assertThat(deleteButtons).hasSize(2);
    }

    // ── Dropdown ───────────────────────────────────────────────────────

    @Test(description = "Select option from dropdown")
    public void shouldSelectDropdownOption() {
        navigateTo("/dropdown");
        var dropdownEl = driver.findElement(org.openqa.selenium.By.id("dropdown"));
        var dropdown = new org.openqa.selenium.support.ui.Select(dropdownEl);

        // Default: "Please select an option"
        assertThat(dropdown.getFirstSelectedOption().getText())
                .isEqualTo("Please select an option");

        dropdown.selectByVisibleText("Option 1");
        assertThat(dropdown.getFirstSelectedOption().getText()).isEqualTo("Option 1");

        dropdown.selectByValue("2");
        assertThat(dropdown.getFirstSelectedOption().getText()).isEqualTo("Option 2");
    }

    // ── Dynamic Loading ────────────────────────────────────────────────

    @Test(description = "Wait for dynamically loaded element (explicit wait)")
    public void shouldWaitForDynamicElement() {
        navigateTo("/dynamic_loading/2");

        var startButton = driver.findElement(org.openqa.selenium.By.cssSelector("#start button"));
        startButton.click();

        // Explicit wait until "Hello World!" appears
        var finishText = wait.until(d ->
                d.findElement(org.openqa.selenium.By.id("finish")).getText());

        assertThat(finishText).isEqualTo("Hello World!");
    }

    // ── Context Menu (right-click → alert) ─────────────────────────────

    @Test(description = "Right-click context menu triggers JS alert")
    public void shouldHandleContextMenu() {
        navigateTo("/context_menu");

        var hotSpot = driver.findElement(org.openqa.selenium.By.id("hot-spot"));
        new org.openqa.selenium.interactions.Actions(driver)
                .contextClick(hotSpot)
                .perform();

        var alert = driver.switchTo().alert();
        assertThat(alert.getText()).isEqualTo("You selected a context menu");
        alert.accept();
    }
}
