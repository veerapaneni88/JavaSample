package us.tx.state.dfps.service.approval.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.approval.dto.ApprovalStatusFacilityIndicatorDto;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyReq;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyRes;
import us.tx.state.dfps.approval.dto.AprvlStatusUpdateCapsResourceReq;
import us.tx.state.dfps.approval.dto.ClosePlacementReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryRes;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscRes;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageRes;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.common.domain.PersonEligibility;
import us.tx.state.dfps.common.dto.CommonDto;
import us.tx.state.dfps.common.dto.SafetyAssmtDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.approval.service.ApprovalStatusDaoTestService;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.common.response.ApprovalPersonRes;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyTree.bean.TaskGoalValueDto;
import us.tx.state.dfps.service.familyplan.service.FamilyPlanService;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;

@RestController
@RequestMapping(value = "/approvaltest")

public class ApprovalStatusDaoTestController {

	@Autowired
	ApprovalStatusDaoTestService approvalStatusDaoTestService;

	@Autowired
	FamilyPlanService familyPlanService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ApprovalStatusDaoTestController.class);

	@RequestMapping(value = "/deleteIncompleteTodos", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes deleteIncompleteTodos(@RequestParam int idFromStage) {
		log.info("ApprovalStatusDaoTestController.deleteToDosForStage()");
		approvalStatusDaoTestService.deleteIncompleteTodos(idFromStage);
		return null;
	}

	@RequestMapping(value = "/closeStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes closeStage(@RequestBody StageValueBeanDto stageValueBeanDto) {
		log.info("ApprovalStatusDaoTestController.closeStage()");
		approvalStatusDaoTestService.closeStage(stageValueBeanDto);
		return null;
	}

	@RequestMapping(value = "/selectStagePersonLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  List<StagePersonValueDto> selectStagePersonLink(@RequestParam Integer idFromStage) {
		log.info("ApprovalStatusDaoTestController.selectStagePersonLink()");

		List<String> invRolesList = new ArrayList<>();
		invRolesList.add(ServiceConstants.CROLEALL_AR);
		invRolesList.add(ServiceConstants.CROLEALL_NO);
		invRolesList.add(ServiceConstants.CROLEALL_AP);
		invRolesList.add(ServiceConstants.CROLEALL_VC);
		invRolesList.add(ServiceConstants.CROLEALL_VP);
		invRolesList.add(ServiceConstants.CROLEALL_UK);

		return approvalStatusDaoTestService.selectStagePersonLink(idFromStage, invRolesList);
	}

	@RequestMapping(value = "/deleteStagePersonLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes deleteStagePersonLink(@RequestBody StagePersonValueDto stagePersonValueDto) {
		log.info("ApprovalStatusDaoTestController.deleteStagePersonLink()");
		approvalStatusDaoTestService.deleteStagePersonLink(stagePersonValueDto);
		return null;
	}

	@RequestMapping(value = "/fetchForwardPersonsForStagePersons", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes fetchForwardPersonsForStagePersons(
			@RequestBody StagePersonValueDto stagePersonValueDto) {
		log.info("ApprovalStatusDaoTestController.fetchForwardPersonsForStagePersons()");
		approvalStatusDaoTestService.fetchForwardPersonsForStagePersons(stagePersonValueDto.getIdStage());
		return null;
	}

	@RequestMapping(value = "/updateStagePersonLinks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Integer updateStagePersonLinks(@RequestBody List<StagePersonValueDto> spLinkBeans) {
		log.info("ApprovalStatusDaoTestController.updateStagePersonLinks()");
		approvalStatusDaoTestService.updateStagePersonLinks(spLinkBeans);
		return null;
	}

	@RequestMapping(value = "/insertIntoStagePersonLinks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void insertIntoStagePersonLinks(@RequestBody List<StagePersonValueDto> stagePersonValueDtoList) {
		log.info("ApprovalStatusDaoTestController.insertIntoStagePersonLinks()");
		approvalStatusDaoTestService.insertIntoStagePersonLinks(stagePersonValueDtoList);
		return;
	}

	@RequestMapping(value = "/fetchIntakeAllegations", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<AllegationDto> fetchIntakeAllegations(@RequestBody StagePersonValueDto stagePersonValueDtoList) {
		List<AllegationDto> res = new ArrayList<>();
		log.info("ApprovalStatusDaoTestController.insertIntoStagePersonLinks()");
		res = approvalStatusDaoTestService.fetchIntakeAllegations(stagePersonValueDtoList);
		return res;
	}

	@RequestMapping(value = "/createInvestigationAllegations", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long[] createInvestigationAllegations(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.createInvestigationAllegations()");
		return approvalStatusDaoTestService.createInvestigationAllegations(commonDto.getIdStage(),
				commonDto.getIdCase(), commonDto.getAllegationDto());
	}

	@RequestMapping(value = "/createEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public int createEvent(@RequestBody EventValueDto eventValueDto) {
		log.info("ApprovalStatusDaoTestController.createEvent()");
		return approvalStatusDaoTestService.createEvent(eventValueDto);
	}

	@RequestMapping(value = "/getServiceAuthorizationEventDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<SvcAuthEventLinkInDto> getServiceAuthorizationEventDetails(@RequestBody EventValueDto eventValueDto) {
		log.info("ApprovalStatusDaoTestController.getServiceAuthorizationEventDetails()");
		return approvalStatusDaoTestService.getServiceAuthorizationEventDetails(eventValueDto);
	}

	@RequestMapping(value = "/insertIntoServiceAuthorizationEventLinks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<Long> insertIntoServiceAuthorizationEventLinks(
			@RequestBody List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans) {
		log.info("ApprovalStatusDaoTestController.insertIntoServiceAuthorizationEventLinks()");
		return approvalStatusDaoTestService.insertIntoServiceAuthorizationEventLinks(serviceAuthEventLinkValueBeans);
	}

	@RequestMapping(value = "/insertIntoEventPersonLinks", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<Long> insertIntoEventPersonLinks(@RequestBody List<EventPersonDto> spLinkBeans) {
		log.info("ApprovalStatusDaoTestController.insertIntoEventPersonLinks()");
		return approvalStatusDaoTestService.insertIntoEventPersonLinks(spLinkBeans);
	}

	@RequestMapping(value = "/getincomingDetailbyId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public IncomingDetail getincomingDetailbyId(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.getincomingDetailbyId()");
		return approvalStatusDaoTestService.getincomingDetailbyId(commonDto);
	}

	@RequestMapping(value = "/updatePriorStageAndIncomingCallDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void updatePriorStageAndIncomingCallDate(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.updatePriorStageAndIncomingCallDate()");
		approvalStatusDaoTestService.updatePriorStageAndIncomingCallDate(commonDto);
		return;
	}

	@RequestMapping(value = "/retrieveStageInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StageValueBeanDto retrieveStageInfo(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.updatePriorStageAndIncomingCallDate()");
		StageValueBeanDto StageValueBeanDto = new StageValueBeanDto();
		StageValueBeanDto = approvalStatusDaoTestService.retrieveStageInfo(commonDto);
		return StageValueBeanDto;
	}

	@RequestMapping(value = "/updateINVSafetyAssignmentWithARSafetyAssignment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long updateINVSafetyAssignmentWithARSafetyAssignment(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.updateINVSafetyAssignmentWithARSafetyAssignment()");
		Long size = null;
		size = approvalStatusDaoTestService.updateINVSafetyAssignmentWithARSafetyAssignment(commonDto);
		return size;
	}

	@RequestMapping(value = "/populateRiskData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public RiskAssmtValueDto populateRiskData(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.addSafetyAssmt()");
		RiskAssmtValueDto riskAssmtValueDto = new RiskAssmtValueDto();
		riskAssmtValueDto = approvalStatusDaoTestService.populateRiskData(commonDto);
		return riskAssmtValueDto;
	}

	@RequestMapping(value = "/addRiskAssmtDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public long addRiskAssmtDetails(@RequestBody RiskAssmtValueDto riskAssmtValueDto) {
		log.info("ApprovalStatusDaoTestController.addSafetyAssmt()");
		long res;
		res = approvalStatusDaoTestService.addRiskAssmtDetails(riskAssmtValueDto);
		return res;
	}

	@RequestMapping(value = "/addAreaDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto formFactorBean) {
		log.info("ApprovalStatusDaoTestController.addAreaDetails()");
		long res;
		res = approvalStatusDaoTestService.addAreaDetails(idRiskEvent, formFactorBean);
		return res;
	}

	@RequestMapping(value = "/addCategoryDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public long addCategoryDetails(long idRiskEvent, long newRiskAreaId, RiskAssmtValueDto formFactorBean) {
		log.info("ApprovalStatusDaoTestController.addCategoryDetails()");
		long res;
		res = approvalStatusDaoTestService.addCategoryDetails(idRiskEvent, newRiskAreaId, formFactorBean);
		return res;
	}

	@RequestMapping(value = "/addFactorDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long newRiskCategoryId,
			RiskAssmtValueDto formFactorBean) {
		log.info("ApprovalStatusDaoTestController.addFactorDetails()");
		long res;
		res = approvalStatusDaoTestService.addFactorDetails(idRiskEvent, newRiskAreaId, newRiskCategoryId,
				formFactorBean);
		return res;
	}

	@RequestMapping(value = "/updateStageLink", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void updateStageLink(StageValueBeanDto stageValueBeanDto, Long newStageId) {
		log.info("ApprovalStatusDaoTestController.addFactorDetails()");
		approvalStatusDaoTestService.addFactorDetails(stageValueBeanDto, newStageId);
		return;
	}

	@RequestMapping(value = "/getSelectCaseFileManagement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CaseFileManagementDto getSelectCaseFileManagement(@RequestBody CaseFileManagementDto caseFileManagementDto) {
		CaseFileManagementDto CaseFileManagementDto = new CaseFileManagementDto();
		log.info("ApprovalStatusDaoTestController.addFactorDetails()");
		CaseFileManagementDto = approvalStatusDaoTestService.getSelectCaseFileManagement(caseFileManagementDto);
		return CaseFileManagementDto;
	}

	@RequestMapping(value = "/getEmployeeOfficeIdentifier", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public long getEmployeeOfficeIdentifier(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.getEmployeeOfficeIdentifier()");
		return approvalStatusDaoTestService.getEmployeeOfficeIdentifier(commonDto);
	}

	@RequestMapping(value = "/insertCaseFileManagement", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public long insertCaseFileManagement(@RequestBody CaseFileManagementDto caseFileManagementDto) {
		log.info("ApprovalStatusDaoTestController.insertCaseFileManagement()");
		return approvalStatusDaoTestService.insertCaseFileManagement(caseFileManagementDto);
	}

	@RequestMapping(value = "/deleteTodosForACase", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public long deleteTodosForACase(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.deleteTodosForACase()");
		return approvalStatusDaoTestService.deleteTodosForACase(commonDto);
	}

	@RequestMapping(value = "/getActiveStagesForPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<Long> getActiveStagesForPerson(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.getActiveStagesForPerson()");
		return approvalStatusDaoTestService.getActiveStagesForPerson(commonDto);
	}

	@RequestMapping(value = "/makePersonsInactive", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<Long> makePersonsInactive() {
		log.info("ApprovalStatusDaoTestController.makePersonsInactive()");
		List<Long> invRolesList = new ArrayList<>();
		invRolesList.add(0, 25661302L);
		return approvalStatusDaoTestService.makePersonsInactive(invRolesList);
	}

	@RequestMapping(value = "/getARSafetyAssmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ARSafetyAssmtValueDto getARSafetyAssmt(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.ARSafetyAssmtValueDto()");
		ARSafetyAssmtValueDto aRSafetyAssmtValueDto = new ARSafetyAssmtValueDto();
		aRSafetyAssmtValueDto = approvalStatusDaoTestService.getARSafetyAssmt(commonDto);
		return aRSafetyAssmtValueDto;
	}

	@RequestMapping(value = "/updateEventStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public long updateEventStatus(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.updateEventStatus()");
		return approvalStatusDaoTestService.updateEventStatus(commonDto);
	}

	@RequestMapping(value = "/retrieveStageInfoList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public StageValueBeanDto retrieveStageInfoList(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.retrieveStageInfoList()");
		return approvalStatusDaoTestService.retrieveStageInfoList(commonDto);
	}

	@RequestMapping(value = "/getSubStageOpen", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Boolean getSubStageOpen(@RequestBody SafetyAssmtDto safetyAssmtDto) {
		log.info("ApprovalStatusDaoTestController.getSubStageOpen()");
		return approvalStatusDaoTestService.getSubStageOpen(safetyAssmtDto);
	}

	@RequestMapping(value = "/getTodoId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public Long getTodoId(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.getTodoId()");
		return approvalStatusDaoTestService.getTodoId(commonDto);
	}

	@RequestMapping(value = "/getPrimaryWorkerIdForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long getPrimaryWorkerIdForStage(@RequestBody CommonDto commonDto) {
		log.info("ApprovalStatusDaoTestController.getPrimaryWorkerIdForStage()");
		return approvalStatusDaoTestService.getPrimaryWorkerIdForStage(commonDto);
	}

	@RequestMapping(value = "/updateIndSecondApprover", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void updateIndSecondApprover(@RequestBody SaveApprovalStatusReq saveApprovalStatusReq) {
		log.info("ApprovalStatusDaoTestController.updateIndSecondApprover()");
		approvalStatusDaoTestService.updateIndSecondApprover(saveApprovalStatusReq);
		return;
	}

	@RequestMapping(value = "/getEmployeeInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public List<ApprovalCommonInDto> getEmployeeInfo() {
		List<ApprovalCommonInDto> personlst = new ArrayList<>();
		log.info("ApprovalStatusDaoTestController.updateIndSecondApprover()");
		// personlst =
		// approvalStatusDaoTestService.getEmployeeInfo(saveApprovalStatusReq);
		return personlst;
	}

	@RequestMapping(value = "/testDisApproveFamilyPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  FamilyPlanRes testDisApproveFamilyPlan() {
		Map<String, List> inputMap;
		try {
			inputMap = createInputMapForDisAppvFamPlan();

			FamilyPlanRes familyPlanRes = new FamilyPlanRes();
			familyPlanRes.setResult(familyPlanService.disApproveFamilyPlan(inputMap));
			return familyPlanRes;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private Map<String, List> createInputMapForDisAppvFamPlan() throws ParseException {
		Map<String, List> inputMap = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		List<FamilyPlanItemDto> familyPlanItemDtoList = new ArrayList<>();
		FamilyPlanItemDto familyPlanItemDto = new FamilyPlanItemDto();
		familyPlanItemDto.setIdFamilyPlanItem(Long.valueOf(603423));
		familyPlanItemDto.setFamilyPlanItemDateLastUpdate(sdf.parse("04/12/2006"));
		familyPlanItemDto.setIdEvent(Long.valueOf(99989925));
		familyPlanItemDto.setIdCase(Long.valueOf(25928313));
		familyPlanItemDto.setCdAreaConcern("MT");
		familyPlanItemDto.setCdInitialLevelConcern("3");
		familyPlanItemDto.setCdCurrentLevelConcern("3");
		familyPlanItemDto.setTxtItemGoals(
				"- Ms. Hawkins will gain an understanding of how the family history of maltreatment has influenced the family's current situation.");
		familyPlanItemDto.setDtInitiallyAddressed(Calendar.getInstance().getTime());
		familyPlanItemDto.setIndIdentifiedInRiskAssmnt("Y");
		familyPlanItemDtoList.add(familyPlanItemDto);

		familyPlanItemDto = new FamilyPlanItemDto();
		familyPlanItemDto.setIdFamilyPlanItem(Long.valueOf(603424));
		familyPlanItemDto.setFamilyPlanItemDateLastUpdate(sdf.parse("04/12/2006"));
		familyPlanItemDto.setIdEvent(Long.valueOf(99989925));
		familyPlanItemDto.setIdCase(Long.valueOf(25928313));
		familyPlanItemDto.setCdAreaConcern("HE");
		familyPlanItemDto.setCdInitialLevelConcern("5");
		familyPlanItemDto.setCdCurrentLevelConcern("4");
		familyPlanItemDto.setTxtItemGoals(
				"- Ms. Hawkins will understand the cycle of violence and learn how to protect herself.\\r\\n- Ms. Hawkins will learn what causes stressful situations for the family.\\r\\n- Ms. Hawkins will demonstrate the ability to avoid potentially abusive adult relationships.");
		familyPlanItemDto.setDtInitiallyAddressed(Calendar.getInstance().getTime());
		familyPlanItemDto.setIndIdentifiedInRiskAssmnt("Y");
		familyPlanItemDtoList.add(familyPlanItemDto);
		inputMap.put("ITEMSLIST", familyPlanItemDtoList);

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = new ArrayList<>();
		FamilyPlanGoalValueDto familyPlanGoalValueDto = new FamilyPlanGoalValueDto();
		familyPlanGoalValueDto.setFamilyPlanGoalId(Long.valueOf(1591732));
		familyPlanGoalValueDto.setDateLastUpdate(sdf.parse("10/03/2011"));
		familyPlanGoalValueDto.setEventId(Long.valueOf(114674743));
		familyPlanGoalValueDto.setCaseId(Long.valueOf(25521864));
		familyPlanGoalValueDto.setGoalTxt(
				"Felix Gomez and Martha Ochoa will demonstrate an acceptance of the responsibility of being a parent.");
		familyPlanGoalValueDto.setDateApproved(Calendar.getInstance().getTime());
		familyPlanGoalValueDtoList.add(familyPlanGoalValueDto);

		familyPlanGoalValueDto = new FamilyPlanGoalValueDto();
		familyPlanGoalValueDto.setFamilyPlanGoalId(Long.valueOf(1591733));
		familyPlanGoalValueDto.setDateLastUpdate(sdf.parse("10/03/2011"));
		familyPlanGoalValueDto.setEventId(Long.valueOf(114674743));
		familyPlanGoalValueDto.setCaseId(Long.valueOf(25521864));
		familyPlanGoalValueDto.setGoalTxt(
				"Felix Gomez and Martha Ochoa will demonstrate an ability to stay sober drug free and in recovery");
		familyPlanGoalValueDto.setDateApproved(Calendar.getInstance().getTime());
		familyPlanGoalValueDtoList.add(familyPlanGoalValueDto);

		inputMap.put("GOALSLIST", familyPlanGoalValueDtoList);

		List<TaskGoalValueDto> taskGoalValueDtoList = new ArrayList<>();
		TaskGoalValueDto taskGoalValueDto = new TaskGoalValueDto();
		taskGoalValueDto.setFamilyPlanTaskId(Long.valueOf(843131));
		taskGoalValueDto.setFamilyPlanItemId(Long.valueOf(707108));
		taskGoalValueDto.setEventId(Long.valueOf(103371307));
		taskGoalValueDto.setCaseId(Long.valueOf(26019307));
		taskGoalValueDto.setCourtOrderedInd(Boolean.FALSE);
		taskGoalValueDto.setTaskTxt(
				"Marilu Mondragon will continue to attend and participate in individual counseling with Olga Vidal LPC at Zaragosa Clinic. \r\nNecesitas ir a la consulera Olga Vidal en la clinica Zaragosa, para hablar con ella de el Depresion y de el maltrato que paso quando estabas joven con tu madre y de qualqueras problemas que pasan en tu vida y con tu esposo.");
		taskGoalValueDto.setDateLastUpdate(sdf.parse("26/03/2007"));
		taskGoalValueDto.setDateCreated(Calendar.getInstance().getTime());
		taskGoalValueDtoList.add(taskGoalValueDto);

		taskGoalValueDto = new TaskGoalValueDto();
		taskGoalValueDto.setFamilyPlanTaskId(Long.valueOf(843132));
		taskGoalValueDto.setFamilyPlanItemId(Long.valueOf(700704));
		taskGoalValueDto.setEventId(Long.valueOf(104670722));
		taskGoalValueDto.setCaseId(Long.valueOf(26017670));
		taskGoalValueDto.setCourtOrderedInd(Boolean.TRUE);
		taskGoalValueDto.setTaskTxt(
				"Mr. and Mrs. Poyner will demonstrate the ability to lead a sober life.  Mr. and Mrs. Poyner will agree not to associate with any persons who would place their children at risk of abuse\\/neglect.  Mr. and Mrs. Poyner will report any arrest\\/convictions\\/filing of criminal charges to CPS within five days.");
		taskGoalValueDto.setDateLastUpdate(sdf.parse("08/07/2007"));
		taskGoalValueDto.setDateCreated(Calendar.getInstance().getTime());
		taskGoalValueDtoList.add(taskGoalValueDto);
		inputMap.put("TASKLIST", taskGoalValueDtoList);

		return inputMap;
	}

	@RequestMapping(value = "/updateApprovers1", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void updateApprovers(@RequestBody SaveApprovalStatusReq saveApprovalStatusReq) {
		log.info("ApprovalStatusDaoTestController.updateApprovers()");
		approvalStatusDaoTestService.updateApprovers(saveApprovalStatusReq);
		return;
	}

	@RequestMapping(value = "/closePlacement", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void closePlacement(@RequestBody ClosePlacementReq closePlacementReq) {
		log.info("ApprovalStatusDaoTestController.updateApprovers()");
		approvalStatusDaoTestService.closePlacement(closePlacementReq);
		return;
	}

	@RequestMapping(value = "/updatePersonEligibility", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void updatePersonEligibility(@RequestBody PersonEligibilityValueDto personEligibilityValueDto) {
		log.info("ApprovalStatusDaoTestController.updateApprovers()");
		approvalStatusDaoTestService.updatePersonEligibility(personEligibilityValueDto);
		return;
	}

	@RequestMapping(value = "/updateStagePersonLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public Long updateStagePersonLink(@RequestBody CommonHelperReq CommonHelperReq) {
		log.info("ApprovalStatusDaoTestController.updateStagePersonLink()");
		return approvalStatusDaoTestService.updateStagePersonLink(CommonHelperReq);

	}

	@RequestMapping(value = "/getPersonEligibilityByIdPersonAndType", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public List<PersonEligibility> getPersonEligibilityByIdPersonAndType() {
		log.info("ApprovalStatusDaoTestController.retrieveStageInfoList()");
		CommonDto commonDto1 = new CommonDto();
		commonDto1.setIdPerson(20250580L);
		commonDto1.setPersonType("001");
		return approvalStatusDaoTestService.getPersonEligibilityByIdPersonAndType(commonDto1);
	}

	@RequestMapping(value = "/updateApprovers2", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void updateApprovers()
			throws ParseException {
		log.info("ApprovalStatusDaoTestController.updateApprovers()");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ApprovalStatusFacilityIndicatorDto ApprovalStatusFacilityIndicatorDto1 = new ApprovalStatusFacilityIndicatorDto();
		ApprovalStatusFacilityIndicatorDto1.setDtApproversDetermination(sdf.parse("04/12/2006"));
		ApprovalStatusFacilityIndicatorDto1.setIdPerson(1444);
		ApprovalStatusFacilityIndicatorDto1.setTxtApproversComments("Soap UI Update");
		ApprovalStatusFacilityIndicatorDto1.setCdApprovalLength("null");
		ApprovalStatusFacilityIndicatorDto1.setIndWithinWorkerControl("null");
		ApprovalStatusFacilityIndicatorDto1.setCdOvrllDisptn("null");
		ApprovalStatusFacilityIndicatorDto1.setCdStageReasonClosed("null");
		ApprovalStatusFacilityIndicatorDto1.setIdApprovers(24247106);
		approvalStatusDaoTestService.updateApprovers(ApprovalStatusFacilityIndicatorDto1);
	}

	@RequestMapping(value = "/updateCapsResource", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public void updateCapsResource(@RequestBody AprvlStatusUpdateCapsResourceReq AprvlStatusUpdateCapsResourceReq) {
		log.info("ApprovalStatusDaoTestController.updateCapsResource()");
		approvalStatusDaoTestService.updateCapsResource(AprvlStatusUpdateCapsResourceReq);
	}

	@RequestMapping(value = "/getCapsRsc", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public List<SaveApprovalStatusGetCapsRscRes> getCapsRsc(
			@RequestBody SaveApprovalStatusGetCapsRscReq SaveApprovalStatusGetCapsRscReq) {
		log.info("ApprovalStatusDaoTestController.getCapsRsc()");
		return approvalStatusDaoTestService.getCapsRsc(SaveApprovalStatusGetCapsRscReq);
	}

	@RequestMapping(value = "/fetchResourceHisty", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public List<ApprovalStatusResourceHistyRes> fetchResourceHisty(
			@RequestBody ApprovalStatusResourceHistyReq ApprovalStatusResourceHistyReq) {
		log.info("ApprovalStatusDaoTestController.fetchResourceHisty()");
		return approvalStatusDaoTestService.fetchResourceHisty(ApprovalStatusResourceHistyReq);
	}

	@RequestMapping(value = "/updateFacilityIndicator", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public void updateFacilityIndicator(@RequestBody SaveApprovalStatusReq SaveApprovalStatusReq) {
		log.info("ApprovalStatusDaoTestController.updateFacilityIndicator()");
		approvalStatusDaoTestService.updateFacilityIndicator(SaveApprovalStatusReq);
	}

	@RequestMapping(value = "/getNmStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public SaveApprovalStatusNmStageRes getNmStage(
			@RequestBody SaveApprovalStatusNmStageReq SaveApprovalStatusNmStageReq) {
		log.info("ApprovalStatusDaoTestController.getNmStage()");
		return approvalStatusDaoTestService.getNmStage(SaveApprovalStatusNmStageReq);
	}

	@RequestMapping(value = "/getEmployeeInfo2", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public EmployeeDetailDto getEmployeeInfo2(@RequestBody SaveApprovalStatusNmStageReq SaveApprovalStatusNmStageReq) {
		log.info("ApprovalStatusDaoTestController.getEmployeeInfo()");
		return approvalStatusDaoTestService.getEmployeeInfo(SaveApprovalStatusNmStageReq);
	}

	@RequestMapping(value = "/getResourceCount", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public GetResourceHstryRes getResourceCount(@RequestBody GetResourceHstryReq GetResourceHstryReq) {
		log.info("ApprovalStatusDaoTestController.getResourceCount()");
		return approvalStatusDaoTestService.getResourceCount(GetResourceHstryReq);
	}

}
