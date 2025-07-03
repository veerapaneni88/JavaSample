package us.tx.state.dfps.service.childbackground.controller;

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
import us.tx.state.dfps.service.childbackground.service.ChildBackgroundService;
import us.tx.state.dfps.service.common.request.ChildBackgroundReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ChildBackgroundController will have all operation which are
 * mapped to ChildBackground module March 27, 2018- 1:58:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/childbackground")
public class ChildBackgroundController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	ChildBackgroundService childBackgroundService;

	private static final Logger logger = Logger.getLogger("ServiceBusiness-ChildBackgroundControllerLog");

	/**
	 * 
	 * Method Name: getChildBackgroundInfo Method Description:This method is
	 * used to retrieve the information for ChildBackground Summary Form
	 * 
	 * @param childBackgroundReq
	 * @return
	 * 
	 */

	@RequestMapping(value = "/getchildbackgroundinfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getChildBackgroundInfo(@RequestBody ChildBackgroundReq childBackgroundReq) {

		if (TypeConvUtil.isNullOrEmpty(childBackgroundReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(childBackgroundService.getChildBackgroundInfo(childBackgroundReq)));
		logger.info("TransactionId :" + childBackgroundReq.getTransactionId());

		return commonFormRes;

	}

}
