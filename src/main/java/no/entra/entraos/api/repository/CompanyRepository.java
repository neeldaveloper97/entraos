package no.entra.entraos.api.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.util.StringUtils;

public class CompanyRepository {

	private DbClient dbClient;

	public CompanyRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<Company> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM company").execute())
				.map(dbRow -> dbRow.as(Company.class));
	}

	public Multi<Company> findAllByIds(List<String> ids) {
		return  this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM company WHERE id IN (" + String.join(", ", Collections.nCopies(ids.size(), "?")) + ")") 
						.params(ids)
						.execute()
						).map(dbRow -> dbRow.as(Company.class));
	}
	
	public Multi<Company> findAllByNames(List<String> names) {
		return  this.dbClient
				.execute(exec -> exec
						.createQuery("SELECT * FROM company WHERE name IN (" + String.join(", ", Collections.nCopies(names.size(), "?")) + ")") 
						.params(names)
						.execute()
						).map(dbRow -> dbRow.as(Company.class));
	}

	public Single<String> save(Company ca) {
		if(ca!=null) {
			final String id = StringUtils.ensureId(ca.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO company(id, name, org_number) VALUES (?, ?, ?)",
									id, 
									ca.getName(),
									ca.getOrg_number()
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for company_admin"));
		}
	}

	public Single<Company> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM company WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(Company.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	public Company findById(String id) {
		
		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM company WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(Company.class);
		} else {
			return null;
		}
	}
	
	public Company findByCompanyName(String company) {
		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM company WHERE name=?")
						.addParam(company)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(Company.class);
		} else {
			return null;
		}
	}

	public Single<Long> update(String id, Company ca) {
		if(ca!=null) {
			ca.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE company SET name=:name, org_number=:orgnumber WHERE id=:id")
					.namedParam(ca)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for company_admin"));
		}
	}
	
	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM company WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
}
