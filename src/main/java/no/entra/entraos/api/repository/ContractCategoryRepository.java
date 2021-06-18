package no.entra.entraos.api.repository;

import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.ContractCategory;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.util.StringUtils;

public class ContractCategoryRepository {

	private DbClient dbClient;

	public ContractCategoryRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<ContractCategory> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM contract_category").execute())
				.map(dbRow -> dbRow.as(ContractCategory.class));
	}
	
	public Single<Long> deleteById(String id) {
		return this.dbClient.
				execute(exc -> exc.createDelete("DELETE FROM contract_category WHERE id=:id")
						.addParam("id", id)
						.execute());
	}
	
	public Single<String> save(ContractCategory cc){
		if(cc!=null) {
			final String id = StringUtils.ensureId(cc.getId());	
			return this.dbClient
			.execute(exec -> exec.insert("INSERT INTO contract_category(id, name, description) VALUES (?, ?, ?)",
					id, 
					cc.getName(),
					cc.getDescription()
					)).map(i -> i>0?id:null);

		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract category"));
		}
	}
	
	
	
	public Single<Long> update(String id, ContractCategory cc) {
		if(cc!=null) {
			cc.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE contract_category SET name=:name, description=:description WHERE id=:id")
					.namedParam(cc)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for contract category"));
		}
	}
	
	public ContractCategory findById(String id) {
		
		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_category WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractCategory.class);
		} else {
			return null;
		}
	}
	
	public ContractCategory findByName(String name) {
		
		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_category WHERE name=?")
						.addParam(name)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(ContractCategory.class);
		} else {
			return null;
		}
	}

	public Single<ContractCategory> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM contract_category WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(ContractCategory.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	
	
}
