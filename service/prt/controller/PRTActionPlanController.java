/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Mar 28, 2018- 3:20:02 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.prt.controller;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTParticipantReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.PRTActionPlanRes;
import us.tx.state.dfps.service.common.response.PRTParticipantRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.prt.dto.PRTParticipantDto;
import us.tx.state.dfps.service.prt.dto.PRTPermStatusLookupDto;
import us.tx.state.dfps.service.prt.dto.PRTPersonLinkDto;
import us.tx.state.dfps.service.prt.service.PRTActionPlanService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 28, 2018- 3:20:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/prtactionplan")
public class PRTActionPlanController {
	private static final String EXITING_METHOD_FETCH_ACTION_PLAN = "Exiting method fetchActionPlan in PRTActionPlanController";
	private static final String EXITING_METHOD_DELETE_PRT_STRATEGY_FOR_ACTION_PLAN = "Exiting method deletePRTStrategyForActionPlan in PRTActionPlanController";
	private static final String EXITING_METHOD_FETCH_OPEN_ACTION_PLAN = "Exiting method fetchOpenActionPlan in PRTActionPlanController";
	private static final String EXITING_METHOD_FETCH_LATEST_LEGAL_STATUS = "Exiting method fetchLatestLegalStatus in PRTActionPlanController";
	private static final String COMMON_CHILDREN_MANDATORY = "common.children.mandatory";
	private static final String COMMON_STAGE_ID_LIST_MANDATORY = "common.stageIdList.mandatory";
	private static final String EXITING_METHOD_FETCH_AND_POPULATE_LATEST_CHILD_PLANS = "Exiting method fetchAndPopulateLatestChildPlans in PRTActionPlanController";
	private static final String EXITING_METHOD_DELETE_PRT_PARTICIPANT = "Exiting method deletePRTParticipant in PRTActionPlanController";
	private static final String COMMON_ID_PRT_PARTICIPANT_MANDATORY = "common.idPrtParticipant.mandatory";
	private static final String PRT_PARTICIPANT_VALUE_DTO_MANDATORY = "PrtParticipantValueDto.mandatory";
	private static final String EXITING_METHOD_INSERT_PRT_PARTICIPANT = "Exiting method insertPRTParticipant in PRTActionPlanController";
	private static final String ENTERING_METHOD_INSERT_PRT_PARTICIPANT = "Entering method insertPRTParticipant in PRTActionPlanController";
	private static final String EXITING_METHOD_UPDATE_PRT_CONTACT_TO_PERS_LINK = "Exiting method updatePRTContactToPersLink in PRTActionPlanController";
	private static final String COMMON_ID_PERSON_MANDATORY = "common.idPerson.mandatory";
	private static final String COMMON_ID_PRT_ACTION_PLAN_MANDATORY = "common.idPrtActionPlan.mandatory";
	private static final String COMMON_ID_CONTACT_EVENT_MANDATORY = "common.idContactEvent.mandatory";
	private static final String EXITING_METHOD_SAVE_ACTION_PLAN = "Exiting method saveActionPlan in PRTActionPlanController";
	private static final String EXITING_METHOD_DELETE_PRT_ACTION_PLAN = "Exiting method deletePRTActionPlan in PRTActionPlanController";
	private static final String COMMON_ID_EVENT_MANDATORY = "common.idEvent.mandatory";
	private static final String EXITING_METHOD_FETCH_PRT_PERM_STATUS_LOOKUP = "Exiting method fetchPrtPermStatusLookup in PRTActionPlanController";
	private static final String ENTERING_METHOD_FETCH_PRT_PERM_STATUS_LOOKUP = "Entering method fetchPrtPermStatusLookup in PRTActionPlanController";
	private static final String EXITING_METHOD_VALIDATE_ADD_ACTION_PLAN = "Exiting method validateAddActionPlan in PRTActionPlanController";
	private static final String COMMON_ID_STAGE_MANDATORY = "common.idStage.mandatory";
	private static final String COMMON_ID_CASE_MANDATORY = "common.idCase.mandatory";
	private static final String TRANSACTION_ID = "TransactionId :";

	@Autowired
	private PRTActionPlanService pRTActionPlanService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PRTActionPlanControllerLog");

	/**
	 * 
	 * Method Name: fetchActionPlan Method Description:This method retrieves all
	 * PRT Action Plan Details.
	 * 
	 * @param prtActionPlanReq
	 * @return prtActionPlanRes
	 */
	@RequestMapping(value = "/fetchActionPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes fetchActionPlan(@RequestBody PRTActionPlanReq prtActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_CASE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		PRTActionPlanRes prtActionPlanRes = new PRTActionPlanRes();
		prtActionPlanRes.setpRTActionPlanDto(pRTActionPlanService.fetchActionPlan(
				prtActionPlanReq.getIdActionPlanEvent(), prtActionPlanReq.getIdStage(), prtActionPlanReq.getIdCase()));
		LOG.debug(EXITING_METHOD_FETCH_ACTION_PLAN);
		LOG.info(TRANSACTION_ID + prtActionPlanReq.getTransactionId());
		return prtActionPlanRes;

	}

	/**
	 * 
	 * Method Name: displayActionPlanForm Method Description:This method
	 * retrieves all PRT Action Plan Details.
	 * 
	 * @param prtActionPlanReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/displayActionPlanForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes displayActionPlanForm(@RequestBody PRTActionPlanReq prtActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_CASE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(pRTActionPlanService.displayActionPlanForm(
				prtActionPlanReq.getIdActionPlanEvent(), prtActionPlanReq.getIdStage(), prtActionPlanReq.getIdCase())));
		LOG.debug(TRANSACTION_ID + prtActionPlanReq.getTransactionId());
		return commonFormRes;

	}

	/**
	 * Method Name: validateAddActionPlan Method Description:This method
	 * validates before creating new Action Plan.
	 *
	 * @param prtActionPlanReq
	 *            the prt action plan req
	 * @return prtActionPlanRes
	 */
	@RequestMapping(value = "/validateAddActionPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes validateAddActionPlan(@RequestBody PRTActionPlanReq prtActionPlanReq) {
		List<Long> errorList = null;
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_CASE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		PRTActionPlanRes prtActionPlanRes = new PRTActionPlanRes();
		errorList = pRTActionPlanService.validateAddActionPlan(prtActionPlanReq.getIdStage(),
				prtActionPlanReq.getIdCase());
		LOG.debug(EXITING_METHOD_VALIDATE_ADD_ACTION_PLAN);
		prtActionPlanRes.setValidateList(errorList);
		return prtActionPlanRes;
	}

	/**
	 * Method Name: fetchPrtPermStatusLookup Method Description: This method
	 * accept the request to fetch PRT Status Definition details.
	 * 
	 * @return pRTActionPlanRes
	 */
	@RequestMapping(value = "/fetchPrtPermStatusLookup", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes fetchPrtPermStatusLookup() {
		PRTActionPlanRes pRTActionPlanRes = new PRTActionPlanRes();
		LOG.debug(ENTERING_METHOD_FETCH_PRT_PERM_STATUS_LOOKUP);
		List<PRTPermStatusLookupDto> permStatusList = pRTActionPlanService.fetchPrtPermStatusLookup();
		pRTActionPlanRes.setPermStatusList(permStatusList);
		LOG.debug(EXITING_METHOD_FETCH_PRT_PERM_STATUS_LOOKUP);
		return pRTActionPlanRes;
	}

	/**
	 * 
	 * Method Name: deletePRTActionPlan Method Description: This method is used
	 * to delete the PRT Action Plan
	 * 
	 * @param prtActionPlanReq
	 * @return prtActionPlanRes
	 */
	@RequestMapping(value = "/deletePRTActionPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes deletePRTActionPlan(@RequestBody PRTActionPlanReq prtActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_CASE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_STAGE_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(prtActionPlanReq.getIdActionPlanEvent())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_EVENT_MANDATORY, null, Locale.US));
		}
		PRTActionPlanRes prtActionPlanRes = new PRTActionPlanRes();
		Long count = pRTActionPlanService.deleteActionPlan(prtActionPlanReq.getIdActionPlanEvent(),
				prtActionPlanReq.getIdStage(), prtActionPlanReq.getIdCase());
		prtActionPlanRes.setCount(count);
		LOG.debug(EXITING_METHOD_DELETE_PRT_ACTION_PLAN);
		return prtActionPlanRes;
	}

	/**
	 * 
	 * Method Name: saveActionPlan Method Description: This method is used to
	 * save the PRT action Plan
	 * 
	 * @param prtActionPlanReq
	 * @return
	 */
	@RequestMapping(value = "/saveActionPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes saveActionPlan(@RequestBody PRTActionPlanReq prtActionPlanReq) {
		PRTActionPlanRes prtActionPlanRes = new PRTActionPlanRes();
		prtActionPlanRes
				.setIdCurrentEvent((long) pRTActionPlanService.saveActionPlan(prtActionPlanReq.getPrtActionPlanDto()));
		LOG.debug(EXITING_METHOD_SAVE_ACTION_PLAN);
		return prtActionPlanRes;

	}

	/**
	 * 
	 * Method Name: updatePRTContactToPersLink Method Description: This method
	 * is used to update the PRT Contact Persion Link table
	 * 
	 * @param pRTActionPlanReq
	 * @return response
	 */
	@RequestMapping(value = "/updatePRTContactToPersLink", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes updatePRTContactToPersLink(@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getpRTPersonLinkValueDto().getIdContactEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage(COMMON_ID_CONTACT_EVENT_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getpRTPersonLinkValueDto().getIdPrtActionPlan())) {
			throw new InvalidRequestException(
					messageSource.getMessage(COMMON_ID_PRT_ACTION_PLAN_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getpRTPersonLinkValueDto().getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_PERSON_MANDATORY, null, Locale.US));
		}
		// count is the number of updated database entries
		// will be returned wrapped in the response object
		Long count = pRTActionPlanService.updatePRTContactToPersLink(pRTActionPlanReq);
		PRTActionPlanRes response = new PRTActionPlanRes();
		response.setCount(count);
		LOG.debug(EXITING_METHOD_UPDATE_PRT_CONTACT_TO_PERS_LINK);
		return response;

	}

	/**
	 * 
	 * Method Name: insertPRTParticipant Method Description:This method is used
	 * to insert the PRT Participant into DB
	 * 
	 * @param participant
	 * @return participantRes
	 */
	@RequestMapping(value = "/insertPRTParticipant", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTParticipantRes insertPRTParticipant(@RequestBody PRTParticipantReq participant) {
		LOG.debug(ENTERING_METHOD_INSERT_PRT_PARTICIPANT);
		if (TypeConvUtil.isNullOrEmpty(participant.getPrtParticipantDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage(PRT_PARTICIPANT_VALUE_DTO_MANDATORY, null, Locale.US));
		}
		PRTParticipantRes participantRes = new PRTParticipantRes();
		Long count = pRTActionPlanService.insertPRTParticipant(participant);
		List<PRTParticipantDto> paricipantList = pRTActionPlanService
				.selectPRTParticipants(participant.getPrtParticipantDto().getIdPrtActionPlan());
		participantRes.setCount(count);
		participantRes.setPrtParticipantDtoList(paricipantList);
		LOG.debug(EXITING_METHOD_INSERT_PRT_PARTICIPANT);
		return participantRes;
	}

	/**
	 * 
	 * Method Name: deletePRTParticipant Method Description: This method is used
	 * to delete the PRT Participant
	 * 
	 * @param pRTActionPlanReq
	 * @return res
	 */
	@RequestMapping(value = "/deletePRTParticipant", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes deletePRTParticipant(@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getIdPrtParticipant())) {
			throw new InvalidRequestException(
					messageSource.getMessage(COMMON_ID_PRT_PARTICIPANT_MANDATORY, null, Locale.US));
		}
		PRTActionPlanRes res = new PRTActionPlanRes();
		res.setCount(pRTActionPlanService.deletePRTParticipant(pRTActionPlanReq.getIdPrtParticipant()));
		LOG.debug(EXITING_METHOD_DELETE_PRT_PARTICIPANT);
		return res;
	}

	/**
	 * 
	 * Method Name: fetchAndPopulateLatestChildPlans Method Description: Fetch
	 * and populate latest child plans.
	 * 
	 * @param pRTActionPlanReq
	 * @return response
	 */
	@RequestMapping(value = "/fetchAndPopulateLatestChildPlans", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes fetchAndPopulateLatestChildPlans(
			@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getStageIdList())) {
			throw new InvalidRequestException(
					messageSource.getMessage(COMMON_STAGE_ID_LIST_MANDATORY, null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getChildren())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_CHILDREN_MANDATORY, null, Locale.US));
		}
		List<PRTPersonLinkDto> children = pRTActionPlanService
				.fetchAndPopulateLatestChildPlans(pRTActionPlanReq.getStageIdList(), pRTActionPlanReq.getChildren());
		PRTActionPlanRes response = new PRTActionPlanRes();
		response.setChildren(children);
		LOG.debug(EXITING_METHOD_FETCH_AND_POPULATE_LATEST_CHILD_PLANS);
		return response;
	}

	/**
	 * 
	 * Method Name: fetchLatestLegalStatus Method Description: Fetch latest
	 * legal status.
	 * 
	 * @param pRTActionPlanReq
	 * @return pRTActionPlanRes
	 */
	@RequestMapping(value = "/fetchLatestLegalStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes fetchLatestLegalStatus(@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_PERSON_MANDATORY, null, Locale.US));
		}
		LegalStatusDto legalStatusDto = pRTActionPlanService.fetchLatestLegalStatus(pRTActionPlanReq.getIdPerson());
		PRTActionPlanRes pRTActionPlanRes = new PRTActionPlanRes();
		pRTActionPlanRes.setLegalStatusDto(legalStatusDto);
		LOG.debug(EXITING_METHOD_FETCH_LATEST_LEGAL_STATUS);
		return pRTActionPlanRes;

	}

	/**
	 * 
	 * Method Name: fetchOpenActionPlan Method Description: This method is used
	 * to fetch open action plan.
	 * 
	 * @param pRTActionPlanReq
	 * @return pRTActionPlanRes
	 */
	@RequestMapping(value = "/fetchOpenActionPlan", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes fetchOpenActionPlan(@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage(COMMON_ID_PERSON_MANDATORY, null, Locale.US));
		}
		PRTActionPlanRes pRTActionPlanRes = pRTActionPlanService.fetchOpenActionPlan(pRTActionPlanReq.getIdPerson());
		LOG.debug(EXITING_METHOD_FETCH_OPEN_ACTION_PLAN);
		return pRTActionPlanRes;
	}

	/**
	 * 
	 * Method Name: deletePRTStrategyForActionPlan Method Description: This
	 * method is used to delete the Strategy for PRT Action Plan
	 * 
	 * @param pRTActionPlanReq
	 * @return prtActionPlanRes
	 */
	@RequestMapping(value = "/deletePRTStrategy", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PRTActionPlanRes deletePRTStrategyForActionPlan(
			@RequestBody PRTActionPlanReq pRTActionPlanReq) {
		PRTActionPlanRes prtActionPlanRes = new PRTActionPlanRes();
		pRTActionPlanService.deletePRTStrategy(pRTActionPlanReq);
		LOG.debug(EXITING_METHOD_DELETE_PRT_STRATEGY_FOR_ACTION_PLAN);
		return prtActionPlanRes;
	}

	/**
	 * 
	 * Method Name: getTimeStamp Method Description:
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	@RequestMapping(value = "/getTimeStamp", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes getTimeStamp(@RequestBody CommonHelperReq commonHelperReq) {
		CommonHelperRes commonHelperRes = new CommonHelperRes();
		commonHelperRes.setDtLastUpdate(pRTActionPlanService.getTimeStamp(commonHelperReq));
		return commonHelperRes;
	}

}
