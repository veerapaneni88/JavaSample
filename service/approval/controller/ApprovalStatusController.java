/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 23, 2017- 10:59:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.approval.controller;

import static us.tx.state.dfps.service.common.CodesConstant.KIN_TASK_APRV;
import static us.tx.state.dfps.service.common.CodesConstant.KIN_TASK_REJ;

import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.approval.service.ApprovalStatusService;
import us.tx.state.dfps.service.approval.service.SaveApprovalStatusService;
import us.tx.state.dfps.service.casepackage.dto.SSCCRefDto;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;
import us.tx.state.dfps.service.workload.service.ToDoService;
import us.tx.state.dfps.service.approval.service.GoldWebService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 23, 2017- 10:59:00 AM © 2017 Texas Department of
 * Family and Protective Services
 * ************** Change history *****************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 */
@RestController
@RequestMapping(value = "/approval")

public class ApprovalStatusController {
	private static final Logger log = Logger.getLogger(ApprovalStatusController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	ApprovalStatusService approvalStatusService;

	@Autowired
	ToDoService toDoService;

	@Autowired
	SaveApprovalStatusService saveApprovalStatus;

	@Autowired
	private PostEventService postEventService;

	@Autowired
	GoldWebService goldWebService;

	/**
	 * Method Name: saveApproval Method Description: This method is used to save
	 * the assign staff to print closure notifications in approval status page
	 * 
	 * @param approvalPersonReq
	 * @return ApprovalPersonRes
	 */
	@RequestMapping(value = "/saveApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes saveApproval(@RequestBody ApprovalPersonReq approvalPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("msg.approvalPersonDto.mandatory", null, Locale.US));
		} else if (TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		} else if (TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		} else if (TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		ApprovalPersonRes approvalPersonRes = approvalStatusService.saveApproval(approvalPersonReq);

		return approvalPersonRes;
	}

	/**
	 * * Method Name: getApproval Method Description: This method is to retrieve
	 * the name of assign staff to print in approval status page
	 * 
	 * @param approvalPersonReq
	 * @return ApprovalPersonRes 
	 */
	@RequestMapping(value = "/getApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes getApproval(@RequestBody ApprovalPersonReq approvalPersonReq) {
		if (TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto().getIdEvent())
				&& TypeConvUtil.isNullOrEmpty(approvalPersonReq.getApprovalPersonDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.id.mandatory", null, Locale.US));
		}
		return approvalStatusService.getApproval(approvalPersonReq);
	}

	/**
	 * Method Name:isSecondLevelApproverRequiredForCPSINV * Method Description:
	 * The Method returns if the Second level Approval is required for CPS
	 * Investigation Conclusion Approval.
	 * 
	 * @param secondaryApprovalReq
	 * @return
	 */
	@RequestMapping(value = "/isSecondLevelApproverRequiredForCPSINV", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SecondaryApprovalRes isSecondLevelApproverRequiredForCPSINV(
			@RequestBody SecondaryApprovalReq secondaryApprovalReq) {
		if (TypeConvUtil.isNullOrEmpty(secondaryApprovalReq.getSecondaryApprovalDto().getIdEvent())
				|| TypeConvUtil.isNullOrEmpty(secondaryApprovalReq.getSecondaryApprovalDto().getIdStage())
				|| TypeConvUtil.isNullOrEmpty(secondaryApprovalReq.getSecondaryApprovalDto().getIdCase())
				|| TypeConvUtil.isNullOrEmpty(secondaryApprovalReq.getSecondaryApprovalDto().getCdStageProgram())) {
			throw new InvalidRequestException(messageSource.getMessage("msg.id.mandatory", null, Locale.US));
		}
		SecondaryApprovalRes secondaryApprovalRes = new SecondaryApprovalRes();
		secondaryApprovalRes.setSecondaryApprovalRequired(approvalStatusService
				.isSecondLevelApproverRequiredForCPSINV(secondaryApprovalReq.getSecondaryApprovalDto()));

		return secondaryApprovalRes;
	}

	/**
	 * Method Name: updateTodos Method Description: Manual Stage Progression INT
	 * to A-R This function updates To do Table. EJB Name : To DoBean.java
	 * 
	 * @param todoReq
	 * @return ToDosRes
	 */
	@RequestMapping(value = "/updateTodos", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  TodoRes updateTodos(@RequestBody TodoReq todoReq) {
		TodoRes toDosRes = new TodoRes();
		toDosRes.setResult(toDoService.updateToDos(todoReq.getTodoDto()));
		toDosRes.setTodoDtoList(todoReq.getTodoDto());
		return toDosRes;
	}

	/**
	 * Method Name: deleteToDosForStage Method Description: This function
	 * deletes Tasks / Alerts of the given Type for the given Stage. EJB Name :
	 * ToDoBean.java
	 * 
	 * @param todoReq
	 * @return ToDoStageRes
	 */
	@RequestMapping(value = "/deleteToDosForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  TodoRes deleteToDosForStage(@RequestBody TodoReq todoReq) {

		log.info("approvalStatusController.deleteToDosForStage()");

		if (TypeConvUtil.isNullOrEmpty(todoReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idstage.mandatory", null, Locale.US));
		}

		TodoRes todoRes = new TodoRes();
		toDoService.deleteToDosForStage(todoReq.getIdStage(), todoReq.getTodoInfoList());
		todoRes.setIdStage(todoReq.getIdStage());
		todoRes.setTodoInfoList(todoReq.getTodoInfoList());
		return todoRes;
	}

	/**
	 * Method Name: getDayCareApproval Method Description:This method determine
	 * where it is Day care Request Service Authorization Approval or Regular
	 * Approval
	 * 
	 * @param serviceAuthGetReq
	 * @return ServiceAuthGetRes
	 */
	@RequestMapping(value = "/getDayCareApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceAuthGetRes getDayCareApproval(@RequestBody ServiceAuthGetReq serviceAuthGetReq) {
		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		ServiceAuthGetRes serviceAuthRes = new ServiceAuthGetRes();
		serviceAuthRes.setDayCareRequest(approvalStatusService.getDayCareApproval(serviceAuthGetReq.getIdEvent()));
		return serviceAuthRes;
	}

	/**
	 * 
	 * Method Name: getSSCCReferalForIdPersonDC Method Description: Get the
	 * Active Placement Referral for Day care Request
	 * 
	 * @param serviceAuthGetReq
	 * @return ServiceAuthGetRes
	 */
	@RequestMapping(value = "/getSSCCReferalForIdPersonDC", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceAuthGetRes getSSCCReferalForIdPersonDC(
			@RequestBody ServiceAuthGetReq serviceAuthGetReq) {
		ServiceAuthGetRes serviceAuthGetRes = new ServiceAuthGetRes();

		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		serviceAuthGetRes
				.setIdSSCCRererral(approvalStatusService.getSSCCReferalForIdPersonDC(serviceAuthGetReq.getIdEvent()));
		return serviceAuthGetRes;
	}

	/**
	 * MethodName: updateSSCCReferral Method Description: This method sets
	 * SSCC_REFERRAL table with IND_LINKED_SVC_AUTH_DATA = 'Y',
	 * DT_LINKED_SVC_AUTH_DATA = SYSDATE for the given SSCC Referral Id. and
	 * sets SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'.
	 * 
	 * @param ssccReferralReq
	 * @return Updated ssccRefDto/ssccReferral
	 */
	@RequestMapping(value = "/updateSSCCReferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCReferralRes updateSSCCReferral(@RequestBody SSCCReferralReq ssccReferralReq) {
		SSCCReferralRes ssccReferralRes = new SSCCReferralRes();
		if (TypeConvUtil.isNullOrEmpty(ssccReferralReq.getIdSSCCReferral())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sscc.idSSCCReferral.mandatory", null, Locale.US));
		}
		SsccReferral ssccReferral = approvalStatusService.updateSSCCReferral(ssccReferralReq.getIdSSCCReferral());
		if (!TypeConvUtil.isNullOrEmpty(ssccReferral)) {
			SSCCRefDto ssccRefDto = new SSCCRefDto();
			BeanUtils.copyProperties(ssccReferral,ssccRefDto);
			ssccRefDto.setIdSSCCReferral(ssccReferral.getIdSSCCReferral());
		
			ssccReferralRes.setSsccRefDto(ssccRefDto);
		}
		ssccReferralRes.setIdSSCCReferral(ssccReferral.getIdSSCCReferral());

		return ssccReferralRes;
	}

	/**
	 * Method Name: getSSCCReferralForIdPersonPALorSUB Method Description: This
	 * method gets SSCC Referral id for the person id in PAL or SUB stage.
	 * 
	 * @param serviceAuthGetReq
	 * @return ServiceAuthGetRes
	 */
	@RequestMapping(value = "/getSSCCReferralForIdPersonPALorSUB", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceAuthGetRes getSSCCReferralForIdPersonPALorSUB(
			@RequestBody ServiceAuthGetReq serviceAuthGetReq) {
		ServiceAuthGetRes serviceAuthGetRes = new ServiceAuthGetRes();
		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		serviceAuthGetRes.setIdSSCCRererral(
				approvalStatusService.getSSCCReferralForIdPersonPALorSUB(serviceAuthGetReq.getIdEvent()));
		return serviceAuthGetRes;
	}

	/**
	 * Method Name: updateSSCCList Method Description: Update a
	 * IND_NONSSCC_SVC_AUTH as 'Y' into the SSCC_LIST table for
	 * 
	 * @param sSCCReferralReq
	 * @return ssccListRes
	 */
	@RequestMapping(value = "/updateSSCCList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCReferralRes updateSSCCList(@RequestBody SSCCReferralReq sSCCReferralReq) {
		SSCCReferralRes resp = new SSCCReferralRes();
		if (!TypeConvUtil.isNullOrEmpty(sSCCReferralReq.getIdSSCCReferral())) {
			resp.setSsccListDto(approvalStatusService.updateSSCCList(sSCCReferralReq.getIdSSCCReferral()));
		}
		return resp;
	}

	/**
	 * Method Name: getSSCCReferralFamilyForIdPerson Method Description: This
	 * method gets SSCC Referral Family id for the person id in NOT (SUB or PAL)
	 * stage.
	 * 
	 * @param serviceAuthGetReq
	 * @return ServiceAuthGetRes
	 */
	@RequestMapping(value = "/getSSCCReferralFamilyForIdPerson", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ServiceAuthGetRes getSSCCReferralFamilyForIdPerson(
			@RequestBody ServiceAuthGetReq serviceAuthGetReq) {
		log.info("approvalStatusController.getSSCCReferralFamilyForIdPerson()");
		ServiceAuthGetRes serviceAuthGetRes = new ServiceAuthGetRes();
		serviceAuthGetRes.setTransactionId(serviceAuthGetReq.getTransactionId());
		if (TypeConvUtil.isNullOrEmpty(serviceAuthGetReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		serviceAuthGetRes.setIdSSCCRererral(
				approvalStatusService.getSSCCReferralFamilyForIdPerson(serviceAuthGetReq.getIdEvent()));

		return serviceAuthGetRes;
	}

	/**
	 * Method Name: getVendorId Method Description: This method gets the Vendor
	 * Id for the given event id
	 * 
	 * @param placementReq
	 * @return VendorRes
	 */
	@RequestMapping(value = "/getVendorId", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes getVendorId(@RequestBody PlacementReq placementReq) {
		PlacementRes placementRes = new PlacementRes();

		if (TypeConvUtil.isNullOrEmpty(placementReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("sscc.idEvent.mandatory", null, Locale.US));
		}

		placementRes.setVid(approvalStatusService.getVendorId(placementReq.getIdEvent()));
		return placementRes;
	}

	/**
	 * Method Name: isVendorIdExistsBatchParameters Method Description:This
	 * method checks whether vendor id exists or not in BATCH_SSCC_PARAMETERS
	 * table
	 * 
	 * @param vendorReq
	 * @return VendorRes
	 */
	@RequestMapping(value = "/isVendorIdExistsBatchParameters", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PlacementRes isVendorIdExistsBatchParameters(@RequestBody PlacementReq vendorReq) {

		PlacementRes vendorRes = new PlacementRes();

		if (TypeConvUtil.isNullOrEmpty(vendorReq.getVid())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		vendorRes.setVidExists(approvalStatusService.isVendorIdExistsBatchParameters(vendorReq.getVid()));
		return vendorRes;
	}

	/**
	 * MethodName: updateSSCCReferralFamily MethoDescription:This method sets
	 * SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'
	 *
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param sSCCReq
	 * @return SSCCRes
	 */
	@RequestMapping(value = "/updateSSCCReferralFamily", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SSCCRes updateSSCCReferralFamily(@RequestBody SSCCReq sSCCReq) {

		SSCCRes sSCCRes = new SSCCRes();

		if (TypeConvUtil.isNullOrEmpty(sSCCReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("sscc.idEvent.mandatory", null, Locale.US));
		}

		sSCCRes.setIdEvent(approvalStatusService.updateSSCCReferralFamily(sSCCReq.getIdEvent()));
		return sSCCRes;
	}

	/**
	 * Method Name: fetchTodoDtlAdptAssistNxtRecrtRevw Method Description: Fetch
	 * the To do Details of the next recent open To do task for the stage.
	 * 
	 * @param recertificationReq
	 * @return RecertificationRes
	 */
	@RequestMapping(value = "/fetchTodoDtlAdptAssistNxtRecrtRevw", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecertificationRes fetchTodoDtlAdptAssistNxtRecrtRevw(
			@RequestBody RecertificationReq recertificationReq) {

		if (TypeConvUtil.isNullOrEmpty(recertificationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(recertificationReq.getCdEventTaskCode())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.cdEventTaskCode.mandatory", null, Locale.US));
		}
		RecertificationRes recertificationRes = new RecertificationRes();

		recertificationRes.setListToDoDto(approvalStatusService.fetchTodoDtlAdptAssistNxtRecrtRevw(
				recertificationReq.getIdStage(), recertificationReq.getCdEventTaskCode()));
		return recertificationRes;
	}

	/**
	 * Method Name: approveKinHome Method Description: This method is used to
	 * update kinship approval.
	 *
	 * @param kinApprovalReq
	 * @return KinApprovalRes
	 */
	@RequestMapping(value = "/approveKinHome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  KinApprovalRes approveKinHome(@RequestBody KinApprovalReq kinApprovalReq) {

		if (ObjectUtils.isEmpty(kinApprovalReq.getApproversDto())
				&& ObjectUtils.isEmpty(kinApprovalReq.getEventDto())
				&& ObjectUtils.isEmpty(kinApprovalReq.getAppEventDto())
				&& ObjectUtils.isEmpty(kinApprovalReq.getToDoValueDto())
				&& ObjectUtils.isEmpty(kinApprovalReq.getKinHomeInfoDto())) {
			throw new InvalidRequestException(messageSource
					.getMessage("controller.missed.mandatory.field", null, Locale.US));
		}

		KinApprovalRes kinApprovalRes = new KinApprovalRes();
		kinApprovalRes.setTransactionId(kinApprovalRes.getTransactionId());

		kinApprovalRes.setUpdateResult(approvalStatusService.approveKinHome(kinApprovalReq.getApproversDto(),
				kinApprovalReq.getEventDto(), kinApprovalReq.getAppEventDto(), kinApprovalReq.getToDoValueDto(),
				kinApprovalReq.getKinHomeInfoDto()));

		return kinApprovalRes;
	}

	@RequestMapping(value = "/rejectKinHome", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes rejectKinHome(@RequestBody KinRejectApprovalReq kinRejectApprovalReq) {
		CommonBooleanRes commonBooleanRes = new CommonBooleanRes();

		if (ObjectUtils.isEmpty(kinRejectApprovalReq) ) {
			throw new InvalidRequestException(messageSource
					.getMessage("controller.missed.mandatory.field", null, Locale.US));
		}

		Integer updatedRecordCount = approvalStatusService.rejectKinHome(kinRejectApprovalReq);

		commonBooleanRes.setExists(!ObjectUtils.isEmpty(updatedRecordCount) && updatedRecordCount > 0);

		return commonBooleanRes;
	}

	/**
	 * Method Name: approveKinMonthlyPayment Method Description: This method is used to
	 * update kinship approval.
	 *
	 * @param kinApprovalReq
	 * @return KinApprovalRes
	 */
	@RequestMapping(value = "/approveKinMonthlyPayment", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public KinApprovalRes approveKinMonthlyPayment(@RequestBody KinApprovalReq kinApprovalReq) {

		if (TypeConvUtil.isNullOrEmpty(kinApprovalReq.getApproversDto())
				&& TypeConvUtil.isNullOrEmpty(kinApprovalReq.getEventDto())
				&& TypeConvUtil.isNullOrEmpty(kinApprovalReq.getAppEventDto())
				&& TypeConvUtil.isNullOrEmpty(kinApprovalReq.getToDoValueDto())) {
			throw new InvalidRequestException(messageSource
					.getMessage("KinApprovalController.approveKinMonthlyPayment.missed.mandatory.field", null, Locale.US));
		}

		KinApprovalRes kinApprovalRes = new KinApprovalRes();
		kinApprovalRes.setTransactionId(kinApprovalRes.getTransactionId());

		kinApprovalRes.setUpdateResult(approvalStatusService.approveKinMonthlyPayment(kinApprovalReq.getKinHomeInfoDto(),
				kinApprovalReq.getEventDto(), kinApprovalReq.getAppEventDto(), kinApprovalReq.getToDoValueDto()));

		return kinApprovalRes;
	}

	/**
	 * Method Name: createTodoNxtRecertReview Method Description: Creates a next
	 * recert To do in the newly opened PAD stage succeeding the ADO stage
	 *
	 * @param recertificationReq
	 * @return RecertificationRes
	 * 
	 */
	@RequestMapping(value = "/createTodoNxtRecertReview", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  RecertificationRes createTodoNxtRecertReview(
			@RequestBody RecertificationReq recertificationReq) {

		if (TypeConvUtil.isNullOrEmpty(recertificationReq.getAdptAssistRecertTodoDetail())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.adptAssistRecertTodoDetail.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(recertificationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		RecertificationRes recertificationRes = new RecertificationRes();

		recertificationRes.setIdStagePadvalue(approvalStatusService.createTodoNxtRecertReview(
				recertificationReq.getAdptAssistRecertTodoDetail(), recertificationReq.getIdStage()));
		return recertificationRes;
	}

	/**
	 * Method Name: createRCLAlert Method Description: Creates alerts for Child Sexual Aggression, trafficking, sexual behavior, Alleged Aggressor of Child Sexual Aggression
	 * for the sub worker and the supervisor  
	 * added for artf129782
	 * @param createRCLAlertReq
	 * @return CreateRCLAlertRes
	 * 
	 */
	@RequestMapping(value = "/createRCLAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CreateRCLAlertRes createRCLAlert(
			@RequestBody CreateRCLAlertReq createRCLAlertReq) {

		if (TypeConvUtil.isNullOrEmpty(createRCLAlertReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}		
		CreateRCLAlertRes createRCLAlertRes = new CreateRCLAlertRes();
		toDoService.createRCLAlerts(createRCLAlertReq.getIdStage(), createRCLAlertReq.getIdEvent(), createRCLAlertReq.getAssignedTo(), 
		                createRCLAlertReq.getDescription(), createRCLAlertReq.getIdCreatedPerson());
		createRCLAlertRes.setIdEvent(createRCLAlertReq.getIdEvent());
		return createRCLAlertRes;
	}

	/**
	 * Method Name: processARConclusionStage Method Description: This method
	 * will look at the closure reason and decide if it needs to progress the
	 * case to INV or to close the AR Stage or progress to FPR
	 * 
	 * @param arStageProgReq
	 * @return ARStageProgRes
	 */
	@RequestMapping(value = "/processARConclusionStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes processARConclusionStage(@RequestBody CommonHelperReq arStageProgReq) {

		if (TypeConvUtil.isNullOrEmpty(arStageProgReq.getIdCase())
				|| TypeConvUtil.isNullOrEmpty(arStageProgReq.getIdFromStage())
				|| TypeConvUtil.isNullOrEmpty(arStageProgReq.getIdUser())
				|| TypeConvUtil.isNullOrEmpty(arStageProgReq.getIdApproval())) {
			throw new InvalidRequestException(messageSource.getMessage("arstageprog.createarstage", null, Locale.US));
		}
		CommonHelperRes arStageProgRes = new CommonHelperRes();
		arStageProgRes.setOverallDisposition(approvalStatusService.processARConclusionStage(
				arStageProgReq.getIdCase().intValue(), arStageProgReq.getIdFromStage(), arStageProgReq.getIdUser(),
				arStageProgReq.getIdApproval()));

		return arStageProgRes;

	}

	/**
	 * Method Name: createInvestigationAlerts Method Description: This function
	 * creates 30-Day / 37-Day INV Case worker Alerts. EJB Name : ToDoBean.java
	 * 
	 * @param investigationAlertReq
	 * @return InvestigationAlertRes
	 */
	@RequestMapping(value = "/createInvestigationAlerts", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes createInvestigationAlerts(@RequestBody InvdApprReq investigationAlertReq){
		CommonHelperRes investigationAlertRes = new CommonHelperRes();

		if (TypeConvUtil.isNullOrEmpty(investigationAlertReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.idCase.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(investigationAlertReq.getIdIntakeStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("todo.idIntakeStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(investigationAlertReq.getIdLoggedInWorker())) {
			throw new InvalidRequestException(
					messageSource.getMessage("todo.idLoggedInWorker.mandatory", null, Locale.US));
		}
		toDoService.createInvestigationAlerts(investigationAlertReq.getIdCase(),
				investigationAlertReq.getIdIntakeStage(), investigationAlertReq.getIdLoggedInWorker());
		investigationAlertRes.setIdCase(investigationAlertRes.getIdCase());
		investigationAlertRes.setIdIntakeStage(investigationAlertReq.getIdIntakeStage());
		investigationAlertRes.setIdLoggedInWorker(investigationAlertReq.getIdLoggedInWorker());
		return investigationAlertRes;
	}

	/**
	 * Fetch boolean for Secondary Approval Required or Not
	 * 
	 * @param secondaryApprovalReq
	 * @return ApprovalRes
	 * 
	 */
	@RequestMapping(value = "/isSecondaryApprovalRequired", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SecondaryApprovalRes isSecondaryApprovalRequired(
			@RequestBody SecondaryApprovalReq secondaryApprovalReq) {

		if (ObjectUtils.isEmpty(secondaryApprovalReq.getSecondaryApprovalDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approval.SecondaryApprovalDto.mandatory", null, Locale.US));
		}

		SecondaryApprovalRes approvalStatusRes = new SecondaryApprovalRes();
		approvalStatusRes.setSecondaryApprovalRequired(
				approvalStatusService.isSecondaryApprovalRequired(secondaryApprovalReq.getSecondaryApprovalDto()));
		return approvalStatusRes;
	}

	/**
	 * MethodName: getPendingApprovalCount MethodDescription: Fetch Pending
	 * Approval Count of the Pending Approvals for the given Approval Id.
	 * 
	 * @param approvalReq
	 * @return ApprovalStatusRes
	 * 
	 */
	@RequestMapping(value = "/getPendingApprovalCount", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalStatusRes getPendingApprovalCount(@RequestBody ApprovalStatusReq approvalReq) {

		if (TypeConvUtil.isNullOrEmpty(approvalReq.getIdApproval())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approval.idApproval.mandatory", null, Locale.US));
		}

		ApprovalStatusRes approvalRes = new ApprovalStatusRes();
		approvalRes.setPendingCount(approvalStatusService.getPendingApprovalCount(approvalReq.getIdApproval()));
		return approvalRes;
	}

	/**
	 * Saves information change on Approval Window (CCMN35S). Updates the status
	 * of all related events to the Approval. Sends out appropriate To-Do
	 * notifications.
	 *
	 * @param saveApprovalStatusReq
	 * @return
	 */
	@RequestMapping(value = "/saveApprovalStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalStatusRes saveApprovalStatus(
			@RequestBody SaveApprovalStatusReq saveApprovalStatusReq) {
		if (ObjectUtils.isEmpty(saveApprovalStatusReq.getIdTodo())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approvalstatus.todoid.mandatory", null, Locale.US));
		}
		ApprovalStatusRes approvalStatusRes = new ApprovalStatusRes();
		approvalStatusRes = saveApprovalStatus.saveApprovalStatus(saveApprovalStatusReq);
		return approvalStatusRes;
	}

	@RequestMapping(value = "/checkRegionChanged", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalStatusRes checkRegionChanged(@RequestBody CommonHelperReq approvalReq) {
		ApprovalStatusRes approvalRes = new ApprovalStatusRes();
		String placementRegion = approvalStatusService.checkRegionChange(approvalReq.getIdEvent(),
				approvalReq.getIdCase());
		Boolean indRegionChanged = ObjectUtils.isEmpty(placementRegion) ? Boolean.FALSE : Boolean.TRUE;
		approvalRes.setIndRegionChanged(indRegionChanged);
		approvalRes.setPlacementRegion(placementRegion);
		return approvalRes;
	}

	/**
	 * Method Name: getICPCApprovalLevel Method Description: controller method
	 * to get the approval level for ICPC
	 * 
	 * @param approvalReq
	 * @return ApprovalStatusRes
	 */
	@RequestMapping(value = "/getICPCApprovalLevel", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalStatusRes getICPCApprovalLevel(@RequestBody CommonHelperReq approvalReq) {

		if (TypeConvUtil.isNullOrEmpty(approvalReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approvalstatus.eventid.mandatory", null, Locale.US));
		}
		ApprovalStatusRes approvalRes = new ApprovalStatusRes();
		Long icpcApproval = approvalStatusService.getICPCApprovalLevel(approvalReq.getIdEvent());
		approvalRes.setICPCApprovalLevel(icpcApproval);
		return approvalRes;
	}

	/**
	 * Method Name: isApproverLoggedIn Method Description: controller method to
	 * get get the approver logged in person id
	 * 
	 * @param approvalReq
	 * @return ApprovalStatusRes
	 */
	@RequestMapping(value = "/isApproverLoggedIn", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalStatusRes isApproverLoggedIn(@RequestBody CommonHelperReq approvalReq) {

		if (TypeConvUtil.isNullOrEmpty(approvalReq.getIdApproval())) {
			throw new InvalidRequestException(
					messageSource.getMessage("approvalstatus.approvalid.mandatory", null, Locale.US));
		}
		ApprovalStatusRes approvalRes = approvalStatusService.isApproverLoggedIn(approvalReq.getIdApproval());

		return approvalRes;
	}

	@RequestMapping(value = "/participants", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ChildPlanParticipantEmailRes getParticipants(@RequestBody SaveApprovalStatusReq saveApprovalStatusReq) {
		if (ObjectUtils.isEmpty(saveApprovalStatusReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return saveApprovalStatus.getParticipants(saveApprovalStatusReq);
	}

	@RequestMapping(value = "/updateDateCopyProvided", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public ChildPlanParticipantEmailRes updateDateCopyProvided(@RequestBody CommonHelperReq CommonHelperReq) {
		if (ObjectUtils.isEmpty(CommonHelperReq.getPersonIds())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return saveApprovalStatus.updateDateCopyProvided(CommonHelperReq);
	}

	/**
	 * Method Name: getBoardEmail Method Description: This method retrieves
	 * board member's email addresses based on placement county.
	 * 
	 * @param commonHelperReq
	 * @return ApprovalStatusRes
	 */
	@RequestMapping(value = "/getBoardEmail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public ApprovalStatusRes getBoardEmail(@RequestBody CommonHelperReq commonHelperReq) {

		ApprovalStatusRes approvalRes = new ApprovalStatusRes();
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getNmStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.nmStage.mandatory", null, Locale.US));
		}

		approvalRes.setEmailAddress(
				approvalStatusService.getBoardEmail(commonHelperReq.getIdCase(), commonHelperReq.getNmStage()));

		return approvalRes;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: generateFBSSAlerts
	 * Method Description: This method generate the FBSS alerts for Preceding or Progressed stages
	 *
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/generateFBSSAlerts", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes generateFBSSAlerts(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null,
					Locale.US));
		}

		approvalStatusService.generateAlertIfStageOpen(commonHelperReq.getIdStage(), commonHelperReq.getCdStage(),
				commonHelperReq.getIdCase(), (long) commonHelperReq.getIdUser(), commonHelperReq.isFbssSdmRa());

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(true);
		return commonHelperRes;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: cpsCopyOpenServiceAuth
	 * Method Description: This method is used for CPS - INV/A-R to copy open Service Auth to FPR
	 *
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/cpsCopyOpenServiceAuth", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes cpsCopyOpenServiceAuth(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdUser())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null,
					Locale.US));
		}

		approvalStatusService.cpsCopyOpenServiceAuth(commonHelperReq.getIdStage(), commonHelperReq.getCdStage(),
				(long) commonHelperReq.getIdUser());

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(true);
		return commonHelperRes;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: getCpsProgressedStage
	 * Method Description: This method generate the FBSS alerts for Preceding or Progressed stages
	 *
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/getCpsProgressedStage", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes getCpsProgressedStage(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null,
					Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getStageType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null,
					Locale.US));
		}

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setIdProgressedStage(approvalStatusService.getCpsProgressedStageForSelectedStage(commonHelperReq.getIdStage(),
				commonHelperReq.getCdStage(), commonHelperReq.getStageType()));
		return commonHelperRes;
	}

	/**
	 * This method will update the EMR status to DB.
	 *
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/updateEmrStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes updateEmrApprovalStatus(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null,
					Locale.US));
		}

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		Long temp = approvalStatusService.updateEmrStatusForSelectedStage(commonHelperReq.getEmrStatus(), commonHelperReq.getIdStage());
		commonHelperRes.setIsEmrStatusUpdated(temp == 0 ? false : true);
		return commonHelperRes;
	}

	/**
	 * Artifact ID: artf164464
	 * Method Name: checkPcspOpenOnApproval
	 * Method Description: This method checks if there is an open PCSP in the case and there are no other PCSP
	 * applicable stages (INV, A-R, FPR, FSU, FRE) open in the case
	 *
	 * @param commonHelperReq
	 * @return CommonHelperRes
	 */
	@RequestMapping(value = "/checkPcspOpenOnApproval", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonHelperRes checkPCSPOpenOnApproval(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())
				|| TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())
				|| TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null,
					Locale.US));
		}

		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setResult(approvalStatusService.checkPCSPOpenOnApproval(commonHelperReq.getIdCase(),
				commonHelperReq.getIdStage(), commonHelperReq.getCdStage()));
		return commonHelperRes;
	}


	@RequestMapping(value = "/homeAssessment", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public void saveHomeAssessmentApproval(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdPerson()) && TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource
					.getMessage("ApprovalController.approveIdCaseOrIdStage.missed.mandatory.field", null, Locale.US));
		}
		approvalStatusService.saveHomeAssessmentApproval(commonHelperReq);
	}


	@RequestMapping(value = "/deleteTodosByTaskAndCase", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  void  deleteContactCciStaffingTodos(@RequestBody TodoReq todoReq) {
		log.info("approvalStatusController.deleteTodosByTaskAndCase()");
		if (TypeConvUtil.isNullOrEmpty(todoReq.getTodoDto())) {
			throw new InvalidRequestException(messageSource.getMessage("todo.TodoDto.mandatory", null, Locale.US));
		}
		for (TodoDto todoDto : todoReq.getTodoDto()) {
			approvalStatusService.deleteTodosByTaskAndCase(todoReq.getIdCase(), todoDto.getCdTask());
		}


	}

	@RequestMapping(value = "/createAlert", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public TodoUpdateRes createAlert(
			@RequestBody TodoUpdateReq todoUpdateReq) {
		TodoUpdateRes todoUpdateRes = new TodoUpdateRes();
		TodoDto todoDto = new TodoDto();
		todoDto.setIdTodo(toDoService.createAlert( todoUpdateReq.getTodoDto()));
		todoUpdateRes.setTodoDto(todoDto);
		return todoUpdateRes;
	}

	@RequestMapping(value = "/createEvent", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public PostEventRes createEvent(@RequestBody PostEventReq postEventReq) {
		PostEventRes postEventRes = new PostEventRes();
		postEventRes.setPostEventOPDto(
				postEventService.checkPostEventStatus(postEventReq.getPostEventIPDto(), postEventReq.getServiceReqHeaderDto()));
		return postEventRes;
	}


	@RequestMapping(value = "/sendAndSave", headers = {"Accept=application/json"}, method = RequestMethod.POST)
	public GoldCommunicationDto insertIntoFormsAndData(@RequestBody CommonHelperReq commonHelperReq) throws SQLException {
		Long idEvent = commonHelperReq.getIdEvent();
		//Call Service impl and save data into WEBSVC_FORM_TRANS
		GoldCommunicationDto dto = goldWebService.sendAndSave(idEvent);
		return dto;
	}
}