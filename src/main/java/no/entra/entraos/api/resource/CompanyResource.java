package no.entra.entraos.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import no.entra.entraos.api.exception.AppExceptionCode;
import no.entra.entraos.api.model.Company;
import no.entra.entraos.api.model.Template;
import no.entra.entraos.api.repository.CompanyRepository;
import no.entra.entraos.api.repository.TemplateRepository;
import no.entra.entraos.api.security.SecurityFilter;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.api.whydah.WhydahService;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.Person;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class CompanyResource implements Service {

	public static final Logger logger = LoggerFactory.getLogger(CompanyResource.class);

	private final CompanyRepository domain_repo;
	private final WhydahService whydahService;

	public CompanyResource(CompanyRepository domain_repo,  WhydahService whydahService) {
		this.domain_repo = domain_repo;
		this.whydahService = whydahService;
	}

	@Override
	public void update(Rules rules) {
		rules
		.get("/api/company", this::getCompanyList) //has admin
		.get("/api/company/{id}", this::getCompanyById) //admin and specific companies
		.post("/api/company", this::createCompany) //has admin
		.delete("/api/company/{id}", this::deleteCompanyById) //admin
		.put("/api/company/{id}", this::updateCompany) //admin

		//TODO: make doc for this endpoint
		.get("/api/company/find_by_ids/{ids}", this::getCompanyListByIds) //admin
		.get("/api/company/search/{company_id}/{keyword}", this::searchPerson) //open
		;



	}

	private void searchPerson(ServerRequest serverRequest, ServerResponse serverResponse) {
		String companyId = serverRequest.path().param("company_id");
		String keyword = serverRequest.path().param("keyword");
		logger.info("searchPerson called, company_id {} keyword {}", companyId, keyword);


		//filter company


		try {
			Company com = domain_repo.findById(companyId);
			if(com==null) {
				throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Company not found");
			} 
			//for the sake of security, we only reveal contracts of this company
			List<Person> personList = whydahService.findPerson(keyword);
			personList = personList.stream().map(p -> {
				p.getContractRoles().removeIf(i -> 
				!com.getName().equalsIgnoreCase(i.getCompanyName())
						);
				//TODO:How about p.getActiveContractRole()???
				//p.setActiveContractRole(null) will throw a NPE
				return p;
			}).collect(Collectors.toList());

			serverResponse.status(200).send(EntityUtils.objectList_toJsonArray(personList));
		}catch (Exception ex) {
			logger.error("searchPerson failed with exception", ex);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to find person - ", ex.getMessage()));
		}


	}

	private void getCompanyById(ServerRequest serverRequest, ServerResponse serverResponse) {

		String id = serverRequest.path().param("id");
		logger.info("getCompanyById called, id {}", id);
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				if(!SecurityFilter.getUserRole(serverRequest).hasValidAccess(id)) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			this.domain_repo.getOne(id)
			.subscribe(template -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(template)),
					throwable -> {
						logger.error("getCompanyById failed", throwable);
						serverRequest.next(throwable);
					});

		}catch (Exception e) {
			e.printStackTrace();
			logger.error("getCompanyById failed with exception", e);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("getCompanyById failed - ", e.getMessage()));
		}
	}

	private void updateCompany(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			String id = serverRequest.path().param("id");
			logger.info("updateCompany called, id {}", id);

			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(Company.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(com -> {
				if(com==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				this.domain_repo.update(id, com).subscribe(
						p -> serverResponse.status(200).send(EntityUtils.object_toJsonObject(com)),
						throwable -> {
							logger.error("updateCompany failed", throwable);
							serverRequest.next(throwable);
						}
						);

			}, throwable ->{
				logger.error("updateCompany failed", throwable);
				serverRequest.next(throwable);
			}
					);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("updateCompany failed with exception", e);
			serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to find person - ", e.getMessage()));
		}
	}

	private void createCompany(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("createCompany called");
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}

			serverRequest.content().as(JsonObject.class)
			.map(jsonObj -> {

				try {
					return EntityUtils.jsonObject_toObject(Company.class, jsonObj);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;

			}).subscribe(com -> {
				if(com==null) {
					throw AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Cannot parse the payload");
				}

				Company found = this.domain_repo.findByCompanyName(com.getName());
				if(found!=null) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Company name " + com.getName() + " is already existing");
				}


				this.domain_repo.save(com).subscribe(
						p -> {
							if (p != null) {
								com.setId(p);
								serverResponse.status(201)
								.headers()
								.location(URI.create("/api/company/" + p));
								serverResponse.send(EntityUtils.object_toJsonObject(com));
							} else {
								logger.error("createCompany failed");
								serverRequest.next(AppExceptionCode.COMMON_INTERNALEXCEPTION_500_9999.addMessageParams("Failed to save to DB"));
							}
						},
						throwable -> {
							logger.error("createCompany failed", throwable);
							serverRequest.next(throwable);
						}
						);


			}
			, throwable -> {
				logger.error("createCompany failed", throwable);
				serverRequest.next(throwable);

			}	);

		} catch(Exception ex) {
			logger.error("createCompany failed with exception {}", ex);
			serverRequest.next(ex);
		}

	}

	private void deleteCompanyById(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			String id = serverRequest.path().param("id");
			logger.info("deleteCompanyById called, id {}", id);
			this.domain_repo.deleteById(id)
			.subscribe(
					count -> {
						logger.info("deleteCompanyById {} deleted.", count);
						serverResponse.status(204).send();
					},
					throwable -> {
						logger.error("deleteCompanyById failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch(Exception ex) {
			logger.error("deleteCompanyById failed with exception {}", ex);
			serverRequest.next(ex);
		}
	}

	private void getCompanyList(ServerRequest serverRequest, ServerResponse serverResponse) {
		logger.info("getCompanyList called");
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			this.domain_repo.all()
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getCompanyList failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch(Exception ex) {
			logger.error("getCompanyList failed with exception {}", ex);
			serverRequest.next(ex);
		}
	}

	private void getCompanyListByIds(ServerRequest serverRequest, ServerResponse serverResponse) {
		try {
			if(SecurityFilter.isRouteAuthenticated(serverRequest)) {
				//for admin only
				if(!SecurityFilter.getUserRole(serverRequest).hasSuperAdminRole()) {
					throw AppExceptionCode.COMMON_BADREQUESTEXCEPTION_400_9998.addMessageParams("Access denied");
				} 
			}
			String[] ids = serverRequest.path().param("ids").split(",");
			logger.info("getCompanyList called, ids {}", serverRequest.path().param("ids"));
			this.domain_repo.findAllByIds(Arrays.asList(ids))
			.collectList()
			.map(EntityUtils::objectList_toJsonArray)
			.subscribe(
					serverResponse::send,
					throwable -> {
						logger.error("getCompanyListByIds failed", throwable);
						serverRequest.next(throwable);
					}
					);
		} catch(Exception ex) {
			logger.error("getCompanyListByIds failed with exception {}", ex);
			serverRequest.next(ex);
		}
	}
}
