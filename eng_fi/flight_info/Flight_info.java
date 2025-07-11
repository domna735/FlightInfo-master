package hkapps.flight_info_t;

import javax.naming.*;
import java.sql.*;
import com.hkapps.util.JdbcConn;
import com.hkapps.util.DataTypeUtil;
import java.util.*;

public class Flight_info
{

   private JdbcConn myJdbc;
   private JdbcConn myJdbc_wcrd;
   //private JdbcConn myJdbc_wsis;
   private JdbcConn myJdbc_ivrdb;
   private JdbcConn myJdbc_ivrdb2;
   private String sqlCmd;
   private static ResultSet myRs;
   private static ResultSet myRs2;
   
   public Flight_info(String ds_ty)
   {

  	   try 
	   {
	     if (ds_ty.equals("ds")) {
		myJdbc = new JdbcConn("webdb_ds");
	     } else {
	     	myJdbc = new JdbcConn("webdb");
	     }
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }
           
           try 
	   {
	     if (ds_ty.equals("ds")) {
		myJdbc_wcrd = new JdbcConn("wcrd_ds");
	     } else {
	     	myJdbc_wcrd = new JdbcConn("wcrd");
	     }
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }
           
           /*
           try 
	   {
	     if (ds_ty.equals("ds")) {
		myJdbc_wsis = new JdbcConn("wsis_ds");
	     } else {
	        myJdbc_wsis = new JdbcConn("wsis");
	     }
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }
           */
           
           try 
	   {
	     if (ds_ty.equals("ds")) {
		myJdbc_ivrdb = new JdbcConn("ivrdb_ds");
	     } else {
	        //myJdbc_ivrdb = new JdbcConn("ivrdb");
	        myJdbc_ivrdb = new JdbcConn("ivr");
	     }
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }
           
           try 
	   {
	     if (ds_ty.equals("ds")) {
		myJdbc_ivrdb2 = new JdbcConn("ivrdb_ds");
	     } else {
	        //myJdbc_ivrdb2 = new JdbcConn("ivrdb");
	        myJdbc_ivrdb2 = new JdbcConn("ivr");
	     }
	   }
	   catch (Exception e)
	   {
		e.printStackTrace();
           }
           
   }

         
   public String get_orig_dest(String awb) {
   	 String origin = "";
   	 String destination = "";
   	 String io_date = "";
   	 //myJdbc_wsis.web_makeConn();
   	 myJdbc_ivrdb.web_makeConn();
	  
	 try {
	 	
	 	/*
		sqlCmd="SELECT sd_orig, sd_dest, sd_dtmadd FROM ship_detail " +
   			"WHERE " +
   			"sd_awb = " + awb + 
   			" AND sd_dtpu >= today-90" +
   			" order by sd_dtmadd";
		*/
		
		sqlCmd="{call getSD(" + awb + ")}";
		
		myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
		while (myRs.next())
	        {
	           //origin = myRs.getString(1);
	           //destination = myRs.getString(2);
	           origin = myRs.getString(4);
	           destination = myRs.getString(5);
                }
		myRs.close();
         } catch (Exception e) {
                e.printStackTrace();
         } 
	   
	 myJdbc_ivrdb.web_releaseConn();
	   
   	 	
       	 if (origin.equals("HKG")) {
         	io_date = "Departure";   
   	 } else if (destination.equals("HKG")) {
   		io_date = "Arrival";   
   	 }
     	
         return io_date;
   }
   
   
   public String get_country (String station_code) {
	   	
	   	String full_name = "";
	   	
	   	myJdbc_wcrd.web_makeConn();
		  
		try {
			sqlCmd="select trim(c.country_name)  " +
	   			"from  v_country c, v_station s " +
	   			"where " +
	   			"s.station_code = \"" + station_code +  "\" " + 
	   			"and s.country_code = c.country_code";

			myRs = myJdbc_wcrd.exeQuery(sqlCmd);
			while (myRs.next())
		        {
		           full_name =  myRs.getString(1);
	                }
			myRs.close();
	         } catch (Exception e) {
	                e.printStackTrace();
	         } 
		   
		 myJdbc_wcrd.web_releaseConn();
	   	
	   	 return full_name;
	   }
   
   
   public String get_station_name(String station_code) {
	   	
	   	String full_name = "";
	   	
	   	myJdbc_wcrd.web_makeConn();
		  
		try {
			/*
			sqlCmd="select station_code, trim(station_name), trim(country_name) " +
	   			"from v_station, v_country " +
	   			"where " +
	   			"station_code = \"" + station_code +  "\" " +
	   			"and " +
	   			"v_station.country_code = v_country.country_code";
			*/
			
				sqlCmd="select trim(station_name) " +
				     "from v_station " +
				     "where " +
				     "station_code = \"" + station_code + "\"";
			
			myRs = myJdbc_wcrd.exeQuery(sqlCmd);
			while (myRs.next())
		        {
		           full_name =  myRs.getString(1);
	                }
			myRs.close();
	         } catch (Exception e) {
	                e.printStackTrace();
	         } 
		   
		 myJdbc_wcrd.web_releaseConn();
	   	
	   	 return full_name;
	   }
   
   public String get_station(String station_code) {
   	
   	String full_name = "";
   	
   	myJdbc_wcrd.web_makeConn();
	  
	try {
		/*
		sqlCmd="select station_code, trim(station_name), trim(country_name) " +
   			"from v_station, v_country " +
   			"where " +
   			"station_code = \"" + station_code +  "\" " +
   			"and " +
   			"v_station.country_code = v_country.country_code";
		*/
			sqlCmd="select station_code, " +
			     "trim(decode(iata_name,null,station_name,iata_name)), " +
			     "trim(v_country.country_name) " + 
			     "from v_station, outer iata, v_country " +
			     "where " +
			     "station_code = \"" + station_code + "\" " +
			     "and station_code = iata_code " +
			     "and v_station.country_code = iata.country_code " +
			     "and v_station.country_code = v_country.country_code";
				     
		myRs = myJdbc_wcrd.exeQuery(sqlCmd);
		while (myRs.next())
	        {
	           full_name = myRs.getString(2) + ", " + myRs.getString(3);
                }
		myRs.close();
         } catch (Exception e) {
                e.printStackTrace();
         } 
	   
	 myJdbc_wcrd.web_releaseConn();
   	
   	 return full_name;
   }
   
   public int get_ttlpcs(String awb_str) {
   	int sd_pcs = 0;
   	
   	//myJdbc_wsis.web_makeConn();
   	 myJdbc_ivrdb.web_makeConn();
	  
	try {
		/*
		sqlCmd="SELECT sd_pcs, sd_dtmadd FROM ship_detail " +
			"WHERE " +
			"sd_awb = " + awb_str + " AND " +
			"sd_pcs > 0 " +
			"AND sd_dtpu >= today-90 " +
			"order by sd_dtmadd desc";
		*/
		
		sqlCmd="{call getSDPCS(" + awb_str + ")}";
		
		myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
		while (myRs.next())
	        {
	           sd_pcs = Integer.parseInt(myRs.getString(1));
                }
		myRs.close();
         } catch (Exception e) {
                e.printStackTrace();
         } 
	   
	 myJdbc_ivrdb.web_releaseConn();
    
	 return sd_pcs;

   }
 
   public int get_ttlrec(String awb_str) {
	int count_int = 0;
	
	//DataTypeUtil dtu = new DataTypeUtil();
      	//String tmp_tbl_postfix = awb_str + "" + dtu.hk_datetime().getHours() + "" + dtu.hk_datetime().getMinutes() + "" + dtu.hk_datetime().getSeconds();
      		  
	//myJdbc_wsis.web_makeConn();
   	myJdbc_ivrdb.web_makeConn();  
   	 
	try {
		/*
		sqlCmd="SELECT unique gen_ckpt, gen_id, gen_date, gen_time FROM checkpoint " +
   			"WHERE " +
   			"gen_awb = " + awb_str + " AND " +
   			//"gen_date >= today - 90 AND " +
   			"gen_id = 42 AND " +
   			"gen_ckpt[7,8]=\"DF\" AND " + 
   			"(((gen_ckpt[14,16] =\"HHP\" ) or (gen_ckpt[14,16] =\"HKG\")) " +
   			"AND gen_ckpt[43,45] not in (\"HHP\",\"HKG\")) " +
   			"into temp ckpt_rec_" + tmp_tbl_postfix;   			
   		*/
   		
   		sqlCmd="{call getDetailTest(" + awb_str + ")}";
		
		myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
   		
		/*
		myJdbc_wsis.exeUpdate(sqlCmd);
		sqlCmd="SELECT count(*) FROM ckpt_rec_" + tmp_tbl_postfix;
				
		myRs = myJdbc_wsis.exeQuery(sqlCmd);
		*/
		
		while (myRs.next())
	        {
	           //count_int = Integer.parseInt(myRs.getString(1));
	           count_int++;
                }
		myRs.close();
		
		//sqlCmd="drop table ckpt_rec_" + tmp_tbl_postfix;
		
		//myJdbc_wsis.exeUpdate(sqlCmd);
		
         } catch (Exception e) {
                e.printStackTrace();
         }
         
         myJdbc_ivrdb.web_releaseConn();
         
         return count_int;
     
   }
 
 
   public int get_inbound_ttlrec(String awb_str) {
         
        int count_int = 0;
	
	//DataTypeUtil dtu = new DataTypeUtil();
      	//String tmp_tbl_postfix = awb_str + "" + dtu.hk_datetime().getHours() + "" + dtu.hk_datetime().getMinutes() + "" + dtu.hk_datetime().getSeconds();
      	      	
	//myJdbc_wsis.web_makeConn();
	myJdbc_ivrdb.web_makeConn();
	  
	try {
		/*
		sqlCmd="SELECT unique gen_ckpt, gen_id, gen_ckpt[26,35] as c2, gen_time, gen_date FROM checkpoint " +
   			"WHERE " +
   			"gen_awb = " + awb_str + " AND " +
   			//"gen_date >= today - 90 AND " +
   			"gen_id = 38 AND " +
   			"gen_ckpt[1,2]=\"73\" " + 
   			"into temp ckpt_rec_" + tmp_tbl_postfix;   			
   		*/
   		sqlCmd="{call getDetailTest(" + awb_str + ")}";	
   		
   		myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
   		
   		/*
		myJdbc_wsis.exeUpdate(sqlCmd);
		sqlCmd="SELECT count(*) FROM ckpt_rec_" + tmp_tbl_postfix;
				
		myRs = myJdbc_wsis.exeQuery(sqlCmd);
		*/
		
		while (myRs.next())
	        {
	           //count_int = Integer.parseInt(myRs.getString(1));
	           count_int++;
                }
		myRs.close();
		
		//sqlCmd="drop table ckpt_rec_" + tmp_tbl_postfix;
		//myJdbc_wsis.exeUpdate(sqlCmd);
		
         } catch (Exception e) {
                e.printStackTrace();
         }
         
         myJdbc_ivrdb.web_releaseConn();
         
         return count_int;
     
  }
  
  
  
  
   public Vector get_outboundData(String awb_str) {
      int ttlpcs = 0;
      int ttlrec = 0;
      
      String flag="N";
      String fn = "";
      String fd = "";
      String ma = "";
      int ps = 0;
      String pd = "";
      
      int rec = 0;
      int sumofpcs=0;
      String first = "Y";
      String lst1done = "";
      
      String fd_dt = "";
      String fd_mth = "";
      int fd_yr = 0;
      String gen_mth = "";
      
      String cu_fn = "";
      String cu_fd = "";
      String cu_ma = "";
      int cu_ps = 0;
      String cu_pd = "";
      
      Vector myList= new Vector();
      
      DataTypeUtil dtu = new DataTypeUtil();
        
      ttlpcs = get_ttlpcs(awb_str);
      ttlrec = get_ttlrec(awb_str);
      
      //myJdbc_wsis.web_makeConn();
      myJdbc_ivrdb.web_makeConn(); 
      myJdbc_ivrdb2.web_makeConn(); 
	  
      try {
      	String tmp_tbl_postfix = awb_str + "" + dtu.hk_datetime().getHours() + "" + dtu.hk_datetime().getMinutes() + "" + dtu.hk_datetime().getSeconds();
      	
      	sqlCmd="create temp table ckpt_rec_" + tmp_tbl_postfix + " (gen_ckpt varchar(200), gen_id smallint, gen_date char(10), gen_time datetime hour to minute)";
      	myJdbc_ivrdb.exeUpdate(sqlCmd);
      	
      	/*
	sqlCmd="SELECT unique gen_ckpt, gen_id, gen_date, gen_time FROM checkpoint " +
   		"WHERE " +
   		"gen_awb = " + awb_str + " AND " +
   		//"gen_date >= today - 90 AND " +
   		"gen_id = 42 AND " +
   		"gen_ckpt[7,8]=\"DF\" AND " + 
   		"(((gen_ckpt[14,16] =\"HHP\" ) or (gen_ckpt[14,16] =\"HKG\")) " +
   		"AND gen_ckpt[43,45] not in (\"HHP\",\"HKG\")) " +
   		"order by gen_date desc, gen_time desc";
   	*/
   	
   	sqlCmd="{call getDetailTest(" + awb_str + ")}";
   	
   	myRs = myJdbc_ivrdb2.exeQuery(sqlCmd);
	
	while (myRs.next())
	{
		sqlCmd="insert into ckpt_rec_" + tmp_tbl_postfix + " values ('" + dtu.trimR(myRs.getString(5)) + "','" + myRs.getString(2) + "','" + myRs.getString(3) + "','" + dtu.trimAll(myRs.getString(4)) + "')";
		myJdbc_ivrdb.exeUpdate(sqlCmd);
		
	}
	myRs.close();
		
	sqlCmd="SELECT unique gen_ckpt, gen_id, gen_date, gen_time from ckpt_rec_" + tmp_tbl_postfix + 
		" order by gen_date desc, gen_time desc";	
	
	myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
	
	String[] chk_ps;
	while (myRs.next())
        {
           if(sumofpcs < ttlpcs*1) {
           	flag="Y";
           	
              if (myRs.getInt(2) == 42) {
           	
	        cu_fn=myRs.getString(1).substring(17,24);
	  
		  //updated by Charmaine at Dec 2009
		  //use date in ckpt comment instead of DF checkpoint gen_date for flight date
		  //month = get_month(wsis_cur.gen_date.toString().substring(4,7));
		  //var cu_fd=wsis_cur.gen_date.toString().substring(8,10) + "-"+ month + "-" + wsis_cur.gen_date.toString().substring(35,39);
	  
	  	fd_dt=myRs.getString(1).substring(25,27);
	        fd_mth=myRs.getString(1).substring(27,29);
	  	//fd_yr=Integer.parseInt(myRs.getString(3).toString().substring(35,39));
	  	fd_yr=Integer.parseInt(myRs.getString(3).toString().substring(0,4));
	        //gen_mth=get_month(myRs.getString(3).toString().substring(4,7));
	        gen_mth=myRs.getString(3).toString().substring(5,7);
	  
	        if ((gen_mth.equals("12")) && (fd_mth.equals("01"))) {
		     fd_yr = (fd_yr * 1) + 1;
		}
	  
   	        cu_fd = fd_dt + "-" + fd_mth + "-" + fd_yr;
	  
	        cu_ma=myRs.getString(1).substring(30,41);
	  	//cu_ps=Integer.parseInt(dtu.trimAll(myRs.getString(1).substring(9,12)));
	  	chk_ps=myRs.getString(1).substring(9,12).split("\\.");
	  	cu_ps=Integer.parseInt(dtu.trimAll(chk_ps[0]));
	  	cu_pd=myRs.getString(1).substring(42,45); // Port of discharge
	  	
	      } else {
	      	//gen_id = 97
	      	cu_fn=myRs.getString(1).substring(14,24);
	  	//cu_fd=myRs.getString(3).substring(0,2) + "-" + myRs.getString(3).substring(3,5) + "-20" + myRs.getString(3).substring(6,8);
	  	cu_fd=myRs.getString(1).substring(25,27) + "-" + myRs.getString(1).substring(28,30) + "-20" + myRs.getString(1).substring(31,33);	  	
	  	cu_ma=myRs.getString(1).substring(2,13);
	  	//cu_ps=Integer.parseInt(dtu.trimAll(myRs.getString(1).substring(36,39)));
	  	chk_ps=myRs.getString(1).substring(36,39).split("\\.");
	  	cu_ps=Integer.parseInt(dtu.trimAll(chk_ps[0]));
	  	cu_pd=myRs.getString(1).substring(40,43); // Port of discharge
	      
	      }  
		
		if (first.equals("Y")) {
		      fn = cu_fn;
	              fd = cu_fd;
	              ma = cu_ma;				
		      ps = ps + cu_ps * 1;	//pcs
		      sumofpcs = sumofpcs + cu_ps * 1;
		      pd = cu_pd; 			// Port of discharge
		      lst1done = "N";
		      first = "N";
		      rec = rec + 1;
		      
		} else if (!((cu_fn.equals(fn)) && (cu_fd.equals(fd)) && (cu_ma.equals(ma)))) {
		    //Add by Charmaine to avoid sumofpcs > ttlpcs
		    sumofpcs = sumofpcs + cu_ps * 1;
		    if (sumofpcs <= ttlpcs*1) {
		      FlightInfo flightinfo = new FlightInfo();
		      flightinfo.flight_no=fn; 		//Flight number
		      flightinfo.flight_date=fd;		//Flight Date
		      flightinfo.mawb=ma;			//MAWB
		      flightinfo.pcs=ps;			//piece
		      flightinfo.port=pd; 			// Port of discharge
		      
		      myList.addElement(flightinfo);
		      
		      fn = cu_fn;
	              fd = cu_fd;
	              ma = cu_ma;
	              pd = cu_pd; 
		      //sumofpcs = sumofpcs + cu_ps * 1;
	              rec = rec + 1;	  			      
	              if (rec!=ttlrec) { 
		      	//ps = 0;
		      	ps = cu_ps*1;
		      } 
		      else {
		      	rec = -1;
		      }
		    }   		
		} else {
		    //Add by Charmaine to avoid sumofpcs > ttlpcs
		    sumofpcs = sumofpcs + cu_ps * 1;
		    if (sumofpcs <= ttlpcs*1) {	
			fn = cu_fn;
	              	fd = cu_fd;
	              	ma = cu_ma;
		      	ps = ps + cu_ps * 1;
		      	//sumofpcs = sumofpcs + cu_ps * 1;
		      	pd = cu_pd;
		      	lst1done="N";
		      	rec=rec+1;
		    }
	        }
	           
	   }
        }
		
	myRs.close();
	
	sqlCmd="drop table ckpt_rec_" + tmp_tbl_postfix;
		
	myJdbc_ivrdb.exeUpdate(sqlCmd);
		
	
      } catch (Exception e) {
                e.printStackTrace();
      }
      
      myJdbc_ivrdb.web_releaseConn();
      myJdbc_ivrdb2.web_releaseConn();
      
      if ((flag.equals("Y")) && (lst1done.equals("N"))) {
      	  FlightInfo flightinfo = new FlightInfo();
      	  flightinfo.flight_no=fn; 		//Flight number
          flightinfo.flight_date=fd;		//Flight Date
	  flightinfo.mawb=ma;			//MAWB
	  flightinfo.pcs=ps;			//piece
	  flightinfo.port=pd; 			// Port of discharge
		      
	  if (rec == -1) {
	    flightinfo.pcs=cu_ps * 1;
	  }
	  myList.addElement(flightinfo);
      }
      
      return myList;
      
   }
   
   
   public Vector get_inboundData(String awb_str) {
   
      int ttlpcs = 0;
      int ttlrec = 0;
      
      String flag="N";
      String fn = "";
      String fd = "";
      String ma = "";
      int ps = 0;
      String pl = "";
      
      int rec = 0;
      int sumofpcs=0;
      String first = "Y";
      String lst1done = "";
      
      String fd_dt = "";
      String fd_mth = "";
      int fd_yr = 0;
      String gen_mth = "";
      
      String cu_fn = "";
      String cu_fd = "";
      String cu_ma = "";
      int cu_ps = 0;
      String cu_pl = "";
      
      Vector myList= new Vector();
      
      DataTypeUtil dtu = new DataTypeUtil();
      
            
      ttlpcs = get_ttlpcs(awb_str);
      ttlrec = get_inbound_ttlrec(awb_str);
      
      //System.out.println("ttlpcs:" + ttlpcs);
      //System.out.println("ttlrec:" + ttlrec);
   
      //myJdbc_wsis.web_makeConn();
      myJdbc_ivrdb.web_makeConn(); 
      myJdbc_ivrdb2.web_makeConn(); 
      
      try {
      	String tmp_tbl_postfix = awb_str + "" + dtu.hk_datetime().getHours() + "" + dtu.hk_datetime().getMinutes() + "" + dtu.hk_datetime().getSeconds();
      	
      	sqlCmd="create temp table ckpt_rec_" + tmp_tbl_postfix + " (gen_ckpt varchar(200), gen_id smallint, gen_date char(10), gen_time datetime hour to minute)";
      	myJdbc_ivrdb.exeUpdate(sqlCmd);
      	
      	/*
	sqlCmd="SELECT unique gen_ckpt, gen_id, gen_ckpt[26,35], gen_time, gen_date FROM checkpoint " +
			"WHERE " +
			"gen_awb = " + awb_str + " AND " +
			//"gen_date >= today - 90 AND " +
			"gen_id = 38 AND " +
			"gen_ckpt[1,2]=\"73\" " + 
			"order by gen_date desc, gen_time desc";
	*/
	
	sqlCmd="{call getDetailTest(" + awb_str + ")}";
   	
   	myRs = myJdbc_ivrdb2.exeQuery(sqlCmd);
   	
   	while (myRs.next())
	{
		sqlCmd="insert into ckpt_rec_" + tmp_tbl_postfix + " values ('" + dtu.trimR(myRs.getString(5)) + "','" + myRs.getString(2) + "','" + myRs.getString(3) + "','" + dtu.trimAll(myRs.getString(4)) + "')";
		myJdbc_ivrdb.exeUpdate(sqlCmd);
		
	}
	myRs.close();
		
	sqlCmd="SELECT unique gen_ckpt, gen_id, gen_date, gen_time from ckpt_rec_" + tmp_tbl_postfix + 
		" order by gen_date desc, gen_time desc";
	
	myRs = myJdbc_ivrdb.exeQuery(sqlCmd);
   	
	String[] chk_ps;
	while (myRs.next())
        {   		  
 
          if(sumofpcs < ttlpcs*1) {
     	  	flag="Y";
          	cu_fn=myRs.getString(1).substring(14,24);
	  	//cu_fd=myRs.getString(3).substring(0,2) + "-" + myRs.getString(3).substring(3,5) + "-20" + myRs.getString(3).substring(6,8);
	  	cu_fd=myRs.getString(1).substring(25,27) + "-" + myRs.getString(1).substring(28,30) + "-20" + myRs.getString(1).substring(31,33);	  	
	  	cu_ma=myRs.getString(1).substring(2,13);
	  	//cu_ps=Integer.parseInt(dtu.trimAll(myRs.getString(1).substring(36,39)));
	  	chk_ps=myRs.getString(1).substring(36,39).split("\\.");
	  	cu_ps=Integer.parseInt(dtu.trimAll(chk_ps[0]));
	  	cu_pl=myRs.getString(1).substring(40,43); // Port of loading
	
		  if (first.equals("Y")) {
		      
		      fn = cu_fn;
	              fd = cu_fd;
	              ma = cu_ma;
		      ps = ps + cu_ps * 1;	//pcs
		      sumofpcs = sumofpcs + cu_ps * 1;
		      pl = cu_pl; // Port of loading
		      lst1done = "N";
		      first = "N";
		      rec = rec + 1;
		      
		  } else if (!((cu_fn.equals(fn)) && (cu_fd.equals(fd)) && (cu_ma.equals(ma)))) {
		    //Add by Charmaine to avoid sumofpcs > ttlpcs
		    sumofpcs = sumofpcs + cu_ps * 1;
		    if (sumofpcs <= ttlpcs*1) {
		      FlightInfo flightinfo = new FlightInfo();
		      flightinfo.flight_no=fn; 		//Flight number
		      flightinfo.flight_date=fd;		//Flight Date
		      flightinfo.mawb=ma;			//MAWB
		      flightinfo.pcs=ps;
		      //sumofpcs = sumofpcs + cu_ps * 1;
		      flightinfo.port=pl;			// Port of loading
		      myList.addElement(flightinfo);
		      
		      fn = cu_fn;
	              fd = cu_fd;
	              ma = cu_ma;
	              pl = cu_pl;
	              rec = rec + 1;	  			      
	              
	              if (rec!=ttlrec) { 
		      	ps = cu_ps * 1;
		      } 
		      else {
		      	rec = -1;
		      }
		    }  		
	      
		  } else {
		    //Add by Charmaine to avoid sumofpcs > ttlpcs
		    sumofpcs = sumofpcs + cu_ps * 1;
		    if (sumofpcs <= ttlpcs*1) {
			fn = cu_fn;
	              	fd = cu_fd;
	              	ma = cu_ma;				
		      	ps = ps + cu_ps * 1;	//pcs
		      	//sumofpcs = sumofpcs + cu_ps * 1;
		      	pl = cu_pl; 			// Port of loading
		      	lst1done="N";
		      	rec=rec+1;		      	
		    }
	          }
	      } //end of sumofpcs and ttlpcs comparison 
	    } //end while
	    myRs.close();
	    
	    sqlCmd="drop table ckpt_rec_" + tmp_tbl_postfix;
		
	    myJdbc_ivrdb.exeUpdate(sqlCmd);
	
      } catch (Exception e) {
                e.printStackTrace();
      }	
      myJdbc_ivrdb.web_releaseConn();
      myJdbc_ivrdb2.web_releaseConn();
      
      if ((flag.equals("Y")) && (lst1done.equals("N"))) {      
      	  FlightInfo flightinfo = new FlightInfo();
	  flightinfo.flight_no=fn; 			//Flight number
	  flightinfo.flight_date=fd;		//Flight Date
	  flightinfo.mawb=ma;			//MAWB
	  flightinfo.pcs=ps;
	  flightinfo.port=pl;			// Port of loading
	  
	  if (rec == -1) {
	    flightinfo.pcs=cu_ps;
	  }
	  myList.addElement(flightinfo);	  
	  
      }
 
      return myList;
   }
   
      	
        	
}
