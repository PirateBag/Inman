package com.inman.model;

import java.lang.reflect.Field;

public class MetaData {
	public static String show( String className  ) throws ClassNotFoundException {
		Class<?> myclass = Class.forName( className );
		
		String rValue = ""; 
		Field[] fields = myclass.getDeclaredFields();
		
		for ( Field f : fields ) {
			rValue += f.getName() + "\n";
		}
		return rValue;
	}
}
