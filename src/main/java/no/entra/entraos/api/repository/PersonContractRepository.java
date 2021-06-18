package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.PersonCompany;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.util.StringUtils;

public class PersonContractRepository {

	private DbClient dbClient;

	public PersonContractRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<PersonContract> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM person_contract").execute())
				.map(dbRow -> dbRow.as(PersonContract.class));
	}

	public Single<String> save(PersonContract pc) {
		if(pc!=null) {
			
			final String id = StringUtils.ensureId(pc.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO person_contract(id, person_ref, contract_id, company_id) VALUES (?, ?, ?, ?)",
									id, 
									pc.getPerson_ref(),
									pc.getContract_id(),
									pc.getCompany_id()
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for person contract"));
		}
	}

	public Single<PersonContract> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_contract WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(PersonContract.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}

	public Single<Long> update(String id, PersonContract pc) {
		if(pc!=null) {
			pc.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE person_contract SET person_ref=:personref, contract_id=:contractid, company_id=:companyid WHERE id=:id")
					.namedParam(pc)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for person contract"));
		}
	}
	
	public Long countByCompanyIdAndContractId(String companyId, String contractId) {
		
		 DbRow row = this.dbClient
				 .execute(exec -> exec
						 .createQuery("SELECT COUNT(id) AS num FROM person_contract WHERE company_id=? AND contract_id=?")
						 .addParam(companyId)
						 .addParam(contractId)
						 .execute()
						 ).first().await();
		  return row.column("num").as(Long.class);
		  
	}
	
	public PersonContract findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_contract WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(PersonContract.class);
		} else {
			return null;
		}
	}

	
	public Multi<PersonContract> findAllByCompanyId(String companyId) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_contract WHERE company_id=?")
						.addParam(companyId)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonContract.class));
	}
	
	public Multi<PersonContract> findAllByContractId(String contractId) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_contract WHERE contract_id=?")
						.addParam(contractId)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonContract.class));
	}
	
	public PersonContract findByCompanyIdAndPersonRefAndContractId(String companyId, String personRef, String contractId) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM person_contract WHERE company_id=? AND person_ref=? AND contract_id=?")
						.addParam(companyId)
						.addParam(personRef)
						.addParam(contractId)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(PersonContract.class);
		} else {
			return null;
		}
	}
	
	public Multi<PersonContract> findAllByCompanyIdAndPersonRef(String companyId, String personRef) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_contract WHERE person_ref=? AND company_id=?")
						.addParam(personRef)
						.addParam(companyId)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonContract.class));
	}
	
	public Multi<PersonContract> findAllByPersonRef(String personRef) {
		Multi<DbRow> rows = this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM person_contract WHERE person_ref=?")
						.addParam(personRef)
						.execute()
						);
		
		return rows.map(dbRow -> dbRow.as(PersonContract.class));
	}
	
	public Single<Long> deleteByContractId(String contractId) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_contract WHERE contract_id = ?")
				.addParam(contractId)
				.execute()
				);
	}
	
	public Single<Long> deleteByPersonRef(String personRef) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_contract WHERE person_ref = ?")
				.addParam(personRef)
				.execute()
				);
	}
	
	public Single<Long> deleteByPersonRefAndCompanyId(String personRef, String companyId) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_contract WHERE person_ref = ? AND company_id = ?")
				.addParam(personRef)
				.addParam(companyId)
				.execute()
				);
	}
	
	public Single<Long> deleteByPersonRefAndCompanyIdAndContractId(String personRef, String companyId, String contractId) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_contract WHERE person_ref = ? AND company_id = ? AND contract_id = ?")
				.addParam(personRef)
				.addParam(companyId)
				.addParam(contractId)
				.execute()
				);
	}

	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM person_contract WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
}
