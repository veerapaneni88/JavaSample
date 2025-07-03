package us.tx.state.dfps.service.workload.controller;

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
import us.tx.state.dfps.service.common.request.NotifToParentEngReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.service.NotifToParentEngService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 5, 2018- 11:44:05 AM © 2017 Texas Department of
 * Family and Protective Services
 */

@RestController
@RequestMapping("/notifToParentEng")
public class NotifToParentEngController {

	@Autowired
	NotifToParentEngService notifToParentEngService;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(NotifToParentEngController.class);

	@RequestMapping(value = "/getNotifToParentEng", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getNotifToParentEng(@RequestBody NotifToParentEngReq notifToParentEngReq) {
		if (TypeConvUtil.isNullOrEmpty(notifToParentEngReq.getIdStage())) {
			throw new InvalidRequestException(
					messageSource.getMessage("NotifToParentEng.idStage.mandatory", null, Locale.US));
		}
		;
		if (TypeConvUtil.isNullOrEmpty(notifToParentEngReq.getIdPerson())) {
			throw new InvalidRequestException(
					messageSource.getMessage("NotifToParentEng.idPerson.mandatory", null, Locale.US));
		}
		;
		// CommonFormRes
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(notifToParentEngService.getParentReporterNotified(notifToParentEngReq)));
		log.info("TransactionId :" + notifToParentEngReq.getTransactionId());
		return commonFormRes;
	}
}
