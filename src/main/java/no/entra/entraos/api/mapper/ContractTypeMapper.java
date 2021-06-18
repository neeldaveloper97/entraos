package no.entra.entraos.api.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.ContractType;

public class ContractTypeMapper implements DbMapper<ContractType> {

	
	@Override
	public ContractType read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn contract_name = row.column("name");		
		DbColumn description = row.column("description");
		
		
		ContractType ct = new ContractType();
		ct.setId(id.as(String.class));
		ct.setName(contract_name.as(String.class));	
		ct.setDescription(description.as(String.class));
		
		return ct;
	}

	@Override
	public Map<String, ?> toNamedParameters(ContractType value) {
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("id", value.getId());
		m.put("name", value.getName());
		m.put("description", value.getDescription());
		return m;
		
	}

	@Override
	public List<?> toIndexedParameters(ContractType value) {
		
		 return Arrays.asList(
	                value.getId(),
	                value.getName(),
	                value.getDescription()
	               
	        );
	}
	
	

}
