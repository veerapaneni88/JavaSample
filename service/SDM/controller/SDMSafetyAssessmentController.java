package us.tx.state.dfps.service.SDM.controller;

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
import us.tx.state.dfps.service.SDM.service.SDMSafetyAssessmentService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SafetyAssessmentReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.SafetyAssessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;

/**
 * 
 * service-business - IMPACT PHASE 2 EJB MODERNIZATION EJB Bean
 * SDMSafetyAssessmentBean EJB implementation for functions required for
 * implementing SDM Safety Assessment functionality
 * 
 */

@RestController
@RequestMapping("/sdmsafeassess")
public class SDMSafetyAssessmentController {

	@Autowired
	SDMSafetyAssessmentService safetyAssessService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(SDMSafetyAssessmentController.class);

	/**
	 * This method is used to update SDM Safety assessment
	 * 
	 * @param safetyAssessReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/saveAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes saveAssessment(@RequestBody SafetyAssessmentReq safetyAssessReq) {
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();

		SDMSafetyAssessmentDto safetyAssessmentDto = new SDMSafetyAssessmentDto();

		safetyAssessmentDto = safetyAssessService.saveAssessment(safetyAssessReq.getSafetyAssessmentDB(),
				safetyAssessReq.getUserProfileDB());

		log.info("TransactionId :" + safetyAssessReq.getTransactionId());

		safetyAssessRes.setSafetyAssessmentDB(safetyAssessmentDto);
		return safetyAssessRes;
	}

	/**
	 * This method is used to delete SDM Safety assessment details
	 * 
	 * @param safetyAssessReq
	 * @return HandWritingRes
	 */
	@RequestMapping(value = "/deleteSafetyAssmtDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes deleteSafetyAssmtDetails(
			@RequestBody SafetyAssessmentReq safetyAssessReq) {
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();

		safetyAssessRes
				.setUpdateResult(safetyAssessService.deleteSafetyAssmtDetails(safetyAssessReq.getSafetyAssessmentDB()));
		log.info("TransactionId :" + safetyAssessReq.getTransactionId());
		return safetyAssessRes;
	}

	@RequestMapping(value = "/getSDMSafetyAssessment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getSDMSafetyAssessment(@RequestBody SafetyAssessmentReq safetyAssessReq) {

		if (TypeConvUtil.isNullOrEmpty(safetyAssessReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessRes = safetyAssessService.getSDMSafetyAssessment(safetyAssessReq.getIdEvent(),
				safetyAssessReq.getIdStage());
		log.info("TransactionId :" + safetyAssessReq.getTransactionId());
		return safetyAssessRes;
	}

	@RequestMapping(value = "/isSftAsmntInProcStatusAvail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes isSftAsmntInProcStatusAvail(
			@RequestBody SafetyAssessmentReq safetyAssessReq) {

		if (TypeConvUtil.isNullOrEmpty(safetyAssessReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(safetyAssessReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageType.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessRes = safetyAssessService
				.isSftAsmntInProcStatusAvail(safetyAssessReq.getIdStage(), safetyAssessReq.getCdStage());
		log.info("TransactionId :" + safetyAssessReq.getTransactionId());
		return safetyAssessRes;
	}

	@RequestMapping(value = "/getQueryPageData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes getQueryPageData(@RequestBody SafetyAssessmentReq safetyAssessReq) {

		if (TypeConvUtil.isNullOrEmpty(safetyAssessReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessRes = safetyAssessService.getQueryPageData(safetyAssessReq.getIdStage());
		log.info("TransactionId :" + safetyAssessReq.getTransactionId());
		return safetyAssessRes;
	}

	@RequestMapping(value = "/getAsmtTypHoHold", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonStringRes getAsmtTypHoHold(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonStringRes commonStringRes = safetyAssessService.getAsmtTypHoHold(commonHelperReq);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return commonStringRes;
	}

	@RequestMapping(value = "/completeAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes completeAssessment(@RequestBody SafetyAssessmentReq safetyAssessReq) {
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();

		SDMSafetyAssessmentDto safetyAssessmentDto = new SDMSafetyAssessmentDto();

		safetyAssessmentDto = safetyAssessService.completeAssessment(safetyAssessReq.getSafetyAssessmentDB(),
				safetyAssessReq.getUserProfileDB());

		log.info("TransactionId :" + safetyAssessReq.getTransactionId());

		safetyAssessRes.setSafetyAssessmentDB(safetyAssessmentDto);
		return safetyAssessRes;
	}

	@RequestMapping(value = "/undoCompleteAssessment", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SafetyAssessmentRes undoCompleteAssessment(@RequestBody SafetyAssessmentReq safetyAssessReq) {
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();

		SDMSafetyAssessmentDto safetyAssessmentDto = new SDMSafetyAssessmentDto();

		safetyAssessmentDto = safetyAssessService.undoCompleteAssessment(safetyAssessReq.getSafetyAssessmentDB());

		log.info("TransactionId :" + safetyAssessReq.getTransactionId());

		safetyAssessRes.setSafetyAssessmentDB(safetyAssessmentDto);
		return safetyAssessRes;
	}

	/**
	 * 
	 * Method Name: displaySDMSafetyAssessmentForm Method Description:
	 * 
	 * @param safetyAssessReq
	 * @return
	 */
	@RequestMapping(value = "/displaySDMSafetyAssessmentForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes displaySDMSafetyAssessmentForm(
			@RequestBody SafetyAssessmentReq safetyAssessReq) {

		if (TypeConvUtil.isNullOrEmpty(safetyAssessReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(safetyAssessService.displaySDMSafetyAssessmentForm(safetyAssessReq)));
		log.info("TransactionId :" + safetyAssessReq.getTransactionId());
		return commonFormRes;
	}
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getLatestSafetyAssessmentEvent", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody SafetyAssessmentRes getLatestSafetyAssessmentEvent(
			@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sdmsafetyassessment.stageid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();
		safetyAssessRes.setTransactionId(safetyAssessmentReq.getTransactionId());
		safetyAssessRes
				.setIdEvent(safetyAssessService.getLatestSafetyAssessmentEvent(safetyAssessmentReq.getIdStage()));
		log.info("TransactionId :" + safetyAssessmentReq.getTransactionId());
		return safetyAssessRes;

	}
	
	/**
	 * 
	 * Method Name: getLatestSafetyAssessmentEvent Method Description:Gets
	 * latest safety assessment event id
	 * 
	 * @param safetyAssessmentReq
	 * @return SafetyAssessmentRes
	 */
	@RequestMapping(value = "/getLatestSafetyAssessmentDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public @ResponseBody SafetyAssessmentRes getLatestSafetyAssessmentDate(
			@RequestBody SafetyAssessmentReq safetyAssessmentReq) {
		if (TypeConvUtil.isNullOrEmpty(safetyAssessmentReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("sdmsafetyassessment.stageid.mandatory", null, Locale.US));
		}
		SafetyAssessmentRes safetyAssessRes = new SafetyAssessmentRes();
		safetyAssessRes.setTransactionId(safetyAssessmentReq.getTransactionId());
		safetyAssessRes.setDtLatestAssessment(safetyAssessService.getLatestSafetyAssessmentEvent(safetyAssessmentReq.getIdStage(),safetyAssessmentReq.getIdEvent()));
		safetyAssessRes.setCdRecordExist(safetyAssessService.getAsmtTypHouseHoldForAssessedPerson(safetyAssessmentReq.getIdStage(),safetyAssessmentReq.getIdCpsSa(),safetyAssessmentReq.getIdPersonList()));
		log.info("TransactionId :" + safetyAssessmentReq.getTransactionId());
		return safetyAssessRes;

	}

}
