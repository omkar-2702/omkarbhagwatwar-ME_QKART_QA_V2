package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;
import net.bytebuddy.implementation.bytecode.Duplication;
import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
// import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;

public class QKART_Tests {

        static RemoteWebDriver driver;
        public static String lastGeneratedUserName;

        @BeforeSuite
        public static void createDriver() throws MalformedURLException {
                // Launch Browser using Zalenium
                final DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName(BrowserType.CHROME);
                driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
                System.out.println("createDriver()");
        }

        /*
         * Testcase01: Verify a new user can successfully register
         */
        @Test
        public void TestCase01() throws InterruptedException {
                Boolean status;
                // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
                // takeScreenshot(driver, "StartTestCase", "TestCase1");

                // Visit the Registration page and register a new user
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                assertTrue(status, "Failed to register new user");

                // Save the last generated username
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Visit the login page and login with the previuosly registered user
                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
                assertTrue(status, "Failed to login with registered user");

                // Visit the home page and log out the logged in user
                Home home = new Home(driver);
                status = home.PerformLogout();

                // logStatus("End TestCase", "Test Case 1: Verify user Registration : ",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "EndTestCase", "TestCase1");
        }


        @AfterSuite
        public static void quitDriver() {
                System.out.println("quit()");
                driver.quit();
        }

        public static void logStatus(String type, String message, String status) {

                System.out.println(String.format("%s |  %s  |  %s | %s",
                                String.valueOf(java.time.LocalDateTime.now()), type, message,
                                status));
        }

        public static void takeScreenshot(WebDriver driver, String screenshotType,
                        String description) {
                try {
                        // File theDir = new File("/screenshots");
                        // if (!theDir.exists()) {
                        // theDir.mkdirs();
                        // }
                        String timestamp = String.valueOf(java.time.LocalDateTime.now());
                        String fileName = String.format("screenshot_%s_%s_%s.png", timestamp,
                                        screenshotType, description);
                        TakesScreenshot scrShot = ((TakesScreenshot) driver);
                        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
                        File DestFile = new File("screenshots/" + fileName);
                        FileUtils.copyFile(SrcFile, DestFile);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        @Test
        public void TestCase02() throws InterruptedException {
                Boolean status;
                logStatus("Start Testcase",
                                "Test Case 2: Verify User Registration with an existing username ",
                                "DONE");

                // Visit the Registration page and register a new user
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                Assert.assertTrue(status, "unable to perform registration");

                logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");

                // if (!status) {
                // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ?
                // "PASS" :
                // "FAIL");
                // //return false;

                // }

                // Save the last generated username
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Visit the Registration page and try to register using the previously
                // registered user's credentials
                registration.navigateToRegisterPage();
                status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

                // If status is true, then registration succeeded, else registration has
                // failed. In this case registration failure means Success
                Assert.assertFalse(status, "Re-registration Successful");

                logStatus("End TestCase", "Test Case 2: Verify user Registration : ",
                                status ? "FAIL" : "PASS");
                // return !status;
        }

        /*
         * Verify the functinality of the search text box
         */
        @Test
        public void TestCase03() throws InterruptedException {
                logStatus("TestCase 3", "Start test case: Verify functionality of search box",
                                "DONE");
                boolean status;

                // Visit the home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Search for the "yonex" product
                status = homePage.searchForProduct("YONEX");
                Assert.assertTrue(status, "Unable to search for the given product");

                // Fetch the search results
                List<WebElement> searchResults = homePage.getSearchResults();

                // Verify the search results are available
                Assert.assertFalse(searchResults.isEmpty(),
                                "No results found for the given search string");

                for (WebElement webElement : searchResults) {
                        // Create a SearchResult object from the parent element
                        SearchResult resultElement = new SearchResult(webElement);

                        // Verify that all results contain the searched text
                        String elementText = resultElement.getTitleofResult();
                        Assert.assertTrue(elementText.toUpperCase().contains("YONEX"),
                                        "Search results contain unexpected values: " + elementText);
                }

                logStatus("Step Success", "Successfully validated the search results", "PASS");

                // Search for an invalid product
                status = homePage.searchForProduct("Gesundheit");
                Assert.assertFalse(status, "Invalid keyword returned results");

                // Verify no search results are found
                searchResults = homePage.getSearchResults();
                Assert.assertTrue(searchResults.isEmpty(),
                                "Expected no results, but results were available");

                // Check that the 'no products found' message is displayed
                Assert.assertTrue(homePage.isNoResultFound(),
                                "No products found message is not displayed");

                logStatus("TestCase 3",
                                "Test Case PASS. Verified that no search results were found for the given text",
                                "PASS");
        }

        /*
         * Verify the presence of size chart and check if the size chart content is as expected
         */
        @Test
        public void TestCase04() throws InterruptedException {
                logStatus("TestCase 4", "Start test case: Verify the presence of size Chart",
                                "DONE");
                boolean status;

                // Visit home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Search for product and get card content element of search results
                status = homePage.searchForProduct("Running Shoes");
                Assert.assertTrue(status, "Product search for 'Running Shoes' failed.");

                List<WebElement> searchResults = homePage.getSearchResults();
                Assert.assertFalse(searchResults.isEmpty(),
                                "No search results found for 'Running Shoes'.");

                // Expected values
                List<String> expectedTableHeaders =
                                Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
                List<List<String>> expectedTableBody =
                                Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                                                Arrays.asList("7", "7", "41", "10.2"),
                                                Arrays.asList("8", "8", "42", "10.6"),
                                                Arrays.asList("9", "9", "43", "11"),
                                                Arrays.asList("10", "10", "44", "11.5"),
                                                Arrays.asList("11", "11", "45", "12.2"),
                                                Arrays.asList("12", "12", "46", "12.6"));

                // Verify size chart presence and content for each search result
                for (WebElement webElement : searchResults) {
                        SearchResult result = new SearchResult(webElement);

                        // Assert size chart existence for the search result
                        Assert.assertTrue(result.verifySizeChartExists(),
                                        "Size Chart Link does not exist for a search result.");
                        logStatus("Step Success",
                                        "Successfully validated presence of Size Chart Link",
                                        "PASS");

                        // Assert size dropdown existence
                        status = result.verifyExistenceofSizeDropdown(driver);
                        Assert.assertTrue(status, "Size dropdown is missing in the search result.");
                        logStatus("Step Success", "Validated presence of drop down", "PASS");

                        // Open the size chart
                        Assert.assertTrue(result.openSizechart(), "Failed to open Size Chart.");

                        // Assert size chart contents match expected values
                        Assert.assertTrue(
                                        result.validateSizeChartContents(expectedTableHeaders,
                                                        expectedTableBody, driver),
                                        "Size Chart content validation failed.");
                        logStatus("Step Success",
                                        "Successfully validated contents of Size Chart Link",
                                        "PASS");

                        // Close the size chart modal
                        status = result.closeSizeChart(driver);
                        Assert.assertTrue(status, "Failed to close the Size Chart modal.");
                }

                logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", "PASS");
                // return true;
        }

        /*
         * Verify the complete flow of checking out and placing order for products is working
         * correctly
         */

        @Test
        public void TestCase05() throws InterruptedException {
                logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products",
                                "DONE");
                Boolean status;

                // Go to the Register page
                Register registration = new Register(driver);
                registration.navigateToRegisterPage();

                // Register a new user
                status = registration.registerUser("testUser", "abc@123", true);
                Assert.assertTrue(status,
                                "Test Case 5 Failure: Happy Flow Test Failed during registration.");
                logStatus("Step Success", "User registered successfully", "PASS");

                // Save the username of the newly registered user
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Go to the login page
                Login login = new Login(driver);
                login.navigateToLoginPage();

                // Login with the newly registered user's credentials
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Assert.assertTrue(status, "Test Case 5 Failure: Login failed.");
                logStatus("Step Success", "User logged in successfully", "PASS");

                // Go to the home page
                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Find required products by searching and add them to the user's cart
                status = homePage.searchForProduct("YONEX");
                Assert.assertTrue(status, "Failed to search for 'YONEX' product.");
                homePage.addProductToCart("YONEX Smash Badminton Racquet");

                status = homePage.searchForProduct("Tan");
                Assert.assertTrue(status, "Failed to search for 'Tan' product.");
                homePage.addProductToCart("Tan Leatherette Weekender Duffle");

                // Click on the checkout button
                homePage.clickCheckout();

                // Add a new address on the Checkout page and select it
                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

                // Place the order
                checkoutPage.placeOrder();

                WebDriverWait wait = new WebDriverWait(driver, 30);
                wait.until(ExpectedConditions
                                .urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

                // Check if placing order redirected to the Thanks page
                status = driver.getCurrentUrl().endsWith("/thanks");
                Assert.assertTrue(status, "Failed to reach 'Thanks' page after placing order.");

                logStatus("Step Success", "Order placed successfully", "PASS");

                // Go to the home page and log out
                homePage.navigateToHome();
                homePage.PerformLogout();
                logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed", "PASS");
        }


        /*
         * Verify the quantity of items in cart can be updated
         */

        @Test(groups = "CartFunctionality")
        public void TestCase06() throws InterruptedException {
                // Test logic for verifying cart edit functionality
                Boolean status;
                logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
                Home homePage = new Home(driver);
                Register registration = new Register(driver);
                Login login = new Login(driver);

                // Register the user and assert successful registration
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                Assert.assertTrue(status, "User registration failed");
                lastGeneratedUserName = registration.lastGeneratedUsername;

                // Login the user and assert successful login
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Assert.assertTrue(status, "User login failed");

                // Search and add products to cart
                homePage.navigateToHome();
                status = homePage.searchForProduct("Xtend");
                Assert.assertTrue(status, "Product 'Xtend' not found");
                homePage.addProductToCart("Xtend Smart Watch");

                status = homePage.searchForProduct("Yarine");
                Assert.assertTrue(status, "Product 'Yarine' not found");
                homePage.addProductToCart("Yarine Floor Lamp");

                // Update the quantities of products in the cart
                homePage.changeProductQuantityinCart("Xtend Smart Watch", 2);
                homePage.changeProductQuantityinCart("Yarine Floor Lamp", 0);
                homePage.changeProductQuantityinCart("Xtend Smart Watch", 1);

                // Proceed to checkout
                homePage.clickCheckout();

                // Fill in address and place the order
                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.placeOrder();

                // Verify that the order was placed by checking the URL
                try {
                        WebDriverWait wait = new WebDriverWait(driver, 30);
                        wait.until(ExpectedConditions.urlToBe(
                                        "https://crio-qkart-frontend-qa.vercel.app/thanks"));
                } catch (TimeoutException e) {
                        Assert.fail("Error while placing order: " + e.getMessage());
                }

                status = driver.getCurrentUrl().endsWith("/thanks");
                Assert.assertTrue(status, "Order placement failed");

                // Log out
                homePage.navigateToHome();
                homePage.PerformLogout();

                logStatus("End TestCase", "Test Case 6: Verify that cart can be edited", "PASS");
        }

        @Test(groups = "CartFunctionality")
        public void TestCase07() throws InterruptedException {
                // Test logic for verifying insufficient balance error
                Boolean status;
                logStatus("Start TestCase",
                                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                                "DONE");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);
                Assert.assertTrue(status, "User registration failed");

                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");
                Assert.assertTrue(status, "User login failed");

                // Add a product to cart and adjust quantities
                Home homePage = new Home(driver);
                homePage.navigateToHome();
                status = homePage.searchForProduct("Stylecon");
                Assert.assertTrue(status, "Product 'Stylecon' not found");
                homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set");

                // Change quantity in the cart and proceed to checkout
                homePage.changeProductQuantityinCart("Stylecon 9 Seater RHS Sofa Set", 10);
                homePage.clickCheckout();

                // Fill in address and place the order
                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
                checkoutPage.placeOrder();
                Thread.sleep(3000);

                // Verify the insufficient balance message is displayed
                status = checkoutPage.verifyInsufficientBalanceMessage();
                Assert.assertTrue(status, "Insufficient balance message was not displayed");

                logStatus("End TestCase",
                                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                                "PASS");
        }

        @Test(groups = "BalanceValidation")
        public void TestCase08() throws InterruptedException {
                Boolean status = false;

                logStatus("Start TestCase",
                                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                                "DONE");
                takeScreenshot(driver, "StartTestCase", "TestCase09");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);

                Assert.assertTrue(status, "Unable to register");

                // if (!status) {
                // logStatus("TestCase 8",
                // "Test Case Failure. Verify that product added to cart is available when a new tab
                // is opened",
                // "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // }
                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");

                Assert.assertTrue(status, "Unable to login");

                // if (!status) {
                // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");
                // logStatus("End TestCase",
                // "Test Case 8: Verify that product added to cart is available when a new tab is
                // opened",
                // status ? "PASS" : "FAIL");
                // }

                Home homePage = new Home(driver);
                homePage.navigateToHome();

                status = homePage.searchForProduct("YONEX");

                Assert.assertTrue(status, "Unable to serch the product : Stylecon ");

                homePage.addProductToCart("YONEX Smash Badminton Racquet");

                String currentURL = driver.getCurrentUrl();

                driver.findElement(By.linkText("Privacy policy")).click();
                Set<String> handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

                driver.get(currentURL);
                Thread.sleep(2000);

                List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
                status = homePage.verifyCartContents(expectedResult);

                Assert.assertTrue(status, "Unable to verify the message");

                driver.close();

                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

                logStatus("End TestCase",
                                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                                status ? "PASS" : "FAIL");
                takeScreenshot(driver, "EndTestCase", "TestCase08");

                // return status;
        }

        @Test(groups = "BalanceValidation")
        public void TestCase09() throws InterruptedException {
                Boolean status = false;

                logStatus("Start TestCase",
                                "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
                                "DONE");
                takeScreenshot(driver, "StartTestCase", "TestCase09");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);

                Assert.assertTrue(status, "Unable to registerUser");

                // if (!status) {
                // logStatus("TestCase 09",
                // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed
                // correctly ",
                // "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // }
                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");

                Assert.assertTrue(status, "Unable to login");

                // if (!status) {
                // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // logStatus("End TestCase",
                // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                // status ? "PASS" : "FAIL");
                // }

                Home homePage = new Home(driver);
                homePage.navigateToHome();

                String basePageURL = driver.getCurrentUrl();

                driver.findElement(By.linkText("Privacy policy")).click();
                status = driver.getCurrentUrl().equals(basePageURL);

                Assert.assertTrue(status, "Parent page URL changed after clicking Privacy Policy");

                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying parent page url didn't change on privacy policy link click failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase09");
                // logStatus("End TestCase",
                // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                // status ? "PASS" : "FAIL");
                // }

                Set<String> handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
                WebElement PrivacyPolicyHeading =
                                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
                status = PrivacyPolicyHeading.getText().equals("Privacy Policy");

                Assert.assertTrue(status, "Privacy Policy page heading is incorrect");

                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying new tab opened has Privacy Policy page heading failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");
                // logStatus("End TestCase",
                // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                // status ? "PASS" : "FAIL");
                // }

                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
                driver.findElement(By.linkText("Terms of Service")).click();

                handles = driver.getWindowHandles();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
                WebElement TOSHeading =
                                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
                status = TOSHeading.getText().equals("Terms of Service");

                Assert.assertTrue(status, "Terms of Service page heading is incorrect");

                // if (!status) {
                // logStatus("Step Failure",
                // "Verifying new tab opened has Terms Of Service page heading failed",
                // status ? "PASS" : "FAIL");
                // takeScreenshot(driver, "Failure", "TestCase9");
                // logStatus("End TestCase",
                // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                // status ? "PASS" : "FAIL");
                // }

                driver.close();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
                driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

                logStatus("End TestCase",
                                "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
                                "PASS");
                takeScreenshot(driver, "EndTestCase", "TestCase9");

                // return status;
        }

        @Test
        public void TestCase10() throws InterruptedException {
                logStatus("Start TestCase",
                                "Test Case 10: Verify that contact us option is working correctly ",
                                "DONE");
                takeScreenshot(driver, "StartTestCase", "TestCase10");

                Home homePage = new Home(driver);
                homePage.navigateToHome();

                // Click on 'Contact us' link and verify it opens the contact form
                driver.findElement(By.xpath("//*[text()='Contact us']")).click();
                WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
                Assert.assertTrue(name.isDisplayed(), "Contact form did not open");

                // Enter name, email, and message, then verify they were entered correctly
                name.sendKeys("crio user");
                Assert.assertEquals(name.getAttribute("value"), "crio user",
                                "Name not entered correctly");

                WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
                email.sendKeys("criouser@gmail.com");
                Assert.assertEquals(email.getAttribute("value"), "criouser@gmail.com",
                                "Email not entered correctly");

                WebElement message =
                                driver.findElement(By.xpath("//input[@placeholder='Message']"));
                message.sendKeys("Testing the contact us page");
                Assert.assertEquals(message.getAttribute("value"), "Testing the contact us page",
                                "Message not entered correctly");

                // Click the 'Contact Us' button and wait for the element to become invisible,
                // confirming submission
                WebElement contactUs = driver.findElement(By.xpath(
                                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));
                contactUs.click();

                WebDriverWait wait = new WebDriverWait(driver, 30);
                Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOf(contactUs)),
                                "Contact form was not submitted successfully");

                logStatus("End TestCase",
                                "Test Case 10: Verify that contact us option is working correctly ",
                                "PASS");
                takeScreenshot(driver, "EndTestCase", "TestCase10");
        }


        @Test
        public void TestCase11() throws InterruptedException {
                Boolean status = false;
                logStatus("Start TestCase",
                                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                                "DONE");
                takeScreenshot(driver, "StartTestCase", "TestCase11");

                Register registration = new Register(driver);
                registration.navigateToRegisterPage();
                status = registration.registerUser("testUser", "abc@123", true);

                Assert.assertTrue(status, "Unable to register");

                lastGeneratedUserName = registration.lastGeneratedUsername;

                Login login = new Login(driver);
                login.navigateToLoginPage();
                status = login.PerformLogin(lastGeneratedUserName, "abc@123");

                Assert.assertTrue(status, "Unable to login");

                Home homePage = new Home(driver);
                homePage.navigateToHome();

                status = homePage.searchForProduct("YONEX Smash Badminton Racquet");

                Assert.assertTrue(status, "Unable to search for product");

                homePage.addProductToCart("YONEX Smash Badminton Racquet");
                homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
                homePage.clickCheckout();

                Checkout checkoutPage = new Checkout(driver);
                checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
                checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
                checkoutPage.placeOrder();
                Thread.sleep(3000);

                String currentURL = driver.getCurrentUrl();

                List<WebElement> advertisements = driver.findElements(By.xpath("//iframe"));
                status = advertisements.size() == 3;
                Assert.assertTrue(status, "Three advertisements are not available");

                logStatus("Step", "Verify that 3 advertisements are available", "PASS");

                // Check the first advertisement
                WebElement advertisement1 = driver.findElement(
                                By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
                driver.switchTo().frame(advertisement1);
                driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
                driver.switchTo().parentFrame();

                status = !driver.getCurrentUrl().equals(currentURL);
                Assert.assertTrue(status, "Advertisement 1 is not clickable");
                logStatus("Step", "Verify that Advertisement 1 is clickable", "PASS");

                driver.get(currentURL);
                Thread.sleep(3000);

                // Check the second advertisement
                WebElement advertisement2 = driver.findElement(
                                By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
                driver.switchTo().frame(advertisement2);
                driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
                driver.switchTo().parentFrame();

                status = !driver.getCurrentUrl().equals(currentURL);
                Assert.assertTrue(status, "Advertisement 2 is not clickable");
                logStatus("Step", "Verify that Advertisement 2 is clickable", "PASS");

                logStatus("End TestCase",
                                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                                "PASS");
                takeScreenshot(driver, "EndTestCase", "TestCase11");
        }


}


