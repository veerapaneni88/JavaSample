/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 23, 2017- 11:03:31 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.approval.service;

import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.common.request.ApprovalPersonReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.KinRejectApprovalReq;
import us.tx.state.dfps.service.common.response.ApprovalPersonRes;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.workload.dto.ApproversDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.SecondaryApprovalDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 23, 2017- 11:03:31 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ApprovalStatusService {

	/**
	 * Method Name: saveApproval Method Description:This method is used to save
	 * the assign staff to print closure notifications in approval status page
	 * 
	 * @param approvalPersonRes
	 * @return
	 * 
	 */
	public ApprovalPersonRes saveApproval(ApprovalPersonReq approvalPersonRes);

	/**
	 * Method Name: getApproval Method Description:This method is to retrieve
	 * the name of assign staff to print in approval status page
	 * 
	 * @param approvalPersonRes
	 * @return
	 */
	public ApprovalPersonRes getApproval(ApprovalPersonReq approvalPersonRes);

	/**
	 * The Method returns if the Second level Approval is required for CPS
	 * Investigation Conclusion Approval.
	 * 
	 * @param secondaryApprovalDto
	 * @return
	 */
	public Boolean isSecondLevelApproverRequiredForCPSINV(SecondaryApprovalDto secondaryApprovalDto);

	/**
	 * Method Name: getDayCareApproval Method Description:This method determine
	 * where it is Day care Request Service Authorization Approval or Regular
	 * Approval
	 * 
	 * @param idEvent
	 * @return Boolean
	 */
	public Boolean getDayCareApproval(Long idEvent);

	/**
	 * 
	 * Method Name: getSSCCReferalForIdPersonDC Method Description: Get the
	 * Active Placement Referral for Day care Request
	 * 
	 * @param idEvent
	 * @return Long
	 */
	public Long getSSCCReferalForIdPersonDC(Long idEvent);

	/**
	 * Method Name: getSSCCReferralForIdPersonPALorSUB Method Description:This
	 * method gets SSCC Referral id for the person id in PAL or SUB stage.
	 * 
	 * @param idEvent
	 * @return Long
	 */
	public Long getSSCCReferralForIdPersonPALorSUB(Long idEvent);

	/**
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 * 
	 * @param idSSCCRererral
	 * @return Long
	 */
	public SSCCListDto updateSSCCList(Long idSSCCRererral);

	/**
	 * Method Name: getSSCCReferralFamilyForIdPerson Method Description: This
	 * method gets SSCC Referral Family id for the person id in NOT (SUB or PAL)
	 * stage.
	 * 
	 * @param idEvent
	 * @return Long
	 */
	public Long getSSCCReferralFamilyForIdPerson(Long idEvent);

	/**
	 * Method Name: updateSSCCReferral Method Description:This method sets
	 * SSCC_REFERRAL table with IND_LINKED_SVC_AUTH_DATA = 'Y',
	 * DT_LINKED_SVC_AUTH_DATA = SYSDATE for the given SSCC Referral Id. and
	 * sets SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'.
	 * 
	 * @param idSSCCReferral
	 * @return SsccReferral
	 */
	public SsccReferral updateSSCCReferral(Long idSSCCReferral);

	/**
	 * Method Name: getVendorId Method Description:getVendorId Method
	 * Description: This method gets the Vendor Id for the given event id
	 * 
	 * @param idEvent
	 * @return String
	 */
	public String getVendorId(Integer idEvent);

	/**
	 * Method Name: isVendorIdExistsBatchParameters Method Description:This
	 * method checks whether vendor id exists or not in BATCH_SSCC_PARAMETERS
	 * table
	 * 
	 * @param vid
	 * @return Boolean
	 */
	public Boolean isVendorIdExistsBatchParameters(String vid);

	/**
	 * MethodName: updateSSCCReferralFamily MethodDescription: This method sets
	 * SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idEvent
	 * @return long
	 */
	public long updateSSCCReferralFamily(long idEvent);

	/**
	 * Method Name: fetchTodoDtlAdptAssistNxtRecrtRevw Method Description:Fetch
	 * the Todo Details of the next recert open Todo task for the stage.
	 * 
	 * @param idStage
	 * @param cdEventTaskCode
	 * @return List<TodoDto>
	 */
	public List<TodoDto> fetchTodoDtlAdptAssistNxtRecrtRevw(Long idStage, String cdEventTaskCode);

	/**
	 * Method Name: approveKinHome Method Description: This method is used to
	 * process kinship approval.
	 * 
	 * @param approversDto
	 * @param eventDto
	 * @param appEventDto
	 * @param todoDto
	 */
	public Long approveKinHome(ApproversDto approversDto, EventDto eventDto,
								  EventDto appEventDto, TodoDto todoDto, KinHomeInfoDto kinHomeInfoDto);

	public Integer rejectKinHome(KinRejectApprovalReq kinRejectApprovalReq);

	public Long approveKinMonthlyPayment(KinHomeInfoDto kinHomeInfoDto, EventDto eventDto,
										 EventDto appEventDto, TodoDto todoDto);

	/**
	 * Method Name: updateApprovers Method Description: This method is used to
	 * update approvers details.
	 * 
	 * @param approversValBean
	 * @return Long
	 * 
	 */
	public Long updateApprovers(ApproversDto approversValBean);

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update event status details.
	 * 
	 * @param eventId
	 * @param eventStatus
	 * @return Long
	 */
	public Long updateEventStatus(Long eventId, String eventStatus);

	/**
	 * Method Name: deleteTodoKin Method Description: This method is used to
	 * delete toDo details.
	 * 
	 * @param todoDto
	 * @return Long
	 */
	public Long deleteTodoKin(TodoDto todoDto);

	/**
	 * Method Name: getNmCaseKin Method Description: This method is used to get
	 * case kin details.
	 * 
	 * @param idCase
	 * @return String
	 */
	public String getNmCaseKin(Long idCase);

	/**
	 * Method Name: insertTodoKin Method Description: This method is used to
	 * insert todo details.
	 * 
	 * @param todoDto
	 * @return Long
	 */
	public Long insertTodoKin(TodoDto todoDto);

	/**
	 * Method Name: createTodoNxtRecertReview Method Description:Creates a next
	 * recert To do in the newly opened PAD stage succeeding the ADO stage.
	 * 
	 * @param adptAssistRecertTodoDetail
	 * @param idStage
	 * @return Long
	 */
	public Long createTodoNxtRecertReview(List<TodoDto> adptAssistRecertTodoDetail, Long idStage);

	/**
	 * 
	 * Method Name: processARConclusionStage Method Description: This method
	 * will look at the closure reason and decide if it needs to progress the
	 * case to INV or to close the AR Stage or progress to FPR
	 * 
	 * @param idCase
	 * @param idFromStage
	 * @param idApprover
	 * @param idApproval
	 * @return String
	 */
	public String processARConclusionStage(int idCase, int idFromStage, int idApprover, Long idApproval);

	/**
	 * Method Name: isSecondaryApprovalRequired Method Description:This method
	 * is used to determine if there is a need for secondary approval
	 * 
	 * @param approvalViewDto
	 * @return boolean
	 */
	public boolean isSecondaryApprovalRequired(SecondaryApprovalDto approvalViewDto);

	/**
	 * Method Name: getPendingApprovalCount Method Description:This function
	 * returns Count of the Pending Approvals for the given Approval Id.
	 * 
	 * @param idApproval
	 * @return long
	 */
	public long getPendingApprovalCount(long idApproval);

	/**
	 * 
	 * Method Name: checkRegionChange Method Description: check if region for
	 * the child is changed
	 * 
	 * @param idEvent
	 * @param idCase
	 * @return
	 */
	public String checkRegionChange(Long idEvent, Long idCase);

	public Long getICPCApprovalLevel(Long idEvent);

	public List<ApproversDto> getapproversdtoList(Long idApproval);

	public ApprovalStatusRes isApproverLoggedIn(Long idApproval);

	/**
	 * Method Name: getBoardEmail Method Description: This method retrieves
	 * board member's email addresses based on placement county.
	 * 
	 * @param idCase,
	 *            nmStage
	 * @return List<String>
	 */
	public List<String> getBoardEmail(Long idCase, String nmStage);

	/**
	 * Artifact ID: artf151569
	 * Method Name: generateAlertIfStageOpen
	 * Method Description: This method is used to generate alerts for the open Prior or Progressed stage based on the
	 * given cdStage and idStage
	 *
	 * @param idStage
	 * @param cdStage
	 * @param idCase
	 * @param idUser
	 * @param isCpsRA
	 */
	void generateAlertIfStageOpen(Long idStage, String cdStage, Long idCase, Long idUser, boolean isCpsRA);

	/**
	 * Artifact ID: artf151569
	 * Method Name: fetchCpsPriorOrProgressedStage
	 * Method Description: This method is used to fetch the prior or progressed stage id's for the given stage
	 *
	 * @param idStage
	 * @param cdStage
	 * @return
	 */
	List<Long> fetchCpsPriorOrProgressedStage(Long idStage, String cdStage);

	/**
	 * Artifact ID: artf151569
	 * Method Name: getCpsProgressedStageForSelectedStage
	 * Method Description: This method is used to retrieve the FPR Stage ID
	 *
	 * @param idStage
	 * @param cdStage
	 * @param stageType
	 * @return
	 */
	Long getCpsProgressedStageForSelectedStage(Long idStage, String cdStage, String stageType);

	/**
	 * This method will update the EMR status in DB.
	 *
	 * @param emrStatus
	 * @param idStage
	 * @return
	 */
	Long updateEmrStatusForSelectedStage(String emrStatus, Long idStage);

	/**
	 * Artifact ID: artf151569
	 * Method Name: cpsCopyOpenServiceAuth
	 * Method Description: This method is used for CPS - INV/A-R to copy open Service Auth to FPR
	 *
	 * @param idStage
	 * @param cdStage
	 * @param idUser
	 * @return
	 */
	void cpsCopyOpenServiceAuth(Long idStage, String cdStage, Long idUser);

	/**
	 * Artifact ID: artf164464
	 * Method Name: checkPCSPOpenOnApproval
	 * Method Description: This method checks if there is an open PCSP in the case and there are no other PCSP
	 * applicable stages (INV, A-R, FPR, FSU, FRE) open in the case
	 *
	 * @param idCase
	 * @param idStage
	 * @param cdStage
	 * @return
	 */
	boolean checkPCSPOpenOnApproval(Long idCase, Long idStage, String cdStage);


	public void saveHomeAssessmentApproval(CommonHelperReq commonHelperReq);

	//public void updateAndDeleteCciStaffingTodos(Long idStage,String taskCode);

	public void deleteTodosByTaskAndCase(Long idCase, String taskCode);

}
