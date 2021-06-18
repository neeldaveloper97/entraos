package no.entra.entraos.api.mapper;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.helidon.common.mapper.MapperException;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.model.ContractStatus;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;

public class CompanyContractMapper implements DbMapper<CompanyContract> {

	
	@Override
	public CompanyContract read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn contract_name = row.column("contract_name");
		DbColumn company_id = row.column("company_id");
		DbColumn contract_type_id = row.column("contract_type_id");
		DbColumn contract_category_id = row.column("contract_category_id");
		DbColumn description = row.column("description");
		DbColumn contract_properties = row.column("contract_properties");
		DbColumn last_updated = row.column("last_updated");
		DbColumn created_at = row.column("created_at");
		DbColumn valid_from = row.column("valid_from");
		DbColumn valid_to = row.column("valid_to");
		DbColumn status = row.column("status");
		DbColumn docs = row.column("docs");
		DbColumn quantity = row.column("quantity");
		
		CompanyContract contract = new CompanyContract();
		contract.setId(id.as(String.class));
		contract.setContract_name(contract_name.as(String.class));
		contract.setCompany_id(company_id.as(String.class));
		contract.setContract_type_id(contract_type_id.as(String.class));
		contract.setContract_category_id(contract_category_id.as(String.class));
		contract.setLast_updated(last_updated.as(Timestamp.class).toLocalDateTime());
		contract.setCreated_at(created_at.as(Timestamp.class).toLocalDateTime());
		contract.setValid_from(valid_from.as(Timestamp.class).toLocalDateTime());
		contract.setValid_to(valid_to.as(Timestamp.class).toLocalDateTime());
		contract.setStatus(ContractStatus.valueOf(status.as(String.class)));
		contract.setDescription(description.as(String.class));
		contract.setQuantity(quantity.as(Integer.class));
		try {
			contract.setContract_properties(EntityUtils.mapFromJson(contract_properties.as(String.class), new TypeReference<List<ContractProperty>>() { }));
			contract.setDocs(EntityUtils.mapFromJson(docs.as(String.class), new TypeReference<List<String>>() {}));
		} catch (JsonProcessingException | MapperException e) {
			e.printStackTrace();
		}
		
		
		return contract;
	}

	@Override
	public Map<String, ?> toNamedParameters(CompanyContract value) {
		
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", value.getId());
		m.put("contractname", value.getContract_name());
		m.put("companyid", value.getCompany_id());
		m.put("contracttypeid", value.getContract_type_id());
		m.put("contractcategoryid", value.getContract_category_id());
		m.put("description", value.getDescription());
		m.put("contractproperties", StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getContract_properties()), "[]"));
		m.put("lastupdated", value.getLast_updated());
		m.put("createdat", value.getCreated_at());
		m.put("validto", value.getValid_to());
		m.put("validfrom", value.getValid_from());
		m.put("status", value.getStatus().toString());
		m.put("quantity", value.getQuantity());
		m.put("docs", StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getDocs()), "[]"));
		
		return m;
		
	}
	


	@Override
	public List<?> toIndexedParameters(CompanyContract value) {
		
		 return Arrays.asList(
	                value.getContract_name(),
	                value.getCompany_id(),
	                value.getContract_category_id(),
	                value.getContract_type_id(),
	                value.getDescription(),
	                StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getContract_properties()), "[]"),
	                value.getLast_updated(),
	                value.getCreated_at(),
	                value.getValid_from(),
	                value.getValid_to(),
	                value.getStatus().toString(),
	                value.getQuantity(),
	                StringUtils.ensureNotNullOrEmpty(EntityUtils.object_mapToJsonString(value.getDocs()), "[]")
	        );
	}
	
	

}
