package us.tx.state.dfps.service.guardianshipdtl.dao;

public interface GuardianshipDtlDao {
	/**
	 * 
	 * Method Name: isFinalOutcomeDocumentedForDAD Method Description:Fetches
	 * Guardianship details records for given case where final outcome is null
	 * for DAD type.
	 * 
	 * @param idCase
	 * @return
	 */
	public boolean isFinalOutcomeDocumentedForDAD(Long idCase);

}
