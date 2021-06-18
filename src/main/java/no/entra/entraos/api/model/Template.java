package no.entra.entraos.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Template implements Serializable{
	
	private String id;
	private String contract_name="";
	private String contract_category_id; //food or service
	private String contract_type_id; //sub category
	private String description="";
	private List<ContractProperty> contract_properties = new ArrayList<ContractProperty>(); //list of overriden contract properties
	private LocalDateTime created_at;
	private LocalDateTime last_updated;
}
