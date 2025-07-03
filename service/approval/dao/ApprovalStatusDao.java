/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 23, 2017- 10:59:32 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.approval.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.approval.dto.ApprovalStatusFacilityIndicatorDto;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;
import us.tx.state.dfps.service.workload.dto.ApproveApprovalDto;
import us.tx.state.dfps.service.workload.dto.ApproversDto;
import us.tx.state.dfps.service.workload.dto.RejectApprovalDto;
import us.tx.state.dfps.service.workload.dto.SecondaryApprovalDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 23, 2017- 10:59:32 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ApprovalStatusDao {

	/**
	 * Method Name: saveApproval Method Description:This method is used to save
	 * the assign staff to print closure notifications in approval status page
	 * 
	 * @return @
	 */
	public ApprovalPersonPrintDto saveApproval(ApprovalPersonPrintDto approvalPersonDto);

	/**
	 * Method Name: getApproval Method Description:This method is to retrieve
	 * the name of assign staff to print in approval status page
	 * 
	 * @param approvalPersonDto
	 * @return @
	 */
	public ApprovalPersonPrintDto getApproval(ApprovalPersonPrintDto approvalPersonDto);

	/**
	 * *Method Name: getStageChildAgeList Method Description:This method is to
	 * retrieve the information of underaged child for the stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return @
	 */
	public List<Date> getStageChildAgeList(Long idCase, Long idStage);

	/**
	 * *Method Name: getFinalRiskLevelForStage Method Description:This method is
	 * to retrieve the final risk level for the stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return @
	 */
	public List<String> getFinalRiskLevelForStage(Long idStage);

	/**
	 * Method Name: getLatestSafetyDecision Method Description:This method is to
	 * retrieve the Latest safety decision for Stage.
	 * 
	 * @param idStage
	 * @return @
	 */
	public String getLatestSafetyDecision(Long idStage);


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
	 * @throws DataNotFoundException
	 */
	public SsccReferral getSSCCReferalForIdPersonDC(Long idEvent);

	/**
	 * Method Name: updateSSCCReferral Method Description:This method sets
	 * SSCC_REFERRAL table with IND_LINKED_SVC_AUTH_DATA = 'Y',
	 * DT_LINKED_SVC_AUTH_DATA = SYSDATE for the given SSCC Referral Id. and
	 * sets SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'.
	 * 
	 * @param idSSCCReferral
	 * @return Long
	 */
	public SsccReferral updateSSCCReferral(Long idSSCCReferral);

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
	 * @param ssccListDto
	 * @return long
	 */
	public SSCCListDto updateSSCCList(Long idSSCCRererral);

	/**
	 * Method Name: getSSCCReferralFamilyForIdPerson Method Description: This
	 * method gets SSCC Referral id for the person id NOT (SUB or PAL) stage.
	 * 
	 * @param idEvent
	 * @return Long
	 */
	public Long getSSCCReferralFamilyForIdPerson(Long idEvent);

	/**
	 * Method Name: getVendorId Method Description:This method gets the Vendor
	 * Id for the given event id
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
	 * MethodName:updateSSCCReferralFamilyDao MethodDescription:This method sets
	 * SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idEvent
	 * @return long
	 * 
	 */
	public long updateSSCCReferralFamilyDao(long idEvent);

	/**
	 * This method will update the EMR status in DB.
	 *
	 * @param emrStatus
	 * @param idStage
	 * @return
	 */
	public long updateEmrStatus(String emrStatus, long idStage);

	/**
	 * Method Name: fetchOpenTodoForStage Method Description:Fetch the IdTodo of
	 * the open Next review Todo task for the stage. It should have only one
	 * Todo in the list.
	 * 
	 * @param idStage
	 * @return List<TodoDto>
	 */
	public List<TodoDto> fetchOpenTodoForStage(Long idStage);

	/**
	 * Method Name: updateApproversSql. Method Description: This method will
	 * update the aprrovers table status after the kinHome is approved.
	 * 
	 * @param approversDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateApproversSql(ApproversDto approversDto);

	/**
	 *
	 * @param eventId
	 * @param eventStatus
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateEventStatus(Long eventId, String eventStatus);

	/**
	 * Method Name: deleteTodo. Method Description: This method used to delete
	 * todo.
	 * 
	 * @param toDoValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long deleteTodo(TodoDto todoDto);

	/**
	 * Method Name: getNmCase Method Description: Returns the case name from its
	 * id.
	 * 
	 * @param ulIdCase
	 * @return String.
	 */
	public String getNmCase(Long IdCase);

	/**
	 * Method Name: insertTodo. Method Description: This method used to insert
	 * todo.
	 * 
	 * @param toDoValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertTodo(TodoDto todoDto);

	/**
	 * Method Name: fetchPADStageIdForADO Method Description: This method
	 * fetches idStage of PAD which has ADO stage as prior stage
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long fetchPADStageIdForADO(Long idStage);

	/**
	 * Returns the ulIdPerson of the Primary Worker of the stage. i;e the Person
	 * with role as PRIMARY ("PR").
	 * 
	 * @param ulIdStage
	 * @return
	 */

	public long getPrimaryWorkerIdForStage(Long ulIdStage);

	/**
	 * Get the open stages for the case. This method is called by ApprovalBean
	 * to determine if case would need secondary investigation.
	 * 
	 * @param idCase
	 * @return List<String>
	 * 
	 */
	public List<String> getOpenStagesForCase(Long idCase);

	/**
	 * Get the over all disposition for the investigation conclusion stage
	 * 
	 * @param idCase
	 * @param idStage
	 * @return String
	 * 
	 */
	public String queryOverallDispositionForInvst(long idCase, long idStage);

	/**
	 * Get various details of the stage which are required for secondary
	 * approval determination
	 * 
	 * @param idStage
	 * @return SecondaryApprovalViewDto
	 * 
	 */
	public SecondaryApprovalDto getStageDetails(long idStage);

	/**
	 * Check persons in stage to see if AP(DP) has rel/int as "School
	 * Personnel".
	 * 
	 * @param idStage
	 * @return List<SecondaryInvstDto>
	 * 
	 */

	public List<ApprovalCommonInDto> queryStageChildrenDOB(long idCase, long idStage);

	/**
	 * Find if the investigation is a school related investigation or not.
	 * 
	 * @param idStage
	 * @return boolean
	 * 
	 */
	public boolean isSchoolInvestigation(long idStage);

	/**
	 * Check persons in stage to see if AP(DP) has rel/int as "School
	 * Personnel".
	 * 
	 * @param idStage
	 * @return boolean
	 * 
	 */
	public boolean isSchoolPersonnelInvolved(long idStage);

	/**
	 * Get person info for this stage in the case. This method is called by
	 * ApprovalBean to determine if case would need secondary investigation.
	 * 
	 * @param idStage
	 * @return boolean
	 * 
	 */
	public boolean isChildDeath(long idStage);

	/**
	 * get Prior Stage
	 * 
	 * @param idStage
	 * @return Date
	 * 
	 */
	public Date getPriorStage(Long idStage);

	/**
	 * Method Name: getPendingApprovalCount (getCcmn56DO) Method
	 * Description:This function returns Count of the Pending Approvals for the
	 * given Approval Id.
	 * 
	 * @param approvalServiceDto
	 * @return ApprovalServiceOprnDto
	 * 
	 */
	public ApproveApprovalDto getCcmn56DO(SecondaryApprovalDto approvalServiceDto);

	/**
	 * 
	 * Method Name: getSelectCaseFileManagement Method Description:case file
	 * Management
	 * 
	 * @param caseFileMgmtValueBean
	 * @return CaseFileManagementDto
	 * @throws DataNotFoundException
	 */
	public CaseFileManagementDto getSelectCaseFileManagement(CaseFileManagementDto caseFileManagementDto)
			throws DataNotFoundException;

	/**
	 * 
	 * Method Name: insertCaseFileManagement Method Description:
	 * 
	 * @Return Long
	 * @param caseFileManagementDto
	 * @throws DataNotFoundException
	 */
	public Long insertCaseFileManagement(CaseFileManagementDto caseFileManagementDto) throws DataNotFoundException;

	/**
	 * Method Name: getARSafetyAssmt Method Description:This method is called
	 * from display method in SafetyAssmtConversation if the page has been
	 * previously saved. It retrieves back all the responses
	 * 
	 * @param idStage
	 * @param cdAssmtType
	 * @param idUser
	 * @return ARSafetyAssmtValueDto
	 * @throws DataNotFoundException
	 */
	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser)
			throws DataNotFoundException;

	/**
	 * Method Name: updateEventStatus Method Description: This method is used to
	 * update CD_EVENT_STATUS with provided idEvent and cdEventStatus status
	 * 
	 * @param idEvent
	 * @param cdEventStatus
	 * @return long
	 * 
	 */
	public long updateEventStatus(int idEvent, String cdEventStatus) throws DataNotFoundException;

	/**
	 * Method Name: updateFacilityIndicator MethodDescription:This DAM will
	 * update IND_FACIL_SUPERINT_NOTIF to'Y'on the FACILITY_INVST_DTL table
	 * whenever the Investigation Conclusion is approved. This needs to be done
	 * according to the design for the MHMR Enhancement for AFC
	 * Investigation"Waiting for Superintendent Comments".
	 * 
	 * @param saveApprovalStatusReq
	 * @throws DataNotFoundException
	 */
	public void updateFacilityIndicator(SaveApprovalStatusReq saveApprovalStatusReq);

	/**
	 * Method Name: getEmployeeInfo Method Description: Fetch the employee
	 * information based on the ID_PERSON of Approver We need JobClass and
	 * EmpConfirmedHrmis for storing it into Approver or Approval rejection
	 * table Dam Name: CSES00D for CCMN35S Service
	 * 
	 * @param idPerson
	 * @return EmployeeDetailDto
	 */
	public EmployeeDetailDto getEmployeeInfo(Long idPerson);

	/**
	 * Method Name:updateIndSecondApprover Method Description : This Method
	 * (CAUDK4D SIR 25379) if this request is from secondary approver, then we
	 * need to store this information on the STAGE table in IND_SECOND_APPROVER.
	 * Once a stage has been reviewed by second approver, this stage would not
	 * require secondary approval, even if other conditions are met.
	 * 
	 * DAM NAME : CAUDK4D SERVICE NAME : CCMN35S
	 * 
	 * @param saveApprovalStatusReq
	 * @return
	 */
	public void updateIndSecondApprover(SaveApprovalStatusReq saveApprovalStatusReq);

	/**
	 * Method Name:updateApprovers MethodDesccription: Executes the AUD Type DAM
	 * that will update the changed Approvers row on the Approvers table.
	 * Service : CCMN35S DAM NAME: CCMN61D
	 * 
	 * @param approvalStatusFacilityIndicatorDto
	 */
	public void updateApprovers(ApprovalStatusFacilityIndicatorDto approvalStatusFacilityIndicatorDto);

	/**
	 * Method Name: saveRejectionApproval Method Description: This method will
	 * update the APPROVAL_REJECTION table
	 * 
	 * DAM Name: CCMNI2D Service Name: CCMN35S
	 * 
	 * @param saveApprovalStatusReq
	 */
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto);

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
	 * Method Name: sessionFlush
	 * Method Description: This method is used to synchronize the data with the Database
	 *
	 */
	void sessionFlush();

	/**
	 * Artifact ID: artf151569
	 * Method Name: fetchCpsPriorOrProgressedStage
	 * Method Description: This method is used to retrieve the open Prior or Progressed stage based on the given cdStage
	 * and idStage
	 *
	 * @param idStage
	 * @param cdStage
	 * @param retrieveInv
	 * @return
	 */
	List<StageDto> fetchCpsPriorOrProgressedStage(Long idStage, String cdStage, boolean retrieveInv);

	/**
	 * Artifact ID: artf151569
	 * Method Name: retrieveFbssSdmRiskReassessment
	 * Method Description: This method is used to retrieve the latest SDM Risk Reassessment
	 *
	 * @param idStage
	 * @return
	 */
	SDMRiskReasmntDto retrieveFbssSdmRiskReassessment(Long idStage);

	/**
	 * Method Name: deleteAutEvent - Method Description: used to delete AUT event in COMP status when there is no header.
	 *
	 *
	 * @param ulIdCase
	 * @return String.
	 */
	public void deleteAutEvent(Long IdCase);


	/**
	 * Returns the ulIdPerson of the Primary Worker of the case. i;e the Person
	 * with role as PRIMARY ("PR").
	 *
	 * @param ulIdCase
	 * @return
	 */

	public long getPrimaryWorkerIdForCase(Long ulIdCase);


}
