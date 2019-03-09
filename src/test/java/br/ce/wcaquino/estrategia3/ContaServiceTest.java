package br.ce.wcaquino.estrategia3;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javafaker.Faker;

import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.UsuarioService;

public class ContaServiceTest {
	
	static Faker faker = new Faker();
	ContaService service = new ContaService();
	UsuarioService userService = new UsuarioService();
	
	@Test
	public void testInserir() throws Exception {
		Usuario usuario = new Usuario(faker.name().fullName(), faker.internet().emailAddress(), faker.internet().password());
		usuario = userService.salvar(usuario);
		Conta conta = new Conta(faker.superhero().name(), usuario);
		Conta contaSalva = service.salvar(conta);
		Assert.assertNotNull(contaSalva.getId());
		userService.printAll();
		service.printAll();
	}

	@Test
	public void testAlterar() throws Exception {
		Conta contaTeste = service.findByName(new MassaDAOImpl().obterMassa(GeradorMassas.CHAVE_CONTA));
		String novoNome = faker.ancient().god() + " " + faker.ancient().titan();
		contaTeste.setNome(novoNome);
		Conta contaAlterada = service.salvar(contaTeste);
		Assert.assertEquals(novoNome, contaAlterada.getNome());
		service.printAll();
	}
	
	@Test
	public void testConsultar() throws Exception {
		String nomeConta = new MassaDAOImpl().obterMassa(GeradorMassas.CHAVE_CONTA);
		Conta contaTeste = service.findByName(nomeConta);
		Conta contaBuscada = service.findById(contaTeste.getId());
		Assert.assertEquals(contaTeste.getNome(), contaBuscada.getNome());
	}
	
	@Test
	public void testExcluir() throws Exception {
		Conta contaTeste = service.findByName(new MassaDAOImpl().obterMassa(GeradorMassas.CHAVE_CONTA));
		service.printAll();
		service.delete(contaTeste);
		Conta contaBuscada = service.findById(contaTeste.getId());
		Assert.assertNull(contaBuscada);
		service.printAll();
	}
}
