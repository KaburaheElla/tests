package com.purse.steps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ContactForm {
    private WebDriver driver;
    private JsonNode userData;

    private String get_item_string(String parent, int index, String item){
        return userData.get(parent).get(index).get(item).asText();
    } 

    private boolean isElementVisible(By by){
        try {
            WebElement element = driver.findElement(by);
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }

    }

    private void define_gender() {
        driver.findElement(By.id("gender")).click();
        Select drpgender = new Select (driver.findElement(By.id("gender")));
        switch(get_item_string("data", 0, "title")) {
            case "mrs":
                drpgender.selectByVisibleText("Femme");
                break;
            case "mr":
                drpgender.selectByVisibleText("Homme");
                break;
            case "miss":
                drpgender.selectByVisibleText("Femme");
                break;
            default:
                drpgender.selectByVisibleText("Autre");
          }
    }

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Given("I am on the contact page")
    public void iAmOnTheContactPage() {
        driver.get("https://testqa.purse.tech/fake-contact");
    }

    @When("I retrieve all data from dummyAPI")
    public void iRetrieveAllDataFromDummyAPI() throws IOException, InterruptedException {
        // Step 1: Create the HttpClient 
        HttpClient client = HttpClient.newHttpClient();
        // Step 2: Create the HttpRequest 
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://dummyapi.io/data/v1/user?limit=5"))
            .header("app-id", "61f4248c9d9bb038eaf0c6c0")
            .build();
        // Step 3: Send the HttpRequest and get the HttpResponse 
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 
        ObjectMapper objectMapper = new ObjectMapper();
        userData = objectMapper.readTree(response.body());
        
    }

    @When("I fill in the contact form with gathered data")
    public void iFillInTheContactFormWithGatheredData() {
        define_gender();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 0, "firstName"));
        driver.findElement(By.id("last-name")).sendKeys(get_item_string("data", 0, "lastName"));
        driver.findElement(By.id("message-title")).sendKeys("test ok");
        driver.findElement(By.id("message")).sendKeys("Ok");
    }

    @When("I fill in the contact form without fill in the name")
    public void iFillInTheContactFormNameMissing() {
        define_gender();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 0, "firstName"));
        driver.findElement(By.id("message-title")).sendKeys("test ok");
        driver.findElement(By.id("message")).sendKeys("Ok");
    }

    @When("I fill in the contact form without writting a message")
    public void iFillInTheContactFormMessageNotDefined() {
        define_gender();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 0, "firstName"));
        driver.findElement(By.id("last-name")).sendKeys(get_item_string("data", 0, "lastName"));
        driver.findElement(By.id("message-title")).sendKeys("test ok");
    }

    @When("I fill in the contact form without selecting gender")
    public void iFillInTheContactFormGenderNotSelected(){
        driver.findElement(By.id("first-name")).click();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 1, "firstName"));
        driver.findElement(By.id("last-name")).sendKeys(get_item_string("data", 1, "lastName"));
        driver.findElement(By.id("message-title")).sendKeys("test ok");
        driver.findElement(By.id("message")).sendKeys("Ok");
    }
    @When("I fill in the contact form with invalid phone number")
    public void iFillInTheContactFormWithInvalidPhoneNumber() {
        define_gender();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 0, "firstName"));
        driver.findElement(By.id("last-name")).sendKeys(get_item_string("data", 0, "lastName"));
        driver.findElement(By.id("phone")).sendKeys(get_item_string("data", 0, "id"));
        driver.findElement(By.id("message-title")).sendKeys("Erreur sur le format du numéro");
        driver.findElement(By.id("message")).sendKeys("Ceci est un test qui prouve qu'il manque un contrôle sur le format du téléphone.");
    }
    @When("I fill in the contact form with invalid name")
    public void iFillInTheContactFormWithInvalidName() {
        define_gender();
        driver.findElement(By.id("first-name")).sendKeys(get_item_string("data", 0, "id"));
        driver.findElement(By.id("last-name")).sendKeys(get_item_string("data", 0, "picture"));
        driver.findElement(By.id("message-title")).sendKeys("Erreur sur le format des noms");
        driver.findElement(By.id("message")).sendKeys("Erreur sur le format des noms");
    }

    @When("I submit the form")
    public void iSubmitTheForm() {
        driver.findElement(By.id("submit-button")).click();
    }

    @Then("I should see a success message")
    public void iShouldSeeASuccessMessage() {
        WebElement successMessage = driver.findElement(By.id("popin-message"));
        assertTrue(successMessage.getText().contains("Le message a été envoyé."));
        /* We expect the submission of the form to succeed, so there will not be an invalid element on the page */ 
        assertFalse(isElementVisible(By.cssSelector(":invalid")));

    }
    @Then("I should see select error message")
    public void iShouldSeeSelectErrorMessage() {
        /* We expect the submission of the form to fail, so there will be an invalid element on the page */ 
        assertTrue(isElementVisible(By.cssSelector(":invalid")));

    }
    @Then("I should see invalid phone number message")
    public void iShouldSeeInvalidPhoneNumberMessage() {
        WebElement failureMessage = driver.findElement(By.id("popin-message"));
        assertTrue(failureMessage.getText().contains("Le numéro de téléphone n'est pas valide."));
    }
    @Then("I should see special character not supported")
    public void iShouldSeeSpecialCharacterNotSupported() {
        WebElement failureMessage = driver.findElement(By.id("popin-message"));
        assertTrue(failureMessage.getText().contains("Les caractères speciaux ne sont pas supportés."));
    }

    @Then(" I should see fill in all fields")
    public void iShouldSeeFillInAllFields() {
        WebElement failureMessage = driver.findElement(By.id("popin-message"));
        assertTrue(failureMessage.getText().contains("Veuillez remplir tous les champs obligatoires."));
    }

    
}
