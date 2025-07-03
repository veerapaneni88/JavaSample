
package us.tx.state.dfps.service.casemanagement.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PCSPPlcmntDao Sep 21, 2017- 4:19:18 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PCSPPlcmntDao {

	/**
	 * Method Name: hasPlcmntForStage Method Description:This method checks if
	 * there any pcsp placements exists for the stage
	 * 
	 * @param idStage
	 * @return boolean
	 * @throws DataNotFoundException
	 */
	public boolean hasPlcmntForStage(Long idStage) throws DataNotFoundException;

	/**
	 * Method Name: isPlcmntWithCrgvrAtCls Method Description:This method checks
	 * if there any pcsp placements with closed stage as the input stage having
	 * reason Child Remains in PCSP at Case Closure-No Legal Custody(060)
	 * 
	 * @param idStage
	 * @return boolean
	 * @throws DataNotFoundException
	 */
	public boolean isPlcmntWithCrgvrAtCls(Long idStage) throws DataNotFoundException;

	/**
	 * Method Name: hasPlacementForCase Method Description: Method declaration
	 * for checing pcsp placements for the Case.
	 * 
	 * @param idCase
	 * @return boolean
	 */
	public boolean hasPlacementForCase(Long idCase);

	/**
	 *
	 * @param eventId
	 * @return String
	 */
	public String getPlacementIdForEvent(Long eventId);
}
