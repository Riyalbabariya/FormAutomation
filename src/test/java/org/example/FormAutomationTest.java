package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class FormAutomationTest {

    WebDriver driver;
    WebDriverWait wait;
    TextBoxPage textBoxPage;

    @BeforeMethod
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        textBoxPage = new TextBoxPage(driver, wait);
        System.out.println("Browser launched and setup done.");
    }

    @Test(dataProvider = "userData")
    public void testTextBoxForm(String fullName, String email, String currentAddress, String permanentAddress) {
        driver.get("https://demoqa.com/text-box");
        System.out.println("Navigated to text box form.");

        textBoxPage.enterFullName(fullName);
        textBoxPage.enterEmail(email);
        textBoxPage.enterCurrentAddress(currentAddress);
        textBoxPage.enterPermanentAddress(permanentAddress);

        if (textBoxPage.areAllFieldsFilled()) {
            textBoxPage.clickSubmit();

            Assert.assertTrue(textBoxPage.isOutputDisplayed(), "Output section is not displayed.");

            String outputText = textBoxPage.getOutputText();
            Assert.assertTrue(outputText.contains(fullName), "Output does not contain the full name.");
            Assert.assertTrue(outputText.contains(email), "Output does not contain the email.");
            System.out.println("Form submitted and verified successfully.");
        } else {
            System.out.println("One or more input fields are empty. Submission skipped.");
            Assert.fail("Form submission failed: One or more required fields are empty.");
        }
    }

    @DataProvider(name = "userData")
    public Object[][] getUserData() {
        return new Object[][] {
                {"Riyal Babariya", "riyal@example.com", "123 Current Street", "456 Permanent Ave"},
                {"Test User", "testuser@example.com", "789 Current Blvd", "101 Permanent Rd"},
                {"", "empty@example.com", "", "Some Address"}  // This will fail submission check
        };
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }

    static class TextBoxPage {
        WebDriver driver;
        WebDriverWait wait;

        By fullNameInput = By.id("userName");
        By emailInput = By.id("userEmail");
        By currentAddressInput = By.id("currentAddress");
        By permanentAddressInput = By.id("permanentAddress");
        By submitBtn = By.id("submit");
        By outputDiv = By.id("output");

        public TextBoxPage(WebDriver driver, WebDriverWait wait) {
            this.driver = driver;
            this.wait = wait;
        }

        public void enterFullName(String name) {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(fullNameInput));
            element.clear();
            element.sendKeys(name);
        }

        public void enterEmail(String email) {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
            element.clear();
            element.sendKeys(email);
        }

        public void enterCurrentAddress(String address) {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(currentAddressInput));
            element.clear();
            element.sendKeys(address);
        }

        public void enterPermanentAddress(String address) {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(permanentAddressInput));
            element.clear();
            element.sendKeys(address);
        }

        public void clickSubmit() {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            btn.click();
        }

        public boolean isOutputDisplayed() {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(outputDiv)).isDisplayed();
        }

        public String getOutputText() {
            return driver.findElement(outputDiv).getText();
        }

        // Check if all fields have non-empty values
        public boolean areAllFieldsFilled() {
            String fullName = driver.findElement(fullNameInput).getAttribute("value").trim();
            String email = driver.findElement(emailInput).getAttribute("value").trim();
            String currentAddr = driver.findElement(currentAddressInput).getAttribute("value").trim();
            String permanentAddr = driver.findElement(permanentAddressInput).getAttribute("value").trim();

            return !fullName.isEmpty() && !email.isEmpty() && !currentAddr.isEmpty() && !permanentAddr.isEmpty();
        }
    }
}
