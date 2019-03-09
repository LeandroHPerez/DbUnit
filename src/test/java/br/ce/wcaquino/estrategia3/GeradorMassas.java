package br.ce.wcaquino.estrategia3;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import com.github.javafaker.Faker;

import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.UsuarioService;

public class GeradorMassas {
	
	public static final String CHAVE_CONTA_SB = "CONTA_SB";
	public static final String CHAVE_CONTA = "CONTA";

	public void gerarContaSeuBarriga() throws ClassNotFoundException, SQLException {
		
		ChromeDriver driver;

		System.out.println("A: Verificar se ha o driver do chrome em C:\\\\drivers_automacao\\\\chromedriver_win32_v2.40\\\\chromedriver.exe");
		
		String caminhoDriver = "C:\\drivers_automacao\\chromedriver_win32_v2.40\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", caminhoDriver);
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		driver.get("http://seubarriga.wcaquino.me/");
		
		driver.findElement(By.id("email")).sendKeys("a@a");
		driver.findElement(By.id("senha")).sendKeys("a");
		driver.findElement(By.tagName("button")).click();
		
		Faker faker = new Faker();
		String registro = faker.gameOfThrones().character() + " " + faker.gameOfThrones().dragon();
		driver.findElement(By.linkText("Contas")).click();
		driver.findElement(By.linkText("Adicionar")).click();
		driver.findElement(By.id("nome")).sendKeys(registro); //massa gerada
		driver.findElement(By.tagName("button")).click();
		driver.quit();
		
		new MassaDAOImpl().inserirMassa(CHAVE_CONTA_SB, registro);
	}
	
	public void gerarConta() throws Exception {
		Faker faker = new Faker();
		ContaService service = new ContaService();
		UsuarioService userService = new UsuarioService();
		Usuario usuarioGlobal = new Usuario(faker.name().fullName(), faker.internet().emailAddress(), faker.internet().password());
		Usuario usuarioSalvo = userService.salvar(usuarioGlobal);
		Conta conta = new Conta(faker.superhero().name(), usuarioSalvo);
		service.salvar(conta);
		new MassaDAOImpl().inserirMassa(CHAVE_CONTA, conta.getNome());
	}
	
	public static void main(String[] args) throws Exception {
//		GeradorMassas gerador = new GeradorMassas();
//		for(int i = 0; i < 10; i++) {
//			gerador.gerarConta();
//		}
		String massa = new MassaDAOImpl().obterMassa(CHAVE_CONTA_SB);
		System.out.println(massa);
	}
}
