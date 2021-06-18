package no.entra.entraos.api.model;

import lombok.Data;

@Data
public class ContractDocument {

	private String id;
	private String file_name;
	private String file_type;
	private byte[] data;
	
}
