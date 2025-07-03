package us.tx.state.dfps.service.casepackage.service;

import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailOutDto;
import us.tx.state.dfps.service.common.request.SpecialHandlingAudReq;
import us.tx.state.dfps.service.common.response.SpecialHandlingAudRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * while perform AUD operations for the special handling. It will update part of
 * one row in the CAPS CASE table. Apr 12, 2018- 3:24:44 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SpecialHandlingAudService {
	/**
	 * 
	 * Method Name: specialHandlingAud Method Description: Handles AUD for
	 * Special handling in case summary
	 * 
	 * @param specialHandlingAudReq
	 * @return @
	 */
	public SpecialHandlingAudRes specialHandlingAud(SpecialHandlingAudReq specialHandlingAudReq);

	/**
	 * 
	 * Method Name: updateSpecialHandling Method Description: This method will
	 * update the sensitivity, special handling and worker safety issues for a
	 * case. DAM CCMNG4D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @
	 */
	public void updateSpecialHandling(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes);

	/**
	 * 
	 * Method Name: updateSensitiveIndicator Method Description: Update the
	 * Sensitive Indicator in incoming detail table for the cases which are
	 * marked sensitive in either Intake or in Investigation. DAM CINVE7D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @
	 */
	public void updateSensitiveIndicator(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes);

	/**
	 * 
	 * Method Name: CallCCMNB1D Method Description: Retrieve one row from the
	 * CAPS_CASE table for a given ID_CASE. DAM CCMNB1D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @return @
	 */
	public String retrieveCapsCaseDetail(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes);

	/**
	 * 
	 * Method Name: CallCCMNE1D Method Description: This DAM retrieves ID STAGE,
	 * CD STAGE TYPE, and DT STAGE CLOSE for all stages linked to an ID CASE on
	 * the STAGE table. DAM CCMNE1D
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @param tempIndCaseSensitive
	 * @
	 */
	public void retrieveStageDetail(SpecialHandlingAudReq specialHandlingAudReq,
			SpecialHandlingAudRes specialHandlingAudRes, String tempIndCaseSensitive);

	/**
	 * 
	 * Method Name: postEvent Method Description: Handles EVENT Creation
	 * 
	 * @param specialHandlingAudReq
	 * @param specialHandlingAudRes
	 * @param specialHandlingStageDetailOutDto
	 * @param tempIndCaseSensitive
	 * @
	 */
	public void postEvent(SpecialHandlingAudReq specialHandlingAudReq, SpecialHandlingAudRes specialHandlingAudRes,
			SpecialHandlingStageDetailOutDto specialHandlingStageDetailOutDto, String tempIndCaseSensitive);

}
