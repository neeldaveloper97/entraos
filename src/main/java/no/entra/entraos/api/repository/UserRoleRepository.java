package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.model.UserRole;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;
import rx.Observable;

public class UserRoleRepository {

	private DbClient dbClient;

	public UserRoleRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<UserRole> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM user_role").execute())
				.map(dbRow -> dbRow.as(UserRole.class));
	}


	public Single<String> save(UserRole userrole) {
		if(userrole!=null) {
			final String id = StringUtils.ensureId(userrole.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO user_role(id, username, roles, company_ids) VALUES (?, ?, ?, ?)",
									id, 
									userrole.getUsername() ,
									EntityUtils.object_mapToJsonString(userrole.getRoles()),
									EntityUtils.object_mapToJsonString(userrole.getCompany_ids())
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for userrole"));
		}
	}

	public Single<UserRole> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM user_role WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(UserRole.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	

	public Single<Long> update(String id, UserRole userrole) {
		if(userrole!=null) {
			userrole.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE user_role SET roles=:roles, username=:username, company_ids=:companyids, last_synced=:lastsynced WHERE id=:id")
					.namedParam(userrole)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for user role"));
		}
	}
	
	public UserRole findByUsername(String username) {
		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM user_role WHERE username=?")
						.addParam(username)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(UserRole.class);
		} else {
			return null;
		}
	}
	
	public UserRole findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM user_role WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(UserRole.class);
		} else {
			return null;
		}
	}
	

	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM user_role WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
}
