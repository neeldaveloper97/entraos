package no.entra.entraos.api.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.ContractProperty;

public class ContractPropertyMapper implements DbMapper<ContractProperty> {

	
	@Override
	public ContractProperty read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn name = row.column("name");	
		DbColumn value = row.column("value");
		DbColumn description = row.column("description");
		
		
		ContractProperty cc = new ContractProperty();
		cc.setId(id.as(String.class));
		cc.setName(name.as(String.class));	
		cc.setDescription(description.as(String.class));
		cc.setValue(value.as(String.class));
		return cc;
	}

	@Override
	public Map<String, ?> toNamedParameters(ContractProperty value) {
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("id", value.getId());
		m.put("name", value.getName());
		m.put("value", value.getValue());
		m.put("description", value.getDescription());
		return m;		
	}

	@Override
	public List<?> toIndexedParameters(ContractProperty value) {
		
		
		 return Arrays.asList(
	                value.getId(),
	                value.getName(),
	                value.getValue(),
	                value.getDescription()
	                
	        );
	}
	
}
