package com.autotest.driver;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.autotest.enums.Key;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * Keywords函数库
 * 
 * @author Mark
 */
public class KeyWords {

	public RemoteWebDriver	driver;
	public Logger			log;
	private WebDriverWait	wait				= null;

	Actions					action				= null;

	/**
	 * Javascript执行器
	 */
	JavascriptExecutor		javascriptExecutor	= null;

	/**
	 * 构造函数
	 * 
	 * @param driver
	 * @param log
	 */
	public KeyWords(RemoteWebDriver driver, Logger log) {
		if (null == driver || null == log) {
			System.out.println("driver或者log为空，new KeyWords失败！系统退出！");
			System.exit(-1);
		}
		this.driver = driver;
		this.log = log;
		int waitTime = Integer
				.parseInt(BaseConfigUtil.getWaitElementLoadTime());
		this.wait = new WebDriverWait(driver, waitTime);
		javascriptExecutor = (JavascriptExecutor) this.driver;
		action = new Actions(driver);
	}

	/**
	 * 执行无返回值的js代码
	 * 
	 * @param js
	 */
	public void executeJS(String js) throws Exception {
		if (null != js && js.length() > 0) {
			javascriptExecutor.executeScript(js);
		}
	}

	/**
	 * 打开一个浏览器新窗口
	 * 
	 * @throws Exception
	 */
	public void openNewWindow() throws Exception {
		String js = "var result = window.open(\"about:blank\")";
		String result = executeJsReturnString(js);
		System.out.println(result);
	}

	/**
	 * 关闭窗口
	 * 
	 * @throws Exception
	 */
	public void closeWindow() throws Exception {
		String js = "var result = window.close()";
		String result = executeJsReturnString(js);
		System.out.println(result);
	}

	/**
	 * 执行返回值为String的js代码
	 * 
	 * @param js
	 * @return
	 */
	public String executeJsReturnString(String js) throws Exception {
		String result = null;
		if (!StringUtils.isEmpty(js)) {
			result = (String) javascriptExecutor.executeScript(js);
		}
		return result;
	}

	/**
	 * 打开页面
	 * 
	 * @param url
	 * @return
	 */
	public void open(String url) throws Exception {
		// 打开页面
		driver.get(url);
		waitPageLoad();
		log.info("open url - " + url);
	}

	/**
	 * 等待页面加载完毕
	 */
	public void waitPageLoad() throws Exception {
		String timeout = BaseConfigUtil.getWaitPageLoadTime();
		driver.manage().timeouts()
				.pageLoadTimeout(Long.parseLong(timeout), TimeUnit.SECONDS);
	}

	/**
	 * 等待，直到元素对象可点击
	 * 
	 * @param by
	 */
	public void waitElementToBeClickable(By by) throws Exception {
		wait.until(ExpectedConditions.elementToBeClickable(by));
	}

	/**
	 * 判断元素对象在一定的时间内可见，超时则认为不可见
	 * 
	 * @param by
	 */
	public boolean isElementVisible(By by) {

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			log.info("[" + by + "],元素可见!");
			return true;
		} catch (Exception e) {
			log.error("[" + by + "],元素不可见!");
			return false;
		}

	}

	/**
	 * 判断元素对象是否存在
	 * 
	 * @param by
	 * @return
	 */
	public boolean isElementExist(By by) {
		try {
			isElementVisible(by);
			driver.findElement(by);
			log.info("[" + by + "],元素存在!");
			return true;
		} catch (Exception e) {
			log.error("[" + by + "],元素不存在!");
			return false;
		}
	}

	/**
	 * 判断元素对象是否存在
	 * 
	 * @param by
	 * @return
	 */
	public boolean isElementExist2(By by) {
		try {
			ThreadUtil.sleep();
			driver.findElement(by);
			log.info("[" + by + "],元素存在!");
			return true;
		} catch (Exception e) {
			log.error("[" + by + "],元素不存在!");
			return false;
		}
	}

	/**
	 * 判断元素是否显示
	 * 
	 * @param by
	 * @return
	 */
	public boolean isVisible(By by) {
		boolean flag = false;
		try {
			WebElement webElement = driver.findElement(by);
			flag = webElement.isDisplayed();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取元素
	 * 
	 * @param by
	 * @return
	 */
	public WebElement getWebElement(By by) throws Exception {
		WebElement webElement = null;
		isElementVisible(by);
		webElement = driver.findElement(by);
		return webElement;
	}

	/**
	 * 获取元素
	 * 
	 * @param by
	 * @return
	 */
	public WebElement getWebElement2(By by) {
		WebElement webElement = null;
		try {
			webElement = driver.findElement(by);
			return webElement;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取元素组
	 * 
	 * @param by
	 * @return
	 */
	public List<WebElement> getWebElements(By by) throws Exception {
		List<WebElement> webElements = null;
		isElementVisible(by);
		webElements = driver.findElements(by);
		return webElements;
	}

	/**
	 * 点击元素
	 * 
	 * @param by
	 * @return
	 */
	public void click(By by) throws Exception {
		waitElementToBeClickable(by);
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Click on [" + by + "]");
	}

	/**
	 * 点击div
	 * 
	 * @param by
	 */
	public void clickDiv(By by) {
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Click on [" + by + "]");
	}

	/**
	 * 根据文本内容点击DIV
	 * 
	 * @param text
	 */
	public void clickDivByText(String text) {
		By by = By.xpath("//div[text()='" + text + "']");
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Click on [" + by + "]");
	}

	/**
	 * 根据文本内容点击
	 * 
	 * @param text
	 */
	public void clickByText(String text) {
		By by = By.xpath("//*[text()='" + text + "']");
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Click on [" + by + "]");
	}

	/**
	 * 根据文本内容点击
	 * 
	 * @param text
	 */
	public void clickByText2(String text) {
		By by = By.xpath("//a[text()='" + text + "']");
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Click on [" + by + "]");
	}

	/**
	 * 输入值
	 * 
	 * @param by
	 * @param value
	 * @return
	 */
	public void setValue(By by, String value) throws Exception {
		isElementVisible(by);
		WebElement element = driver.findElement(by);
		element.clear();
		element.sendKeys(value);
		log.info("Set the value of [" + by + "] to [" + value + "]");
	}

	/**
	 * 输入值
	 * 
	 * @param by
	 * @param value
	 * @return
	 */
	public void setValue2(By by, String value) throws Exception {
		WebElement element = driver.findElement(by);
		element.clear();
		element.sendKeys(value);
		log.info("Set the value of [" + by + "] to [" + value + "]");
	}

	/**
	 * 判断是否存在指定文本的元素或元素组
	 * 
	 * @param text
	 * @return
	 */
	public boolean isExistByText(String text) {
		boolean flag = false;
		try {
			String xpath = "//*[contains(text(),'" + text + "')]";
			List<WebElement> webElements = driver.findElements(By.xpath(xpath));
			if (webElements != null && webElements.size() > 0) {
				flag = true;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 勾选checkbox
	 * 
	 * @param by
	 * @return
	 */
	public void check(By by) throws Exception {
		waitElementToBeClickable(by);
		WebElement element = driver.findElement(by);
		element.click();
		log.info("Check on [" + by + "]");
	}

	/**
	 * 取消checkbox
	 * 
	 * @param by
	 * @return
	 */
	public void uncheck(By by) throws Exception {
		isElementVisible(by);
		WebElement element = driver.findElement(by);
		element.clear();
		log.info("Uncheck on [" + by + "]");
	}

	/**
	 * 获取文本
	 * 
	 * @param by
	 * @return
	 */
	public String getText(By by) throws Exception {
		isElementVisible(by);
		WebElement element = driver.findElement(by);
		if (null != element) {
			return element.getText();
		}
		return null;
	}

	/**
	 * 获取文本
	 * 
	 * @param by
	 * @return
	 */
	public String getText2(By by) {
		WebElement element = driver.findElement(by);
		if (null != element) {
			return element.getText();
		}
		return null;
	}

	/**
	 * 获得属性值
	 * 
	 * @param by
	 * @param attributeName
	 * @return
	 */
	public String getAttribute(By by, String attributeName) throws Exception {
		isElementVisible(by);
		WebElement element = driver.findElement(by);
		if (null != element) {
			return element.getAttribute(attributeName);
		}
		return null;
	}

	/**
	 * 获得属性值
	 * 
	 * @param webElement
	 * @param attributeName
	 * @return
	 */
	public String getAttribute2(WebElement webElement, String attributeName) {
		return webElement.getAttribute(attributeName);
	}

	/**
	 * 通过文本选择
	 * 
	 * @param by
	 * @param text
	 * @return
	 */
	public void selectByText(By by, String text) throws Exception {
		Select select = new Select(driver.findElement(by));
		select.selectByVisibleText(text);
	}

	/**
	 * 通过value选择
	 * 
	 * @param by
	 * @param value
	 * @return
	 */
	public void selectByValue(By by, String value) throws Exception {
		Select select = new Select(driver.findElement(by));
		select.selectByValue(value);
	}

	/**
	 * 通过序号选择
	 * 
	 * @param by
	 * @param index
	 * @return
	 */
	public void selectByIndex(By by, int index) throws Exception {
		Select select = new Select(driver.findElement(by));
		select.selectByIndex(index);
	}

	/**
	 * 是否选中
	 * 
	 * @param by
	 * @return
	 */
	public boolean isSelected(By by) throws Exception {
		isElementVisible(by);
		WebElement element = driver.findElement(by);
		if (null != element) {
			return element.isSelected();
		}
		return false;
	}

	/**
	 * 判断弹出框是否存在
	 * 
	 * @return
	 */
	public boolean isAlertPresent() throws Exception {
		driver.switchTo().alert();
		return true;
	}

	/**
	 * 判断是否有Alert，如果有，则返回true
	 * 
	 * @return
	 */
	public boolean isExistAlert() {
		boolean flag = false;
		try {
			driver.switchTo().alert();
			flag = true;
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 点击OK
	 * 
	 * @return
	 */
	public void acceptAlert() throws Exception {
		if (isAlertPresent()) {
			Alert alert = driver.switchTo().alert();
			alert.accept();
		}
	}

	/**
	 * 点击Cancel
	 * 
	 * @return
	 */
	public void dismissAlert() throws Exception {
		if (isAlertPresent()) {
			Alert alert = driver.switchTo().alert();
			alert.dismiss();
		}
	}

	/**
	 * 获取弹出框文本
	 * 
	 * @return
	 */
	public String getAlertText() throws Exception {
		String text = null;
		if (isAlertPresent()) {
			Alert alert = driver.switchTo().alert();
			text = alert.getText();
		}
		return text;
	}

	/**
	 * 模拟键盘按键
	 * 
	 * @param key
	 */
	public void sendKeys(Key key) {
		switch (key) {
			case PAGE_DOWN:
				action.sendKeys(Keys.PAGE_DOWN).perform();
				break;
			case PAGE_UP:
				action.sendKeys(Keys.PAGE_UP).perform();
				break;
			case END:
				action.sendKeys(Keys.END).perform();
				break;
			case ENTER:
				action.sendKeys(Keys.ENTER).perform();
				break;
			case ESCAPE:
				action.sendKeys(Keys.ESCAPE).perform();
				break;
			case BACK_SPACE:
				action.sendKeys(Keys.BACK_SPACE).perform();
				break;
			case HOME:
				action.sendKeys(Keys.HOME).perform();
				break;
			case EQUALS:
				action.sendKeys(Keys.EQUALS).perform();
				break;
			case ALT:
				action.sendKeys(Keys.ALT).perform();
				break;
			case CONTROL:
				action.sendKeys(Keys.CONTROL).perform();
				break;
			case TAB:
				action.sendKeys(Keys.TAB).perform();
				break;
			case CANCEL:
				action.sendKeys(Keys.CANCEL).perform();
				break;
			case F5:
				action.sendKeys(Keys.F5).perform();
				break;
			case LEFT:
				action.sendKeys(Keys.LEFT).perform();
				break;
			case RETURN:
				action.sendKeys(Keys.RETURN).perform();
				break;
			case RIGHT:
				action.sendKeys(Keys.RIGHT).perform();
				break;
			case UP:
				action.sendKeys(Keys.UP).perform();
				break;
			case DELETE:
				action.sendKeys(Keys.DELETE).perform();
				break;
			case DOWN:
				action.sendKeys(Keys.DOWN).perform();
				break;
			default:
				break;
		}
	}

	/**
	 * 鼠标悬浮
	 * 
	 * @param by
	 */
	public void Focus(By by) throws Exception {
		WebElement button = getWebElement(by);
		action.contextClick(button).perform();
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ESCAPE);

	}

	/**
	 * 将鼠标移动到指定元素上
	 * 
	 * @param by
	 * @throws Exception
	 */
	public void moveToElement(By by) throws Exception {
		WebElement webElement = getWebElement(by);
		action.moveToElement(webElement);
	}

	/**
	 * 获取当前driver驱动的所有窗口的handle然后进行比对筛选，如果窗口的title符合预期 则切换，并返回true，反之则返回false
	 * 
	 * @param windowTitle
	 * @return
	 */
	public boolean switchToWindow(String windowTitle) throws Exception {
		boolean flag = false;
		String currentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		for (String s : handles) {
			if (s.equals(currentHandle))
				continue;
			else {
				driver.switchTo().window(s);
				if (driver.getTitle().contains(windowTitle)) {
					flag = true;
					log.info("Switch to window: " + windowTitle
							+ " successfully!");
					break;
				} else
					continue;
			}
		}
		return flag;
	}

	/**
	 * 切换回默认的iframe
	 */
	public void switchToDefaultFrame() {
		driver.switchTo().defaultContent();
	}

	/**
	 * 根据frameElement切换frame
	 * 
	 * @param frameElement
	 */
	public void switchToFrame(WebElement frameElement) throws Exception {
		driver.switchTo().defaultContent();
		driver.switchTo().frame(frameElement);
	}

	/**
	 * 根据frameId切换frame
	 * 
	 * @param frameId
	 */
	public void switchToFrame(String frameId) throws Exception {
		driver.switchTo().defaultContent();
		driver.switchTo().frame(frameId);
	}

	/**
	 * 根据index切换frame
	 * 
	 * @param index
	 */
	public void switchToFrame(int index) throws Exception {
		driver.switchTo().defaultContent();
		driver.switchTo().frame(index);
	}

	/**
	 * 获得窗口名
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getTitle() throws Exception {
		return driver.getTitle();
	}

	/**
	 * 刷新页面
	 */
	public void refreshPage() {
		driver.navigate().refresh();
	}

	public void goBack() {
		driver.navigate().back();
	}

	/**
	 * 功能：让元素失去焦点
	 * 
	 * @param id
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void blurById(String id) throws Exception {
		// $('#id').blur();
		String jquery = "$('#" + id + "').blur();";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

	/**
	 * 功能：让元素失去焦点
	 * 
	 * @param name
	 * @param elementType
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void blurByName(String elementType, String name) throws Exception {
		// $('input[name="name"]').blur();
		String jquery = "$('" + elementType + "[name=\"" + name
				+ "\"]').blur()";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

	/**
	 * 功能：让元素失去焦点
	 * 
	 * @param classStr
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void blurByClass(String classStr) throws Exception {
		// $('.class').blur();
		String jquery = "$('." + classStr + "').blur();";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

	/**
	 * 功能：让元素获得焦点
	 * 
	 * @param id
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void focusById(String id) throws Exception {
		// $('#id').foucs();
		String jquery = "$('#" + id + "').foucs();";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

	/**
	 * 功能：让元素获得焦点
	 * 
	 * @param name
	 * @param elementType
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void focusByName(String elementType, String name) throws Exception {
		// $('input[name="name"]').foucs();
		String jquery = "$('" + elementType + "[name=\"" + name
				+ "\"]').foucs();";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

	/**
	 * 功能：让元素获得焦点
	 * 
	 * @param classStr
	 * @return void
	 * @throws Exception
	 * 
	 */
	public void focusByClass(String classStr) throws Exception {
		// $('.class').foucs();
		String jquery = "$('." + classStr + "').foucs();";
		log.info("execute jquery [" + jquery + "]");
		executeJS(jquery);
	}

}
