package no.entra.entraos.api.util;

import java.util.UUID;

public class StringUtils {

	public static String ensureId(String id) {
		if(id==null || id.length()!=36) {
			id = UUID.randomUUID().toString();
		}
		return id;
	}
	
	public static String ensureNotNull(String value, String defaultVal) {
		return value!=null? value:defaultVal;
	}
	public static String ensureNotNull(String value) {
		return value!=null? value:"";
	}
	
	public  static String ensureNotNullOrEmpty(String value, String defaultVal) {
		return value!=null && !value.isEmpty()? value:defaultVal;
	}
	
	
}
