package us.tx.state.dfps.service.disasterplan.controller;

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
import us.tx.state.dfps.service.common.request.DisasterPlanReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.service.DisasterPlanService;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CFAD19S
 * Class Description: Controller class for services related to disasterplam. Apr
 * 20,2017 - 4:29:18 PM
 */

@RestController
@RequestMapping("/disasterplan")
public class DisasterPlanController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	DisasterPlanService disasterPlanService;

	private static final Logger logger = Logger.getLogger(DisasterPlanController.class);

	/**
	 * 
	 * Method Description: Method to update person details in the CVS Home
	 * window. This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param disasterPlanReq
	 * @return DisasterPlanRes @
	 */

	@RequestMapping(value = "/getdisasterplan", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getDisasterPlan(@RequestBody DisasterPlanReq disasterPlanReq) {

		if (TypeConvUtil.isNullOrEmpty(disasterPlanReq.getIdResource())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		if (TypeConvUtil.isNullOrEmpty(disasterPlanReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(disasterPlanService.getDisasterPlan(disasterPlanReq)));
		logger.info("TransactionId :" + disasterPlanReq.getTransactionId());

		return commonFormRes;

	}
}
