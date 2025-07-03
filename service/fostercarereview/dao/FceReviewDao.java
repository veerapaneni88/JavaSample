/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 21, 2017- 4:42:16 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fostercarereview.dao;

import us.tx.state.dfps.common.domain.FceReview;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 21, 2017- 4:42:16 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FceReviewDao {

	FceReview getById(Long idfceReview);

	/**
	 * Method Name: enableFosterGoupMessage Method Description: Method is used
	 * to specify weather to display foster care group message
	 * 
	 * @return
	 */
	CommonBooleanRes enableFosterGoupMessage();

}
