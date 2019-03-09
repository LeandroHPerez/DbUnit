package br.ce.wcaquino.estrategia4;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.dao.SaldoDAO;
import br.ce.wcaquino.dao.impl.SaldoDAOImpl;
import br.ce.wcaquino.dao.utils.ConnectionFactory;
import br.ce.wcaquino.dbunit.ImportExport;
import br.ce.wcaquino.utils.DataUtils;

public class CalculoSaldoTest {

	// 1 usuario
	// 1 conta
	// 1 transacao
	
	//+deve considerar transacoes do mesmo usuário
	//+deve considerar transacoes da mesma conta
	//+deve considerar transacoes pagas
	//+deve considerar transacoes até a data corrente 
		// +ontem / +hoje / +amanhã
	//+deve somar receitas
	//+deve subtrair despesas
	
	@Test
	public void deveCalcularSaldoCorreto() throws Exception {
//		ImportExport.importarBanco("saldo.xml");
		
		
		//Importa o xml saldo.xml para popular o banco
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet dataSet = builder.build(new FileInputStream("massas" + File.separator + "saldo.xml"));
		
		//Troca as variaveis de datas dentro do xml por datas corretas, para que execute corretamente no dia o teste, ou seja, as datas n�o ficam chumbadas no xml
		ReplacementDataSet dataSetAlterado = new ReplacementDataSet(dataSet);
		dataSetAlterado.addReplacementObject("[hoje]", new Date()); //troca o valor [HOJE] do xml pela data atual
		dataSetAlterado.addReplacementObject("[ontem]", DataUtils.obterDataComDiferencaDias(-1)); //troca o valor [ontem] do xml pela data de ontem
		dataSetAlterado.addReplacementObject("[amanha]", DataUtils.obterDataComDiferencaDias(1)); //troca o valor [amanha] do xml pela data de amanha
		
		DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSetAlterado); //apaga todo o banco e depois insere os registros do xml
		
		
		SaldoDAO saldoDAO = new SaldoDAOImpl();
		Assert.assertEquals(new Double(162d), saldoDAO.getSaldoConta(1L));
		Assert.assertEquals(new Double(8d), saldoDAO.getSaldoConta(2L));
		Assert.assertEquals(new Double(4d), saldoDAO.getSaldoConta(3L));
	}
}
