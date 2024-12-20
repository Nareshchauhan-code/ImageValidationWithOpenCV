import org.apache.commons.io.FileUtils;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.IOException;

public class ImageComparisonWithSeleniumOpenCV {

    static {
        // Load OpenCV native library
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        nu.pattern.OpenCV.loadShared();
    }

    public static void main(String[] args) throws IOException {

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Open the website
        driver.get("https://www.indiatv.in/livetv");

        // Capture screenshot of the element or entire page (full page capture)
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        //File screenshot = element.getScreenshotAs(OutputType.FILE);

        // Copy the screenshot to a specific location;
        FileUtils.copyFile(screenshot, new File("CurrentPage.png"));

        // Load the template image (image you want to match against)
        Mat template = Imgcodecs.imread("./IndiaTV.png", Imgcodecs.IMREAD_COLOR);

        // Read the screenshot image
        Mat image = Imgcodecs.imread("./CurrentPage.png", Imgcodecs.IMREAD_COLOR);

        // Convert the images to grayscale for better comparison
        Mat grayImage = new Mat();
        Mat grayTemplate = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(template, grayTemplate, Imgproc.COLOR_BGR2GRAY);

        // Perform template matching using OpenCV
        Mat result = new Mat();
        Imgproc.matchTemplate(grayImage, grayTemplate, result, Imgproc.TM_CCOEFF_NORMED);

        // Find the best match location
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;

        // Draw a rectangle around the matched region
        Imgproc.rectangle(image, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(0, 255, 0), 2);

        // Save the result image with the rectangle drawn around the matched region
        Imgcodecs.imwrite("matched_result.png", image);

        // Check if the matching score is good enough (optional threshold)
        if (mmr.maxVal > 0.8) {
            System.out.println("Template matched successfully!");
        } else {
            System.out.println("Template match failed.");
        }

        // Clean up
        driver.quit();
    }
}
