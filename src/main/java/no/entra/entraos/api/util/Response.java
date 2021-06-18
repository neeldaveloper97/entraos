package no.entra.entraos.api.util;

import lombok.Data;
import no.cantara.base.util.StringConv;


@Data
public class Response {

	
	private int responseCode;
	private byte[] data = null;
	
	public String getContent() {
		if(data!=null) {
			return StringConv.UTF8(data);
		}
		return null;
	}
}