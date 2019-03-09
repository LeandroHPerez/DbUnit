package br.ce.wcaquino.estrategia5;

import java.sql.Connection;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.dao.utils.ConnectionFactory;
import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.UsuarioService;

public class ContaServiceTest {
	
	ContaService service = new ContaService();
	UsuarioService userService = new UsuarioService();
	
	@BeforeClass
	public static void inserirConta() throws Exception {
		Connection conn = ConnectionFactory.getConnection();
		conn.createStatement().executeUpdate("DELETE FROM transacoes");
		conn.createStatement().executeUpdate("DELETE FROM contas");
		conn.createStatement().executeUpdate("DELETE FROM usuarios");
		conn.createStatement().executeUpdate("INSERT INTO usuarios (id, nome, email, senha) "
				+ "VALUES (1, 'Usuario de controle', 'usuario@email.com', 'passwd')");
		conn.createStatement().executeUpdate("INSERT INTO contas (id, nome, usuario_id) VALUES "
				+ "(1, 'Conta para testes', 1)");
		conn.createStatement().executeUpdate("INSERT INTO contas (id, nome, usuario_id) VALUES "
				+ "(2, 'Conta CT005 alteracao', 1)");
		conn.createStatement().executeUpdate("INSERT INTO contas (id, nome, usuario_id) VALUES "
				+ "(3, 'Conta para deletar', 1)");
	}

	@Test
	public void testInserir() throws Exception {
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		Conta contaSalva = service.salvar(conta);
		Assert.assertNotNull(contaSalva.getId());
		userService.printAll();
		service.printAll();
	}

	@Test
	public void testAlterar() throws Exception {
		Conta contaTeste = service.findByName("Conta CT005 alteracao");
		contaTeste.setNome("Conta alterada");
		Conta contaAlterada = service.salvar(contaTeste);
		Assert.assertEquals("Conta alterada", contaAlterada.getNome());
		service.printAll();
	}
	
	@Test
	public void testConsultar() throws Exception {
		Conta contaBuscada = service.findById(1L);
		Assert.assertEquals("Conta para testes", contaBuscada.getNome());
	}
	
	@Test
	public void testExcluir() throws Exception {
		Conta contaTeste = service.findByName("Conta para deletar");
		service.printAll();
		service.delete(contaTeste);
		Conta contaBuscada = service.findById(contaTeste.getId());
		Assert.assertNull(contaBuscada);
		service.printAll();
	}
}
