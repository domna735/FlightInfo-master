package com.hkapps.util;

import javax.naming.*;
import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;


public class DataTypeUtil
{

   String whitespace = " \t\n\r";
   
   public static String trimAll(String sString)
   {
       if (sString == null || sString.equals("")) {
	  return sString;
       } else {
		while (sString.substring(0,1).equals(" "))
		{
			sString = sString.substring(1, sString.length());
		}
		while (sString.substring(sString.length()-1, sString.length()).equals(" "))
		{
			sString = sString.substring(0,sString.length()-1);
		}
		return sString;
       }
   }
   
   public static String trimR(String sString)
   {
       if (sString == null || sString.equals("")) {
	  return sString;
       } else {
       		/*
		while (sString.substring(0,1).equals(" "))
		{
			sString = sString.substring(1, sString.length());
		}
		*/		
		while (sString.substring(sString.length()-1, sString.length()).equals(" "))
		{
			sString = sString.substring(0,sString.length()-1);
		}
		return sString;
       }
   }


   public static boolean isNumeric(String str)
   {
       for (char c : str.toCharArray())
       {
           if (!Character.isDigit(c)) return false;
       }
       return true;
   }
	
   public static boolean isAlphabetic(String str)
   {
	   for (char c : str.toCharArray())
       {
		  if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) return false;
	   }
	   return true;
   }

   public static boolean isAlphaNumeric(String str)
   {
	   for (char c : str.toCharArray())
       {
		  if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (Character.isDigit(c)))) return false;
	   }
	   return true;
   
   }

   public static Date hk_datetime() {
       Date t = new Date();
       //Date today = new Date(t.toString().replace("+0000", "-0800"));
       //return today;
       return t;
   }
  
   
   public static int getCurrentMonth() {
	Date t = new Date();
	return t.getMonth() + 1;
   }
   
   public static int getCurrentDate() {
	Date t = new Date();
	return t.getDate();
   }
   
   public static int getCurrentYear() {
	Date t = new Date();
	return t.getYear() + 1900;
   }
      
   //convert double quote and single quote in query string
   public static String convertQStr(String inStr) {
	String outStr = "";
	int symbol_pos = 0;
	String doubleQ = "\"";
	String singleQ = "'";
	String and_sf = "&";
	
	for (int i=0; i<inStr.length(); i++) {
	   
	   if ((doubleQ.indexOf(inStr.charAt(i))) != -1) {
	   	outStr += inStr.substring(symbol_pos,i) + "%22";
	   	symbol_pos = i+1;
	   } else if ((singleQ.indexOf(inStr.charAt(i))) != -1) {
	   	outStr += inStr.substring(symbol_pos,i) + "%27";
	   	symbol_pos = i+1;
	   } else if ((and_sf.indexOf(inStr.charAt(i))) != -1) {
	   	outStr += inStr.substring(symbol_pos,i) + "%26";
	   	symbol_pos = i+1;
	   }	
	}
	
	if (symbol_pos != inStr.length()) outStr += inStr.substring(symbol_pos, inStr.length());
	
	return outStr;
   }


   public static String replaceSingleQuote(String inStr) {
	String outStr = "";
	int symbol_pos = 0;
	String symbol_str = "'";
	
	for (int i=0; i<inStr.length(); i++) {
	   
	   if ((symbol_str.indexOf(inStr.charAt(i))) != -1) {
	   	
	   	outStr += inStr.substring(symbol_pos,i) + "''";
	   	symbol_pos = i+1;	
	   }
	}
	
	if (symbol_pos != inStr.length()) outStr += inStr.substring(symbol_pos, inStr.length());
	
	return outStr;
   }
   
   public static String replaceDoubleQuote(String inStr) {
	String outStr = "";
	int symbol_pos = 0;
	String symbol_str = "\"";
	
	for (int i=0; i<inStr.length(); i++) {
	   
	   if ((symbol_str.indexOf(inStr.charAt(i))) != -1) {
	   	
	   	outStr += inStr.substring(symbol_pos,i) + "'";	   	
	   	symbol_pos = i+1;	
	   }
	}
	
	if (symbol_pos != inStr.length()) outStr += inStr.substring(symbol_pos, inStr.length());
	
	return outStr;
   }

   public static boolean containSingleQuote(String inStr) {
	String symbol_str = "'";
	
	for (int i=0; i<inStr.length(); i++) {
	   if ((symbol_str.indexOf(inStr.charAt(i))) != -1) {
		return true;   	
	   }
	}
	return false;
   }

   public static boolean containDoubleQuote(String inStr) {
	String symbol_str = "\"";
	
	for (int i=0; i<inStr.length(); i++) {
	   if ((symbol_str.indexOf(inStr.charAt(i))) != -1) {
		return true;   	
	   }
	}
	return false;
   }
   
   public boolean isAwb(String instr) {
	 if (instr == null) {
		return false;
	 } else {
	   if (instr.equals("")) {
		   return false;
	   } else {
		   if ((!isNumeric(instr)) || (instr.length() != 10)) {
			   return false;
		   } else {
			   
			   int check1=Integer.parseInt(instr.substring(9,10));
			   int tocheck=Integer.parseInt(instr.substring(0,9));
			   int check2=tocheck % 7;
			   
			   if (check1 != check2) {
					return false;
			   } else {
					return true;
			   }
			   
		   }
	   }
	 }
   }
   
   public boolean isAccount(String instr) {
	 if (instr == null) {
		return false;
	 } else {
	   if (instr.equals("")) {
		   return false;
	   } else {
		   if ((!isNumeric(instr)) || (instr.length() != 9)) {
			   return false;
		   } else {
			   if ((instr.substring(0,2).equals("63")) || (instr.substring(0,2).equals("96")) || (instr.substring(0,2).equals("95"))) {
					return true;
			   } else {
					return false;
			   }
			   
		   }
	   }
	 }
   }
   
   public boolean isValidLine(String instr, int maxlen) {
	   
	   if ((containInvalidSymbol(instr)) || (instr.length() > maxlen)) {
		   return false;
	   } else {
		   return true;
	   }
   }
   
   public boolean isValidPhone(String instr, int maxlen) {
	   
	   if (instr.length() > maxlen) {
		   return false;
	   } else {
		 for (int i=0; i<instr.length(); i++) {
	      if (!isNumeric(instr.substring(i,i+1))) {
			if ((isAlphabetic(instr.substring(i,i+1))) || (containInvalidSymbol_Phone(instr.substring(i,i+1)))) return false;
		  }
	     }
	   }
	   return true;
		
   }
   
   public boolean isValidEmail(String instr, int maxlen) {
	   
	   if ((containInvalidSymbol_Email(instr)) || (instr.length() > maxlen)) {
		   return false;
	   } else {
		   
		 int i = 1;
		 int sLength = instr.length();

			// look for @
			while ((i < sLength) && (instr.charAt(i) != '@'))
			{ i++;
			}

			if ((i >= sLength) || (instr.charAt(i) != '@')) return false;
			else i += 2;

			// look for .
			while ((i < sLength) && (instr.charAt(i) != '.'))
			{ i++;
			}

			// there must be at least one character after the .
			if ((i >= sLength - 1) || (instr.charAt(i) != '.')) return false;
			
	   }
	   return true;

   }
   
   public boolean isValidPassword(String instr, int minlen, int maxlen) {
		if (!isAlphaNumeric(instr)) {
		  return false;
		} else {
			if ((instr.length() < minlen) || (instr.length() > maxlen)) {
				return false;
			}
		}
		
		return true;
		
   }
   
   public boolean isValidSup_qt(String instr, int qt_min, int qt_max) {
		if (!isNumeric(instr)) {
			return false;
		} else {
			if ((Integer.parseInt(instr) < qt_min) || (Integer.parseInt(instr) > qt_max)) {
				return false;
			}
		}

		return true;
   }
   
   public boolean containInvalidSymbol (String s) {   
		
		String InvalidSymbol = "$%*=;<>?\\\"";
		
		//block: $%*=\;"<>?
		//used: `!@#&()-+[]:',./
		//not used: ~$%^*_={}|\;"<>?
		//still allow: ~^_{}|
		
		for (int i = 0; i < s.length(); i++)
		{   
			if (InvalidSymbol.indexOf(s.charAt(i)) != -1) return true;
		}

		// All characters do not contain invalid symbol.
		return false;
   }

	public boolean containInvalidSymbol_Email (String s) {   
		
		String InvalidSymbol = "`~!#$%^&*()+={}|[]:;'<>?,/\\\"";
		//allow @.-_
			
		for (int i = 0; i < s.length(); i++)
		{   
			if (InvalidSymbol.indexOf(s.charAt(i)) != -1) return true;
			if (whitespace.indexOf(s.charAt(i)) != -1) return true;
		}
		// All characters do not contain invalid symbol.
		return false;
	}

	public boolean containInvalidSymbol_Phone (String s) {
		
		String InvalidSymbol = "`~!@#$%^&*_={}|[]:;'<>?,.\\\"";
		//allow +-() /
		
		for (int i = 0; i < s.length(); i++)
		{   
			if (InvalidSymbol.indexOf(s.charAt(i)) != -1) return true;
		}
		// All characters do not contain invalid symbol.
		return false;
	}

   
    

}
