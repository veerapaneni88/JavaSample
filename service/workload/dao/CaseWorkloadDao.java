package us.tx.state.dfps.service.workload.dao;

import java.text.ParseException;
import java.util.List;

import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseLink;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN20S Class
 * Description: Operations for Case Apr 14, 2017 - 12:17:44 PM
 */

public interface CaseWorkloadDao {

	/**
	 * 
	 * Method Description:searchCaseLinksByPersonId
	 * 
	 * @param personId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN20S
	public List<CaseLink> searchCaseLinksByPersonId(Long personId);

	/**
	 * 
	 * Method Description:searchCaseByNameAndPersonId
	 * 
	 * @param personFull
	 * @param ulIdPerson
	 * @return
	 * @throws DataNotFoundException
	 * @ @throws
	 *       ParseException
	 */
	// CCMNH4D
	public List<CapsCase> searchCaseByNameAndPersonId(String personFull, Long ulIdPerson);

	/**
	 * 
	 * Method Description:updateCapsCase
	 * 
	 * @param capsCase
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMNH4D
	public void updateCapsCase(CapsCase capsCase);

	/**
	 * 
	 * Method Description:updateCasesByUnitId
	 * 
	 * @param unitId
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CAUDC2D
	public void updateCasesByUnitId(Long unitId);
}
