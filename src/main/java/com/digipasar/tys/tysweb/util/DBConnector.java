package com.digipasar.tys.tysweb.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBConnector {

	private static final String DB_URL = "jdbc:mariadb://localhost:3306/tysdb?user=root&password=myPassword";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "loosecontrol";
	
	
	public static void writeToTable(String tableName, List<Map<String,String>> dataMap) {
		Connection connection = null;
		int batchNumber = 0;
		String errorRecord = "";
		try {
			connection= DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			
			PreparedStatement ps = null;
			
			//For getting column name
			for(Map<String,String> rowMap: dataMap) {
				ps = connection.prepareStatement(getInsertStmtString(rowMap, tableName));
				break;
			}
			
			
			
			outer: for(Map<String,String> rowMap: dataMap) {
				int paramIndex = 1;
				int runningNumber = 0;
				
				errorRecord = rowMap.toString();
				
				for(String colName: rowMap.keySet()) {
					if(colName.equalsIgnoreCase(rowMap.get(colName))) {
						continue outer;
					}
					//System.out.println("Index:"+paramIndex+"::Value:"+handleNull(rowMap.get(colName)));
					ps.setString(paramIndex++, handleNull(rowMap.get(colName)));
				}
				ps.addBatch();
				
				if(runningNumber % 100 == 0){
					batchNumber++;
					ps.executeBatch();
					//ps.clearBatch();
				}
			}

			//For last batch
			ps.executeBatch();
			
		} catch(BatchUpdateException e){
    		e.getNextException().printStackTrace();
    		int count[] = e.getUpdateCounts();
    		for(int i=0;i<count.length;i++){
    			if(count[i] == Statement.EXECUTE_FAILED){
    				System.out.println("Batch No.:"+(batchNumber-1)+" Record Number:"+(i+1));
    			}
    		}
    		
    	} catch (SQLException e) {
    		System.out.println("Error Record:"+errorRecord);
    		System.out.println();
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String handleNull(String str){
		return str == null?"":str;
	}
	
	private static String getInsertStmtString(Map<String,String> rowMap, String tableName){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(tableName);
		sql.append(" (");
		sql.append(getColumnNameString(rowMap.keySet()));
		sql.append(")");
		sql.append(" values (");
		
		String questionMarkStr = "";
		
		for(int i = 0; i < rowMap.size(); i++){
			if(i==0){
				questionMarkStr += "?";
			}else{
				questionMarkStr += ",?";
			}
		}
		
		sql.append(questionMarkStr);
		sql.append(");");
		
		return sql.toString();
	}
	
	private static String getColumnNameString(Set<String> colNameMap){
		String colStr = "";
		int i = 0;
		for(String columnName : colNameMap){
			if(i++==0){
				colStr += columnName;
			}else{
				colStr += ","+columnName;
			}
		}
		
		return colStr;
	}
	
}
