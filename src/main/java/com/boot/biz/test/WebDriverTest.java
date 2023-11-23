package com.boot.biz.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * options.add_argument('--disable-infobars') # 禁止策略化
 *
 * options.add_argument('--no-sandbox') # 解决DevToolsActivePort文件不存在的报错
 *
 * options.add_argument('window-size=1920x3000') # 指定浏览器分辨率
 *
 * options.add_argument('--disable-gpu') # 谷歌禁用GPU加速
 *
 * options.add_argument('--incognito') # 隐身模式（无痕模式）
 *
 * options.add_argument('--disable-javascript') # 禁用javascript
 *
 * options.add_argument('--start-maximized') # 最大化运行（全屏窗口）,不设置，取元素会报错
 *
 * options.add_argument('--hide-scrollbars') # 隐藏滚动条, 应对一些特殊页面
 *
 * options.add_argument('blink-settings=imagesEnabled=false') # 不加载图片, 提升速度
 *
 * options.add_argument('--headless') # 浏览器不提供可视化页面（无头模式）. linux下如果系统不支持可视化不加这条会启动失败
 *
 * options.binary_location = r"C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" # 手动指定使用的浏览器位置
 *
 * chrome_options.add_experimental_option("debuggerAddress", "127.0.0.1:9222") #调用原来的浏览器，不用再次登录即可重启
 *
 * options.add_argument('lang=en_US') # 设置语言
 *
 * options.add_argument('User-Agent=Mozilla/5.0 (Linux; U; Android 8.1.0; zh-cn; BLA-AL00 Build/HUAWEIBLA-AL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/8.9 Mobile Safari/537.36')
 * @author JackMeng on 2023-11-14 13:46.
 */
public class WebDriverTest {

    public static void main2(String[] args) throws Exception {
        String path = "/Users/jackmeng/Desktop/test2/chromedriver-mac-x64/chromedriver";

        // Optional. If not specified, WebDriver searches the PATH for chromedriver.
        System.setProperty("webdriver.chrome.driver", path);

        ChromeOptions chromeOptions = new ChromeOptions();

        //无头模式
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.setBinary("/Users/jackmeng/Desktop/test2/chrome-mac-x64/Google Chrome for Testing.app");

        //禁止加载图片
        chromeOptions.addArguments("blink-settings=imagesEnabled=false");
        chromeOptions.addArguments("--disable-javascript");


        WebDriver driver = new ChromeDriver(chromeOptions);

        // Navigate to Url
        driver.get("https://geoip.com/");

//        Thread.sleep(1000);

        // Click on the element
        WebElement input = driver.findElement(By.xpath("//input[@name='ip']"));
        input.clear();
        //Enter Text
        String ip = "112.124.32.111";
        input.sendKeys(ip);

//        Thread.sleep(1000);

        WebElement submit = driver.findElement(By.xpath("//input[@name='submit']"));
        submit.click();

        WebElement p = driver.findElement(By.xpath("//div[@class='entry-content']/p[2]"));

        String str = p.getText();

        System.out.println("--->"+str);

//        Thread.sleep(5*1000);  // Let the user actually see something!

        driver.close();
        driver.quit();
    }

    static final String APP_URL = "https://www.baidu.com";
    static final String HOST_URL = "http://10.10.1.85:4444/wd/hub";

    public static void main(String[] args) throws Exception{

        ChromeOptions chromeOptions = new ChromeOptions();

        //无头模式
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.setBinary("/Users/jackmeng/Desktop/test2/chrome-mac-x64/Google Chrome for Testing.app");

        //禁止加载图片
        chromeOptions.addArguments("blink-settings=imagesEnabled=false");
        chromeOptions.addArguments("--disable-javascript");

        WebDriver driver = new RemoteWebDriver(new URL(HOST_URL),chromeOptions);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        // Navigate to Url
        driver.get("https://geoip.com/");

//        Thread.sleep(1000);

        // Click on the element
        WebElement input = driver.findElement(By.xpath("//input[@name='ip']"));
        input.clear();
        //Enter Text
        String ip = "112.124.32.111";
        input.sendKeys(ip);

//        Thread.sleep(1000);

        WebElement submit = driver.findElement(By.xpath("//input[@name='submit']"));
        submit.click();

        WebElement p = driver.findElement(By.xpath("//div[@class='entry-content']/p[2]"));

        String str = p.getText();

        System.out.println("--->"+str);

        Thread.sleep(5*1000);  // Let the user actually see something!

        driver.close();
        driver.quit();
    }




}
