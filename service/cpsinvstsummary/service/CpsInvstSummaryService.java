package us.tx.state.dfps.service.cpsinvstsummary.service;

import us.tx.state.dfps.service.common.request.CpsInvstSummaryReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for CINV65S Mar 28, 2018- 4:23:38 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface CpsInvstSummaryService {

	/**
	 * Method Name: getRiskAssessmentInfo Method Description: Makes DAO calls
	 * and returns prefill string for form CFIV1000
	 * 
	 * @param request
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getRiskAssessmentInfo(CpsInvstSummaryReq request);

}
