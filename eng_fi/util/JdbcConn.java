package com.hkapps.util;

import javax.naming.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
import com.informix.jdbc.*;

public class JdbcConn
{
	private Statement stmSql;
	private Connection conSql;
	private DataSource db;
	private String url_str;
	
	public JdbcConn(String inSource)
	{
	  try {
	  
	  if (inSource.substring(inSource.length() - 3).equals("_ds")) {
	   Context initCtx=new InitialContext();
	   if ( initCtx == null ) {
	     System.out.println("No Context <br>");
	   } else {
	     Context envContext = (Context) initCtx.lookup("java:/comp/env");	
	     //db = (DataSource) envContext.lookup("jdbc/webdm_ds");
	     db = (DataSource) envContext.lookup("jdbc/" + inSource);
		 
	     if ( initCtx == null ) {
	       System.out.println("No Informix JDBC JNDI Found <br>");
	     } else {
	       //System.out.println("Informix JDBC JNDI Found <br>");
             }
	   }
	  } else {
		  Properties props = new Properties();
		  FileInputStream in = new FileInputStream("/hkweb/apache-tomcat-7.0.41_prd/conf/db.properties");
		  props.load(in);
		  in.close();
		  Class.forName(props.getProperty("jdbc.driver")); 
		  if (inSource.equals("ivr")) {
			 url_str = props.getProperty("jdbc.url") + "/" + inSource + ":" + props.getProperty("jdbc.url.srv") + ";user=" + props.getProperty("jdbc.username.ivr") + ";password=" + props.getProperty("jdbc.password.ivr"); 
		  } else {
	         url_str = props.getProperty("jdbc.url") + "/" + inSource + ":" + props.getProperty("jdbc.url.srv") + ";user=" + props.getProperty("jdbc.username") + ";password=" + props.getProperty("jdbc.password");
		  }

	  }

	 } catch (Exception e) {
	    System.out.println("J_Connect -- SQLException : " + e.getMessage() );
         }
	
	}
		
	public void web_makeConn()
	{
		// Start connection
		try {
		     if (url_str == null) {			
			conSql = db.getConnection();
		     } else {
		     	conSql = DriverManager.getConnection(url_str);
		     }
		     stmSql = conSql.createStatement();
		} 
		catch (SQLException e) 
		{
			System.out.println("J_Connect -- SQLException : " + e.getMessage() );
		}
	}

	public void web_releaseConn()
	{
	 // Close connection
		try {
			stmSql.close();
			conSql.close();
		}
		catch (Exception e) 
		{
			System.out.println("J_Close -- Exception : " + e.getMessage() );
		}
	}

	public synchronized ResultSet exeQuery(String sql) throws SQLException 
	{
		try {
			return stmSql.executeQuery(sql);
		}
		catch(SQLException e) {
			System.out.println("exeQuery -- SQLException : " + e.getMessage() );	
			System.out.println(sql);
			throw e;
		}
	}

	public synchronized void rsClose() throws SQLException 
	{
		try {
			stmSql.close();
		}
		catch(SQLException e) {
			System.out.println("rsClose -- SQLException : " + e.getMessage() );	
			throw e;
		}
	}	
	
	public synchronized void exeUpdate(String sql) throws SQLException
	{
		try {
			stmSql.executeUpdate(sql);
		}
		catch(SQLException e) {
			System.out.println("exeUpdate -- SQLException : " + e.getMessage() );			
			System.out.println(sql);			
			throw e;
		}
	}
	
	
	public synchronized void exeUpdateTrans(String[] sqlArray) throws SQLException 
	{
		int size = Array.getLength(sqlArray);
		
		if (size>0)
		{
			try {
				//Class.forName(dbDriver);
				//conSql = DriverManager.getConnection(url, usr, psw);
				conSql = db.getConnection();
				conSql.setAutoCommit(false);
				stmSql = conSql.createStatement();
				
				for (int i=0; i<size; i++)
				{
					if(sqlArray[i]!=null && sqlArray[i]!="")
					{
						stmSql.executeUpdate(sqlArray[i]);
						System.out.println(i);
						System.out.println(sqlArray[i]);
					}
				}

				conSql.commit();
			}
			catch(SQLException e) {
				conSql.rollback();
				System.out.println("exeUpdateTrans -- SQLException : " + e.getMessage() );	
				throw e;
			}						
			finally {
				try {
					if (conSql !=null)
						conSql.close();
				}
				catch (SQLException ignored) {}
			}
		}
	}
	
	public synchronized void exeUpdateTrans(String sql) throws SQLException
	{
		try {
			conSql = db.getConnection();
			conSql.setAutoCommit(false);
			stmSql = conSql.createStatement();
			stmSql.executeUpdate(sql);
			conSql.commit();
		}
		catch(SQLException e) {
			conSql.rollback();
			System.out.println("exeUpdate -- SQLException : " + e.getMessage() );			
			System.out.println(sql);			
			throw e;
		}
		finally {
		   try {
		     if (conSql !=null)
			conSql.close();
	             }
		   catch (SQLException ignored) {}
	       }
	}
	

	
	
}
