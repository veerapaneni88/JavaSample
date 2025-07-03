package us.tx.state.dfps.service.investigation.controller;

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
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.investigation.service.LicensingInvReportService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * service call and sends prefill data to forms Apr 13, 2018- 12:28:32 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("licinvrep")
public class LicensingInvReportController {

	@Autowired
	private LicensingInvReportService licensingInvReportService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(LicensingInvReportController.class);

	@RequestMapping(value = "/getLicensinginvReport", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getLicensingInvReportDtls(@RequestBody CommonApplicationReq commonApplicationReq) {
		log.debug("Entering method CSVC25S getReport in LicensingInvReportController");
		if (TypeConvUtil.isNullOrEmpty(commonApplicationReq)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(
				TypeConvUtil.getXMLFormat(licensingInvReportService.getLicensingInvReportDtls(commonApplicationReq)));

		return commonFormRes;
	}

}
