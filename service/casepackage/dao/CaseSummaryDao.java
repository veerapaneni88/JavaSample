package us.tx.state.dfps.service.casepackage.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.casemanagement.dto.NotificationFileDto;
import us.tx.state.dfps.casemanagement.dto.ProviderPlacementDto;
import us.tx.state.dfps.common.domain.CgNotifFileUpload;
import us.tx.state.dfps.common.domain.CgNotifFileUploadDtl;
import us.tx.state.dfps.service.casepackage.dto.CaseStageSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.CaseSummaryDto;
import us.tx.state.dfps.service.casepackage.dto.SelectStageDto;
import us.tx.state.dfps.service.casepackage.dto.ToDoDetailDto;
import us.tx.state.dfps.service.common.request.CaregiverAckReq;
import us.tx.state.dfps.service.common.response.ListObjectRes;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.casepackage.dto.MergedIntakeARStageDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN37S
 * Tuxedo DAM Name: CCMN15D, CCMND9D, CSECD2D, CSEC54D, CSECF4D Class
 * Description: Case Summary Dao Class Apr 26, 2017 - 3:58:12 PM
 */

public interface CaseSummaryDao {

	/**
	 * 
	 * Method Description: This method is used to retrieve Case summary based on
	 * id case Tuxedo Service Name:CCMN37S Tuxedo DAM Name :CCMND9D
	 * 
	 * @param idCase
	 * @
	 */
	public CaseSummaryDto getCaseInfo(Long idCase);

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from CPS Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @
	 */

	public List<CaseStageSummaryDto> getCaseStageCPSInfo(Long idCase);

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from APS Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @
	 */

	public List<CaseStageSummaryDto> getCaseStageAPSInfo(Long idCase);

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from CCL Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @
	 */

	public List<CaseStageSummaryDto> getCaseStageCCLInfo(Long idCase);

	/**
	 * 
	 * Method Description: This method is used to retrieve case summary based on
	 * case and stage table from DTL Program Tuxedo Service Name:CCMN37S Tuxedo
	 * DAM Name :CCMN15D
	 * 
	 * @param idCase
	 * @param stagePersonRoleOpen
	 * @param stagePersonRoleClose
	 * @param personPhonePrimary
	 * @param personPhoneType
	 * @
	 */

	public List<CaseStageSummaryDto> getCaseStageDTLInfo(Long idCase);

	public List<CaseStageSummaryDto> getCaseStagePhonePersonInfo(Long idPerson);

	/**
	 * 
	 * Method Description: This method is used to get prior stage id based on id
	 * stage as input Tuxedo Service Name: CCMN37S Tuxedo DAM Name :CSECD2D
	 * 
	 * @param idStage
	 * @
	 */

	public Long getStageMergeInfo(Long idStage);

	/**
	 * 
	 * Method Description: This Method is used to retrieve Date of Incoming
	 * Detail by giving id stage as input Tuxedo Service Name: CCMN37S Tuxedo
	 * DAM Name : CSEC54D
	 * 
	 * @param idStage
	 * @
	 */

	public CaseStageSummaryDto getIncomingDetail(Long idStage);

	/**
	 * 
	 * Method Description: This method is used to check for child fatality from
	 * stored procedure Tuxedo Service Name:CCMN37S Tuxedo DAM Name :CSECF4D
	 * 
	 * @param idCase
	 * @
	 */

	public boolean checkChildFt(Long idCase);

	/**
	 * 
	 * Method Description: This method is used to retrieve the count of AFC
	 * stage records which are still pending for approval Tuxedo Service Name:NA
	 * (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidCase - Case ID
	 * 
	 */
	public Long getAFCPendingCount(Long idCase);

	/**
	 * 
	 * Method Description: This method will check to see if an SDM already
	 * exists for the stage Tuxedo Service Name:NA (Util Service) Tuxedo DAM
	 * Name :NA
	 * 
	 * @param idStage
	 *            - Stage ID @
	 */
	public Long sdmEventExists(Long idStage);

	/**
	 * 
	 * Method Description: This method is used to get the person ID who has
	 * checked out the case for the passed stage ID Tuxedo Service Name:NA (Util
	 * Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidStage - Stage ID
	 * 
	 */
	public Long getCaseCheckoutPerson(Long idStage);

	/**
	 * 
	 * Method Description: Returns details for the stage passed. When the type
	 * is current, the passed stage details is fetched. When the type is prior,
	 * the details of the stage prior to the one passed if fetched
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidStage - Stage ID
	 * 
	 */
	public SelectStageDto getStage(Long idStage, String stageType);

	/**
	 * 
	 * Method Description: Method will fetch the last update date for the passed
	 * Entity Class
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramentityClass - The Entity Class to fetch
	 * @paramprimaryKey - Primary Key for the Entity Class
	 * @paramentityID - value for the Primary Key
	 * 
	 */
	public Date getLastUpdateDate(String entityClass, String primaryKey, Long entityID);

	/**
	 * Method Description: This method returns if all questions have been
	 * answered. Service Name: CpsInvCnclsn
	 * 
	 * @param idStage
	 * @return Integer @
	 */
	public Integer getCountForCpsInvCnclsn(Long idStage);

	/**
	 * Method Description: Method to get a case record, given the case id.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idStage
	 *            - caseId
	 * @ @return CaseSummaryDto
	 */
	public CaseSummaryDto getCaseDetails(Long caseId);

	/**
	 * Method Description: Method to look at the case to determine if the given
	 * user has access to any open or closed stage.
	 * 
	 * @paramindStageClose, idCase, userID
	 * 
	 * @returnBoolean
	 * 
	 */
	public Boolean hasStageAccessToAnyStage(String indStageClose, Long ulIdCase, Long userID);

	/**
	 * 
	 * Method Description: Returns a the event and todo id's for an approval
	 * given an event from the stage.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidEvent - Event ID
	 * 
	 */
	public ToDoDetailDto getApprovalToDo(Long idEvent);

	/**
	 * 
	 * Method Description: Returns the To Do Details for the passed To Do ID.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidToDo - To Do ID
	 * 
	 */
	public ToDoDetailDto getToDo(Long idToDo);

	/**
	 * 
	 * Method Description: Returns a set of tasks associated with events not in
	 * COMP or APRV status for a particular stage.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidStage - Stage ID
	 * 
	 */
	public ListObjectRes getPendingEventTasks(Long idStage);

	/**
	 * 
	 * Method Description: Returns details for the stage of the given type, if
	 * one exists, that originated from the given stage id. (USAGE: This method
	 * was written for SIR 16114 to find the FSU stage that most closely
	 * precedes the FRE stage with the given start date.)
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @parampriorStageID - Prior Stage ID
	 * @paramcdStage - Stage Code
	 * 
	 */
	public SelectStageDto getStageByTypeAndPriorStage(Long priorStageID, String cdStage);

	/**
	 * 
	 * Method Description: Returns the most recent event id, event status, task
	 * code and timestamp for the given stage and event type.
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidStage - Stage ID
	 * @paramcdEventType - Event Type
	 * 
	 */
	public EventDto getEventByStageAndEventType(Long idStage, String cdEventType);

	/**
	 * 
	 * Method Description: Looks at the case to determine if the given user has
	 * access to any stage. Use this version of the method if you want to test
	 * access for the current user. The following items are checked: primary
	 * worker assigned to stage, one of the four secondary workers assigned to
	 * the stage, the supervisor of any of the above, the designee of any of the
	 * above supervisors
	 * 
	 * Tuxedo Service Name:NA (Util Service) Tuxedo DAM Name :NA
	 * 
	 * @paramidCase - Case ID
	 * @paramidPerson - Person ID
	 * 
	 */
	public Boolean hasAccessToCase(Long idCase, Long idPerson);

	/**
	 * Method Description: This method inserts closed case image access audit
	 * data into CASE_IMAGE_API_AUDIT table. Service Name: ClosedCaseImageAccess
	 * 
	 * @param idCase
	 * @param idPerson
	 * @return String @
	 * 
	 */
	public String insertApiAuditRecord(Long idCase, Long idPerson);

	/**
	 * 
	 * Method Name: getOldestIntakeStageByCaseId Method Description: Retrieve
	 * the Intake Stage Details for a Case
	 * 
	 * @param idCase
	 * @return StageDto @
	 */
	public StageDto getOldestIntakeStageByCaseId(Long idCase);

	/**
	 * 
	 * Method Name: updateStageCloseReason Method Description: Update the stage
	 * close reason
	 * 
	 * @param idStage
	 * @param cdStageReasonClosed
	 * @return CommonHelperRess @
	 */
	public String updateStageCloseReason(Long idStage);

	/**
	 * 
	 * Method Name: getRegionByCounty Method Description:
	 * 
	 * @param county
	 * @return
	 */
	public String getRegionByCounty(String county);

	/**
	 * 
	 * @param idStage
	 * @param nmStage
	 * @param idCase
	 * @return
	 */
	String getAlertForIntakeNotes(Long idStage, String nmStage, Long idCase);

	/**
	 * Method Name: getIntIntakeDate Method Description: This method is used to
	 * get Int Intake Date
	 * 
	 * @param idStage
	 * @return Date
	 */
	public Date getIntIntakeDate(Long idStage);

	/**
	 * Method Name: getLaterFSUStage Method Description:
	 * 
	 * @param idStage
	 * @return
	 */
	public SelectStageDto getLaterFSUStage(Long idStage);
	
	/**
	 * Method Name: getIntakeStageIdForSelectedStage Method Description: Method to fetch
	 * the Intake Stage Id for the passed stage ID
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long getIntakeStageIdForSelectedStage(Long idStage);

	/**
	 *Method Name:	getPriorStgWthStageType
	 *Method Description: Method returns the specific prior stage for 
	 *passed current stage.
	 *@param idStage
	 *@param cdStage
	 *@return
	 */
	Long getPriorStgWthStageType(Long idStage, String cdStage);

	List<MergedIntakeARStageDto> getLinkedIntakeARStages(int idCase);


    public List<CgNotifFileUpload> getNeubusRecords(Long idCase);
    public Long createNeubusRecord(String idDocument, Long idCase, Long idStage, Long loginUserId);

	public Long getNebusRecordById(String newRecordDocId, Long idCase, Long idStage, Long loginUserId);
    public List<CgNotifFileUploadDtl> getNeubusFileList(List<Long> idCgNotifFileUploadList);
    public Long createNeubusFile(String newFileNuid, Long idCgNotifFileUpload, String filename, String cdDocType, String cdReqStatus, String comments, Long placementId, Long loginUserId);
    public void updateNeubusFile(Long idCgNotifFileUploadDtl, String status, Long loginUserId);
    public NotificationFileDto getNeubusIdentifiers(Long idCgNotifFileUpload, Long idCgNotifFileUploadDtl);

    public List<ProviderPlacementDto> fetchCurrentPlacementInformation(Long idCase);
    public CaregiverAckReq findAckRequestParameters(Long idCgNotifFileUploadDtl);
}
