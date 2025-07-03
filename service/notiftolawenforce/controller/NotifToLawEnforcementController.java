package us.tx.state.dfps.service.notiftolawenforce.controller;

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
import us.tx.state.dfps.service.common.request.NotifToLawEnforceReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.notiftolawenforce.service.NotifToLawEnforcementService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * Populates the APS Facility Notice to Law Enforcement form.> Mar 14, 2018-
 * 5:15:37 PM © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("/notifToLawEnfore")
public class NotifToLawEnforcementController {

	@Autowired
	NotifToLawEnforcementService notifToLawEnforcementService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(NotifToLawEnforcementController.class);

	@RequestMapping(value = "/getNotifToLawEnforce", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getPopulateForm(@RequestBody NotifToLawEnforceReq notifToLawEnforceReq) {
		if (TypeConvUtil.isNullOrEmpty(notifToLawEnforceReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notifToLawEnforceReq.idStage.mandatory", null, Locale.US));
		}
		if (TypeConvUtil.isNullOrEmpty(notifToLawEnforceReq.getFormName())) {
			throw new InvalidRequestException(
					messageSource.getMessage("notifToLawEnforceReq.formName.mandatory", null, Locale.US));
		}
		// CommonFormRes
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(notifToLawEnforcementService.getLawEnforcementNoticed(notifToLawEnforceReq)));
		log.info("TransactionId :" + notifToLawEnforceReq.getTransactionId());
		return commonFormRes;
	}
}
