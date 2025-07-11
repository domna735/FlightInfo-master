<%@ page import="hkapps.flight_info.*"%>
<%@ page import="hkapps.event_extract.*"%>
<%@ page import="com.hkapps.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="java.lang.reflect.Array"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*" %>
<%@ page import="org.json.*"%>

<% response.setHeader("Referrer-Policy", "no-referrer-when-downgrade"); %>


<html>

<head>
<title>Flight Information</title>

<noscript>
<b>Your brower has JavaScript turned off.</b><br>
Please enable JavaScript in order to have this page fully functional!!!
After updating your preferences, click 'Reload' button to start all over again.
</noscript>

</head>

<script src="../js_client/rollovers.js" type="text/javascript"></script>
<body bgcolor=#ffffff><blockquote>

<font size="4" face="Frutiger, Arial" color="#9C0000">

<%
Common common = new Common();
String referpage = request.getHeader("referer");

boolean ispartner = false;
if (request.getParameter("partnerid") != null) {
	 if (request.getParameter("partnerid").equals("04c08F9fc02343F48ecAe7cD232476a8")) {
        ispartner = true;
	 }
}

if (!ispartner) {
	
	if (referpage == null) {
	   response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
	   if(true){return;}
	}

	if (request.getQueryString() != null) {
	   response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
	   if(true){return;}
	}

	URL referurl = new URL(referpage);
	if (!common.isValidHost(referurl.getHost())) {
	   response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
	   if(true){return;}
	}

	if (!referurl.getPath().equals("/eng_fi/eng_fi_Home.html")) {
	   response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
	   if(true){return;}
	}

}

//if ((request.getMethod().equals("GET")) || (request.getParameter("awb") == null) || (request.getParameter("awb").equals("")) || (request.getParameter("awb").equals("undefined"))) {
if ((request.getParameter("awb") == null) || (request.getParameter("awb").equals("")) || (request.getParameter("awb").equals("undefined"))) {
   response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
   if(true){return;}
}

  int nawb = 0;
  
  String[] awblist=request.getParameter("awb").split("\\|");
  nawb = Array.getLength(awblist);
  
  if (nawb == 0) {
    response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
    if(true){return;}
  }
  
  DataTypeUtil dtu = new DataTypeUtil();
  
  boolean chk_awb = true;
  String awblist_str = "";
  
  //out.println(nawb);
  for (int p=0; p<nawb; p++) {
    //out.println(awblist[p].length());
    //out.println(awblist[p]);
    if (awblist[p].length() != 10) {
       chk_awb = false;
       p=nawb;
    } else {
       if (!dtu.isNumeric(awblist[p])) {
     	  chk_awb = false;
          p=nawb;
       } else {
	     if (p == 0) {
			awblist_str = awblist[p];
		 } else {
			awblist_str = awblist_str + "," + awblist[p]; 
		 }
		 
	   }
    }    
  }
  
  if (!chk_awb) {
    response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "index.html"));
    if(true){return;}
  }
    
  Log log = new Log();
  
  if (!log.add_log("FI_E", request.getParameter("awb"))) {				
	response.sendRedirect(common.convert_path(request.getServerPort(), (request.getRequestURL()).toString(), request.getServletPath(), "err_htmls/eng_fi_DbErr.html"));
	if(true){return;} 
  }
  
  Properties prop=new Properties();
  FileInputStream ip=new FileInputStream(System.getProperty("catalina.base")+"/webapps/config.properties");
  prop.load(ip);
  
  JSONObject req_json = new JSONObject();
  ConnectAPI connectAPI = new ConnectAPI();
  connectAPI.set_request_json(req_json);
  connectAPI.set_api_url(prop.getProperty("flightinfo_api"));
  connectAPI.set_api_key(prop.getProperty("apikey"));  
  
  
  for (int i=0; i<nawb; i++) {
	  
  //req_json.put("awbn", awblist_str);
  req_json.put("awbn", awblist[i]);
  
  connectAPI.send_request();  
  JSONObject res_json = connectAPI.get_response_json();
  //out.println(res_json);

  JSONObject res_json_awb;
  JSONObject res_json_shp;
  JSONArray res_jsonarr_result;
  JSONObject res_json_result;
  
  String orig_srv = "";
  String dest_srv = "";
  //int sd_pcs = 0;
  String io_date = "";
  String action = "";
  
  //for (int i=0; i<nawb; i++) {
	
	res_json_awb = res_json.getJSONObject(awblist[i]);
	res_json_shp = res_json_awb.getJSONObject("shp");
	orig_srv = res_json_shp.getString("OrgSrvACd");
	dest_srv = res_json_shp.getString("DstSrvACd");
	//sd_pcs = res_json_shp.getInt("DclNPcs");

        res_jsonarr_result = res_json_awb.getJSONArray("result");
   /* 
    if (sd_pcs == 0) {
        io_date = "";
	action = "Discharge / Loading";
    } else {
        out.println(res_json_awb.getString("result"));

     //res_jsonarr_result = res_json_awb.getJSONArray("result");
*/				      
        if (res_jsonarr_result.length() == 0) {
          io_date = "";
	  action = "Discharge / Loading";
        } else {
           if ((orig_srv.equals("HKG")) && (!dest_srv.equals("HKG"))) {
	     io_date = "Departure";
	     action = "Discharge";
	   } else if ((!orig_srv.equals("HKG")) && (dest_srv.equals("HKG"))) {
	     io_date = "Arrival";
	     action = "Loading";
	   } else {
             io_date = "";
	     action = "Discharge / Loading";
           }

        }

//    }
	
    String title_awb_no = "Airwaybill<br>Number";
    String title_flg_no = "Flight Number /<br>Vehicle Number";
    String title_flg_date = "Flight<br>" + io_date +" Date";
    String title_mawb_no = "Master Airwaybill Number /<br>Customs Cargo Reference Number (CCRN)";
    String title_piece = "Pieces";
    String title_port = "Port of <br>" + action + " Country or Territory";
    
    out.println("<table border=\"1\" width=\"80%\">\n");
    out.println("<tr>\n");
    out.println("      <td VALIGN=TOP width='10%'>\n");   
    out.println("      <font size='2' face='Univers, Arial, Times New Roman'  color='#a60018'>\n");
    out.println("      <b>" + title_awb_no +"</b></font></td>\n");
      
    out.println("      <td VALIGN=TOP width='11%' nowrap height='32'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>" + title_flg_no + "</b></font></td>\n");
      
    out.println("     <td VALIGN=TOP width='25%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_flg_date + "</b></font></td>\n");

    out.println("      <td VALIGN=TOP width='27%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>" +title_mawb_no + "</b></font></td>\n");
    
    // Port of discharge / loading
    out.println("      <td VALIGN=TOP width='30%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_port + "</b></font></td>\n");

    out.println("      <td VALIGN=TOP width='8%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_piece + "</b></font></td>\n");
    out.println("   </tr>\n");
	
	//get flight info record
	//if (sd_pcs == 0) {
	//if (io_date.equals("")) {
	if (res_jsonarr_result.length() == 0) {
		out.println("<tr>\n");
		out.println("      <td VALIGN=TOP width='10%'>\n");
		out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
		out.println("      <b>" + awblist[i] + "</b></font></td>\n");
      
		out.println("      <td VALIGN=TOP align=center width='16%' nowrap height='32' colspan=5>\n");
		out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
		out.println("      <b>No information available</b></font></td>\n");
		out.println("   </tr>\n");
		
	} else {
	
		//res_jsonarr_result = res_json_awb.getJSONArray("result");
	
		for (byte b = 0; b < res_jsonarr_result.length(); b++) {
			res_json_result = res_jsonarr_result.getJSONObject(b);
			
        	out.println("<tr>\n");
            out.println("      <td VALIGN=TOP width='10%'>\n");
			out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
			out.println("      <b>" + awblist[i] + "</b></font></td>\n");
      
			out.println("      <td VALIGN=TOP width='11%' nowrap height='32'>\n");
			out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
			out.println("      <b>" + res_json_result.getString("fn") + "</b></font></td>\n");
      
      	    out.println("     <td VALIGN=TOP width='25%'>\n");
			out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
			out.println("      <b>" + res_json_result.getString("fDate") + "</b></font></td>\n");

      	    out.println("      <td VALIGN=TOP width='27%'>\n");
      	    out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
      	    out.println("      <b>" + res_json_result.getString("mawb") + "</b></font></td>\n");
      	
      	    out.println("      <td VALIGN=TOP width='30%'>\n");
      	    out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n"); 
      	    out.println("      <b>" + res_json_result.getString("countryStation") + "</b></font></td>\n");
      
      	    out.println("      <td VALIGN=TOP width='8%'>\n");
      	    out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
      	    out.println("      <b>" + res_json_result.getString("noPcs") + "</b></font></td>\n");
      
       	    out.println("   </tr>\n");

		}
	
	}
     	out.println("</table>\n");
        out.println("<p>");


  }


  /*
  Flight_info flight_info = new Flight_info("ds");
  String io_date = "";
  String action = "";
  
  for (int i=0; i<nawb; i++) {
    //System.out.println("fi awb:" + awblist[i]);
    io_date = flight_info.get_orig_dest(awblist[i]);
    
    if (io_date.equals("Departure")) {
	action = "Discharge";	
  } else if (io_date.equals("Arrival")) {
 	action = "Loading";
    } else {
	action = "Discharge / Loading";
    }
    
    
    String title_awb_no = "Airwaybill<br>Number";
    String title_flg_no = "Flight Number /<br>Vehicle Number";
    String title_flg_date = "Flight<br>" + io_date +" Date";
    String title_mawb_no = "Master Airwaybill Number /<br>Customs Cargo Reference Number (CCRN)";
    String title_piece = "Pieces";
    String title_port = "Port of <br>" + action + " Country or Territory";
    
    out.println("<table border=\"1\" width=\"80%\">\n");
    out.println("<tr>\n");
    out.println("      <td VALIGN=TOP width='10%'>\n");   
    out.println("      <font size='2' face='Univers, Arial, Times New Roman'  color='#a60018'>\n");
    out.println("      <b>" + title_awb_no +"</b></font></td>\n");
      
    out.println("      <td VALIGN=TOP width='11%' nowrap height='32'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>" + title_flg_no + "</b></font></td>\n");
      
    out.println("     <td VALIGN=TOP width='25%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_flg_date + "</b></font></td>\n");

    out.println("      <td VALIGN=TOP width='27%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>" +title_mawb_no + "</b></font></td>\n");
    
    // Port of discharge / loading
    out.println("      <td VALIGN=TOP width='30%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_port + "</b></font></td>\n");

    out.println("      <td VALIGN=TOP width='8%'>\n");
    out.println("      <font size='2' face='Univers, Arial, Times New Roman' color='#a60018'>\n");
    out.println("      <b>"+ title_piece + "</b></font></td>\n");
    out.println("   </tr>\n");
    
    
    //flight_main(awblist[i], flight_conn);
    Vector flg_lst = new Vector();
    
    if (io_date.equals("Departure")) {	
	flg_lst = flight_info.get_outboundData(awblist[i]);
    } else if (io_date.equals("Arrival")) {
 	flg_lst = flight_info.get_inboundData(awblist[i]);
    }
    
    
    if (flg_lst.isEmpty()) {
        out.println("<tr>\n");
	out.println("      <td VALIGN=TOP width='10%'>\n");
	out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
	out.println("      <b>" + awblist[i] + "</b></font></td>\n");
      
	out.println("      <td VALIGN=TOP align=center width='16%' nowrap height='32' colspan=5>\n");
	out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
	out.println("      <b>No information available</b></font></td>\n");
	out.println("   </tr>\n");
    } else {
    	String station_name = "";
    	for (int k=0; k<flg_lst.size(); k++) {
    	   FlightInfo flg_data = (FlightInfo) flg_lst.elementAt(k);
    	   station_name = flight_info.get_station(flg_data.port);
    	   
    	   out.println("<tr>\n");
           out.println("      <td VALIGN=TOP width='10%'>\n");
	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
	   out.println("      <b>" + awblist[i] + "</b></font></td>\n");
      
	   out.println("      <td VALIGN=TOP width='11%' nowrap height='32'>\n");
	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
	   out.println("      <b>" + flg_data.flight_no + "</b></font></td>\n");
      
      	   out.println("     <td VALIGN=TOP width='25%'>\n");
	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
	   out.println("      <b>" + flg_data.flight_date + "</b></font></td>\n");

      	   out.println("      <td VALIGN=TOP width='27%'>\n");
      	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
      	   out.println("      <b>" + flg_data.mawb + "</b></font></td>\n");
      	
      	   out.println("      <td VALIGN=TOP width='30%'>\n");
      	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n"); 
      	   out.println("      <b>" + station_name + "</b></font></td>\n");
      
      	   out.println("      <td VALIGN=TOP width='8%'>\n");
      	   out.println("      <font size='2' face='Univers, Arial, Times New Roman'>\n");
      	   out.println("      <b>" + flg_data.pcs + "</b></font></td>\n");
      
       	   out.println("   </tr>\n");
    
    
    	}
    }
    
    out.println("</table>\n");
    out.println("<p>");
    
    
  }
*/

%>


<p><font size="2" face="Frutiger, Arial" color=#000000>Please make use of the information for 
import and export declaration
 <b>within 14 days.
<br>(Apply on dutiable shipment only)
<br><br>Flight number & master airway bill information is only available for dutiable parcel shipment.
 </b></font></p>
</blockquote></body>
<%
if (nawb < 5) {
   out.println("<SCRIPT LANGUAGE='javascript' SRC='../js_client/copyright_a.js'></SCRIPT>");
}else {
   out.println("<SCRIPT LANGUAGE='javascript' SRC='../js_client/copyright_r.js'></SCRIPT>");
}
%>
</html>
