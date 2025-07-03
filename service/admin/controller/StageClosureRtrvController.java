package us.tx.state.dfps.service.admin.controller;

import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.StageClosureRtrvDto;
import us.tx.state.dfps.service.admin.service.StageClosureRtrvService;
import us.tx.state.dfps.service.common.request.StageClosureRtrvReq;
import us.tx.state.dfps.service.common.response.StageClosureRtrvRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * controller StageClosure for FRE , FSU,SUB , ADO, PAC, AOC Aug 19, 2017-
 * 8:49:34 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/stageClosureRtrv")
public class StageClosureRtrvController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	StageClosureRtrvService stageClosureRtrvService;

	private static final Logger log = Logger.getLogger("ServiceBusiness-StageClosureRtrvController");

	/**
	 * 
	 * Method Name: getStageClosure Method Description: this Service Retrievies
	 * StageClosure Display details( CSUB67S and extra)
	 * 
	 * @param stageClosureRtrvReq
	 * @return StageClosureRtrvRes
	 */
	@RequestMapping(value = "/getStageClosure", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public StageClosureRtrvRes getStageClosure(@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method StageClosureRtrv in StageClosureRtrvController");

		if (TypeConvUtil.isNullOrEmpty(stageClosureRtrvReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("Stage.Id.mandatory", null, Locale.US));
		}

		StageClosureRtrvRes stageClosureRtrvRes = stageClosureRtrvService
				.getStageClosurePageDetails(stageClosureRtrvReq.getIdStage(), stageClosureRtrvReq.getCdTask());

		log.debug("Exiting method StageClosureRtrv in StageClosureRtrvController");
		return stageClosureRtrvRes;
	}

	/**
	 * This service performs edit checks in a variety of situations related to
	 * stage closure. It searches the appropriate table given a stage to close
	 * and the closure reason. Not all stages and closure reason have edit
	 * checks that must be performed. This service will also call CloseOpenStage
	 * to close the stage and will call CloseCaseStage if the stage being closed
	 * is the last in the case CSUB68S
	 * 
	 * @param stageClosureRtrvReq
	 * @return StageClosureRtrvRes
	 */
	@RequestMapping(value = "/saveStageClosure", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRtrvRes saveStageClosure(@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method saveStageClosure in StageClosureAudController");
		StageClosureRtrvRes stageClosureRtrvRes = stageClosureRtrvService.saveStageClosure(stageClosureRtrvReq);
		if (CollectionUtils.isEmpty(stageClosureRtrvRes.getErrorDtos())) {
			StageClosureRtrvDto stageClosureRtrvDto = stageClosureRtrvService.getStageClosureDetails(
					stageClosureRtrvRes.getStageclosureRtrvDto().getStageDto().getIdStage(),
					stageClosureRtrvRes.getStageclosureRtrvDto().getEventDto().getIdEvent());
			stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);
		}
		log.debug("Exiting method saveStageClosure in StageClosureAudController");
		return stageClosureRtrvRes;
	}

	/**
	 * This service performs edit checks in a variety of situations related to
	 * stage closure.before sending to Approval
	 * 
	 * @param stageClosureRtrvReq
	 * @return StageClosureRtrvRes
	 */
	@RequestMapping(value = "/validateApprovalStageClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRtrvRes validateApprovalStageClosure(
			@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method validateApprovalStageClosure in StageClosureAudController");
		StageClosureRtrvRes stageClosureRtrvRes = stageClosureRtrvService.saveStageClosure(stageClosureRtrvReq);
		if (CollectionUtils.isEmpty(stageClosureRtrvRes.getErrorDtos())) {
			StageClosureRtrvDto stageClosureRtrvDto = stageClosureRtrvService.getStageClosureDetails(
					stageClosureRtrvRes.getStageclosureRtrvDto().getStageDto().getIdStage(),
					stageClosureRtrvRes.getStageclosureRtrvDto().getEventDto().getIdEvent());
			stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);
		}
		log.debug("Exiting method validateApprovalStageClosure in StageClosureAudController");
		return stageClosureRtrvRes;
	}

	/**
	 * This service performs edit checks in a variety of situations related to
	 * stage closure. It searches the appropriate table given a stage to close
	 * and the closure reason. Not all stages and closure reason have edit
	 * checks that must be performed. This service will also call CloseOpenStage
	 * to close the stage and will call CloseCaseStage if the stage being closed
	 * is the last in the case CSUB68S calls CSUB68S ,and performs additional
	 * Checks apart From checks done in CSUB68S
	 * 
	 * @param stageClosureRtrvReq
	 * @return StageClosureRtrvRes
	 */
	@RequestMapping(value = "/saveSubmitStageClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRtrvRes saveSubmitStageClosure(
			@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method CSUB68S in StageClosureAudController");
		StageClosureRtrvRes stageClosureRtrvRes = stageClosureRtrvService.saveSubmit(stageClosureRtrvReq);
		if (CollectionUtils.isEmpty(stageClosureRtrvRes.getErrorDtos())
				&& StringUtils.isBlank(stageClosureRtrvRes.getFdtcErrorMessage())) {
			StageClosureRtrvDto stageClosureRtrvDto = stageClosureRtrvService.getStageClosureDetails(
					stageClosureRtrvRes.getStageclosureRtrvDto().getStageDto().getIdStage(),
					stageClosureRtrvRes.getStageclosureRtrvDto().getEventDto().getIdEvent());
			stageClosureRtrvRes.setStageclosureRtrvDto(stageClosureRtrvDto);
		}
		log.debug("Exiting method CSUB68S in StageClosureAudController");
		return stageClosureRtrvRes;
	}

	/**
	 * Method Name: isActiveReferral Method Description: Returns true/false if
	 * there is an active child referral for stage id
	 * 
	 * @param idStage
	 * @return
	 */
	@RequestMapping(value = "/isActiveReferral", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRtrvRes isActiveReferral(@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method isActiveReferral method");
		StageClosureRtrvRes stageClosureRtrvRes = new StageClosureRtrvRes();
		boolean isActiveApproval = stageClosureRtrvService.isActiveReferral(stageClosureRtrvReq.getIdStage());
		stageClosureRtrvRes.setActiveApproval(isActiveApproval);
		return stageClosureRtrvRes;

	}

	/**
	 * Method Name: getPrtActiveActionPlan Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in open status.
	 * 
	 * @param idPerson
	 * @return
	 */
	@RequestMapping(value = "/getPrtActiveActionPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  StageClosureRtrvRes getPrtActiveActionPlan(
			@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method getPrtActiveActionPlan method");
		StageClosureRtrvRes stageClosureRtrvRes = new StageClosureRtrvRes();
		boolean prtActiveActionPlan = stageClosureRtrvService.getPrtActiveActionPlan(stageClosureRtrvReq.getIdPerson());
		stageClosureRtrvRes.setPrtActiveActionStatus(prtActiveActionPlan);
		return stageClosureRtrvRes;

	}

	/**
	 * Method Name: getPrtActionPlanInProcStatus Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in Proc status.
	 * 
	 * @param idPerson
	 * @return
	 */
	public  StageClosureRtrvRes getPrtActionPlanInProcStatus(
			@RequestBody StageClosureRtrvReq stageClosureRtrvReq) {
		log.debug("Entering method getPrtActionPlanInProcStatus method");
		StageClosureRtrvRes stageClosureRtrvRes = new StageClosureRtrvRes();
		boolean isPrtActiveActionPlaninProcStatus = stageClosureRtrvService
				.getPrtActionPlanInProcStatus(stageClosureRtrvReq.getIdPerson());
		stageClosureRtrvRes.setPrtActiveActionInProcStatus(isPrtActiveActionPlaninProcStatus);
		return stageClosureRtrvRes;

	}

}
