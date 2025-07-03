package us.tx.state.dfps.service.childserviceplan.controller;

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
import us.tx.state.dfps.service.childserviceplan.service.ChildServicePlan;
import us.tx.state.dfps.service.common.request.ChildServicePlanReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildServicePlanController will have all operation which are
 * mapped to ChildServicePlan module Apr 3, 2018- 1:58:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/childplan")
public class ChildServicePlanController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ChildServicePlan childServicePlan;

	private static final Logger logger = Logger.getLogger(ChildServicePlanController.class);

	/**
	 * 
	 * Method Name: getChildPlanInfo Method Description: This method retrieves
	 * data for the child service plan form
	 * 
	 * @param childServicePlanReq
	 * @return
	 */

	@RequestMapping(value = "/getchildplaninfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getChildPlanInfo(@RequestBody ChildServicePlanReq childServicePlanReq) {

		if (TypeConvUtil.isNullOrEmpty(childServicePlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(childServicePlanReq.getIdEvent())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes
				.setPreFillData(TypeConvUtil.getXMLFormat(childServicePlan.getChildServicePlan(childServicePlanReq)));
		logger.info("TransactionId :" + childServicePlanReq.getTransactionId());

		return commonFormRes;

	}

}
