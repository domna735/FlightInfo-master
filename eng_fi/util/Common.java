package com.hkapps.util;

import com.hkapps.util.DataTypeUtil;
import javax.naming.*;
import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

public class Common
{
   
   public static String get_month(String m) {
    
    Hashtable month = new Hashtable();
    month.put("Jan", "01");
    month.put("Feb", "02");
    month.put("Mar", "03");
    month.put("Apr", "04");
    month.put("May", "05");
    month.put("Jun", "06");
    month.put("Jul", "07");
    month.put("Aug", "08");
    month.put("Sep", "09");
    month.put("Oct", "10");
    month.put("Nov", "11");
    month.put("Dec", "12");
    
    return (String)month.get(m);
   }
   
   public static String get_month_mmm(String m) {
    
    Hashtable month = new Hashtable();
    month.put("01","Jan");
    month.put("02","Feb");
    month.put("03","Mar");
    month.put("04","Apr");
    month.put("05","May");
    month.put("06","Jun");
    month.put("07","Jul");
    month.put("08","Aug");
    month.put("09","Sep");
    month.put("10","Oct");
    month.put("11","Nov");
    month.put("12","Dec");
    
    return (String)month.get(m);
   }
   
   public String get_date(int whichDate) {
   	String wkday = "";
	  switch(whichDate) {
	  case 0:
	    wkday = "Sun";
	    break;
	  case 1:
	    wkday = "Mon";
	    break;
	  case 2:
	    wkday = "Tue";
	    break;
	  case 3:
	    wkday = "Wed";
	    break;
	  case 4:
	    wkday = "Thr";
	    break;
	  case 5:
	    wkday = "Fri";
	    break;
	  case 6:
	    wkday = "Sat";
	    break;
	  //default:
	  //  return "Error";
	}
	return wkday;
   }


   public boolean isProd(int port) {
	int prodport = 4003;
        int prodport2 = 80;
	/*
	var dunia = "80";
	var calvin = "80";
	var panorama = "4003";
	if ((server.port == panorama) || (server.port == calvin)) {
		return true;
	} else {
		return false;
	}
	*/
	
        if ((port == prodport) || (port == prodport2)) {
		return true;
	} else {
		return false;	
	} 
	
   }
   
   public boolean isValidHost(String inHost) {
	String prod_host = "apps.dhl.com.hk";
	String prod_host2 = "hkapps.dhl.com.hk";
    String test_host = "radsap1.apis.dhl.com";

    if ((inHost.equals(prod_host)) || (inHost.equals(test_host)) || (inHost.equals(prod_host2))) {
		return true;
	} else {
		return false;
	}
	
   }
   
   public boolean isValidProtocol(String inProtocol, int port) {
	String prod_protocol = "https";
    String test_protocol = "http";

	if (isProd(port)) {
      if (inProtocol.equals(prod_protocol)) {
		return true;
	  }
	} else {
	  if (inProtocol.equals(test_protocol)) {
		return true;
	  }
	}
	return false;
	
   }
   
   public String convert_path(int port, String inURL, String inPage, String toPage) {
	 String outPath = "";

	 if (isProd(port)) {
		outPath = inURL;
        outPath = outPath.replace("http://","https://");
	    outPath = outPath.replace(inPage, "/" + toPage);
	 } else {
		outPath = toPage;
	 }
	 return outPath;

   }
   

}
