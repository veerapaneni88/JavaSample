package us.tx.state.dfps.service.SDM.service;

import us.tx.state.dfps.service.common.request.SDMRiskAssessmentReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.SDMRiskAssessmentRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.investigation.dto.SDMRiskAssessmentDto;

public interface SDMRiskAssessmentService {

	/**
	 * To save the SDMRiskAssessmentDB to the CPS_RA table
	 * 
	 * @param SDMRiskAssessmentDto
	 * @return SDMRiskAssessmentRes @
	 */
	public SDMRiskAssessmentRes saveRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * 
	 * @param SDMRiskAssessmentReq
	 * @return long updateResult @
	 */
	public long completeAssessment(SDMRiskAssessmentReq sDMRiskAssessmentReq);

	SDMRiskAssessmentRes deleteRiskAssessment(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	SDMRiskAssessmentRes getHouseholdName(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	SDMRiskAssessmentRes saveHouseholdDtl(SDMRiskAssessmentDto sDMRiskAssessmentdto);

	/**
	 * Method Name: getExistingHRAForHousehold Method Description:
	 * 
	 * @param getsDMRiskAssessmentdto
	 * @return
	 */
	public SDMRiskAssessmentRes getExistingRAForHousehold(SDMRiskAssessmentDto getsDMRiskAssessmentdto);

	/**
	 * Method Name: getRiskAssesment. Method Description: This method gets the
	 * cpa_ra record for input idStage
	 * 
	 * @param sdmRiskAssessmentReq
	 * @return SDMRiskAssessmentRes
	 */
	public SDMRiskAssessmentRes getRiskAssesment(Long idStage);

	/**
	 * Method Name: queryRiskAssessmentDtls. Method Description: This service is
	 * to retrieve if Risk Assessment details exists and the event status of
	 * Risk Assessment.
	 * 
	 * @param riskAssmtValueDto
	 * @return SDMRiskAssessmentRes
	 */
	public SDMRiskAssessmentRes queryRiskAssessmentExists(SDMRiskAssessmentDto sdmRiskAssessmentDto);

	/**
	 * Method Name: getSDMRiskAssessment. Method Description: This service is to
	 * retrieve if Risk Assessment details based on the idStage and idEvent
	 * 
	 * @param idStage,idEvent
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto getSDMRiskAssessment(Long idStage, Long idEvent);

	/**
	 * Method Name: queryPageData Method Description:To pull the questions,
	 * answers, followups ans secondary follow ups when the assessment is new
	 * 
	 * @param idStage
	 * @return SDMRiskAssessmentDto
	 */
	public SDMRiskAssessmentDto queryPageData(Long idStage);

	/**
	 * Method Name: getPrimaryCreGivrHistoryCount Method Description:To get a
	 * Primary Caregiver History with care giver Id and stageId
	 * 
	 * @param idPrimaryCaregiver,idStage
	 * @return Long
	 */
	public Long getPrimaryCreGivrHistoryCount(Long idPrimaryCaregiver, Long idStage);

	/**
	 * Method Name: getSecondaryCreGivrHistoryCount Method Description:To get a
	 * Secondary Caregiver History with care giver Id and stageId
	 * 
	 * @param idPrimaryCaregiver,idStage
	 * @return Long
	 */
	public Long getSecondaryCreGivrHistoryCount(Long idSecondaryCaregiver, Long idStage);

	/**
	 * Method Name: retrieveSafetyAssmtData Method Description:This method is
	 * called from display method in SafetyAssmtConversation if the page has
	 * been previously saved. It retrives back all the responses
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	public SafetyAssessmentRes retrieveSafetyAssmtData(SafetyAssessmentReq safetyAssessmentReq);

	/**
	 * Method Name:getSubStageOpen Method Description:Returns if a Sub Stage is
	 * open, with a given StageId
	 * 
	 * @param safetyAssessmentDto
	 * @return boolean
	 */
	public boolean getsubStageOpen(Long idCase);

	/**
	 * Method Name:getCurrentEventStatus Method Description:Returns back a
	 * string containing current Event status
	 * 
	 * @param idStage
	 * @param idCase
	 * @return String
	 */
	public String getCurrentEventStatus(Long idStage, Long idCase);

}