package br.ce.wcaquino.dbunit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import br.ce.wcaquino.dao.utils.ConnectionFactory;

public class ImportExport {

	public static void main(String[] args) throws Exception {
		//exportarBanco("saldo.xml");
		exportarBanco("saidaFiltrada.xml");
        //importarBanco("saida.xml");
		//importarBanco("entrada.xml");
		importarBanco("saidaFiltrada.xml");
	}

	public static void importarBanco(String massa) throws DatabaseUnitException, SQLException, ClassNotFoundException, DataSetException, FileNotFoundException {		
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet dataSet = builder.build(new FileInputStream("massas" + File.separator + massa));
		
		List<String> tabelas = obterTabelas();
		
		desabilitarTriggers(tabelas); //desabilita as triggers/constraints do banco para evitar erros de constraint em delete, inserts etc - erros de relacionamento ciclico
		DatabaseOperation.CLEAN_INSERT.execute(dbConn, dataSet); //operação de limpar tabelas e inserir dados
		habilitarTriggers(tabelas); //reabilita as triggers/constraints do banco que foram previamente desabilitadas via código
		
		atualizarSequences(tabelas); //ajusta as sequences via codigo para evitar erros de o xml tentar inserir um regsitro de uma sequence que já existe
	}

	//essa é a estrategia que funciona no banco postgrees
	private static void atualizarSequences(List<String> tabelas) throws ClassNotFoundException, SQLException {
		for(String tabela: tabelas) {
			Statement stmt = ConnectionFactory.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM public." + tabela);
			rs.next();
			Long id = rs.getLong(1);
			rs.close();
			stmt.close();
			if(id > 0) { //caso já existam registro/id inseridos no banco, ou seja, já existe id > 0
				stmt = ConnectionFactory.getConnection().createStatement();
				System.out.println("Sequence atualizada manualmente/codigo na tabela " + tabela + " > " + (id+1));
				stmt.executeUpdate("ALTER SEQUENCE " + tabela + "_id_seq RESTART WITH " + (id + 1)); //inicia a sequence via código com o valor desejado, no caso, reseta a sequence ja levando em consideração os registro inseridos anteriormente via xml
				stmt.close();
			}
		}
	}

	//essa é a estrategia que funciona no banco postgrees
	private static void desabilitarTriggers(List<String> tabelas) throws ClassNotFoundException, SQLException {
		for(String tabela: tabelas) {
			System.out.println("desabilitarTriggers --"+tabela);
			Statement stmt = ConnectionFactory.getConnection().createStatement();
			stmt.executeUpdate("ALTER TABLE public."+tabela+" disable trigger all");
			stmt.close();
		}
	}

	//essa é a estrategia que funciona no banco postgrees
	private static void habilitarTriggers(List<String> tabelas) throws ClassNotFoundException, SQLException {
		for(String tabela: tabelas) {
			System.out.println("habilitarTriggers ++"+tabela);
			Statement stmt = ConnectionFactory.getConnection().createStatement();
			stmt.executeUpdate("ALTER TABLE public."+tabela+" enable trigger all");
			stmt.close();
		}
	}

	//essa é a estrategia que funciona no banco postgrees
	private static List<String> obterTabelas() throws ClassNotFoundException, SQLException {
		List<String> tabelas = new ArrayList<String>();
		ResultSet rs = ConnectionFactory.getConnection().createStatement().executeQuery(
				"SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
		while(rs.next()) {
			tabelas.add(rs.getString("table_name"));
		}
		rs.close();
		return tabelas;
	}

	public static void exportarBanco(String massa) throws Exception {
		DatabaseConnection dbConn = new DatabaseConnection(ConnectionFactory.getConnection());
		IDataSet dataSet = dbConn.createDataSet(); //retorna um xml do banco
		
		//Esse trecho serve para que na exportação do banco o arquivo seja gerado de forma inteligente, já com a ordenação correta de quais regsitros/tabelas podem ser apagados/incluídos na ordem correta
		DatabaseSequenceFilter databaseSequenceFilter = new DatabaseSequenceFilter(dbConn);
		FilteredDataSet filteredDataSet = new FilteredDataSet(databaseSequenceFilter, dataSet);
		
		FileOutputStream fos = new FileOutputStream("massas" + File.separator + massa); //file sepator é a barra / correta para cada sistema operacional
//		FlatXmlDataSet.write(dataSet, fos);
		FlatXmlDataSet.write(filteredDataSet, fos); //exporta o xml no padrão flat do dbunit, é menos verboso
	}
}
