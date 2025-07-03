package us.tx.state.dfps.service.casepackage.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casepackage.dto.PcspPlcmntDto;
import us.tx.state.dfps.service.casepackage.service.PcspService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PCSPAssessmentReq;
import us.tx.state.dfps.service.common.request.PcspReq;
import us.tx.state.dfps.service.common.request.PcspValueReq;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.response.PCSPAssessmentRes;
import us.tx.state.dfps.service.common.response.PcspPlcmntRes;
import us.tx.state.dfps.service.common.response.PcspRes;
import us.tx.state.dfps.service.common.response.PcspValueRes;
import us.tx.state.dfps.service.common.response.PriorStageInRevRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 *
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:PCSP
 * List, PCSP Details Class Description:Pcspcontroller class will have all
 * operation which are related to case and relevant page to case.
 *
 */
@RestController
@RequestMapping("/pcsp")
public class PcspController {

	@Autowired
	PcspService pcspService;

	@Autowired
	MessageSource messageSource;

	private static final Logger logger = Logger.getLogger(PcspController.class);

	/**
	 *
	 * Method Description: This method is used to retrieve placement details of
	 * a caseid from PCSP_PLCMNT table.
	 *
	 * @return PcspRes InvalidRequestException
	 *
	 */

	@RequestMapping(value = "/getPcspPlacementsList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcspRes getPcspPlacemntDtl(@RequestBody PcspReq pcspRequest) {

		PcspRes placementList = null;
		if (TypeConvUtil.isNullOrEmpty(pcspRequest.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.caseId.mandatory", null, Locale.US));
		}

		placementList = pcspService.getPcspPlacementsService(pcspRequest.getCaseId());
		logger.info("TransactionId :" + pcspRequest.getTransactionId());
		return placementList;

	}

	/**
	 *
	 * Method Description: This method is used to retrieve assessment details of
	 * a caseid from PCSP_ASMNT table.
	 *
	 * @param caseId
	 * @param PcspRes
	 *            InvalidRequestException
	 */

	@RequestMapping(value = "/getPcspAssessmentsList", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcspRes getPcspAssessmntsDtl(@RequestBody PcspReq pcspRequest) {

		PcspRes assessmentsList = null;
		if (TypeConvUtil.isNullOrEmpty(pcspRequest.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.caseId.mandatory", null, Locale.US));
		}

		assessmentsList = pcspService.getPcspAssessmentsService(pcspRequest.getCaseId());
		logger.info("TransactionId :" + pcspRequest.getTransactionId());
		return assessmentsList;

	}

	/**
	 *
	 * Method Description: This method is used to retrieve placements details
	 * from PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 * @param caseId
	 * @param PcspRes
	 */

	@RequestMapping(value = "/pcspPlacemetInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcspPlcmntRes getPcspPlacemetInfo(@RequestBody PcspReq pcspRequest) {
		PcspPlcmntDto pcspDto = pcspRequest.getPcspplcmnt();
		if (TypeConvUtil.isNullOrEmpty(pcspDto.getIdPcspPlcmnt())) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.plcmntId.mandatory", null, Locale.US));
		} /*
		 * else if (pcspDto.getIdPcspPlcmnt() == 0) { throw new
		 * InvalidRequestException(messageSource.getMessage(
		 * "pcsp.plcmntId.mandatory", null, Locale.US)); }
		 */
		logger.info("TransactionId :" + pcspRequest.getTransactionId());
		return pcspService.getPcspPlacemetInfo(pcspDto.getIdPcspPlcmnt(),pcspRequest.getCaseId());
	}



	/**
	 *
	 * Method Description: This method is used to update placements details from
	 * PCSP_PLCMNT, PCSP_ASMNT tables.
	 *
	 */

	@RequestMapping(value = "/pcspUpdateDet", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcspPlcmntRes updatePcspPlacemetDet(@RequestBody PcspReq pcspPlcmntDB) {
		PcspPlcmntRes pcspPlcmntRes = new PcspPlcmntRes();
		PcspPlcmntDto pcspDto = pcspPlcmntDB.getPcspplcmnt();
		if (TypeConvUtil.isNullOrEmpty(pcspPlcmntDB)) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.pcspPlcmntDB.mandatory", null, Locale.US));
		} else if (0 == pcspDto.getIdPcspPlcmnt()) {
			throw new InvalidRequestException(messageSource.getMessage("pcsp.plcmntId.mandatory", null, Locale.US));
		}

		pcspPlcmntRes = pcspService.updatePcspPlcmntDetails(pcspPlcmntDB);
		if (TypeConvUtil.isNullOrEmpty(pcspPlcmntRes.getPcspPlcmntDtl())) {
			throw new DataNotFoundException(messageSource.getMessage("pcsp.Data.NotFound", null, Locale.US));
		}
		logger.info("TransactionId :" + pcspPlcmntDB.getTransactionId());
		return pcspPlcmntRes;

	}

	/**
	 * This Method will retrieve the list of PCSP primary caregiver and children
	 * details
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes
	 */
	@RequestMapping(value = "/pcspPersonDtls", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getPcspPersonDetails(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.stageId.mandatory", null, Locale.US));
		}

		return pcspService.getPcspPersonDetails(pcspAssessmentReq);

	}

	/**
	 * This Method is used to check if answers are saved for the assessment
	 *
	 * @param eventId
	 * @return Boolean
	 */
	@RequestMapping(value = "/responseSave", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getResponseSaved(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.eventId.mandatory", null, Locale.US));
		}

		return pcspService.getResponseSaved(pcspAssessmentReq.getIdEvent());

	}

	/**
	 * This Method is used to check if answers are saved for the assessment
	 *
	 * @param eventId
	 * @return Boolean
	 */
	@RequestMapping(value = "/pcspAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getPcspAssessmentDtls(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.eventId.mandatory", null, Locale.US));
		}

		return pcspService.getPcspAssessmentDtls(pcspAssessmentReq.getIdEvent());

	}

	/**
	 * This method will returns pcsp Assessment Data bean to build a New pcsp
	 * Assessment form in Impact. This would contain questions, answers related
	 * to latest pcsp Assessment version.
	 *
	 * @param eventId
	 * @return PCSPAssessmentDto
	 */
	@RequestMapping(value = "/queryPageData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getQueryPageData(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.eventId.mandatory", null, Locale.US));
		}

		return pcspService.getQueryPageData(pcspAssessmentReq.getIdEvent());

	}

	/**
	 * This Method is used to save the PCSPAssessment details to the PCSP_ASSMT
	 * table
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes
	 */
	@RequestMapping(value = "/savePcspAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getSavePcspAssessment(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.PcspAssessmentDto.mandatory", null, Locale.US));
		}

		return pcspService.savePcspAssessment(pcspAssessmentReq);

	}

	/**
	 * This Method will complete the Assessment Process
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes
	 */
	@RequestMapping(value = "/completeAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getcompleteAssessment(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto().getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.eventId.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.PcspAssessmentDto.mandatory", null, Locale.US));
		}

		return pcspService.completeAssessment(pcspAssessmentReq);

	}

	/**
	 * This method will delete an assessment in PROC
	 *
	 * @param pcspAssessmentReq
	 * @return PCSPAssessmentRes
	 */
	@RequestMapping(value = "/deletePcspAssmtDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getdeletePcspAssmtDetails(@RequestBody PCSPAssessmentReq pcspAssessmentReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspAssessmentReq.getPcspAssessmentDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("pcspAssessment.PcspAssessmentDto.mandatory", null, Locale.US));
		}

		return pcspService.deletePcspAssmtDetails(pcspAssessmentReq);

	}

	/**
	 * This method checks to see if case open date is before Mar 16 release
	 *
	 * @param CommonHelperReq
	 * @return PCSPAssessmentRes
	 */

	@RequestMapping(value = "/checkLegacyPCSP", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes checkLegacyPCSP(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}

		return pcspService.checkLegacyPCSP(commonHelperReq);
	}

	/**
	 * This method will fetch the the meth indicator flag
	 *
	 * @param idStage
	 * @return PCSPAssessmentRes -- String meth Ind
	 * @throw InvalidRequestException
	 */

	@RequestMapping(value = "/getMethIndicator", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes getMethIndicator(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}

		return pcspService.getMethIndicator(commonHelperReq);
	}

	/**
	 * This method checks to see there are open placements for input case and if
	 * they can be verified for the input stage and reason closed. Error message
	 * is returned if no open placements are allowed or if no verification
	 * records exists for allowable closed reasons.
	 *
	 *
	 */
	@RequestMapping(value = "/valPCSPPlcmt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PCSPAssessmentRes valPCSPPlcmt(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCdStageReasonClosed())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		return pcspService.valPCSPPlcmt(commonHelperReq);
	}

	/**
	 *
	 * Returns any prior stage ID for any given stage ID and a type request.
	 * Example. If a INT stage needs be found for a case thats currently in a
	 * FPR stage. Pass FPR stage ID and 'INT'
	 *
	 * @param CommonHelperReq
	 *            --ulIdStage id of the stage for which to retrieve the
	 *            corresponding prior stage
	 * @param CommonHelperReq
	 *            cdStageType stage type code. Example INT, INV etc
	 * @return PriorStageInRevRes
	 */

	@RequestMapping(value = "/getPriorStageInReverseChronologicalOrder", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PriorStageInRevRes getPriorStageInReverseChronologicalOrder(
			@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getStageType())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageType.mandatory", null, Locale.US));
		}

		return pcspService.getPriorStageInReverseChronologicalOrder(commonHelperReq);
	}

	/**
	 *
	 * This method checks to see if there are any open assessments for stage
	 *
	 * @param CommonHelperReq
	 *            --IdStage
	 * @return CpsInvCnclsnRes
	 */

	@RequestMapping(value = "/hasOpenPCSPAsmntForStage", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvCnclsnRes hasOpenPCSPAsmntForStage(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return pcspService.hasOpenPCSPAsmntForStage(commonHelperReq);
	}

	/**
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 *
	 * @param CommonHelperReq
	 *            -- stageId
	 * @return CpsInvCnclsnRes -- boolean response
	 */

	@RequestMapping(value = "/getContactPurposeStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvCnclsnRes getContactPurposeStatus(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return pcspService.getContactPurposeStatus(commonHelperReq);
	}

	/**
	 * This method queries the database to find if the contact with the purpose
	 * of initial already exist.
	 *
	 * @param CommonHelperReq
	 *            -- stageId
	 * @return CpsInvCnclsnRes -- boolean response
	 */

	@RequestMapping(value = "/getCntctPurposeInitiationStatus", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvCnclsnRes getCntctPurposeInitiationStatus(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return pcspService.getCntctPurposeInitiationStatus(commonHelperReq);
	}

	/**
	 * Check to see if the given event id has an entry on the approval event
	 * link table. If it does, the event has been submitted for approval at one
	 * point.(CPS Inst Conclusion Page)
	 *
	 */
	@RequestMapping(value = "/hasBeenSubmittedForApprovalCps", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CpsInvCnclsnRes hasBeenSubmittedForApprovalCps(@RequestBody CommonHelperReq commonHelperReq) {

		if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return pcspService.hasBeenSubmittedForApprovalCps(commonHelperReq);
	}

	/**
	 *
	 * Method Name: getChildPCSPEndDate Method Description:Retrieve open PCSP of
	 * the particular child
	 *
	 * @param pcspValueReq
	 * @return
	 */
	@RequestMapping(value = "/getChildPCSPEndDate", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  PcspValueRes getChildPCSPEndDate(@RequestBody PcspValueReq pcspValueReq) {

		if (TypeConvUtil.isNullOrEmpty(pcspValueReq.getPcspValueDto())) {
			throw new InvalidRequestException(messageSource.getMessage("PcspValueDto.mandatory", null, Locale.US));
		}
		PcspValueRes pcspValueRes = new PcspValueRes();
		pcspValueRes.setPcspValueList(pcspService.getChildPCSPEndDate(pcspValueReq.getPcspValueDto()));
		return pcspValueRes;
	}

	/**
	 *
	 * Method Name: displayPCSPList Method Description:This method fetches the
	 * Child Safety Placement details from the Table CHILD_SAFETY_PLACEMENT
	 *
	 * @param PcspReq
	 * @return PcspRes
	 */
	@RequestMapping(value = "/displayPCSPList", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcspRes displayPCSPList(@RequestBody PcspReq pcspReq) {

		PcspRes pcspRes = new PcspRes();
		if (TypeConvUtil.isNullOrEmpty(pcspReq.getCaseId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idCase.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(pcspReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		pcspRes.setPcspList(pcspService.displayPCSPList(pcspReq.getCaseId(), pcspReq.getCdStage()));
		return pcspRes;
	}

	/**
	 *
	 * Method Name: getPersonDetails Method Description:Retrieve PCSP child name
	 * and cargiver name
	 *
	 * @param PcspReq
	 * @return PcspRes
	 */
	@RequestMapping(value = "/getPersonDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  PcspRes getPersonDetails(@RequestBody PcspReq pcspReq) {

		PcspRes pcspRes = new PcspRes();
		if (TypeConvUtil.isNullOrEmpty(pcspReq.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.idStage.mandatory", null, Locale.US));
		}
		pcspRes.setPcspList(pcspService.getPersonDetails(pcspReq.getStageId()));
		return pcspRes;
	}

}
