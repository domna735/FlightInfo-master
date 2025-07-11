<!-- hide
var win;
var msie3=false;

browserVer=parseInt( navigator.appVersion );
if( browserVer == 2 && navigator.appName == "Microsoft Internet Explorer" ) {
  browserVer++;
  msie3=true;
}

function winclose() {
  if( browserVer <= 2 )
    return true;
  if( msie3 != true && typeof( win ) == 'object' && !win.closed )
    win.close();
  return true;
}

function dhl_alert(name, text, height)
{    	    

  if( typeof( height) == "undefined" )
    height = 200;
  winclose();
  win=open( "", "dhl_alert", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=300,height=" + height);
  if( msie3 == true )
    while( typeof( win.document ) == "undefined" );

  win.document.write( "<HTML><HEAD><TITLE>Flight Information</TITLE></HEAD>\n")
  win.document.write( "<script language=\"JavaScript\"> \n <!-- \n")

  win.document.write( "\nfunction private_MouseOver() \n")
  win.document.write( "{\nif (document.images) { eval(\"document.\" + this.stImageName + \".src=\\\'\" + this.stOverImage + \"\\\'\");}\n}\n")

  win.document.write( "\nfunction private_MouseOut() \n")
  win.document.write( "{\nif (document.images) { eval(\"document.\" + this.stImageName + \".src=\\\'\" + this.stOutImage + \"\\\'\");}\n}\n")

  win.document.write( "\n\nfunction objMouseChangeImg(stImageName, stOverImage, stOutImage) \n")
  win.document.write( "{\nthis.stImageName = stImageName; \n this.stOverImage = stOverImage; \n this.stOutImage = stOutImage; \n")
  win.document.write( " this.MouseOut = private_MouseOut; \n this.MouseOver = private_MouseOver; } \n ")

  win.document.write( "\n\nobjPIC1 = new objMouseChangeImg('PIC1', \'../images/return2.gif\', \'../images/return1.gif\'); ")
  
  win.document.write( "\n // --> </script> \n")
/*
  win.document.write("<BODY BGCOLOR=\"#FFFFFF\">\n" );                                                           

  win.document.write( "<TABLE CELLPADDING=6 WIDTH=\"100\%\" HEIGHT=\"100\%\" BORDER=0><TR><TD BGCOLOR=\"#951314\"><FONT FACE=\"arial,helvetica\" COLOR=\"#FFFFFF\" SIZE=\"+1\"><B>" + name +"</B></FONT></TD></TR>" );                          
  win.document.write( "<TR><TD><FONT FACE=\"arial,helvetica\" SIZE=2>"+ text +"</FONT></TD></TR>" ); 
  
  win.document.write( "<TR><TD ALIGN=RIGHT><a href=\"javascript:window.close()\" onMouseOver=objPIC1.MouseOver() onMouseOut=objPIC1.MouseOut()><img src=../images/return1.gif border=0 name=PIC1></a></td></tr></TABLE>" );
*/
  win.document.write("<BODY BGCOLOR=\"#FFFFFF\" TEXT=\"#000000\" topmargin=0 leftmargin=0 rightmargin=0 marginheight=0 marginwidth=0 SCROLL=NO>\n");
  win.document.write("<table width=\"103\%\" border=0 CELLSPACING=0 CELLPADDING=0>");
  win.document.write("<tr bgcolor=\"#FFCC00\"><td>&nbsp;</td></tr>");
  win.document.write("<tr bgcolor=\"#CC0000\" valign=\"bottom\"><td height=\"45\">");
  win.document.write("<table height=\"100\%\" width=\"100\%\" border=0>");
  win.document.write("<tr valign=\"bottom\"><td width=\"2\%\">&nbsp;</td><td><font color=\"#FFFFFF\" size=\"+1\" face=\"Arial\"><b>"+name+"</b></font></td></tr>");
  win.document.write("</table>");
  win.document.write("</td></tr>");
  win.document.write("<TR><TD>");
  win.document.write("<table CELLPADDING=12 BORDER=0 WIDTH=100%>");
  win.document.write("<tr><td><FONT FACE=\"Arial\" SIZE=2>"+ text +"</FONT></td></tr>");
  win.document.write("<tr><TD ALIGN=RIGHT><form name=\"back\"><input type=button name=\"toReturn\" value=\"Return\" onClick=\"javascript:window.close()\"></form></TD></TR>");
  win.document.write("</table>");
  win.document.write("</TD></TR>");
  win.document.write("</table>");
  win.document.write( "</BODY></HTML>\n" );                                     
  win.document.close();           

  return false;
}

function strip( instring )
{
  var outstring="";
  var bit="";
  var founddigit=false;

  for( j=0;j<instring.length;j++ ) {
    c=instring.charAt(j);
    if( c != " " && c !="\t" && c !="\n"  && c !="\r" ) {
      if( founddigit == true )
        outstring+=bit;
      bit="";
      outstring+=c;
      founddigit=true;
    }
    else if( founddigit == true )
      bit+=c;
  }
  return outstring;
}

function checkForm(form)
{
  if( browserVer <= 2 )
    return true;
  awb=strip( form.airbill.value );
  var awbs=new Array;
  var tmp="";
  var curr=0;
  for(i=0;i<awb.length;i++) {
    c=awb.charAt( i );
    d=parseInt( c );
    if( ( msie3==true && !( d==0 && c != "0" ) ) || ( msie3==false && !isNaN( d ) ) ) {
      tmp+=c;
    }
    else {
      if( tmp != "" ) {
        awbs[curr]=tmp;
        curr++;
        tmp="";
      }
    }
  }
  if( tmp != "" )
    awbs[curr]=tmp;

  num=awbs.length;

  if( num == 0 )
  {
     dhl_alert( "The Airwaybill No. is missing", "Please enter a 10-digit Airwaybill No.", 180 );
     return false;
  }


  errors="";
  off=0;
  for( i=0 ; i< num ; i++ )
  {
    awbs[i]=strip( awbs[i] );
    if( awbs[i].length == 3 && awbs[i+1] && awbs[i+1].length == 4 && awbs[i+2] && awbs[i+2].length == 3 )
    {
      if( errors == "" )
        errors=""+(i+1);
      else
        errors+=(  ", " + (i+1-off) );
      off+=2;
    }
  }

  if( errors != "" ) {
    if( msie3 == false )
      numbad=errors.split( ", " );
    else
      numbad=new Array(2);
  if( numbad.length == 1 )
      dhl_alert( "Invalid Airwaybill No.", "Airwaybill No. " + numbad[0] + " has been split into 3 groups of digits.<BR>Airwaybill No. should be an unbroken string of 10-digits.<P>Please correct or remove this Airwaybill No.", 230 );
    else
      dhl_alert( "Invalid Airwaybill numbers", "Airwaybill numbers " + errors + " have all been split into 3 groups of digits.<BR>Airwaybill numbers should be an unbroken string of 10-digits.<P>Please correct or remove these Airwaybill numbers.", 230 );

    return false;
  }

  if( num > 40  ) {
     dhl_alert( "More than 40 shipments are input", "Please enter a maximum of 40 shipments.", 180 );
     return false;
  }

  errors="";
  for( i=0 ; i< num ; i++ ) {
    awbs[i]=strip( awbs[i] );
    if( awbs[i].length != 10 && !isNaN( awbs[i] ) ) {
      if( errors == "" )
        errors=""+(i+1);
      else
        errors+=(  ", " + (i+1) );
    }
  }
  if( errors != "" ) {
    if( msie3 == false )
      numbad=errors.split( ", " );
    else
      numbad=new Array(2);

    if( numbad.length == 1 )
      dhl_alert( "Invalid Airwaybill No.", "The Airwaybill No. on row " + numbad[0] + " is not a 10-digit No.<P>Please enter a 10-digit Airwaybill No." );
    else
      dhl_alert( "Invalid Airwaybill numbers", "The Airwaybill numbers on rows " + errors + " are not 10-digit numbers.<P>Please enter 10-digit Airwaybill numbers." );

    return false;
  }

  errors="";
  for( i=0 ; i< num ; i++ ) {
    awbs[i]=strip( awbs[i] );
    check1=awbs[i].substring(9,10);
    tocheck=awbs[i].substring(0,9);
    check2=tocheck % 7;
    if( check1 != check2  && awbs[i] != "" ) {
      if( errors == "" )
        errors=""+(i+1);
      else
        errors+=(  ", " + (i+1) );
    }
  }
  if( errors != "" ) {
    if( msie3 == false )
      numbad=errors.split( ", " );
    else
      numbad=new Array(2);

    if( numbad.length == 1 )
      dhl_alert( "Invalid Airwaybill No.", "The Airwaybill No. on row " + numbad[0] + " is not a valid Airwaybill No.<P>Please correct or remove this Airwaybill No." );
    else
      dhl_alert( "Invalid Airwaybill numbers", "The Airwaybill numbers on rows " + errors + " are not valid airwaybill numbers.<P>Please correct or remove these Airwaybill numbers." );

    return false;
  }

  form.awb.value=awbs[0];

  for( i=1 ; i< num ; i++ )
    form.awb.value+="|"+awbs[i];

  return true;
}

// -->
