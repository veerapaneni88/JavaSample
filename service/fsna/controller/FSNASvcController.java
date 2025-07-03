package us.tx.state.dfps.service.fsna.controller;

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
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.CpsMonthlyEvalReq;
import us.tx.state.dfps.service.common.request.FSNAAssessmentDtlGetReq;
import us.tx.state.dfps.service.common.request.SdmFsnaReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;
import us.tx.state.dfps.service.common.response.FSNAValidationRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fsna.service.FSNAService;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@RestController
@RequestMapping(value = "/fsna")
public class FSNASvcController {

	private static final Logger log = Logger.getLogger(FSNASvcController.class);

	@Autowired
	MessageSource messageSource;

	@Autowired
	FSNAService fSNAService;

	/**
	 * This is the service to get the FSNA assessment details for display Method
	 * Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	@RequestMapping(value = "/fsnaRead", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAAssessmentDtlGetRes getFSNAAssessmentDtl(
			@RequestBody FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq) {
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}

		return fSNAService.getFSNAAssessmentDtl(getFSNAAssessmentDtlReq);
	}

	/**
	 * This is the service to get the FSNA assessment FBSS form details
	 * Description:
	 * 
	 * @param getFSNAAssessmentDtlReq
	 * @return
	 */
	@RequestMapping(value = "/getfsnafbssform", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes fetchFSNAFbssForm(@RequestBody FSNAAssessmentDtlGetReq getFSNAAssessmentDtlReq) {
		log.debug("Inside the method getFSNAAssessmentDtl of FSNASvcController");
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(getFSNAAssessmentDtlReq.getCdTask())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdTask.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(fSNAService.getFSNAFbssForm(getFSNAAssessmentDtlReq)));
		return commonFormRes;

	}

	/**
	 * Method Name: saveCpsFsna Method Description:This method is used to save
	 * the CPS FSNA assessment
	 *
	 * @param appEvent
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @
	 */

	@RequestMapping(value = "/saveCpsFsna", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAAssessmentDtlGetRes saveCpsFsna(@RequestBody SdmFsnaReq sdmFsnaReq) {

		return fSNAService.saveCpsFsna(sdmFsnaReq);
	}

	/**
	 * Method Name: deleteSdmFsna Method Description: this method is to delete a
	 * PROC status assessment
	 *
	 * @param appEvent
	 * @param idUpdatePerson
	 * @param evtStatus
	 * @param eventDesc
	 * @
	 */

	@RequestMapping(value = "/deleteFbss", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAAssessmentDtlGetRes deleteSdmFsna(@RequestBody SdmFsnaReq sdmFsnaReq) {

		if (ObjectUtils.isEmpty(sdmFsnaReq.getCpsFsnaDto().getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.eventid.mandatory", null, Locale.US));
		}

		return fSNAService.deleteSdmFsna(sdmFsnaReq.getCpsFsnaDto());
	}

	/**
	 * 
	 * Method Description: this service is to the complete the FSNA assessment
	 * 
	 * @param sdmFsnaReq
	 * @return
	 */
	@RequestMapping(value = "/completeFbss", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAAssessmentDtlGetRes completeSdmFsna(@RequestBody SdmFsnaReq sdmFsnaReq) {
		fSNAService.saveCpsFsna(sdmFsnaReq);
		return fSNAService.completeSdmFsna(sdmFsnaReq.getCpsFsnaDto(), sdmFsnaReq.getUserProfile());
	}

	/**
	 * 
	 * Method Description: This method is to get the assessment type for the
	 * given primary or secondary parent/caregiver
	 * 
	 * @param sdmFsnaReq
	 * @return
	 */
	@RequestMapping(value = "/getFSNAAssmntType", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonStringRes getFSNAAssmntType(@RequestBody CommonHelperReq commonHelperReq) {

		if (ObjectUtils.isEmpty(commonHelperReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		if (ObjectUtils.isEmpty(commonHelperReq.getIdPerson()) && ObjectUtils.isEmpty(commonHelperReq.getIdPerson2())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}

		return fSNAService.getFSNAAssmntType(commonHelperReq);
	}

	/**
	 * 
	 * Method Description: This method is to get validation errors before save
	 * 
	 * @param sdmFsnaReq
	 * @return
	 */
	@RequestMapping(value = "/validateFSNAAssmnt", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAValidationRes validateFSNAAssessment(@RequestBody SdmFsnaReq sdmFsnaReq) {

		if (ObjectUtils.isEmpty(sdmFsnaReq.getCpsFsnaDto().getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}

		return fSNAService.validateFSNAAssessment(sdmFsnaReq);
	}

	@RequestMapping(value = "/checkCareGiverPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  FSNAAssessmentDtlGetRes checkCareGiverPerson(@RequestBody PersonDto personDto) {
		if (ObjectUtils.isEmpty(personDto.getIdPerson())) {
			throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
		}
		return fSNAService.checkCareGiverPerson(personDto.getIdPerson(), personDto.getIdStage());
	}

	/**
	 * Method Description: This method is used to retrieve CPS Monthly
	 * Evaluation form by passing IdStage,IdCase,IdEvent along with Contact
	 * Detail Screen User Selection Values as input request
	 * 
	 * @param CpsMonthlyEvalReq
	 * @return commonFormRes
	 */
	@RequestMapping(value = "/getCpsMonthlyEvalForm", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getCpsMonthlyEvalForm(@RequestBody CpsMonthlyEvalReq cpsMonthlyEvalReq) {

		if (ObjectUtils.isEmpty(cpsMonthlyEvalReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		if (ObjectUtils.isEmpty(cpsMonthlyEvalReq.getCdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.cdStage.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(fSNAService.getCpsMonthlyEvalForm(cpsMonthlyEvalReq)));
		return commonFormRes;
	}
}