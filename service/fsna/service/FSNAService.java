/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 3:12:26 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fsna.service;

import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsMonthlyEvalReq;
import us.tx.state.dfps.service.common.request.FSNAAssessmentDtlGetReq;
import us.tx.state.dfps.service.common.request.SdmFsnaReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;
import us.tx.state.dfps.service.common.response.FSNAValidationRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: May 3, 2018 - 12:57:20 PM
 */
public interface FSNAService {
	/**
	 * This is the service to get the FSNA assessment details for dispaly Method
	 * Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	FSNAAssessmentDtlGetRes getFSNAAssessmentDtl(FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq);

	/**
	 * This is the service to get the FSNA FBSS Form details for dispaly Method
	 * Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */

	PreFillDataServiceDto getFSNAFbssForm(FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq);

	/**
	 * Method Name: saveCpsFsna Method Description:This method is used to save
	 * the CPS FSNA assessment
	 *
	 * @param appEvent
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @
	 */
	FSNAAssessmentDtlGetRes saveCpsFsna(SdmFsnaReq sdmFsnaReq);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param cpsFsnaDto
	 * @return
	 */
	FSNAAssessmentDtlGetRes deleteSdmFsna(CpsFsnaDto cpsFsnaDto);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param cpsFsnaDto
	 * @return
	 */
	FSNAAssessmentDtlGetRes completeSdmFsna(CpsFsnaDto cpsFsnaDto, UserProfileDto userProfileDB);

	/**
	 * 
	 * Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	CommonStringRes getFSNAAssmntType(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description:This method is to get validation errors before save
	 * 
	 * @param sdmFsnaReq
	 * @return
	 */
	FSNAValidationRes validateFSNAAssessment(SdmFsnaReq sdmFsnaReq);

	FSNAAssessmentDtlGetRes checkCareGiverPerson(Long personId, Long stageId);

	/**
	 * This is the service to get the CVS Monthly Evaluation Form details for
	 * display Method Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */

	PreFillDataServiceDto getCpsMonthlyEvalForm(CpsMonthlyEvalReq cpsMonthlyEvalReq);
}
