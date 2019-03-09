package br.ce.wcaquino.estrategia4;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.dao.utils.ConnectionFactory;
import br.ce.wcaquino.dbunit.ImportExport;
import br.ce.wcaquino.entidades.Conta;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.service.ContaService;
import br.ce.wcaquino.service.UsuarioService;

public class ContaServiceTestDbUnit {
	
	ContaService service = new ContaService();
	UsuarioService userService = new UsuarioService();
	
	@Test
	public void testInserir() throws Exception {
		ImportExport.importarBanco("est4_inserirConta.xml");
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		Conta contaSalva = service.salvar(conta);
		Assert.assertNotNull(contaSalva.getId());
	}
	
	@Test //uma estrat�gia de compara��o de dados utilizando dbunit - compara APENAS algumas tabelas
	public void testInserir_Filter() throws Exception { //faz a compara��o de tabelas excluindo a coluna x de forma mais simples, utilizando filtros
		ImportExport.importarBanco("est4_inserirConta.xml");
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		service.salvar(conta);
		
		//estado atual do banco
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		IDataSet estadoFinalBanco = dbConn.createDataSet();
		
		//estado esperado (XML)
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		FlatXmlDataSet dataSetEsperado = builder.build(new FileInputStream("massas" + File.separator + "est4_inserirConta_saida.xml"));
		
		//Comparar os dois estados - compara��o de dados esperados e atuais entre tabelas do banco de dados - dessa vez UTILIZANDO FILTROS que servem apra excluir colunas da compara��o para n�o gerar falsos asserts, por exemplo devido a ids de autoincremento em que o resultado esperado tem um e ao inserir um novo registro teremos outro id
		ITable contasAtualFiltradas = DefaultColumnFilter.excludedColumnsTable(estadoFinalBanco.getTable("contas"), new String[] {"id"});   //na tabela contas, excluir a coluna id da compara��o entre tabelas
		ITable contasEsperadoFiltradas = DefaultColumnFilter.excludedColumnsTable(dataSetEsperado.getTable("contas"), new String[] {"id"}); //na tabela contas, excluir a coluna id da compara��o entre tabelas
		
		ITable usuarioAtualFiltradas = DefaultColumnFilter.excludedColumnsTable(estadoFinalBanco.getTable("usuarios"), new String[] {"conta_principal_id"}); //na tabela usuarios, excluir a coluna conta_principal_id da compara��o entre tabelas
		ITable usuarioEsperadoFiltradas = DefaultColumnFilter.excludedColumnsTable(dataSetEsperado.getTable("usuarios"), new String[] {"conta_principal_id"});
		
		Assertion.assertEquals(contasEsperadoFiltradas, contasAtualFiltradas); //faz a compara��o do dbunit entre tabelas
		Assertion.assertEquals(usuarioEsperadoFiltradas, usuarioAtualFiltradas);
	}
	
	@Test //uma estrat�gia de compara��o de dados utilizando dbunit - compara o banco inteiro
	public void testInserir_Assertion() throws Exception { //os testes Assertion s�o valida��es de estado de banco de dados feitos atrav�s de dbunit
		ImportExport.importarBanco("est4_inserirConta.xml");
		Usuario usuario = userService.findById(1L);
		Conta conta = new Conta("Conta salva", usuario);
		Conta contaSalva = service.salvar(conta);
		
		//estado atual do banco
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		IDataSet estadoFinalBanco = dbConn.createDataSet();
		
		//estado esperado (XML)
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		FlatXmlDataSet dataSetEsperado = builder.build(new FileInputStream("massas" + File.separator + "est4_inserirConta_saida.xml"));
		
		//Comparar os dois estados do banco de dados
//		Assertion.assertEquals(dataSetEsperado, estadoFinalBanco); //assertion � uma classe do DBUNIT e n�o do junit
		
		
		//Basicamente o tratamento abaixo serve para ignorar os erros em que o xml esperado do banco tem um "id antigo" e o novo registro inclu�do no banco tem um id novo, rec�m gerado pelo bd, ou seja, o assert falha pela divergencia nos dados, sendo que � apenas o id com autoincremento
		DiffCollectingFailureHandler handler = new DiffCollectingFailureHandler();
		Assertion.assertEquals(dataSetEsperado, estadoFinalBanco, handler); //assertion � uma classe do DBUNIT e n�o do junit
		List<Difference> erros = handler.getDiffList(); //lista os erros encontrados na compara��o
		boolean erroReal = false;
		for(Difference erro: erros) {
			System.out.println(erro.toString());
			if(erro.getActualTable().getTableMetaData().getTableName().equals("contas")) { //se o nome da tabela em que foi encontrado o erro for igual a contas
				if(erro.getColumnName().equals("id")) { //se o erro for da coluna id
					if(erro.getActualValue().toString().equals(contaSalva.getId().toString())) { //se o valor do erro retornado (id atual da tabela contas) for igual ao id da conta que acabou de ser inserida e est� armazenado no obj contaSalva.getId()
						continue; //ignora o erro, o handler sempre ignora erros, se quiser pegar  o erro deve-se fornecer o tratamento para que o erro seja lan�ado
					} else {
						System.out.println("Id errado mesmo!");
						erroReal = true;
					}
				} else { // se for outro tipo de erro que n�o o esperado de ids diferentes o erro � real e ser� tratado
					erroReal = true;
				}
			} else { // se n�o for um erro de id (erro esperado) � um erro real
				erroReal = true;
			}
		}
		Assert.assertFalse(erroReal); //trata de seu erro ou n�o na valida��o do bd
		
	}

	@Test
	public void testAlterar() throws Exception {
		ImportExport.importarBanco("est4_umaConta.xml");
		Conta contaTeste = service.findByName("Conta para testes");
		contaTeste.setNome("Conta alterada");
		Conta contaAlterada = service.salvar(contaTeste);
		Assert.assertEquals("Conta alterada", contaAlterada.getNome());
		service.printAll();
	}
	
	@Test
	public void testConsultar() throws Exception {
		ImportExport.importarBanco("est4_umaConta.xml");
		Conta contaBuscada = service.findById(1L);
		Assert.assertEquals("Conta para testes", contaBuscada.getNome());
	}
	
	@Test
	public void testExcluir() throws Exception {
		ImportExport.importarBanco("est4_umaConta.xml");
		Conta contaTeste = service.findByName("Conta para testes");
		service.printAll();
		service.delete(contaTeste);
		Conta contaBuscada = service.findById(contaTeste.getId());
		Assert.assertNull(contaBuscada);
		service.printAll();
	}
}