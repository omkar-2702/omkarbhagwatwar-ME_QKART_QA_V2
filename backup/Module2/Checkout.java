package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.internal.WebElementToJsonConverter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Click on the "Add new address" button, enter the addressString in the address text
             * box and click on the "ADD" button to save the address
             */
            WebElement addnewaddressbutton = driver.findElement(By.id("add-new-btn"));
            addnewaddressbutton.click();

            WebElement addrtextarea = driver.findElement(
                    By.xpath("//textarea[@placeholder='Enter your complete address']"));
            addrtextarea.sendKeys(addresString);

            WebElement addbutton = driver.findElement(By.xpath("//button[text()='Add']"));
            addbutton.click();
            Thread.sleep(3000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            /*
             * Iterate through all the address boxes to find the address box with matching text,
             * addressToSelect and click on it
             */
            WebElement parentBox =
                    driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[1]/div/div[1]"));
            List<WebElement> allBoxes = parentBox.findElements(By.className("not-selected"));

            for (WebElement box : allBoxes) {
                if (box.findElement(By.className("css-yg30e6")).getText().replaceAll(" ", "")
                        .equals(addressToSelect.replaceAll(" ", ""))) {
                    box.findElement(By.tagName("input")).click();
                    return true;
                }
            }
            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println(
                    "Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find the "PLACE ORDER" button and click on it

            WebElement placeorderbutton =
                    driver.findElement(By.xpath("//button[text()='PLACE ORDER']"));
            placeorderbutton.click();
            return true;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 07: MILESTONE 6


            WebElement element = driver.findElement(By.xpath("//div[@id='notistack-snackbar']"));
            String message = element.getText();
            if (element.isDisplayed())
                System.out.println("message is found" + " " + message);
            return true;

        } catch (Exception e) {
            System.out.println(
                    "Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
