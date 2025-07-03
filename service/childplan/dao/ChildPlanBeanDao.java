package us.tx.state.dfps.service.childplan.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.childplan.dto.ChildPlanEventDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegacyDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanOfServiceDtlDto;
import us.tx.state.dfps.service.childplan.dto.CurrentlySelectedPlanDto;
import us.tx.state.dfps.service.common.request.ChildPlanReq;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.subcare.dto.ChildPlanGuideTopicDto;
import us.tx.state.dfps.service.subcare.dto.StaffSearchResultDto;

public interface ChildPlanBeanDao {
	/**
	 * 
	 * Method Name: selectGuideTopic Method Description: Queries the specified
	 * guide topic from the database.
	 * 
	 * @param guideTopicValueDto
	 * @param childPlanReq
	 * @return ChildPlanGuideTopicValueBeanDto
	 * @throws DataNotFoundException
	 */
	public ChildPlanGuideTopicDto selectGuideTopic(ChildPlanGuideTopicDto guideTopicValueDto,
			ChildPlanReq childPlanReq);

	/**
	 * 
	 * Method Name: queryPlanTypeCode Method Description: Query the plan type
	 * code for the given child plan.
	 * 
	 * @param caseId
	 * @param eventId
	 * @return String @
	 */
	public String selectPlanTypeCode(Long caseId, Long eventId);

	/**
	 *
	 * Method Name: queryPlanTypeCode Method Description: Query the plan type
	 * code for the given child plan.
	 *
	 * @param eventId
	 * @return String @
	 */
	public String selectPlanTypeCode(Long eventId);
	/**
	 * 
	 * Method Name: selectPersonsInStage Method Description: Queries the
	 * specified guide topic from the database.
	 * 
	 * @param childPlanReq
	 * @return List<PersonValueDto>
	 * @throws DataNotFoundException
	 */
	public List<PersonValueDto> selectPersonsInStage(ChildPlanReq childPlanReq);

	/**
	 * 
	 * Method Name: checkForUnapprovedChildPlanCreatedInIMPACT Method
	 * Description:Checks to see if the given stage has any unapproved child
	 * plans that were created in IMPACT
	 * 
	 * @param caseId
	 * @param stageId
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean checkForUnapprovedChildPlanCreatedInIMPACT(Long caseId, Long stageId);

	/**
	 *
	 * @param blobData
	 * @return String
	 * @throws DataNotFoundException
	 */
	// public String unwrapBlob(byte[] blobData) ;

	/**
	 * 
	 * Method Name: saveGuideTopic Method Description: Saves the guide topic
	 * data to the database.
	 * 
	 * @param guideTopicValueDto
	 * @return ChildPlanGuideTopicValueBeanDto
	 * @throws DataNotFoundException
	 */
	public Long saveGuideTopic(ChildPlanGuideTopicDto guideTopicValueDto);

	/**
	 * 
	 * Method Name: getTopicDtLastUpdate Method Description: Saves the guide
	 * topic data to the database.
	 * 
	 * @param valueDto
	 * @return ChildPlanGuideTopicValueBeanDto
	 * @throws DataNotFoundException
	 */
	public ChildPlanGuideTopicDto getTopicDtLastUpdate(ChildPlanGuideTopicDto valueDto);

	/**
	 * 
	 * Method Name: checkIfEventIsLegacy Method Description: Queries a row from
	 * the EVENT_PLAN_LINK for the given event id to determine whether or not
	 * the event is a legacy event--one created before the initial launch of
	 * IMPACT.
	 * 
	 * @param eventId
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean checkIfEventIsLegacy(Long eventId);

	/**
	 *
	 * @param text
	 * @return byte[]
	 * @throws DataNotFoundException
	 */
	// public byte[] wrapBlob(String text) ;

	/**
	 * 
	 * Method Name: addEventPlanLinkRow Method Description: Inserts a new row
	 * into the EVENT_PLAN_LINK table to indicate that the child plan was
	 * created using IMPACT.
	 * 
	 * @param eventId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long addEventPlanLinkRow(Long eventId);

	/**
	 * 
	 * Method Name: checkCrimHist Method Description:This method gets idPerson,
	 * if the Criminal History Action is null for the given Id_Stage.
	 * 
	 * @param idStage
	 * @return Long
	 */
	public Long checkCrimHist(Long idStage);

	/**
	 * 
	 * Method Name: updateChildPlanPriorAdopInfo Method Description: This method
	 * updates the ChildPlanBean with Prior Adoption Information
	 * 
	 * @param primaryChildPersonId
	 * @return ChildPlanRes
	 */
	public void updateChildPlanPriorAdopInfo(Long primaryChildPersonId, ChildPlanLegacyDto childPlanLegacyDto);

	/**
	 * 
	 * Method Name: getDtLastUpdatePrimaryChild Method Description: This method
	 * is retrieves the DT_LAST_UPDATE of the person_dtl information for the
	 * Primary child
	 * 
	 * @param primaryChildPersonId
	 * @return Date
	 */
	public Date getDtLastUpdatePrimaryChild(Long primaryChildPersonId);

	/**
	 * 
	 * Method Name: getLegalStatusInformation Method Description: This method
	 * retrieves the Legal status info of the Primary child
	 * 
	 * @param primaryChildPersonId
	 * @return List<LegalStatusDto>
	 * @throws DataNotFoundException
	 */
	public List<LegalStatusDto> getLegalStatusInformation(Long primaryChildPersonId);

	/**
	 * 
	 * Method Name: saveChildPriorAdoption Method Description: This method save
	 * the Prior Adoption information
	 * 
	 * @param childPlanLegacyDto
	 * @param primaryChildPersonId
	 * @param dtLastUpdatePrimaryChild
	 * @return Long
	 */
	public Long saveChildPriorAdoption(ChildPlanLegacyDto childPlanLegacyDto);

	/**
	 * 
	 * Method Name: getBirthDate Method Description: This method returns the
	 * Birth date of the Primary Child
	 * 
	 * @param primaryChildPersonId
	 * @return Date
	 */
	public Date getBirthDate(Long primaryChildPersonId);

	/**
	 * 
	 * Method Name: getStaffSearchResultInformation Method Description: Returns
	 * the list of Primary and Secondary case workers
	 * 
	 * @param stageId
	 * @return List<StaffSearchResultDto>
	 * @throws DataNotFoundException
	 */
	public List<StaffSearchResultDto> getStaffSearchResultInformation(Long stageId);

	/**
	 * 
	 * Method Name: isChildPlanExistForEvent Method Description: to check for
	 * Child Plan By Id Event
	 * 
	 * @param idEvent
	 * @return Boolean
	 * @throws DataNotFoundException
	 */
	public Boolean isChildPlanExistForEvent(Long idEvent);

	/**
	 * 
	 * Method Name: selectChildPlansForPID Method Description: idChildPlanEvent
	 * for Child Plan By Id Person
	 * 
	 * @param idEvent
	 * @return List<Long>
	 * @throws DataNotFoundException
	 */
	public List<Long> selectChildPlansForPID(Long idEvent);

	/**
	 * Unwrap blob.
	 *
	 * @param blobData
	 *            the blob data
	 * @return the string
	 */
	public String unwrapBlob(byte[] blobData);

	/**
	 *
	 * @param idStage
	 * @return String
	 * @throws DataNotFoundException
	 */
	// public String selectMostRecentChildPlanPermGoal(int idStage)

	/**
	 *
	 * @param idPerson1
	 * @param idPerson2
	 * @return ArrayList
	 * @throws DataNotFoundException
	 */
	// public ArrayList fetchOpenStageChildPlanWithParticipants(int
	// idPerson1,int
	// idPerson2)

	/**
	 * 
	 * Method Name: getChildPlanEvents Method Description:This method to get
	 * child plans with Conservatorship removal date
	 * 
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	public List<ChildPlanOfServiceDtlDto> getChildPlanEvents(Long idStage, Long idCase);

	/**
	 * Method Name: deleteTopicTable Method Description:This Method is used to
	 * delete the child plan based on The event id of the child plan.
	 * 
	 * @param idEvent
	 * @param List<CurrentlySelectedPlanDto>
	 */
	public void deleteTopicTable(Long idEvent, List<CurrentlySelectedPlanDto> childPlanList);

	/**
	 * Method Name: deleteChildPlanParticip Method Description:This Method is
	 * used to delete Child Plan Particip based on The event id of the child
	 * plan.
	 * 
	 * @param idEvent
	 */
	public void deleteChildPlanParticip(Long idEvent);

	/**
	 * Method Name: deleteChildPlanItem Method Description:This Method is used
	 * to delete Child Plan Item based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	public void deleteChildPlanItem(Long idEvent);

	/**
	 * Method Name: deleteTodo Method Description:This Method is used to delete
	 * Todo based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	public void deleteTodo(Long idEvent);

	/**
	 * Method Name: deleteChildPlan Method Description:This Method is used to
	 * delete Child Plan based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	public void deleteChildPlan(Long idEvent);

	/**
	 * Method Name: deleteCpConcurrentGoal Method Description:This Method is
	 * used to delete Cp Concurrent Goal based on The event id of the child
	 * plan.
	 * 
	 * @param idEvent
	 */
	public void deleteCpConcurrentGoal(Long idEvent);

	/**
	 * Method Name: deleteCpConcurrentGoal Method Description:This Method is
	 * used to delete Event Plan Link based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	public void deleteEventPlanLink(Long idEvent);

	/**
	 * Method Name: deleteEventPersonlink Method Description:This Method is used
	 * to delete Event person Link based on The event id of the child plan.
	 * 
	 * @param idEvent
	 */
	public void deleteEventPersonlink(Long idEvent);

	/**
	 * Method Name: childPlanAud Method Description: This method will add,
	 * update, & delete a row from the child plan
	 * 
	 * @param childPlanEventDto
	 * @param cdReqFunc
	 */
	public void childPlanAud(ChildPlanEventDto childPlanEventDto, String cdReqFunc);

	/**
	 * Method Name: deleteCpConcurrentGoalById Method Description: This method
	 * is used to deleteCpConcurrentGoalById
	 * 
	 * @param idCpConGoal
	 */
	public void deleteCpConcurrentGoalById(Long idCpConGoal);

	/**
	 * Method Name: addCpConcurrentGoal Method Description: This method is used
	 * to addCpConcurrentGoal
	 * 
	 * @param cdConcurrentGoal
	 * @param childPlanEventDto
	 * @param idCase
	 * @param idStage
	 */
	public void addCpConcurrentGoal(String cdConcurrentGoal, ChildPlanEventDto childPlanEventDto, Long idCase,
			Long idStage);

	/**
	 * Method Name: deleteSuperVisionDtl Method Description: This method
	 * is used to delete CP_SPRVSN_DTL
	 *
	 * @param idEvent
	 */
	public void deleteSuperVisionDtl(Long idEvent);

	public void deleteCpTranstnAdultBlwDtl(Long idEvent);

	public void deleteSocialRecreationalDtl(Long idEvent);

	public void deleteChildFamilyTeamDtl(Long idEvent);

	public void deleteBehaviouralDtl(Long idEvent);

	public void deleteYouthPregntDtl(Long idEvent);

	public void deleteIntlCtlDevelopment(Long idEvent);

	public void deleteTreatementServiceDtl(Long idEvent);

	public void deleteAdultAboveDtl(Long idEvent);

	public void deleteHighRiskBehavourDtl(Long idEvent);

	public void deleteCPInformation(Long idEvent);

	public void deleteCPEducationDtl(Long idEvent);

	public void deleteCPEmtnlThrptcDtl(Long idEvent);

	public void deleteMedCtnDtl(Long idEvent);

	public void deleteCPHealthCareSumm(Long idEvent);

	public void deleteSSCCChildPlan(Long idEvent);

	public void deleteVisitCntFmly(Long idEvent);

	public void deleteQrtpPtm(Long idEvent);

	public void deleteAdtnlSctnDtls(Long idEvent);

	public void deleteCpLstGoals(Long idEvent);

	public void deleteCpAdoptnDtl(Long idEvent);

	public void deleteLegalGardianShip(Long idEvent);
}
