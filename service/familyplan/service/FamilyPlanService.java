package us.tx.state.dfps.service.familyplan.service;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;
import us.tx.state.dfps.familyplan.response.FamilyPlanSaveResp;
import us.tx.state.dfps.service.common.request.FamilyPlanReq;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyTree.bean.TaskGoalValueDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

// TODO: Auto-generated Javadoc
/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FamilyPlanServiceImpl will implemented all operation defined in
 * FamilyPlanService Interface related FamilyPlan module. Mar 9, 2018- 2:02:21
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface FamilyPlanService {

	/**
	 * Method Name: getFamilyPlanAssmt Method Description:his service gathers
	 * the information needed to build the family assessment form and then call
	 * the architecture black box to produce the text delimited string passed to
	 * Word on the client side Service Name : CSVC03S.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return @
	 */
	public PreFillDataServiceDto getFamilyPlanAssmt(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: getFamilyPlanEval Method Description:Populates the Family
	 * Plan Evaluation form Service Name : csvc19s.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return @
	 */
	public PreFillDataServiceDto getFamilyPlanEval(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: getFamilyPlan Method Description: CSVC23S.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan
	 */
	public PreFillDataServiceDto getFamilyPlan(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: approveFamilyPlan Method Description: This method update the
	 * FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the current
	 * time stamp.
	 *
	 * @param approvalId
	 *            the approval id
	 * @param caseId
	 *            the case id
	 * @return Map
	 */
	public Map approveFamilyPlan(Long approvalId, Long caseId);

	/**
	 * Method Name: disApproveFamilyPlan Method Description: This method will
	 * disApprove the FamilyPlan ( DT_APPROVAL = NULL) in Task and Goal Table.
	 *
	 * @param input
	 *            the input
	 * @return Long
	 * 
	 *         the service exception
	 */
	public Long disApproveFamilyPlan(Map input);

	/**
	 * Method Name: updateFPTasksGoals Method Description: This method fetches
	 * all the approved tasks and goals for an event id and update the
	 * FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the current
	 * time stamp.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long
	 */
	public Long updateFPTasksGoals(Long idEvent);

	/**
	 * Method Name: getEventBean Method Description: This method will get
	 * details from Event table.
	 *
	 * @param eventId
	 *            the event id
	 * @return EventValueBeanDto
	 * 
	 *         the service exception
	 */
	public String getEventStatus(Long eventId);

	/**
	 * Method Name: getChildCandidacy Method Description: This method will get
	 * Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	public FamilyPlanRes getChildCandidacy(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: getChildCandidacy Method Description: This method will Save
	 * Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	public FamilyPlanRes saveChildCandidacy(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: queryFamilyPlan Method Description:Query the family plan
	 * details from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto
	 * @throws HibernateException
	 *             the hibernate exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public FamilyPlanDto queryFamilyPlan(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: fetchFamilyPlanDtl Method Description: Retrieves all family
	 * plan details.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return familyPlanDto
	 * 
	 *         the service exception
	 * @throws HibernateException
	 *             the hibernate exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */
	public FamilyPlanDto fetchFamilyPlanDtl(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: deleteFamilyPlan Method Description: Delete family plan
	 * based on Event ID.
	 *
	 * @param idEvent
	 *            the id event
	 */
	public void deleteFamilyPlan(Long idEvent);

	/**
	 * Method Name: queryFamilyPlanGoal Method Description: Query the Goals
	 * table to retrieve all the Goals associated with a Family Plan.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return the list
	 */
	public List<FamilyPlanGoalValueDto> queryFamilyPlanGoal(Long eventId, Long caseId);

	/**
	 * Method Name: queryFamilyPlanTask Method Description: This method will
	 * query the Family Plan task table to retrieve the task.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryFamilyPlanTask(Long eventId, Long caseId);

	/**
	 * Method Name: queryApprovedActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved active
	 * tasks.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryApprovedActiveTasks(Long eventId, Long caseId, Long currentIdEvent);

	/**
	 * Method Name: queryApprovedInActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved in active
	 * tasks.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryApprovedInActiveTasks(Long eventId, Long caseId, Long currentIdEvent);

	/**
	 * Method Name: queryFGDMFamilyGoal Method Description: This method is used
	 * to query the Family Plan Goal.
	 *
	 * @param idCase
	 *            the id case
	 * @param idEvent
	 *            the id event
	 * @return List<FamilyPlanGoalValueDto>
	 */
	public List<FamilyPlanGoalValueDto> queryFGDMFamilyGoal(Long idCase, Long idEvent);

	/**
	 * Method Name: getAreaOfConcernList Method Description: This method returns
	 * a list of Area of Concern for a family plan goal.
	 *
	 * @param idFamilyPlanGoal
	 *            the id family plan goal
	 * @return List<String>
	 */
	public List<String> getAreaOfConcernList(Long idFamilyPlanGoal);

	/**
	 * 
	 * Method Name: getFamilyPlanDetails Method Description: Gets the family
	 * plan details.
	 * 
	 * @param familyPlanReq
	 * @return
	 */
	public FamilyPlanDtlEvalRes getFamilyPlanDetails(FamilyPlanDtlEvalReq familyPlanReq);

	/**
	 * 
	 * Method Name: saveFamilyPlan Method Description: Saves family plan.
	 * 
	 * @param familyPlanDtlEvalReq
	 * @return
	 * 
	 */
	public FamilyPlanSaveResp saveFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * Method Name: getFamilyPlanFormDetails Method Description:This method get
	 * details required for family plan form.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return the family plan form details
	 */
	public PreFillDataServiceDto getFamilyPlanFormDetails(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * 
	 * Method Name: getFamilyPlanEvalFormDetails Method Description: gets the
	 * family plan evaluation information.
	 * 
	 * @param familyPlanDtlEvalReq
	 * @return
	 */
	public PreFillDataServiceDto getFamilyPlanEvalFormDetails(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * 
	 * Method Name: getFamilyPlanNarravtive Method Description: gets the family
	 * plan narrative information.
	 * 
	 * @param isFamilyPlanEval
	 * @param isFamilyPlanEval
	 * 
	 * @param familyPlanReq
	 * @return
	 */
	public FamilyPlanDtlEvalRes getFamilyPlanNarravtive(Long idStage, Long idEvent, boolean isFamilyPlanEval);

	/**
	 * Method Name: saveFamilyPlanLegacy Method Description:Saves the family
	 * plan details to the database. Creates a new family plan event or a new
	 * family plan evaluation event, if needed. Otherwise, updates the existing
	 * family plan event or family plan evaluation event. Returns the event id
	 * of the event being added or updated.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan res
	 */
	public FamilyPlanRes saveFamilyPlanLegacy(FamilyPlanDto familyPlanDto);

	/**
	 * 
	 * Method Name: getFamilyPlanEvalAddMode Method Description: fetches the
	 * information required to add the family plan evaluation.
	 * 
	 * @param familyPlanDto
	 * @return
	 */
	public FamilyPlanDto getFamilyPlanEvalAddMode(FamilyPlanDto familyPlanDto);

	/**
	 * 
	 * Method Name: fetchPrincipalsRiskOfRemoval Method Description: Fetch
	 * principals risk of removal.
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<Long> fetchPrincipalsRiskOfRemoval(Long idEvent);

	/**
	 * 
	 * Method Name: getCountFCCComplete Method Description: Gets the count FCC
	 * complete.
	 * 
	 * @param idEvent
	 * @return
	 */
	public Long getCountFCCComplete(Long idEvent);

	/**
	 * 
	 * Method Name: getListChildInFCC Method Description: Gets the list child in
	 * FCC.
	 * 
	 * @param familyPlanReq
	 * @return
	 */
	public FamilyPlanRes getListChildInFCC(FamilyPlanReq familyPlanReq);

	/**
	 * 
	 * Method Name: fetchAllChildCandidacy Method Description: fetches all the
	 * child candidacy information.
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<FamilyPlanDto> fetchAllChildCandidacy(Long idEvent);

	/**
	 * 
	 * Method Name: queryGoalsAndActiveTasksForItem Method Description: gets the
	 * goals and active tasks for item.
	 * 
	 * @param familyPlanItemDto
	 * @return
	 */
	FamilyPlanItemDto queryGoalsAndActiveTasksForItem(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * 
	 * Method Name: setAllTasksNotLinkedToGoals Method Description: Sets the all
	 * tasks not linked to goals.
	 * 
	 * @param familyPlanItemDto
	 */
	void setAllTasksNotLinkedToGoals(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: saveSubmitFamilyPlanLegacy Method Description: This method
	 * saves the information and do further validations to continue save and
	 * submit.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param familyPlanRes
	 *            the family plan res
	 * @return the family plan res
	 */
	FamilyPlanRes saveSubmitFamilyPlanLegacy(FamilyPlanDto familyPlanDto, FamilyPlanRes familyPlanRes);

	/**
	 * Method Name: savePermanencyGoals Method Description: Service for saving
	 * Child Permanency Goal for the family plan.
	 * 
	 * @param familyPlanDto
	 * @return FamilyPlanRes
	 */
	FamilyPlanRes savePermanencyGoals(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: saveFamilyPlanTask Method Description: Saves the Task
	 * related information into the Task Table.
	 *
	 * @param familyPlanGoalValueDtos
	 *            - list of tasks
	 * @param idEvent
	 *            -event id
	 * @param idCase
	 *            - case id
	 * @param currentEventId
	 * @param familyPlanDto
	 *            - family plan task detail
	 * @param handwritingFieldsToBeDeleted
	 *            - handwriting Fields ToBe Deleted
	 * @return FamilyPlanRes - family plan task response
	 */
	public FamilyPlanRes saveFamilyPlanTask(List<TaskGoalValueDto> familyPlanGoalValueDtos, Long idEvent, Long idCase,
			Long currentEventId, FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: deleteFamilyPlanTask Method Description: Deletes the
	 * selected Family Plan Task.
	 * 
	 * @param familyPlanDto,familyPlanItemDto,
	 *            indexOfTaskToDelete
	 * @return FamilyPlanRes
	 */
	public FamilyPlanRes deleteFamilyPlanTask(FamilyPlanDto familyPlanDto, FamilyPlanItemDto familyPlanItemDto,
			Integer indexOfTaskToDelete);

	/**
	 * Method Name: deleteNewFamilyPlanTask Method Description: Delete a row
	 * from the family_plan_task,FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param idFamilyPlanTask
	 *            - id key for the family_plan_task
	 * @param familyPlanDto
	 *            - family plan task detail
	 * @return FamilyPlanRes - family plan task response
	 */
	public FamilyPlanRes deleteNewFamilyPlanTask(Long idFamilyPlanTask, FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: saveOrUpdateFamPlanGoal Method Description: to save or
	 * update family goal
	 * 
	 * @param familyPlanDto
	 * @param idCase
	 * @param idEvent
	 * @return String
	 */
	FamilyPlanRes saveOrUpdateFamPlanGoal(FamilyPlanDto familyPlanDto, Long idEvent, Long idCase);

	/**
	 * Method Name: deleteFamPlanGoal Method Description: to delete family goal
	 * 
	 * @param idGoal
	 * @param familyPlanDto
	 * @return String
	 */
	String deleteFamPlanGoal(Long idGoal, FamilyPlanDto familyPlanDto);

	/**
	 *Method Name:	getFamilyPlanVersions
	 *Method Description: This method is used to getFamilyPlanVersions
	 *@param idEvent
	 *@return FamilyPlanDtlEvalRes
	 */
	public FamilyPlanDtlEvalRes getFamilyPlanVersions(Long idEvent);

}
