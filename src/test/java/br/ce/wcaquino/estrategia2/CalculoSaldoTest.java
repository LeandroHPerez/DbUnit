package br.ce.wcaquino.estrategia2;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.dao.SaldoDAO;
import br.ce.wcaquino.dao.impl.SaldoDAOImpl;
import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.TipoTransacao;
import br.ce.wcaquino.entidades.Transacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.TransacaoService;
import br.ce.wcaquino.service.UsuarioService;
import br.ce.wcaquino.utils.DataUtils;

public class CalculoSaldoTest {

	//para testar o saldo precisa de um
	// 1 usuario
	// 1 conta
	// 1 transacao
	
	//+deve considerar transacoes do mesmo Usuario
	//+deve considerar transacoes da mesma conta
	//+deve considerar transacoes pagas
	//+deve considerar transacoes atÃ© a data corrente 
		// +ontem / +hoje / +amanhÃ£
	//+deve somar receitas
	//+deve subtrair despesas
	
	
	//ATENÇÂO - antes de executar essa classe rodar o script MontarAmbiente.java do pacote dao.utils
	
	@Test
	public void deveCalcularSaldoCorreto() throws Exception {
		UsuarioService usuarioService = new UsuarioService();
		ContaService contaService = new ContaService();
		TransacaoService transacaoService = new TransacaoService();
		
		
		//Cria os usuários, contas e transações
		//Usuarios
		Usuario usuario = usuarioService.salvar(new Usuario("Usuario", "email@email.com", "123"));
		Usuario usuarioAlternativo = usuarioService.salvar(new Usuario("Usuario Alternativo", "email2@qualquer.com", "123"));
		
		//contas
		Conta conta = contaService.salvar(new Conta("Conta principal", usuario.getId()));
		Conta contaSecundaria = contaService.salvar(new Conta("Conta Secundaria", usuario.getId()));
		Conta contaUsuarioAlternativo = contaService.salvar(new Conta("Conta Usuario Alternativo", usuarioAlternativo.getId()));
		
		//transacoes
		//Transacao Correta / Saldo = 2
		transacaoService.salvar(new Transacao("Transacao inicial", "Envolvido", TipoTransacao.RECEITA, 
				new Date(), 2d, true, conta, usuario));
		
		//Trasancao usuario alternativo / Saldo = 2
		transacaoService.salvar(new Transacao("Transacao outro Usuario", "Envolvido", TipoTransacao.RECEITA, 
				new Date(), 4d, true, contaUsuarioAlternativo, usuarioAlternativo));
		
		//Transacao de outra conta / Saldo = 2
		transacaoService.salvar(new Transacao("Transacao outra conta", "Envolvido", TipoTransacao.RECEITA, 
				new Date(), 8d, true, contaSecundaria, usuario));
		
		//Transacao pendente / Saldo = 2
		transacaoService.salvar(new Transacao("Transacao pendente", "Envolvido", TipoTransacao.RECEITA, 
				new Date(), 16d, false, conta, usuario));
		
		//Transacao passada / Saldo = 34
		transacaoService.salvar(new Transacao("Transacao passada", "Envolvido", TipoTransacao.RECEITA, 
				DataUtils.obterDataComDiferencaDias(-1), 32d, true, conta, usuario));
		
		//Trasacao futura / Saldo = 34
		transacaoService.salvar(new Transacao("Transacao futura", "Envolvido", TipoTransacao.RECEITA, 
				DataUtils.obterDataComDiferencaDias(1), 64d, true, conta, usuario));
		
		//Transacao despesa / Saldo = -94
		transacaoService.salvar(new Transacao("Transacao despesa", "Envolvido", TipoTransacao.DESPESA, 
				new Date(), 128d, true, conta, usuario));
		
		//Testes de saldo com valor negativo da azar / Saldo = 162 
		transacaoService.salvar(new Transacao("Transacao da sorte", "Envolvido", TipoTransacao.RECEITA, 
				new Date(), 256d, true, conta, usuario));
		
		SaldoDAO saldoDAO = new SaldoDAOImpl();
		Assert.assertEquals(new Double(162d), saldoDAO.getSaldoConta(conta.getId()));
		Assert.assertEquals(new Double(8d), saldoDAO.getSaldoConta(contaSecundaria.getId()));
		Assert.assertEquals(new Double(4d), saldoDAO.getSaldoConta(contaUsuarioAlternativo.getId()));
	}
}
