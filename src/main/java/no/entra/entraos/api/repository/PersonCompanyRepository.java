package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.PersonCompany;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;

public class PersonCompanyRepository {

	private DbClient dbClient;

	public PersonCompanyRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<PersonCompany> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM person_company").execute())
				.map(dbRow -> dbRow.as(PersonCompany.class));
	}

	public Single<String> save(PersonCompany pc) {
		if(pc!=null) {
			
			final String id = StringUtils.ensureId(pc.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO person_company(id, person_ref, company_id, person) VALUES (?, ?, ?, ?)",
									id, 
									pc.getPerson_ref(),								
									pc.getCompany_id(),
									EntityUtils.object_mapToJsonString(pc.getPerson())
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for person company"));
		}
	}

	public Single<PersonCompany> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_company WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(PersonCompany.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}

	public Single<Long> update(String id, PersonCompany pc) {
		if(pc!=null) {
			pc.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE person_company SET person_ref=:personref, company_id=:companyid, person=:person WHERE id=:id")
					.namedParam(pc)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for person company"));
		}
	}
	
	public PersonCompany findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_company WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(PersonCompany.class);
		} else {
			return null;
		}
	}
	
	public PersonCompany findByCompanyIdAndPersonRef(String companyId, String personRef) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_company WHERE company_id=? AND person_ref=?")
						.addParam(companyId)
						.addParam(personRef)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(PersonCompany.class);
		} else {
			return null;
		}
	}
	

	
	public Multi<PersonCompany> findAllByCompanyId(String companyId) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_company WHERE company_id=?")
						.addParam(companyId)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonCompany.class));
	}
	
	public Multi<PersonCompany> findAllByPersonRef(String personRef) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_company WHERE person_ref=?")
						.addParam(personRef)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonCompany.class));
	}
	
	public Single<Long> deleteByCompanyId(String companyId) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_company WHERE company_id = ?")
				.addParam(companyId)
				.execute()
				);
	}
	
	public Single<Long> deleteByPersonRef(String personRef) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_company WHERE person_ref = ?")
				.addParam(personRef)
				.execute()
				);
	}
	
	public Single<Long> deleteByPersonRefAndCompanyId(String personRef, String companyId) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_company WHERE person_ref = ? AND company_id =?")
				.addParam(personRef)
				.addParam(companyId)
				.execute()
				);
	}

	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_company WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
}
