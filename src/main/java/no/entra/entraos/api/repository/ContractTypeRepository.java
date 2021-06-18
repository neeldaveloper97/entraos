package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.ContractType;
import no.entra.entraos.api.util.StringUtils;

public class ContractTypeRepository {

	private DbClient dbClient;

	public ContractTypeRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<ContractType> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM contract_type").execute())
				.map(dbRow -> dbRow.as(ContractType.class));
	}
	
	public Single<Long> deleteById(String id) {
		return this.dbClient.
				execute(exc -> exc.createDelete("DELETE FROM contract_type WHERE id=:id")
						.addParam("id", id)
						.execute());
	}
	
	public Single<String> save(ContractType ct){
		if(ct!=null) {
			final String id = StringUtils.ensureId(ct.getId());

			return this.dbClient
			.execute(exec -> exec.insert("INSERT INTO contract_type(id, name, description) VALUES (?, ?, ?)",
					id, 
					ct.getName(), 
					ct.getDescription()
					)).map(i -> i>0?id:null);

			
		}  else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract type"));
		}
	}
	
	
	public Single<Long> update(String id, ContractType ct) {
		if(ct!=null) {
			ct.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE contract_type SET name=:name, description=:description WHERE id=:id")
					.namedParam(ct)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract type"));
		}
	}
	
	
	
	public ContractType findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_type WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractType.class);
		} else {
			return null;
		}
	}
	
	public ContractType findByName(String name) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_type WHERE name=?")
						.addParam(name)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractType.class);
		} else {
			return null;
		}
	}
	
	public Single<ContractType> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_type WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(ContractType.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
}
