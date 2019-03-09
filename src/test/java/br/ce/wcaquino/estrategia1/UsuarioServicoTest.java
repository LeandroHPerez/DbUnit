package br.ce.wcaquino.estrategia1;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.UsuarioService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsuarioServicoTest {
	private UsuarioService servico = new UsuarioService();
	private static Usuario usuarioGlobal;

	@Test
	public void test1_inserir() throws Exception {
		Usuario usuario = new Usuario("Usuario estrategia #1", "user@email.com", "passwd");
		usuarioGlobal = servico.salvar(usuario);
		Assert.assertNotNull(usuarioGlobal.getId());
	}
	
	@Test
	public void test2_consultar() throws Exception {
		Usuario usuario = servico.findById(usuarioGlobal.getId());
		Assert.assertEquals("Usuario estrategia #1", usuario.getNome());
	}
	
	@Test
	public void test3_alterar() throws Exception {
		Usuario usuario = servico.findById(usuarioGlobal.getId());
		usuario.setNome("Usuário alterado");
		Usuario usuarioAlterado = servico.salvar(usuario);
		Assert.assertEquals("Usuário alterado", usuarioAlterado.getNome());
	}
	
	@Test
	public void test4_excluir() throws Exception {
		servico.delete(usuarioGlobal);
		Usuario usuarioRemovido = servico.findById(usuarioGlobal.getId());
		Assert.assertNull(usuarioRemovido);
	}
}
