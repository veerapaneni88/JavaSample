
package us.tx.state.dfps.service.SDM.service;

import us.tx.state.dfps.service.common.request.SDMRiskAssessmentReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * SdmRiskAssessmentFormService will have all operation which are mapped to
 * SdmRiskAssessmentForm module. May 4, 2018- 2:01:02 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface SdmRiskAssessmentFormService {

	/**
	 * 
	 * Method Name: getSdmRiskAssmtFormInfo Method Description:Returns Risk
	 * Assessment DataBean based on Risk Assessment Event Id and Stage Id It
	 * puuls back the questions, answers, followups, secondary followups and
	 * responses
	 * 
	 * @param sDMRiskAssessmentReq
	 * @return
	 */
	public PreFillDataServiceDto getSdmRiskAssmtFormInfo(SDMRiskAssessmentReq sDMRiskAssessmentReq);

}
