package no.entra.entraos.api.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class ContractType implements Serializable{
	private String id;
	private String name="";
	private String description="";
	
	public no.entra.entraos.domain.person.ContractType getMatchedContractType() {
	    for (no.entra.entraos.domain.person.ContractType me : no.entra.entraos.domain.person.ContractType.values()) {
	        if (me.name().equalsIgnoreCase(name))
	            return me;
	    }
	    return null;
	}
}
