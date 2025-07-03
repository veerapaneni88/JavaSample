package us.tx.state.dfps.service.populateform.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populateform.service.PcspHistoryFormService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Controller
 * class for PcspHistoryFormSercice> Mar 29, 2018- 12:16:29 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/pcspHistoryForm")
public class PcspHistoryFormController {

	@Autowired
	PcspHistoryFormService pcspHistoryFormService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PcspHistoryFormControllerLog");

	/**
	 * 
	 * Method Description: Populates the Pcsp History Outcome Matrix Forms &
	 * Narrative. Name: PcspHist
	 * 
	 * @param populateFormReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getPcspHistoryForm", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getfacilityInvSum(@RequestBody PopulateFormReq populateFormReq) {
		if (ObjectUtils.isEmpty(populateFormReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.idCase.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(pcspHistoryFormService.getPcspHistoryForm(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());

		return commonFormRes;
	}

	/**
	 * 
	 * Method Description: Populates Pcsp Assessment Form Name:
	 * PcspAssessmenForm
	 * 
	 * @param PopulateFormReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getpcspAssessment", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAssessmentForm(@RequestBody PopulateFormReq populateFormReq) {

		if (ObjectUtils.isEmpty(populateFormReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("aFIStatementReq.IdEvent.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(
				pcspHistoryFormService.getPcspAssessmentForm(populateFormReq, ServiceConstants.PCSPASMT)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());

		return commonFormRes;
	}

	/**
	 * 
	 * Method Description: Populates Pcsp Addendum Form Name: PcspAssessmenForm
	 * 
	 * @param PopulateFormReq
	 * @return commonFormRes
	 */

	@RequestMapping(value = "/getpcspAddendum", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getAddendumForm(@RequestBody PopulateFormReq populateFormReq) {
		if (ObjectUtils.isEmpty(populateFormReq.getIdStage()))
			throw new InvalidRequestException(
					messageSource.getMessage("aFIStatementReq.idStage.mandatory", null, Locale.US));
		if (ObjectUtils.isEmpty(populateFormReq.getIdEvent()))
			throw new InvalidRequestException(
					messageSource.getMessage("aFIStatementReq.IdEvent.mandatory", null, Locale.US));

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(
				pcspHistoryFormService.getPcspAssessmentForm(populateFormReq, ServiceConstants.PCSPANDM)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());

		return commonFormRes;
	}
}
