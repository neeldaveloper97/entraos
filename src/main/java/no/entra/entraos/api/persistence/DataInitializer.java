package no.entra.entraos.api.persistence;

import io.helidon.dbclient.DbClient;
import no.cantara.config.ApplicationProperties;
import no.entra.entraos.api.Main;
import no.entra.entraos.api.MainProperties;
import no.entra.entraos.api.model.*;
import no.entra.entraos.api.repository.*;
import no.entra.entraos.domain.person.Person;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DataInitializer {

	private static final Logger LOGGER = Logger.getLogger(DataInitializer.class.getName());

	public static void init(DbClient dbClient) {

		LOGGER.info("Data initialization is starting...");
		dbClient
		.inTransaction(tx -> 
		tx.createGet("SELECT (CASE WHEN EXISTS(SELECT NULL FROM contract_category) THEN 0 ELSE 1 END) AS IsEmpty;").execute()            
		.flatMap(v -> {

			if(v.get().column("IsEmpty").as(Integer.class) == 1) {
				LOGGER.info("Inserting contract_category...");
				return tx.createInsert("INSERT INTO contract_category(id, name, description) VALUES(?, ?, ?), (?, ?, ?), (?, ?, ?)")
						.params(Arrays.asList(UUID.randomUUID().toString(), "Food And Beverage", "FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT", 
								UUID.randomUUID().toString(), "Tenant", "FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT",
								UUID.randomUUID().toString(), "Others", "FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT"
								))
						.execute();
			} else {
				return null;
			}
		}).log()
		.flatMap(v -> tx.createGet("SELECT (CASE WHEN EXISTS(SELECT NULL FROM contract_type) THEN 0 ELSE 1 END) AS IsEmpty;").execute()).log()
		.flatMap(v -> {
			if(v.get().column("IsEmpty").as(Integer.class) == 1) {
				
				
				LOGGER.info("Inserting contract_type...");
				
				List<String> list = Arrays.asList(no.entra.entraos.domain.person.ContractType.values()).stream().map(e ->  {
					List<String> a = new ArrayList<String>();
					a.add(UUID.randomUUID().toString());
					a.add(e.toString());
					a.add("FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT");
					return a;
				}).flatMap(x -> x.stream()).collect(Collectors.toList());
				
				
				
				return tx
						.createInsert("INSERT INTO contract_type(id, name, description) VALUES " + String.join(", ", Collections.nCopies(list.size()/3, "(?, ?, ?)")))
						.params(list)
						.execute();
				
			} else {
				
				return null;
			}
		}).log()
		
		.flatMap(v -> tx.createGet("SELECT (CASE WHEN EXISTS(SELECT NULL FROM contract_property) THEN 0 ELSE 1 END) AS IsEmpty;").execute()).log()
		.flatMap(v -> {
			if(v.get().column("IsEmpty").as(Integer.class) == 1) {
				LOGGER.info("Inserting contract_property...");
				return tx
						.createInsert("INSERT INTO contract_property(id, name, value, description) VALUES (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?)")
						.params(Arrays.asList(
								UUID.randomUUID().toString(), "sponsorMealMaxValue", "0", "FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT", 
								UUID.randomUUID().toString(), "rebelDiscountInPercent", "0","FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT",
								UUID.randomUUID().toString(), "fursetDiscountInPercent", "0","FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT",
								UUID.randomUUID().toString(), "contractClassName", "","FOR COMPATIBILITY WITH CURRENT CONTRACTS' PROPERTIES IN PERSON REGSITER API, DO NOT EDIT",
								UUID.randomUUID().toString(), "productGroupsCoveredInContract", "Lunch","Type supported: Lunch,Dinner,FoodForMeetings,FoodForMeetups,Snacks",
								UUID.randomUUID().toString(), "serviceProvider", "Munu", "Food service provider",
								UUID.randomUUID().toString(), "Munu_SponsoredMeetupContractId", "1","",
								UUID.randomUUID().toString(), "tenantLocations", "","FOR COMPATIBILITY WITH CURRENT CONTRACTS IN PERSON REGSITER API, DO NOT EDIT",
								UUID.randomUUID().toString(), "recLocationsAndSensorTypes", "","FOR COMPATIBILITY WITH CURRENT CONTRACTS IN PERSON REGSITER API, DO NOT EDIT"
								
								
								
								
								))
						.execute();
			} else {
				return null;
			}
		}).log()
				)
		.subscribe(
				data -> LOGGER.log(Level.INFO, "=======>data:{0}", data),
				error -> LOGGER.warning("error: " + error),
				() -> {
					LOGGER.info("Data Initialization is done.");
					if(ApplicationProperties.getInstance().get(MainProperties.APP_USEDUMMYDATA).equalsIgnoreCase("true")) {
						createDummyTemplates(dbClient);
						createDummyCompanies(dbClient);
						createDummyContracts(dbClient);
					}
				} 
				);

		

	}
	
	private static void createDummyCompanies(DbClient dbClient) {
		CompanyRepository comRepo = new CompanyRepository(dbClient);
		List<Person> persons;
		Set<String> companies = new HashSet<String>();
		
		try {
			persons = Main.whydahservice.findAllPersons();
			persons.stream().forEach(item -> {
				item.getContractRoles().forEach(contract -> {
					if(contract.getCompanyName()!=null) {
						if(!companies.contains(contract.getCompanyName())) {
						Company com = new Company();
						com.setId(UUID.randomUUID().toString());
						com.setName(contract.getCompanyName());
						if(contract.getOrgNo()!=null) {
							com.setOrg_number(contract.getOrgNo());
						} else {
							com.setOrg_number("N/A");
						}
						companies.add(contract.getCompanyName());
						comRepo.save(com).toStage();
						}
					}
				});
			});
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		Company c = new Company();
		if(!companies.contains("areo.io")) {
			c = new Company();
			c.setId(UUID.randomUUID().toString());
			c.setName("areo.io");
			c.setOrg_number("99999999");
			comRepo.save(c).toStage();
		}
		if(!companies.contains("devhouse.no")) {
			c = new Company();
			c.setId(UUID.randomUUID().toString());
			c.setName("devhouse.no");
			c.setOrg_number("88888888");
			comRepo.save(c).toStage();
		}
		if(!companies.contains("Sunstone Tech AS")) {
			c = new Company();
			c.setId(UUID.randomUUID().toString());
			c.setName("Sunstone Tech AS");
			c.setOrg_number("925148598");
			comRepo.save(c).toStage();
		}
//		if(!companies.contains("repel")) {
//			c = new Company();
//			c.setId(UUID.randomUUID().toString());
//			c.setName("repel");
//			c.setOrg_number("77777777");
//			comRepo.save(c).toStage();
//		}
		
		
		companies.add("areo.io");
		companies.add("devhouse.no");
		companies.add("Sunstone Tech AS");
		
		LOGGER.info("Inserted companies: " +  String.join(",", companies));
	}
	
	private static void createDummyContracts(DbClient dbClient) {
		CompanyRepository comRepo = new CompanyRepository(dbClient);
		ContractRepository contractRepo = new ContractRepository(dbClient);
		TemplateRepository templateRepo = new TemplateRepository(dbClient);	
		
		ModelMapper mapper = new ModelMapper();
		try {
			List<Template> templates = templateRepo.all().collectList().await();
			for(Template template: templates) {
				//create from the template
				
				CompanyContract contract = mapper.map(template, CompanyContract.class);
				contract.setValid_from(LocalDateTime.now());
				contract.setValid_to(LocalDateTime.now().plusYears(2));
				contract.setStatus(ContractStatus.Activated);
				contract.setCompany_id(comRepo.findByCompanyName("devhouse.no").getId());
				contract.setContract_name("Dummy contract from template " + template.getContract_name());
				contract.setId(UUID.randomUUID().toString());
				String id = contractRepo.save(contract).get();
				
				
				contract = mapper.map(template, CompanyContract.class);
				contract.setValid_from(LocalDateTime.now());
				contract.setValid_to(LocalDateTime.now().plusYears(2));
				contract.setStatus(ContractStatus.Activated);
				contract.setCompany_id(comRepo.findByCompanyName("areo.io").getId());
				contract.setContract_name("Dummy contract from template " + template.getContract_name());
				contract.setId(UUID.randomUUID().toString());
				id = contractRepo.save(contract).get();
			}
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static void createDummyTemplates(DbClient dbClient) {

		try {
			ContractCategoryRepository catRepo = new ContractCategoryRepository(dbClient);
			ContractTypeRepository typeRepo = new ContractTypeRepository(dbClient);
			TemplateRepository templateRepo = new TemplateRepository(dbClient);			
			ContractPropertyRepository cpRepo = new ContractPropertyRepository(dbClient); 
			
			List<ContractCategory> cat = catRepo.all().collectList().get();
			List<ContractType> ty = typeRepo.all().collectList().get();
			List<ContractProperty> cp = cpRepo.all().collectList().get();	
			
			Template t = new Template();
			t.setContract_name("General Contract / Fast_leietaker");
			t.setContract_category_id(cat.get(2).getId()); //Others
			t.setContract_type_id(ty.get(0).getId()); //Fast_leietaker
			t.setDescription("");
			
			ContractProperty p = cp.stream().filter(i->i.getName().equals("fursetDiscountInPercent")).findFirst().get();
			p.setValue("40");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("rebelDiscountInPercent")).findFirst().get();
			p.setValue("20");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("tenantLocations")).findFirst().get();
			p.setValue("");
			t.getContract_properties().add(p);
			
			templateRepo.save(t).toStage();
			
			t = new Template();
			t.setContract_name("Tenant Contract / Fast_leietaker");
			t.setContract_category_id(cat.get(1).getId());
			t.setContract_type_id(ty.get(0).getId()); //Fast_leietaker
			t.setDescription("");
			p = cp.stream().filter(i->i.getName().equals("contractClassName")).findFirst().get();
			p.setValue("no.entra.entraos.domain.person.contracts.tenant.TenantContract");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("rebelDiscountInPercent")).findFirst().get();
			p.setValue("20");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("tenantLocations")).findFirst().get();
			p.setValue("location1, location2");
			t.getContract_properties().add(p);
			
			templateRepo.save(t).toStage();

			t = new Template();
			t.setContract_name("Sponsored Lunch Contract / Fast_leietaker");
			t.setContract_category_id(cat.get(0).getId()); //food and beverage
			t.setContract_type_id(ty.get(0).getId()); //Fast_leietaker
			t.setDescription("");
			
			p = cp.stream().filter(i->i.getName().equals("fursetDiscountInPercent")).findFirst().get();
			p.setValue("20");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("contractClassName")).findFirst().get();
			p.setValue("no.entra.entraos.domain.person.contracts.foodandbeverage.SponsoredLunchContract");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("sponsorMealMaxValue")).findFirst().get();
			p.setValue("60");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("productGroupsCoveredInContract")).findFirst().get();
			p.setValue("Lunch");
			t.getContract_properties().add(p);
			templateRepo.save(t).toStage();
			
			t = new Template();
			t.setContract_name("Free Lunch Contract / Fast_leietaker");
			t.setContract_category_id(cat.get(0).getId()); //food and beverage
			t.setContract_type_id(ty.get(0).getId()); //Fast_leietaker
			t.setDescription("");
			
			p = cp.stream().filter(i->i.getName().equals("fursetDiscountInPercent")).findFirst().get();
			p.setValue("100");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("contractClassName")).findFirst().get();
			p.setValue("no.entra.entraos.domain.person.contracts.foodandbeverage.FreeLunchContract");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("productGroupsCoveredInContract")).findFirst().get();
			p.setValue("Lunch");
			t.getContract_properties().add(p);
			templateRepo.save(t).toStage();
			
			t = new Template();
			t.setContract_name("Food and Beverage Contract / Fast_leietaker");
			t.setContract_category_id(cat.get(0).getId()); //food and beverage
			t.setContract_type_id(ty.get(0).getId()); //Fast_leietaker
			t.setDescription("");
			
			p = cp.stream().filter(i->i.getName().equals("contractClassName")).findFirst().get();
			p.setValue("no.entra.entraos.domain.person.contracts.foodandbeverage.FoodAndBeverageContract");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("serviceProvider")).findFirst().get();
			p.setValue("Munu");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("Munu_SponsoredMeetupContractId")).findFirst().get();
			p.setValue("1");
			t.getContract_properties().add(p);
			p = cp.stream().filter(i->i.getName().equals("productGroupsCoveredInContract")).findFirst().get();
			p.setValue("");
			t.getContract_properties().add(p);
			templateRepo.save(t).toStage();
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private DataInitializer() {
	}

}
