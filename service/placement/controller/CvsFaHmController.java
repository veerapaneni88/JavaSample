package us.tx.state.dfps.service.placement.controller;

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
import us.tx.state.dfps.service.common.request.CvsFaHmReq;
import us.tx.state.dfps.service.common.response.CvsFaHmRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.placement.service.CvsFaHmService;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service CvsFaHm List,
 * CvsFaHmBean Details Class Description:CvsFaHmController class will have all
 * operation which are related to case and relevant page to case.
 * 
 */

@RestController
@RequestMapping("/cvsFaHm")
public class CvsFaHmController {

	@Autowired
	CvsFaHmService cvsFaHmService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CvsFaHmController.class);

	/**
	 * This method saves the CvsfaHome page details in person detail table.
	 * 
	 * @param cvsFaHmReq
	 * @return
	 */
	@RequestMapping(value = "/updatePersonDetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHmRes getUpdatePersonDetail(@RequestBody CvsFaHmReq cvsFaHmReq){

		if (TypeConvUtil.isNullOrEmpty(cvsFaHmReq.getCvsFaHomeValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("cvsFaHmReq.CvsFaHomeValueDto.mandatory", null, Locale.US));
		}

		log.info("TransactionId :" + cvsFaHmReq.getTransactionId());
		return cvsFaHmService.updatePersonDetail(cvsFaHmReq.getCvsFaHomeValueDto());

	}

	/**
	 * This method inserts the CvsfaHome page details if the record does not
	 * exist
	 * 
	 * @param cvsFaHmReq
	 * @return
	 */
	@RequestMapping(value = "/insertIntoPersonDetail", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHmRes getInsertIntoPersonDetail(@RequestBody CvsFaHmReq cvsFaHmReq){

		if (TypeConvUtil.isNullOrEmpty(cvsFaHmReq.getCvsFaHomeValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("cvsFaHmReq.CvsFaHomeValueDto.mandatory", null, Locale.US));
		}

		return cvsFaHmService.insertIntoPersonDetail(cvsFaHmReq.getCvsFaHomeValueDto());

	}

	/**
	 * This method saves Primary caregiver information on CvsfaHome page details
	 * in stage person link table.
	 * 
	 * @param cvsFaHmReq
	 * @return
	 */
	@RequestMapping(value = "/updatePrimaryKinshipIndicator", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHmRes getUpdatePrimaryKinshipIndicator(@RequestBody CvsFaHmReq cvsFaHmReq) {

		if (TypeConvUtil.isNullOrEmpty(cvsFaHmReq.getCvsFaHomeValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("cvsFaHmReq.CvsFaHomeValueDto.mandatory", null, Locale.US));
		}

		log.info("TransactionId :" + cvsFaHmReq.getTransactionId());
		return cvsFaHmService.updatePrimaryKinshipIndicator(cvsFaHmReq.getCvsFaHomeValueDto());

	}

	@RequestMapping(value = "/updateKinIndPersonNameResourceId", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CvsFaHmRes getUpdateKinIndPersonNameResourceId(@RequestBody CvsFaHmReq cvsFaHmReq){

		if (TypeConvUtil.isNullOrEmpty(cvsFaHmReq.getCvsFaHomeValueDto())) {
			throw new InvalidRequestException(
					messageSource.getMessage("cvsFaHmReq.CvsFaHomeValueDto.mandatory", null, Locale.US));
		}

		log.info("TransactionId :" + cvsFaHmReq.getTransactionId());
		return cvsFaHmService.updateKinIndPersonNameResourceId(cvsFaHmReq.getCvsFaHomeValueDto());

	}

}