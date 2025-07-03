package us.tx.state.dfps.service.casemanagement.service;

import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ArInvCnclsnReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.ArHelperRes;
import us.tx.state.dfps.service.common.response.ArInvCnclsnRes;
import us.tx.state.dfps.service.common.response.ArValidationRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.PersonDtlRes;
import us.tx.state.dfps.service.workload.dto.EventDto;

import java.util.List;

public interface ArHelperService {

	/**
	 * 
	 * @param idCase
	 * @return @
	 */
	public ArHelperRes isRiskIndicated(Long idCase);

	/**
	 * 
	 * @param ulIdEvent
	 * @return @
	 */
	public ArHelperRes hasBeenSubmittedForApproval(Long ulIdEvent);

	/**
	 * 
	 * @param idCase
	 * @return @
	 */
	public ArHelperRes getContactPurposeFamAssmtRScheduling(Long idCase);

	/**
	 * 
	 * @param idCase
	 * @return @
	 */
	public ArHelperRes isLegalActionExists(Long idCase);
	
	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isPendingDayCareApprvalExists(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isPendingSeviceAuthApprvalExists(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isInitialSftyAssmntRejectedOrPending(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isExtensionRequestPending(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isMoreReferenceChildExists(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes getServiceReferral(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isLegalActionPending(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes isPendingIntlSafetyAssmtApprvalExists(Long idStage);

	/**
	 * 
	 * @param idEvent
	 * @return @
	 */
	public ArHelperRes getApprovalEvents(Long idEvent);

	/**
	 * 
	 * @param idStage
	 * @param evntType
	 * @return @
	 */
	public ArHelperRes getEventByStageAndEventType(Long idStage, String evntType);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public PersonDtlRes getPersonCharacteristics(Long idStage);

	/**
	 * 
	 * @param idStage
	 * @return @
	 */
	public ArHelperRes getOutcome(Long idStage);

	/**
	 * 
	 * @param idEvent
	 * @return ArHelperRes @
	 */
	public ArHelperRes getIdAssessmentHousehold(Long idEvent);

	/**
	 * 
	 * @param idEvent
	 * @return ArHelperRes @
	 */

	public ArHelperRes updIdAssessmentHousehold(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * @param idEvent
	 * @return ArHelperRes @
	 */
	public ArInvCnclsnRes getArInvConclusionDetail(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * @param idEvent
	 * @return ArHelperRes @
	 */
	public ArInvCnclsnRes saveArInvConclusionDetail(ArInvCnclsnReq arInvCnclsnReq);

	/**
	 * Method Description: Method to get Validations for AR conclusion page.
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException
	 * @
	 */
	public ArValidationRes getArInvConclusionValidation(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Method to get PrintNotification Flag
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException
	 * @
	 */
	public CommonHelperRes getPrintNotificationFlag(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Method to get get Conclusion EventId
	 * 
	 * @param commonHelperReq
	 * @return
	 * @throws InvalidRequestException
	 * @
	 */
	public CommonHelperRes getConclusionEventId(CommonHelperReq commonHelperReq);

	/**
	 * Method Name: checkCrimHistPerson Method Description: This method returns
	 * true if the criminal history action is null, else will return false
	 * 
	 * @param idPerson
	 * @return boolean @
	 */
	public boolean checkCrimHistPerson(long idPerson);

	/**
	 * Method Name: selectARConclusion Method Description: get the AR Conclusion
	 * Detail for the current Stage.
	 * 
	 * @param idStage
	 * @return
	 */
	public ArInvCnclsnDto selectARConclusion(long idStage);

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

	public ArInvCnclsnDto getArEaEligibilityDetails(ArInvCnclsnDto arInvCnclsnDto);

	/**
	 * Method Name: invalidateApprovalStatus Method Description: Retrieve the
	 * Approval Id's and Delete the To-Do/Approval Event
	 * 
	 * @param ArInvCnclsnDto
	 */

	public void invalidateApprovalStatus(ArInvCnclsnDto arInvCnclsnDto);
	
	/**
	 *Method Name:	validationForSexualVctmizationQues
	 *Method Description:To validate the sexual victimization Question answeres or not for all persons < 18 years in that stage
	 *@param stageId
	 *@return
	 */
	public boolean validationForSexualVctmizationQues(Long stageId);

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
