package no.entra.entraos.api.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class ContractProperty implements Serializable {
	private String id;
	private String name="";
	private String value="";
	private String description="";
	
}
