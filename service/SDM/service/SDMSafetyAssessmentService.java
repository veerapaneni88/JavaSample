package us.tx.state.dfps.service.SDM.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.UserProfileDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;

/**
 * Service Interface for functions required for implementing SDM Safety
 * Assessment functionality
 *
 */
public interface SDMSafetyAssessmentService {

	/**
	 * This method is used to update SDM Safety assessment
	 * 
	 * @param safetyAssessmentDB
	 * @param userProfileDB
	 * @return SDMSafetyAssessmentDB @
	 */
	public SDMSafetyAssessmentDto saveAssessment(SDMSafetyAssessmentDto safetyAssessmentDB,
			UserProfileDto userProfileDB);

	/**
	 * This method is used to delete SDM Safety assessment details
	 * 
	 * @param safetyAssessmentDB
	 * @return long @
	 */
	long deleteSafetyAssmtDetails(SDMSafetyAssessmentDto safetyAssessmentDB);

	SafetyAssessmentRes getSDMSafetyAssessment(Long idEvent, Long idStage);

	SafetyAssessmentRes isSftAsmntInProcStatusAvail(Long idStage, String cdStage);

	SafetyAssessmentRes getQueryPageData(Long idStage);

	CommonStringRes getAsmtTypHoHold(CommonHelperReq commonHelperReq);

	public SDMSafetyAssessmentDto completeAssessment(SDMSafetyAssessmentDto safetyAssessmentDB,
			UserProfileDto userProfileDB);

	public SDMSafetyAssessmentDto undoCompleteAssessment(SDMSafetyAssessmentDto safetyAssessmentDB);

	/**
	 * 
	 * Method Name: displaySDMSafetyAssessmentForm Method Description:
	 * 
	 * @return
	 */
	public PreFillDataServiceDto displaySDMSafetyAssessmentForm(SafetyAssessmentReq safetyAssessReq);
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getLatestSafetyAssessmentEvent(Long idStage);
	/**
	 * 
	 *Method Name:	getLatestSafetyAssessmentEvent
	 *Method Description:gets the Date of Latest Assessment in the Stage.
	 * latest safety assessment event id
	 *@param idStage
	 *@param idEvent
	 *@return
	 */
	public Date getLatestSafetyAssessmentEvent(Long idStage,Long idEvent);
	/**
	 * 
	 *Method Name:	getAsmtTypHouseHoldForAssessedPerson
	 *Method Description:This method is used to check if some household is selected as a person assessed.
	 *@param idStage
	 *@param idCpsSa
	 *@param idPersonList
	 *@return
	 */
	public String getAsmtTypHouseHoldForAssessedPerson(Long idStage, Long idCpsSa,List<Long> idPersonList);
}
