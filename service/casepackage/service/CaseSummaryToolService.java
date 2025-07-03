/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Jun 6, 2018- 2:12:21 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.common.response.CaseSumToolRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jun 6, 2018- 2:12:21 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface CaseSummaryToolService {

	/**
	 * 
	 * Method Name: getPersonList Method Description:Calls Dao get method to
	 * retrieve the person list.
	 * 
	 * @param idStage
	 * @return
	 */
	CaseSumToolRes getCaseSumToolPersonList(Long idStage);

}
