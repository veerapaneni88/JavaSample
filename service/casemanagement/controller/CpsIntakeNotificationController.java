package us.tx.state.dfps.service.casemanagement.controller;

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
import us.tx.state.dfps.service.casemanagement.service.CpsIntakeNotificationService;
import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * Class Name:CpsIntakeNotificationController Class Description:This service is
 * used to launch the CPS Intake Notification Form Tux Service name - CINT42S
 * Form Name - CFIN0500 Oct 30, 2017- 3:26:43 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@RestController
@RequestMapping("/cpsintake")
public class CpsIntakeNotificationController {

	@Autowired
	CpsIntakeNotificationService cpsIntakeNotificationService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsIntakeNotificationController.class);

	/**
	 * Method Name : getIncomingDetails Method Description: This Method is used
	 * to retrieve the data required for CPS Intake Notification for law
	 * Enforcement Report Service Name : CINT42S
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return cpsIntakeNotificationRes
	 */

	@RequestMapping(value = "/getIncomingDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getIncomingDetails(@RequestBody CpsIntakeNotificationReq cpsIntakeNotificationReq) {

		if (TypeConvUtil.isNullOrEmpty(cpsIntakeNotificationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(
				cpsIntakeNotificationService.getCpsIntkNotificnLawEnfrcemntReport(cpsIntakeNotificationReq)));

		log.info("TransactionId :" + cpsIntakeNotificationReq.getTransactionId());
		return commonFormRes;
	}

}
