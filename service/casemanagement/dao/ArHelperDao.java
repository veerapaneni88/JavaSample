package us.tx.state.dfps.service.casemanagement.dao;

import java.util.List;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsArCnclsnDetail;
import us.tx.state.dfps.common.dto.CpsArInvCnclsnDto;
import us.tx.state.dfps.common.dto.ServiceReferralDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

public interface ArHelperDao {

	public Long isRiskIndicated(Long idCase);

	public Long getContactPurposeFamAssmtRScheduling(Long idCase);

	public Long isLegalActionExists(Long idCase);

	public Long isPendingDayCareApprvalExists(Long idStage);

	public Long isPendingSeviceAuthApprvalExists(Long idStage);

	public Long isInitialSftyAssmntRejectedOrPending(Long idStage);

	public Long isExtensionRequestPending(Long idStage);

	public Long isMoreReferenceChildExists(Long idStage);

	public Long getServiceReferral(Long idStage);

	public String isLegalActionPending(Long idStage);

	public Long isPendingIntlSafetyAssmtApprvalExists(Long idStage);

	public Long getApprovalEvents(Long idEvent);

	public Long getEventByStageAndEventType(Long idStage, String evntType);

	public Long hasBeenSubmittedForApproval(Long ulIdEvent);

	public List<PersonDto> getPersonCharacteristics(Long idStage);

	public List<ServiceReferralDto> getServiceReferrals(Long idStage);

	public Long getIdAssessmentHousehold(Long idEvent);

	public void updIdAssessmentHousehold(Long idEvent, String idAssmntHousehold);

	public CpsArInvCnclsnDto getArinvCnclsnDetail(CommonHelperReq commonHelperReq);

	public CpsArInvCnclsnDto saveArinvCnclsnDetail(CpsArCnclsnDetail cpsArCnclsnDetail);

	public Long isInitialSafetyassessmentComplete(Long idEvent);

	public String isSaQuestionAnswered(Long idCpsSa, Long question);

	public Long isFsuStageOpened(Long idCase);

	public Long isSdmRaComplete(Long idCpsSa);

	public Long isPcspComplete(Long idStage);

	public Long isPcspCompletewith060(Long idStage);

	public String getCdSafetyDecn(Long idCpsSa);

	Boolean getPrintNotificationFlag(Long idEvent);

	public String isPcspProc(Long idStage);

	public Long isSdmSaProc(Long idStage);

	Long getConclusionEventId(Long idEvent, String cdTask);

	/**
	 * Method Name: checkCrimHistPerson Method Description: This method returns
	 * true if the criminal history action is null, else will return false
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	public boolean checkCrimHistPerson(long idPerson);

	/**
	 * Method Name: getIdHouseholdFromAssessmentHousehold Method Description:
	 * The method fetches the AssessmentHoudholdLink detail for given Id.
	 * 
	 * @param idAssessmentHouseholdLink
	 * @return
	 */
	public AssessmentHouseholdLink getIdHouseholdFromAssessmentHousehold(Long idAssessmentHouseholdLink);

	/**
	 * Method Name: getArinvCnclsnDetail Method Description: get ArInvstigation
	 * Detail for the current Stage.
	 * 
	 * @param idStage
	 * @return
	 */
	public CpsArCnclsnDetail getArinvCnclsnDetail(Long idStage);

	/**
	 * Method Name: selectARConclusion Method Description: get the AR Conclusion
	 * Detail for the current Stage.
	 * 
	 * @param idStage
	 * @return
	 */

	public ArInvCnclsnDto selectARConclusion(Long idStage);

	/**
	 * Method Name: updateARConclusion Method Description: update the AR
	 * Conclusion and AR EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */
	public void updateARConclusion(ArInvCnclsnDto arInvCnclsnDto);

	/**
	 * Method Name: getArEaEligibilityDetails Method Description: retrieves the
	 * AR EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */

	public List<ArEaEligibilityDto> getArEaEligibilityDetails(ArInvCnclsnDto arInvCnclsnDto);

	/**
	 * Method Name: invalidateApprovalStatus Method Description: Retrieve the
	 * Approval Id's and Delete the To-Do/Approval Event
	 * 
	 * @param ArInvCnclsnDto
	 */
	public void invalidateApprovalStatus(ArInvCnclsnDto arInvCnclsnDto);

	/**
	 * Method Name: isFbssReferralApproved
	 * Method Desc: Checks with the FBSS Referral is approved in the case if idHouseHoldPerson not null then
	 * selected for house hold else for any house hold
	 *
	 * @param idCase
	 * @param taskCodes
	 * @return
	 */
	List<EventDto> isFbssReferralApproved(Long idCase, List<String> taskCodes);

}

