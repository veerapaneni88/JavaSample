package us.tx.state.dfps.service.cpsinvstsummary.controller;

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
import us.tx.state.dfps.service.common.request.CpsInvstSummaryReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinvstsummary.service.CpsInvstSummaryService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Makes
 * service call and sends prefill string to form Mar 28, 2018- 4:27:33 PM © 2017
 * Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("cpsinvstsummary")
public class CpsInvstSummaryController {

	@Autowired
	private CpsInvstSummaryService cpsInvstSummaryService;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(CpsInvstSummaryController.class);

	@RequestMapping(value = "/getriskinfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
	public CommonFormRes getRiskInfo(@RequestBody CpsInvstSummaryReq request) {
		log.debug("Entering method CINV65S getRiskInfo in CpsInvstSummaryController");
		if (TypeConvUtil.isNullOrEmpty(request)) {
			throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
		}
		CommonFormRes commonFormRes = new CommonFormRes();
		commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(cpsInvstSummaryService.getRiskAssessmentInfo(request)));

		return commonFormRes;
	}

}
