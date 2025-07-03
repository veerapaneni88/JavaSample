package us.tx.state.dfps.service.investigationaction.controller;

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
import us.tx.state.dfps.service.common.request.InvActionReportReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigationaction.service.InvActionReportService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: May 3,
 * 2018- 11:29:29 AM
 *
 */
@RestController
@RequestMapping("/invactionreport")
public class InvActionReportController {

	@Autowired
	MessageSource messageSource;

	@Autowired
	InvActionReportService invActionReportService;

	private static final Logger logger = Logger.getLogger(InvActionReportController.class);

	@RequestMapping(value = "/getinvactionreportinfo", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonFormRes getInvActionReportInfo(@RequestBody InvActionReportReq invActionReportReq) {

		if (TypeConvUtil.isNullOrEmpty(invActionReportReq.getIdStage())) {
			throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
		}

		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(invActionReportService.getInvActionReportInfo(invActionReportReq)));
		logger.info("TransactionId :" + invActionReportReq.getTransactionId());

		return commonFormRes;

	}

}
