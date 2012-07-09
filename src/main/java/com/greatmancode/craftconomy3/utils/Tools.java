package com.greatmancode.craftconomy3.utils;

public class Tools {

	public static boolean isInteger(String number)
	{
		boolean result = false;
		try
		{
			Integer.parseInt(number);
			result = true;
		}
		catch (NumberFormatException e){}
		return result;
	}
	
	public static boolean isLong(String number)
	{
		boolean result = false;
		try
		{
			Long.parseLong(number);
			result = true;
		}
		catch (NumberFormatException e){}
		return result;
	}
	
	public static boolean isdouble(String number)
	{
		boolean result = false;
		try
		{
			Double.parseDouble(number);
			result = true;
		}
		catch (NumberFormatException e){}
		return result;
	}
}
