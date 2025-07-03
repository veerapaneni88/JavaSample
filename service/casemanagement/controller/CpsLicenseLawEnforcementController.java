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
import us.tx.state.dfps.service.casemanagement.service.CpsLicenseLawEnforcementService;
import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * Class Name: CpsLicenseLawEnforcementController Service Name: CINT43S
 * Description: CpsLicenseLawEnforcementController will have all operation which
 * produces the data required for the CPS Intake license law enforcement report.
 */

@RestController
@RequestMapping("/cpslicense")
public class CpsLicenseLawEnforcementController {

	@Autowired
	CpsLicenseLawEnforcementService cpsLicenseLawEnforcementService;

	@Autowired
	MessageSource messageSource;
	private static final Logger log = Logger.getLogger(CpsLicenseLawEnforcementController.class);

	/**
	 * 
	 * Method Description: This Method is used to retrieve the data required for
	 * CPS Intake license law enforcement reportService Name : CINT43S
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return cpsIntakeNotificationRes
	 */
	@RequestMapping(value = "/getIntkLicenseLaw", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getIntkLicenseLaw(@RequestBody CpsIntakeNotificationReq cpsIntakeNotificationReq) {

		if (TypeConvUtil.isNullOrEmpty(cpsIntakeNotificationReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil
				.getXMLFormat(cpsLicenseLawEnforcementService.getCpsIntkLicenseLawReport(cpsIntakeNotificationReq)));

		log.info("TransactionId :" + cpsIntakeNotificationReq.getTransactionId());
		return commonFormRes;

	}
}