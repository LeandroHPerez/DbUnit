package br.ce.wcaquino.estrategia5;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class ContaTesteWeb {

	private static ChromeDriver driver;
	
	@BeforeClass
	public static void reset() {
		System.out.println("A: Verificar se ha o driver do chrome em C:\\\\drivers_automacao\\\\chromedriver_win32_v2.40\\\\chromedriver.exe");
		
		String caminhoDriver = "C:\\drivers_automacao\\chromedriver_win32_v2.40\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", caminhoDriver);
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		driver.get("http://seubarriga.wcaquino.me/");
		
		driver.findElement(By.id("email")).sendKeys("a@a");
		driver.findElement(By.id("senha")).sendKeys("a");
		driver.findElement(By.tagName("button")).click();
		
		driver.findElement(By.linkText("reset")).click(); //clica no botão invisivel reset na webapp
	}
	
	@Test
	public void inserir() throws ClassNotFoundException, SQLException {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Adicionar")).click();
		driver.findElement(By.id("nome")).sendKeys("Conta estrategia #5");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta adicionada com sucesso!", msg);
	}
	
	@Test
	public void consultar() throws ClassNotFoundException, SQLException {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta para saldo')]/..//a")).click();
		String nomeConta = driver.findElement(By.id("nome")).getAttribute("value");
		Assert.assertEquals("Conta para saldo", nomeConta);
	}
	
	@Test
	public void alterar() throws ClassNotFoundException, SQLException {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta para alterar')]/..//a")).click();
		driver.findElement(By.id("nome")).sendKeys(" Alterada");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta alterada com sucesso!", msg);
	}

	@Test
	public void excluir() throws ClassNotFoundException, SQLException {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta mesmo nome')]/..//a[2]")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta removida com sucesso!", msg);
	}
	
	@AfterClass
	public static void fechar() {
		driver.quit();
	}
}
