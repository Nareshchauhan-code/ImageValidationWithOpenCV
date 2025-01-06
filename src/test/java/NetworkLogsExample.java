import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v130.network.Network;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;

import java.util.Map;
import java.util.Optional;

public class NetworkLogsExample {
    public static void main(String[] args) {
        // Set the path to your ChromeDriver executable
       // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        // Create ChromeOptions
        ChromeOptions options = new ChromeOptions();
        options.setCapability("goog:loggingPrefs", Map.of("performance", "ALL"));
        options.addArguments("--remote-allow-origins=*"); // Allow connections to remote origins

        // Initialize WebDriver with ChromeOptions
        WebDriver driver = new ChromeDriver(options);

        // Get the DevTools instance
        DevTools devTools = ((ChromeDriver) driver).getDevTools();

        // Create a new DevTools session
        devTools.createSession();

        // Enable network tracking via DevTools
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Add listener to capture network responses
        devTools.addListener(Network.responseReceived(), response -> {
            System.out.println("Response URL: " + response.getResponse().getUrl());
            System.out.println("Status Code: " + response.getResponse().getStatus());
            System.out.println("Response Headers: " + response.getResponse().getHeaders());
        });

        // Add listener to capture network requests
        devTools.addListener(Network.requestWillBeSent(), request -> {
            System.out.println("Request URL: " + request.getRequest().getUrl());
            System.out.println("Request Method: " + request.getRequest().getMethod());
            System.out.println("Request Headers: " + request.getRequest().getHeaders());
        });

        // Navigate to a website
        driver.get("https://www.google.com");

        // Capture performance logs
        LogEntries logs = driver.manage().logs().get(LogType.PERFORMANCE);
        for (LogEntry log : logs) {
            System.out.println("Performance Log: " + log.getMessage());
        }

        // Wait for a few seconds (for example purposes, to allow network requests to complete)
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Quit the WebDriver
        driver.quit();
    }
}
