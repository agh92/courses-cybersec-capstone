package org.coursera.cybersecurity.capstone.group8.internal.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

public class DbTools {
	   
	@Value("${spring.datasource.url}")
	private String url;

	/*
	 * returns the contents of the database as a String
	 */
	public String backup() throws SQLException {
		StringBuilder sb = new StringBuilder();
		try (Connection conn = DriverManager.getConnection(url)) {
			List<String> tableNames = getTableNames(conn);
			for (String tableName : tableNames) {
				listAll(conn, tableName, sb);
				sb.append("\n\n");
			}
		}
		return sb.toString();
	}

	/*
	 * Appends the data of the given table to the given StringBuilder
	 */
	private void listAll(Connection conn, String tablename, StringBuilder sb) throws SQLException {
		sb.append("Table ").append(tablename).append("\n");
		PreparedStatement pStmt = conn.prepareStatement("select * from " + tablename);
		ResultSet rs = pStmt.executeQuery();
		ResultSetMetaData rsmeta = rs.getMetaData();
		int columns = rsmeta.getColumnCount();
		for (int i = 1; i <= columns; i++) {
			if (i > 1)
				sb.append("\t");
			sb.append(rsmeta.getColumnName(i)).append(" ").append(rsmeta.getColumnTypeName(i));
		}
		sb.append("\n");
		
		int recordsCount = 0;
		while (rs.next()) {
			recordsCount++;
			for (int i = 1; i <= columns; i++) {
				if (i > 1)
					sb.append("\t");
				sb.append(rs.getString(i));
			}
			sb.append("\n");
		}
		sb.append("Total records: ").append(recordsCount).append("\n");
	}

	/*
	 * returns the names of all tables in the database
	 */
	private List<String> getTableNames(Connection conn) throws SQLException {
		List<String> tableNames = new LinkedList<>();
		DatabaseMetaData metadata = conn.getMetaData();
		ResultSet rs = metadata.getTables(conn.getCatalog(), null, "%", null);
		while (rs.next()) {
			String schema = rs.getString(2);
			String tableName = rs.getString(3);
			if (!tableName.toUpperCase().startsWith("SYS")) {
				tableNames.add(schema + "." + tableName);
			}
		}
		return tableNames;
	}
}
