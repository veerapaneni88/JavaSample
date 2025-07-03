package us.tx.state.dfps.service.stageClosure.dao;

import java.util.HashMap;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * StageClosureDao Sep 6, 2017- 6:31:51 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface StageClosureDao {

	/**
	 * Method Name: getPersonIdInFDTC Method Description:This method returns the
	 * list of person that have legal actions of type FDTC for a given case
	 * 
	 * @param caseId
	 * @return HashMap<Integer, String>
	 */
	public HashMap<Long, String> getPersonIdInFDTC(Long caseId);

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description:Returns the most
	 * recent FDTC Subtype and Outcome date for a Person in a given case id
	 * 
	 * @param personId
	 * @return HashMap
	 */
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId);

	/**
	 * Method Name: getRunAwayStatus Method Description: This method is to check
	 * runAway status
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean getRunAwayStatus(Long idPerson);
}
