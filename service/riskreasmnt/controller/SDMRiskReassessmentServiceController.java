package us.tx.state.dfps.service.riskreasmnt.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.common.request.SDMRiskReassessmentReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.riskreasmnt.service.SDMRiskReassessmentService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to handle the REST service requests from the business delegate to
 * fetch, save and delete the SDM Risk Reassessment details. Jun 14, 2018-
 * 3:25:43 PM © 2017 Texas Department of Family and Protective Services
 */
@RequestMapping("/sdmRiskReassessment/")
@RestController
public class SDMRiskReassessmentServiceController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SDMRiskReassessmentService sdmRiskReassessmentService;

	private static final String SAVE_FAILED = "Save Failed";

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method
	 * is used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to retrieve the SDM
	 *            Risk Reassessment details.
	 * @return SDMRiskReassessmentRes - This dto will have the SDM Risk
	 *         Reassessment details.
	 */
	@RequestMapping(value = "getSDMRiskReassessmentDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskReassessmentRes getSDMRiskReassessmentDetails(
			@RequestBody SDMRiskReassessmentReq sdmRiskReassessmentReq) {

		// Calling the service implementation to get the SMD Risk Reassessment
		// details.
		return sdmRiskReassessmentService.getSDMRiskReassessmentDtls(sdmRiskReassessmentReq.getSdmRiskReasmntDto(),
				sdmRiskReassessmentReq.getIdRiskAsmntLkp());
	}

	/**
	 * Method Name: saveSDMRiskReassessmentDetails Method Description:This
	 * method is used to save the SDM Risk Reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment details to be
	 *            saved or updated.
	 * @return SDMRiskReassessmentRes - This dto will hold the saved/updated SDM
	 *         Risk reassessment details.
	 */
	@RequestMapping(value = "saveSDMRiskReassessmentDtls", headers = {
			"Accept=application/json;charset=windows-1252" }, method = RequestMethod.POST)
	public  SDMRiskReassessmentRes saveSDMRiskReassessmentDetails(
			@RequestBody SDMRiskReassessmentReq sdmRiskReassessmentReq) {
		SDMRiskReassessmentRes sdmRiskReassessmentRes = new SDMRiskReassessmentRes();
		/*
		 * Calling the service implementation to save/update SDM Risk
		 * reassessment details.
		 */
		try {

			sdmRiskReassessmentRes = sdmRiskReassessmentService.saveSDMRiskReassessmentDtls(sdmRiskReassessmentReq);

		} catch (Exception e) {
			ErrorDto errorDto = new ErrorDto();
			if (e.getMessage().contains(SAVE_FAILED)) {
				errorDto.setErrorCode(Messages.MSG_CMN_TMSTAMP_MISMATCH);
			}

			sdmRiskReassessmentRes.setErrorDto(errorDto);
		}
		return sdmRiskReassessmentRes;
	}

	/**
	 * Method Name: checkRiskReasmntExists Method Description:This method is
	 * used to check if for a particular person , a Risk Reassessment exists in
	 * PROC or PEND status.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to check if a risk
	 *            reassessment exists.
	 * @return SDMRiskReassessmentRes - This dto will hold the risk reassessment
	 *         id if present for a person in the stage.
	 */
	@RequestMapping(value = "checkRiskReasmntExists", headers = {
			"Accept=application/json", }, method = RequestMethod.POST)
	public  SDMRiskReassessmentRes checkRiskReasmntExists(
			@RequestBody SDMRiskReassessmentReq sdmRiskReassessmentReq) {
		/*
		 * Calling the service implementation to get the Risk reassessment id
		 * which is in PROC or PEND status if present.
		 */
		return sdmRiskReassessmentService.checkRiskReasmntExists(sdmRiskReassessmentReq.getIdPerson(),
				sdmRiskReassessmentReq.getIndHshldPrmryScndry(), sdmRiskReassessmentReq.getEventStatusList(),
				sdmRiskReassessmentReq.getIdStage());
	}

	/**
	 * Method Name: deleteSDMRiskReassessmentDtls Method Description:This method
	 * is used to delete the SDM Risk reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the SDM Risk reassessment id to be
	 *            deleted.
	 * @return
	 */
	@RequestMapping(value = "deleteSDMRiskReassessmentDtls", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMRiskReassessmentRes deleteSDMRiskReassessmentDtls(
			@RequestBody SDMRiskReassessmentReq sdmRiskReassessmentReq){
		return sdmRiskReassessmentService.deleteSDMRiskReassessmentDtls(sdmRiskReassessmentReq);
	}

	/**
	 * Method Name: getSDMRiskReassessmentDetails Method Description:This method
	 * is used to get a new SDM Risk Reassessment details or an existing Risk
	 * reassessment details.
	 * 
	 * @param sdmRiskReassessmentReq
	 *            - This dto will hold the input parameters to retrieve the SDM
	 *            Risk Reassessment details.
	 * @return SDMRiskReassessmentRes - This dto will have the SDM Risk
	 *         Reassessment details.
	 */

	@RequestMapping(value = "getSDMRiskReassessmentDetails", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getSDMRiskReassessmentDtls(@RequestBody SDMRiskReassessmentReq sdmRiskReassessmentReq) {
		if (ObjectUtils.isEmpty(sdmRiskReassessmentReq.getIdEvent()))
			throw new InvalidRequestException(messageSource
					.getMessage("sdmRiskReassessmentReq.getSdmRiskReasmntDto().idEvent.mandatory", null, Locale.US));
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(sdmRiskReassessmentService.getSDMRiskReassessmentDetails(
				sdmRiskReassessmentReq.getIdEvent(), sdmRiskReassessmentReq.getCdStage())));
		return commonFormRes;
	}

}
