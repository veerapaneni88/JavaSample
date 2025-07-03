package us.tx.state.dfps.service.interstatecompact.controller;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.interstatecompact.service.ICPformsService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<controller
 * class for Interstate Compact forms, sending specific request accordingly to
 * service layer> May 1, 2018- 10:15:50 AM © 2017 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("/icFormsController")
public class ICPformsController {

	@Autowired
	ICPformsService iCPformsService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ICPformsController.class);

	/**
	 * TUX service: CSUB42S/icp01o00 Method Description: sending request object
	 * to service class, in order to populate form for icp01o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getICPlacementReqform", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getICPlacementReqforms(@RequestBody PopulateFormReq populateFormReq) {

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getICPlacementReqForm(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * TUX service: CSUB43S/icp02o00 Method Description: sending request object
	 * to service class, in order to populate form for icp02o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getICPStatusform", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getICPStatusform(@RequestBody PopulateFormReq populateFormReq) {

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getICstatusform(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * TUX service: CSUB30S/icp14o00 Method Description: sending request object
	 * to service class, in order to populate form for icp14o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getPriorityHomeStudyReq", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getPriorityHomeStudyReq(@RequestBody PopulateFormReq populateFormReq) {

		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.idEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.idStage.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getPriorityHomeStudyReq(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * TUX service: CSUB32S/icp18o00 Method Description: sending request object
	 * to service class, in order to populate form for icp18o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getICcoverLetter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getICPcoverLetter(@RequestBody PopulateFormReq populateFormReq) {

		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.idEvent.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateFormReq.IdStage.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getICcoverLetter(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * TUX service: CSUB20S/icp20o00 Method Description: sending request object
	 * to service class, in order to populate form for icp20o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getICfinancialPlan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getICfinancialPlan(@RequestBody PopulateFormReq populateFormReq) {
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdEvent())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idEvent.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getICfinancialPlan(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * TUX service: CSUB33S/icp22o00 Method Description: sending request object
	 * to service class, in order to populate form for icp22o00.
	 *
	 * @param populateFormReq
	 *            the populate form req
	 * @return the populate form
	 */
	@RequestMapping(value = "/getICtransmittalMemo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getICtransmittalMemo(@RequestBody PopulateFormReq populateFormReq) {
		if (TypeConvUtil.isNullOrEmpty(populateFormReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idStage.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(iCPformsService.getICtransmittalMemo(populateFormReq)));
		log.info("TransactionId :" + populateFormReq.getTransactionId());
		return commonFormRes;
	}
}
