package _WebScrpaer;

import java.net.MalformedURLException;



public class App {
    

    public static void main(String[] args) throws InterruptedException, MalformedURLException {
    	 // This is to remove unnecessary warnings from your console
        System.setProperty("java.util.logging.config.file", "logging.properties");
        
        TestCases tests = new TestCases(); // Initialize your test class

        //TODO: call your test case functions one after other here

        tests.testCase01();
        tests.testCase02();

        //END Tests


        tests.endTest();
    }
}

