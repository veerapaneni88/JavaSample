/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 21, 2018- 3:42:28 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.servicedlvryclosure.service;

import java.util.HashMap;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 21, 2018- 3:42:28 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface DlvryClosureSubmitService {

	/**
	 * Method Name: getPersonIdInFDTC Method Description: to get person list
	 * 
	 * @param caseId
	 * @return HashMap
	 */
	public HashMap<Integer, String> getPersonIdInFDTC(Long caseId);

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: to get most
	 * recent fdtc sub type
	 * 
	 * @param personId
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getMostRecentFDTCSubtype(Long personId);

	/**
	 * Method Name: getSDMAssessmentStatus Method Description: This method is to
	 * get SDM Assessment Status
	 * 
	 * @param idStage
	 * @return
	 */
	public boolean getSDMAssessmentStatus(Long idStage);

}
