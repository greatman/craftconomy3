package com.greatmancode.craftconomy3.converter;

import java.util.HashMap;

public class ConverterList {

	public static HashMap<String,Converter> converterList = new HashMap<String,Converter>();
	public ConverterList() {
		converterList.put("iconomy", new Iconomy6());
	}
}
