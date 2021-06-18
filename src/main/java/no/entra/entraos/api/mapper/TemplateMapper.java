package no.entra.entraos.api.mapper;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.helidon.common.mapper.MapperException;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;

public class TemplateMapper implements DbMapper<Template> {

	
	@Override
	public Template read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn contract_name = row.column("contract_name");
		DbColumn contract_type_id = row.column("contract_type_id");
		DbColumn contract_category_id = row.column("contract_category_id");		
		DbColumn description = row.column("description");
		DbColumn contract_properties = row.column("contract_properties");
		DbColumn last_updated = row.column("last_updated");
		DbColumn created_at = row.column("created_at");
		
		Template template = new Template();
		template.setId(id.as(String.class));
		template.setContract_name(contract_name.as(String.class));
		template.setContract_type_id(contract_type_id.as(String.class));
		template.setContract_category_id(contract_category_id.as(String.class));
		template.setLast_updated(last_updated.as(Timestamp.class).toLocalDateTime());
		template.setCreated_at(created_at.as(Timestamp.class).toLocalDateTime());
		template.setDescription(description.as(String.class));
		try {
			
			template.setContract_properties(EntityUtils.mapFromJson(contract_properties.as(String.class), new TypeReference<List<ContractProperty>>() { }));
		} catch (JsonProcessingException | MapperException e) {
			e.printStackTrace();
		}
		
		
		return template;
	}

	@Override
	public Map<String, ?> toNamedParameters(Template value) {
		
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", value.getId());
		m.put("contractname", value.getContract_name());
		m.put("contracttypeid", value.getContract_type_id());
		m.put("contractcategoryid", value.getContract_category_id());
		m.put("description", value.getDescription());
		m.put("contractproperties",  StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getContract_properties()), "[]"));
		m.put("lastupdated", value.getLast_updated());
		m.put("createdat", value.getCreated_at());
		return m;
		
	}
	
	

	@Override
	public List<?> toIndexedParameters(Template value) {
		
		
		 return Arrays.asList(
	                value.getContract_name(),
	                value.getContract_category_id().toString(),
	                value.getContract_type_id().toString(),
	                value.getDescription(),	             
	                StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getContract_properties()), "[]"),    
	                value.getLast_updated(),
	                value.getCreated_at()
	        );
	}
	
	

}
