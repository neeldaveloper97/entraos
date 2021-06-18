package no.entra.entraos.api.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;
import io.netty.util.ResourceLeakDetector.Level;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.util.StringUtils;

public class TemplateRepository {

	private DbClient dbClient;

	public TemplateRepository(DbClient dbClient) {
		this.dbClient = dbClient;
	}

	public Multi<Template> all() {
		return this.dbClient
				.execute(exc -> exc.createQuery("SELECT * FROM template").execute())
				.map(dbRow -> dbRow.as(Template.class));
	}


	public Single<String> save(Template template) {
		if(template!=null) {
			final String id = StringUtils.ensureId(template.getId());
			return this.dbClient
					.execute(exec -> exec
							.insert("INSERT INTO template(id, contract_name, contract_category_id, contract_type_id, description, contract_properties) VALUES (?, ?, ?, ?, ?, ?)",
									id, 
									template.getContract_name(), 
									template.getContract_category_id(),
									template.getContract_type_id(),
									template.getDescription(),
									EntityUtils.object_mapToJsonString(template.getContract_properties())
									)).map(i -> i>0?id:null);
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for template"));
		}
	}

	public Single<Template> getOne(String id) {
		return this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM template WHERE id=?")
						.addParam(id)
						.execute()
						)
				.map(rowOptional -> rowOptional.map(dbRow -> dbRow.as(Template.class)).orElseThrow(() -> AppExceptionCode.COMMON_IDNOTFOUNDEXCEPTION_400_9999.addMessageParams(id))
						);
	}
	
	
	public Single<Long> update(String id, Template template) {
		if(template!=null) {
			template.setId(id);
			return this.dbClient.execute(exec -> exec.createUpdate("UPDATE template SET contract_name=:contractname , contract_category_id=:contractcategoryid, contract_type_id=:contracttypeid, description=:description, contract_properties=:contractproperties WHERE id=:id")
					.namedParam(template)
					.execute());
		} else {
			return Single.error(AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Cannot parse the request content for template"));
		}
	}

	public Single<Long> deleteById(String id) {
		return this.dbClient.execute(exec -> exec
				.createDelete("DELETE FROM template WHERE id = :id")
				.addParam("id", id)
				.execute()
				);
	}
	
	public Template findById(String id) {

		Optional<DbRow> row = this.dbClient
				.execute(exec -> exec
						.createGet("SELECT * FROM template WHERE id=?")
						.addParam(id)
						.execute()
						).await();
		if(row.isPresent()) {
			return row.get().as(Template.class);
		} else {
			return null;
		}
	}

}
