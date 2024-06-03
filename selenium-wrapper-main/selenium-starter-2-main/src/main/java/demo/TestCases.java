package demo;

import org.openqa.selenium.WebDriver;


import io.github.bonigarcia.wdm.WebDriverManager;

 
public class TestCases {
    WebDriver driver;
    public TestCases()
    {
        System.out.println("Constructor: TestCases");
        
    } 

    public void endTest()
    {
        System.out.println("End Test: TestCases");
        driver.close();
        driver.quit();

    }

    
    public  void testCase01(){
        System.out.println("Start Test case: testCase01");
        driver.get("https://www.google.com");
        System.out.println("end Test case: testCase02");
    }


}
