/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 11, 2018- 2:19:25 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.subcare.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This method
 * is used to retrieve the information on the subcare Visitation Plan form.
 * Service name :CINV95S May 11, 2018- 2:19:25 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SubVisitationPlanService {

	/**
	 * Method Name: getSubVisitPlanDetail Method Description: This method is
	 * used to retrieve the information on the subcare Visitation Plan form.
	 * 
	 * @param commonHelperReq
	 * @return PreFillDataServiceDto
	 */
	PreFillDataServiceDto getSubVisitPlanDetail(CommonHelperReq commonHelperReq);

}
