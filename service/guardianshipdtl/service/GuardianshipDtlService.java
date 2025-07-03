package us.tx.state.dfps.service.guardianshipdtl.service;

public interface GuardianshipDtlService {

	/**
	 * 
	 * Method Name: isDADFinalOutcomeDocumented Method Description:Method checks
	 * if Final Outcome has been documented on Guardian Detail page for Guardian
	 * Type = DAD
	 * 
	 * @param idCase
	 * @return
	 */
	public Boolean isDADFinalOutcomeDocumented(Long idCase);

}
