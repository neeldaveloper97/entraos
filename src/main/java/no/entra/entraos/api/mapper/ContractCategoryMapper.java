package no.entra.entraos.api.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.ContractCategory;

public class ContractCategoryMapper implements DbMapper<ContractCategory> {

	
	@Override
	public ContractCategory read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn contract_name = row.column("name");		
		DbColumn description = row.column("description");
		
		
		ContractCategory cc = new ContractCategory();
		cc.setId(id.as(String.class));
		cc.setName(contract_name.as(String.class));	
		cc.setDescription(description.as(String.class));
		
		return cc;
	}

	@Override
	public Map<String, ?> toNamedParameters(ContractCategory value) {
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("id", value.getId());
		m.put("name", value.getName());
		m.put("description", value.getDescription());
		return m;
	}

	@Override
	public List<?> toIndexedParameters(ContractCategory value) {
		
		
		
		 return Arrays.asList(
	                value.getId(),
	                value.getName(),
	                value.getDescription()
	               
	        );
	}
	
	

}
