package no.entra.entraos.api.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class ContractCategory implements Serializable{
	private String id;
	private String name="";
	private String description="";
}
