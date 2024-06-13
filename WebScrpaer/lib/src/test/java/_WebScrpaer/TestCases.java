package _WebScrpaer;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.bonigarcia.wdm.WebDriverManager;
public class TestCases {
	public static WebDriver driver;
    private ArrayList<HashMap<String, Object>> dataList;
    private static final String OUTPUT_DIR = "output/";
    private static final ObjectMapper mapper = new ObjectMapper();
    @BeforeClass
    public void setUp() {
    	WebDriverManager.chromedriver().setup();
    	driver = (WebDriver) new ChromeDriver();

    	    driver.get("https://www.scrapethissite.com/pages/");
    	    driver.manage().window().maximize();
    	    
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
    	 driver.get("https://www.scrapethissite.com/pages/");
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
    	dataList=new ArrayList<>();
        List<WebElement> years = ((WebDriver) driver).findElements(By.xpath("//*[@id='oscars']/div/div[4]/div/a"));
        for (WebElement year : years) {
            year.click();            

            List<WebElement> bestfilms = ((WebDriver) driver).findElements(By.xpath("//*[@id='table-body']/tr/td/i"));
            for (int i = 0; i < Math.min(5, bestfilms.size()); i++) {
                WebElement film = bestfilms.get(i);
                System.out.println("Check point1");
                String title = film.findElement(By.xpath("//tbody[@id='table-body']/tr/child::td[@class='film-title']")).getText();
                String nomination = film.findElement(By.xpath(".//tbody[@id='table-body']/tr/child::td[@class='film-nominations']")).getText();
                String awards = film.findElement(By.xpath("//tbody[@id='table-body']/tr/child::td[@class='film-awards']")).getText();
                System.out.println("Check point2");
                HashMap<String, Object> data = new HashMap<>();
                data.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                data.put("Year", year.getText());
                data.put("Title", title);
                data.put("Nomination", nomination);
                data.put("Awards", awards);
                data.put("isWinner", i == 0); 
                dataList.add(data);
                System.out.println("Check point3");
            }
        }
		
	}

	private String verifySiteName(String path) {
        WebElement container = ((WebDriver) driver).findElement(By.xpath(path));
        return container.getText();
    }

    private void selectSite(String path) {
        WebElement container = ((WebDriver) driver).findElement(By.xpath(path));
        container.click();
    }

    private ArrayList<HashMap<String,Object>> scrapeHockeyData(int numOfPages, double percentage) {
    	dataList=new ArrayList<>();
        for (int i = 0; i < numOfPages; i++) {
        	nextPageOnHockeyData(i);
        	
            List<WebElement> rows = ((WebDriver) driver).findElements(By.xpath("//table[@class='table']/tbody"));
            for (WebElement row : rows) {
            	
                String teamName = row.findElement(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Team Name']/following::tr//td[1]")).getText();
                int year = Integer.parseInt(row.findElement(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Year']/following::tr//td[2]")).getText());
                double winPercentage = Double.parseDouble(row.findElement(By.xpath("//table[@class='table']/tbody/tr/th[normalize-space()='Win %']/following::tr/td[6]")).getText());
               
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
           
            }
        return dataList;
        }   
  public static void  nextPageOnHockeyData(int pageNo) {
	  // Go to the next page
      WebElement nextPageButton = ((WebDriver) driver).findElement(By.xpath("//a[@aria-label='Next']"));
      nextPageButton.click();
  }
    
  private void saveToJson(String fileName) {
	    try {
	       
	        String filePath = "src/test/resources/" + fileName + ".json";
	        
	        
	        mapper.enable(SerializationFeature.INDENT_OUTPUT);

	        
	        mapper.writeValue(new File(filePath), dataList);

	        
	        File outputFile = new File(filePath);
	        assert outputFile.exists() && outputFile.length() > 0 : "JSON file is missing or empty";

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}


