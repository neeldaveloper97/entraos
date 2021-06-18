package no.entra.entraos.api.repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractStatus;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;

public class ContractRepository {

	private DbClient dbClient;

	public ContractRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<CompanyContract> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM contract").execute())
				.map(dbRow -> dbRow.as(CompanyContract.class));
	}


	public Single<String> save(CompanyContract contract) {
		if(contract!=null) {
			final String id = StringUtils.ensureId(contract.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO contract(id, contract_name, company_id, contract_category_id, contract_type_id, description, contract_properties, valid_from, valid_to, status, quantity, docs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
									id, 
									contract.getContract_name(), 
									contract.getCompany_id(),
									contract.getContract_category_id(),
									contract.getContract_type_id(),
									contract.getDescription(),
									EntityUtils.object_mapToJsonString(contract.getContract_properties()),
									contract.getValid_from(),
									contract.getValid_to(),
									contract.getStatus().toString(),
									contract.getQuantity(),
									EntityUtils.object_mapToJsonString(contract.getDocs())
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract"));
		}
	}

	public Single<CompanyContract> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(CompanyContract.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	public Multi<CompanyContract> findAllByCompanyId(String companyId) {
		return this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM contract WHERE company_id = ?")
						.addParam(companyId)
						.execute()
						)
				.map(dbRow -> dbRow.as(CompanyContract.class));			
	}

	public Single<Long> update(String id, CompanyContract contract) {
		if(contract!=null) {
			contract.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE contract SET contract_name=:contractname, company_id=:companyid, contract_category_id=:contractcategoryid, contract_type_id=:contracttypeid, description=:description, contract_properties=:contractproperties, valid_from=:validfrom, valid_to=:validto, status=:status, last_updated=:lastupdated, quantity=:quantity, docs=:docs WHERE id=:id")
					.namedParam(contract)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract"));
		}
	}
	
	public Single<Long>  updateStatus(String contractId, ContractStatus status){		
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE contract SET status=:status, last_updated=:last_updated WHERE id=:id")
					.addParam("status", status)
					.addParam("last_updated", LocalDateTime.now())
					.addParam("id", contractId)
					.execute());
		
	}

	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM contract WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
	
	public CompanyContract findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(CompanyContract.class);
		} else {
			return null;
		}
	}
	
	
	public Multi<CompanyContract> findAllByIds(List<String> ids) {
		return  this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM contract WHERE id IN (" + String.join(", ", Collections.nCopies(ids.size(), "?")) + ")") 
						.params(ids)
						.execute()
						).map(dbRow -> dbRow.as(CompanyContract.class));
	}

}
