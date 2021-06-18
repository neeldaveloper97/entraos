package no.entra.entraos.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Company implements Serializable {
	private String id;
	private String name;
	private String org_number;
}
