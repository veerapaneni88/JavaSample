/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 7, 2018- 10:42:07 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.service;

import java.text.ParseException;

import us.tx.state.dfps.service.common.request.FamilyPlanEvalReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 7, 2018- 10:42:07 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FamilyPlanEvalService {

	/**
	 * 
	 * Method Name: getfamilyService Method Description: The service for CSVC24S
	 * 
	 * @param familyPlanEvalReq
	 * @return
	 * @throws ParseException
	 */
	public PreFillDataServiceDto getFamilyPlanService(FamilyPlanEvalReq familyPlanEvalReq);

}
