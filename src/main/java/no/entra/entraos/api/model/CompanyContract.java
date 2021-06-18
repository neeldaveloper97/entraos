package no.entra.entraos.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Data;
import no.entra.entraos.api.util.EntityUtils;
import no.entra.entraos.domain.person.Contract;
import no.entra.entraos.domain.person.contracts.DefaultContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FoodAndBeverageContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.FreeLunchContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.ProductGroupsCoveredInContract;
import no.entra.entraos.domain.person.contracts.foodandbeverage.SponsoredLunchContract;
import no.entra.entraos.domain.person.contracts.tenant.TenantContract;

@Data
public class CompanyContract implements Serializable {
	private String id;
	private String company_id="";
	private String contract_name="";
	private String contract_type_id;
	private String contract_category_id;
	private String description="";
	private List<ContractProperty> contract_properties = new ArrayList<ContractProperty>();
	private LocalDateTime valid_from;
	private LocalDateTime valid_to;
	private LocalDateTime last_updated;
	private LocalDateTime created_at;
	private ContractStatus status;
	private int quantity=1;
	private List<String> docs = new ArrayList<String>();

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(valid_to);
	}

	public String getContractProperty(String propertyName, String defaultValue) {
		ContractProperty p = getContract_properties().stream().filter(i -> i.getName().equalsIgnoreCase(propertyName)).findFirst().orElse(null);
		return p!=null?p.getValue():defaultValue;
	}

	public List<ProductGroupsCoveredInContract> getProductGroupsCoveredInContract() {
		String value = getContractProperty("productGroupsCoveredInContract", null);
		if(value!=null) {

			return Arrays.asList(value.split(",")).stream().map(i -> {
				for (ProductGroupsCoveredInContract me : ProductGroupsCoveredInContract.values()) {
					if (me.name().equalsIgnoreCase(i.trim())) {
						return me;
					}
				}
				return null;
			}).filter(x -> x!=null).collect(Collectors.toList());
		} else {
			return new ArrayList<ProductGroupsCoveredInContract>();
		}
	}

	public Contract convertToRegisterAPIContract(String companyName, String orgNumber, no.entra.entraos.domain.person.ContractType contractType)  {
		try {
			Contract contract = new Contract();
			String contractClassName = getContractProperty("contractClassName", null);
			if(contractClassName!=null) {
				contract.setContractClassName(contractClassName);
			}
			contract.setContractId(UUID.fromString(id));
			contract.setCompanyName(companyName);
			contract.setContractType(contractType);
			contract.setFursetDiscountInPercent(Integer.valueOf(getContractProperty("fursetDiscountInPercent", "0")));
			contract.setRebelDiscountInPercent(Integer.valueOf(getContractProperty("rebelDiscountInPercent", "0")));
			contract.setContractName(getContract_name());
			contract.setOrgNo(orgNumber);

			if (contract.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.foodandbeverage.FreeLunchContract")) {
				FreeLunchContract fcontract = new FreeLunchContract(contract);
				fcontract.setProductGroupsCoveredInContract(getProductGroupsCoveredInContract());

				String munuId = getContractProperty("Munu_SponsoredMeetupContractId", null);
				if(munuId!=null) {
					fcontract.setMunuId(munuId);
				}

				return fcontract;

			} else if (contract.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.foodandbeverage.SponsoredLunchContract")) {
				SponsoredLunchContract scontract = new SponsoredLunchContract(contract);
				scontract.setProductGroupsCoveredInContract(getProductGroupsCoveredInContract());

				String munuId = getContractProperty("Munu_SponsoredMeetupContractId", null);
				if(munuId!=null) {
					scontract.setMunuId(munuId);
				}

				String sponsorMealMaxValue = getContractProperty("sponsorMealMaxValue", "0");
				scontract.setSponsorMealMaxValue(Integer.valueOf(sponsorMealMaxValue));
				

				return scontract;
			} else if (contract.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.foodandbeverage.FoodAndBeverageContract")) {
				FoodAndBeverageContract fbContract = new FoodAndBeverageContract(contract);
				fbContract.setProductGroupsCoveredInContract(getProductGroupsCoveredInContract());

				String munuId = getContractProperty("Munu_SponsoredMeetupContractId", null);
				if(munuId!=null) {
					fbContract.setMunuId(munuId);
				}

				return fbContract;

			} else if (contract.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.tenant.TenantContract")) {
				TenantContract tContract = new TenantContract(contract);
				String recLocationsAndSensorTypes = getContractProperty("recLocationsAndSensorTypes", null);
				if(recLocationsAndSensorTypes!=null) {
					Map<String, String> recLocationsAndSensorTypesMap;
					recLocationsAndSensorTypesMap = EntityUtils.mapFromJson(recLocationsAndSensorTypes, new TypeReference<Map<String, String>>() { });
					tContract.setRecLocationsAndSensorTypes(recLocationsAndSensorTypesMap);

				}
				return tContract;
			}
			else if (contract.getContractClassName().equalsIgnoreCase("no.entra.entraos.domain.person.contracts.DefaultContract")) {
				DefaultContract dContract = new DefaultContract();
				return dContract;
			} else {
				return contract;
			}
		}	catch(Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
