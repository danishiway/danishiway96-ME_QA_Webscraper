package _WebScrpaer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class TestCases {
	public static WebDriver driver;
    private ArrayList<HashMap<String, Object>> dataList;
    private static final String OUTPUT_DIR = "output/";
    private static final ObjectMapper mapper = new ObjectMapper();
    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().timeout(30).setup();
        driver = (WebDriver) new ChromeDriver();
        driver.get("https://www.scrapethissite.com/pages/");
        driver.manage().window().maximize();
        dataList = new ArrayList<>();
    }

    @AfterClass
    public void endTest() {
        driver.quit();
    }

    @Test
    public void testCase01() {
        System.out.println("Start Test case: testCase01");
        String siteName = verifySiteName("//a[@href='/pages/forms/']");
        assert siteName.contains("Hockey Teams: Forms, Searching and Pagination") :
                "SiteName does not match expected value";
        selectSite("//a[@href='/pages/forms/']");
        scrapeHockeyData(4, 0.40);
        saveToJson("hockey-team-data");
        System.out.println("End Test case: testCase01");
    }
    @Test
    public void testCase02() {
        System.out.println("Start Test case: testCase02");
        String siteName = verifySiteName("//a[@href='/pages/ajax-javascript/']");
        assert siteName.contains("Oscar Winning Films: AJAX and Javascript") :
                "SiteName does not match expected value";
        selectSite("//a[@href='/pages/ajax-javascript/']");
        scrapeOscarData();
        saveToJson("oscar-winner-data");
        System.out.println("End Test case: testCase02");
    }

    private void scrapeOscarData() {
        List<WebElement> years = driver.findElements(By.xpath("//div[@class='col-md-12 text-center']/a"));
        for (WebElement year : years) {
            year.click();            

            List<WebElement> films = driver.findElements(By.xpath("//table[@class='table']/tbody/tr/"));
            for (int i = 0; i < Math.min(5, films.size()); i++) {
                WebElement film = films.get(i);
                String title = film.findElement(By.xpath(".//td[1]")).getText();
                String nomination = film.findElement(By.xpath(".//td[2]")).getText();
                String awards = film.findElement(By.xpath(".//td[3]")).getText();

                HashMap<String, Object> data = new HashMap<>();
                data.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                data.put("Year", year.getText());
                data.put("Title", title);
                data.put("Nomination", nomination);
                data.put("Awards", awards);
                data.put("isWinner", i == 0); 
                dataList.add(data);
            }
        }
		
	}

	private String verifySiteName(String path) {
        WebElement container = driver.findElement(By.xpath(path));
        return container.getText();
    }

    private void selectSite(String path) {
        WebElement container = driver.findElement(By.xpath(path));
        container.click();
    }

    private void scrapeHockeyData(int numOfPages, double percentage) {
    	dataList=new ArrayList<>();
        for (int i = 0; i < numOfPages; i++) {
            List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table']/tbody/tr"));
            for (WebElement row : rows) {
                String teamName = row.findElement(By.xpath(".//td[1]")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath(".//td[2]")).getText());
                double winPercentage = Double.parseDouble(row.findElement(By.xpath(".//td[5]")).getText());

                // If win percentage is less than 40%, add to dataList
                if (winPercentage < percentage) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                    data.put("Team Name", teamName);
                    data.put("Year", year);
                    data.put("Win %", winPercentage);
                    dataList.add(data);
                }
            }
            // Go to the next page
            WebElement nextPageButton = driver.findElement(By.xpath("//a[@aria-label='Next']"));
            nextPageButton.click();
            }
        }   
    
    
    private void saveToJson(String fileNmae) {   
    	
    try {
        File outputFile = new File("src/test/resources/"+ fileNmae+".json");
        mapper.writeValue(outputFile, dataList);
        assert outputFile.exists() && outputFile.length() > 0 : "JSON file is missing or empty";
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}


