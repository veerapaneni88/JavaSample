package us.tx.state.dfps.service.SDM.controller;

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
import us.tx.state.dfps.service.SDM.service.SDMService;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.response.SDMHoseHoldRes;

@RestController
@RequestMapping("/sdm")
public class SDMController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	SDMService sDMService;

	private static final Logger log = Logger.getLogger(SDMController.class);

	/**
	 * This service is to get the list of house hold that are completed SDM
	 * safety assessments with safety decision
	 * 
	 * @param commonCaseIdReq
	 * @return
	 */
	@RequestMapping(value = "/getsdmcomplhousehold", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  SDMHoseHoldRes getSDMComplHouseHold(@RequestBody CommonCaseIdReq commonCaseIdReq) {

		if (ObjectUtils.isEmpty(commonCaseIdReq.getUlIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + commonCaseIdReq.getTransactionId());
		return sDMService.getSDMComplHouseHold(commonCaseIdReq);
	}

	/**
	 * This service is to get the list of house hold that are completed SDM
	 * safety assessments with safety decision
	 * 
	 * @param commonCaseIdReq
	 * @return
	 */
	@RequestMapping(value = "/getsdmhousehold", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  SDMHoseHoldRes getSDMHouseHold(@RequestBody CommonCaseIdReq commonCaseIdReq) {

		if (ObjectUtils.isEmpty(commonCaseIdReq.getUlIdCase())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		//ALMID : 9711 : get SDM households from current stage 
		if (ObjectUtils.isEmpty(commonCaseIdReq.getStageId())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		log.info("TransactionId :" + commonCaseIdReq.getTransactionId());
		return sDMService.getSDMHouseHold(commonCaseIdReq);
	}

}
