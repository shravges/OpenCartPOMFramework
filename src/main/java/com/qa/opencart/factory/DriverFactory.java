package com.qa.opencart.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.qa.opencart.frameworkexception.FrameworkException;

public class DriverFactory 
{
	WebDriver driver;
	BrowserOptionsManager bo;
	public static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();
	FileInputStream fip;
	
	public WebDriver intializeDriver(Properties prop)
	{
		//String browserName = prop.getProperty("browser");
		String browserName = System.getProperty("browser");
		
		//if user has not provided browser , use edge browser by default
		if(browserName == null)
		{
			browserName = "firefox";
		}
		
		System.out.println("Considering browser =" +browserName);
		
		String url = prop.getProperty("url");
		
		bo = new BrowserOptionsManager(prop);
				
		switch(browserName.toLowerCase())
		{
		case "chrome":  System.setProperty("webdriver.chrome.driver", "C:\\Users\\Shilps\\.cache\\selenium\\chromedriver\\win32\\112.0.5615.49\\chromedriver.exe");
						//driver = new ChromeDriver();
						tlDriver.set(new ChromeDriver(bo.getChromeOptions()));
						break;
		case "edge": 	 //driver = new EdgeDriver();
						tlDriver.set(new EdgeDriver(bo.getEdgeOptions()));
						break;
		case "firefox": //driver = new FirefoxDriver();
						tlDriver.set(new FirefoxDriver(bo.getFirefoxOptions()));
						break;
		default: System.out.println("Please pass correct browser");
					throw new FrameworkException("INVALIDBROWSEREXCEPTION");
		}
		
		getDriver().manage().window().maximize();
		getDriver().manage().deleteAllCookies();
		getDriver().get(url);
		return getDriver();
	}
	
	public Properties initializeProperties()
	{
		Properties prop = new Properties();
		try
		{
		String environment = System.getProperty("env");
		
		//check if user has not provided any value. If not provided, use default value
		if(environment == null)
		{
			environment = "qa";
		}
				
		System.out.println("Considering environemnt = " +environment);
		
		switch(environment)
		{
			case "qa": fip = new FileInputStream("./src/main/resources/config/QA_Config.properties");
						 break;
			case "staging": fip = new FileInputStream("./src/main/resources/config/Staging_Config.properties");
		 				 break;
			case "uat": fip = new FileInputStream("./src/main/resources/config/UAT_Config.properties");
		 				 break;
			case "prod": fip = new FileInputStream("./src/main/resources/config/config.properties");
		 				 break;
			default: System.out.println("Please provide correct environment");
						throw new FrameworkException("INVALIDENVIRONMENT");
		}
		prop.load(fip);
		} 
		catch (IOException e) 
		{
				e.printStackTrace();
		}
		return prop;
	}
	
	public synchronized static WebDriver getDriver()
	{
		return tlDriver.get();
	}
	
	
	/**
	 * This method takes screenshot and returns path of the screenshot taken
	 */
	public static String getScreenshot() {
		File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		String path = System.getProperty("user.dir") + "/screenshot/" + System.currentTimeMillis() + ".png";
		File destination = new File(path);
		try 
		{
			FileUtils.copyFile(srcFile, destination);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

		return path;
	}
	
}
