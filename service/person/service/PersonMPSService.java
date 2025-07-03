/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 7, 2018- 6:37:46 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.person.service;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 7, 2018- 6:37:46 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonMPSService {

	/**
	 * This Method is used for checking if a person is related to particular
	 * stage of not in the MPS person tables.
	 * 
	 * @param idStage
	 * @param stagePerRelType
	 * @return
	 */
	public boolean isStagePersReltd(Long idStage, String stagePerRelType);
}
