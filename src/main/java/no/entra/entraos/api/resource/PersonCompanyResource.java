package no.entra.entraos.api.resource;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.CompanyContract;
import no.entra.entraos.api.model.ContractCategory;
import no.entra.entraos.api.model.ContractProperty;
import no.entra.entraos.api.model.ContractStatus;
import no.entra.entraos.api.model.ContractType;
import no.entra.entraos.api.model.PersonCompany;
import no.entra.entraos.api.model.PersonContract;
import no.entra.entraos.api.repository.CompanyRepository;
import no.entra.entraos.api.repository.ContractCategoryRepository;
import no.entra.entraos.api.repository.ContractPropertyRepository;
import no.entra.entraos.api.repository.ContractRepository;
import no.entra.entraos.api.repository.ContractTypeRepository;
import no.entra.entraos.api.repository.PersonCompanyRepository;
import no.entra.entraos.api.repository.PersonContractRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.whydah.WhydahService;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.Person;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FoodAndBeverageContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FreeLunchContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.SponsoredLunchContract;
import no.entra.entraos.domain.person.contracts.tenant.TenantContract;

public class PersonCompanyResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(PersonCompanyResource.class);

	private final PersonCompanyRepository person_company_repo;
	private final CompanyRepository company_repo;
	private final WhydahService whydahService;
	private final ContractRepository contract_repo;
	private final ContractCategoryRepository ccategory_repo;
	private final ContractPropertyRepository cproperty_repo;
	private final ContractTypeRepository ctype_repo;
	private final PersonContractRepository pcontract_repo;


	public PersonCompanyResource(PersonCompanyRepository person_company_repo, CompanyRepository company_repo, ContractRepository contract_repo, WhydahService whydahService, ContractCategoryRepository ccategory_repo, ContractPropertyRepository cproperty_repo, ContractTypeRepository ctype_repo, PersonContractRepository pcontract_repo) {
		this.person_company_repo = person_company_repo;
		this.company_repo = company_repo;
		this.whydahService = whydahService;
		this.contract_repo = contract_repo;
		this.ccategory_repo = ccategory_repo;
		this.cproperty_repo = cproperty_repo;
		this.ctype_repo = ctype_repo;
		this.pcontract_repo = pcontract_repo;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/person_company", this::getPersonCompanyList) //admin
		.get("/api/person_company/{company_id}", this::getPersonCompanyListByCompanyId) //admin and specific companies
		.get("/api/person_company/{company_id}/{person_ref}", this::getPersonCompanyByCompanyIdAndPersonRef) //admin an specific companies
		//person ref
		.post("/api/person_company/{company_id}/{person_ref}", this::createPersonCompany) //admin and specific companies
		.delete("/api/person_company/{company_id}/{person_ref}", this::deletePersonCompany)
		;

	}
	
	private void deletePersonCompany(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
		String companyId= serverRequest.path().param("company_id");
		String person_ref = serverRequest.path().param("person_ref");
		if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
			if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
			} 
		}

		logger.info("deletePersonCompany called, company_id {} person_ref {}", companyId, person_ref);
		Company com = company_repo.findById(companyId);
		if(com==null) {
			throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Company not found");
		} else {
			Person p = whydahService.getPerson(person_ref);
			//delete all contracts associated to this company 
			p.getContractRoles().removeIf(i -> !com.getName().equalsIgnoreCase(i.getCompanyName()));
			p.getContractRoles().stream().forEach(contract -> {
				if(whydahService.removeContract(person_ref, contract.getContractId().toString())) {
					long count = pcontract_repo.deleteByPersonRefAndCompanyIdAndContractId(person_ref, com.getId(), contract.getContractId().toString()).await();
					logger.info("deletePersonCompany deleteByPersonRefAndCompanyIdAndContractId {} deleted.", count);
				} else {
					logger.error("deletePersonCompany failed with an exception from EntraSSO person register API, company_id {}, person_ref {}, contract_id {}", com.getId(), person_ref, contract.getContractId().toString());
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonCompany failed with an exception from EntraSSO person register API");
				}
			});
			
			//delete all contracts assigned for this person
			person_company_repo.deleteByPersonRefAndCompanyId(person_ref, companyId)
				.subscribe(j -> {
					logger.info("deletePersonCompany deleteByPersonRefAndCompanyId {} deleted.", j);
					serverResponse.status(204).send();
				}, throwable -> {
				logger.error("deletePersonCompany failed", throwable);
				serverRequest.next(throwable);
			});
		} } catch (Exception ex) {
			ex.printStackTrace();
			logger.error("deletePersonCompany failed with exception", ex);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("deletePersonCompany failed with exception " + ex.getMessage()));
			
		}
	}

	private void createPersonCompany(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
		String companyId= serverRequest.path().param("company_id");
		String person_ref = serverRequest.path().param("person_ref");

		if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
			if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
			} 
		}

		logger.info("createPersonCompany called, compamy_id {} person_ref {}", companyId, person_ref);


		Company com = company_repo.findById(companyId);
		if(com==null) {
			throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Company not found");
		} else {
			
				PersonCompany person_company =  person_company_repo.findByCompanyIdAndPersonRef(com.getId(), person_ref);
				if(person_company!=null) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Person was already added");
				}
				
				//filter company, do not interfere with other companies' contracts
				Person p = whydahService.getPerson(person_ref);
				p.getContractRoles().removeIf(i -> 
				!com.getName().equalsIgnoreCase(i.getCompanyName())
						);
				
				//sync the current contracts to Contract Admin for this company
				syncContractFromPersonAPIToContractAdmin(com, p);

				PersonCompany pc = new PersonCompany();
				pc.setCompany_id(com.getId());
				pc.setCreated_at(LocalDateTime.now());
				pc.setId(UUID.randomUUID().toString());
				pc.setPerson(p);
				pc.setPerson_ref(p.getId().toString());

				person_company_repo.save(pc).subscribe(i -> {
					if (i != null) {
						pc.setId(i);
						serverResponse.status(201)
						.headers()
						.location(URI.create("/api/person_company/" + companyId + "/" + p.getId().toString()));
						serverResponse.send(EntityUtils.object_toJsonObject(pc));
					} else {
						serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
					}
				},
						throwable -> {
							logger.error("createPersonCompany failed", throwable);
							serverRequest.next(throwable);
						}
						);

			

		}} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("createPersonCompany failed with exception", ex);
			serverRequest.next( AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("createPersonCompany failed with exception " + ex.getMessage()));
			
		}


	}

	


	private void syncContractFromPersonAPIToContractAdmin(Company com, Person p) {
		Set<Contract> contractList = p.getContractRoles();
		contractList.stream().forEach(item -> {
			String contractId = item.getContractId().toString();
			CompanyContract cc = contract_repo.findById(contractId);
			boolean existing = cc!=null;
			if(!existing) {
				cc = new CompanyContract();
			}
			cc.setId(contractId);
			cc.setCompany_id(com.getId());
			cc.setContract_name(item.getContractName());

			ContractType type = ctype_repo.findByName(item.getContractType().toString());
			if(type!=null) {
				cc.setContract_type_id(type.getId());
			}


			cc.setCreated_at(LocalDateTime.now());
			cc.setDescription("");
			cc.setLast_updated(LocalDateTime.now());
			cc.setStatus(ContractStatus.Activated);
			cc.setValid_from(LocalDateTime.now());
			cc.setValid_to(LocalDateTime.now().plusYears(100));
			cc.setQuantity(1);

			List<ContractProperty> properties = new ArrayList<ContractProperty>();
			ContractProperty property = cproperty_repo.findByName("rebelDiscountInPercent");
			if(property!=null) {
				property.setValue(String.valueOf(item.getRebelDiscountInPercent()));
				properties.add(property);
			}

			property = cproperty_repo.findByName("fursetDiscountInPercent");
			if(property!=null) {
				property.setValue(String.valueOf(item.getFursetDiscountInPercent()));
				properties.add(property);
			}

			property = cproperty_repo.findByName("contractClassName");
			if(property!=null) {
				property.setValue(item.getContractClassName());
				properties.add(property);
			}

			if(item.getContractClassName().startsWith("no.entra.entraos.domain.person.contracts.foodandbeverage")) {
				ContractCategory cat = ccategory_repo.findByName("Food And Beverage");
				if(cat!=null) {
					cc.setContract_category_id(cat.getId());
					
					property = cproperty_repo.findByName("Munu_SponsoredMeetupContractId");
					property.setValue(((FoodAndBeverageContract) item).getMunuId());
					properties.add(property);
					
					property = cproperty_repo.findByName("productGroupsCoveredInContract");
					property.setValue(String.join(",", ((FoodAndBeverageContract) item).getProductGroupsCoveredInContract().stream().map(i -> i.toString()).collect(Collectors.toList())));
					properties.add(property);
					
					if(item instanceof SponsoredLunchContract) {
						property = cproperty_repo.findByName("sponsorMealMaxValue");
						property.setValue(String.valueOf(((SponsoredLunchContract) item).getSponsorMealMaxValue()));
						properties.add(property);
					}
					
				}
			} else if(item.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.tenant.TenantContract")) {
				ContractCategory cat = ccategory_repo.findByName("Tenant");
				if(cat!=null) {
					cc.setContract_category_id(cat.getId());
				}

				if(item instanceof TenantContract) {
					property = cproperty_repo.findByName("recLocationsAndSensorTypes");
					property.setValue(EntityUtils.object_mapToJsonString(((TenantContract) item).getRecLocationsAndSensorTypes()));
					properties.add(property);
				}


			} else if(item.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.DefaultContract")) {
				ContractCategory cat = ccategory_repo.findByName("Others");
				if(cat!=null) {
					cc.setContract_category_id(cat.getId());
				}


			} else {
				ContractCategory cat = ccategory_repo.findByName("Others");
				if(cat!=null) {
					cc.setContract_category_id(cat.getId());
				}
			}

			
			cc.setContract_properties(properties);

			if(!existing) {
				contract_repo.save(cc).await();
			} else {
				contract_repo.update(contractId, cc);
			}

			PersonContract found = pcontract_repo.findByCompanyIdAndPersonRefAndContractId(com.getId(), p.getId().toString(), contractId);
			if(found ==null) {
				//save to person_contract mapping 
				PersonContract pc = new PersonContract();
				pc.setCompany_id(com.getId());
				pc.setContract_id(contractId);
				pc.setPerson_ref(p.getId().toString());
				pcontract_repo.save(pc).await();
			}


		});




	}

	private void getPersonCompanyList(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			logger.info("getPersonCompanyList called");
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.person_company_repo.all()
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getPersonCompanyList failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("getPersonCompanyList failed with exception", ex);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonCompanyList failed with exception " + ex.getMessage()));

		}
	}

	private void getPersonCompanyListByCompanyId(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String id = serverRequest.path().param("company_id");
			logger.info("getPersonCompanyList called, company_id {}", id);
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(id)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.person_company_repo.findAllByCompanyId(id)
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getPersonCompanyListByCompanyId failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("getPersonCompanyListByCompanyId failed with exception", ex);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonCompanyListByCompanyId failed with exception " + ex.getMessage()));

		}
	}

	private void getPersonCompanyByCompanyIdAndPersonRef(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			String companyId = serverRequest.path().param("company_id");
			String personRef = serverRequest.path().param("person_ref");
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(companyId)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			PersonCompany pc = this.person_company_repo.findByCompanyIdAndPersonRef(companyId, personRef);
			serverResponse.status(200).send(EntityUtils.object_toJsonObject(pc));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("getPersonCompanyByCompanyIdAndPersonRef failed with exception", ex);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getPersonCompanyByCompanyIdAndPersonRef failed with exception " + ex.getMessage()));

		}
	}



}
