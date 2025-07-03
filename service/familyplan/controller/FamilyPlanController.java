/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This class helps to call the service to retrieve or save or update the family plan information.
 *Mar 12, 2018- 12:15:26 PM
 *
 */
package us.tx.state.dfps.service.familyplan.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;
import us.tx.state.dfps.familyplan.response.FamilyPlanSaveResp;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.EventReq;
import us.tx.state.dfps.service.common.request.FamilyPlanReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EventDetailRes;
import us.tx.state.dfps.service.common.response.FamilyPlanItemDtlRes;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanService;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;

@RestController
@RequestMapping("/familyplan")
public class FamilyPlanController {

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The family plan service. */
	@Autowired
	FamilyPlanService familyPlanService;

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(FamilyPlanController.class);

	/**
	 * Method Name: getFamilyPlanAssmt Method Description: This method gets
	 * family plan assessments.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return @
	 */
	@RequestMapping(value = "/getfamilyplanassmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getFamilyPlanAssmt(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(familyPlanService.getFamilyPlanAssmt(familyPlanReq)));

		return commonFormRes;

	}

	/**
	 * Method Name: getFamilyPlanEval Method Description: This method gets the
	 * family plan evaluation.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return @
	 */
	@RequestMapping(value = "/getfamilyplaneval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getFamilyPlanEval(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(familyPlanService.getFamilyPlanEval(familyPlanReq)));

		return commonFormRes;

	}

	/**
	 * Method Name: getFamilyPlanAssmt Method Description: CSVC23S, This method
	 * gets the family plan details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return @
	 */
	@RequestMapping(value = "/getfamilyplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getFamilyPlan(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.IdStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdSvcPlnEvalEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.IdSvcPlnEvalEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.IdEvent.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(familyPlanService.getFamilyPlan(familyPlanReq)));

		return commonFormRes;

	}

	/**
	 * Method Name: approveFamilyPlan Method Description: This method update the
	 * FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the current
	 * time stamp.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/approveFamilyPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes approveFamilyPlan(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getApprovalId())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.approveFamilyPlan.approvalId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.approveFamilyPlan.caseId.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());

		familyPlanRes.setItemGoalsMap(
				familyPlanService.approveFamilyPlan(familyPlanReq.getApprovalId(), familyPlanReq.getIdCase()));
		return familyPlanRes;
	}

	/**
	 * Method Name: disApproveFamilyPlan Method Description: This method will
	 * disApprove the FamilyPlan ( DT_APPROVAL = NULL) in Task and Goal Table.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/disApproveFamilyPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes disApproveFamilyPlan(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getMapEntities())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.disApproveFamilyPlan.mapEntities.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());

		familyPlanRes.setResult(familyPlanService.disApproveFamilyPlan(familyPlanReq.getMapEntities()));

		return familyPlanRes;
	}

	/**
	 * Method Name: updateFPTasksGoals Method Description:This method fetches
	 * all the approved tasks and goals for an event id and update the
	 * FAMILY_PLAN_GOAL & FAMILY_PLAN_TASK DT_APPROVAL column with the current
	 * time stamp.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/updateFPTasksGoals", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes updateFPTasksGoals(@RequestBody FamilyPlanReq familyPlanReq) {

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());

		familyPlanRes.setResult(familyPlanService.updateFPTasksGoals(familyPlanReq.getIdEvent()));

		return familyPlanRes;
	}

	/**
	 * Method Name: getEventStatus Method Description: This method will get
	 * event status from Event table.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the event status
	 */
	@RequestMapping(value = "/getEventStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  String getEventStatus(@RequestBody FamilyPlanReq familyPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.getEventBean.eventId.mandatory", null, Locale.US));
		}
		return familyPlanService.getEventStatus(familyPlanReq.getIdEvent());

	}

	/**
	 * Method Name: getChildCandidacy Method Description: This method will fetch
	 * Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/getChildCandidacy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes getChildCandidacy(@RequestBody FamilyPlanReq familyPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto().getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.getEventBean.eventId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.fetchChildNotInSubcare.idStage.idPerson.not.found", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto().getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.approveFamilyPlan.caseId.mandatory", null, Locale.US));
		}
		return familyPlanService.getChildCandidacy(familyPlanReq);
	}

	/**
	 * Method Name: saveChildCandidacy Method Description: This method will Save
	 * the Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/saveChildCandidacy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveChildCandidacy(@RequestBody FamilyPlanReq familyPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.getEventBean.eventId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDtoList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.getFamilyPlanDtoList.mandatory", null, Locale.US));
		}
		return familyPlanService.saveChildCandidacy(familyPlanReq);
	}

	/**
	 * Method Name: queryFamilyPlan Method Description:Query the family plan
	 * details from the database.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryFamilyPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryFamilyPlan(@RequestBody FamilyPlanReq familyPlanReq) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setFamilyPlanDto(familyPlanService.queryFamilyPlan(familyPlanReq.getFamilyPlanDto()));
		return familyPlanRes;
	}

	/**
	 * Method Name: getFamilyPlanAssmt Method Description: This method fetches
	 * the family plan information.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return @
	 */
	@RequestMapping(value = "/fetchfamilyplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes fetchFamilyPlan(@RequestBody FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setFamilyPlanDto(familyPlanService.fetchFamilyPlanDtl(familyPlanDto));
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteFamilyPlan Method Description: Delete Family Plan.
	 *
	 * @param eventReq
	 *            the event req
	 * @return the event detail res
	 */
	@RequestMapping(value = "/deleteFamilyPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EventDetailRes deleteFamilyPlan(@RequestBody EventReq eventReq) {

		EventDetailRes eventDetailRes = new EventDetailRes();
		familyPlanService.deleteFamilyPlan(eventReq.getUlIdEvent());
		return eventDetailRes;
	}

	/**
	 * Method Name: queryFamilyPlanTask Method Description: This method will
	 * query the Family Plan task table to retrieve the task.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryFamilyPlanTask", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryFamilyPlanTask(@RequestBody FamilyPlanReq familyPlanReq) {

		logger.debug("Entering method queryFamilyPlanTask in FamilyPlanController");

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.eventId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.caseId.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();

		familyPlanRes.setTaskGoalValueDtoList(
				familyPlanService.queryFamilyPlanTask(familyPlanReq.getIdEvent(), familyPlanReq.getIdCase()));

		logger.debug("Exiting method queryFamilyPlanTask in FamilyPlanController");

		return familyPlanRes;
	}

	/**
	 * Method Name: queryFamilyPlanGoal Method Description: Query the Goals
	 * table to retrieve all the Goals associated with a Family Plan.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryFamilyPlanGoal", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryFamilyPlanGoal(@RequestBody FamilyPlanReq familyPlanReq) {

		logger.debug("Entering method queryFamilyPlanGoal in FamilyPlanController");

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanGoal.idEvent.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanGoal.idCase.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());

		familyPlanRes.setFamilyPlanGoalValueDtos(
				familyPlanService.queryFamilyPlanGoal(familyPlanReq.getIdEvent(), familyPlanReq.getIdCase()));

		logger.debug("Exiting method queryFamilyPlanGoal in FamilyPlanController");

		return familyPlanRes;
	}

	/**
	 * Method Name: queryApprovedActiveTasks Method Description: Query to get
	 * the approved active tasks for selected plan.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryApprovedActiveTasks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryApprovedActiveTasks(@RequestBody FamilyPlanReq familyPlanReq) {

		logger.debug("Entering method queryApprovedActiveTasks in FamilyPlanController");
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.eventId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.caseId.mandatory", null, Locale.US));
		}
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();

		familyPlanRes.setApprovedActiveTasksList(familyPlanService.queryApprovedActiveTasks(familyPlanReq.getIdEvent(),
				familyPlanReq.getIdCase(), familyPlanReq.getCurrentIdEvent()));
		logger.debug("Entering method queryApprovedActiveTasks in FamilyPlanController");
		return familyPlanRes;
	}

	/**
	 * Method Name: queryApprovedInActiveTasks Method Description: Query the
	 * approved inactive tasks for family plan.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryApprovedInActiveTasks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryApprovedInActiveTasks(@RequestBody FamilyPlanReq familyPlanReq){
		logger.debug("Entering method queryApprovedInActiveTasks in FamilyPlanController");
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.eventId.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFamilyPlanTask.caseId.mandatory", null, Locale.US));
		}

		familyPlanRes.setApprovedInactiveTasksList(familyPlanService.queryApprovedInActiveTasks(familyPlanReq.getIdEvent(), familyPlanReq.getIdCase(),
				familyPlanReq.getCurrentIdEvent()));
		
		logger.debug("Entering method queryApprovedInActiveTasks in FamilyPlanController");
		return familyPlanRes;
	}

	/**
	 * Method Name: queryFGDMFamilyGoal Method Description: This method is used
	 * to query the Family Plan Goal.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/queryFGDMFamilyGoal", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes queryFGDMFamilyGoal(@RequestBody FamilyPlanReq familyPlanReq) {

		logger.debug("Entering method queryFGDMFamilyGoal in FamilyPlanController");

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFGDMFamilyGoal.idEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFGDMFamilyGoal.idCase.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setFamilyPlanGoalValueDtos(
				familyPlanService.queryFGDMFamilyGoal(familyPlanReq.getIdCase(), familyPlanReq.getIdEvent()));

		logger.debug("Exiting method queryFGDMFamilyGoal in FamilyPlanController");
		return familyPlanRes;
	}

	/**
	 * Method Name: getAreaOfConcernList Method Description: This method returns
	 * a list of Area of Concern for a family plan goal.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/getAreaOfConcernList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes getAreaOfConcernList(@RequestBody FamilyPlanReq familyPlanReq) {

		logger.debug("Entering method getAreaOfConcernList in FamilyPlanController");
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdFamilyPlanGoal())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.getAreaOfConcernList.idFamilyPlanGoal.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes
				.setListOfAreaOfConcern(familyPlanService.getAreaOfConcernList(familyPlanReq.getIdFamilyPlanGoal()));
		logger.debug("Exiting method getAreaOfConcernList in FamilyPlanController");

		return familyPlanRes;
	}

	/**
	 * Gets the family plan details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan details
	 */
	@RequestMapping(value = "/getFamilyPlanDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanDtlEvalRes getFamilyPlanDetails(@RequestBody FamilyPlanDtlEvalReq familyPlanReq) {
		
		return familyPlanService.getFamilyPlanDetails(familyPlanReq);
	}

	/**
	 * Method Name: getAreaOfConcernList Method Description: This service needs
	 * idUser, caseid, stageid, taskcode, stagetype, IndSubmit(if the action is
	 * from "save" or from "save & submit".
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/saveFamilyPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanSaveResp saveFamilyPlan(@RequestBody FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.queryFGDMFamilyGoal.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getIndSubmit())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.indSubmit.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageType.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanDtlEvalReq.getCdStageType())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageProgramType.mandatory", null, Locale.US));
		}
		return familyPlanService.saveFamilyPlan(familyPlanDtlEvalReq);
	}

	/**
	 * Method Name: getFamilyPlanFormDetails Method Description:Method used to
	 * retrieve the family plan form details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan form details
	 */
	@RequestMapping(value = "/getFamilyPlanFormDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getFamilyPlanFormDetails(@RequestBody FamilyPlanDtlEvalReq familyPlanReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdFamilyPlanEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.idFamilyPlanEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.cdStage.mandatory", null, Locale.US));
		}
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(familyPlanService.getFamilyPlanFormDetails(familyPlanReq)));
		return commonFormRes;
	}

	/**
	 * Method Name: getFamilyPlanEvalFormDetails Method Description:Method used
	 * to retrieve the family plan evaluation form details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan eval form details
	 */
	@RequestMapping(value = "/getFamilyPlanEvalFormDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getFamilyPlanEvalFormDetails(@RequestBody FamilyPlanDtlEvalReq familyPlanReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdFamilyPlanEvalEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.idFamilyPlanEvalEvent().mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getCdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("FamilyPlanController.cdStage.mandatory", null, Locale.US));
		}
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(familyPlanService.getFamilyPlanEvalFormDetails(familyPlanReq)));
		return commonFormRes;
	}

	/**
	 * Method Name: getFamilyPlanNarrative Method Description: gets the
	 * information about family plan narrative
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the family plan narrative
	 */
	@RequestMapping(value = "/getFamilyPlanNarrative", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public FamilyPlanDtlEvalRes getFamilyPlanNarrative(@RequestBody FamilyPlanDtlEvalReq familyPlanReq) {
		FamilyPlanDtlEvalRes familyPlanDtlEvalRes = new FamilyPlanDtlEvalRes();

		familyPlanDtlEvalRes = familyPlanService.getFamilyPlanVersions(familyPlanReq.getIdEvent());

		return familyPlanDtlEvalRes;
	}

	/**
	 * Adds the family plan eval legacy.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan res
	 */
	@RequestMapping(value = "/addFamilyPlanEvalLegacy", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes addFamilyPlanEvalLegacy(@RequestBody FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setFamilyPlanDto(familyPlanService.getFamilyPlanEvalAddMode(familyPlanDto));
		return familyPlanRes;
	}

	/**
	 * Fetch principals risk of removal.
	 *
	 * @param commonHelperReq
	 *            the common helper req
	 * @return the common helper res
	 */
	@RequestMapping(value = "/fetchPrincipalsRiskOfRemoval", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes fetchPrincipalsRiskOfRemoval(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setPersonIds(familyPlanService.fetchPrincipalsRiskOfRemoval(commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	/**
	 * Gets the count FCC complete.
	 *
	 * @param commonHelperReq
	 *            the common helper req
	 * @return the count FCC complete
	 */
	@RequestMapping(value = "/getCountFCCComplete", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getCountFCCComplete(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResultCount(familyPlanService.getCountFCCComplete(commonHelperReq.getIdEvent()));
		return commonHelperRes;
	}

	/**
	 * Gets the list child in FCC.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return the list child in FCC
	 */
	@RequestMapping(value = "/getListChildInFCC", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes getListChildInFCC(@RequestBody FamilyPlanReq familyPlanReq) {
		return familyPlanService.getListChildInFCC(familyPlanReq);
	}

	/**
	 * Fetch all child candidacy.
	 *
	 * @param commonHelperReq
	 *            the common helper req
	 * @return the family plan res
	 */
	@RequestMapping(value = "/fetchAllChildCandidacy", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes fetchAllChildCandidacy(@RequestBody CommonHelperReq commonHelperReq) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setListPersonDtl(familyPlanService.fetchAllChildCandidacy(commonHelperReq.getIdEvent()));
		return familyPlanRes;
	}

	/**
	 * Save family plan legacy.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan res
	 */
	@RequestMapping(value = "/saveFamilyPlanLegacy", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveFamilyPlanLegacy(@RequestBody FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = familyPlanService.saveFamilyPlanLegacy(familyPlanDto);
		return familyPlanRes;
	}

	/**
	 * Save submit family plan legacy.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan res
	 */
	@RequestMapping(value = "/saveSubmitFamilyPlanLegacy", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveSubmitFamilyPlanLegacy(@RequestBody FamilyPlanDto familyPlanDto) {
		FamilyPlanRes familyPlanRes = familyPlanService.saveFamilyPlanLegacy(familyPlanDto);
		familyPlanRes = familyPlanService.saveSubmitFamilyPlanLegacy(familyPlanDto, familyPlanRes);
		return familyPlanRes;
	}

	/**
	 * Gets the goals and active tasks for item.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return the goals and active tasks for item
	 */
	@RequestMapping(value = "/getGoalsAndActiveTasksForItem", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanItemDtlRes getGoalsAndActiveTasksForItem(
			@RequestBody FamilyPlanItemDto familyPlanItemDto) {
		FamilyPlanItemDtlRes familyPlanItemDtlRes = new FamilyPlanItemDtlRes();
		familyPlanItemDtlRes.setFamilyPlanItemDto(familyPlanService.queryGoalsAndActiveTasksForItem(familyPlanItemDto));
		return familyPlanItemDtlRes;
	}

	/**
	 * Gets the all tasks not linked to goals.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return the all tasks not linked to goals
	 */
	@RequestMapping(value = "/getAllTasksNotLinkedToGoals", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanItemDtlRes getAllTasksNotLinkedToGoals(
			@RequestBody FamilyPlanItemDto familyPlanItemDto) {
		FamilyPlanItemDtlRes familyPlanItemDtlRes = new FamilyPlanItemDtlRes();
		familyPlanService.setAllTasksNotLinkedToGoals(familyPlanItemDto);
		familyPlanItemDtlRes.setFamilyPlanItemDto(familyPlanItemDto);
		return familyPlanItemDtlRes;
	}

	/**
	 * Method Name: saveFamilyPlanTask Method Description: Saves the Task
	 * related information into the family plan Task and related Table.
	 *
	 * @param familyPlanReq
	 *            - family plan tasks requests
	 * @return FamilyPlanRes - family plan tasks response
	 */
	@RequestMapping(value = "/saveFamilyPlanTask", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveFamilyPlanTask(@RequestBody FamilyPlanReq familyPlanReq){

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.saveFamilyPlanTask.familyPlanValueBeanDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto().getTaskGoalValueDtos())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.saveFamilyPlanTask.taskGoalValueDtoList.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.saveFamilyPlanTask.eventId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.saveFamilyPlanTask.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getCurrentIdEvent())) {
			throw new InvalidRequestException(messageSource
					.getMessage("FamilyPlanController.saveFamilyPlanTask.currentEventId.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());
		familyPlanRes = familyPlanService.saveFamilyPlanTask(familyPlanReq.getFamilyPlanDto().getTaskGoalValueDtos(),
				familyPlanReq.getIdEvent(), familyPlanReq.getIdCase(), familyPlanReq.getCurrentIdEvent(),
				familyPlanReq.getFamilyPlanDto());

		logger.debug("Exiting method saveFamilyPlanTask in FamilyPlanController");

		return familyPlanRes;
	}

	/**
	 * Method Name: deleteNewFamilyPlanTask Method Description: Delete a row
	 * from the family_plan_task,FAM_PLN_TASK_GOAL_LINK tables
	 *
	 * @param familyPlanReq
	 *            - family plan tasks requests
	 * @return FamilyPlanRes - family plan tasks response
	 */
	@RequestMapping(value = "/deleteNewFamilyPlanTask", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes deleteNewFamilyPlanTask(@RequestBody FamilyPlanReq familyPlanReq){

		logger.debug("Entering method deleteNewFamilyPlanTask in FamilyPlanController");

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdFamilyPlanTask())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.deleteNewFamilyPlanTask.idFamilyPlanTask.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto())) {
			throw new InvalidRequestException(messageSource.getMessage(
					"FamilyPlanController.deleteNewFamilyPlanTask.familyPlanValueBeanDto.mandatory", null, Locale.US));
		}

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		familyPlanRes.setTransactionId(familyPlanReq.getTransactionId());

		familyPlanRes = familyPlanService.deleteNewFamilyPlanTask(familyPlanReq.getIdFamilyPlanTask(),
				familyPlanReq.getFamilyPlanDto());

		logger.debug("Exiting method deleteNewFamilyPlanTask in FamilyPlanController");

		return familyPlanRes;
	}

	/**
	 * Deletes the selected Family Plan Task.
	 * 
	 * @param familyPlanReq
	 * @return familyPlanItemRes
	 */
	@RequestMapping(value = "/deleteFamilyPlanTask", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes deleteFamilyPlanTask(@RequestBody FamilyPlanReq familyPlanReq){
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("familyPlan.FamilyPlanValueBeanDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getFamilyPlanDto().getFamilyPlanItemList())) {
			throw new InvalidRequestException(
					messageSource.getMessage("familyPlan.FamilyPlanItemValueBeanDto.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIndexOfTaskToDelete())) {
			throw new InvalidRequestException(
					messageSource.getMessage("familyPlan.IndexOfTaskToDelete.mandatory", null, Locale.US));
		}
		FamilyPlanRes familyPlan = familyPlanService.deleteFamilyPlanTask(familyPlanReq.getFamilyPlanDto(),
				familyPlanReq.getFamilyPlanItemDto(), familyPlanReq.getIndexOfTaskToDelete());
		return familyPlan;
	}

	/**
	 * Method Name: saveChildPermanencyGoal Method Description: Rest controller
	 * method for handling Request to save Child Permenency Goal in Family Plan
	 * 
	 * @param familyPlanDto
	 * @return familyPlanRes
	 */
	@RequestMapping(value = "/saveChildPermanencyGoal", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveChildPermanencyGoal(@RequestBody FamilyPlanReq familyPlanReq) {
		FamilyPlanRes familyPlanRes = familyPlanService.savePermanencyGoals(familyPlanReq.getFamilyPlanDto());
		return familyPlanRes;
	}

	/**
	 * Method Name: saveOrUpdateFamilyPlanGoal Method Description: save or
	 * update the Goals associated with a Family Plan.
	 * 
	 * @param familyPlanReq
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/saveOrUpdateFGDMFamilyGoal", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes saveOrUpdateFamilyPlanGoal(@RequestBody FamilyPlanReq familyPlanReq) {
		logger.debug("Entering method saveOrUpdateFamilyPlanGoal in FamilyPlanController");
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		FamilyPlanRes familyPlanRes = familyPlanService.saveOrUpdateFamPlanGoal(familyPlanReq.getFamilyPlanDto(),
				familyPlanReq.getIdEvent(), familyPlanReq.getIdCase());

		// familyPlanRes.setFamPlanGoalSave(res);
		logger.debug("Exiting method saveOrUpdateFamilyPlanGoal in FamilyPlanController");
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteFGDMFamilyGoal Method Description: save or update the
	 * Goals associated with a Family Plan.
	 * 
	 * @param familyPlanReq
	 * @return FamilyPlanRes
	 */
	@RequestMapping(value = "/deleteFGDMFamilyGoal", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes deleteFGDMFamilyGoal(@RequestBody FamilyPlanReq familyPlanReq) {
		logger.debug("Entering method deleteFGDMFamilyGoal in FamilyPlanController");
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdFamilyPlanGoal())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.idFamilyPlanGoal.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(familyPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		String res = familyPlanService.deleteFamPlanGoal(familyPlanReq.getIdFamilyPlanGoal(),
				familyPlanReq.getFamilyPlanDto());
		familyPlanRes.setFamPlanGoalSave(res);
		logger.debug("Exiting method deleteFGDMFamilyGoal in FamilyPlanController");
		return familyPlanRes;
	}
}
