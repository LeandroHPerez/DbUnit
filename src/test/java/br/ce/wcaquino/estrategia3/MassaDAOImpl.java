package br.ce.wcaquino.estrategia3;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.ce.wcaquino.dao.utils.ConnectionFactory;

public class MassaDAOImpl {

	public void inserirMassa(String tipo, String valor) throws ClassNotFoundException, SQLException {
		PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(
				"INSERT INTO massas (tipo, valor) VALUES (?, ?)");
		stmt.setString(1, tipo);
		stmt.setString(2, valor);
		stmt.executeUpdate();
		stmt.close();
	}
	
	public String obterMassa(String tipo) throws ClassNotFoundException, SQLException {
		PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(
				"WITH M AS (SELECT id, valor FROM massas WHERE tipo = ? AND usada = false " + 
				"	ORDER BY id LIMIT 1) UPDATE MASSAS M2 SET USADA = TRUE FROM M WHERE M.ID = M2.ID RETURNING M.VALOR");
		stmt.setString(1, tipo);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) return null;
		return rs.getString(1); //vai retornar a primeira coluna (só virá uma, a col m.valor)
	}
	
	public Integer obterEstoque(String tipo) throws SQLException, ClassNotFoundException {
		PreparedStatement stmt = ConnectionFactory.getConnection().prepareStatement(
				"SELECT COUNT(*) FROM massas WHERE tipo = ? AND usada = FALSE");
		stmt.setString(1, tipo);
		ResultSet rs = stmt.executeQuery();
		if(!rs.next()) return null;
		return rs.getInt(1); //pega o valor d 1ª coluna retornada
	}
}
