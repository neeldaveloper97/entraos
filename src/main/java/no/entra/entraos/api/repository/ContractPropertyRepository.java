package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractCategory;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.util.StringUtils;

public class ContractPropertyRepository {

	private DbClient dbClient;

	public ContractPropertyRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<ContractProperty> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM contract_property").execute())
				.map(dbRow -> dbRow.as(ContractProperty.class));
	}
	
	public Single<Long> deleteById(String id) {
		return this.dbClient.
				execute(exc -> exc.createDelete("DELETE FROM contract_property WHERE id=:id")
						.addParam("id", id)
						.execute());
	}
	
	public Single<String> save(ContractProperty cp){
		if(cp!=null) {
			final String id = StringUtils.ensureId(cp.getId());	
			return this.dbClient
			.execute(exec -> exec.insert("INSERT INTO contract_property(id, name, value, description) VALUES (?, ?, ?, ?)",
					id, 
					cp.getName(),
					cp.getValue(),
					cp.getDescription()
					)).map(i -> i>0?id:null);

		
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract property"));
		}
	}
	
	public Single<ContractProperty> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_property WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(ContractProperty.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	public ContractProperty findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_property WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractProperty.class);
		} else {
			return null;
		}
	}
	
	public ContractProperty findByName(String name) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_property WHERE name=?")
						.addParam(name)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractProperty.class);
		} else {
			return null;
		}
	}
	
	
	public Single<Long> update(String id, ContractProperty cp) {
		if(cp!=null) {
			cp.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE contract_property SET name=:name, value=:value, description=:description WHERE id=:id")
					.namedParam(cp)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract property"));
		}
	}
	
	

	
}
