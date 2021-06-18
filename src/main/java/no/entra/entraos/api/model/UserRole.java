package no.entra.entraos.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class UserRole implements Serializable {	
	private String id;
	private Set<String> roles = new HashSet<String>();
	private Set<String> company_ids = new HashSet<String>();
	private String username;
	private LocalDateTime last_synced; //when synchronized against Whydah	

	public boolean hasSuperAdminRole() {
		return roles.contains("SUPERADMIN");
	}
	
	public boolean hasCompanyAdminRole() {
		return roles.contains("COMPANYADMIN");
	}
	
	public boolean hasValidAccess(String companyId) {
		return hasSuperAdminRole() || (hasCompanyAdminRole() && company_ids.contains(companyId));
	}
}
