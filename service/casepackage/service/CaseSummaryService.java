package us.tx.state.dfps.service.casepackage.service;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.casemanagement.dto.NotificationFileDto;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.common.request.CaseMergeValidationReq;
import us.tx.state.dfps.service.common.request.CaseSummaryReq;
import us.tx.state.dfps.service.common.request.ClosedCaseImageAccessReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsInvCnclsnReq;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.response.CaseMergeValidationRes;
import us.tx.state.dfps.service.common.response.CaseSummaryRes;
import us.tx.state.dfps.service.common.response.ClosedCaseImageAccessRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.common.response.NotificationFileRes;
import us.tx.state.dfps.service.common.response.StageRes;
import us.tx.state.dfps.service.common.response.StageInfoRes;
import us.tx.state.dfps.service.intake.dto.IncomingDetailDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.ReopenStageDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN37S
 * Class Description:Case Summary Service class Apr 26, 2017 - 3:40:18 PM
 * 07/05/2021 kurmav Artifact artf190991 : Hide PCSP tab for FAD and KIN stages
 *
 */

public interface CaseSummaryService {

	/**
	 * This method will take stage id as a parameter and check if the stage has the start date before the APS Release Date.
	 * If the stage start date is prior to the APS Release Date, it returns true, else false.
	 * @param stageStartDate
	 * @return
	 */
	public Boolean checkPreSingleStageByStartDate(Date stageStartDate);


	/**
	 * 
	 * Method Description: This Method is designed to retrieve case information
	 * as well as a list of stages associated with that case. It receives ID
	 * CASE. It returns data from the CASE, PERSON, PERSON PHONE, STAGE, STAGE
	 * PERSON LINK tables.
	 * 
	 * @param rtvCaseSummaryReq
	 * @ Tuxedo Service Name:CCMN37S
	 */

	public CaseSummaryRes getCaseSummary(CaseSummaryReq rtvCaseSummaryReq);

	/**
	 * Method Description: This method returns if all questions have been
	 * answered. Service Name: CpsInvCnclsn
	 * 
	 * @param cpsInvCnclsnReq
	 * @return CpsInvCnclsnRes @
	 */
	CpsInvCnclsnRes getQuesAnsrd(CpsInvCnclsnReq cpsInvCnclsnReq);

	/**
	 * 
	 * Method Description: The getAFCPendingStatus method was added as a part of
	 * SIR 23966 (MPS Phase III Lockdown Changes) to determine if any AFC stage
	 * approval event is currently in PROC status. This is necessary because of
	 * the possibility of multiple stage approval submissions in AFC.
	 * 
	 * @paramulIdCase - Case ID
	 * 
	 * @returnBoolean
	 */

	CommonHelperRes getAFCPendingStatus(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Method to determine if Stage is Valid for SDM Risk
	 * Assessment.
	 * 
	 * @paramulIdStage - Stage ID
	 * 
	 * @returnBoolean
	 */

	CommonHelperRes isSDMInvRiskAssmt(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: This function returns true if the case is currently
	 * checked out Checked out (OT or AI) for the given Stage Id.
	 *
	 * @paramulIdStage - Stage ID
	 * 
	 * @returnBoolean
	 */

	CommonHelperRes getCaseCheckoutStatus(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns details for the stage prior to the given
	 * stage as indicated by the STAGE_LINK table.
	 *
	 * @paramidStage - Stage ID
	 * 
	 * @returnSelectStageDto
	 */

	SelectStageDto getPriorStage(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns details for the stage Later to the given
	 * stage as indicated by the STAGE_LINK table.
	 *
	 * @paramidStage - Stage ID
	 * 
	 * @returnSelectStageDto
	 */

	SelectStageDto getLaterStage(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns information about a stage.
	 *
	 * @paramidStage - Stage ID
	 * 
	 * @returnSelectStageDto
	 */

	public SelectStageDto getStage(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Method will fetch the last update date for the passed
	 * Entity Class
	 *
	 * @paramentityClass - The Entity Class to fetch
	 * @paramprimaryKey - Primary Key for the Entity Class
	 * @paramentityID - value for the Primary Key
	 * 
	 * @returnSelectStageDto
	 */

	CommonHelperRes getLastUpdateDate(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: This method inserts closed case image access audit
	 * data into CASE_IMAGE_API_AUDIT table. Service Name: ClosedCaseImageAccess
	 * 
	 * @param closedCaseImageAccessReq
	 * @return ClosedCaseImageAccessRes @
	 */
	public ClosedCaseImageAccessRes insertApiAuditRecord(ClosedCaseImageAccessReq closedCaseImageAccessReq);

	/**
	 * Method Description: Method to determine if user has access to modify PCSP
	 * page.
	 * 
	 * Service Name - NA (Util Method hasPCSPAccess)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 * @throwsInvalidRequestException
	 * 
	 */
	CommonHelperRes hasPCSPAccess(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Method to determine if user has access to modify PCSP
	 * page.
	 * 
	 * Service Name - NA (Util Method hasStageAccessToAnyStage)
	 * 
	 * @paramcommonHelperReq
	 * @returnBoolean
	 * @throwsInvalidRequestException
	 * 
	 */
	CommonHelperRes hasStageAccessToAnyStage(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: Method to determine if user has access to modify PCSP
	 * page.
	 * 
	 * Service Name - NA (Util Method getCaseCheckoutPerson)
	 * 
	 * @paramcommonHelperReq
	 * @returnLong
	 * 
	 */
	CommonHelperRes getCaseCheckoutPerson(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval
	 * given an event from the stage.
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnToDoDetailDto
	 */

	ToDoDetailDto getApprovalToDo(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns the To Do Details for the passed To Do ID.
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnToDoDetailDto
	 */

	ToDoDetailDto getToDo(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns a set of tasks associated with events not in
	 * COMP or APRV status for a particular stage.
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnListObjectRes
	 */

	ListObjectRes getPendingEventTasks(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns details for the stage of the given type, if
	 * one exists, that originated from the given stage id. (USAGE: This method
	 * was written for SIR 16114 to find the FSU stage that most closely
	 * precedes the FRE stage with the given start date.)
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnSelectStageDto
	 */

	SelectStageDto getStageByTypeAndPriorStage(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Returns the most recent event id, event status, task
	 * code and timestamp for the given stage and event type.
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnEventDto
	 */

	EventDto getEventByStageAndEventType(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Description: Looks at the case to determine if the given user has
	 * access to any stage. Use this version of the method if you want to test
	 * access for the current user. The following items are checked: primary
	 * worker assigned to stage, one of the four secondary workers assigned to
	 * the stage, the supervisor of any of the above, the designee of any of the
	 * above supervisors
	 *
	 * @paramcommonHelperReq
	 * 
	 * @returnBoolean
	 */

	CommonHelperRes hasAccessToCase(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: This method will do the case merge validation for
	 * case summary screen. Service Name:CaseMergeValidation
	 * 
	 * @param caseMergeValidationReq
	 * @return CaseMergeValidationRes @
	 */
	public CaseMergeValidationRes getCaseMrgValidation(CaseMergeValidationReq caseMergeValidationReq);

	/**
	 * Method-Description: This method returns a date value when a stage is
	 * closed.
	 * 
	 * Service Name - NA (Util Method dtStageClosed)
	 * 
	 * @paramcommonHelperReq(Stage id)
	 * @returnDate
	 * 
	 */
	CommonHelperRes dtStageClosed(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method returns all SUB stages in the case
	 * 
	 * @throws @paramulIdCase
	 * @returnListOfStageId
	 * 
	 */
	public IDListRes getAllSUBStages(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method returns all FSU stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @
	 */
	public IDListRes getOpenFSUStages(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method returns all FRE stages in the case
	 * 
	 * @param ulIdCase
	 * @return ListOfStageId @ InvalidRequestException
	 */
	public IDListRes getOpenFREStages(CommonHelperReq commonHelperReq);

	/**
	 * Method-Description:This method checks if the passed CVS stage is
	 * currently checked out to the MPS Mobile device. The indicator for checked
	 * out cases is the CD_MOBILE_STATUS column on the Workload table.
	 * 
	 * @param ulIdStage
	 * @return Boolean -- true or False @
	 */
	public CommonHelperRes getCaseStageCheckoutStatus(CommonHelperReq commonHelperReq);

	/**
	 * Method Description: This method returns IncomingDetail for a given stage
	 * id
	 * 
	 * @param commonHelperReq
	 * @return IncomingDetailDto @
	 */
	public IncomingDetailDto getIncomingDetailByStageId(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: getOldestIntakeStageByCaseId Method Description: Fetch the
	 * Intage Stage ID for the Case
	 * 
	 * @param commonHelperReq
	 * @return StageDto @
	 */
	public StageDto getOldestIntakeStageByCaseId(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: updateStageCloseReason Method Description: update the stage
	 * close reason code
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes @
	 */
	public CommonHelperRes updateStageCloseReason(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: getRegionByCounty Method Description:
	 * 
	 * @param county
	 * @return
	 */
	public String getRegionByCounty(String county);

	StageRes saveIntakeNotesOrReopnInv(StageClosureRtrvReq stageClosureRtrvReq);

	StageRes getIntakeNotesOrReopnInv(StageClosureRtrvReq stageClosureRtrvReq);

	/**
	 * Method Name: getIntIntakeDate Method Description: This method is used to
	 * get Int Intake Date
	 * 
	 * @param idStage
	 * @return Date
	 */
	public Date getIntIntakeDate(Long idStage);
	
	/**
	 * Method Name: getIntakeStageIdForSelectedStage Method Description: Method to fetch
	 * the Intake Stage Id for the passed stage ID
	 * 
	 * @param commonHelperReq
	 * @return CommonHelperRes	 
	 */
	public CommonHelperRes getIntakeStageIdForSelectedStage(CommonHelperReq commonHelperReq);

	public void reopenStage(ReopenStageDto reopenStageDto);
	
	/**	
	 * Method Name: getPrimaryChildIdByIdStage
	 * Method Description: Method to fetch Person Id for primary child 
	 * for the passed stage ID
	 * 
	 * @param idStage
	 * 
	 * © 2019 Texas Department of Family and Protective Services
	 * FCL Artifact ID: artf128756
	 */
	public long getPrimaryChildIdByIdStage(long idStage);

	/**
	 * Method Name: getStageByIdCase
	 * Method Description: Method to fetch Stage Info for Case Id
	 *
	 * @param idCase
	 *
	 * Artifact ID: artf190991
	 */
	StageInfoRes getStageByIdCase(Long idCase);

	/**
	 * Method Name: getCaseSensitiveByIdCase
	 * Method Description: Method to fetch Case summary information for passed case ID
	 *
	 * @param idCase
	 * @return boolean
	 */
	public boolean getCaseSensitiveByIdCase(Long idCase);

	/**
	 *
	 * @param eventId
	 * @return CommonHelperRes
	 */
	CommonHelperRes getPcspPlacementId(Long eventId);
    // PPM 67321 - FCL Caregiver Notification Enhancements
    public NotificationFileRes getAcknowledgeFileList(Long idCase);
    public NotificationFileRes acknowledgementRequestDownload(CaseSummaryReq caseSummaryReq);
	public NotificationFileRes acknowledgementRequestUpload(CaseSummaryReq caseSummaryReq);
	public NotificationFileRes acknowledgementRequestSend(CaseSummaryReq caseSummaryReq);
}
