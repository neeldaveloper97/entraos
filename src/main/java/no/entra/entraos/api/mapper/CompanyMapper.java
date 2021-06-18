package no.entra.entraos.api.mapper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.Company;

public class CompanyMapper implements DbMapper<Company> {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public Company read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn name = row.column("name");
		DbColumn org_number = row.column("org_number");
		
		Company com = new Company();
		com.setId(id.as(String.class));
		com.setName(name.as(String.class));
		com.setOrg_number(org_number.as(String.class));
		return com;
	}

	@Override
	public Map<String, ?> toNamedParameters(Company value) {	
		return Map.of(
				"id", value.getId(),
				"name", value.getName(),
				"orgnumber", value.getOrg_number()
				);
		
	}
	

	@Override
	public List<?> toIndexedParameters(Company value) {
		 return List.of(
	                value.getId(),
	                value.getName(),
	                value.getOrg_number()
	        );
	}
	
	

}
