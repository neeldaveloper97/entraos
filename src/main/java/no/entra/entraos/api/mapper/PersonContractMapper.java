package no.entra.entraos.api.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.common.mapper.MapperException;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.domain.person.Person;

public class PersonContractMapper implements DbMapper<PersonContract> {

	@Override
	public PersonContract read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn person_ref = row.column("person_ref");
		DbColumn contract_id = row.column("contract_id");
		DbColumn company_id = row.column("company_id");
		DbColumn created_at = row.column("created_at");
			
		PersonContract pc = new PersonContract();
		pc.setId(id.as(String.class));
		pc.setPerson_ref(person_ref.as(String.class));
		pc.setContract_id(contract_id.as(String.class));
		pc.setCompany_id(company_id.as(String.class));
		pc.setCreated_at(created_at.as(Timestamp.class).toLocalDateTime());
		
		return pc;
	}

	@Override
	public Map<String, ?> toNamedParameters(PersonContract value) {
		
		return Map.of(
				"id", value.getId(),
				"personref", value.getPerson_ref(),
				"contractid", value.getContract_id(),
				"companyid", value.getCompany_id(),
				"createdat", value.getCreated_at()
				);
		
	}
	

	@Override
	public List<?> toIndexedParameters(PersonContract value) {
		
		 return List.of(
	                value.getId(),
	                value.getPerson_ref(),
	                value.getContract_id(),
	                value.getCompany_id(),
	                value.getCreated_at()
	        );
	}
	
	

}
