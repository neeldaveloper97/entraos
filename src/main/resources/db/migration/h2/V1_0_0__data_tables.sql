CREATE TABLE IF NOT EXISTS company(
	id VARCHAR(64) NOT NULL,
	name VARCHAR(255) NOT NULL,
	org_number VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS contract_category(
	id VARCHAR(64) NOT NULL,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(10000),
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS contract_type(
	id VARCHAR(64) NOT NULL,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(10000),
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS contract_property(
	id VARCHAR(64) NOT NULL,
	name VARCHAR(255) NOT NULL,
	value VARCHAR(255) NOT NULL,
	description VARCHAR(10000),
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_role(
	id VARCHAR(64) NOT NULL,
	username VARCHAR(255) NOT NULL,
	roles VARCHAR(255) NOT NULL,
	company_ids VARCHAR(1000) NOT NULL,
	last_synced TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	PRIMARY KEY (id)
);
	
CREATE TABLE IF NOT EXISTS person_company(
	id VARCHAR(64) NOT NULL,
	company_id VARCHAR(64) NOT NULL,
	person_ref VARCHAR(64) NOT NULL,
	person VARCHAR(10000) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	PRIMARY KEY (id),
	FOREIGN KEY (company_id) REFERENCES company(id)
);


CREATE TABLE IF NOT EXISTS template(
	id VARCHAR(64) not null,
	contract_name VARCHAR(512) NOT NULL,
	description VARCHAR(10000),
	contract_category_id VARCHAR(64),
	contract_type_id VARCHAR(64),
	contract_properties VARCHAR(10000),
	last_updated TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	PRIMARY KEY (id),
	FOREIGN KEY (contract_category_id) REFERENCES contract_category(id),
	FOREIGN KEY (contract_type_id) REFERENCES contract_type(id)
);

CREATE TABLE IF NOT EXISTS contract(
	id VARCHAR(64) not null,
	company_id VARCHAR(64) not null,
	contract_name VARCHAR(512) NOT NULL,
	description VARCHAR(10000),
	contract_category_id VARCHAR(64),
	contract_type_id VARCHAR(64),
	contract_properties VARCHAR(10000),
	status VARCHAR(64) not null,
	last_updated TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	valid_from TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	valid_to TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	quantity INT DEFAULT 1,
	docs VARCHAR(10000),
	PRIMARY KEY (id),
	FOREIGN KEY (company_id) REFERENCES company(id),
	FOREIGN KEY (contract_category_id) REFERENCES contract_category(id),
	FOREIGN KEY (contract_type_id) REFERENCES contract_type(id)
);

CREATE TABLE IF NOT EXISTS person_contract(
	id VARCHAR(64) NOT NULL,
	company_id VARCHAR(64) NOT NULL,
	person_ref VARCHAR(255) NOT NULL,
	contract_id VARCHAR(64) NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT LOCALTIMESTAMP,
	PRIMARY KEY (id),
	FOREIGN KEY (company_id) REFERENCES company(id),
	FOREIGN KEY (contract_id) REFERENCES contract(id),
);


