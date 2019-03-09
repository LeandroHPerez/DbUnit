package br.ce.wcaquino.estrategia1;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContaTesteWeb {

	private ChromeDriver driver;
	
	@Before
	public void login() {
		System.out.println("A: Verificar se ha o driver do chrome em C:\\\\drivers_automacao\\\\chromedriver_win32_v2.40\\\\chromedriver.exe");
		
		String caminhoDriver = "C:\\drivers_automacao\\chromedriver_win32_v2.40\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", caminhoDriver);
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("http://seubarriga.wcaquino.me/login");
		
		driver.findElement(By.id("email")).sendKeys("a@a");
		driver.findElement(By.id("senha")).sendKeys("a");
		driver.findElement(By.tagName("button")).click();
		
	}
	
	@Test
	public void test_1_inserir() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Adicionar")).click();
		driver.findElement(By.id("nome")).sendKeys("Conta estrategia #1");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta adicionada com sucesso!", msg);
	}
	
	@Test
	public void test_2_consultar() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estrategia #1')]/..//a")).click();
		String nomeConta = driver.findElement(By.id("nome")).getAttribute("value");
		Assert.assertEquals("Conta estrategia #1", nomeConta);
	}
	
	@Test
	public void test_3_alterar() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estrategia #1')]/..//a")).click();
		driver.findElement(By.id("nome")).clear();
		driver.findElement(By.id("nome")).sendKeys("Conta estrategia #1 Alterada");
		driver.findElement(By.tagName("button")).click();
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta alterada com sucesso!", msg);
	}

	@Test
	public void test_4_excluir() {
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Listar")).click();
		driver.findElement(By.xpath("//td[contains(text(), 'Conta estrategia #1')]/..//a[2]")).click(); //contém o texto, não é o texto exato
		String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();
		Assert.assertEquals("Conta removida com sucesso!", msg);
	}
	
	@After
	public void fechar() {
		driver.quit();
	}
}
