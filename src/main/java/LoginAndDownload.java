import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v135.network.Network;
import org.openqa.selenium.devtools.v135.network.model.Request;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.Duration;
import java.util.*;


public class LoginAndDownload {
    private static final Logger log = LoggerFactory.getLogger(LoginAndDownload.class);

    private static String chromeDriverUrl = "chromedriver-win64/chromedriver.exe";
    private static String username = "15871364924";
    private static String password = "1624289434jL";
    private static String bookPageUrl = "https://basic.smartedu.cn/tchMaterial/detail?contentType=assets_document&contentId=4c34b03c-933c-4a67-b211-f5d79fb05001&catalogType=tchMaterial&subCatalog=dzjc";

    private static String mp3Url;

    public static void main(String[] args) {


//        initialize();

        // 加载driver文件
        lodgingDriverFile();

        // 设置Chrome浏览器选项
        WebDriver driver = setDriverOptions();

        // 获取DevTools实例
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // 启用网络监听
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));




        try {
            //登录
            login(driver);


            //教科书页面
            driver.get(bookPageUrl);

            // 等待页面出现（
            waitLoading(driver, By.className("index-module_title_bnE9V"));


            // 找到音频列表按
            WebElement allAudioElement = driver.findElement(By.className("audioList-module_audio-list_-XAH6"));
            List<WebElement> clickList = allAudioElement.findElements(By.cssSelector("div[class*='audioList-module_center']"));

            System.out.println(clickList);

            // 监听网络请求事件
            devTools.addListener(Network.requestWillBeSent(), requestWillBeSent -> {
                Request request = requestWillBeSent.getRequest();
                String url = request.getUrl();

                if (url.startsWith("blob:https://basic.smartedu.cn")) {
                    mp3Url = url;

                    System.out.println("Intercepted JavaScript resource: " + url);
                }
            });


            // 顺序点击
            for (WebElement element : clickList) {
                element.click();
                //等待监听请求
                Thread.sleep(5000);
                // 使用LinkedHashSet进行去重并保持顺序

                //List<Mp3ReSource> urlList = new ArrayList<>();
                String text = element.getText();
                //urlList.add(new Mp3ReSource(mp3Url, text));
                // 下载音频文件
                downloadFile((ChromeDriver) driver, mp3Url, text);


            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭浏览器
            driver.quit();
        }
    }

    private static void initialize() {
        try {
            // 要杀死的进程名称（这里以notepad.exe为例）
            String processName = "chromedriver.exe";
            // 执行taskkill命令
            Process process = Runtime.getRuntime().exec("taskkill /F /IM " + processName);
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void waitLoading(WebDriver driver, By locator) {
        WebDriverWait waitDoc = new WebDriverWait(driver, Duration.ofSeconds(100));
        waitDoc.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private static WebDriver setDriverOptions() {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", "C:\\Users\\zmmmmmm\\Downloads\\mp3");
        //禁止弹出下载提示框，让文件自动下载
//        prefs.put("download.prompt_for_downloads", false);
        // 禁用下载提示框
        prefs.put("download.prompt_for_download", false);
        // 启用下载目录升级
        prefs.put("download.directory_upgrade", true);
        // 将偏好设置添加到 ChromeOptions 中
        options.setExperimentalOption("prefs", prefs);

        // 创建ChromeDriver实例
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

    private static void lodgingDriverFile() {
        try {
            // 加载ChromeDriver
            ClassLoader classLoader = LoginAndDownload.class.getClassLoader();
            URL resourceUrl = classLoader.getResource(chromeDriverUrl);

            // 设置ChromeDriver路径
            System.setProperty("webdriver.chrome.driver", resourceUrl.getPath());

        } catch (RuntimeException e) {
            log.error("加载ChromeDriver失败", e);
        }
    }

    /**
     * 模拟登录
     *
     * @param driver ChromeDriver
     */
    private static void login(WebDriver driver) {
        // 打开登录页面
        driver.get("https://auth.smartedu.cn/uias/login");

        // 等待登录出
        waitLoading(driver, By.id("tmpPassword"));


        // 自动注入账号密码
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("tmpPassword"));

        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);


        Scanner scanner = new Scanner(System.in);
        System.out.println("请手动点击登录按钮，然后按任意键继续...");
        scanner.nextLine();
    }

    public static void downloadFile(ChromeDriver driver, String blobLink, String newName) throws InterruptedException {
        try {

            // 创建一个隐藏的下载链接
            WebElement downloadLink = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    "var link = document.createElement('a'); " +
                            "link.href = '" + blobLink + "'; " +
                            "link.download = '"+newName+"mp3'; " +
                            "link.style.display = 'none'; " +
                            "document.body.appendChild(link); " +
                            "return link;");

            // 模拟点击隐藏链接触发下载
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadLink);

            // 等待下载完成
            Thread.sleep(1000);

            JavascriptExecutor js = (JavascriptExecutor) driver;
//            js.executeScript("var document.querySelector('a[download=\"" + newName +"mp3\"]');" +
//                    "if (link) { link.parentNode.remove(link); }");


        } catch (Exception e) {
            throw e;
        }





    }
}    