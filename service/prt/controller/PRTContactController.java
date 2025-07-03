package us.tx.state.dfps.service.prt.controller;

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
import us.tx.state.dfps.service.common.request.PRTActionPlanReq;
import us.tx.state.dfps.service.common.request.PRTActplanFollowUpReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.prt.service.PRTContactService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * PRTContactController for PRT Contact Action Plan Form and PRT Contact Follow
 * Up Form July 6, 2018- 3:03:42 PM © 2017 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("/prtcontactcontroller")
public class PRTContactController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PRTContactService pRTContactService;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-BasePRTSessionControllerLog");

	/**
	 * method name: fetchPrtContactAction description: This method fetches all
	 * strategies from action contact action
	 * 
	 * @param basePRTSessionReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/fetchPrtContactAction", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes fetchPrtContactAction(@RequestBody PRTActionPlanReq pRTActionPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(pRTActionPlanReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idCase.mandatory", null, Locale.US));

		// CommonFormRes
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(pRTContactService.getPRTContactActionPopulated(pRTActionPlanReq)));
		LOG.info("TransactionId :" + pRTActionPlanReq.getTransactionId());
		return commonFormRes;
	}

	/**
	 * method name: fetchPrtContactFollowUp description: This method fetches all
	 * strategies from action contact follow up service
	 * 
	 * @param basePRTSessionReq
	 * @return CommonFormRes
	 */
	@RequestMapping(value = "/fetchPrtContactFollowUp", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes fetchPrtContactFollowUp(
			@RequestBody PRTActplanFollowUpReq pRTActplanFollowUpReq) {

		if (TypeConvUtil.isNullOrEmpty(pRTActplanFollowUpReq.getIdCase()))
			throw new InvalidRequestException(
					messageSource.getMessage("populateForm.idCase.mandatory", null, Locale.US));

		// CommonFormRes
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(pRTContactService.getPRTContactFollowUpPopulated(pRTActplanFollowUpReq)));
		LOG.info("TransactionId :" + pRTActplanFollowUpReq.getTransactionId());
		return commonFormRes;
	}
}
