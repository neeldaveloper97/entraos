package no.entra.entraos.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import no.entra.entraos.domain.person.Person;

@Data
public class PersonCompany implements Serializable {
	private String id;
	private String company_id;
	private String person_ref;
	private Person person;
	LocalDateTime created_at;
}
