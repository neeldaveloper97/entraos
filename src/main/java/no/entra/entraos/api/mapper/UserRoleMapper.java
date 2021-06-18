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
import no.entra.entraos.api.model.UserRole;
import no.entra.entraos.api.util.EntityUtils;

public class UserRoleMapper implements DbMapper<UserRole> {
	
	@Override
	public UserRole read(DbRow row) {
		DbColumn id = row.column("id");
		DbColumn username = row.column("username");
		DbColumn roles = row.column("roles");
		DbColumn company_domains = row.column("company_ids");
		DbColumn last_synced = row.column("last_synced");
		
		UserRole userrole = new UserRole();
		userrole.setId(id.as(String.class));
		userrole.setUsername(username.as(String.class));
		userrole.setLast_synced(last_synced.as(Timestamp.class).toLocalDateTime());
		try {
			userrole.setRoles(EntityUtils.mapFromJson(roles.as(String.class), new TypeReference<Set<String>>() { }));
			userrole.setCompany_ids(EntityUtils.mapFromJson(company_domains.as(String.class), new TypeReference<Set<String>>() { }));
		} catch (JsonProcessingException | MapperException e) {
			e.printStackTrace();
		}
		return userrole;
	}

	@Override
	public Map<String, ?> toNamedParameters(UserRole value) {
		
		return Map.of(
				"id", value.getId(),
				"username", value.getUsername(),
				"roles", EntityUtils.object_mapToJsonString(value.getRoles()),
				"companyids", EntityUtils.object_mapToJsonString(value.getCompany_ids()),
				"lastsynced", value.getLast_synced()
				);
		
	}
	

	@Override
	public List<?> toIndexedParameters(UserRole value) {
	
		 return List.of(
	                value.getId(),
	                value.getUsername(),
	                EntityUtils.object_mapToJsonString(value.getRoles()),
	                EntityUtils.object_mapToJsonString(value.getCompany_ids()),
	                value.getLast_synced()
	        );
	}
	
	

}
