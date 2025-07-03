package us.tx.state.dfps.service.familyplan.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.FamilyPlan;
import us.tx.state.dfps.common.domain.FamilyPlanEval;
import us.tx.state.dfps.common.domain.FamilyPlanNeed;
import us.tx.state.dfps.common.domain.FamilyPlanPartcpnt;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.common.dto.FamAssmtDto;
import us.tx.state.dfps.common.dto.FamAssmtFactorDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.common.dto.ServicePlanEvalDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanActnRsrcDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNarrDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNeedsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanPartcpntDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanReqrdActnsDto;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.CapsEmailDto;
import us.tx.state.dfps.service.common.request.FamilyPlanReq;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyTree.bean.TaskGoalValueDto;
import us.tx.state.dfps.service.familyplan.dto.PrincipalParticipantDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.forms.dto.RiskAreaLookUpDto;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FamilyPlanDao define all operation defined which is implemented
 * in FamilyPlanDaoImpl. March 8, 2018- 2:02:21 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FamilyPlanDao {

	/**
	 * Method Name: getFamilyAssmt Method Description:This method retrieves a
	 * full row from the Family_Assmt table DAM Name : CSES05D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the family assmt
	 */
	public FamAssmtDto getFamilyAssmt(Long idEvent);

	/**
	 * Method Name: getFamAssmtFactors Method Description: This method selects n
	 * full rows from the Fam_Assmt_Fctrs table using Id_Fam_Assmt_Event and
	 * Cd_Fam_Assmt_Subject DAM Name: CLSS17D.
	 *
	 * @param idFamAssmtEvent
	 *            the id fam assmt event
	 * @param cdFamAssmtSubject
	 *            the cd fam assmt subject
	 * @return the fam assmt factors
	 */
	public List<FamAssmtFactorDto> getFamAssmtFactors(Long idFamAssmtEvent, String cdFamAssmtSubject);

	/**
	 * Method Name: getServicePlanEvalDtl Method Description:This method does a
	 * retrieval of the SERVICE_PLAN_EVAL_DTL table given an event id for the
	 * Family Plan Eval Event DAM Name : CSVC23D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the service plan eval dtl
	 */
	public ServicePlanEvalDto getServicePlanEvalDtl(Long idEvent);

	/**
	 * Method Name: getServicePlanByIdEvent Method Description:This method does
	 * a full row retrieval of SERVICE_PLAN table based on ID_SVC_PLAN_EVENT DAM
	 * Name : CSVC04D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the service plan by id event
	 */
	public ServicePlanDto getServicePlanByIdEvent(Long idEvent);

	/**
	 * Method Name: getServPlanEvalRec Method Description: Retrieve Service Plan
	 * Eval Item records for a given PLAN EVENT DAM name : CLSC07D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the serv plan eval rec
	 */
	public List<ServPlanEvalRecDto> getServPlanEvalRec(Long idEvent);

	/**
	 * Method Name: getRiskAreaLookUp DAM Name = CSVC42D Method Description:Full
	 * Row retrieval from RISK_AREA_LOOKUP RAL and FAMILY_PLAN_ITEM FPI based on
	 * ID_EVENT.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the risk area look up
	 */

	public List<RiskAreaLookUpDto> getRiskAreaLookUp(Long idEvent);

	/**
	 * Method Name: getFamilyPlanItemGoal DAM Name : CSVC39D Method Description:
	 * Queries the FAMILY_PLAN_ITEM table for the goals.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the family plan item goal
	 */
	public List<FamilyPlanItemDto> getFamilyPlanItemGoal(Long idEvent);

	/**
	 * Method Name: getFamilyPlanItems DAM Name: CSVC44D Method Description:
	 * Queries FAMILY_PLAN_ITEM table.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the family plan items
	 */
	public List<FamilyPlanItemDto> getFamilyPlanItems(Long idEvent);

	/**
	 * Method Name: getTaskForSpecRiskArea Method Description:Queries the
	 * FAMILY_PLAN_TASK table to determine if there are any task for the
	 * specific Risk Area DAM Name: CSVC45D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the task for spec risk area
	 */
	public List<FamilyPlanItemDto> getTaskForSpecRiskArea(Long idEvent);

	/**
	 * Method Name: updateFamilyPlanItems Method Description: updated the family
	 * plan items.
	 *
	 * @param familyPlanItemDtoList
	 *            the family plan item dto list
	 * @param reject
	 *            the reject
	 */
	public void updateFamilyPlanItems(List<FamilyPlanItemDto> familyPlanItemDtoList, int reject);

	/**
	 * Method Name: updateGoals Method Description: Update the goals table with
	 * DT_APPROVED.
	 *
	 * @param familyPlanGoalValueDtoList
	 *            the family plan goal value dto list
	 * @param reject
	 *            the reject
	 */
	public void updateGoals(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList, int reject);

	/**
	 * Method Name: updateTasks Method Description: Update Tasks for DT_APPROVED
	 * column.
	 *
	 * @param taskGoalValueDtoList
	 *            the task goal value dto list
	 * @param reject
	 *            the reject
	 * @return Long
	 */
	public Long updateTasks(List<TaskGoalValueDto> taskGoalValueDtoList, int reject);

	/**
	 * Method Name: isEventIdFamilyPlanEval Method Description: Returns true is
	 * the input event corresponds to a Family Plan Evaluation.
	 *
	 * @param idEvent
	 *            the id event
	 * @return boolean
	 */
	public boolean isEventIdFamilyPlanEval(Long idEvent);

	/**
	 * Method Name: queryApprovedGoals Method Description: Fetches all approved
	 * family plan goals.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<FamilyPlanGoalValueDto>
	 */
	public List<FamilyPlanGoalValueDto> queryApprovedGoals(Long idEvent);

	/**
	 * Method Name: queryApprovedTasks Method Description: Fetch all approved
	 * tasks associated with a event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryApprovedTasks(Long idEvent);

	/**
	 * Method Name: getEventInfo Method Description: This method will get
	 * details from Event table.
	 *
	 * @param eventId
	 *            the event id
	 * @return Event
	 */
	public Event getEventInfo(Long eventId);

	/**
	 * Method Name: getEventIdFromAppEventLink Method Description: This method
	 * will get details from Event table for the given approval Id.
	 *
	 * @param approvalId
	 *            the approval id
	 * @return EventValueBeanDto
	 */
	public List<Long> getEventIdFromAppEventLink(Long approvalId);

	/**
	 * Method Name: isEventIdFamilyPlan Method Description: This method will
	 * determine if a family plan exist for the given event id.
	 *
	 * @param eventId
	 *            the event id
	 * @return Boolean
	 */
	public Boolean isEventIdFamilyPlan(Long eventId);

	/**
	 * Method Name: getFamilyPlanEventId Method Description: This method will
	 * get family plan event id for the given event Id.
	 *
	 * @param eventId
	 *            the event id
	 * @return List of Event id's
	 */
	public Long getFamilyPlanEventId(Long eventId);

	/**
	 * Method Name: queryFamilyPlanItems Method Description: This method will
	 * get family plan Item object for the given eventId and caseId.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List of FamilyPlanItem
	 */
	public List<FamilyPlanItemDto> queryFamilyPlanItems(Long eventId, Long caseId);

	/**
	 * Method Name: queryAllGoals Method Description: This method will get all
	 * goals for the given event id and case id.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List of Event id's
	 */
	public List<FamilyPlanGoalValueDto> queryAllGoals(Long eventId, Long caseId);

	/**
	 * Method Name: queryTasksNotApproved Method Description: This method will
	 * get all the task for the given list of Family Plan Goals.
	 *
	 * @param familyPlanGoalValueDtoList
	 *            the family plan goal value dto list
	 * @param caseId
	 *            the case id
	 * @return List of TaskGoalValueDto
	 */
	public List<TaskGoalValueDto> queryTasksNotApproved(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList,
			Long caseId);

	/**
	 * Method Name: queryWorkloadForSecondaryWorker Method Description: This
	 * method will get the worker ids for given family plan event id.
	 *
	 * @param familyPlanEventId
	 *            the family plan event id
	 * @return List of TaskGoalValueDto
	 */
	public List<Long> queryWorkloadForSecondaryWorker(Long familyPlanEventId);

	/**
	 * Method Name: queryToDo Method Description: This method will get the
	 * worker ids for given family plan event id.
	 *
	 * @param eventIdToBeAproved
	 *            the event id to be aproved
	 * @return Todo
	 */
	public Todo queryToDo(Long eventIdToBeAproved);

	/**
	 * Method Name: queryToDo Method Description: This method will get the
	 * Worker.Employee details by worker/person id
	 *
	 * @param secondaryWorkerId
	 *            the secondary worker id
	 * @return Employee
	 */
	public String getWorkersName(Long secondaryWorkerId);

	/**
	 * Method Name: addToDoAlertForSecondaryWorker Method Description: This
	 * method will insert a todo Object.
	 *
	 * @param todoToAdd
	 *            the todo to add
	 * @param workerId
	 *            the worker id
	 * @param familyPlanEventId
	 *            the family plan event id
	 * @return void
	 */
	public void addToDoAlertForSecondaryWorker(Todo todoToAdd, Long workerId, Long familyPlanEventId);

	/**
	 * Method Name: deleteFamilyPlan Method Description: Delete family plan
	 * based on Event ID.
	 *
	 * @param idEvent
	 *            the id event
	 */
	public void deleteFamilyPlan(Long idEvent);

	/**
	 * Method Name: fetchPrincipalsForPlan Method Description: Fetches the
	 * principals for the family plan from EVENT_PERSON_LINK based on Event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the array list
	 */
	public ArrayList<Long> fetchPrincipalsForPlan(Long idEvent);

	/**
	 * Method Name: fetchChildNotInSubcare Method Description: Fetches the child
	 * 1)Whose marital status is "Child Not applicable" 2)Who may have a
	 * placement but with living arrangement of "Return Home" or "Non custodial"
	 * 3)Who does not have date of death 4)Age < 18 5)Should not be a birth
	 * parent.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idCase
	 *            the id case
	 * @param idStage
	 *            the id stage
	 * @return FamilyPlanDto
	 */
	public FamilyPlanDto fetchChildNotInSubcare(Long idPerson, Long idCase, Long idStage);

	/**
	 * Method Name: fetchChildCandidacyRS Method Description: Fetches the risk
	 * of removal and child status from FAMILYCHILDCANDIDACY.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idEvent
	 *            the id event
	 * @return FamilyPlanDto
	 */
	public FamilyPlanDto fetchChildCandidacyRS(Long idPerson, Long idEvent);

	/**
	 * Method Name: fetchChildCandidacy Method Description: Fetches the IDPERSON
	 * from FAMILYCHILDCANDIDACY based on the EventId.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<FamilyChildCandidacy>
	 */
	public List<FamilyPlanDto> fetchAllChildCandidacy(Long idEvent);

	/**
	 * Method Name: saveChildCandidacy Method Description: This method saves the
	 * child candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan res
	 */
	public FamilyPlanRes saveChildCandidacy(FamilyPlanReq familyPlanReq);

	/**
	 * Method Name: queryLegacyEvents Method Description: Given the event id of
	 * a legacy family plan, family plan evaluation, or family assessment,
	 * queries all family plan, family plan evaluation and family assessment
	 * events for the same stage.
	 *
	 * @param eventId
	 *            the event id
	 * @return List<EventValueBeanDto>
	 */
	public List<EventValueDto> queryLegacyEvents(Long eventId);

	/**
	 * Method Name: queryEvent Method Description: Queries event information for
	 * the given event id.
	 *
	 * @param eventId
	 *            the event id
	 * @return EventValueBeanDto
	 */
	public EventValueDto queryEvent(Long eventId);

	/**
	 * Method Name: checkIfEventIsLegacy Method Description: This method checks
	 * the given event id to determine whether or not the event was created
	 * before the initial launch of IMPACT.
	 *
	 * @param idEvent
	 *            the id event
	 * @return boolean
	 */
	public boolean checkIfEventIsLegacy(Long idEvent);

	/**
	 * Method Name: queryFamilyPlan Method Description: Query the family plan
	 * details from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto
	 */
	public FamilyPlanDto queryFamilyPlan(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: queryFamilyPlanItem Method Description: Query the family
	 * plan item details from the database.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return FamilyPlanItemDto
	 */
	public FamilyPlanItemDto queryFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: queryFamilyPlanEvalItems Method Description: Query the
	 * Family Plan Evaluation Items from the database.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return FamilyPlanItemDto
	 */
	public FamilyPlanItemDto queryFamilyPlanEvalItems(FamilyPlanItemDto familyPlanItemDto);

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
	 * Method Name: queryFamilyPlanGoal Method Description: Select List of all
	 * Goals associated with a particular Family Plan event.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return familyPlanGoalValueDtoList
	 */
	public List<FamilyPlanGoalValueDto> queryFamilyPlanGoal(Long eventId, Long caseId);

	/**
	 * Method Name: queryTaskGoalLink Method Description: This method will query
	 * the task goal link table.
	 *
	 * @param taskGoalValueDtos
	 *            the task goal value dtos
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryTaskGoalLink(List<TaskGoalValueDto> taskGoalValueDtos, Long eventId,
			Long caseId);

	/**
	 * Method Name: queryApprovedActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved active
	 * task list.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param CurrIdEvent
	 *            the curr id event
	 * @return List<TaskGoalValueDto>
	 */
	public List<TaskGoalValueDto> queryApprovedActiveTasks(Long eventId, Long caseId, Long CurrIdEvent);

	/**
	 * Method Name: queryApprovedInActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved list of
	 * inactive tasks.
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
	 * Method Name: queryFamilyPlanEvaluations Method Description: Query the
	 * Family Plan evaluations from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto
	 */
	public FamilyPlanDto queryFamilyPlanEvaluations(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: queryPrincipalsForStage Method Description: Query all
	 * principals (PRN) associated with this Stage.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PersonValueDto>
	 */
	public List<PrincipalParticipantDto> queryPrincipalsForStage(EventDto mostRecentEvent);

	/**
	 * Method Name: queryInvestigationStageId Method Description: Query the
	 * ID_STAGE of the investigation stage that led to the creation of the given
	 * family plan stage.
	 *
	 * @param idstage
	 *            the idstage
	 * @return Long
	 */
	public Long queryInvestigationStageId(Long idstage);

	/**
	 * Method Name: queryFamilyPlanEventId Method Description: Queries the event
	 * id of the family plan to which the given evaluation belongs.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long
	 */

	public Long queryFamilyPlanEventId(Long idEvent);

	/**
	 * Method Name: queryFamilyPlanRole Method Description: Queries the Family
	 * Plan Roles for the event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<String>
	 */

	public List<String> queryFamilyPlanRole(Long idEvent);

	/**
	 * Method Name: querySelectedParticipants Method Description: Queries the
	 * Selected Participants for the event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<PersonDto>
	 */

	public List<PersonDto> querySelectedParticipants(Long idEvent);

	/**
	 * Method Name: querySelectedPrincipals Method Description: Queries the
	 * Seelected principals for the event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<PersonDto>
	 */

	public List<PersonDto> querySelectedPrincipals(Long idEvent);

	/**
	 * Method Name: getFirstParticipant Method Description: get primary
	 * participant id for family plan.
	 *
	 * @param id
	 *            the id
	 * @param isFamilyPlan
	 *            the is family plan
	 * @return PersonValueDto
	 */
	public PersonValueDto getFirstParticipant(Long id, Boolean isFamilyPlan);

	/**
	 * Gets the event assessment.
	 *
	 * @param idEvent
	 *            the id event
	 * @param Operation
	 *            the operation
	 * @return the event assessment
	 */
	public EventDto getEventAssessment(Long idEvent, String Operation);

	/**
	 * Method Name: saveFamilyPlanActnRsrc Method Description: saves the
	 * FamilyPlanActnRsrc.
	 *
	 * @param familyPlanActnRsrcDto
	 *            the family plan actn rsrc dto
	 * @return void
	 */

	public void saveFamilyPlanActnRsrc(FamilyPlanActnRsrcDto familyPlanActnRsrcDto);

	/**
	 * Method Name: saveFamilyPlanNeed Method Description: saves the Family Plan
	 * Action Resource.
	 *
	 * @param familyPlanNeedsDto
	 *            the family plan needs dto
	 * @return void
	 */
	public void saveFamilyPlanNeed(FamilyPlanNeedsDto familyPlanNeedsDto);

	/**
	 * Method Name: saveFamilyPlanReqrdActn Method Description: saves the Family
	 * Plan Required Action.
	 *
	 * @param familyPlanReqrdActnsDto
	 *            the family plan reqrd actns dto
	 * @return void
	 */
	public void saveFamilyPlanReqrdActn(FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto);

	/**
	 * Method Name: saveFamilyPlan Method Description: saves the Family Plan.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return idFamilyPlanDtlEval - Id of the respective Family Plan or
	 *         Evaluation saved/updated
	 */
	public Long saveFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * Method Name: updateFamilyPlan Method Description: saves the Family Plan.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return void
	 */
	public Long updateFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * Method Name: queryPrincipalsForEvent Method Description: Query all
	 * principals (PRN) associated with this event.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PersonValueDto>
	 */
	public List<PersonValueDto> queryPrincipalsForEvent(EventDto mostRecentEvent);

	/**
	 * Method Name: getFamilyPlanFormDtl Method Description: This method fetches
	 * the FamilyPlan or Family Plan Evaluation Details for the idEvent.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return Object
	 */
	public Object getFamilyPlanFormDtl(FamilyPlanDtlEvalReq familyPlanDtlEvalReq);

	/**
	 * Method Name: queryPrincipalsForEvent Method Description: Query all
	 * principals (PRN) associated with this event for which IND_FAM_PLAN_PART
	 * is Y.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PersonValueDto>
	 */
	public List<PersonValueDto> queryPrincipalsSelectedForEvent(EventDto mostRecentEvent);

	/**
	 * Method Name: updateFamilyPlanPartcpnt Method Description:This method is
	 * used to add or update the Family Plan Participant.
	 *
	 * @param familyPlanPartcpntDto
	 *            the family plan partcpnt dto
	 * @param familyPlanPartcpntAddList
	 *            the family plan partcpnt add list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	public void updateFamilyPlanPartcpnt(FamilyPlanPartcpntDto familyPlanPartcpntDto,
			List<FamilyPlanPartcpnt> familyPlanPartcpntAddList, List<FamilyPlanPartcpnt> familyPlanPartcpntList,
			FamilyPlan familyPlan, FamilyPlanEval familyPlanEval, Long idUser);

	/**
	 * Method Name: updateFamilyPlanNeed Method Description:This method is used
	 * to add or update Family Plan Needs.
	 *
	 * @param familyPlanNeedsDto
	 *            the family plan needs dto
	 * @param familyPlanNeedAddList
	 *            the family plan need add list
	 * @param familyPlanNeedsList
	 *            the family plan needs list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	public void updateFamilyPlanNeed(FamilyPlanNeedsDto familyPlanNeedsDto, List<FamilyPlanNeed> familyPlanNeedAddList,
			List<FamilyPlanNeed> familyPlanNeedsList, FamilyPlan familyPlan, FamilyPlanEval familyPlanEval,
			Long idUser);

	/**
	 * Method Name: populateFamilyPlanPartcpntDeleteList Method Description:This
	 * method Populates the Delete list for Family Plan Participants.
	 *
	 * @param familyPlanPartcpntDtoList
	 *            the family plan partcpnt dto list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @return void
	 */

	public List<FamilyPlanPartcpnt> populateFamilyPlanPartcpntDeleteList(
			List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList, List<FamilyPlanPartcpnt> familyPlanPartcpntList);

	/**
	 * Method Name: populateFamilyPlanNeedsDeleteList Method Description:This
	 * method populates the Family Plan Delete List.
	 *
	 * @param familyPlanNeedsDtoList
	 *            the family plan needs dto list
	 * @param familyPlanNeed
	 *            the family plan need
	 * @param familyPlanNeedDeleteList
	 *            the family plan need delete list
	 * @return void
	 */

	public List<FamilyPlanNeed> populateFamilyPlanNeedsDeleteList(List<FamilyPlanNeedsDto> familyPlanNeedsDtoList,
			FamilyPlanNeed familyPlanNeed, List<FamilyPlanNeed> familyPlanNeedDeleteList);

	/**
	 * Method Name: queryChildrenInCaseInSubcare Method Description: Query all
	 * children in the case that are currently in substitute care.
	 *
	 * @param caseId
	 *            the case id
	 * @return List<Long>
	 */
	public List<Long> queryChildrenInCaseInSubcare(Long caseId);

	/**
	 * Method Name: getFamilyPlanNarrList Method Description: fetches the family
	 * plan narrative list.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan narr list
	 */
	public List<FamilyPlanNarrDto> getFamilyPlanNarrList(Long idStage, Long idEvent);

	/**
	 * Fetch principals risk of removal.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the list
	 */
	public List<Long> fetchPrincipalsRiskOfRemoval(Long idEvent);

	/**
	 * Gets the count FCC complete.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the count FCC complete
	 */
	public Long getCountFCCComplete(Long idEvent);

	/**
	 * Method Name: updateFamilyPlanEntity Method Description:This method
	 * updates only the family plan table.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param indFamilyPlanEval
	 *            the ind family plan eval
	 * @return the long
	 */
	public Long updateFamilyPlanEntity(FamilyPlanDto familyPlanDto, boolean indFamilyPlanEval);

	/**
	 * Method Name: updateFamilyPlanCost Method Description: This method is used
	 * update the family plan cost table.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the long
	 */
	public Long updateFamilyPlanCost(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: updateFamilyPlanRole Method Description: This method deletes
	 * the roles for the event id and add based on the role selection.
	 *
	 * @param idEvent
	 *            the id event
	 * @param roles
	 *            the roles
	 */
	public void updateFamilyPlanRole(Long idEvent, List<String> roles);

	/**
	 * Method Name: addFamilyPlanEval Method Description: This method adds the
	 * family plan evaluation.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param familyPlanEvalEventId
	 *            the family plan eval event id
	 * @return the long
	 */
	public Long updateFamilyPlanEval(FamilyPlanDto familyPlanDto, Long familyPlanEvalEventId);

	/**
	 * Method Name: updateFamilyplanitem Method Description: updates
	 * FAMILY_PLAN_ITEM table.
	 *
	 * @param idEvent
	 *            the id event
	 * @param idCase
	 *            the id case
	 */
	public void updateFamilyplanitem(Long idEvent, Long idCase);

	/**
	 * Method Name: updateToDoforNextReview Method Description:Inserts a row
	 * into the TODO table to create a Task To-Do for the primary worker to
	 * create the family plan review.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 */
	public void updateToDoforNextReview(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: deleteChildCandidacy Method Description: Deletes the rows in
	 * Child candidacy table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	public void deleteChildCandidacy(Long idEvent);

	/**
	 * Method Name: updateChildCandidacyDeterminations Method Description:Update
	 * the Determination and Redetermination dates in the FAMILY_CHILD_CANDIDACY
	 * table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	public void updateChildCandidacyDeterminations(Long idEvent);

	/**
	 * Method Name: fetchAndInsertRecsChildCandidacy Method Description:Fetches
	 * the all the fields child candidacy from FAMILY_CHILD_CANDIDACY and
	 * inserts the records in PERSON_ELIGIBILITY table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	public void fetchAndInsertRecsChildCandidacy(Long idEvent);

	/**
	 * Method Name: getFamilyPlanVersion Method Description: This method is used
	 * to getFamilyPlanVersion.
	 *
	 * @param idEvent
	 *            the id event
	 * @return String
	 */
	public String getFamilyPlanVersion(Long idEvent);

	/**
	 * Gets the family plan request.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan request
	 */
	public List<CpsFsnaDto> getFamilyPlanRequest(FamilyPlanDtlEvalReq familyPlanReq);

	/**
	 * Gets the family plan eval request.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan eval request
	 */
	public List<CpsFsnaDto> getFamilyPlanEvalRequest(FamilyPlanDtlEvalReq familyPlanReq);

	/**
	 * Method Name: getActiveTaskGoals Method Description: This method is used
	 * to getActiveTaskGoals.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return the active task goals
	 */
	public void getActiveTaskGoals(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: getAllTasksNotLinkedToGoals Method Description: This method
	 * is used to getAllTasksNotLinkedToGoals.
	 *
	 * @param familyPlanItemDto
	 *            the new all tasks not linked to goals
	 */
	public void setAllTasksNotLinkedToGoals(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: getFamilyPlanEvalVersion Method Description: This method is
	 * used to getFamilyPlanEvalVersion.
	 *
	 * @param idEvent
	 *            the id event
	 * @return String
	 */
	public String getFamilyPlanEvalVersion(Long idEvent);

	/**
	 * Method Name: checkIfIntepreterTranslatorIsNeeded Method Description: This
	 * method is used to checkIfIntepreterTranslatorIsNeeded.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 */
	public void checkIfIntepreterTranslatorIsNeeded(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: updateChildPermanencyInfo Method Description: Method
	 * signature to Update the Permanency Goal in Event Person Link Table for
	 * Family Plan.
	 * 
	 * @param idEvent
	 * @param person
	 */
	void updateChildPermanencyInfo(Long idEvent, PersonValueDto person);

	/**
	 * Method Name: updatePermanencyGoalComment Method Description: Method
	 * signature for Update Permanency goal comment in Family Plan table.
	 * 
	 * @param familyPlanDto
	 */
	void updatePermanencyGoalComment(FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: saveFamilyPlanTask Method Description: Saves the data into
	 * the family_plan_task,FAM_PLN_TASK_GOAL_LINK tables.
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to save
	 * @param currentEventId
	 *            - event id
	 */
	public FamilyPlanRes saveFamilyPlanTask(List<TaskGoalValueDto> insertGoalValueDtos, Long currentEventId);

	/**
	 * Method Name: updateFamilyPlanTask Method Description: Updates information
	 * in the FAMILY_PLAN_TASK table
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to update
	 * @param currentEventId
	 *            - event id
	 */
	public FamilyPlanRes updateFamilyPlanTask(List<TaskGoalValueDto> updateValueDtos, Long currentEventId);

	/**
	 * Method Name: updateTaskGoalLink Method Description: Updates information
	 * in the FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to update
	 */
	public FamilyPlanRes updateTaskGoalLink(List<TaskGoalValueDto> updateValueDtos);

	/**
	 * Method Name: updateFamilPlanActiveTasks Method Description: Updates
	 * information in the FAMILY_PLAN_TASK table
	 *
	 * @param activeUpdateTaskGoalDtos
	 *            - list of tasks with goals to update
	 * @param currentEventId
	 *            - event id
	 */
	public FamilyPlanRes updateFamilPlanActiveTasks(List<TaskGoalValueDto> activeUpdateTaskGoalDtos,
			Long currentEventId);

	/**
	 * Method Name: deleteTaskGoalLink Method Description: Deletes a row from
	 * the FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param idFamilyPlanTask
	 *            id - of FAMILY_PLAN_TASK
	 * @return FamilyPlanRes - FamilyPan response
	 */
	public FamilyPlanRes deleteTaskGoalLink(Long idFamilyPlanTask);

	/**
	 * Method Name: deleteNewFamilyPlanTask Method Description: Deletes a row
	 * from the FAMILY_PLAN_TASK table
	 *
	 * @param idFamilyPlanTask
	 *            - primary key of FAMILY_PLAN_TASK
	 */
	public FamilyPlanRes deleteNewFamilyPlanTask(Long idFamilyPlanTask);

	/**
	 * Method Name: deleteFamilyPlanTask Method Description: Deletes the
	 * specified Family Plan Task from the database.
	 * 
	 * @param taskToDelete
	 * @return FamilyPlanRes
	 */
	public FamilyPlanRes deleteFamilyPlanTask(FamilyPlanTaskDto taskToDelete);

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description:This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id
	 * 
	 * @param idEventList
	 * @return List<EmailDetailsDto> @
	 */
	public List<EmailDetailsDto> fetchEmployeeEmail(Long idEvent, String cdStage);

	/**
	 * 
	 * Method Name: getFamilyPlanReviewDate Method Description:This Method is
	 * used for fetching the Next ReviewDate for Family plan if not exist then
	 * from Family Plan Evaluation.
	 * 
	 * @param idEvent
	 * @return reviewDate
	 */
	public CapsEmailDto getFamilyPlanReviewDetail(Long idEvent, String trigger);

	/**
	 * Gets the family plan.
	 *
	 * @param idFamilyPlan
	 *            the id family plan
	 * @return the family plan
	 */
	public FamilyPlan getFamilyPlan(Long idFamilyPlan);

	/**
	 * Gets the family plan eval.
	 *
	 * @param idFamilyPlanEval
	 *            the id family plan eval
	 * @return the family plan eval
	 */
	public FamilyPlanEval getFamilyPlanEval(Long idFamilyPlanEval);

	/**
	 *Method Name:	geteventPersonLinkDtl
	 *Method Description:
	 *@param idEvent
	 *@param personId
	 *@return
	 */
	public EventPersonLink geteventPersonLinkDtl(Long idEvent, Long personId);

	/**
	 *Method Name:	getFamilyplanDtLastUpdate
	 *Method Description:This method gets the last update date for family plan
	 *@param idEvent
	 *@return
	 */
	Date getFamilyplanDtLastUpdate(Long idEvent);

	/**
	 *Method Name:	getFamilyplanEvalDtLastUpdate
	 *Method Description: This method gets the last update date for family plan Eval
	 *@param idEvent
	 *@return
	 */
	Date getFamilyplanEvalDtLastUpdate(Long idEvent);

	/**
	 *Method Name:	getFamilyPlanVersions
	 *Method Description: This method is used to getFamilyPlanVersions
	 *@param idEvent
	 *@return List<FamilyPlanNarrDto>
	 */
	public List<FamilyPlanNarrDto> getFamilyPlanVersions(Long idEvent);

}
