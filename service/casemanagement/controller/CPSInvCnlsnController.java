package us.tx.state.dfps.service.casemanagement.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.service.CPSInvCnlsnService;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Rest
 * Controller for Cps Investigation Validation and Closure notice Related
 * Services. Aug 3, 2018- 2:53:34 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/cpsInvCnlsn")
public class CPSInvCnlsnController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CPSInvCnlsnService cnlsnService;

	private static final Logger log = Logger.getLogger(CPSInvCnlsnController.class);

	//PPM 69915
	/**
	 *
	 *
	 * Method Description:This Method is used to retireve the information of all
	 * the substances that a prent/caregiver tested positive for a given Person ID.
	 *
	 * @param idStage stage id
	 * @return CpsInvSubstancesRes
	 */

	@GetMapping(value = "/getSubstances/{idStage}")
	public CpsInvSubstancesRes getSubtancesbyStageId(@PathVariable(value = "idStage") Long idStage) {
		if (TypeConvUtil.isNullOrEmpty(idStage)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CpsInvSubstancesRes cpsInvSubstancesRes = cnlsnService.getSubstancesByStageId(idStage);
		return cpsInvSubstancesRes;
	}

	/**
	 *
	 *
	 * Method Description:This Method is used to retireve the information of all
	 * the substances that a prent/caregiver tested positive for a given Person ID.
	 *
	 * @param cpsInvSubstanceReq
	 * @return CpsInvSubstancesRes
	 */

	@RequestMapping(value = "/saveSubstances", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvSubstancesRes saveSubtances(
			@RequestBody CpsInvSubstanceReq cpsInvSubstanceReq) {
		CpsInvSubstancesRes cpsInvSubstancesRes = new CpsInvSubstancesRes();
		cpsInvSubstancesRes = cnlsnService.saveSubstances(cpsInvSubstanceReq);
		log.info("TransactionId :" + cpsInvSubstanceReq.getTransactionId());
		return cpsInvSubstancesRes;

	}

	/**
	 * 
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId.
	 * 
	 * @param CommonHelperReq(eventId)
	 * @return cpsInvNoticesRes
	 */
	@RequestMapping(value = "/getClosureNotices", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvNoticesRes getClosureNotices(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		cpsInvNoticesRes = cnlsnService.getClosureNotices(commonHelperReq);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Description: This method is used to save/update/delete the
	 * information of closure notices.
	 * 
	 * @param cpsInvNoticesClosureReq
	 * @return CpsInvNoticesRes(successMessage String)
	 */
	@RequestMapping(value = "/saveClosureNotices", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvNoticesRes saveClosureNotices(
			@RequestBody CpsInvNoticesClosureReq cpsInvNoticesClosureReq) {
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		cpsInvNoticesRes = cnlsnService.saveClosureNotices(cpsInvNoticesClosureReq);
		log.info("TransactionId :" + cpsInvNoticesClosureReq.getTransactionId());
		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Description:This Method is used to retireve the information of all
	 * the closure notices for a given EventId for AR.
	 * 
	 * @param CommonHelperReq(eventId)
	 * @return cpsInvNoticesRes
	 * 
	 * @
	 */
	@RequestMapping(value = "/getARClosureNotices", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvNoticesRes getARClosureNotices(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		CpsInvNoticesRes cpsInvNoticesRes = new CpsInvNoticesRes();
		cpsInvNoticesRes = cnlsnService.getARClosureNotices(commonHelperReq);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return cpsInvNoticesRes;
	}

	/**
	 * 
	 * Method Name: getCompletedAssementsExists Method Description:This Method
	 * is used to check completed safety and risk assessment exists for default
	 * Household.
	 * 
	 * @param commonHelperReq
	 * @return
	 * 
	 * @
	 */
	@RequestMapping(value = "/completedAssessmentExists", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes getCompletedAssessmentsExists(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		CommonBooleanRes commonCountRes = cnlsnService.getCompletedAssessmentsExists(commonHelperReq);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return commonCountRes;
	}

	/**
	 * Method Name: fetchAllegQuestionAnswers Method Description: This method
	 * gets ID, DOB, and DOD for victim on an allegation.
	 * 
	 * @param cpsInvCnlsnReq
	 * @return CPSInvCnlsnRes
	 */
	@RequestMapping(value = "/fetchAllegQuestionAnswers", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvCnclsnRes fetchAllegQuestionAnswers(@RequestBody CpsInvCnclsnReq cpsInvCnlsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnlsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CpsInvCnclsnRes cpsInvCnlsnRes = new CpsInvCnclsnRes();
		cpsInvCnlsnRes.setHasAllegQuestionAnswers(cnlsnService.fetchAllegQuestionAnswers(cpsInvCnlsnReq.getIdStage()));
		return cpsInvCnlsnRes;
	}

	/**
	 * Method Name: savePrintPerson Method Description: Saves the Print Person
	 * Selected for the Conclusion event
	 * 
	 * @param cnclsnPrintStaffReq
	 * @return
	 */
	@RequestMapping(value = "/savePrintPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonStringRes savePrintPerson(@RequestBody CnclsnPrintStaffReq cnclsnPrintStaffReq) {
		if (TypeConvUtil.isNullOrEmpty(cnclsnPrintStaffReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(cnclsnPrintStaffReq.getIdPrintPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("cnclsnPrintStaffReq.idPrintStaff.mandatory", null, Locale.US));
		}
		CommonStringRes commonRes = cnlsnService.savePrintPerson(cnclsnPrintStaffReq);
		log.info("TransactionId :" + cnclsnPrintStaffReq.getTransactionId());
		return commonRes;
	}

	@RequestMapping(value = "/getPrintPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  ApprovalPersonRes getPrintPerson(@RequestBody CommonHelperReq commonHelperReq) {
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		ApprovalPersonRes approvalPersonRes = new ApprovalPersonRes();
		ApprovalPersonPrintDto printPerson = cnlsnService.getPrintPerson(commonHelperReq);
		approvalPersonRes.setApprovalPersonDto(printPerson);
		log.info("TransactionId :" + commonHelperReq.getTransactionId());
		return approvalPersonRes;
	}

	/**
	 * Method Name: isDispositionMissing
	 * 
	 * Method Description:This method will be called to retrieve true if any of
	 * allegation(s) for the input stage is missing a disposition.
	 * 
	 * @param cpsInvCnclsnReq
	 *            - This request object will hold the input parameters for
	 *            retriving if disposition is missing .
	 * @return CommonBooleanRes - This response object will hold the status of
	 *         disposition.
	 */
	@RequestMapping(value = "/dispositionStatus", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes isDispositionMissing(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return cnlsnService.isDispositionMissing(cpsInvCnclsnReq);
	}

	/**
	 * Method Name: fetchAllegQuestionYAnswers
	 * 
	 * Method Description:This method will be called to check if the allegation
	 * questions are answered for a cps investigation.
	 * 
	 * @param cpsInvCnclsnReq
	 *            - This request object will hold the input parameters for
	 *            retriving if allegation questions are answered.
	 * @return CommonBooleanRes - This response object will hold the status of
	 *         allegation questions answered.
	 */
	@RequestMapping(value = "/allegQuestionYAnswers", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes fetchAllegQuestionYAnswers(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		return cnlsnService.fetchAllegQuestionYAnswers(cpsInvCnclsnReq);
	}

	/**
	 * artf228543
	 * Method Name: hasAllegedPerpetratorWithAgeLessThanTen
	 * Method Description: This method checks if the given stage has any alleged perpetrator
	 * with age less than ten and disposition is not Admin Closure.
	 *
	 * @param cpsInvCnclsnReq
	 * @return CommonBooleanRes
	 */
	@RequestMapping(value = "/hasAllegedPerpetrator", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonBooleanRes hasAllegedPerpetratorWithAgeLessThanTen(@RequestBody CpsInvCnclsnReq cpsInvCnclsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnclsnReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		return cnlsnService.hasAllegedPerpetratorWithAgeLessThanTen(cpsInvCnclsnReq);
	}

	/**
	 * Method Name: validateInvStageClosure Method Description: This method
	 * implements business logic for stage closure validations related to all
	 * the events in a particular stage
	 * 
	 * @param invStageClosureReq
	 * @return CPSInvCnlsnRes
	 */
	@RequestMapping(value = "/validateInvStageClosure", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CpsInvCnclsnRes cpsInvConclValiation(@RequestBody CpsInvCnclsnReq invStageClosureReq) {
		log.info("cpsInvConclValiation method of CpsInvConclValController: Execution Started");
		// Validate the request
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("validateInvStageClosure.invalidrequest", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto().getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto().getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto().getDcdEditProcess())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.editProcess.mandatory", null, Locale.US));
		}
		CpsInvCnclsnRes response = cnlsnService.validateInvStageForClosure(invStageClosureReq);
		log.info("cpsInvConclValiation method of CPS Executed : Return Response");
		return response;
	}

	/**
	 * Method Name: validateRlqshCstdyAndPrsnCharQuestion Method
	 * Description:This method returns list of Id persons.
	 * 
	 * @param cpsInvCnlsnReq
	 * @return CommonBooleanRes
	 * 
	 */
	@RequestMapping(value = "/validateRlqshCstdyAndPrsnCharQuestion", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonBooleanRes validateRlqshCstdyAndPrsnCharQuestion(
			@RequestBody CpsInvCnclsnReq cpsInvCnlsnReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvCnlsnReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonBooleanRes response = new CommonBooleanRes();
		response.setExists(cnlsnService.validateRlqshCstdyAndPrsnCharQuestion(cpsInvCnlsnReq));
		return response;
	}

}
