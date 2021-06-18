package no.entra.entraos.api.model;

public enum ContractStatus {
	
	Activated, //set when the building owner or the company confirmed and agreed 
	Pending_For_Confirmation, //one side is waiting for agreement 
	Inactivated, //invalidated by the building owner
}
