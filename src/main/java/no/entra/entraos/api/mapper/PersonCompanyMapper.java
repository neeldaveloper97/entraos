package no.entra.entraos.api.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.common.mapper.MapperException;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.PersonCompany;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.domain.person.Person;

public class PersonCompanyMapper implements DbMapper<PersonCompany> {

	
	@Override
	public PersonCompany read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn person_ref = row.column("person_ref");
		DbColumn person = row.column("person");
		DbColumn company_id = row.column("company_id");
		DbColumn created_at = row.column("created_at");
			
		PersonCompany pc = new PersonCompany();
		pc.setId(id.as(String.class));
		pc.setPerson_ref(person_ref.as(String.class));
		pc.setCompany_id(company_id.as(String.class));
		pc.setCreated_at(created_at.as(Timestamp.class).toLocalDateTime());
		
		try {
			pc.setPerson(EntityUtils.mapFromJson(person.as(String.class), Person.class));
			
		} catch (JsonProcessingException | MapperException e) {
			e.printStackTrace();
		}
		
		return pc;
	}

	@Override
	public Map<String, ?> toNamedParameters(PersonCompany value) {
		
		return Map.of(
				"id", value.getId(),
				"personref", value.getPerson_ref(),
				"companyid", value.getCompany_id(),
				"person", EntityUtils.object_mapToJsonString(value.getPerson()),
				"createdat", value.getCreated_at()
				);
		
	}
	

	@Override
	public List<?> toIndexedParameters(PersonCompany value) {
		
		 return List.of(
	                value.getId(),
	                value.getPerson_ref(),
	                EntityUtils.object_mapToJsonString(value.getPerson()),
	                value.getCompany_id(),
	                value.getCreated_at()
	        );
	}
	
	

}
