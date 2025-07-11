package com.hkapps.util;

import javax.naming.*;
import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;


public class Log
{
   private JdbcConn myJdbc;
   private String sqlCmd;
   
   	
   public Log()
   {
	   try 
	   {
		myJdbc = new JdbcConn("webdb_ds");
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }

   }

   public boolean add_log (String app_type, String log_contents)
   {
      try
      {
      	
        sqlCmd = "insert into app_logs values ('" + app_type + "',current,'" + log_contents + "')";
        myJdbc.exeUpdateTrans(sqlCmd);  
        myJdbc.web_releaseConn();
        return true;  
      }
      catch (Exception e)
      {
	e.printStackTrace();
	return false;
      }
      
  
   }
 
}