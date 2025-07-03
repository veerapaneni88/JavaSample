package us.tx.state.dfps.service.admin.controller;

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
import us.tx.state.dfps.service.admin.service.CpsInvConclValService;
import us.tx.state.dfps.service.common.request.CPSInvConclValBeanReq;
import us.tx.state.dfps.service.common.request.CpsInvCnclsnReq;
import us.tx.state.dfps.service.common.response.CPSInvConclValBeanRes;
import us.tx.state.dfps.service.common.response.CpsInvCnclsnRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Controller
 * Class for fetching event, person details Aug 8, 2017- 2:20:36 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/cpsinvconclval")
public class CpsInvConclValController {

	@Autowired
	CpsInvConclValService cpsInvConclValService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsInvConclValController.class);

	/**
	 * 
	 * Method Name: CpsInvConclValoDto Method Description:This service performs
	 * server side validation for the CPS Investigation Conclusion window. The
	 * edits performed by the service depend on the decode string in
	 * DCD_EDIT_PROCESS. Once all required edits are passed, the service will
	 * set all the to-dos associated with the input ID_EVENT to 'COMPLETE' and
	 * return a list of all the ID_EVENTs associated with the input ID_STAGE.
	 * service Name: CINV15S
	 * 
	 * @param CPSInvestigationConclusionReq
	 * @return
	 */
	@RequestMapping(value = "/cpsinvConclValidation", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CpsInvCnclsnRes cpsInvConclValiation(@RequestBody CpsInvCnclsnReq invStageClosureReq) {
		log.info("cpsInvConclValiation method of CpsInvConclValController: Execution Started");
		// Validate the request
		if (TypeConvUtil.isNullOrEmpty(invStageClosureReq.getCpsInvConclValiDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("validateInvStageClosure.invalidrequest", null, Locale.US));
		}
		CpsInvCnclsnRes response = new CpsInvCnclsnRes();
		response.setCpsInvConclValoDto(
				cpsInvConclValService.cpsInvConclValidationService(invStageClosureReq.getCpsInvConclValiDto()));
		log.info("cpsInvConclValiation method of CpsInvConclValController Executed : Return Response");
		return response;
	}

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: getCoSleepingData
	 * 
	 * @param cpsInvConclValReq
	 * @return List<CPSInvConclValBeanRes>
	 */
	@RequestMapping(value = "/fetchCoSleepingData", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CPSInvConclValBeanRes getCoSleepingData(@RequestBody CPSInvConclValBeanReq cpsInvConclValBeanReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvConclValBeanReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		CPSInvConclValBeanRes cpsInvConclValBeanRes = cpsInvConclValService
				.fetchCoSleepingData(cpsInvConclValBeanReq.getIdStage());
		return cpsInvConclValBeanRes;
	}

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: getCoSleepingData
	 * 
	 * @param cpsInvConclValReq
	 * @return CPSInvConclValBeanRes
	 */
	@RequestMapping(value = "/priorStgReverseChronologicalOrder", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CPSInvConclValBeanRes getPriorStageInReverseChronologicalOrder(
			@RequestBody CPSInvConclValBeanReq cpsInvConclValBeanReq) {
		if (TypeConvUtil.isNullOrEmpty(cpsInvConclValBeanReq.getIdStage()))
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		if (TypeConvUtil.isNullOrEmpty(cpsInvConclValBeanReq.getCdStageType()))
			throw new InvalidRequestException(
					messageSource.getMessage("common.cdStageType.mandatory", null, Locale.US));
		CPSInvConclValBeanRes cpsInvConclValBeanRes = cpsInvConclValService.getPriorStageInReverseChronologicalOrder(
				cpsInvConclValBeanReq.getIdStage(), cpsInvConclValBeanReq.getCdStageType());
		return cpsInvConclValBeanRes;
	}
}
