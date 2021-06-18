package no.entra.entraos.api.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import no.entra.entraos.domain.person.Person;

@Data
public class PersonContract {
	private String id;
	private String person_ref="";
	private String contract_id="";
	private String company_id="";
	private LocalDateTime created_at;
}
